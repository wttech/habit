package io.wttech.habit.server.request.logs;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestLogsDTOFactory {

  public RequestLogsDTO create(String id, List<StructuredLogLine> lines) {

    Map<LogFileId, List<StructuredLogLine>> groupedByFile = lines.stream().collect(
        Collectors.groupingBy(line -> new LogFileId(line.getServer(), line.getFilePath())));

    List<StructuredLogFile> logFiles = groupedByFile.entrySet().stream()
        .map(entry -> {
          List<StructuredLogLine> value = entry.getValue();
          value.sort(Comparator.comparing(StructuredLogLine::getTimestamp));
          return new StructuredLogFile(entry.getKey().getServerName(), entry.getKey().getFilePath(),
              value);
        }).collect(Collectors.toList());

    return new RequestLogsDTO(id, logFiles);
  }

  @Value
  private class LogFileId {

    private final String serverName;
    private final String filePath;

  }

}
