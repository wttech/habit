package io.wttech.habit.server.environment.diff.step;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class WaitingStep implements BuildStep {

  private final Duration duration;

  @Override
  public Mono<Void> perform() {
    return Mono.delay(duration).then();
  }

  @Override
  public int getOrder() {
    return Phase.POST_START;
  }

  @Override
  public String getMessage() {
    return "Waiting for " + duration.toString();
  }

}
