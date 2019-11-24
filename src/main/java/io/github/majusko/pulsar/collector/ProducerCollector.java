package io.github.majusko.pulsar.collector;

import io.github.majusko.pulsar.annotation.PulsarProducer;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ProducerCollector implements BeanPostProcessor {

    private Map<String, ProducerHolder> producers = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        final Class<?> beanClass = bean.getClass();

        final Map<String, ProducerHolder> registeredProducers = Arrays.stream(beanClass.getDeclaredMethods())
            .filter($ -> $.isAnnotationPresent(PulsarProducer.class))
            .map($ -> new ProducerHolder($.getAnnotation(PulsarProducer.class), $, bean))
            .collect(Collectors.toMap(a -> a.getAnnotation().topic(), a -> a));

        registeredProducers.keySet().forEach($ -> {
            if(producers.containsKey($)) throw new RuntimeException("TODO -> topic already have producer.");
        });

        producers.putAll(registeredProducers);

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    public Map<String, ProducerHolder> getProducers() {
        return producers;
    }

    public Optional<ProducerHolder> getProducer(String topic) {
        return Optional.ofNullable(producers.get(topic));
    }
}
