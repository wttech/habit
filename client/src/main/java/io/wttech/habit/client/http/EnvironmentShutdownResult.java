package io.wttech.habit.client.http;

public class EnvironmentShutdownResult {

  private final int statusCode;
  private final String error;

  EnvironmentShutdownResult(int statusCode, String error) {
    this.statusCode = statusCode;
    this.error = error;
  }

  public static EnvironmentShutdownResult success() {
    return new EnvironmentShutdownResult(200, null);
  }

  public static EnvironmentShutdownResult error(int statusCode, String error) {
    return new EnvironmentShutdownResult(statusCode, error);
  }

  public boolean isSuccess() {
    return statusCode == 200;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getError() {
    return error;
  }

}
