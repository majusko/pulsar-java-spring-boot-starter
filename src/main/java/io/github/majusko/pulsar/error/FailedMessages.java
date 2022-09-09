package io.github.majusko.pulsar.error;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Messages;

public class FailedMessages {
    private final Exception exception;
    private final Consumer<?> consumer;
    private final Messages<?> messages;

    public FailedMessages(Exception exception, Consumer<?> consumer, Messages<?> messages) {
        this.exception = exception;
        this.consumer = consumer;
        this.messages = messages;
    }

    public Exception getException() {
        return exception;
    }

    public Consumer<?> getConsumer() {
        return consumer;
    }

    public Messages<?> getMessages() {
        return messages;
    }
}
