package io.github.majusko.pulsar;

import io.github.majusko.pulsar.annotation.PulsarConsumer;
import io.github.majusko.pulsar.constant.Serialization;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TestConsumers {

    public AtomicBoolean mockTopicListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean mockTopicAsyncListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean mockTopicMessageListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean avroTopicReceived = new AtomicBoolean(false);

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

    @PulsarConsumer(topic = "topic-async", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicAsyncListener(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        mockTopicAsyncListenerReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-message", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicMessageListener(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        mockTopicMessageListenerReceived.set(true);
    }
}
