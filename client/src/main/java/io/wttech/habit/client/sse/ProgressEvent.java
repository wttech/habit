package io.wttech.habit.client.sse;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.Instant;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class ProgressEvent {

  private String environmentId;
  private String buildId;
  private Instant timestamp;
  private Integer totalSteps;
  private Integer completedSteps;
  private String message;
  private boolean error;

  ProgressEvent() {
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

  public Integer getTotalSteps() {
    return totalSteps;
  }

  public Integer getCompletedSteps() {
    return completedSteps;
  }

  public String getMessage() {
    return message;
  }

  public boolean isCompleted() {
    return totalSteps != null && totalSteps.equals(completedSteps);
  }

  public boolean isGeneral() {
    return totalSteps == null;
  }

  public boolean isError() {
    return error;
  }
}
