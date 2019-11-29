package io.github.majusko.pulsar.collector;

import io.github.majusko.pulsar.annotation.PulsarConsumer;

import java.lang.reflect.Method;

public class ConsumerHolder {

    private final PulsarConsumer annotation;
    private final Method handler;
    private final Object bean;

    ConsumerHolder(PulsarConsumer annotation, Method handler, Object bean) {
        this.annotation = annotation;
        this.handler = handler;
        this.bean = bean;
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
}
