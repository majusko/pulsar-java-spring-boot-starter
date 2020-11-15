package io.github.majusko.pulsar.producer;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PulsarTemplate<T> {

    private final ProducerCollector producerCollector;

    public PulsarTemplate(ProducerCollector producerCollector) {
        this.producerCollector = producerCollector;
    }

    public MessageId send(String topic, T msg) throws PulsarClientException {
        //noinspection unchecked
        return producerCollector.getProducers().get(topic).send(msg);
    }

    public CompletableFuture<MessageId> sendAsync(String topic, T message) {
        return producerCollector.getProducers().get(topic).sendAsync(message);
    }

    public TypedMessageBuilder<T> createMessage(String topic, T message) {
        return producerCollector.getProducers().get(topic).newMessage().value(message);
    }
}
