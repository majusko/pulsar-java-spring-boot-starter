package io.github.majusko.pulsar.producer;

import java.util.HashMap;
import java.util.Map;

@PulsarProducer
public class ProducerFactory implements PulsarProducerFactory {

    private final Map<String, Class<?>> topics = new HashMap<>();

    public ProducerFactory addProducer(String topic, Class<?> clazz) {
        topics.put(topic, clazz);
        return this;
    }

    public Map<String, Class<?>> getTopics() {
        return topics;
    }
}
