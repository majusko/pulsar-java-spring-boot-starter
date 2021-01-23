package io.github.majusko.pulsar;

import io.github.majusko.pulsar.annotation.PulsarConsumer;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.msg.AvroMsg;
import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar.msg.ProtoMsg;
import org.apache.pulsar.client.api.SubscriptionType;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TestConsumers {

    public AtomicBoolean mockTopicListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean mockTopicAsyncListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean mockTopicMessageListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean avroTopicReceived = new AtomicBoolean(false);
    public AtomicBoolean protoTopicReceived = new AtomicBoolean(false);
    public AtomicBoolean mockRetryCountListenerReceived = new AtomicBoolean(false);
    public AtomicInteger retryCount = new AtomicInteger(0);

    @PulsarConsumer(topic = "topic-one", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicOneListener(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        mockTopicListenerReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-for-error", clazz = String.class, serialization = Serialization.JSON)
    public void topicForErrorListener(Integer myMsg) {
    }

    @PulsarConsumer(topic = "topic-avro", clazz = AvroMsg.class, serialization = Serialization.AVRO)
    public void avroTopic(AvroMsg avroMsg) {
        Assertions.assertNotNull(avroMsg);
        avroTopicReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-proto", clazz = ProtoMsg.class, serialization = Serialization.PROTOBUF)
    public void protoTopic(ProtoMsg protoMsg) {
        Assertions.assertNotNull(protoMsg);
        protoTopicReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-async", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicAsyncListener(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        mockTopicAsyncListenerReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-message", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicMessageListener(PulsarMessage<MyMsg> myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertNotNull(myMsg.getProducerName());
        Assertions.assertNotNull(myMsg.getProperties());
        Assertions.assertNotNull(myMsg.getKey());
        Assertions.assertNotNull(myMsg.getSequenceId());
        Assertions.assertNotNull(myMsg.getPublishTime());
        Assertions.assertNotNull(myMsg.getTopicName());
        Assertions.assertNotNull(myMsg.getMessageId());
        mockTopicMessageListenerReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-retry", clazz = MyMsg.class, maxRedeliverCount = 3, subscriptionType = SubscriptionType.Shared, deadLetterTopic = "dead-letter-topic")
    public void failTwice(MyMsg myMsg) throws Exception {
        int retryAttempt = retryCount.getAndIncrement();

        if(retryAttempt < 2) {
            throw new Exception("Expected msg fail.");
        }
        Assertions.assertNotNull(myMsg);
        mockRetryCountListenerReceived.set(true);
    }
}
