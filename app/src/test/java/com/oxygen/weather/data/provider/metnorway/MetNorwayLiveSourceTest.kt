package com.oxygen.weather.data.provider.metnorway

import com.oxygen.weather.data.LiveWeatherResult
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.WeatherOrigin
import com.oxygen.weather.data.WeatherSection
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import com.oxygen.weather.data.provider.GeoCoordinates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI
import java.net.SocketTimeoutException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class MetNorwayLiveSourceTest {
    private val retrievedAt = Instant.parse("2026-10-03T16:02:00Z")
    private val location = WeatherLocation(
        LocalLocationId("home"),
        "Home",
        java.time.ZoneId.of("America/Chicago"),
    )
    private val request = ForecastRequest(
        location,
        GeoCoordinates(41.88, -87.63),
        ForecastCoverage(hourlyHours = 3, dailyDays = 2),
        ForecastField.entries.toSet(),
    )
    private val endpoint = ForecastEndpoint(URI("https://weather.invalid/compact"))
    private val identification = MetNorwayIdentification(
        "OxygenWeather/1.0",
        "weather@example.invalid",
    )

    @Test fun completeResponseIsForecastOnlyWithMetNorwayIdentityAndRetrievalTime() {
        val (result, calls) = fetch(fixture("complete.json"))
        assertEquals(1, calls)
        val success = result as LiveWeatherResult.Success
        assertEquals(request, success.request)
        assertEquals(WeatherOrigin.LIVE, success.origin)
        assertEquals(MetNorwayMapper.source, success.source)
        assertEquals(retrievedAt, success.retrievedAt)
        assertNull(success.current)
        assertNull(success.currentProvenance)
        assertEquals(MetNorwayMapper.source, success.forecast!!.provenance.source)
        assertEquals(retrievedAt, success.forecast.provenance.retrievedAt)
        assertTrue(success.forecast.hourly.isNotEmpty())
        assertTrue(success.forecast.daily.isEmpty())
        assertEquals(
            setOf(
                ForecastField.DEW_POINT,
                ForecastField.PRECIPITATION_PROBABILITY,
                ForecastField.DAILY_LOW,
                ForecastField.DAILY_HIGH,
                ForecastField.DAILY_WIND_GUST,
                ForecastField.DAILY_SUNSHINE_HOURS,
            ),
            success.unsupportedFields,
        )
    }

    @Test fun dailyOnlyIsUnsupportedRatherThanManufacturedFromHourlyRows() {
        val daily = request.copy(
            coverage = ForecastCoverage(dailyDays = 2),
            fields = setOf(ForecastField.TEMPERATURE, ForecastField.DAILY_HIGH),
        )
        assertEquals(
            LiveWeatherResult.UnsupportedFields(daily.fields),
            fetch(fixture("complete.json"), daily).first,
        )
    }

    @Test fun unsupportedOnlyHourlyRequestDoesNotUseUnrequestedCompactFacts() {
        val unsupportedOnly = request.copy(
            coverage = ForecastCoverage(hourlyHours = 3),
            fields = setOf(ForecastField.DEW_POINT),
        )
        assertEquals(
            LiveWeatherResult.UnsupportedFields(unsupportedOnly.fields),
            fetch(fixture("complete.json"), unsupportedOnly).first,
        )
    }

    @Test fun noDataMalformedAndInvalidChronologyRemainDistinct() {
        assertEquals(LiveWeatherResult.NoResult, fetch(fixture("no_data.json")).first)
        assertEquals(LiveWeatherResult.InvalidMapping(emptySet()), fetch("{bad").first)
        val badTime = """{
          "properties": {
            "meta": {"units": {"air_temperature": "celsius"}},
            "timeseries": [{"time": "bad", "data": {"instant": {"details": {"air_temperature": 2}}}}]
          }
        }"""
        assertEquals(
            LiveWeatherResult.InvalidMapping(setOf(WeatherSection.HOURLY)),
            fetch(badTime).first,
        )
    }

    @Test fun httpAndTransportFailuresUseSafeTypedVocabularyAndInjectedClock() {
        val http = fetch("private response body", status = 503).first
            as LiveWeatherResult.TransportFailure
        assertEquals(ForecastTransportFailure.Kind.SERVICE_UNAVAILABLE, http.failure.kind)
        assertEquals(retrievedAt, http.failure.occurredAt)

        val adapter = MetNorwayAdapter(endpoint, identification) {
            throw SocketTimeoutException("private detail")
        }
        val timeout = MetNorwayLiveSource(
            adapter,
            Clock.fixed(retrievedAt, ZoneOffset.UTC),
        ).fetch(request) as LiveWeatherResult.TransportFailure
        assertEquals(ForecastTransportFailure.Kind.TIMEOUT, timeout.failure.kind)
        assertNull(timeout.failure.occurredAt)
    }

    private fun fetch(
        body: String,
        request: ForecastRequest = this.request,
        status: Int = 200,
    ): Pair<LiveWeatherResult, Int> {
        var calls = 0
        val adapter = MetNorwayAdapter(endpoint, identification) {
            calls++
            MetNorwayHttpResponse(status, body)
        }
        val result = MetNorwayLiveSource(
            adapter,
            Clock.fixed(retrievedAt, ZoneOffset.UTC),
        ).fetch(request)
        return result to calls
    }

    private fun fixture(name: String) =
        javaClass.classLoader!!.getResourceAsStream("metnorway/$name")!!.bufferedReader().use { it.readText() }
}
