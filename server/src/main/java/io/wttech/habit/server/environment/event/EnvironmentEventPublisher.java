package io.wttech.habit.server.environment.event;

public interface EnvironmentEventPublisher {

  void publishEvent(EnvironmentEvent event);

}
