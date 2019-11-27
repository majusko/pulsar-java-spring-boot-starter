package io.github.majusko.pulsar;

import io.github.majusko.pulsar.test.TestProducerConfiguration;
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
	private TestProducerConfiguration testProducerConfiguration;

	@Test
	void contextLoads() {



	}

}

