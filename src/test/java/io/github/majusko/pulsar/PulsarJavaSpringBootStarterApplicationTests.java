package io.github.majusko.pulsar;

import io.github.majusko.pulsar.collector.ConsumerCollector;
import io.github.majusko.pulsar.collector.ConsumerHolder;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.consumer.ConsumerBuilder;
import io.github.majusko.pulsar.producer.ProducerFactory;
import io.github.majusko.pulsar.producer.PulsarTemplate;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.shade.org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestProducerConfiguration.class)
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

		Assert.assertEquals(1, consumers.size());

		final Consumer consumer = consumers.stream().findFirst().orElseThrow(Exception::new);

		Assert.assertNotNull(consumer);
		Assert.assertEquals("mock-topic", consumer.getTopic());
	}

	@Test
	void testConsumerRegistration2() throws Exception {
		final Class<TestConsumerConfiguration> clazz = TestConsumerConfiguration.class;
		final String descriptor = clazz.getName() + "#" + clazz.getDeclaredMethods()[0].getName();
		final ConsumerHolder consumerHolder = consumerCollector.getConsumer(descriptor).orElseThrow(Exception::new);

		Assert.assertNotNull(consumerHolder);
		Assert.assertEquals("mock-topic", consumerHolder.getAnnotation().topic());
		Assert.assertEquals(TestConsumerConfiguration.class, consumerHolder.getBean().getClass());
		Assert.assertEquals("mockTheListener", consumerHolder.getHandler().getName());
	}

	@Test
	void testProducerRegistration() {

		final Map<String, ImmutablePair<Class<?>, Serialization>> topics = producerFactory.getTopics();

		Assert.assertEquals(2, topics.size());

		final Set<String> topicNames = topics.entrySet().stream()
				.map(Map.Entry::getKey).collect(Collectors.toSet());

		Assert.assertTrue(topicNames.contains("topic-one"));
		Assert.assertTrue(topicNames.contains("topic-two"));
	}
}
