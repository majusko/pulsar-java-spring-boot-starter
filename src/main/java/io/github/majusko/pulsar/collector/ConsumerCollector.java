package io.github.majusko.pulsar.collector;

import com.beust.jcommander.internal.Sets;
import io.github.majusko.pulsar.annotation.PulsarConsumer;
import io.github.majusko.pulsar.utils.UrlBuildService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.github.majusko.pulsar.utils.SchemaUtils.getParameterType;

@Configuration
public class ConsumerCollector implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerCollector.class);

    private final UrlBuildService urlBuildService;

    private final Map<String, ConsumerHolder> consumers = new ConcurrentHashMap<>();
    private final Set<Class<?>> nonAnnotatedClasses = Sets.newHashSet();

    public ConsumerCollector(UrlBuildService urlBuildService) {
        this.urlBuildService = urlBuildService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        final Class<?> beanClass = bean.getClass();

        if (this.nonAnnotatedClasses.contains(beanClass)) {
            return bean;
        }

        consumers.putAll(Arrays.stream(beanClass.getDeclaredMethods())
            .filter($ -> {
                if (!$.isAnnotationPresent(PulsarConsumer.class)) {
                    this.nonAnnotatedClasses.add(beanClass);
                    logger.trace( "No @PulsarConsumer annotations found on bean type: " + bean.getClass());
                    return false;
                }
                return true;
            })
            .collect(Collectors.toMap(
                method -> urlBuildService.buildConsumerName(beanClass, method),
                method -> new ConsumerHolder(method.getAnnotation(PulsarConsumer.class), method, bean,
                    getParameterType(method)))));

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    public Map<String, ConsumerHolder> getConsumers() {
        return consumers;
    }

    public Optional<ConsumerHolder> getConsumer(String methodDescriptor) {
        return Optional.ofNullable(consumers.get(methodDescriptor));
    }
}
