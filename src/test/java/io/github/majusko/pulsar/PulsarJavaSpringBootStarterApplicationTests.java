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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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

    @Autowired
    private PulsarTemplate<String> producerForError;

    @Autowired
    private PulsarTemplate<AvroMsg> producerForAvroTopic;

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
    void testProducerSendAsyncMethod() throws PulsarClientException {
        producer.sendAsync("topic-async", new MyMsg("async")).thenAccept(messageId -> {
            Assertions.assertNotNull(messageId);
        });

        await().untilTrue(testConsumers.mockTopicAsyncListenerReceived);
    }

    @Test
    void testProducerCreateMessageMethod() throws PulsarClientException {
        producer.createMessage("topic-message", new MyMsg("my-message"))
                .property("my-key", "my-value")
                .property("my-other-key", "my-other-value")
                .sequenceId(123l)
                .key("my-key")
                .send();

        await().untilTrue(testConsumers.mockTopicMessageListenerReceived);
    }

    @Test
    void testConsumerRegistration1() throws Exception {
        final List<Consumer> consumers = consumerBuilder.getConsumers();

        Assertions.assertEquals(5, consumers.size());

        final Consumer<?> consumer = consumers.stream().filter( $-> $.getTopic().equals("topic-one")).findFirst().orElseThrow(Exception::new);

        Assertions.assertNotNull(consumer);
    }

    @Test
    void testConsumerRegistration2() {
        final Class<TestConsumers> clazz = TestConsumers.class;
        final List<ConsumerHolder> consumerHolders = Arrays.stream(clazz.getMethods())
            .map($ -> consumerCollector.getConsumer(clazz.getName() + $.getName()).orElse(null))
            .collect(Collectors.toList());

        Assertions.assertNotNull(consumerHolders);
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getAnnotation().topic().equals("topic-one")));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getAnnotation().topic().equals("topic-for-error")));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getBean().getClass().equals(TestConsumers.class)));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getHandler().getName().equals("topicOneListener")));
    }

    @Test
    void testProducerRegistration() {

        final Map<String, ImmutablePair<Class<?>, Serialization>> topics = producerFactory.getTopics();

        Assertions.assertEquals(6, topics.size());

        final Set<String> topicNames = new HashSet<>(topics.keySet());

        Assertions.assertTrue(topicNames.contains("topic-one"));
        Assertions.assertTrue(topicNames.contains("topic-two"));
    }

    @Test
    void testMessageErrorHandling() throws PulsarClientException {
        final AtomicBoolean receivedError = new AtomicBoolean(false);
        final String messageToSend = "This message will never arrive.";

        producerForError.send("topic-for-error", messageToSend);

        consumerBuilder.onError(($) -> {
            Assertions.assertEquals($.getConsumer().getTopic(), "topic-for-error");
            Assertions.assertEquals($.getMessage().getValue(), messageToSend);
            Assertions.assertNotNull($.getException());

            receivedError.set(true);
        });

        await().untilTrue(receivedError);
    }

    @Test
    void avroSerializationTestOk() throws Exception {
        AvroMsg testAvroMsg = new AvroMsg();
        testAvroMsg.setData("avro-test");
        producerForAvroTopic.send("topic-avro", testAvroMsg);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.avroTopicReceived.get());
    }
}
