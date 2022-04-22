package io.wttech.habit.server.request.logs;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(staticName = "instance")
public class PatternFactory {

  public String standardPattern() {
    return date() + " " + logLevel() + " " + pidTid();
  }

  private String lowercaseWord() {
    return "[a-z]+";
  }

  private String lowercaseAlphanumeric() {
    return "[a-z0-9]+";
  }

  private String lazyAnything() {
    return ".+?";
  }

  private String integer() {
    return "[1-9][0-9]*";
  }

  private String date() {
    return "\\[" + namedGroup("date", lazyAnything()) + "\\]";
  }

  private String logLevel() {
    return "\\[" + namedGroup("module", lowercaseWord()) + ":" + namedGroup("logLevel", lowercaseAlphanumeric()) + "\\]";
  }

  private String pid() {
    return "pid " + namedGroup("pid", integer());
  }

  private String tid() {
    return "tid " + namedGroup("tid", integer());
  }

  private String pidTid() {
    return "\\[" + pid() + "(:" + tid() + ")?\\]";
  }

  private String namedGroup(String name, String pattern) {
    return String.format("(?<%s>%s)", name, pattern);
  }

}
