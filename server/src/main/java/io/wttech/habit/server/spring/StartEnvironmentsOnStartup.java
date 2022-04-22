package io.wttech.habit.server.spring;

import io.wttech.habit.server.environment.EnvironmentApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class StartEnvironmentsOnStartup {

  private final EnvironmentApi environmentApi;

  @EventListener(ApplicationReadyEvent.class)
  public void environmentsUp() {
    log.info("Habit Server startup");
    environmentApi.destroyAllOfPreviousVersion();
    environmentApi.startAllUp();
  }

}
