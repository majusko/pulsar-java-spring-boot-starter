package io.github.majusko.pulsar.collector;

import io.github.majusko.pulsar.annotation.PulsarProducer;

import java.lang.reflect.Method;

public class ProducerHolder {

    private final PulsarProducer annotation;
    private final Method handler;
    private final Object bean;

    public ProducerHolder(PulsarProducer annotation, Method handler, Object bean) {
        this.annotation = annotation;
        this.handler = handler;
        this.bean = bean;
    }

    public PulsarProducer getAnnotation() {
        return annotation;
    }

    public Method getHandler() {
        return handler;
    }

    public Object getBean() {
        return bean;
    }
}
