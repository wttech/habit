package io.wttech.habit.server.request.logs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class LogStructureFactoryTest {

  @Test
  public void parseApacheLog() {
    String dateString = "Sat Nov 23 14:20:43.031159 2019";
//    EEE MMM dd HH:mm:ss.SSSSSS yyyy
    DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseStrict()
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
    TemporalAccessor parsed = formatter.parse(dateString);
    LocalDateTime date = LocalDateTime.from(parsed);
    OffsetDateTime offsetDateTime = date.atOffset(ZoneOffset.UTC);
    Instant instant = offsetDateTime.toInstant();
    System.out.println(instant.toEpochMilli());
  }

  @Test
  public void apacheErrorWithoutSource() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:20:43.031159 2019] [ssl:info] [pid 1:tid 140667177050176] AH01914: Configuring server front.domain.com:443 for SSL protocol";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("Configuring server front.domain.com:443 for SSL protocol");
    assertThat(parsed.getTags())
        .contains("error");
    assertThat(parsed.getData())
        .containsEntry("errorCode", "AH01914")
        .containsEntry("pid", "1")
        .containsEntry("tid", "140667177050176")
        .containsEntry("module", "ssl")
        .containsEntry("logLevel", "info");
  }

  @Test
  public void apacheErrorWithSource() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:20:43.031211 2019] [ssl:debug] [pid 1:tid 140667177050176] ssl_engine_init.c(1750): AH10083: Init: (front.domain.com:443) mod_md support is unavailable.";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("Init: (front.domain.com:443) mod_md support is unavailable.");
    assertThat(parsed.getTags())
        .contains("error");
    assertThat(parsed.getData())
        .containsEntry("errorCode", "AH10083")
        .containsEntry("pid", "1")
        .containsEntry("tid", "140667177050176")
        .containsEntry("module", "ssl")
        .containsEntry("logLevel", "debug");
  }

  @Test
  public void apacheErrorMessageWithBrackets() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:20:43.032378 2019] [ssl:debug] [pid 1:tid 140667177050176] ssl_util_ssl.c(476): AH02412: [front.domain.com:443] Cert does not match for name 'front.domain.com' [subject: CN=example.com / issuer: CN=example.com / serial: 8B5B9010F7DA95A6 / notbefore: Mar 29 16:22:35 2019 GMT / notafter: Mar 26 16:22:35 2029 GMT]";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("[front.domain.com:443] Cert does not match for name 'front.domain.com' [subject: CN=example.com / issuer: CN=example.com / serial: 8B5B9010F7DA95A6 / notbefore: Mar 29 16:22:35 2019 GMT / notafter: Mar 26 16:22:35 2029 GMT]");
    assertThat(parsed.getTags())
        .contains("error");
    assertThat(parsed.getData())
        .containsEntry("errorCode", "AH02412")
        .containsEntry("pid", "1")
        .containsEntry("tid", "140667177050176")
        .containsEntry("module", "ssl")
        .containsEntry("logLevel", "debug");
  }

  @Test
  public void apacheErrorMessageWithParentheses() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:21:31.680158 2019] [ssl:debug] [pid 218:tid 140666881435392] ssl_engine_kernel.c(383): [client 172.30.0.2:56355] AH02034: Initial (No.1) HTTPS request received for child 132 (server front.domain.com:443)";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("Initial (No.1) HTTPS request received for child 132 (server front.domain.com:443)");
    assertThat(parsed.getTags())
        .contains("error");
    assertThat(parsed.getData())
        .containsEntry("errorCode", "AH02034")
        .containsEntry("pid", "218")
        .containsEntry("tid", "140666881435392")
        .containsEntry("module", "ssl")
        .containsEntry("logLevel", "debug");
  }

  @Test
  public void rewriteInit() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:21:31.680203 2019] [rewrite:trace2] [pid 218:tid 140666881435392] mod_rewrite.c(483): [client 172.30.0.2:56355] 172.30.0.2 - - [front.domain.com/sid#7fefa1139420][rid#7fef941030a0/initial] init rewrite engine with requested uri /product-instructions/";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("init rewrite engine with requested uri /product-instructions/");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "218")
        .containsEntry("tid", "140666881435392")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace2");
  }

  @Test
  public void applyingPattern() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:21:31.680216 2019] [rewrite:trace3] [pid 218:tid 140666881435392] mod_rewrite.c(483): [client 172.30.0.2:56355] 172.30.0.2 - - [front.domain.com/sid#7fefa1139420][rid#7fef941030a0/initial] applying pattern '^' to uri '/product-instructions/'";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("applying pattern '^' to uri '/product-instructions/'");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "218")
        .containsEntry("tid", "140666881435392")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace3");
  }

  @Test
  public void rewriteCond() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:21:31.680253 2019] [rewrite:trace4] [pid 218:tid 140666881435392] mod_rewrite.c(483): [client 172.30.0.2:56355] 172.30.0.2 - - [front.domain.com/sid#7fefa1139420][rid#7fef941030a0/initial] RewriteCond: input='DELETE' pattern='!^(POST|GET|HEAD)' => matched";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("RewriteCond: input='DELETE' pattern='!^(POST|GET|HEAD)' => matched");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "218")
        .containsEntry("tid", "140666881435392")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace4")
        .containsEntry("input", "DELETE")
        .containsEntry("pattern", "!^(POST|GET|HEAD)")
        .containsEntry("result", "matched");
  }

  @Test
  public void forcingResponse() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Wed Nov 13 14:21:31.680263 2019] [rewrite:trace2] [pid 218:tid 140666881435392] mod_rewrite.c(483): [client 172.30.0.2:56355] 172.30.0.2 - - [front.domain.com/sid#7fefa1139420][rid#7fef941030a0/initial] forcing responsecode 403 for /product-instructions/";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("forcing responsecode 403 for /product-instructions/");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "218")
        .containsEntry("tid", "140666881435392")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace2");
  }

  @Test
  public void settingEnvVariable() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Sat Nov 16 13:12:47.261147 2019] [rewrite:trace5] [pid 317:tid 140359992567552] mod_rewrite.c(470): [client 172.20.0.2:59932] 172.20.0.2 - - [hmicom.vagrant/sid#5556525b67b0][rid#7fa808006690/initial] setting env variable 'SITE_LANG' to 'en_us'";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("setting env variable 'SITE_LANG' to 'en_us'");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "317")
        .containsEntry("tid", "140359992567552")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace5");
  }

  @Test
  public void rewrite() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Sat Nov 16 13:13:58.921212 2019] [rewrite:trace2] [pid 653:tid 140359958996736] mod_rewrite.c(470): [client 172.20.0.2:52492] 172.20.0.2 - - [hmicom.vagrant/sid#55565272d7e0][rid#7fa800006690/initial] rewrite '/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html' -> 'http://publish.knotx.service.consul:8093/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html'";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("rewrite '/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html' -> 'http://publish.knotx.service.consul:8093/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html'");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "653")
        .containsEntry("tid", "140359958996736")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace2");
  }

  @Test
  public void forcingProxyThroughput() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Sat Nov 16 13:13:58.921222 2019] [rewrite:trace2] [pid 653:tid 140359958996736] mod_rewrite.c(470): [client 172.20.0.2:52492] 172.20.0.2 - - [hmicom.vagrant/sid#55565272d7e0][rid#7fa800006690/initial] forcing proxy-throughput with http://publish.knotx.service.consul:8093/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("forcing proxy-throughput with http://publish.knotx.service.consul:8093/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "653")
        .containsEntry("tid", "140359958996736")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace2");
  }

  @Test
  public void goAhead() {
    LogStructureFactory underTest = new LogStructureFactory(PatternFactory.instance());
    String logLine = "[Sat Nov 16 13:13:58.921235 2019] [rewrite:trace1] [pid 653:tid 140359958996736] mod_rewrite.c(470): [client 172.20.0.2:52492] 172.20.0.2 - - [hmicom.vagrant/sid#55565272d7e0][rid#7fa800006690/initial] go-ahead with proxy request proxy:http://publish.knotx.service.consul:8093/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html [OK]";
    ParsedLine parsed = underTest.parse(logLine);
    assertThat(parsed.getLine())
        .isEqualTo(logLine);
    assertThat(parsed.getMessage())
        .isEqualTo("go-ahead with proxy request proxy:http://publish.knotx.service.consul:8093/content/hmicom/northamerica/en_us/home/resources/3d-models-and-planning-tools/planning-ideas.html [OK]");
    assertThat(parsed.getTags())
        .contains("rewrite");
    assertThat(parsed.getData())
        .containsEntry("pid", "653")
        .containsEntry("tid", "140359958996736")
        .containsEntry("module", "rewrite")
        .containsEntry("logLevel", "trace1");
  }

}
