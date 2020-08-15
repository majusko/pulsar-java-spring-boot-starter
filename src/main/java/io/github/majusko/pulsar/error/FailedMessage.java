package io.github.majusko.pulsar.error;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;

public class FailedMessage {
    private final Exception exception;
    private final Consumer<?> consumer;
    private final Message<?> message;

    public FailedMessage(Exception exception, Consumer<?> consumer, Message<?> message) {
        this.exception = exception;
        this.consumer = consumer;
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public Consumer<?> getConsumer() {
        return consumer;
    }

    public Message<?> getMessage() {
        return message;
    }
}
