package io.github.majusko.pulsar.reactor;

import reactor.core.publisher.Flux;

public interface FluxConsumer<T> {
    Flux<T> asFlux();
}
