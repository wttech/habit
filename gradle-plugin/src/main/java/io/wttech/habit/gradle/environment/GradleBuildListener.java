package io.wttech.habit.gradle.environment;

import io.wttech.habit.client.ProgressListener;
import io.wttech.habit.client.sse.ProgressEvent;
import org.gradle.api.logging.Logger;

public class GradleBuildListener implements ProgressListener {

  private final Logger logger;

  GradleBuildListener(Logger logger) {
    this.logger = logger;
  }

  public static GradleBuildListener of(Logger logger) {
    return new GradleBuildListener(logger);
  }

  @Override
  public void onEvent(ProgressEvent event) {
    if (event.isGeneral()) {
      logger.lifecycle("{}", event.getMessage());
    } else {
      logger.lifecycle("{}/{}: {}", event.getCompletedSteps(), event.getTotalSteps(),
          event.getMessage());
    }
  }

  @Override
  public void onComplete() {
  }

}
