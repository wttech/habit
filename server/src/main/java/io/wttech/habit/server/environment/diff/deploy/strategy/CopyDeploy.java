package io.wttech.habit.server.environment.diff.deploy.strategy;

import io.wttech.habit.server.docker.container.ContainerService;
import io.wttech.habit.server.environment.configuration.request.DeployConfiguration;
import java.nio.file.Path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CopyDeploy implements DeployStrategy {

  private final ContainerService containerService;
  private final String containerName;
  private final String serverName;
  private final DeployConfiguration deploy;

  public static CopyDeployBuilder builder() {
    return new CopyDeployBuilder();
  }

  public void deploy(Path rootFolder) {
    log.info("Deploying to {}", containerName);
    for (int i = 0; i < deploy.getPaths().size(); i++) {
      String copyTarget = deploy.getPaths().get(i).getTarget();
      Path copySource = rootFolder.resolve(serverName).resolve(Integer.toString(i));
      containerService.copy(copySource.toAbsolutePath(), containerName, copyTarget);
    }
  }


  public static final class CopyDeployBuilder {

    private ContainerService containerService;
    private String containerName;
    private String serverName;
    private DeployConfiguration deploy;

    private CopyDeployBuilder() {
    }

    public CopyDeployBuilder withContainerService(ContainerService containerService) {
      this.containerService = containerService;
      return this;
    }

    public CopyDeployBuilder withContainerName(String containerName) {
      this.containerName = containerName;
      return this;
    }

    public CopyDeployBuilder withServerName(String serverName) {
      this.serverName = serverName;
      return this;
    }

    public CopyDeployBuilder withDeploy(DeployConfiguration deploy) {
      this.deploy = deploy;
      return this;
    }

    public CopyDeploy build() {
      return new CopyDeploy(containerService, containerName, serverName, deploy);
    }
  }
}
