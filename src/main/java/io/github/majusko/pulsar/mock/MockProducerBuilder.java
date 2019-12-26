package io.github.majusko.pulsar.mock;

import org.apache.pulsar.client.api.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MockProducerBuilder<T> implements ProducerBuilder<T> {
    @Override
    public Producer<T> create() throws PulsarClientException {
        return new MockProducer<T>();
    }

    @Override
    public CompletableFuture<Producer<T>> createAsync() {
        return CompletableFuture.supplyAsync(MockProducer::new);
    }

    @Override
    public ProducerBuilder<T> clone() {
        return this;
    }

    @Override
    public ProducerBuilder<T> topic(String topicName) {
        return this;
    }

    @Override
    public ProducerBuilder<T> producerName(String producerName) {
        return this;
    }

    @Override
    public ProducerBuilder<T> sendTimeout(int sendTimeout, TimeUnit unit) {
        return this;
    }

    @Override
    public ProducerBuilder<T> maxPendingMessages(int maxPendingMessages) {
        return this;
    }

    @Override
    public ProducerBuilder<T> maxPendingMessagesAcrossPartitions(int maxPendingMessagesAcrossPartitions) {
        return this;
    }

    @Override
    public ProducerBuilder<T> blockIfQueueFull(boolean blockIfQueueFull) {
        return this;
    }

    @Override
    public ProducerBuilder<T> messageRoutingMode(MessageRoutingMode messageRoutingMode) {
        return this;
    }

    @Override
    public ProducerBuilder<T> hashingScheme(HashingScheme hashingScheme) {
        return this;
    }

    @Override
    public ProducerBuilder<T> compressionType(CompressionType compressionType) {
        return this;
    }

    @Override
    public ProducerBuilder<T> messageRouter(MessageRouter messageRouter) {
        return this;
    }

    @Override
    public ProducerBuilder<T> enableBatching(boolean enableBatching) {
        return this;
    }

    @Override
    public ProducerBuilder<T> cryptoKeyReader(CryptoKeyReader cryptoKeyReader) {
        return this;
    }

    @Override
    public ProducerBuilder<T> addEncryptionKey(String key) {
        return this;
    }

    @Override
    public ProducerBuilder<T> cryptoFailureAction(ProducerCryptoFailureAction action) {
        return this;
    }

    @Override
    public ProducerBuilder<T> batchingMaxPublishDelay(long batchDelay, TimeUnit timeUnit) {
        return this;
    }

    @Override
    public ProducerBuilder<T> batchingMaxMessages(int batchMessagesMaxMessagesPerBatch) {
        return this;
    }

    @Override
    public ProducerBuilder<T> batcherBuilder(BatcherBuilder batcherBuilder) {
        return this;
    }

    @Override
    public ProducerBuilder<T> initialSequenceId(long initialSequenceId) {
        return this;
    }

    @Override
    public ProducerBuilder<T> property(String key, String value) {
        return this;
    }

    @Override
    public ProducerBuilder<T> intercept(ProducerInterceptor[] interceptors) {
        return this;
    }

    @Override
    public ProducerBuilder<T> autoUpdatePartitions(boolean autoUpdate) {
        return this;
    }

    @Override
    public ProducerBuilder<T> properties(Map properties) {
        return this;
    }

    @Override
    public ProducerBuilder<T> loadConf(Map config) {
        return this;
    }
}
