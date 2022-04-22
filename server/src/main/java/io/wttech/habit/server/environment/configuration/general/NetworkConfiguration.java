package io.wttech.habit.server.environment.configuration.general;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class NetworkConfiguration {

  @JsonSetter(nulls = Nulls.SKIP)
  private String subnet;
  @JsonSetter(nulls = Nulls.SKIP)
  private String ipRange;
  @JsonSetter(nulls = Nulls.SKIP)
  private String gateway;

  NetworkConfiguration() {
  }

  public NetworkConfiguration(String subnet, String ipRange, String gateway) {
    this.subnet = subnet;
    this.ipRange = ipRange;
    this.gateway = gateway;
  }

  public static NetworkConfiguration of(String subnet, String ipRange, String gateway) {
    return new NetworkConfiguration(subnet, ipRange, gateway);
  }

  public String getSubnet() {
    return subnet;
  }

  public void setSubnet(String subnet) {
    this.subnet = subnet;
  }

  public String getIpRange() {
    return ipRange;
  }

  public void setIpRange(String ipRange) {
    this.ipRange = ipRange;
  }

  public String getGateway() {
    return gateway;
  }

  public void setGateway(String gateway) {
    this.gateway = gateway;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NetworkConfiguration that = (NetworkConfiguration) o;
    return Objects.equals(subnet, that.subnet) &&
        Objects.equals(ipRange, that.ipRange) &&
        Objects.equals(gateway, that.gateway);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subnet, ipRange, gateway);
  }
}
