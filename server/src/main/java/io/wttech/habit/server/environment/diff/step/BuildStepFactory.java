package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.docker.container.ContainerService;
import io.wttech.habit.server.docker.image.ImageService;
import io.wttech.habit.server.docker.network.NetworkRepository;
import io.wttech.habit.server.docker.network.NetworkService;
import io.wttech.habit.server.environment.EnvironmentService;
import io.wttech.habit.server.environment.LogTailingService;
import io.wttech.habit.server.environment.diff.deploy.ProxyServerDeployment;
import com.spotify.docker.client.messages.ContainerConfig;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildStepFactory {

  private final ContainerService containerService;
  private final NetworkService networkService;
  private final NetworkRepository networkRepository;
  private final ImageService imageService;
  private final EnvironmentService environmentService;
  private final LogTailingService logTailingService;

  public CommandStep command(String containerName, String command) {
    return new CommandStep(containerService, containerName, command);
  }

  public DeployStep deploy(String serverName,
                           ProxyServerDeployment strategy, Path path) {
    return new DeployStep(serverName, strategy, path);
  }

  public ConnectToNetworkStep connect(String containerId, String networkId,
      List<String> domainNames) {
    return new ConnectToNetworkStep(networkService, networkId, containerId, domainNames);
  }

  public DisconnectFromDefaultNetworkStep disconnectFromBridge(String containerId) {
    return new DisconnectFromDefaultNetworkStep(networkService, networkRepository, containerId);
  }

  public DestroyContainerStep destroy(String containerId) {
    return new DestroyContainerStep(containerService, containerId);
  }

  public StartContainerStep start(String containerId) {
    return new StartContainerStep(containerService, containerId);
  }

  public CreateContainerStep create(ContainerConfig containerConfig, String containerName) {
    return new CreateContainerStep(imageService, containerService, containerConfig, containerName);
  }

  public HealthcheckStep healthcheck(String environmentId) {
    return new HealthcheckStep(environmentService, environmentId);
  }

  public LogTailingStep tail(String environmentId, String serverName, String containerId, List<String> logFiles) {
    return new LogTailingStep(logTailingService, environmentId, serverName, containerId, logFiles);
  }

  public ServerHealthcheckStep proxyServerHealthcheck(String containerName) {
    return new ServerHealthcheckStep(containerService, containerName);
  }

  public WaitingStep waiting(Duration duration) {
    return new WaitingStep(duration);
  }
}
