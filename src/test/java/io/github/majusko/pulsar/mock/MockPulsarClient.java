package io.github.majusko.pulsar.mock;

import org.apache.pulsar.client.api.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MockPulsarClient implements PulsarClient {

    @Override
    public ProducerBuilder<byte[]> newProducer() {
        return new MockProducerBuilder<>();
    }

    @Override
    public <T> ProducerBuilder<T> newProducer(Schema<T> schema) {
        return new MockProducerBuilder<>();
    }

    @Override
    public ConsumerBuilder<byte[]> newConsumer() {
        return new MockConsumerBuilder<>();
    }

    @Override
    public <T> ConsumerBuilder<T> newConsumer(Schema<T> schema) {
        return new MockConsumerBuilder<>();
    }

    @Override
    public ReaderBuilder<byte[]> newReader() {
        return null;
    }

    @Override
    public <T> ReaderBuilder<T> newReader(Schema<T> schema) {
        return null;
    }

    @Override
    public void updateServiceUrl(String serviceUrl) throws PulsarClientException {
    }

    @Override
    public CompletableFuture<List<String>> getPartitionsForTopic(String topic) {
        return null;
    }

    @Override
    public void close() throws PulsarClientException {
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        return null;
    }

    @Override
    public void shutdown() throws PulsarClientException {
    }
}
