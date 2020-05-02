package io.github.majusko.pulsar;

import io.github.majusko.pulsar.collector.ConsumerCollector;
import io.github.majusko.pulsar.collector.ConsumerHolder;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.consumer.ConsumerBuilder;
import io.github.majusko.pulsar.producer.ProducerFactory;
import io.github.majusko.pulsar.producer.PulsarTemplate;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
@Import({TestProducerConfiguration.class, TestConsumerConfiguration.class})
class PulsarJavaSpringBootStarterApplicationTests {

    @Autowired
    private ConsumerBuilder consumerBuilder;

    @Autowired
    private ConsumerCollector consumerCollector;

    @Autowired
    private ProducerFactory producerFactory;

    @Autowired
    private PulsarTemplate<MyMsg> producer;

    @Test
    void testProducerSendMethod() throws PulsarClientException {
        producer.send("topic-one", new MyMsg("bb"));
    }

    @Test
    void testConsumerRegistration1() throws Exception {
        final List<Consumer> consumers = consumerBuilder.getConsumers();

        Assertions.assertEquals(1, consumers.size());

        final Consumer consumer = consumers.stream().findFirst().orElseThrow(Exception::new);

        Assertions.assertNotNull(consumer);
        Assertions.assertEquals("mock-topic", consumer.getTopic());
    }

    @Test
    void testConsumerRegistration2() {
        final Class<TestConsumerConfiguration> clazz = TestConsumerConfiguration.class;
        final String descriptor = clazz.getName() + "#" + clazz.getMethods()[0].getName();
        final ConsumerHolder consumerHolder = consumerCollector.getConsumer(descriptor).orElse(null);

        Assertions.assertNotNull(consumerHolder);
        Assertions.assertEquals("mock-topic", consumerHolder.getAnnotation().topic());
        Assertions.assertEquals(TestConsumerConfiguration.class, consumerHolder.getBean().getClass());
        Assertions.assertEquals("mockTheListener", consumerHolder.getHandler().getName());
    }

    @Test
    void testProducerRegistration() {

        final Map<String, ImmutablePair<Class<?>, Serialization>> topics = producerFactory.getTopics();

        Assertions.assertEquals(2, topics.size());

        final Set<String> topicNames = new HashSet<>(topics.keySet());

        Assertions.assertTrue(topicNames.contains("topic-one"));
        Assertions.assertTrue(topicNames.contains("topic-two"));
    }
}
