package io.github.majusko.pulsar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pulsar.consumer.default")
public class ConsumerProperties {
    int deadLetterPolicyMaxRedeliverCount = -1;
    int ackTimeoutMs = 0;
    int syncConsumerPollSpeedInMs = 100;
    boolean syncConsumerFailSilently = true;

    public int getDeadLetterPolicyMaxRedeliverCount() {
        return deadLetterPolicyMaxRedeliverCount;
    }

    public void setDeadLetterPolicyMaxRedeliverCount(int deadLetterPolicyMaxRedeliverCount) {
        this.deadLetterPolicyMaxRedeliverCount = deadLetterPolicyMaxRedeliverCount;
    }

    public int getAckTimeoutMs() {
        return ackTimeoutMs;
    }

    public void setAckTimeoutMs(int ackTimeoutMs) {
        this.ackTimeoutMs = ackTimeoutMs;
    }

    public int getSyncConsumerPollSpeedInMs() {
        return syncConsumerPollSpeedInMs;
    }

    public void setSyncConsumerPollSpeedInMs(int syncConsumerPollSpeedInMs) {
        this.syncConsumerPollSpeedInMs = syncConsumerPollSpeedInMs;
    }

    public boolean isSyncConsumerFailSilently() {
        return syncConsumerFailSilently;
    }

    public void setSyncConsumerFailSilently(boolean syncConsumerFailSilently) {
        this.syncConsumerFailSilently = syncConsumerFailSilently;
    }
}