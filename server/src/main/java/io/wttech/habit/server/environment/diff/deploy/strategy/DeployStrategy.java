package io.wttech.habit.server.environment.diff.deploy.strategy;

import java.nio.file.Path;

public interface DeployStrategy {

  void deploy(Path rootFolder);

}
