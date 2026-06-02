package com.vod.onboarding.ui.uiTests;

import com.microsoft.playwright.assertions.LocatorAssertions;
import com.vod.onboarding.ui.UiTestBase;
import com.vod.onboarding.ui.pages.OnboardingPage;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("VOD onboarding")
@Feature("Onboarding survey UI")
@Tag("ui")
/**
 * UI tests for onboarding questionnaire behavior and flow.
 */
class OnboardingUiTest extends UiTestBase {

  private static LocatorAssertions.ContainsTextOptions ignoreCase() {
    return new LocatorAssertions.ContainsTextOptions().setIgnoreCase(true);
  }

  @Test
  @TmsLink("VP-UI-001")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("VP-UI-001 Далі disabled until 3 genres selected")
  void nextButton_disabledUntilThreeGenres() {
    OnboardingPage onboarding = openOnboarding(newPage(), "profile-ui-001");

    assertThat(onboarding.nextButton()).isDisabled();

    onboarding.selectGenres("genre-action", "genre-comedy");
    assertThat(onboarding.nextButton()).isDisabled();

    onboarding.selectGenre("genre-drama");
    assertThat(onboarding.nextButton()).isEnabled();
  }

  @Test
  @TmsLink("VP-UI-002")
  @DisplayName("VP-UI-002 Далі advances to movies step")
  void nextButton_advancesToMovieStep() {
    OnboardingPage onboarding = openOnboarding(newPage(), "profile-ui-002")
        .selectThreeGenres()
        .clickNext();

    assertThat(onboarding.moviesStep()).isVisible();
    assertThat(onboarding.stepLabel()).containsText("Крок 2");
    assertThat(onboarding.nextButton()).isDisabled();
  }

  @Test
  @TmsLink("VP-UI-003")
  @DisplayName("VP-UI-003 Пропустити saves skip and shows default recommendations")
  void skipButton_usesDefaultRecommendations() {
    OnboardingPage onboarding = openOnboarding(newPage(), "profile-ui-003")
        .clickSkip();

    assertThat(onboarding.status()).containsText("default", ignoreCase());
  }

  @Test
  @TmsLink("VP-UI-004")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("VP-UI-004 complete flow saves personalized recommendations")
  void completeFlow_showsPersonalizedRecommendations() {
    OnboardingPage onboarding = openOnboarding(newPage(), "profile-ui-004")
        .selectThreeGenres()
        .clickNext()
        .selectFiveMovies()
        .clickNext();

    assertThat(onboarding.status()).containsText("personalized", ignoreCase());
  }
}
