package io.github.majusko.pulsar.reactor;

import com.google.common.base.Strings;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.error.exception.ClientInitException;
import org.apache.pulsar.client.api.SubscriptionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

public class PulsarFluxConsumer<T> implements FluxConsumer<T> {
    private final Sinks.Many<T> simpleSink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    private final Sinks.Many<FluxConsumerHolder> robustSink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    private final String topic;

    private final Class<?> clazz;

    private final Serialization serialization;

    private final SubscriptionType subscriptionType;

    private final String consumerName;

    private final String subscriptionName;

    private final int maxRedeliverCount;

    private final String deadLetterTopic;

    private final boolean simple;

    // TODO add buffer size configuration
    private PulsarFluxConsumer(
        String topic,
        Class<?> clazz,
        Serialization serialization,
        SubscriptionType subscriptionType,
        String consumerName,
        String subscriptionName,
        int maxRedeliverCount,
        String deadLetterTopic,
        boolean simple
    ) {
        this.topic = topic;
        this.clazz = clazz;
        this.serialization = serialization;
        this.subscriptionType = subscriptionType;
        this.consumerName = consumerName;
        this.subscriptionName = subscriptionName;
        this.maxRedeliverCount = maxRedeliverCount;
        this.deadLetterTopic = deadLetterTopic;
        this.simple = simple;
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

    public boolean isSimple() {
        return simple;
    }

    public Sinks.EmitResult simpleEmit(T msg) {
        return simpleSink.tryEmitNext(msg);
    }

    public Sinks.EmitResult simpleEmitError(Throwable error) {
        return simpleSink.tryEmitError(error);
    }

    public Flux<T> asSimpleFlux() {
        return simpleSink.asFlux();
    }

    public Flux<FluxConsumerHolder> asFlux() {
        return robustSink.asFlux();
    }

    public Sinks.EmitResult emit(FluxConsumerHolder msg) {
        return robustSink.tryEmitNext(msg);
    }

    public Sinks.EmitResult emitError(Throwable error) {
        return robustSink.tryEmitError(error);
    }

    public static FluxConsumerBuilder builder() {
        return new FluxConsumerBuilder();
    }

    public static class FluxConsumerBuilder {
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

        /**
         * Define rather you wish to use simple subscription or you wish to handle negative acknowledges.
         * By default, the simple subscription is used.
         */
        private boolean simple = true;

        public FluxConsumerBuilder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public FluxConsumerBuilder setClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public FluxConsumerBuilder setSerialization(Serialization serialization) {
            this.serialization = serialization;
            return this;
        }

        public FluxConsumerBuilder setSubscriptionType(SubscriptionType subscriptionType) {
            this.subscriptionType = subscriptionType;
            return this;
        }

        public FluxConsumerBuilder setConsumerName(String consumerName) {
            this.consumerName = consumerName;
            return this;
        }

        public FluxConsumerBuilder setSubscriptionName(String subscriptionName) {
            this.subscriptionName = subscriptionName;
            return this;
        }

        public FluxConsumerBuilder setMaxRedeliverCount(int maxRedeliverCount) {
            this.maxRedeliverCount = maxRedeliverCount;
            return this;
        }

        public FluxConsumerBuilder setDeadLetterTopic(String deadLetterTopic) {
            this.deadLetterTopic = deadLetterTopic;
            return this;
        }

        public FluxConsumerBuilder setSimple(boolean simple) {
            this.simple = simple;
            return this;
        }

        public <T> PulsarFluxConsumer<T> build() throws ClientInitException {
            validateBuilder();

            return new PulsarFluxConsumer<>(topic, clazz, serialization, subscriptionType, consumerName, subscriptionName, maxRedeliverCount, deadLetterTopic, simple);
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
