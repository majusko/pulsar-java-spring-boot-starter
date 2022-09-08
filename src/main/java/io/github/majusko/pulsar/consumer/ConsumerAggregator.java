package io.github.majusko.pulsar.consumer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.pulsar.client.api.BatchReceivePolicy;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import io.github.majusko.pulsar.PulsarMessage;
import io.github.majusko.pulsar.collector.ConsumerCollector;
import io.github.majusko.pulsar.collector.ConsumerHolder;
import io.github.majusko.pulsar.constant.BatchAckMode;
import io.github.majusko.pulsar.error.FailedMessage;
import io.github.majusko.pulsar.error.exception.ClientInitException;
import io.github.majusko.pulsar.error.exception.ConsumerInitException;
import io.github.majusko.pulsar.properties.ConsumerProperties;
import io.github.majusko.pulsar.properties.PulsarProperties;
import io.github.majusko.pulsar.utils.SchemaUtils;
import io.github.majusko.pulsar.utils.UrlBuildService;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Component
@DependsOn({"pulsarClient", "consumerCollector"})
public class ConsumerAggregator implements EmbeddedValueResolverAware {

    private final Sinks.Many<FailedMessage> sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    private final ConsumerCollector consumerCollector;
    private final PulsarClient pulsarClient;
    private final ConsumerProperties consumerProperties;
    private final PulsarProperties pulsarProperties;
    private final UrlBuildService urlBuildService;
    private final ConsumerInterceptor consumerInterceptor;

    private StringValueResolver stringValueResolver;
    private List<Consumer> consumers;
    

    public ConsumerAggregator(ConsumerCollector consumerCollector, PulsarClient pulsarClient,
                              ConsumerProperties consumerProperties, PulsarProperties pulsarProperties, UrlBuildService urlBuildService,
                              ConsumerInterceptor consumerInterceptor) {
        this.consumerCollector = consumerCollector;
        this.pulsarClient = pulsarClient;
        this.consumerProperties = consumerProperties;
        this.pulsarProperties = pulsarProperties;
        this.urlBuildService = urlBuildService;
        this.consumerInterceptor = consumerInterceptor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (pulsarProperties.isAutoStart()) {
            consumers = consumerCollector.getConsumers().entrySet().stream()
                .filter(holder -> holder.getValue().getAnnotation().autoStart())
                .map(holder -> subscribe(holder.getKey(), holder.getValue()))
                .collect(Collectors.toList());
        }
    }

	private Consumer<?> subscribe(String generatedConsumerName, ConsumerHolder holder) {
		try {
			final String consumerName = stringValueResolver.resolveStringValue(holder.getAnnotation().consumerName());
			final String subscriptionName = stringValueResolver
					.resolveStringValue(holder.getAnnotation().subscriptionName());
			final String topicName = stringValueResolver.resolveStringValue(holder.getAnnotation().topic());
			final String namespace = stringValueResolver.resolveStringValue(holder.getAnnotation().namespace());
			final SubscriptionType subscriptionType = urlBuildService.getSubscriptionType(holder);
			final ConsumerBuilder<?> consumerBuilder = pulsarClient
					.newConsumer(SchemaUtils.getSchema(holder.getAnnotation().serialization(),
							holder.getAnnotation().clazz()))
					.consumerName(urlBuildService.buildPulsarConsumerName(consumerName, generatedConsumerName))
					.subscriptionName(
							urlBuildService.buildPulsarSubscriptionName(subscriptionName, generatedConsumerName))
					.topic(urlBuildService.buildTopicUrl(topicName, namespace)).subscriptionType(subscriptionType)
					.subscriptionInitialPosition(holder.getAnnotation().initialPosition());
			if (!holder.getAnnotation().batch()) {
				consumerBuilder.messageListener((consumer, msg) -> {
					try {
						final Method method = holder.getHandler();
						method.setAccessible(true);

						if (holder.isWrapped()) {
							method.invoke(holder.getBean(), wrapMessage(msg));
						} else {
							method.invoke(holder.getBean(), msg.getValue());
						}

						consumer.acknowledge(msg);
					} catch (Exception e) {
						consumer.negativeAcknowledge(msg);
						sink.tryEmitNext(new FailedMessage(e, consumer, msg));
					}
				});
			}

			if (pulsarProperties.isAllowInterceptor()) {
				consumerBuilder.intercept(consumerInterceptor);
			}

			if (consumerProperties.getAckTimeoutMs() > 0) {
				consumerBuilder.ackTimeout(consumerProperties.getAckTimeoutMs(), TimeUnit.MILLISECONDS);
			}

			if (holder.getAnnotation().batch()) {
				consumerBuilder.batchReceivePolicy(
						BatchReceivePolicy.builder().maxNumMessages(holder.getAnnotation().maxNumMessage())
								.maxNumBytes(holder.getAnnotation().maxNumBytes())
								.timeout(holder.getAnnotation().timeoutMillis(), TimeUnit.MILLISECONDS).build());
			}

			urlBuildService.buildDeadLetterPolicy(holder.getAnnotation().maxRedeliverCount(),
					holder.getAnnotation().deadLetterTopic(), consumerBuilder);

			final Consumer<?> consumer = consumerBuilder.subscribe();
			if (holder.getAnnotation().batch()) {
				CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
					boolean retTypeVoid = true;
					boolean manualAckMode = false;
					Messages<?> msgs = null;
					try {
						final Method method = holder.getHandler();
						method.setAccessible(true);
						retTypeVoid = method.getReturnType().equals(Void.TYPE);
						if (holder.getAnnotation().batchAckMode() == BatchAckMode.MANUAL) {
							manualAckMode = true;
						}
						while (true) {
							msgs = consumer.batchReceive();
							if (manualAckMode) {
								method.invoke(holder.getBean(), msgs, consumer);
							} else if (!retTypeVoid && !manualAckMode) {
								final List<MessageId> ackList = (List<MessageId>) method.invoke(holder.getBean(), msgs);
								final Set<MessageId> ackSet = ackList.stream().collect(Collectors.toSet());
								consumer.acknowledge(ackList);
								msgs.forEach((msg) -> {
									if (!ackSet.contains(msg))
										consumer.negativeAcknowledge(msg);
								});
							} else if (!manualAckMode) {
								method.invoke(holder.getBean(), msgs);
								consumer.acknowledge(msgs);
							}
						}
					} catch (Exception e) {
						if (retTypeVoid && !manualAckMode) {
							if (msgs != null) {
								consumer.negativeAcknowledge(msgs);
							}
						}
					}
				});
			}
			return consumer;
		} catch (PulsarClientException | ClientInitException e) {
			throw new ConsumerInitException("Failed to init consumer.", e);
		}
	}

    public <T> PulsarMessage<T> wrapMessage(Message<T> message) {
        final PulsarMessage<T> pulsarMessage = new PulsarMessage<T>();

        pulsarMessage.setValue(message.getValue());
        pulsarMessage.setMessageId(message.getMessageId());
        pulsarMessage.setSequenceId(message.getSequenceId());
        pulsarMessage.setProperties(message.getProperties());
        pulsarMessage.setTopicName(message.getTopicName());
        pulsarMessage.setKey(message.getKey());
        pulsarMessage.setEventTime(message.getEventTime());
        pulsarMessage.setPublishTime(message.getPublishTime());
        pulsarMessage.setProducerName(message.getProducerName());

        return pulsarMessage;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public Disposable onError(java.util.function.Consumer<? super FailedMessage> consumer) {
        return sink.asFlux().subscribe(consumer);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }
}
