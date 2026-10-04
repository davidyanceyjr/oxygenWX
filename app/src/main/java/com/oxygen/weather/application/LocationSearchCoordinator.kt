package com.oxygen.weather.application

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchRequest
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.presentation.LocationSearchPresentation
import java.util.concurrent.Executor

/** Typed state for one transient manual-location-search route. */
internal sealed interface LocationSearchState {
    val query: String

    data class Idle(override val query: String = "") : LocationSearchState
    data class Loading(override val query: String) : LocationSearchState
    data class Results(override val query: String, val candidates: List<LocationCandidate>) : LocationSearchState
    data class NoResults(override val query: String) : LocationSearchState
    data class Failure(
        override val query: String,
        val category: LocationSearchResult.Category,
    ) : LocationSearchState
}

/**
 * Owns search state and stale-result protection for a single search session.
 * [publisher] must publish on the UI thread when used with Compose state.
 */
class LocationSearchCoordinator(
    private val locationSearch: LocationSearch,
    private val worker: Executor,
    private val publisher: Executor,
    private val localeTag: () -> String,
    private val localIdGenerator: () -> String,
    private val onSelected: (ForecastRequest) -> Unit,
) {
    private val mutableState = mutableStateOf<LocationSearchState>(LocationSearchState.Idle())
    internal val state: State<LocationSearchState> = mutableState
    val presentationState: State<LocationSearchPresentation> = derivedStateOf { mutableState.value.toPresentation() }

    private var generation = 0L
    private var open = false

    val isSessionOpen: Boolean
        @Synchronized get() = open

    /** Start a fresh session with an empty query, invalidating any earlier work. */
    @Synchronized
    fun openSession() {
        generation++
        open = true
        mutableState.value = LocationSearchState.Idle()
    }

    /** Close the session and make all in-flight results obsolete. */
    @Synchronized
    fun dismiss() {
        generation++
        open = false
        mutableState.value = LocationSearchState.Idle()
    }

    /** Submit a trimmed query. Blank input is a strict no-op. */
    fun submit(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        val requestGeneration: Long
        synchronized(this) {
            if (!open) return
            generation++
            requestGeneration = generation
            mutableState.value = LocationSearchState.Loading(trimmed)
        }

        try {
            worker.execute {
                val result = try {
                    val request = LocationSearchRequest(trimmed, localeTag())
                    locationSearch.search(request)
                } catch (_: Exception) {
                    LocationSearchResult.Failure(LocationSearchResult.Category.TRANSPORT)
                }
                publisher.execute { publishIfCurrent(requestGeneration, trimmed, result) }
            }
        } catch (_: Exception) {
            publisher.execute {
                publishIfCurrent(
                    requestGeneration,
                    trimmed,
                    LocationSearchResult.Failure(LocationSearchResult.Category.TRANSPORT),
                )
            }
        }
    }

    /**
     * Select only a candidate in the currently displayed result set. Selection closes
     * the session before invoking the handoff, so repeated actions cannot emit twice.
     */
    fun select(index: Int): Boolean {
        val request: ForecastRequest
        synchronized(this) {
            val results = mutableState.value as? LocationSearchState.Results
                ?: return false
            val candidate = results.candidates.getOrNull(index) ?: return false
            if (!open) return false

            request = candidate.toForecastRequest(LocalLocationId(localIdGenerator()))
            generation++
            open = false
            mutableState.value = LocationSearchState.Idle()
        }
        onSelected(request)
        return true
    }

    @Synchronized
    private fun publishIfCurrent(
        requestGeneration: Long,
        query: String,
        result: LocationSearchResult,
    ) {
        if (!open || requestGeneration != generation) return
        mutableState.value = when (result) {
            is LocationSearchResult.Success -> LocationSearchState.Results(query, result.candidates.toList())
            LocationSearchResult.NoResults -> LocationSearchState.NoResults(query)
            is LocationSearchResult.Failure -> LocationSearchState.Failure(query, result.category)
        }
    }

    private fun LocationCandidate.toForecastRequest(localId: LocalLocationId) = ForecastRequest(
        location = WeatherLocation(localId, displayName, timeZone),
        coordinates = GeoCoordinates(latitude, longitude),
        coverage = ForecastCoverage(hourlyHours = 72, dailyDays = 10),
        fields = ForecastField.entries.toSet(),
    )

    private fun LocationSearchState.toPresentation(): LocationSearchPresentation = when (this) {
        is LocationSearchState.Idle -> LocationSearchPresentation.Idle(query)
        is LocationSearchState.Loading -> LocationSearchPresentation.Loading(query)
        is LocationSearchState.Results -> LocationSearchPresentation.Results(
            query,
            candidates.map { candidate ->
                LocationSearchPresentation.Candidate(
                    displayName = candidate.displayName,
                    admin1 = candidate.admin1,
                    admin2 = candidate.admin2,
                    admin3 = candidate.admin3,
                    admin4 = candidate.admin4,
                    country = candidate.country,
                    countryCode = candidate.countryCode,
                    timeZone = candidate.timeZone.id,
                )
            },
        )
        is LocationSearchState.NoResults -> LocationSearchPresentation.NoResults(query)
        is LocationSearchState.Failure -> LocationSearchPresentation.Failure(
            query,
            LocationSearchPresentation.FailureCategory.valueOf(category.name),
        )
    }
}
