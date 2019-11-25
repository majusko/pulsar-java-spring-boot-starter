package io.github.majusko.pulsar.producer;

import java.util.Map;

public class ProducerFactory {

    private final Map<String, Class<?>> topics;

    public ProducerFactory(Map<String, Class<?>> topics) {
        this.topics = topics;
    }

    public Map<String, Class<?>> getTopics() {
        return topics;
    }
}
