package io.github.majusko.pulsar;

import com.google.common.base.Strings;
import io.github.majusko.pulsar.error.exception.ClientInitException;
import io.github.majusko.pulsar.properties.ConsumerProperties;
import io.github.majusko.pulsar.properties.PulsarProperties;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan
@EnableConfigurationProperties({PulsarProperties.class, ConsumerProperties.class})
public class PulsarAutoConfiguration {

    private final PulsarProperties pulsarProperties;

    public PulsarAutoConfiguration(PulsarProperties pulsarProperties) {
        this.pulsarProperties = pulsarProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public PulsarClient pulsarClient() throws PulsarClientException, ClientInitException {
        if (!Strings.isNullOrEmpty(pulsarProperties.getTlsAuthCertFilePath()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getTlsAuthKeyFilePath()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getTokenAuthValue())
        ) throw new ClientInitException("You cannot use multiple auth options.");

        final ClientBuilder pulsarClientBuilder = PulsarClient.builder()
            .serviceUrl(pulsarProperties.getServiceUrl())
            .ioThreads(pulsarProperties.getIoThreads())
            .listenerThreads(pulsarProperties.getListenerThreads())
            .enableTcpNoDelay(pulsarProperties.isEnableTcpNoDelay())
            .keepAliveInterval(pulsarProperties.getKeepAliveIntervalSec(), TimeUnit.SECONDS)
            .connectionTimeout(pulsarProperties.getConnectionTimeoutSec(), TimeUnit.SECONDS)
            .operationTimeout(pulsarProperties.getOperationTimeoutSec(), TimeUnit.SECONDS)
            .startingBackoffInterval(pulsarProperties.getStartingBackoffIntervalMs(), TimeUnit.MILLISECONDS)
            .maxBackoffInterval(pulsarProperties.getMaxBackoffIntervalSec(), TimeUnit.SECONDS)
            .useKeyStoreTls(pulsarProperties.isUseKeyStoreTls())
            .tlsTrustCertsFilePath(pulsarProperties.getTlsTrustCertsFilePath())
            .tlsCiphers(pulsarProperties.getTlsCiphers())
            .tlsProtocols(pulsarProperties.getTlsProtocols())
            .tlsTrustStorePassword(pulsarProperties.getTlsTrustStorePassword())
            .tlsTrustStorePath(pulsarProperties.getTlsTrustStorePath())
            .tlsTrustStoreType(pulsarProperties.getTlsTrustStoreType())
            .allowTlsInsecureConnection(pulsarProperties.isAllowTlsInsecureConnection())
            .enableTlsHostnameVerification(pulsarProperties.isEnableTlsHostnameVerification());

        if (!Strings.isNullOrEmpty(pulsarProperties.getTlsAuthCertFilePath()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getTlsAuthKeyFilePath())) {
            pulsarClientBuilder.authentication(AuthenticationFactory
                .TLS(pulsarProperties.getTlsAuthCertFilePath(), pulsarProperties.getTlsAuthKeyFilePath()));
        }

        if (!Strings.isNullOrEmpty(pulsarProperties.getTokenAuthValue())) {
            pulsarClientBuilder.authentication(AuthenticationFactory
                .token(pulsarProperties.getTokenAuthValue()));
        }

        return pulsarClientBuilder.build();
    }
}
