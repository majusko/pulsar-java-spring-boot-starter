package io.github.majusko.pulsar.producer;

import io.github.majusko.pulsar.annotation.PulsarProducer;
import io.github.majusko.pulsar.constant.Serialization;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@PulsarProducer
public class ProducerFactory implements PulsarProducerFactory {
    private final Map<String, ImmutableTriple<Class<?>, Serialization, Optional<String>>> topics = new HashMap<>();

    public ProducerFactory addProducer(String topic) {
        return addProducer(topic, byte[].class, Serialization.BYTE);
    }

    public ProducerFactory addProducer(String topic, Class<?> clazz) {
        topics.put(topic, new ImmutableTriple<>(clazz, Serialization.JSON, Optional.empty()));
        return this;
    }

    public ProducerFactory addProducer(String topic, Class<?> clazz, Serialization serialization) {
        topics.put(topic, new ImmutableTriple<>(clazz, serialization, Optional.empty()));
        return this;
    }

    public ProducerFactory addProducer(String topic, String namespace, Class<?> clazz, Serialization serialization) {
        topics.put(topic, new ImmutableTriple<>(clazz, serialization, Optional.of(namespace)));
        return this;
    }

    public ProducerFactory addProducer(String topic, String namespace, Class<?> clazz) {
        topics.put(topic, new ImmutableTriple<>(clazz, Serialization.JSON, Optional.of(namespace)));
        return this;
    }

    public Map<String, ImmutableTriple<Class<?>, Serialization, Optional<String>>> getTopics() {
        return topics;
    }
}
