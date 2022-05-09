package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.docker.network.NetworkService;
import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class ConnectToNetworkStep implements BuildStep {

  private final NetworkService networkService;
  private final String networkId;
  private final String containerId;
  private final List<String> domainNames;

  @Override
  public Mono<Void> perform() {
    return Mono
        .fromRunnable(() -> networkService.connectToNetwork(networkId, containerId, domainNames));
  }

  @Override
  public int getOrder() {
    return Phase.NETWORK;
  }

  @Override
  public String getMessage() {
    return String.format("Connecting %s to network %s", containerId, networkId);
  }
}
