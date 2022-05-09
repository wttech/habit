package io.wttech.habit.server.environment.configuration.request;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class DeployPath {

  @JsonSetter(nulls = Nulls.SKIP)
  private String source;
  @JsonSetter(nulls = Nulls.SKIP)
  private String target;

  DeployPath() {
  }

  public DeployPath(String source, String target) {
    this.source = source;
    this.target = target;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeployPath that = (DeployPath) o;
    return Objects.equals(getSource(), that.getSource()) &&
        Objects.equals(getTarget(), that.getTarget());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSource(), getTarget());
  }

}
