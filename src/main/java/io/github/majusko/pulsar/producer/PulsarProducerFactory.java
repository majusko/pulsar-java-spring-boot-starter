package io.github.majusko.pulsar.producer;

import java.util.Map;

public interface PulsarProducerFactory {
    Map<String, Class<?>> getTopics();
}
