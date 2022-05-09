package io.wttech.habit.client.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildDTO {

  private String environmentId;
  private String buildId;

  BuildDTO() {
  }

  public String getEnvironmentId() {
    return environmentId;
  }

  public String getBuildId() {
    return buildId;
  }

}
