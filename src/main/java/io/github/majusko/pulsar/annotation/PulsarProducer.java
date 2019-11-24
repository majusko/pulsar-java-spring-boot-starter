package io.github.majusko.pulsar.annotation;

import io.github.majusko.pulsar.constant.Serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PulsarProducer {
    String topic();

    Class<?> clazz();

    Serialization serialization() default Serialization.JSON;
}
