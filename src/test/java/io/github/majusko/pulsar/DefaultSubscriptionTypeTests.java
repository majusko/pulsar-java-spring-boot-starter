package io.github.majusko.pulsar;

import io.github.majusko.pulsar.consumer.ConsumerAggregator;
import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar.producer.PulsarTemplate;
import io.github.majusko.pulsar.utils.UrlBuildService;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.client.impl.ConsumerBase;
import org.apache.pulsar.client.impl.conf.ConsumerConfigurationData;
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
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.awaitility.Awaitility.await;

@ActiveProfiles("default-sub-type")
@SpringBootTest
@Import({TestProducerConfiguration.class, TestConsumers.class})
@Testcontainers
class DefaultSubscriptionTypeTests {

    @Autowired
    private ConsumerAggregator consumerAggregator;

    @Autowired
    private PulsarTemplate<MyMsg> producer;

    @Autowired
    private TestConsumers testConsumers;

    @Autowired
    private UrlBuildService urlBuildService;

    @Container
    static PulsarContainer pulsarContainer = new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:latest"));

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
    void sharedSubscriptionOverride() throws Exception {
        final ConsumerBase<?> consumer = (ConsumerBase<?>) consumerAggregator.getConsumers().stream()
            .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.SHARED_SUB_TEST)))
            .findFirst()
            .orElseThrow(() -> new Exception("Missing tested consumer."));

        final Field f = ConsumerBase.class.getDeclaredField("conf");

        f.setAccessible(true);

        final ConsumerConfigurationData<?> conf = (ConsumerConfigurationData<?>) f.get(consumer);

        Assertions.assertEquals(urlBuildService.buildTopicUrl(TestConsumers.SHARED_SUB_TEST), consumer.getTopic());
        Assertions.assertEquals(SubscriptionType.Shared, conf.getSubscriptionType());

        producer.send(TestConsumers.SHARED_SUB_TEST, new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.subscribeToSharedTopicSubscription.get());
    }

    @Test
    void exclusiveSubscriptionOverride() throws Exception {
        final ConsumerBase<?> consumer = (ConsumerBase<?>) consumerAggregator.getConsumers().stream()
            .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.EXCLUSIVE_SUB_TEST)))
            .findFirst()
            .orElseThrow(() -> new Exception("Missing tested consumer."));

        final Field f = ConsumerBase.class.getDeclaredField("conf");

        f.setAccessible(true);

        final ConsumerConfigurationData<?> conf = (ConsumerConfigurationData<?>) f.get(consumer);

        Assertions.assertEquals(urlBuildService.buildTopicUrl(TestConsumers.EXCLUSIVE_SUB_TEST), consumer.getTopic());
        Assertions.assertEquals(SubscriptionType.Exclusive, conf.getSubscriptionType());

        producer.send(TestConsumers.EXCLUSIVE_SUB_TEST, new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.subscribeToSharedTopicSubscription.get());
    }
}
