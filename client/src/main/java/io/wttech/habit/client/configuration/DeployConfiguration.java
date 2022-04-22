package io.wttech.habit.client.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeployConfiguration {

  private List<DeployPath> paths = new ArrayList<>();

  public List<DeployPath> getPaths() {
    return paths;
  }

  public void setPaths(
      List<DeployPath> paths) {
    this.paths = paths;
  }

}
