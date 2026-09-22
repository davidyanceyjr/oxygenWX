package com.oxygen.weather.presentation

import com.oxygen.weather.data.RefreshFailure
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.DerivedWeather

/** Inputs to the application-to-presentation load-state mapper. */
sealed interface HomePresentationInput {
    data object Loading : HomePresentationInput

    data class Data(
        val result: WeatherRepositoryResult,
        val derived: DerivedWeather,
    ) : HomePresentationInput

    data class FailureWithoutData(val failure: RefreshFailure) : HomePresentationInput
}

/** Freshness translated from repository facts without inference or age thresholds. */
enum class PresentedFreshness {
    CURRENT,
    STALE,
    UNKNOWN,
}

/** Origin of data retained after a refresh failure. */
enum class RetainedDataOrigin {
    LIVE,
    SAVED,
}

/** Failure wording category supported by the supplied repository fact. */
enum class PresentedRefreshFailureKind {
    NETWORK,
    SOURCE,
    UNSPECIFIED,
}

data class StatusPresentation(val visibleText: String) {
    val accessibilitySummary: String
        get() = visibleText

    companion object {
        fun of(text: String): StatusPresentation = StatusPresentation(text)
    }
}

/** Outer load/refresh outcome. Weather availability remains nested in data-bearing states. */
sealed interface HomeLoadState {
    data class Loading(val status: StatusPresentation) : HomeLoadState

    data class LiveData(
        val content: HomePresentationState,
        val freshness: PresentedFreshness,
        val status: StatusPresentation,
    ) : HomeLoadState

    data class CachedData(
        val content: HomePresentationState,
        val freshness: PresentedFreshness,
        val status: StatusPresentation,
    ) : HomeLoadState

    data class RefreshFailedWithRetainedData(
        val content: HomePresentationState,
        val origin: RetainedDataOrigin,
        val freshness: PresentedFreshness,
        val failure: PresentedRefreshFailureKind,
        val status: StatusPresentation,
    ) : HomeLoadState

    /** Deliberately contains status only; no weather, source, update, or location claim. */
    data class FailedWithoutData(val status: StatusPresentation) : HomeLoadState
}
