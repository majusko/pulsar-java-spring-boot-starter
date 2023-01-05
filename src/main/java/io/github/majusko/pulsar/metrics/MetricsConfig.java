package io.github.majusko.pulsar.metrics;


import io.github.majusko.pulsar.properties.PulsarProperties;
import io.github.majusko.pulsar.utils.UrlBuildService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    public MetricsConfig(MeterRegistry meterRegistry, PulsarProperties pulsarProperties, UrlBuildService urlBuildService) {
        Metrics.setMetrics(meterRegistry, pulsarProperties, urlBuildService);
    }
}
