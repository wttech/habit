package io.wttech.habit.server.environment.diff.step;

import io.wttech.habit.server.docker.container.ContainerService;
import io.wttech.habit.server.docker.image.ImageService;
import com.spotify.docker.client.messages.ContainerConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class CreateContainerStep implements BuildStep {

  private final ImageService imageService;
  private final ContainerService containerService;
  private final ContainerConfig containerConfig;
  private final String containerName;

  @Override
  public Mono<Void> perform() {
    return Mono.fromRunnable(this::process);
  }

  private void process() {
    imageService.pullReluctantly(containerConfig.image());
    containerService.createContainer(containerName, containerConfig);
  }

  @Override
  public int getOrder() {
    return Phase.CREATE;
  }

  @Override
  public String getMessage() {
    return String
        .format("Creating container %s from image %s", containerName, containerConfig.image());
  }
}
