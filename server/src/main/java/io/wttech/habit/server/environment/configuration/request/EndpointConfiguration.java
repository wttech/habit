package io.wttech.habit.server.environment.configuration.request;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.NoArgsConstructor;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
@NoArgsConstructor
public class EndpointConfiguration {

  private String hostname;
  private List<Integer> ports;
  private List<Integer> sslPorts = new ArrayList<>();

  public EndpointConfiguration(String hostname, List<Integer> ports, List<Integer> sslPorts) {
    this.hostname = hostname;
    this.ports = ports;
    this.sslPorts = sslPorts;
  }

  public static EndpointConfiguration of(String hostname, List<Integer> ports) {
    return new EndpointConfiguration(hostname, ports, Collections.emptyList());
  }

  public static EndpointConfiguration of(String hostname, List<Integer> ports,
      List<Integer> sslPorts) {
    return new EndpointConfiguration(hostname, ports, sslPorts);
  }

  public String getHostname() {
    return hostname;
  }

  public List<Integer> getPorts() {
    return ports;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public void setPorts(List<Integer> ports) {
    this.ports = ports;
  }

  public void setSslPorts(List<Integer> sslPorts) {
    this.sslPorts = sslPorts;
  }

  public List<Integer> getSslPorts() {
    return sslPorts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EndpointConfiguration that = (EndpointConfiguration) o;
    return ports == that.ports &&
        Objects.equals(hostname, that.hostname) &&
        sslPorts == that.sslPorts;
  }

  @Override
  public int hashCode() {
    return Objects.hash(hostname, ports, sslPorts);
  }
}
