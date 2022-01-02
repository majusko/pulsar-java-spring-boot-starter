package io.github.majusko.pulsar.reactor;

import com.google.common.base.Strings;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.error.exception.ClientInitException;
import org.apache.pulsar.client.api.SubscriptionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

public class PulsarFluxConsumer<T> implements FluxConsumer<T> {
    private final Sinks.Many<T> sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    private final String topic;

    private final Class<?> clazz;

    private final Serialization serialization;

    private final SubscriptionType subscriptionType;

    private final String consumerName;

    private final String subscriptionName;

    private final int maxRedeliverCount;

    private final String deadLetterTopic;

    private PulsarFluxConsumer(
        String topic,
        Class<?> clazz,
        Serialization serialization,
        SubscriptionType subscriptionType,
        String consumerName,
        String subscriptionName,
        int maxRedeliverCount,
        String deadLetterTopic
    ) {
        this.topic = topic;
        this.clazz = clazz;
        this.serialization = serialization;
        this.subscriptionType = subscriptionType;
        this.consumerName = consumerName;
        this.subscriptionName = subscriptionName;
        this.maxRedeliverCount = maxRedeliverCount;
        this.deadLetterTopic = deadLetterTopic;
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

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public int getMaxRedeliverCount() {
        return maxRedeliverCount;
    }

    public String getDeadLetterTopic() {
        return deadLetterTopic;
    }

    public Sinks.EmitResult emit(T msg) {
        return sink.tryEmitNext(msg);
    }

    public Sinks.EmitResult emitError(Throwable error) {
        return sink.tryEmitError(error);
    }

    public Flux<T> asFlux() {
        return sink.asFlux();
    }

    public static FluxConsumerBuilder<?> builder() {
        return new FluxConsumerBuilder<>();
    }

    static class FluxConsumerBuilder<T> {
        private String topic;

        private Class<?> clazz = byte[].class;

        private Serialization serialization = Serialization.JSON;

        /**
         * (Optional) Type of subscription.
         * <p>
         * Shared - This will allow you to have multiple consumers/instances of the application in a cluster with same subscription
         * name and guarantee that the message is read only by one consumer.
         * <p>
         * Exclusive - message will be delivered to every subscription name only once but won't allow to instantiate multiple
         * instances or consumers of the same subscription name. With a default configuration you don't need to worry about horizontal
         * scaling because message will be delivered to each pod in a cluster since in case of exclusive subscription
         * the name is unique per instance and can be nicely used to update state of each pod in case your service
         * is stateful (For example - you need to update in-memory cached configuration for each instance of authorization microservice).
         * <p>
         * By default the type is `Exclusive` but you can also override the default in `application.properties`.
         * This can be handy in case you are using `Shared` subscription in your application all the time and you
         * don't want to override this value every time you use `@PulsarConsumer`.
         */
        private SubscriptionType subscriptionType = null;

        /**
         * Flux consumer names are NOT auto-generated. Choose your consumer name.
         */
        private String consumerName = "";

        /**
         * Flux subscription names are NOT auto-generated. Choose your subscription name.
         */
        private String subscriptionName = "";

        /**
         * Maximum number of times that a message will be redelivered before being sent to the dead letter queue.
         * Note: Currently, dead letter topic is enabled only in the shared subscription mode.
         */
        private int maxRedeliverCount = -1;

        /**
         * Name of the dead topic where the failing messages will be sent.
         */
        private String deadLetterTopic = "";

        public FluxConsumerBuilder<T> setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public FluxConsumerBuilder<T> setClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public FluxConsumerBuilder<T> setSerialization(Serialization serialization) {
            this.serialization = serialization;
            return this;
        }

        public FluxConsumerBuilder<T> setSubscriptionType(SubscriptionType subscriptionType) {
            this.subscriptionType = subscriptionType;
            return this;
        }

        public FluxConsumerBuilder<T> setConsumerName(String consumerName) {
            this.consumerName = consumerName;
            return this;
        }

        public FluxConsumerBuilder<T> setSubscriptionName(String subscriptionName) {
            this.subscriptionName = subscriptionName;
            return this;
        }

        public FluxConsumerBuilder<T> setMaxRedeliverCount(int maxRedeliverCount) {
            this.maxRedeliverCount = maxRedeliverCount;
            return this;
        }

        public FluxConsumerBuilder<T> setDeadLetterTopic(String deadLetterTopic) {
            this.deadLetterTopic = deadLetterTopic;
            return this;
        }

        public PulsarFluxConsumer<T> build() throws ClientInitException {
            validateBuilder();

            return new PulsarFluxConsumer<T>(topic, clazz, serialization, subscriptionType, consumerName, subscriptionName, maxRedeliverCount, deadLetterTopic);
        }

        private void validateBuilder() throws ClientInitException {
            if (Strings.isNullOrEmpty(topic)) {
                throw new ClientInitException("Topic is empty");
            }
            if (Strings.isNullOrEmpty(consumerName)) {
                throw new ClientInitException("Consumer name is empty");
            }
            if (Strings.isNullOrEmpty(subscriptionName)) {
                throw new ClientInitException("Subscription name is empty");
            }
        }
    }
}
