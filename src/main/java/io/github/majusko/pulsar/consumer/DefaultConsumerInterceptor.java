package io.github.majusko.pulsar.consumer;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author crossoverJie
 * Date: 2022/3/10 00:32
 */
public class DefaultConsumerInterceptor<T> implements ConsumerInterceptor<T> {
    Logger logger = LoggerFactory.getLogger(DefaultConsumerInterceptor.class);
    @Override
    public void close() {

    }

    @Override
    public Message<T> beforeConsume(Consumer<T> consumer, Message<T> message) {
        logger.info("[Pulsar consumer log:BeforeConsume] ProducerName[{}], ConsumerName:[{}], Topic:[{}], msgID:[{}], Payload:[{}],"+
                " MessageKey:[{}], PublishTime:[{}], RedeliveryCount:[{}], GetReplicatedFrom:[{}]",
                message.getProducerName(), consumer.getConsumerName(), message.getTopicName(), message.getMessageId(), new String(message.getData()),
                message.getKey(), message.getPublishTime(), message.getRedeliveryCount(), message.getReplicatedFrom());

        return message;
    }

    @Override
    public void onAcknowledge(Consumer<T> consumer, MessageId messageId, Throwable exception) {
        if (exception != null){
            logger.info("[Pulsar consumer log:OnAcknowledge] ConsumerName:[{}], msgID:[{}], exception:[{}]", consumer.getConsumerName(), messageId, exception);
            return;
        }
        logger.info("[Pulsar consumer log:OnAcknowledge] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName(), messageId);
    }

    @Override
    public void onAcknowledgeCumulative(Consumer<T> consumer, MessageId messageId, Throwable exception) {
        if (exception != null){
            logger.info("[Pulsar consumer log:OnAcknowledgeCumulative] ConsumerName:[{}], msgID:[{}], exception:[{}]", consumer.getConsumerName(), messageId, exception);
            return;
        }
        logger.info("[Pulsar consumer log:OnAcknowledgeCumulative] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName(), messageId);
    }

    @Override
    public void onNegativeAcksSend(Consumer<T> consumer, Set<MessageId> messageIds) {
        logger.info("[Pulsar consumer log:OnNegativeAcksSend] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName() ,messageIds);
    }

    @Override
    public void onAckTimeoutSend(Consumer<T> consumer, Set<MessageId> messageIds) {
        logger.info("[Pulsar consumer log:OnAckTimeoutSend] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName() ,messageIds);
    }

    @Override
    public void onPartitionsChange(String topicName, int partitions) {
        logger.info("[Pulsar consumer log:OnPartitionsChange] Topic:[{}], Partitions:[{}]", topicName, partitions);
    }
}
