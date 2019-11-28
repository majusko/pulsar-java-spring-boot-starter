package io.github.majusko.pulsar.producer;

import io.github.majusko.pulsar.collector.ProducerHolder;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ProducerCollector implements BeanPostProcessor {

    private final PulsarClient pulsarClient;

    private Map<String, Producer> producers = new ConcurrentHashMap<>();

    public ProducerCollector(PulsarClient pulsarClient) {
        this.pulsarClient = pulsarClient;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        final Class<?> beanClass = bean.getClass();

        if(beanClass.isAnnotationPresent(PulsarProducer.class) && bean instanceof PulsarProducerFactory) {
            producers.putAll(((PulsarProducerFactory) bean).getTopics().entrySet().stream()
                .map($ -> new ProducerHolder($.getKey(), $.getValue()))
                .collect(Collectors.toMap(ProducerHolder::getTopic, this::buildProducer)));
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    private Producer<?> buildProducer(ProducerHolder holder) {
        try {
            return pulsarClient.newProducer(Schema.JSON(holder.getClazz()))
                .topic(holder.getTopic())
                .create();
        } catch(PulsarClientException e) {
            throw new RuntimeException("TODO Custom Exception!", e);
        }
    }

    public Map<String, Producer> getProducers() {
        return producers;
    }
}
