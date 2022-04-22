package io.wttech.habit.server.request.logs;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RequestLogsDTO {

  private final String requestId;
  private final List<StructuredLogFile> logs;

}
