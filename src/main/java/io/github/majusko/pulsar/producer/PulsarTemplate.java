package io.github.majusko.pulsar.producer;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.stereotype.Component;

@Component
public class PulsarTemplate<T> {

    private final ProducerCollector producerCollector;

    public PulsarTemplate(ProducerCollector producerCollector) {
        this.producerCollector = producerCollector;
    }

    public MessageId send(String topic, T msg) throws PulsarClientException {
        return producerCollector.getProducers().get(topic).send(msg);
    }
}
