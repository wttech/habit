package io.wttech.habit.server.request.logs;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StructuredLogFile {

  private final String serverName;
  private final String path;
  private final List<StructuredLogLine> lines;

}
