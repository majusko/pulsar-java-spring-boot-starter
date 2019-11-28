package io.github.majusko.pulsar;

import io.github.majusko.pulsar.producer.ProducerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestProducerConfiguration {

    @Bean
    public ProducerFactory producerFactory() {
        return new ProducerFactory().addProducer("aa", MyMsg.class);
    }
}
