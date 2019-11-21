package io.github.majusko.pulsar.annotation;

import io.github.majusko.pulsar.constant.Serialization;

public @interface PulsarListener {
    Class<?> clazz = null;
    Serialization serialization = Serialization.JSON;
}
