package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.docker.network.NetworkRepository;
import io.wttech.habit.server.docker.network.NetworkService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class DisconnectFromDefaultNetworkStep implements BuildStep {

  private final NetworkService networkService;
  private final NetworkRepository networkRepository;
  private final String containerId;

  @Override
  public Mono<Void> perform() {
    return Mono.fromRunnable(() -> networkService
        .disconnectFromNetwork(networkRepository.getDefaultBridge().id(), containerId));
  }

  @Override
  public int getOrder() {
    return Phase.NETWORK;
  }

  @Override
  public String getMessage() {
    return String.format("Disconnecting %s from the default bridge network", containerId);
  }
}
