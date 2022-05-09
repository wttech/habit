package io.wttech.habit.server.environment.configuration.request;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class DeployConfiguration {

  @JsonMerge
  @JsonSetter(nulls = Nulls.SKIP)
  private List<DeployPath> paths;
  private String reloadCommand;
  private String postDeployCommand;

  public List<DeployPath> getPaths() {
    return paths;
  }

  public void setPaths(
      List<DeployPath> paths) {
    this.paths = paths;
  }

  public String getReloadCommand() {
    return reloadCommand;
  }

  public void setReloadCommand(String reloadCommand) {
    this.reloadCommand = reloadCommand;
  }

  public String getPostDeployCommand() {
    return postDeployCommand;
  }

  public void setPostDeployCommand(String postDeployCommand) {
    this.postDeployCommand = postDeployCommand;
  }

  public boolean isPostDeployCommandDefined() {
    return postDeployCommand != null && !postDeployCommand.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeployConfiguration that = (DeployConfiguration) o;
    return
        Objects.equals(getPaths(), that.getPaths());
  }

  @Override
  public int hashCode() {
    return Objects.hash(paths, reloadCommand, postDeployCommand);
  }

}
