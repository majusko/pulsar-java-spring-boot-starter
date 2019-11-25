package io.github.majusko.pulsar.test;

import io.github.majusko.pulsar.annotation.PulsarProducer;
import io.github.majusko.pulsar.producer.PulsarBasicProducer;

@PulsarProducer(topic = "", clazz = MyMsg.class)
public interface TestBasicProducer extends PulsarBasicProducer<MyMsg> {
}
