package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.environment.EnvironmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class HealthcheckStep implements BuildStep {

  private final EnvironmentService environmentService;
  private final String environmentId;

  @Override
  public Mono<Void> perform() {
    return environmentService.healthcheck(environmentId);
  }

  @Override
  public int getOrder() {
    return Phase.POST_START;
  }

  @Override
  public String getMessage() {
    return "Checking environment connection";
  }
}
