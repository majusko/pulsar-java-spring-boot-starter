package io.github.majusko.pulsar.metrics;


import io.github.majusko.pulsar.properties.PulsarProperties;
import io.github.majusko.pulsar.utils.UrlBuildService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class MetricsConfig {
    @Resource
    private MeterRegistry meterRegistry;

    @Resource
    private PulsarProperties pulsarProperties;

    @Resource
    private UrlBuildService urlBuildService;

    @PostConstruct
    private void init() {
        Metrics.setMetrics(meterRegistry, pulsarProperties, urlBuildService);
    }
}
