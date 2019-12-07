# Spring boot starter for [Apache Pulsar](https://pulsar.apache.org/)

[![Release](https://jitpack.io/v/majusko/pulsar-java-spring-boot-starter.svg)](https://jitpack.io/#majusko/pulsar-java-spring-boot-starter)
[![Build Status](https://travis-ci.com/majusko/pulsar-java-spring-boot-starter.svg?branch=master)](https://travis-ci.com/majusko/pulsar-java-spring-boot-starter)
[![Test Coverage](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master/graph/badge.svg)](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Join the chat at https://gitter.im/pulsar-java-spring-boot-starter/community](https://badges.gitter.im/pulsar-java-spring-boot-starter/community.svg)](https://gitter.im/pulsar-java-spring-boot-starter/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

> Library is still not ready for production - expected in January 2020

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

Just inject `PulsarTemplate` into your service

```java
@Service
class MyProducer {

	private final static String TOPIC = "my-topic";

	@Autowired
	private PulsarTemplate<MyMsg> producer;

	void send(MyMsg msg) {
		producer.send(TOPIC, msg);
	}
}

```

#### 3. Configure Consumer

Just annotate your service with `@PulsarConsumer` annotation.

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

TODO

## Contributing

All contributors are welcome. If you never contributed to the open-source, start with reading the [Github Flow](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/github-flow).

1. Create an [issue](https://help.github.com/en/github/managing-your-work-on-github/about-issues)
2. Create a [pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) with reference to the issue
3. Rest and enjoy the great feeling of being a contributor.
