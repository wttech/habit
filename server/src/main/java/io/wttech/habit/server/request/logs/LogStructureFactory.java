package io.wttech.habit.server.request.logs;

import com.joestelmach.natty.Parser;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class LogStructureFactory {

  private static final Pattern REWRITE_COND_DATA_PATTERN = Pattern.compile("RewriteCond: input='(?<input>.*?)' pattern='(?<pattern>.*?)' => (?<result>[a-z]+)");
  private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseStrict()
      .appendPattern("EEE")
      .appendLiteral(" ")
      .appendPattern("MMM")
      .appendLiteral(" ")
      .appendPattern("dd")
      .appendLiteral(" ")
      .appendPattern("HH:mm:ss.SSSSSS")
      .appendLiteral(" ")
      .appendPattern("yyyy")
      .toFormatter(Locale.ENGLISH);

  private final Parser dateParser = new Parser();
  private final PatternFactory patternFactory;
  private final Pattern commonDataPattern;

  @Inject
  LogStructureFactory(PatternFactory patternFactory) {
    this.patternFactory = patternFactory;
    this.commonDataPattern = Pattern.compile(patternFactory.standardPattern());
  }

  public ParsedLine parse(String line) {
    return firstNonEmpty(
        () -> tryApacheErrorLine(line),
        () -> tryRewriteLine(line)
    ).orElseGet(() -> ParsedLine.builder(line).build());
  }

  private Optional<ParsedLine> tryApacheErrorLine(String line) {
    Pattern apacheErrorPattern = Pattern.compile(" (AH[0-9]{5}):");
    Matcher apacheErrorMatcher = apacheErrorPattern.matcher(line);
    if (apacheErrorMatcher.find()) {
      int messageStartIndex = apacheErrorMatcher.end();
      String message = line.substring(messageStartIndex).trim();
      String rest = line.substring(0, messageStartIndex);
      Map<String, String> lineHeaderData = extractCommonData(rest);
      return Optional.of(ParsedLine.builder(line)
          .withMessage(message)
          .addTag("error")
          .addData("errorCode", apacheErrorMatcher.group(1))
          .addData(lineHeaderData)
          .build());
    }
    return Optional.empty();
  }

  private Optional<ParsedLine> tryRewriteLine(String line) {
    Pattern ridPattern = Pattern.compile("\\[rid#.+?\\]");
    Matcher ridMatcher = ridPattern.matcher(line);
    if (ridMatcher.find()) {
      int messageStartIndex = ridMatcher.end();
      String message = line.substring(messageStartIndex).trim();
      String rest = line.substring(0, ridMatcher.start());
      Map<String, String> lineHeaderData = extractCommonData(rest);
      Map<String, String> rewriteCondData = extractRewriteCondData(message);
      return Optional.of(ParsedLine.builder(line)
          .withMessage(message)
          .addTag("rewrite")
          .addData(lineHeaderData)
          .addData(rewriteCondData)
          .build());
    }
    return Optional.empty();
  }

  private Map<String, String> extractRewriteCondData(String message) {
    Map<String, String> data = new HashMap<>();
    Matcher matcher = REWRITE_COND_DATA_PATTERN.matcher(message);
    if (matcher.find()) {
      data.put("input", matcher.group("input"));
      data.put("pattern", matcher.group("pattern"));
      data.put("result", matcher.group("result"));
    }
    return data;
  }

  private Map<String, String> extractCommonData(String rest) {
    Matcher matcher = commonDataPattern.matcher(rest);
    Map<String, String> data = new HashMap<>();
    if (matcher.find()) {
      data.put("date", matcher.group("date"));
      data.computeIfPresent("date", (key, value) -> {
        Instant instant = parseApacheDate(value);;
        return Long.toString(instant.toEpochMilli());
      });
      data.put("module", matcher.group("module"));
      data.put("logLevel", matcher.group("logLevel"));
      data.put("pid", matcher.group("pid"));
      data.put("tid", matcher.group("tid"));
    }
    return data;
  }

  private Instant parseApacheDate(String dateString) {
    TemporalAccessor parsed = formatter.parse(dateString);
    LocalDateTime date = LocalDateTime.from(parsed);
    OffsetDateTime offsetDateTime = date.atOffset(ZoneOffset.UTC);
    return offsetDateTime.toInstant();
  }

  private <T> Optional<T> firstNonEmpty(Supplier<Optional<T>>... suppliers) {
    return Arrays.stream(suppliers)
        .map(Supplier::get)
        .filter(Optional::isPresent)
        .findFirst()
        .orElseGet(Optional::empty);
  }

}
