package io.github.majusko.pulsar;

import io.github.majusko.pulsar.mock.MockPulsarClient;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarMockAutoConfiguration {

    @Bean
    public PulsarClient pulsarClient() {
        return new MockPulsarClient();
    }
}
