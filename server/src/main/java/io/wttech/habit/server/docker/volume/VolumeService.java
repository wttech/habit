package io.wttech.habit.server.docker.volume;

import io.wttech.habit.server.docker.DockerClientException;
import io.wttech.habit.server.docker.container.ContainerLabel;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListVolumesParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.VolumeNotFoundException;
import com.spotify.docker.client.messages.Volume;
import com.spotify.docker.client.messages.VolumeList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VolumeService {

  private final DockerClient dockerClient;

  public Volume createVolumeIfNotExists(Volume toCreate) {
    try {
      Volume existingVolume = dockerClient.inspectVolume(toCreate.name());
      log.info("Volume {} already exists", toCreate.name());
      return existingVolume;
    } catch (VolumeNotFoundException e) {
      // pass control to outside try/catch
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
    try {
      log.info("Creating volume {}", toCreate.name());
      return dockerClient.createVolume(toCreate);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void deleteVolumesByEnvironment(String environmentId) {
    try {
      VolumeList volumes = dockerClient.listVolumes(
          ListVolumesParam.filter("label", ContainerLabel.ENVIRONMENT + "=" + environmentId));
      for (Volume volume : volumes.volumes()) {
        dockerClient.removeVolume(volume);
      }
    } catch (DockerException e) {
      throw new DockerClientException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}
