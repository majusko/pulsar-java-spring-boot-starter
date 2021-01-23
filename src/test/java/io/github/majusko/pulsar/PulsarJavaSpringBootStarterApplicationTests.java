package io.github.majusko.pulsar;

import io.github.majusko.pulsar.collector.ConsumerCollector;
import io.github.majusko.pulsar.collector.ConsumerHolder;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.consumer.ConsumerAggregator;
import io.github.majusko.pulsar.msg.AvroMsg;
import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar.msg.ProtoMsg;
import io.github.majusko.pulsar.producer.ProducerFactory;
import io.github.majusko.pulsar.producer.PulsarTemplate;
import io.github.majusko.pulsar.utils.TopicUrlService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.Disposable;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

@ActiveProfiles("test")
@SpringBootTest
@Import({TestProducerConfiguration.class, TestConsumers.class})
@Testcontainers
class PulsarJavaSpringBootStarterApplicationTests {

    @Autowired
    private ConsumerAggregator consumerAggregator;

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

    @Autowired
    private PulsarTemplate<ProtoMsg> producerForProtoTopic;

    @Autowired
    private PulsarTemplate<byte[]> producerForByteTopic;

    @Autowired
    private PulsarTemplate<String> producerForStringTopic;

    @Autowired
    private TestConsumers testConsumers;

    @Autowired
    private TopicUrlService topicUrlService;

    @Container
    static PulsarContainer pulsarContainer = new PulsarContainer();

    public static final String VALIDATION_STRING = "validation-string";

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
    void testBasicDeadLetterRetryPolicy() throws PulsarClientException {

        producer.send("topic-retry", new MyMsg("bb"));

        await().untilTrue(testConsumers.mockRetryCountListenerReceived);

        Assertions.assertEquals(3, testConsumers.retryCount.get());
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
        final List<Consumer> consumers = consumerAggregator.getConsumers();

        Assertions.assertEquals(9, consumers.size());

        final Consumer<?> consumer =
            consumers.stream().filter($ -> $.getTopic().equals(topicUrlService.buildTopicUrl("topic-one"))).findFirst().orElseThrow(Exception::new);

        Assertions.assertNotNull(consumer);
    }

    @Test
    void testConsumerRegistration2() {
        final Class<TestConsumers> clazz = TestConsumers.class;
        final List<ConsumerHolder> consumerHolders = Arrays.stream(clazz.getMethods())
            .map($ -> consumerCollector.getConsumer(consumerCollector.getConsumerName(clazz, $)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        Assertions.assertNotNull(consumerHolders);
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getAnnotation().topic().equals("topic-one")));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getAnnotation().topic().equals("topic-for" +
            "-error")));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getBean().getClass().equals(TestConsumers.class)));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getHandler().getName().equals(
            "topicOneListener")));
    }

    @Test
    void testProducerRegistration() {

        final Map<String, ImmutablePair<Class<?>, Serialization>> topics = producerFactory.getTopics();

        Assertions.assertEquals(10, topics.size());

        final Set<String> topicNames = new HashSet<>(topics.keySet());

        Assertions.assertTrue(topicNames.contains("topic-one"));
        Assertions.assertTrue(topicNames.contains("topic-two"));
    }

    @Test
    void testMessageErrorHandling() throws PulsarClientException {
        final AtomicBoolean receivedError = new AtomicBoolean(false);
        final String messageToSend = "This message will never arrive.";
        final Disposable disposable = consumerAggregator.onError(($) -> {
            Assertions.assertEquals($.getConsumer().getTopic(), topicUrlService.buildTopicUrl("topic-for-error"));
            Assertions.assertEquals($.getMessage().getValue(), messageToSend);
            Assertions.assertNotNull($.getException());

            receivedError.set(true);
        });

        producerForError.send("topic-for-error", messageToSend);

        await().untilTrue(receivedError);

        disposable.dispose();
    }

    @Test
    void avroSerializationTestOk() throws Exception {
        AvroMsg testAvroMsg = new AvroMsg();
        testAvroMsg.setData("avro-test");
        producerForAvroTopic.send("topic-avro", testAvroMsg);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.avroTopicReceived.get());
    }

    @Test
    void protoSerializationTestOk() throws Exception {
        final ProtoMsg msg = ProtoMsg.newBuilder().setData(VALIDATION_STRING).build();
        producerForProtoTopic.send("topic-proto", msg);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.protoTopicReceived.get());
    }

    @Test
    void byteSerializationTestOk() throws Exception {
        byte[] data = VALIDATION_STRING.getBytes(StandardCharsets.UTF_8);

        producerForByteTopic.send("topic-byte", data);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.byteTopicReceived.get());
    }

    @Test
    void stringSerializationTestOk() throws Exception {
        producerForStringTopic.send("topic-string", VALIDATION_STRING);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.stringTopicReceived.get());
    }
}
