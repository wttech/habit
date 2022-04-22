package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.HabitException;
import io.wttech.habit.server.docker.container.ContainerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class ServerHealthcheckStep implements BuildStep {

  private final ContainerService containerService;
  private final String containerName;

  @Override
  public Mono<Void> perform() {
    return Mono.fromRunnable(() -> {
      if (!containerService.isRunning(containerName)) {
        String logs = containerService.getLogs(containerName);
        String message = "Proxy server " + containerName + " has stopped. Logs: \n" + logs;
        throw new HabitException(message);
      }
    });
  }

  @Override
  public int getOrder() {
    return Phase.POST_START;
  }

  @Override
  public String getMessage() {
    return "Proxy server healthcheck: " + containerName;
  }

}
