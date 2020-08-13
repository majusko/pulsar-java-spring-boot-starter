package io.github.majusko.pulsar;

import io.github.majusko.pulsar.collector.ConsumerCollector;
import io.github.majusko.pulsar.collector.ConsumerHolder;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.consumer.ConsumerBuilder;
import io.github.majusko.pulsar.producer.ProducerFactory;
import io.github.majusko.pulsar.producer.PulsarTemplate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Import({TestProducerConfiguration.class, TestConsumers.class})
@Testcontainers
class PulsarJavaSpringBootStarterApplicationTests {

    @Autowired
    private ConsumerBuilder consumerBuilder;

    @Autowired
    private ConsumerCollector consumerCollector;

    @Autowired
    private ProducerFactory producerFactory;

    @Autowired
    private PulsarTemplate<MyMsg> producer;

    @Container
    static PulsarContainer pulsarContainer = new PulsarContainer();

    @Autowired
    private TestConsumers testConsumers;

    @DynamicPropertySource
    static void propertySettings(DynamicPropertyRegistry registry) {
        registry.add("pulsar.serviceUrl", pulsarContainer::getPulsarBrokerUrl);
    }

    @Test
    void testProducerSendMethod() throws PulsarClientException {
        producer.send("topic-one", new MyMsg("bb"));

        await().untilTrue(testConsumers.mockTopicListenerReceived);
    }

    @Test
    void testConsumerRegistration1() throws Exception {
        final List<Consumer> consumers = consumerBuilder.getConsumers();

        Assertions.assertEquals(1, consumers.size());

        final Consumer consumer = consumers.stream().findFirst().orElseThrow(Exception::new);

        Assertions.assertNotNull(consumer);
        Assertions.assertEquals("topic-one", consumer.getTopic());
    }

    @Test
    void testConsumerRegistration2() {
        final Class<TestConsumers> clazz = TestConsumers.class;
        final String descriptor = clazz.getName() + "#" + clazz.getMethods()[0].getName();
        final ConsumerHolder consumerHolder = consumerCollector.getConsumer(descriptor).orElse(null);

        Assertions.assertNotNull(consumerHolder);
        Assertions.assertEquals("topic-one", consumerHolder.getAnnotation().topic());
        Assertions.assertEquals(TestConsumers.class, consumerHolder.getBean().getClass());
        Assertions.assertEquals("topicOneListener", consumerHolder.getHandler().getName());
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
