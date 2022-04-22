package io.wttech.habit.client.http;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class DeployResult {

  private boolean success;
  private String errorMessage;

  DeployResult() {
  }

  DeployResult(boolean success, String errorMessage) {
    this.success = success;
    this.errorMessage = errorMessage;
  }

  public static DeployResult success() {
     return new DeployResult(true, null);
  }

  public static DeployResult error(String message) {
     return new DeployResult(false, message);
  }

  public boolean isSuccess() {
    return success;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
