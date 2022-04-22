package io.wttech.habit.server.environment;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EnvironmentNotFoundException extends ResponseStatusException {

  public EnvironmentNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Environment not found");
  }

}
