package io.wttech.habit.server.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import org.springframework.stereotype.Component;

@Component
public class SpotifyDockerClientProvider {

  public DockerClient get() {
    try {
      return DefaultDockerClient.fromEnv().build();
    } catch (DockerCertificateException e) {
      throw new DockerClientException(e);
    }
  }

}
