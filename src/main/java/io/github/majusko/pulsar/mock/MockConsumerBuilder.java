package io.github.majusko.pulsar.mock;

import org.apache.pulsar.client.api.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MockConsumerBuilder<T> implements ConsumerBuilder<T> {
    @Override
    public ConsumerBuilder<T> clone() {
        return this;
    }

    @Override
    public Consumer<T> subscribe() throws PulsarClientException {
        return new MockConsumer<>();
    }

    @Override
    public CompletableFuture<Consumer<T>> subscribeAsync() {
        return CompletableFuture.supplyAsync(MockConsumer::new);
    }

    @Override
    public ConsumerBuilder<T> topic(String... topicNames) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> topicsPattern(Pattern topicsPattern) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> topicsPattern(String topicsPattern) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> subscriptionName(String subscriptionName) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> ackTimeout(long ackTimeout, TimeUnit timeUnit) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> ackTimeoutTickTime(long tickTime, TimeUnit timeUnit) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> negativeAckRedeliveryDelay(long redeliveryDelay, TimeUnit timeUnit) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> subscriptionType(SubscriptionType subscriptionType) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> messageListener(MessageListener messageListener) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> cryptoKeyReader(CryptoKeyReader cryptoKeyReader) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> cryptoFailureAction(ConsumerCryptoFailureAction action) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> receiverQueueSize(int receiverQueueSize) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> acknowledgmentGroupTime(long delay, TimeUnit unit) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> replicateSubscriptionState(boolean replicateSubscriptionState) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> maxTotalReceiverQueueSizeAcrossPartitions(int maxTotalReceiverQueueSizeAcrossPartitions) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> consumerName(String consumerName) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> consumerEventListener(ConsumerEventListener consumerEventListener) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> readCompacted(boolean readCompacted) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> patternAutoDiscoveryPeriod(int periodInMinutes) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> priorityLevel(int priorityLevel) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> property(String key, String value) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> subscriptionInitialPosition(SubscriptionInitialPosition subscriptionInitialPosition) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> subscriptionTopicsMode(RegexSubscriptionMode regexSubscriptionMode) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> intercept(ConsumerInterceptor[] interceptors) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> deadLetterPolicy(DeadLetterPolicy deadLetterPolicy) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> autoUpdatePartitions(boolean autoUpdate) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> properties(Map properties) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> topics(List topicNames) {
        return this;
    }

    @Override
    public ConsumerBuilder<T> loadConf(Map config) {
        return this;
    }
}
