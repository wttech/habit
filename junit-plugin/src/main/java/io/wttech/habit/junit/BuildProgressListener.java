package io.wttech.habit.junit;

import io.wttech.habit.client.ProgressListener;
import io.wttech.habit.client.sse.ProgressEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildProgressListener implements ProgressListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabitRunner.class);

  public static BuildProgressListener instance() {
    return new BuildProgressListener();
  }

  @Override
  public void onEvent(ProgressEvent event) {
    if (event.isGeneral()) {
      LOGGER.info("{}", event.getMessage());
    } else {
      LOGGER.info("{}/{}: {}", event.getCompletedSteps(), event.getTotalSteps(), event.getMessage());
    }
  }

  @Override
  public void onComplete() {
  }
}
