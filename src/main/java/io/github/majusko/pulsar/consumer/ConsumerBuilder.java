package io.github.majusko.pulsar.consumer;

import io.github.majusko.pulsar.collector.ConsumerCollector;
import io.github.majusko.pulsar.collector.ConsumerHolder;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConsumerBuilder {

    private final ConsumerCollector consumerCollector;
    private final PulsarClient pulsarClient;

    private List<Consumer> consumers;

    public ConsumerBuilder(ConsumerCollector consumerCollector, PulsarClient pulsarClient) {
        this.consumerCollector = consumerCollector;
        this.pulsarClient = pulsarClient;
    }

    @PostConstruct
    private void init() {
        consumers = consumerCollector.getConsumers().entrySet().stream()
            .map(holder -> subscribe(holder.getKey(), holder.getValue()))
            .collect(Collectors.toList());
    }

    private Consumer<?> subscribe(String name, ConsumerHolder holder) {
        try {
            return pulsarClient
                .newConsumer(Schema.JSON(holder.getAnnotation().clazz()))
                .consumerName(name)
                .subscriptionName(name)
                .topic(holder.getAnnotation().topic())
                .messageListener((consumer, msg) -> {
                    try {
                        //TODO arg validation?
                        final Method method = holder.getHandler();

                        method.setAccessible(true);
                        method.invoke(holder.getBean(), msg);

                        consumer.acknowledge(msg);
                    } catch(Exception e) {
                        consumer.negativeAcknowledge(msg);
                        throw new RuntimeException("TODO Custom Exception!", e);
                    }
                }).subscribe();
        } catch(PulsarClientException e) {
            throw new RuntimeException("TODO Custom Exception!", e);
        }
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }
}
