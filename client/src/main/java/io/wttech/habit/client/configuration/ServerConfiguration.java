package io.wttech.habit.client.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerConfiguration {

  private String name;
  private DeployConfiguration deploy;

  public String getName() {
    return name;
  }

  public DeployConfiguration getDeploy() {
    return deploy;
  }
}
