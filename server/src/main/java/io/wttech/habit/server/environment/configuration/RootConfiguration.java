package io.wttech.habit.server.environment.configuration;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import io.wttech.habit.server.environment.configuration.general.NetworkConfiguration;
import io.wttech.habit.server.environment.configuration.request.EndpointConfiguration;
import io.wttech.habit.server.environment.configuration.request.ServerConfiguration;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class RootConfiguration {

  private String id;
  private NetworkConfiguration network;
  private List<EndpointConfiguration> mocks = new ArrayList<>();
  @JsonSetter(nulls = Nulls.SKIP)
  private List<ServerConfiguration> servers = new ArrayList<>();

  public String getId() {
    return id;
  }

  public Optional<NetworkConfiguration> getNetwork() {
    return Optional.ofNullable(network);
  }

  public List<EndpointConfiguration> getMocks() {
    return mocks;
  }

  public List<ServerConfiguration> getServers() {
    return servers;
  }

  public int hashCode() {
    return Objects.hash(mocks, servers);
  }
}
