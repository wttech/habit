package io.wttech.habit.server.environment.requestgraph.container;

import io.wttech.habit.server.docker.container.ContainerLabel;
import io.wttech.habit.server.docker.container.ContainerService;
import io.wttech.habit.server.docker.container.ContainerType;
import io.wttech.habit.server.docker.volume.VolumeService;
import io.wttech.habit.server.environment.configuration.request.Mount;
import io.wttech.habit.server.environment.configuration.request.ServerConfiguration;
import io.wttech.habit.server.environment.diff.ExpectedState;
import io.wttech.habit.server.environment.diff.deploy.ProxyServerDeployment;
import io.wttech.habit.server.environment.diff.deploy.strategy.CopyDeploy;
import io.wttech.habit.server.environment.diff.deploy.strategy.DeployStrategy;
import io.wttech.habit.server.environment.diff.deploy.trigger.AlwaysDeploy;
import io.wttech.habit.server.environment.diff.deploy.trigger.DeployTrigger;
import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.HostConfig.Bind;
import com.spotify.docker.client.messages.Volume;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProxyServerContainerService {

  private final ContainerService containerService;
  private final VolumeService volumeService;

  public List<ExpectedState> getExpectedStates(String projectName,
      List<ServerConfiguration> servers) {
    List<ExpectedState> list = new ArrayList<>();
    for (ServerConfiguration server : servers) {
      list.add(getExpectedStateForEachHttpdConfig(projectName, server));
    }
    return list;
  }

  private ExpectedState getExpectedStateForEachHttpdConfig(String projectName,
      ServerConfiguration server) {
    Map<Volume, String> volumes = server.getDocker()
        .getVolumes()
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            v -> createVolumeIfAbsent(projectName, v.getKey()),
            Entry::getValue
        ));
    List<String> hostnames = new ArrayList<>();
    if (server.getDocker().isHostnamePresent()) {
      hostnames.add(server.getDocker().getContainerHostname());
    }
    List<String> backendHostnames = server.getDomains().stream()
        .map(domain -> domain + ".real")
        .collect(Collectors.toList());
    hostnames.addAll(backendHostnames);
    String containerName = generateApacheContainerName(projectName, server.getName());
    ExpectedState.Builder builder = ExpectedState.builder()
        .withName(containerName)
        .withType("proxy-server")
        .withHash(String.valueOf(server.hashCode()))
        .withDomainNames(hostnames)
        .withLogFiles(server.getLogFiles())
        .withFactory(() -> createApacheContainerConfiguration(projectName, server, volumes))
        .withExternal(false);
    if (server.isDeployPresent()) {
      DeployStrategy deployStrategy = CopyDeploy.builder()
          .withContainerService(containerService)
          .withContainerName(containerName)
          .withServerName(server.getName())
          .withDeploy(server.getDeploy())
          .build();
      DeployTrigger deployTrigger = new AlwaysDeploy();
      ProxyServerDeployment proxyServerDeployment = ProxyServerDeployment.builder()
          .withTrigger(deployTrigger)
          .withStrategy(deployStrategy)
          .withPostDeployCommand(server.getDeploy().getPostDeployCommand())
          .withReloadCommand(server.getDeploy().getReloadCommand())
          .build();
      builder
          .withDeployment(proxyServerDeployment);
    }
    return builder.build();
  }

  private Volume createVolumeIfAbsent(String projectName, String volumeName) {
    String fullVolumeName = projectName + '.' + volumeName;

    Volume toCreate = Volume.builder()
        .driver("local")
        .name(fullVolumeName)
        .labels(ImmutableMap.of(ContainerLabel.ENVIRONMENT, projectName))
        .build();

    return volumeService.createVolumeIfNotExists(toCreate);
  }

  private String generateApacheContainerName(String projectName, String serverId) {
    return String.format("%s_proxy-server_%s", projectName, serverId);
  }

  private ContainerConfig createApacheContainerConfiguration(String projectName,
      ServerConfiguration server, Map<Volume, String> volumes) {
    Map<String, String> labels = new HashMap<>();
    labels.put(ContainerLabel.ENVIRONMENT, projectName);
    labels.put(ContainerLabel.TYPE, ContainerType.BLACK_BOX);
    labels.put(ContainerLabel.SERVER_ID, server.getName());
    labels.put(ContainerLabel.HASH, String.valueOf(server.hashCode()));
    String timestamp = String.valueOf(System.currentTimeMillis());
    labels.put(ContainerLabel.CONFIGURATION_TIMESTAMP, timestamp);
    HostConfig.Builder hostConfigurationBuilder = HostConfig.builder();
    for (Mount mount : server.getDocker().getMounts()) {
      hostConfigurationBuilder.appendBinds(Bind
          .from(mount.getSource())
          .to(mount.getTarget())
          .readOnly(mount.isReadonly())
          .build());
    }
    hostConfigurationBuilder.tmpfs(server.getDocker().getTmpfs());
    hostConfigurationBuilder.capAdd(server.getDocker().getCapabilities());

    for (Entry<Volume, String> volume : volumes.entrySet()) {
      hostConfigurationBuilder.appendBinds(Bind
          .from(volume.getKey())
          .to(volume.getValue())
          .readOnly(false)
          .build());
    }
    return ContainerConfig.builder()
        .tty(true)
        .image(server.getDocker().getImage())
        .hostConfig(hostConfigurationBuilder.build())
        .labels(labels)
        .build();
  }
}
