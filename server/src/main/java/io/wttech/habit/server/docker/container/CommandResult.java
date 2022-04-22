package io.wttech.habit.server.docker.container;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class CommandResult {

  private final String output;
  private final long exitCode;

  public boolean isSuccess() {
    return exitCode == 0L;
  }

  public boolean isError() {
    return exitCode != 0L;
  }


}
