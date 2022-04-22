package io.wttech.habit.server.request.logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParsedLine {

  private final String line;
  private final String message;
  private final Map<String, String> data;
  private final List<String> tags;

  public static Builder builder(String line) {
    return new Builder(line);
  }

  public static final class Builder {

    private String line;
    private String message;
    private Map<String, String> data = new HashMap<>();
    private List<String> tags = new ArrayList<>();

    private Builder(String line) {
      this.line = line;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withData(Map<String, String> data) {
      this.data = data;
      return this;
    }

    public Builder addData(String key, String value) {
      data.put(key, value);
      return this;
    }

    public Builder addData(Map<String, String> data) {
      this.data.putAll(data);
      return this;
    }

    public Builder withTags(List<String> tags) {
      this.tags = tags;
      return this;
    }

    public Builder addTag(String tag) {
      tags.add(tag);
      return this;
    }

    public ParsedLine build() {
      return new ParsedLine(line, message, data, tags);
    }
  }
}
