package io.github.majusko.pulsar.producer;

import io.github.majusko.pulsar.constant.Serialization;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.Map;
import java.util.Optional;

public interface PulsarProducerFactory {
    Map<String, ImmutableTriple<Class<?>, Serialization, Optional<String>>> getTopics();
}
