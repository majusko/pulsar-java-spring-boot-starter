package io.github.majusko.pulsar.reactor;

import io.github.majusko.pulsar.error.exception.ClientInitException;
import io.github.majusko.pulsar.properties.ConsumerProperties;
import io.github.majusko.pulsar.properties.PulsarProperties;
import io.github.majusko.pulsar.utils.SchemaUtils;
import io.github.majusko.pulsar.utils.UrlBuildService;
import org.apache.pulsar.client.api.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FluxConsumerFactory {
    private final PulsarClient pulsarClient;
    private final UrlBuildService urlBuildService;
    private final ConsumerProperties consumerProperties;
    private final PulsarProperties pulsarProperties;
    private final ConsumerInterceptor consumerInterceptor;

    private List<Consumer> consumers = new ArrayList<>();

    public FluxConsumerFactory(PulsarClient pulsarClient, UrlBuildService urlBuildService, ConsumerProperties consumerProperties, PulsarProperties pulsarProperties, ConsumerInterceptor consumerInterceptor) {
        this.pulsarClient = pulsarClient;
        this.urlBuildService = urlBuildService;
        this.consumerProperties = consumerProperties;
        this.pulsarProperties = pulsarProperties;
        this.consumerInterceptor = consumerInterceptor;
    }

    public <T> FluxConsumer<T> newConsumer(PulsarFluxConsumer<T> fluxConsumer) throws ClientInitException, PulsarClientException {
        final SubscriptionType subscriptionType = urlBuildService.getSubscriptionType(fluxConsumer.getSubscriptionType());
        final ConsumerBuilder<?> consumerBuilder = pulsarClient
            .newConsumer(SchemaUtils.getSchema(fluxConsumer.getSerialization(), fluxConsumer.getMessageClass()))
            .consumerName(fluxConsumer.getConsumerName())
            .subscriptionName(fluxConsumer.getSubscriptionName())
            .topic(urlBuildService.buildTopicUrl(fluxConsumer.getTopic()))
            .subscriptionInitialPosition(fluxConsumer.getInitialPosition())
            .subscriptionType(subscriptionType)
            .messageListener((consumer, msg) -> {
                try {
                    if(fluxConsumer.isSimple()) {
                        fluxConsumer.simpleEmit((T) msg.getValue());
                        consumer.acknowledge(msg);
                    } else {
                        fluxConsumer.emit(new FluxConsumerHolder(consumer, msg));
                    }
                } catch (Exception e) {
                    consumer.negativeAcknowledge(msg);

                    if(fluxConsumer.isSimple()) {
                        fluxConsumer.simpleEmitError(e);
                    } else {
                        fluxConsumer.emitError(e);
                    }
                }
            });

        if(pulsarProperties.isAllowInterceptor()) {
            consumerBuilder.intercept(consumerInterceptor);
        }

        if (consumerProperties.getAckTimeoutMs() > 0) {
            consumerBuilder.ackTimeout(consumerProperties.getAckTimeoutMs(), TimeUnit.MILLISECONDS);
        }

        urlBuildService.buildDeadLetterPolicy(fluxConsumer.getMaxRedeliverCount(), fluxConsumer.getDeadLetterTopic(), consumerBuilder);

        consumers.add(consumerBuilder.subscribe());

        return fluxConsumer;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }
}
