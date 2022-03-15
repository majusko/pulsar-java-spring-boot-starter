package io.github.majusko.pulsar;

import io.github.majusko.pulsar.producer.DefaultProducerInterceptor;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TestProducerInterceptor extends DefaultProducerInterceptor {

    public AtomicBoolean eligibleReceived = new AtomicBoolean(false);
    public AtomicBoolean beforeSendReceived = new AtomicBoolean(false);
    public AtomicBoolean onSendAcknowledgementReceived = new AtomicBoolean(false);

    @Override
    public boolean eligible(Message message) {
        eligibleReceived.set(true);
        return super.eligible(message);
    }

    @Override
    public Message beforeSend(Producer producer, Message message) {
        beforeSendReceived.set(true);
        return super.beforeSend(producer, message);
    }

    @Override
    public void onSendAcknowledgement(Producer producer, Message message, MessageId msgId, Throwable exception) {
        onSendAcknowledgementReceived.set(true);
        super.onSendAcknowledgement(producer, message, msgId, exception);
    }
}