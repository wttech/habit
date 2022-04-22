package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.environment.diff.deploy.ProxyServerDeployment;
import java.nio.file.Path;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class DeployStep implements BuildStep {

  private final String serverName;
  private final ProxyServerDeployment strategy;
  private final Path path;

  @Override
  public Mono<Void> perform() {
    return Mono.fromRunnable(() -> strategy.deploy(path));
  }

  @Override
  public int getOrder() {
    return Phase.DEPLOY;
  }

  @Override
  public String getMessage() {
    return "Deploying configuration to " + serverName;
  }
}
