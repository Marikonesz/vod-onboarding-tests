package com.vod.onboarding.common.harness;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Assigns each test to exactly one CI shard via stable hash of the JUnit unique id.
 *
 * <p>Activate with Gradle {@code -PshardIndex=0 -PshardTotal=3} (maps to system properties
 * {@code ci.shard.index} / {@code ci.shard.total}). When {@code shardTotal <= 1}, all tests run.
 */
public final class CiShardExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    int total = readPositiveInt("ci.shard.total", 1);
    if (total <= 1) {
      return ConditionEvaluationResult.enabled("CI sharding disabled");
    }
    int index = readNonNegativeInt("ci.shard.index", 0);
    if (index >= total) {
      return ConditionEvaluationResult.enabled("invalid shard index; running test");
    }
    int bucket = Math.floorMod(context.getUniqueId().hashCode(), total);
    if (bucket == index) {
      return ConditionEvaluationResult.enabled("shard " + index + "/" + total);
    }
    return ConditionEvaluationResult.disabled("shard " + index + "/" + total);
  }

  private static int readPositiveInt(String key, int defaultValue) {
    String raw = System.getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    try {
      return Math.max(1, Integer.parseInt(raw.trim()));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private static int readNonNegativeInt(String key, int defaultValue) {
    String raw = System.getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    try {
      return Math.max(0, Integer.parseInt(raw.trim()));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
