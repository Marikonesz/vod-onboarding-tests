package com.vod.onboarding.common.catalog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Exports {@code src/test/resources/test-cases/*.json} to TestRail-friendly CSV.
 *
 * <p>Run: {@code ./gradlew exportTestRail}
 */
public final class TestRailExporter {
  private TestRailExporter() {}

  /** CLI entry: writes {@code build/testrail/vod-preferences.csv}. */
  public static void main(String[] args) throws IOException {
    TestCaseCatalog catalog = TestCaseCatalog.load("vod-preferences.json");
    Path out = Path.of("build", "testrail", "vod-preferences.csv");
    Files.createDirectories(out.getParent());
    Files.writeString(out, toCsv(catalog), StandardCharsets.UTF_8);
    System.out.println("Wrote " + out.toAbsolutePath() + " (" + catalog.cases().size() + " cases)");
  }

  /** Converts catalog to TestRail-friendly CSV (package-visible for tests). */
  static String toCsv(TestCaseCatalog catalog) {
    StringBuilder sb = new StringBuilder();
    sb.append("case_id,title,type,layer,section,preconditions,steps,expected,automated_in\n");
    for (TestCaseCatalog.TestCase c : catalog.cases()) {
      String pre = joinSemicolon(c.preconditions());
      String steps = c.steps().stream().map(TestCaseCatalog.Step::action).collect(Collectors.joining(" | "));
      String expected =
          c.steps().stream().map(TestCaseCatalog.Step::expected).collect(Collectors.joining(" | "));
      sb.append(csv(c.id()))
          .append(',').append(csv(c.title()))
          .append(',').append(csv(c.type()))
          .append(',').append(csv(c.layer()))
          .append(',').append(csv(catalog.suite()))
          .append(',').append(csv(pre))
          .append(',').append(csv(steps))
          .append(',').append(csv(expected))
          .append(',').append(csv(nullToEmpty(c.automated_in())))
          .append('\n');
    }
    return sb.toString();
  }

  private static String joinSemicolon(java.util.List<String> items) {
    if (items == null || items.isEmpty()) {
      return "";
    }
    return String.join("; ", items);
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }

  private static String csv(String value) {
    if (value == null) {
      return "\"\"";
    }
    String escaped = value.replace("\"", "\"\"");
    return "\"" + escaped + "\"";
  }
}

