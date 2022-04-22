package io.wttech.habit.server.environment.builds.event;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.Instant;
import lombok.Getter;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
@Getter
public class BuildErrorEvent {

  private final String environmentId;
  private final String buildId;
  private final Instant timestamp;
  private final String message;

  public BuildErrorEvent(String environmentId, String buildId, Instant timestamp,
      String message) {
    this.environmentId = environmentId;
    this.buildId = buildId;
    this.timestamp = timestamp;
    this.message = message;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String environmentId;
    private String buildId;
    private Instant timestamp;
    private String message;

    private Builder() {
    }

    public Builder withEnvironmentId(String environmentId) {
      this.environmentId = environmentId;
      return this;
    }

    public Builder withBuildId(String buildId) {
      this.buildId = buildId;
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

    public BuildErrorEvent build() {
      return new BuildErrorEvent(environmentId, buildId, timestamp, message);
    }
  }
}
