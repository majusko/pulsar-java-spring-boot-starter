package io.github.majusko.pulsar.consumer;

import io.github.majusko.pulsar.ConsumerProperties;
import io.github.majusko.pulsar.PulsarMessage;
import io.github.majusko.pulsar.PulsarSpringStarterUtils;
import io.github.majusko.pulsar.collector.ConsumerCollector;
import io.github.majusko.pulsar.collector.ConsumerHolder;
import io.github.majusko.pulsar.error.FailedMessage;
import io.github.majusko.pulsar.error.exception.ConsumerInitException;
import org.apache.pulsar.client.api.*;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@DependsOn({"pulsarClient", "consumerCollector"})
public class ConsumerAggregator implements EmbeddedValueResolverAware {

    private final EmitterProcessor<FailedMessage> exceptionEmitter = EmitterProcessor.create();
    private final ConsumerCollector consumerCollector;
    private final PulsarClient pulsarClient;
    private final ConsumerProperties consumerProperties;

    private StringValueResolver stringValueResolver;
    private List<Consumer> consumers;

    public ConsumerAggregator(ConsumerCollector consumerCollector, PulsarClient pulsarClient,
                              ConsumerProperties consumerProperties) {
        this.consumerCollector = consumerCollector;
        this.pulsarClient = pulsarClient;
        this.consumerProperties = consumerProperties;
    }

    @PostConstruct
    private void init() {
        consumers = consumerCollector.getConsumers().entrySet().stream()
            .map(holder -> subscribe(holder.getKey(), holder.getValue()))
            .collect(Collectors.toList());
    }

    private Consumer<?> subscribe(String name, ConsumerHolder holder) {
        try {
            final ConsumerBuilder<?> clientBuilder = pulsarClient
                .newConsumer(PulsarSpringStarterUtils.getSchema(holder.getAnnotation().serialization(),
                    holder.getAnnotation().clazz()))
                .consumerName("consumer-" + name)
                .subscriptionName("subscription-" + name)
                .topic(stringValueResolver.resolveStringValue(holder.getAnnotation().topic()))
                .subscriptionType(holder.getAnnotation().subscriptionType())
                .messageListener((consumer, msg) -> {
                    try {
                        final Method method = holder.getHandler();
                        method.setAccessible(true);

                        if (holder.isWrapped()) {
                            PulsarMessage pulsarMessage = new PulsarMessage();
                            pulsarMessage.setValue(msg.getValue());
                            pulsarMessage.setMessageId(msg.getMessageId());
                            pulsarMessage.setSequenceId(msg.getSequenceId());
                            pulsarMessage.setProperties(msg.getProperties());
                            pulsarMessage.setTopicName(msg.getTopicName());
                            pulsarMessage.setKey(msg.getKey());
                            pulsarMessage.setEventTime(msg.getEventTime());
                            pulsarMessage.setPublishTime(msg.getPublishTime());
                            pulsarMessage.setProducerName(msg.getProducerName());

                            method.invoke(holder.getBean(), pulsarMessage);
                        } else {
                            method.invoke(holder.getBean(), msg.getValue());
                        }

                        consumer.acknowledge(msg);
                    } catch (Exception e) {
                        consumer.negativeAcknowledge(msg);
                        exceptionEmitter.onNext(new FailedMessage(e, consumer, msg));
                    }
                });

            if (consumerProperties.getDeadLetterPolicyMaxRedeliverCount() >= 0) {
                clientBuilder.deadLetterPolicy(DeadLetterPolicy.builder().maxRedeliverCount(consumerProperties.getDeadLetterPolicyMaxRedeliverCount()).build());
            }

            if (consumerProperties.getAckTimeoutMs() > 0) {
                clientBuilder.ackTimeout(consumerProperties.getAckTimeoutMs(), TimeUnit.MILLISECONDS);
            }

            if (holder.getAnnotation().maxRedeliverCount() >= 0) {
                final DeadLetterPolicy.DeadLetterPolicyBuilder deadLetterBuilder = DeadLetterPolicy.builder();

                deadLetterBuilder.maxRedeliverCount(holder.getAnnotation().maxRedeliverCount());

                if (!holder.getAnnotation().deadLetterTopic().isEmpty()) {
                    deadLetterBuilder.deadLetterTopic(holder.getAnnotation().deadLetterTopic());
                }

                clientBuilder.deadLetterPolicy(deadLetterBuilder.build());
            }

            return clientBuilder.subscribe();
        } catch (PulsarClientException e) {
            throw new ConsumerInitException("Failed to init consumer.", e);
        }
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public Disposable onError(java.util.function.Consumer<? super FailedMessage> consumer) {
        return exceptionEmitter.subscribe(consumer);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }
}
