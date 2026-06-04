package com.vod.onboarding.common.catalog;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates test case catalog JSON. Run: {@code ./gradlew validateCatalog}
 */
public final class CatalogValidator {
  private static final Pattern CYRILLIC = Pattern.compile("[\\p{InCyrillic}]");
  private static final Pattern AUTOMATED_IN =
      Pattern.compile("^[A-Za-z0-9_.]+\\.[a-zA-Z0-9_]+$");
  private static final Pattern JIRA_KEY = Pattern.compile("^[A-Z][A-Z0-9]+-\\d+$");

  private CatalogValidator() {}

  public static void main(String[] args) throws Exception {
    String resource = "vod-preferences.json";
    boolean syncDocs = false;
    for (String arg : args) {
      if ("--sync-docs".equals(arg)) {
        syncDocs = true;
      } else if (!arg.startsWith("--")) {
        resource = arg;
      }
    }
    if (syncDocs) {
      syncDocsMirror(resource);
      return;
    }
    List<String> errors = validate(TestCaseCatalog.load(resource));
    if (errors.isEmpty()) {
      System.out.println("Catalog OK: " + resource);
      return;
    }
    errors.forEach(e -> System.err.println("ERROR: " + e));
    System.exit(1);
  }

  public static List<String> validate(TestCaseCatalog catalog) {
    Set<String> ids = new HashSet<>();
    java.util.ArrayList<String> errors = new java.util.ArrayList<>();

    if (catalog.feature() != null && CYRILLIC.matcher(catalog.feature()).find()) {
      errors.add("feature must be English (no Cyrillic): " + catalog.feature());
    }

    for (TestCaseCatalog.TestCase c : catalog.cases()) {
      if (!ids.add(c.id())) {
        errors.add("duplicate id: " + c.id());
      }
      if (c.title() != null && CYRILLIC.matcher(c.title()).find()) {
        errors.add(c.id() + ": title must be English");
      }
      if (c.automated_in() != null && !c.automated_in().isBlank()) {
        if (!AUTOMATED_IN.matcher(c.automated_in()).matches()) {
          errors.add(c.id() + ": invalid automated_in format: " + c.automated_in());
        }
      }
      if (c.jira_key() != null && !c.jira_key().isBlank() && !JIRA_KEY.matcher(c.jira_key()).matches()) {
        errors.add(c.id() + ": invalid jira_key: " + c.jira_key());
      }
      if (c.steps() != null) {
        for (TestCaseCatalog.Step step : c.steps()) {
          if (step.action() != null && CYRILLIC.matcher(step.action()).find()) {
            errors.add(c.id() + ": step action must be English");
          }
          if (step.expected() != null && CYRILLIC.matcher(step.expected()).find()) {
            errors.add(c.id() + ": step expected must be English");
          }
        }
      }
    }
    return errors;
  }

  /** Copies canonical catalog to docs mirror. */
  public static void syncDocsMirror(String resourceName) throws Exception {
    Path src =
        Path.of("src/test/resources/test-cases").resolve(resourceName);
    Path dest = Path.of("docs/test-cases").resolve(resourceName);
    Files.createDirectories(dest.getParent());
    Files.writeString(dest, Files.readString(src, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    System.out.println("Synced " + dest);
  }
}
