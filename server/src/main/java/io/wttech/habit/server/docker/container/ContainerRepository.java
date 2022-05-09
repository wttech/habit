package io.wttech.habit.server.docker.container;

import io.wttech.habit.server.docker.DockerClientException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContainerRepository {

  private final DockerClient dockerClient;

  public List<Container> getAllContainers(String projectKey) {
    try {
      return dockerClient.listContainers(ListContainersParam.allContainers(),
          ListContainersParam.withLabel(ContainerLabel.ENVIRONMENT, projectKey));
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException("Could not get docker containers list", e);
    }
  }

  public List<Container> findByProjectName(String projectName) {
    try {
      return dockerClient.listContainers(
          ListContainersParam.allContainers(),
          ListContainersParam.withLabel(ContainerLabel.ENVIRONMENT, projectName));
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

}
