package io.wttech.habit.server.environment;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import io.wttech.habit.server.environment.configuration.RootConfiguration;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.Instant;

import org.dizitart.no2.objects.Id;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class Environment {

  @Id
  private String id;
  private RootConfiguration configuration;
  private String buildHash;
  private Instant created;
  private Instant lastModified;
  private State state;

  public static Builder builder() {
    return new Builder();
  }

  public String getId() {
    return id;
  }

  public RootConfiguration getConfiguration() {
    return configuration;
  }

  public boolean isConfigurationPresent() {
    return configuration != null;
  }

  public String getBuildHash() {
    return buildHash;
  }

  public Instant getCreated() {
    return created;
  }

  public Instant getLastModified() {
    return lastModified;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void setLastModified(Instant lastModified) {
    this.lastModified = lastModified;
  }

  public void setConfiguration(RootConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setBuildHash(String buildHash) {
    this.buildHash = buildHash;
  }

  public static final class Builder {

    private String id;
    private RootConfiguration configuration;
    private String buildHash;
    private Instant created;
    private Instant lastModified;
    private State state;

    private Builder() {
    }

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    public Builder withConfiguration(RootConfiguration configuration) {
      this.configuration = configuration;
      return this;
    }

    public Builder withCreated(Instant created) {
      this.created = created;
      return this;
    }

    public Builder withBuildHash(String buildHash) {
      this.buildHash = buildHash;
      return this;
    }

    public Builder withLastModified(Instant lastModified) {
      this.lastModified = lastModified;
      return this;
    }

    public Builder withState(State state) {
      this.state = state;
      return this;
    }

    public Environment build() {
      Environment environment = new Environment();
      environment.configuration = this.configuration;
      environment.lastModified = this.lastModified;
      environment.buildHash = this.buildHash;
      environment.created = this.created;
      environment.id = this.id;
      environment.state = this.state;
      return environment;
    }
  }
}
