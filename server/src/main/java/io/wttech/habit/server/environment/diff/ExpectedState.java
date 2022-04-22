package io.wttech.habit.server.environment.diff;

import io.wttech.habit.server.environment.diff.deploy.ProxyServerDeployment;
import com.spotify.docker.client.messages.ContainerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ExpectedState {

  private final String name;
  private final String hash;
  private final String type;
  private final List<String> domainNames;
  private final Supplier<ContainerConfig> factory;
  private final boolean external;
  private final ProxyServerDeployment proxyServerDeployment;
  private final List<String> logFiles;

  ExpectedState(String name, String hash, String type, List<String> domainNames,
      Supplier<ContainerConfig> factory, boolean external,
      ProxyServerDeployment proxyServerDeployment,
      List<String> logFiles) {
    this.name = name;
    this.hash = hash;
    this.type = type;
    this.domainNames = domainNames;
    this.factory = factory;
    this.external = external;
    this.proxyServerDeployment = proxyServerDeployment;
    this.logFiles = logFiles;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getContainerName() {
    return name;
  }

  public String getHash() {
    return hash;
  }

  public String getType() {
    return type;
  }

  public List<String> getDomainNames() {
    return domainNames;
  }

  public List<String> getLogFiles() {
    return logFiles;
  }

  public Supplier<ContainerConfig> getFactory() {
    return factory;
  }

  public boolean isExternal() {
    return external;
  }

  public boolean isDeployRequired(ContainerState containerState) {
    return isDeploymentPresent() && getProxyServerDeployment().isDeployRequired(containerState);
  }

  public boolean isDeploymentPresent() {
    return proxyServerDeployment != null;
  }

  public ProxyServerDeployment getProxyServerDeployment() {
    if (!isDeploymentPresent()) {
      throw new NullPointerException();
    }
    return proxyServerDeployment;
  }

  public static final class Builder {

    private String name;
    private String hash;
    private String type;
    private List<String> domainNames = new ArrayList<>();
    private Supplier<ContainerConfig> factory;
    private boolean external;
    private String[] healthcheckCommand;
    private ProxyServerDeployment proxyServerDeployment;
    private List<String> logFiles = new ArrayList<>();

    private Builder() {
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withHash(String hash) {
      this.hash = hash;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public Builder withDomainNames(List<String> domainNames) {
      this.domainNames = domainNames;
      return this;
    }

    public Builder withDomainNames(String... domainNames) {
      this.domainNames = Arrays.asList(domainNames);
      return this;
    }

    public Builder withFactory(Supplier<ContainerConfig> factory) {
      this.factory = factory;
      return this;
    }

    public Builder withExternal(boolean external) {
      this.external = external;
      return this;
    }

    public Builder withDeployment(ProxyServerDeployment proxyServerDeployment) {
      this.proxyServerDeployment = proxyServerDeployment;
      return this;
    }

    public Builder withLogFiles(List<String> logFiles) {
      this.logFiles = logFiles;
      return this;
    }

    public ExpectedState build() {
      return new ExpectedState(name, hash, type, domainNames, factory, external,
          proxyServerDeployment,
          logFiles);
    }
  }
}
