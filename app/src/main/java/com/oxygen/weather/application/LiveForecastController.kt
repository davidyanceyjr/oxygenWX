package com.oxygen.weather.application

import com.oxygen.weather.data.LiveWeatherResult
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.WeatherRepository
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.LiveWeatherPresentation
import java.util.concurrent.Executor
import java.time.Instant

enum class LiveFetchFailureKind { UNSUPPORTED_FIELDS, NO_RESULT, TRANSPORT, INVALID_MAPPING, UNEXPECTED }

sealed interface LiveForecastState {
    val generation: Long
    val request: ForecastRequest

    data class Loading(override val generation: Long, override val request: ForecastRequest) : LiveForecastState
    data class Loaded(
        override val generation: Long,
        override val request: ForecastRequest,
        val result: LiveWeatherResult.Success,
        val presentation: LiveWeatherPresentation,
    ) : LiveForecastState
    /** A restored normalized forecast while the live refresh for this request continues. */
    data class Cached(
        override val generation: Long,
        override val request: ForecastRequest,
        val forecast: ForecastData,
        val cachedAt: Instant,
    ) : LiveForecastState
    data class Failed(
        override val generation: Long,
        override val request: ForecastRequest,
        val kind: LiveFetchFailureKind,
        val status: String,
    ) : LiveForecastState
}

/** Application-owned live request arbitration. The supplied executor controls where blocking fetches run. */
class LiveForecastController(
    private val repository: WeatherRepository,
    private val executor: Executor,
    private val onStateChanged: (LiveForecastState) -> Unit = {},
    private val cacheStore: ForecastCacheStore? = null,
) {
    private val lock = Any()
    private var nextGeneration = 0L
    @Volatile private var currentState: LiveForecastState? = null

    fun state(): LiveForecastState? = currentState

    fun fetch(request: ForecastRequest): Long {
        val loading = synchronized(lock) {
            val generation = ++nextGeneration
            LiveForecastState.Loading(generation, request).also { currentState = it }
        }
        synchronized(lock) {
            if (currentState?.generation == loading.generation) onStateChanged(loading)
        }
        try {
            executor.execute {
                val matchingCache = try {
                    cacheStore?.read(request.location.id)
                        ?.let { outcome ->
                            val record = (outcome as? ForecastCacheReadResult.Found)?.record
                            record?.takeIf { it.forecast.location.id == request.location.id }
                        }
                } catch (_: Exception) {
                    null
                }
                if (matchingCache != null) {
                    complete(
                        loading.generation,
                        LiveForecastState.Cached(
                            loading.generation,
                            request,
                            matchingCache.forecast,
                            matchingCache.cachedAt,
                        ),
                    )
                }
                if (!isCurrent(loading.generation)) return@execute
                val result = try {
                    repository.fetchLive(request)
                } catch (_: Exception) {
                    complete(loading.generation, failure(loading.generation, request, LiveFetchFailureKind.UNEXPECTED))
                    return@execute
                }
                val state = when (result) {
                    is LiveWeatherResult.Success -> if (result.request != request) {
                        failure(loading.generation, request, LiveFetchFailureKind.UNEXPECTED)
                    } else {
                        if (result.forecast != null && isCurrent(loading.generation)) {
                            try {
                                cacheStore?.write(result.forecast, request.coordinates)
                            } catch (_: Exception) {
                                // Cache persistence is opportunistic; live success remains authoritative.
                            }
                        }
                        LiveForecastState.Loaded(
                            loading.generation,
                            request,
                            result,
                            HomePresentationMapper.mapLiveSuccess(result),
                        )
                    }
                    is LiveWeatherResult.UnsupportedFields -> failure(loading.generation, request, LiveFetchFailureKind.UNSUPPORTED_FIELDS)
                    LiveWeatherResult.NoResult -> failure(loading.generation, request, LiveFetchFailureKind.NO_RESULT)
                    is LiveWeatherResult.TransportFailure -> failure(loading.generation, request, LiveFetchFailureKind.TRANSPORT)
                    is LiveWeatherResult.InvalidMapping -> failure(loading.generation, request, LiveFetchFailureKind.INVALID_MAPPING)
                }
                complete(loading.generation, state)
            }
        } catch (_: Exception) {
            complete(loading.generation, failure(loading.generation, request, LiveFetchFailureKind.UNEXPECTED))
        }
        return loading.generation
    }

    private fun complete(generation: Long, state: LiveForecastState) {
        synchronized(lock) {
            if (currentState?.generation == generation) {
                currentState = state
                onStateChanged(state)
            }
        }
    }

    private fun isCurrent(generation: Long): Boolean = currentState?.generation == generation

    private fun failure(generation: Long, request: ForecastRequest, kind: LiveFetchFailureKind) =
        LiveForecastState.Failed(generation, request, kind, kind.safeStatus())
}

private fun LiveFetchFailureKind.safeStatus(): String = when (this) {
    LiveFetchFailureKind.UNSUPPORTED_FIELDS -> "Requested weather fields are unavailable."
    LiveFetchFailureKind.NO_RESULT -> "No weather data was returned."
    LiveFetchFailureKind.TRANSPORT -> "Weather source could not be reached."
    LiveFetchFailureKind.INVALID_MAPPING -> "Weather data could not be interpreted."
    LiveFetchFailureKind.UNEXPECTED -> "Weather data could not be loaded."
}
