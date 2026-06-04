package com.vod.onboarding.common.fixtures;

import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import com.vod.onboarding.api.client.VodApiClient;

import static org.assertj.core.api.Assertions.assertThat;

/** Common API response assertions for VOD preferences tests. */
public final class ApiAssertions {
  private ApiAssertions() {}

  public static void assertStatus(APIResponse response, int expected) {
    assertThat(response.status()).isEqualTo(expected);
  }

  public static JsonObject assertStatusAndParse(VodApiClient client, APIResponse response, int expected) {
    assertStatus(response, expected);
    return client.parseJson(response);
  }

  public static void assertValidationError(JsonObject body, int minRequired) {
    assertValidationError(body, null, minRequired);
  }

  public static void assertValidationError(JsonObject body, String field, int minRequired) {
    assertThat(body.get("error").getAsString()).isEqualTo("validation_error");
    assertThat(body.get("min_required").getAsInt()).isEqualTo(minRequired);
    if (field != null) {
      assertThat(body.get("field").getAsString()).isEqualTo(field);
    }
  }

  public static void assertSaved(JsonObject body, String profileId) {
    assertThat(body.get("saved").getAsBoolean()).isTrue();
    assertThat(body.get("profile_id").getAsString()).isEqualTo(profileId);
  }
}
