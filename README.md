# Spring boot starter for [Apache Pulsar](https://pulsar.apache.org/)

[![Release](https://jitpack.io/v/majusko/pulsar-java-spring-boot-starter.svg)](https://jitpack.io/#majusko/pulsar-java-spring-boot-starter)
[![Build Status](https://travis-ci.com/majusko/pulsar-java-spring-boot-starter.svg?branch=master)](https://travis-ci.com/majusko/pulsar-java-spring-boot-starter)
[![Test Coverage](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master/graph/badge.svg)](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Join the chat at https://gitter.im/pulsar-java-spring-boot-starter/community](https://badges.gitter.im/pulsar-java-spring-boot-starter/community.svg)](https://gitter.im/pulsar-java-spring-boot-starter/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Quick Start

Simple start consist only from 3 simple steps.

#### 1. Add Maven dependency

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
  <groupId>io.github.majusko</groupId>
  <artifactId>pulsar-java-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

#### 2. Configure Producer

Create your configuration class with all producers you would like to register.

```java
@Configuration
public class TestProducerConfiguration {

    @Bean
    public ProducerFactory producerFactory() {
        return new ProducerFactory()
            .addProducer("my-topic", MyMsg.class)
            .addProducer("other-topic", String.class);
    }
}
```

Use registered producers by simply injecting the `PulsarTemplate` into your service.

```java
@Service
class MyProducer {

	@Autowired
	private PulsarTemplate<MyMsg> producer;

	void send(MyMsg msg) {
		producer.send("my-topic", msg);
	}
}

```

#### 3. Configure Consumer

Annotate your service method with `@PulsarConsumer` annotation.

```java
@Service
class MyConsumer {
    
    @PulsarConsumer(topic="my-topic", clazz=MyMsg.class)
    void consume(MyMsg msg) { 
        producer.send(TOPIC, msg); 
    }
}
```

## Documentation

### Configuration

Default configuration:
```properties
pulsar.serviceUrl=pulsar://localhost:6650
pulsar.ioThreads=10
pulsar.listenerThreads=10
pulsar.isEnableTcpNoDelay=false
pulsar.keepAliveIntervalSec=20
pulsar.connectionTimeoutSec=10
pulsar.operationTimeoutSec=15
pulsar.startingBackoffIntervalMs=100
pulsar.maxBackoffIntervalSec=10
```

Properties explained:

- `pulsar.serviceUrl` - URL used to connect to pulsar cluster.
- `pulsar.ioThreads` - Number of threads to be used for handling connections to brokers.
- `pulsar.listenerThreads` - Set the number of threads to be used for message listeners/subscribers.
- `pulsar.isEnableTcpNoDelay` -  Whether to use TCP no-delay flag on the connection, to disable Nagle algorithm.
- `pulsar.keepAliveInterval` - Keep alive interval for each client-broker-connection.
- `pulsar.connectionTimeoutSec` - duration of time to wait for a connection to a broker to be established. If the duration passes without a response from the broker, the connection attempt is dropped.
- `pulsar.operationTimeoutSec` - Operation timeout.
- `pulsar.startingBackoffIntervalMs` - Duration of time for a backoff interval (Retry algorithm).
- `pulsar.maxBackoffIntervalSec` - The maximum duration of time for a backoff interval (Retry algorithm).

## Contributing

All contributors are welcome. If you never contributed to the open-source, start with reading the [Github Flow](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/github-flow).

### Roadmap task
1. Pick a task from simple roadmap in [Projects](https://github.com/majusko/pulsar-java-spring-boot-starter/projects) section.
2. Create a [pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) with reference (url) to the task inside the [Projects](https://github.com/majusko/pulsar-java-spring-boot-starter/projects) section.
3. Rest and enjoy the great feeling of being a contributor.

### Hotfix
1. Create an [issue](https://help.github.com/en/github/managing-your-work-on-github/about-issues)
2. Create a [pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) with reference to the issue
3. Rest and enjoy the great feeling of being a contributor.
