# Spring boot starter for [Apache Pulsar](https://pulsar.apache.org/)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.majusko/pulsar-java-spring-boot-starter/badge.svg)](https://search.maven.org/search?q=g:io.github.majusko)
[![Release](https://jitpack.io/v/majusko/pulsar-java-spring-boot-starter.svg)](https://jitpack.io/#majusko/pulsar-java-spring-boot-starter)
[![Build Status](https://travis-ci.com/majusko/pulsar-java-spring-boot-starter.svg?branch=master)](https://travis-ci.com/majusko/pulsar-java-spring-boot-starter)
[![Test Coverage](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master/graph/badge.svg)](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Join the chat at https://gitter.im/pulsar-java-spring-boot-starter/community](https://badges.gitter.im/pulsar-java-spring-boot-starter/community.svg)](https://gitter.im/pulsar-java-spring-boot-starter/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Quick Start

Simple start consist only from 3 simple steps.

#### 1. Add Maven dependency

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

## Example project

#### [Java Pulsar Example Project](https://github.com/majusko/java-pulsar-example)

## Documentation

### Configuration

Default configuration:
```properties

#PulsarClient
pulsar.service-url=pulsar://localhost:6650
pulsar.io-threads=10
pulsar.listener-threads=10
pulsar.enable-tcp-no-delay=false
pulsar.keep-alive-interval-sec=20
pulsar.connection-timeout-sec=10
pulsar.operation-timeout-sec=15
pulsar.starting-backoff-interval-ms=100
pulsar.max-backoff-interval-sec=10
pulsar.consumer-name-delimiter=

#Consumer
pulsar.consumer.default.dead-letter-policy-max-redeliver-count=-1
pulsar.consumer.default.ack-timeout-ms=30

```

Properties explained:

- `pulsar.service-url` - URL used to connect to pulsar cluster.
- `pulsar.io-threads` - Number of threads to be used for handling connections to brokers.
- `pulsar.listener-threads` - Set the number of threads to be used for message listeners/subscribers.
- `pulsar.enable-tcp-no-delay` -  Whether to use TCP no-delay flag on the connection, to disable Nagle algorithm.
- `pulsar.keep-alive-interval-sec` - Keep alive interval for each client-broker-connection.
- `pulsar.connection-timeout-sec` - duration of time to wait for a connection to a broker to be established. If the duration passes without a response from the broker, the connection attempt is dropped.
- `pulsar.operation-timeout-sec` - Operation timeout.
- `pulsar.starting-backoff-interval-ms` - Duration of time for a backoff interval (Retry algorithm).
- `pulsar.max-backoff-interval-sec` - The maximum duration of time for a backoff interval (Retry algorithm).
- `pulsar.consumer-name-delimiter` - Consumer names are connection of bean name and method with a delimiter. By default, there is no delimiter and words are connected together.
- `pulsar.consumer.default.dead-letter-policy-max-redeliver-count` - How many times should pulsar try to retry sending the message to consumer.
- `pulsar.consumer.default.ack-timeout-ms` - How soon should be the message acked and how soon will dead letter mechanism try to retry to send the message.

### Additional usages

In case you need to access pulsar metadata you simply use `PulsarMessage` as a wrapper and data will be injected for you.

```java
@Service
class MyConsumer {
    
    @PulsarConsumer(topic="my-topic", clazz=MyMsg.class)
    void consume(PulsarMessage<MyMsg> myMsg) { 
        producer.send(TOPIC, msg.getValue()); 
    }
}
```

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
