package io.github.majusko.pulsar.annotation;

import io.github.majusko.pulsar.constant.Serialization;
import org.apache.pulsar.client.api.SubscriptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PulsarConsumer {
    String topic();

    Class<?> clazz() default byte[].class;

    Serialization serialization() default Serialization.JSON;

    SubscriptionType subscriptionType() default SubscriptionType.Exclusive;

    /**
     * (Optional) Consumer names are auto-generated but in case you wish to use your custom consumer names,
     * feel free to override it.
     */
    String consumerName() default "";

    /**
     * (Optional) Subscription names are auto-generated but in case you wish to use your custom subscription names,
     * feel free to override it.
     */
    String subscriptionName() default "";

    /**
     * Maximum number of times that a message will be redelivered before being sent to the dead letter queue.
     * Note: Currently, dead letter topic is enabled only in the shared subscription mode.
     */
    int maxRedeliverCount() default -1;

    /**
     * Name of the dead topic where the failing messages will be sent.
     */
    String deadLetterTopic() default "";
}
