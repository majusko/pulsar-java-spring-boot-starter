package io.github.majusko.pulsar.producer;

import io.github.majusko.pulsar.collector.ProducerHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ProducerCollector implements BeanPostProcessor {

    private Map<String, ProducerHolder> producers = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        final Class<?> beanClass = bean.getClass();

        if(beanClass.isAnnotationPresent(PulsarProducer.class) && bean instanceof PulsarProducerFactory) {
            producers.putAll(((PulsarProducerFactory) bean).getTopics().entrySet().stream()
                .map($ -> new ProducerHolder($.getKey(), $.getValue()))
                .collect(Collectors.toMap(ProducerHolder::getTopic, $ -> $)));
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    public Map<String, ProducerHolder> getProducers() {
        return producers;
    }
}
