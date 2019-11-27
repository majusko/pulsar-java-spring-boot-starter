package io.github.majusko.pulsar.producer;

import io.github.majusko.pulsar.consumer.ProducerBuilder;
import org.apache.pulsar.client.api.Producer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PulsarTemplate<T> {

    private Map<String, Producer> producers = new HashMap<>();

    void setTopics(ProducerBuilder producerBuilder) {
        this.producers.putAll(producerBuilder.getProducers());
    }

    public void send(String topic, T msg) {

    }
}
