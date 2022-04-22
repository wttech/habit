package io.wttech.habit.server.environment.builds;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeployResult {

  private final boolean success;
  private final String errorMessage;

  public static DeployResult success() {
    return new DeployResult(true, null);
  }

  public static DeployResult error(String message) {
    return new DeployResult(false, message);
  }

}
