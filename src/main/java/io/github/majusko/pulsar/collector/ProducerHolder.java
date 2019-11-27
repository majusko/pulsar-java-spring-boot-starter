package io.github.majusko.pulsar.collector;

import io.github.majusko.pulsar.constant.Serialization;

public class ProducerHolder {

    private final String topic;
    private final Class<?> clazz;
    private final Serialization serialization = Serialization.JSON;

    public ProducerHolder(String topic, Class<?> clazz) {
        this.topic = topic;
        this.clazz = clazz;
    }

    public String getTopic() {
        return topic;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Serialization getSerialization() {
        return serialization;
    }
}
