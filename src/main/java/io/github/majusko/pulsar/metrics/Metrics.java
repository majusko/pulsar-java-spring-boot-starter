package io.github.majusko.pulsar.metrics;


import com.google.common.collect.Sets;
import io.github.majusko.pulsar.properties.PulsarProperties;
import io.github.majusko.pulsar.utils.UrlBuildService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

import java.time.Duration;
import java.util.Set;

public class Metrics {
    private static MeterRegistry meterRegistry;
    private static PulsarProperties pulsarProperties;
    private static UrlBuildService urlBuildService;

    private Metrics() {
    }

    static void setMetrics(MeterRegistry meterRegistry, PulsarProperties pulsarProperties, UrlBuildService urlBuildService) {
        Metrics.meterRegistry = meterRegistry;
        Metrics.pulsarProperties = pulsarProperties;
        Metrics.urlBuildService = urlBuildService;
    }

    private static void increment(String name, String... tags) {
        meterRegistry.counter(name, buildCommonTags().and(tags)).increment();
    }

    private static void incrementCount(String name, double count, String... tags) {
        meterRegistry.counter(name, buildCommonTags().and(tags)).increment(count);
    }


    public static void producerOpened(String topic) {
        topic = urlBuildService.buildTopicUrl(topic);
        increment("pulsar_client_producers_opened", "topic", topic);
    }

    public static void producerPendingMessage(String topic) {
        increment("pulsar_client_producer_pending_messages", "topic", topic);
    }

    public static void producerPublishedMessage(String topic) {
        increment("pulsar_client_producer_published_messages", "topic", topic);
    }

    public static void producerBytesPublished(String topic, double count) {
        incrementCount("pulsar_client_producer_bytes_published", count, "topic", topic);
    }


    public static void consumersOpened(String topic) {
        topic = urlBuildService.buildTopicUrl(topic);
        increment("pulsar_client_consumers_opened", "topic", topic);
    }

    public static void acksCounter(String topic) {
        increment("pulsar_client_consumer_acks", "topic", topic);
    }

    public static void acksTimeoutCounter(String topic) {
        increment("pulsar_client_consumer_acks_timeout", "topic", topic);
    }

    public static void bytesReceived(String topic, double count) {
        incrementCount("pulsar_client_consumer_bytes_received", count, "topic", topic);
    }

    public static void consumerSuccess(String topic, Duration duration) {
        recordTimer("pulsar_client_consumer_status", duration, "status", "success", "topic", topic);
    }

    public static void consumerFail(String topic) {
        increment("pulsar_client_consumer_status", "status", "fail", "topic", topic);
    }

    public static void recordTimer(String name, Duration duration, String... tags) {
        meterRegistry.timer(name, buildCommonTags().and(tags)).record(duration);
    }

    private static Tags buildCommonTags() {
        Set<Tag> tagSet = Sets.newHashSet();
        tagSet.add(Tag.of("client", "Java"));
        tagSet.add(Tag.of("pulsar_namespace", Metrics.pulsarProperties.getNamespace()));
        tagSet.add(Tag.of("pulsar_tenant", Metrics.pulsarProperties.getTenant()));
        return Tags.of(tagSet);
    }
}
