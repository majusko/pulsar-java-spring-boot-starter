package io.github.majusko.pulsar.utils;

import io.github.majusko.pulsar.properties.PulsarProperties;
import org.springframework.stereotype.Service;

@Service
public class TopicUrlService {

    private static final String PERSISTENT_PREFIX = "persistent";
    private static final String NON_PERSISTENT_PREFIX = "non-persistent";
    private static final String DEFAULT_PERSISTENCE = PERSISTENT_PREFIX;

    private final PulsarProperties pulsarProperties;

    private TopicUrlService(PulsarProperties pulsarProperties) {
        this.pulsarProperties = pulsarProperties;
    }

    public String buildTopicUrl(String topic) {
        return DEFAULT_PERSISTENCE + "://" + pulsarProperties.getTenant() + "/" + pulsarProperties.getNamespace() +
            "/" + topic;
    }
}
