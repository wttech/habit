package io.wttech.habit.server.environment.event;

import io.wttech.habit.server.environment.Environment;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EnvironmentUpdateEvent {

  private final String id;
  private final Instant timestamp;
  private final Environment environment;
  private final String type;

  public String getEnvironmentId() {
    return environment.getId();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String id = UUID.randomUUID().toString();
    private Instant timestamp;
    private Environment environment;
    private String type;

    private Builder() {
    }

    public Builder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withEnvironment(Environment environment) {
      this.environment = environment;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public EnvironmentUpdateEvent build() {
      return new EnvironmentUpdateEvent(id, timestamp, environment, type);
    }
  }
}
