package io.wttech.habit.server.docker.image;

import io.wttech.habit.server.docker.DockerClientException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListImagesParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageService {

  private final DockerClient dockerClient;

  public void pull(String imageName) {
    try {
      dockerClient.pull(imageName);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void pullReluctantly(String imageName) {
    try {
      List<Image> images = dockerClient.listImages(ListImagesParam.filter("reference", imageName));
      if (images.isEmpty()) {
        log.info("Pulling image {}", imageName);
        dockerClient.pull(imageName);
      } else {
        log.info("Image already pulled {}", imageName);
      }
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

}
