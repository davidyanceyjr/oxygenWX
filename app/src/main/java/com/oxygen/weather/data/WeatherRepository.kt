package com.oxygen.weather.data

import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import java.time.Instant

/** The origin of a usable repository result. Cache origin is added with cache support. */
enum class WeatherOrigin { LIVE }

enum class WeatherSection { CURRENT, HOURLY, DAILY }

sealed interface LiveWeatherResult {
    data class Success(
        val request: ForecastRequest,
        val current: CurrentWeather?,
        val currentProvenance: DataProvenance?,
        val forecast: ForecastData?,
        val source: WeatherSource,
        val retrievedAt: Instant,
        val unsupportedFields: Set<ForecastField> = emptySet(),
        val invalidSections: Set<WeatherSection> = emptySet(),
        val origin: WeatherOrigin = WeatherOrigin.LIVE,
    ) : LiveWeatherResult {
        init {
            require(current != null || forecast != null)
            require((current == null) == (currentProvenance == null))
            require(forecast == null || forecast.location == request.location)
        }
    }

    data class UnsupportedFields(val fields: Set<ForecastField>) : LiveWeatherResult {
        init { require(fields.isNotEmpty()) }
    }
    data object NoResult : LiveWeatherResult
    data class TransportFailure(val failure: ForecastTransportFailure) : LiveWeatherResult
    data class InvalidMapping(val invalidSections: Set<WeatherSection>) : LiveWeatherResult
}

/** A configured source produces canonical weather, without exposing provider wire types. */
fun interface LiveForecastSource {
    fun fetch(request: ForecastRequest): LiveWeatherResult
}

interface WeatherRepository {
    fun fetchLive(request: ForecastRequest): LiveWeatherResult
}

/** No retries, fallback, state, or cache are part of this live composition seam. */
class LiveWeatherRepository(private val source: LiveForecastSource) : WeatherRepository {
    override fun fetchLive(request: ForecastRequest): LiveWeatherResult = source.fetch(request)
}
