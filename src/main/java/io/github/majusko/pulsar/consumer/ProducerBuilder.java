package io.github.majusko.pulsar.consumer;

import io.github.majusko.pulsar.collector.ProducerCollector;
import io.github.majusko.pulsar.collector.ProducerHolder;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProducerBuilder {

    private final ProducerCollector consumerCollector;
    private final PulsarClient pulsarClient;

    private Map<String, Producer> producers;

    public ProducerBuilder(ProducerCollector consumerCollector, PulsarClient pulsarClient) {
        this.consumerCollector = consumerCollector;
        this.pulsarClient = pulsarClient;
    }

    @PostConstruct
    private void init() {
        producers = consumerCollector.getProducers().values().stream()
            .map(this::buildProducer)
            .collect(Collectors.toMap(Producer::getTopic, $ -> $));
    }

    private Producer<?> buildProducer(ProducerHolder holder) {
        try {
            return pulsarClient.newProducer(Schema.JSON(holder.getAnnotation().clazz()))
                .topic(holder.getAnnotation().topic())
                .create();
        } catch(PulsarClientException e) {
            throw new RuntimeException("TODO Custom Exception!", e);
        }
    }

    public Map<String, Producer> getProducers() {
        return producers;
    }
}
