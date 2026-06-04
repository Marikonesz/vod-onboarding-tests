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
  @DisplayName("VP-UI-001 'Next' disabled until 3 genres selected")
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
  @DisplayName("VP-UI-002 'Next' advances to movies step")
  void nextButton_advancesToMovieStep() {
    OnboardingPage onboarding = openOnboarding(newPage(), "profile-ui-002")
        .selectThreeGenres()
        .clickNext();

    assertThat(onboarding.moviesStep()).isVisible();
    assertThat(onboarding.stepLabel()).containsText("Step 2");
    assertThat(onboarding.nextButton()).isDisabled();
  }

  @Test
  @TmsLink("VP-UI-003")
  @DisplayName("VP-UI-003 'Skip' saves skip and shows default recommendations")
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
        .selectFirstMovies(5)
        .clickNext();

    assertThat(onboarding.status()).containsText("personalized", ignoreCase());
  }

  @Test
  @TmsLink("VP-UI-005")
  @DisplayName("VP-UI-005 'Finish' disabled until 5 movies selected")
  void finishButton_disabledUntilFiveMovies() {
    OnboardingPage onboarding =
        openOnboarding(newPage(), "profile-ui-005")
            .selectThreeGenres()
            .clickNext();

    assertThat(onboarding.moviesStep()).isVisible();
    assertThat(onboarding.nextButton()).isDisabled();

    onboarding.selectFirstMovies(4);
    assertThat(onboarding.nextButton()).isDisabled();

    onboarding.selectFirstMovies(5);
    assertThat(onboarding.nextButton()).isEnabled();
  }

  @Test
  @TmsLink("VP-UI-006")
  @DisplayName("VP-UI-006 survey hidden when already completed")
  void survey_hiddenWhenAlreadyCompleted() {
    String profileId = "profile-ui-006";
    seedSurveySkipped(profileId);

    OnboardingPage onboarding = openOnboarding(newPage(), profileId);
    assertThat(onboarding.status()).containsText("already completed", ignoreCase());
    assertThat(onboarding.genreList()).isHidden();
  }
}
