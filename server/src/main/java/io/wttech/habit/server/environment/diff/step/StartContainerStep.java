package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.docker.container.ContainerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class StartContainerStep implements BuildStep {

  private final ContainerService containerService;
  private final String containerId;

  @Override
  public Mono<Void> perform() {
    return Mono.fromRunnable(() -> containerService.start(containerId));
  }

  @Override
  public int getOrder() {
    return Phase.START;
  }

  @Override
  public String getMessage() {
    return String.format("Starting container %s", containerId);
  }
}
