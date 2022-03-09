package io.github.majusko.pulsar.producer;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProducerInterceptor implements ProducerInterceptor {
    Logger logger = LoggerFactory.getLogger(DefaultProducerInterceptor.class);

    @Override
    public void close() {

    }

    @Override
    public boolean eligible(Message message) {
        return true;
    }

    @Override
    public Message beforeSend(Producer producer, Message message) {
        logger.info("[Pulsar producer log:BeforeSend] ProducerName:[{}], Topic:[{}], Payload:[{}]",
                producer.getProducerName(),
                producer.getTopic(),
                new String(message.getData()));
        return message;
    }

    /**
     * This method will generally execute in the background I/O thread, so the
     * implementation should be reasonably fast. Otherwise, sending of messages
     * from other threads could be delayed.
     */
    @Override
    public void onSendAcknowledgement(Producer producer, Message message, MessageId msgId, Throwable exception) {
        if (exception != null) {
            logger.error("[Pulsar producer log:OnSendAcknowledgement] Producer:[{}], Topic:[{}], Payload:[{}], msgID:[{}], exception:[{}]",
                    producer.getProducerName(), producer.getTopic(), message.getValue().toString(), msgId.toString(), exception);
            return;
        }
        logger.info("[Pulsar producer log:OnSendAcknowledgement] Producer:[{}], Topic:[{}], Payload:[{}], msgID:[{}]",
                producer.getProducerName(), producer.getTopic(), new String(message.getData()), msgId.toString());
    }

    @Override
    public void onPartitionsChange(String topicName, int partitions) {
        logger.info("[Pulsar producer log:OnPartitionsChange] Topic:[{}], Partitions:[{}]", topicName, partitions);
    }
}
