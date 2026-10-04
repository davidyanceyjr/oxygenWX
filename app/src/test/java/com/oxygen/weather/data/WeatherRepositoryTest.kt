package com.oxygen.weather.data

import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class WeatherRepositoryTest {
    private val location = WeatherLocation(LocalLocationId("local-chicago"), "Chicago", ZoneId.of("America/Chicago"))
    private val request = ForecastRequest(
        location, GeoCoordinates(41.88, -87.63), ForecastCoverage(hourlyHours = 24, dailyDays = 3),
        setOf(ForecastField.TEMPERATURE, ForecastField.PRESSURE),
    )
    private val source = WeatherSource(WeatherSourceId("forecast-source"), "Forecast source")
    private val validAt = Instant.parse("2026-10-03T15:00:00Z")
    private val retrievedAt = Instant.parse("2026-10-03T15:05:00Z")
    private val current = CurrentWeather(
        LocalDateTime.of(2026, 10, 3, 10, 0), null, 13.5, null, null, null,
        null, null, null, null, null, null, null,
    )
    private val currentProvenance = DataProvenance(DataType.MODEL_ESTIMATE, source, validAt, retrievedAt)
    private val sparseHour = HourWeather(
        LocalDateTime.of(2026, 10, 3, 11, 0), null, 14.0, null, null, null, null, null, null,
    )
    private val sparseDay = DayWeather(LocalDate.of(2026, 10, 4), null, null, 18.0, null, null, null, null)
    private val forecast = ForecastData(
        location, listOf(sparseHour), listOf(sparseDay),
        DataProvenance(DataType.FORECAST, source, validAt, retrievedAt),
    )

    @Test fun completeSuccessPreservesExactRequestAndCanonicalFactsWithOneSourceCall() {
        val expected = success(current, currentProvenance, forecast)
        val actual = fetchOnce(expected)

        assertSame(expected, actual)
        val result = actual as LiveWeatherResult.Success
        assertSame(request, result.request)
        assertSame(current, result.current)
        assertSame(currentProvenance, result.currentProvenance)
        assertSame(forecast, result.forecast)
        assertSame(source, result.source)
        assertEquals(WeatherOrigin.LIVE, result.origin)
        assertEquals(retrievedAt, result.retrievedAt)
        assertEquals(location, result.forecast?.location)
        assertEquals(ZoneId.of("America/Chicago"), result.forecast?.location?.timeZone)
        assertEquals(validAt, result.forecast?.provenance?.validAt)
        assertEquals(retrievedAt, result.forecast?.provenance?.retrievedAt)
        assertEquals(DataType.MODEL_ESTIMATE, result.currentProvenance?.dataType)
        assertNull(result.current?.pressureHpa)
        assertNull(result.forecast?.hourly?.single()?.pressureHpa)
        assertNull(result.forecast?.daily?.single()?.lowC)
    }

    @Test fun currentOnlySuccessDoesNotInventForecast() {
        val result = fetchOnce(success(current, currentProvenance, null, setOf(WeatherSection.HOURLY)))
            as LiveWeatherResult.Success
        assertSame(current, result.current)
        assertSame(currentProvenance, result.currentProvenance)
        assertNull(result.forecast)
        assertEquals(setOf(WeatherSection.HOURLY), result.invalidSections)
    }

    @Test fun forecastOnlySuccessDoesNotInventCurrent() {
        val result = fetchOnce(success(null, null, forecast, setOf(WeatherSection.CURRENT)))
            as LiveWeatherResult.Success
        assertNull(result.current)
        assertNull(result.currentProvenance)
        assertSame(forecast, result.forecast)
        assertEquals(setOf(WeatherSection.CURRENT), result.invalidSections)
        assertEquals(listOf(sparseHour), result.forecast?.hourly)
        assertEquals(listOf(sparseDay), result.forecast?.daily)
    }

    @Test fun partialSuccessRetainsUnsupportedFieldsAlongsideUsableData() {
        val expected = success(current, currentProvenance, forecast).copy(
            unsupportedFields = setOf(ForecastField.PRESSURE),
        )
        val result = fetchOnce(expected) as LiveWeatherResult.Success
        assertEquals(setOf(ForecastField.PRESSURE), result.unsupportedFields)
        assertSame(forecast, result.forecast)
    }

    @Test fun unusableOutcomesRemainDistinctAndSafe() {
        val failure = ForecastTransportFailure(ForecastTransportFailure.Kind.TIMEOUT, retrievedAt)
        val outcomes = listOf(
            LiveWeatherResult.UnsupportedFields(setOf(ForecastField.PRESSURE)),
            LiveWeatherResult.NoResult,
            LiveWeatherResult.TransportFailure(failure),
            LiveWeatherResult.InvalidMapping(setOf(WeatherSection.CURRENT, WeatherSection.DAILY)),
        )
        outcomes.forEach { assertSame(it, fetchOnce(it)) }
        assertEquals(setOf(ForecastField.PRESSURE), (outcomes[0] as LiveWeatherResult.UnsupportedFields).fields)
        assertSame(failure, (outcomes[2] as LiveWeatherResult.TransportFailure).failure)
        assertEquals(setOf(WeatherSection.CURRENT, WeatherSection.DAILY),
            (outcomes[3] as LiveWeatherResult.InvalidMapping).invalidSections)
    }

    @Test fun successRejectsAbsentDataOrInconsistentLocationAndProvenance() {
        assertThrows(IllegalArgumentException::class.java) { success(null, null, null) }
        assertThrows(IllegalArgumentException::class.java) { success(current, null, null) }
        assertThrows(IllegalArgumentException::class.java) { success(null, currentProvenance, forecast) }
        val wrongLocation = location.copy(id = LocalLocationId("other"))
        val wrongForecast = forecast.copy(location = wrongLocation)
        assertThrows(IllegalArgumentException::class.java) { success(null, null, wrongForecast) }
    }

    private fun success(
        current: CurrentWeather?, provenance: DataProvenance?, forecast: ForecastData?,
        invalidSections: Set<WeatherSection> = emptySet(),
    ) = LiveWeatherResult.Success(
        request, current, provenance, forecast, source, retrievedAt,
        invalidSections = invalidSections,
    )

    private fun fetchOnce(expected: LiveWeatherResult): LiveWeatherResult {
        var calls = 0
        val repository: WeatherRepository = LiveWeatherRepository { received ->
            calls++
            assertSame(request, received)
            expected
        }
        val actual = repository.fetchLive(request)
        assertEquals(1, calls)
        return actual
    }
}
