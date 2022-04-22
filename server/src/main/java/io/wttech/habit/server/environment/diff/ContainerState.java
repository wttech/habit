package io.wttech.habit.server.environment.diff;

import io.wttech.habit.server.docker.container.ContainerLabel;
import com.spotify.docker.client.messages.Container;
import java.time.Instant;
import java.util.Optional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ContainerState {

  private final Container container;

  public String getId() {
    return container.id();
  }

  public String getName() {
    return container.names().get(0).replace("/", "");
  }

  public String getHash() {
    return container.labels().get(ContainerLabel.HASH);
  }

  public boolean isRunning() {
    return container.status().contains("Up");
  }

  public Instant getCreationDate() {
    return Instant.ofEpochMilli(container.created());
  }

  public Optional<String> getLabel(String label) {
    return Optional.ofNullable(container.labels().get(label));
  }

}
