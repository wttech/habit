package io.wttech.habit.client.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentConfigurationModel {

  private String id;
  private List<ServerConfiguration> servers = new ArrayList<>();

  public String getId() {
    return id;
  }

  public List<ServerConfiguration> getServers() {
    return servers;
  }

}
