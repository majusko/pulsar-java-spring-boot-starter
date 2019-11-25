package io.github.majusko.pulsar.producer;

public interface PulsarBasicProducer<T> {
    void send(T msg);

    void sendAsync(T msg);
}
