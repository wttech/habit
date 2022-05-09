package io.wttech.habit.server.environment.builds.event;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.Instant;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class ProgressEvent implements BuildProcessEvent {

  private final String environmentId;
  private final String buildId;
  private final Instant timestamp;
  private final Long totalSteps;
  private final Long completedSteps;
  private final String message;
  private final boolean error;

  ProgressEvent(String environmentId, String buildId, Instant timestamp, Long totalSteps,
      Long completedSteps,
      String message, boolean error) {
    this.environmentId = environmentId;
    this.buildId = buildId;
    this.timestamp = timestamp;
    this.totalSteps = totalSteps;
    this.completedSteps = completedSteps;
    this.message = message;
    this.error = error;
  }

  public static Builder message() {
    return new Builder(false);
  }

  public static Builder error() {
    return new Builder(true);
  }

  public String getEnvironmentId() {
    return environmentId;
  }

  public String getBuildId() {
    return buildId;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Long getTotalSteps() {
    return totalSteps;
  }

  public Long getCompletedSteps() {
    return completedSteps;
  }

  public String getMessage() {
    return message;
  }

  public boolean isError() {
    return error;
  }

  public boolean isCompleted() {
    return totalSteps != null && totalSteps.equals(completedSteps);
  }

  public static final class Builder {

    private String environmentId;
    private String buildId;
    private Long totalSteps;
    private Long completedSteps;
    private String message;
    private boolean error;

    private Builder(boolean error) {
      this.error = error;
    }

    public Builder withEnvironmentId(String environmentId) {
      this.environmentId = environmentId;
      return this;
    }

    public Builder withBuildId(String buildId) {
      this.buildId = buildId;
      return this;
    }

    public Builder withTotalSteps(long totalSteps) {
      this.totalSteps = totalSteps;
      return this;
    }

    public Builder withCompletedSteps(long completedSteps) {
      this.completedSteps = completedSteps;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public ProgressEvent build() {
      return new ProgressEvent(environmentId, buildId, Instant.now(), totalSteps, completedSteps,
          message,
          error);
    }
  }
}
