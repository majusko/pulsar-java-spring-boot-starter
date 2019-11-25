package io.github.majusko.pulsar;

import io.github.majusko.pulsar.annotation.PulsarProducerScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@PulsarProducerScan("io.github.majusko.pulsar")
public class PulsarJavaSpringBootStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PulsarJavaSpringBootStarterApplication.class, args);
	}

}
