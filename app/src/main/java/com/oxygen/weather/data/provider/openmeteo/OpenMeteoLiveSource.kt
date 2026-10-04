package com.oxygen.weather.data.provider.openmeteo

import com.oxygen.weather.data.LiveForecastSource
import com.oxygen.weather.data.LiveWeatherResult
import com.oxygen.weather.data.WeatherSection
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import java.time.Clock

/** Bridges the provider wire result into the provider-neutral live weather boundary. */
class OpenMeteoLiveSource(
    private val adapter: OpenMeteoAdapter,
    private val clock: Clock,
) : LiveForecastSource {
    override fun fetch(request: ForecastRequest): LiveWeatherResult = when (val result = adapter.fetch(request)) {
        is OpenMeteoResult.Success -> {
            val retrievedAt = clock.instant()
            val mapped = OpenMeteoMapper.map(
                result.response,
                request.location,
                retrievedAt,
                result.unsupportedFields,
            )
            val invalidSections = mapped.invalidSections.mapTo(mutableSetOf()) { section ->
                when (section) {
                    OpenMeteoMapping.Section.CURRENT -> WeatherSection.CURRENT
                    OpenMeteoMapping.Section.HOURLY -> WeatherSection.HOURLY
                    OpenMeteoMapping.Section.DAILY -> WeatherSection.DAILY
                }
            }
            when {
                mapped.current != null || mapped.forecast != null -> LiveWeatherResult.Success(
                    request = request,
                    current = mapped.current,
                    currentProvenance = mapped.currentProvenance,
                    forecast = mapped.forecast,
                    source = OpenMeteoMapper.source,
                    retrievedAt = retrievedAt,
                    unsupportedFields = mapped.unsupportedFields,
                    invalidSections = invalidSections,
                )
                invalidSections.isNotEmpty() -> LiveWeatherResult.InvalidMapping(invalidSections)
                mapped.unsupportedFields.isNotEmpty() -> LiveWeatherResult.UnsupportedFields(mapped.unsupportedFields)
                else -> LiveWeatherResult.NoResult
            }
        }
        is OpenMeteoResult.TransportFailure -> LiveWeatherResult.TransportFailure(result.failure)
        is OpenMeteoResult.HttpError -> LiveWeatherResult.TransportFailure(
            ForecastTransportFailure(ForecastTransportFailure.Kind.SERVICE_UNAVAILABLE, clock.instant()),
        )
        is OpenMeteoResult.Malformed -> LiveWeatherResult.InvalidMapping(emptySet())
        OpenMeteoResult.NoData -> LiveWeatherResult.NoResult
    }
}
