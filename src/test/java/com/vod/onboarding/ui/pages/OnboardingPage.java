package com.vod.onboarding.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

/**
 * Page object: locators and user actions only (no assertions — keep those in tests).
 */
public final class OnboardingPage {
  private final Page page;

  /** @param page Playwright page bound to this screen */
  public OnboardingPage(Page page) {
    this.page = page;
  }

  // --- Locators ---

  /** Next button on genre and movie steps. */
  public Locator nextButton() {
    return page.getByTestId("btn-next");
  }

  /** Skip button. */
  public Locator skipButton() {
    return page.getByTestId("btn-skip");
  }

  /** Status line showing recommendation source (default/personalized). */
  public Locator status() {
    return page.getByTestId("status");
  }

  /** Container for step 2 (movie selection). */
  public Locator moviesStep() {
    return page.getByTestId("movies-step");
  }

  /** Step indicator label (e.g. "Step 1"). */
  public Locator stepLabel() {
    return page.getByTestId("step-label");
  }

  /** Genre checkbox list on step 1. */
  public Locator genreList() {
    return page.getByTestId("genre-list");
  }

  /** Movie checkbox list on step 2. */
  public Locator movieList() {
    return page.getByTestId("movie-list");
  }

  /** Single genre checkbox by id. */
  public Locator genre(String genreId) {
    return page.locator("[data-genre-id=\"" + genreId + "\"]");
  }

  /** Single movie checkbox by id. */
  public Locator movie(String movieId) {
    return page.locator("[data-movie-id=\"" + movieId + "\"]");
  }

  // --- Actions (fluent) ---

  /** Navigates to onboarding for the given profile on the mock server. */
  @Step("Open onboarding for profile {profileId}")
  public OnboardingPage open(String baseUrl, String profileId) {
    page.navigate(baseUrl + "/onboarding?profile_id=" + profileId);
    return this;
  }

  /** Clicks one genre checkbox. */
  @Step("Select genre {genreId}")
  public OnboardingPage selectGenre(String genreId) {
    genre(genreId).click();
    return this;
  }

  /** Selects multiple genres in order. */
  @Step("Select genres: {genreIds}")
  public OnboardingPage selectGenres(String... genreIds) {
    for (String genreId : genreIds) {
      selectGenre(genreId);
    }
    return this;
  }

  /** Selects action, comedy, and drama (minimum for enabling Next). */
  @Step("Select default three genres (action, comedy, drama)")
  public OnboardingPage selectThreeGenres() {
    return selectGenres("genre-action", "genre-comedy", "genre-drama");
  }

  /** Checks one movie on step 2. */
  @Step("Select movie {movieId}")
  public OnboardingPage selectMovie(String movieId) {
    movie(movieId).check();
    return this;
  }

  /** Selects {@code movie-1} … {@code movie-5}. */
  @Step("Select five default movies")
  public OnboardingPage selectFiveMovies() {
    for (int i = 1; i <= 5; i++) {
      selectMovie("movie-" + i);
    }
    return this;
  }

  /** Selects the first N movies currently listed on step 2. */
  @Step("Select first {count} visible movies")
  public OnboardingPage selectFirstMovies(int count) {
    var inputs = movieList().locator("[data-movie-id]");
    for (int i = 0; i < count; i++) {
      inputs.nth(i).check();
    }
    return this;
  }

  /** Clicks Next. */
  @Step("Click 'Next'")
  public OnboardingPage clickNext() {
    nextButton().click();
    return this;
  }

  /** Clicks Skip. */
  @Step("Click 'Skip'")
  public OnboardingPage clickSkip() {
    skipButton().click();
    return this;
  }
}
