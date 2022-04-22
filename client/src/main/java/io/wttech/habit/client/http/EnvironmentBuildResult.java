package io.wttech.habit.client.http;

public class EnvironmentBuildResult {

  private final int statusCode;
  private final String error;
  private final BuildDTO buildDto;

  EnvironmentBuildResult(int statusCode, String error, BuildDTO buildDto) {
    this.statusCode = statusCode;
    this.error = error;
    this.buildDto = buildDto;
  }

  public static EnvironmentBuildResult success(BuildDTO buildDto) {
    return new Builder().withStatusCode(201).withBuildDto(buildDto).build();
  }

  public static EnvironmentBuildResult error(int code, String error) {
    return new Builder().withStatusCode(code).withError(error).build();
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getError() {
    return error;
  }

  public BuildDTO getBuildDto() {
    return buildDto;
  }

  public boolean isSuccess() {
    return statusCode == 201;
  }

  public static final class Builder {

    private int statusCode;
    private String error;
    private BuildDTO buildDto;

    private Builder() {
    }

    public Builder withStatusCode(int statusCode) {
      this.statusCode = statusCode;
      return this;
    }

    public Builder withError(String error) {
      this.error = error;
      return this;
    }

    public Builder withBuildDto(BuildDTO buildDto) {
      this.buildDto = buildDto;
      return this;
    }

    public EnvironmentBuildResult build() {
      return new EnvironmentBuildResult(statusCode, error, buildDto);
    }
  }
}
