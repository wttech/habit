package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.environment.LogTailingService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class LogTailingStep implements BuildStep {

  private final LogTailingService logTailingService;
  private final String environmentId;
  private final String serverName;
  private final String containerId;
  private final List<String> logFiles;

  @Override
  public Mono<Void> perform() {
    logTailingService.cleanTailing(environmentId);
    logFiles.forEach(logFile -> {
      logTailingService.startAsyncLogging(environmentId, containerId, logFile);
    });
    return Mono.empty();
  }

  @Override
  public int getOrder() {
    return Phase.POST_START;
  }

  @Override
  public String getMessage() {
    return "Tailing log files for server: " + serverName;
  }
}
