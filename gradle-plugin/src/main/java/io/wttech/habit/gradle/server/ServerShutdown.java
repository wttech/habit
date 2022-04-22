package io.wttech.habit.gradle.server;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerShutdown extends DefaultTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerShutdown.class);

  @TaskAction
  public void stop() {
    ServerInstaller serverInstaller = new ServerInstaller();
    if (!serverInstaller.isDockerInstalled()) {
      LOGGER.error("Docker is not installed!");
      LOGGER.error("Installation interrupted.");
      return;
    }
    serverInstaller.shutdown();
  }

}
