package io.wttech.habit.server.environment.requestgraph.container;

import io.wttech.habit.server.docker.container.ContainerLabel;
import io.wttech.habit.server.docker.volume.VolumeService;
import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.messages.Volume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestLogService {

  private final VolumeService volumeService;

  public Volume createVolumeIfAbsent(String projectName) {
    String volumeName = projectName + ".request-persister.logs";
    Volume toCreate = Volume.builder()
        .driver("local")
        .name(volumeName)
        .labels(ImmutableMap.of(ContainerLabel.ENVIRONMENT, projectName))
        .build();
    return volumeService.createVolumeIfNotExists(toCreate);
  }

}
