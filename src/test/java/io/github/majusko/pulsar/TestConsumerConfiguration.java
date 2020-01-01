package io.github.majusko.pulsar;

import io.github.majusko.pulsar.annotation.PulsarConsumer;
import io.github.majusko.pulsar.constant.Serialization;
import org.junit.Assert;
import org.springframework.stereotype.Service;

@Service
public class TestConsumerConfiguration {

    @PulsarConsumer(topic = "mock-topic", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void mockTheListener(MyMsg myMsg) {
        Assert.assertNotNull(myMsg);
    }
}
