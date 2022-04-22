package io.wttech.habit.server.environment.event;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BasicEnvironmentEvent implements EnvironmentEvent {

  private final String id;
  private final String environmentId;
  private final Instant timestamp;
  private final String message;
  private final String type;

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String id = UUID.randomUUID().toString();
    private String environmentId;
    private Instant timestamp;
    private String message;
    private String type;

    private Builder() {
    }

    public Builder withEnvironmentId(String environmentId) {
      this.environmentId = environmentId;
      return this;
    }

    public Builder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public BasicEnvironmentEvent build() {
      return new BasicEnvironmentEvent(id, environmentId, timestamp, message, type);
    }
  }
}
