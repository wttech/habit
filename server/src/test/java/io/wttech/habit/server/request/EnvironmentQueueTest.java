package io.wttech.habit.server.request;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class EnvironmentQueueTest {

  @Test
  void asdf() {
    EnvironmentQueue queue = new EnvironmentQueue();

    StepVerifier.create(queue.process("A", createInstant(1)))
        .expectNextCount(1)
        .verifyComplete();

//    StepVerifier.withVirtualTime(() -> merged)
//        .expectNext("5")
//        .expectNext("6")
//        .expectNext("7")
//        .expectNext("8")
//        .expectNext("1")
//        .expectNext("2")
//        .expectNext("3")
//        .expectNext("4")
//        .expectNextCount(8)
//        .verifyComplete();

  }

  private Mono<String> create(int i) {
    return Mono.fromSupplier(() -> i + "").delayElement(Duration.ofMillis(100));
  }

  private Mono<String> createInstant(int i) {
    return Mono.fromSupplier(() -> i + "");
  }

}
