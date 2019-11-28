package io.github.majusko.pulsar;

import io.github.majusko.pulsar.producer.PulsarTemplate;
import io.github.majusko.pulsar.test.MyMsg;
import io.github.majusko.pulsar.test.TestProducerConfiguration;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class PulsarJavaSpringBootStarterApplicationTests {

	@Autowired
	private PulsarTemplate<MyMsg> testProducerConfiguration;

	@Test
	void contextLoads() throws PulsarClientException {

		testProducerConfiguration.send("aa", new MyMsg("data"));

	}

}

