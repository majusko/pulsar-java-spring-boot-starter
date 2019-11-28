package io.github.majusko.pulsar.producer;

import io.github.majusko.pulsar.constant.Serialization;
import org.apache.pulsar.shade.org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;

public interface PulsarProducerFactory {
    Map<String, ImmutablePair<Class<?>, Serialization>> getTopics();
}
