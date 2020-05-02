package io.github.majusko.pulsar.mock;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.CompletableFuture;

public class MockProducer<T> implements Producer<T> {
    @Override
    public String getTopic() {
        return "mock-topic";
    }

    @Override
    public String getProducerName() {
        return "mock-producer";
    }

    @Override
    public MessageId send(Object message) throws PulsarClientException {
        return null;
    }

    @Override
    public CompletableFuture<MessageId> sendAsync(Object message) {
        return null;
    }

    @Override
    public void flush() throws PulsarClientException {

    }

    @Override
    public CompletableFuture<Void> flushAsync() {
        return null;
    }

    @Override
    public TypedMessageBuilder newMessage() {
        return null;
    }

    @Override
    public <V> TypedMessageBuilder<V> newMessage(Schema<V> schema) {
        return null;
    }

    @Override
    public long getLastSequenceId() {
        return 0;
    }

    @Override
    public ProducerStats getStats() {
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
    public boolean isConnected() {
        return false;
    }
}
