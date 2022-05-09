package io.wttech.habit.server.environment.configuration.request;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class ServerConfiguration {

  private String name;
  @JsonMerge
  @JsonSetter(nulls = Nulls.SKIP)
  private ProxyServerConfiguration docker;
  @JsonSetter(nulls = Nulls.SKIP)
  private List<String> domains;
  @JsonMerge
  @JsonSetter(nulls = Nulls.SKIP)
  private DeployConfiguration deploy;
  private List<Integer> ports;
  private List<Integer> sslPorts;
  private List<String> logFiles = new ArrayList<>();

  public ServerConfiguration() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProxyServerConfiguration getDocker() {
    return docker;
  }

  public void setDocker(ProxyServerConfiguration docker) {
    this.docker = docker;
  }

  public List<String> getDomains() {
    return domains;
  }

  public void setDomains(List<String> domains) {
    this.domains = domains;
  }

  public DeployConfiguration getDeploy() {
    return deploy;
  }

  public boolean isDeployPresent() {
    return deploy != null;
  }

  public void setDeploy(DeployConfiguration deploy) {
    this.deploy = deploy;
  }

  public List<Integer> getPorts() {
    return ports;
  }

  public void setPorts(List<Integer> ports) {
    this.ports = ports;
  }

  public List<Integer> getSslPorts() {
    return sslPorts;
  }

  public void setSslPorts(List<Integer> sslPorts) {
    this.sslPorts = sslPorts;
  }

  public List<String> getLogFiles() {
    return logFiles;
  }

  public void setLogFiles(List<String> logFiles) {
    this.logFiles = logFiles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerConfiguration that = (ServerConfiguration) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(domains, that.domains) &&
        Objects.equals(docker, that.docker) &&
        Objects.equals(deploy, that.deploy) &&
        Objects.equals(ports, that.ports) &&
        Objects.equals(sslPorts, that.sslPorts) &&
        Objects.equals(logFiles, that.logFiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, docker, domains, deploy, ports, sslPorts, logFiles);
  }
}
