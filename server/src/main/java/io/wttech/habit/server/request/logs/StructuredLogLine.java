package io.wttech.habit.server.request.logs;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
@Indices({
    @Index(value = "epoch", type = IndexType.NonUnique),
    @Index(value = "environmentId", type = IndexType.NonUnique)
})
public class StructuredLogLine {

  private final String id;
  private final String environmentId;
  private final String server;
  private final String filePath;
  private final Instant timestamp;
  private final long epoch;
  private final String line;
  private final String message;
  private final Map<String, String> data;
  private final List<String> tags;

  StructuredLogLine(String id, String environmentId, String server,
      String filePath, Instant timestamp,
      long epoch, String line,
      String message, Map<String, String> data, List<String> tags) {
    this.id = id;
    this.environmentId = environmentId;
    this.server = server;
    this.filePath = filePath;
    this.timestamp = timestamp;
    this.epoch = epoch;
    this.line = line;
    this.message = message;
    this.data = data;
    this.tags = tags;
  }

  public static Builder builder() {
    return new Builder();
  }

  public boolean isStructured() {
    return message != null;
  }

  public String getId() {
    return id;
  }

  public String getEnvironmentId() {
    return environmentId;
  }

  public String getServer() {
    return server;
  }

  public String getFilePath() {
    return filePath;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public long getEpoch() {
    return epoch;
  }

  public String getLine() {
    return line;
  }

  public String getMessage() {
    return message;
  }

  public Map<String, String> getData() {
    return data;
  }

  public List<String> getTags() {
    return tags;
  }

  public static final class Builder {

    private String id = UUID.randomUUID().toString();
    private String environmentId;
    private String server;
    private String filePath;
    private Instant timestamp;
    private Long epoch;
    private String line;
    private String message;
    private Map<String, String> data = new HashMap<>();
    private List<String> tags = new ArrayList<>();

    private Builder() {
    }

    public Builder withEnvironmentId(String environmentId) {
      this.environmentId = environmentId;
      return this;
    }

    public Builder withServer(String server) {
      this.server = server;
      return this;
    }

    public Builder withFilePath(String filePath) {
      this.filePath = filePath;
      return this;
    }

    public Builder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      this.epoch = timestamp.toEpochMilli();
      return this;
    }

    public Builder withLine(String line) {
      this.line = line;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withData(Map<String, String> data) {
      this.data = data;
      return this;
    }

    public Builder withTags(List<String> tags) {
      this.tags = tags;
      return this;
    }

    public StructuredLogLine build() {
      StructuredLogLine structuredLogLine = new StructuredLogLine(id, environmentId, server, filePath,
          timestamp, epoch, line, message, data, tags);
      return structuredLogLine;
    }
  }
}
