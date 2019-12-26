package io.github.majusko.pulsar;

import io.github.majusko.pulsar.mock.MockPulsarClient;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@ConditionalOnProperty(value = "pulsar.mock")
public class PulsarMockAutoConfiguration {

    @Bean
    public PulsarClient pulsarClient() {
        return new MockPulsarClient();
    }
}
