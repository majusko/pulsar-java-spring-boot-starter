package io.github.majusko.pulsar.reactor;

import io.github.majusko.pulsar.error.exception.ClientInitException;
import io.github.majusko.pulsar.properties.ConsumerProperties;
import io.github.majusko.pulsar.utils.SchemaUtils;
import io.github.majusko.pulsar.utils.UrlBuildService;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FluxConsumerFactory {
    private final PulsarClient pulsarClient;
    private final UrlBuildService urlBuildService;
    private final ConsumerProperties consumerProperties;

    public FluxConsumerFactory(PulsarClient pulsarClient, UrlBuildService urlBuildService, ConsumerProperties consumerProperties) {
        this.pulsarClient = pulsarClient;
        this.urlBuildService = urlBuildService;
        this.consumerProperties = consumerProperties;
    }

    public <T> FluxConsumer<T> newConsumer(PulsarFluxConsumer<T> fluxConsumer) throws ClientInitException, PulsarClientException {
        final SubscriptionType subscriptionType = urlBuildService.getSubscriptionType(fluxConsumer.getSubscriptionType());
        final ConsumerBuilder<?> consumerBuilder = pulsarClient
            .newConsumer(SchemaUtils.getSchema(fluxConsumer.getSerialization(), fluxConsumer.getClazz()))
            .consumerName(fluxConsumer.getConsumerName())
            .subscriptionName(fluxConsumer.getSubscriptionName())
            .topic(urlBuildService.buildTopicUrl(fluxConsumer.getTopic()))
            .subscriptionType(subscriptionType)
            .messageListener((consumer, msg) -> {
                try {
                    fluxConsumer.emit((T) msg.getValue());
                    consumer.acknowledge(msg);
                } catch (Exception e) {
                    consumer.negativeAcknowledge(msg);
                    fluxConsumer.emitError(e);
                }
            });

        if (consumerProperties.getAckTimeoutMs() > 0) {
            consumerBuilder.ackTimeout(consumerProperties.getAckTimeoutMs(), TimeUnit.MILLISECONDS);
        }

        urlBuildService.buildDeadLetterPolicy(fluxConsumer.getMaxRedeliverCount(), fluxConsumer.getDeadLetterTopic(), consumerBuilder);

        consumerBuilder.subscribe();

        return fluxConsumer;
    }
}
