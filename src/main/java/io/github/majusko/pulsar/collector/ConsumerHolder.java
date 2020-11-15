package io.github.majusko.pulsar.collector;

import io.github.majusko.pulsar.PulsarMessage;
import io.github.majusko.pulsar.annotation.PulsarConsumer;

import java.lang.reflect.Method;

public class ConsumerHolder {

    private final PulsarConsumer annotation;
    private final Method handler;
    private final Object bean;
    private final Class<?> type;

    ConsumerHolder(PulsarConsumer annotation, Method handler, Object bean, Class<?> type) {
        this.annotation = annotation;
        this.handler = handler;
        this.bean = bean;
        this.type = type;
    }

    public PulsarConsumer getAnnotation() {
        return annotation;
    }

    public Method getHandler() {
        return handler;
    }

    public Object getBean() {
        return bean;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isWrapped() {
        return type.isAssignableFrom(PulsarMessage.class);
    }
}
