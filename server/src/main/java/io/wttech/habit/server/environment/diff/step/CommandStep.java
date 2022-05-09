package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.HabitException;
import io.wttech.habit.server.docker.container.CommandResult;
import io.wttech.habit.server.docker.container.ContainerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class CommandStep implements BuildStep {

  private final ContainerService containerService;
  private final String containerName;
  private final String command;

  @Override
  public Mono<Void> perform() {
    return Mono.fromRunnable(() -> {
      CommandResult exec = containerService.exec(containerName, "sh", "-c", command);
      if (!exec.isSuccess()) {
        String message = "Command unsuccessful. Command logs: \n" + exec.getOutput();
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
    return "Executing command [" + String.join(", ", command) + "]";
  }

}
