package com.oxygen.weather.data.provider.metnorway

import com.oxygen.weather.data.LiveForecastSource
import com.oxygen.weather.data.LiveWeatherResult
import com.oxygen.weather.data.WeatherSection
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import java.time.Clock

/** Bridges the MET Norway wire boundary to a provider-neutral live result. */
class MetNorwayLiveSource(
    private val adapter: MetNorwayAdapter,
    private val clock: Clock,
) : LiveForecastSource {
    override fun fetch(request: ForecastRequest): LiveWeatherResult = when (val result = adapter.fetch(request)) {
        is MetNorwayResult.Success -> {
            val retrievedAt = clock.instant()
            val mapped = MetNorwayMapper.map(
                result.response,
                request,
                retrievedAt,
                result.unsupportedFields,
            )
            val invalidSections = mapped.invalidSections.mapTo(mutableSetOf()) { WeatherSection.HOURLY }
            when {
                mapped.forecast != null -> LiveWeatherResult.Success(
                    request = request,
                    current = null,
                    currentProvenance = null,
                    forecast = mapped.forecast,
                    source = MetNorwayMapper.source,
                    retrievedAt = retrievedAt,
                    unsupportedFields = mapped.unsupportedFields,
                    invalidSections = invalidSections,
                )
                invalidSections.isNotEmpty() -> LiveWeatherResult.InvalidMapping(invalidSections)
                mapped.unsupportedFields.isNotEmpty() ->
                    LiveWeatherResult.UnsupportedFields(mapped.unsupportedFields)
                else -> LiveWeatherResult.NoResult
            }
        }
        is MetNorwayResult.TransportFailure -> LiveWeatherResult.TransportFailure(result.failure)
        is MetNorwayResult.HttpError -> LiveWeatherResult.TransportFailure(
            ForecastTransportFailure(
                ForecastTransportFailure.Kind.SERVICE_UNAVAILABLE,
                clock.instant(),
            ),
        )
        is MetNorwayResult.Malformed -> LiveWeatherResult.InvalidMapping(emptySet())
        MetNorwayResult.NoData -> LiveWeatherResult.NoResult
    }
}
