package io.wttech.habit.server.environment.configuration;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidConfigurationException extends ResponseStatusException {

  public InvalidConfigurationException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

  public InvalidConfigurationException(String message, Throwable cause) {
    super(HttpStatus.BAD_REQUEST, message, cause);
  }
}
