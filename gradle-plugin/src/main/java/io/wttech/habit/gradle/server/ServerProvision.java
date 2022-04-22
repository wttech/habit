package io.wttech.habit.gradle.server;

import io.wttech.habit.gradle.util.PluginProperties;
import io.wttech.habit.gradle.util.PropertiesProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerProvision extends DefaultTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerProvision.class);

  private final PropertiesProvider propertiesProvider = PropertiesProvider.instance();

  private String proxyImage;
  private String serverImage;

  @Input
  @Optional
  public String getProxyImage() {
    return proxyImage;
  }

  public void setProxyImage(String proxyImage) {
    this.proxyImage = proxyImage;
  }

  @Input
  @Optional
  public String getServerImage() {
    return serverImage;
  }

  public void setServerImage(String serverImage) {
    this.serverImage = serverImage;
  }

  @TaskAction
  public void start() {
    ServerInstaller serverInstaller = new ServerInstaller();
    if (!serverInstaller.isDockerInstalled()) {
      LOGGER.error("Docker is not installed!");
      LOGGER.error("Installation interrupted.");
      return;
    }
    if (!serverInstaller.isDockerSwarmEnabled()) {
      LOGGER.error("Docker Swarm mode is not enabled!");
      LOGGER.error("Please use 'docker swarm init' command.");
      LOGGER.error("Installation interrupted.");
      return;
    }
    ProvisionDetails.Builder builder = defaultBuilder();
    if (serverImage != null) {
      builder.withServerImage(serverImage);
    }
    serverInstaller.provision(builder.build());
  }

  private ProvisionDetails.Builder defaultBuilder() {
    PluginProperties props = propertiesProvider.load();
    return ProvisionDetails.builder()
        .withServerImage(props.getServerImageId());
  }

}
