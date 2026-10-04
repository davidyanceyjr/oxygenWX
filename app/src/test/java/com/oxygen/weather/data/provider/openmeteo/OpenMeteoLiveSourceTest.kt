package com.oxygen.weather.data.provider.openmeteo

import com.oxygen.weather.data.*
import com.oxygen.weather.data.provider.*
import org.junit.Assert.*
import org.junit.Test
import java.net.URI
import java.net.SocketTimeoutException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class OpenMeteoLiveSourceTest {
    private val retrievedAt = Instant.parse("2026-10-03T16:00:00Z")
    private val location = WeatherLocation(LocalLocationId("home"), "Home", ZoneId.of("America/Chicago"))
    private val request = ForecastRequest(location, GeoCoordinates(41.88, -87.63), ForecastCoverage(3, 2), ForecastField.entries.toSet())
    private val endpoint = ForecastEndpoint(URI("https://weather.invalid/v1/forecast"))

    private fun fixture() = javaClass.classLoader!!.getResourceAsStream("openmeteo/complete.json")!!.bufferedReader().use { it.readText() }

    private fun fetch(body: String, request: ForecastRequest = this.request, status: Int = 200): Pair<LiveWeatherResult, Int> {
        var calls = 0
        val adapter = OpenMeteoAdapter(endpoint) {
            calls++
            OpenMeteoHttpResponse(status, body)
        }
        val result = OpenMeteoLiveSource(adapter, Clock.fixed(retrievedAt, ZoneId.of("UTC"))).fetch(request)
        return result to calls
    }

    @Test fun completeResponseComposesCurrentForecastAndProvenanceInOneRequest() {
        val (result, calls) = fetch(fixture())
        assertEquals(1, calls)
        val success = result as LiveWeatherResult.Success
        assertEquals(request, success.request)
        assertEquals(WeatherOrigin.LIVE, success.origin)
        assertEquals(OpenMeteoMapper.source, success.source)
        assertEquals(retrievedAt, success.retrievedAt)
        assertEquals(retrievedAt, success.currentProvenance!!.retrievedAt)
        assertEquals(Instant.parse("2026-10-03T15:00:00Z"), success.currentProvenance.validAt)
        assertEquals(DataType.FORECAST, success.currentProvenance.dataType)
        assertEquals(retrievedAt, success.forecast!!.provenance.retrievedAt)
        assertEquals(location, success.forecast.location)
        assertTrue(success.forecast.hourly.isNotEmpty())
        assertTrue(success.forecast.daily.isNotEmpty())
    }

    @Test fun currentOnlyAndForecastOnlyAreUsablePartialResults() {
        val currentOnly = """{"current":{"time":"2026-10-03T10:00","temperature_2m":12},"current_units":{"temperature_2m":"°C"}}"""
        val current = fetch(currentOnly).first as LiveWeatherResult.Success
        assertNotNull(current.current)
        assertNull(current.forecast)
        assertEquals(retrievedAt, current.currentProvenance!!.retrievedAt)

        val forecastOnly = """{"hourly":{"time":["2026-10-03T10:00"],"temperature_2m":[12]},"hourly_units":{"temperature_2m":"°C"}}"""
        val forecast = fetch(forecastOnly).first as LiveWeatherResult.Success
        assertNull(forecast.current)
        assertNull(forecast.currentProvenance)
        assertEquals(1, forecast.forecast!!.hourly.size)
        assertTrue(forecast.forecast.daily.isEmpty())
    }

    @Test fun invalidSiblingIsReportedWhileUsableCurrentSurvives() {
        val body = """{"current":{"time":"2026-10-03T10:00","temperature_2m":12},"current_units":{"temperature_2m":"°C"},"hourly":{"time":["bad"],"temperature_2m":[12]},"hourly_units":{"temperature_2m":"°C"}}"""
        val result = fetch(body).first as LiveWeatherResult.Success
        assertNotNull(result.current)
        assertNull(result.forecast)
        assertEquals(setOf(WeatherSection.HOURLY), result.invalidSections)
    }

    @Test fun unsupportedOnlyNoResultMalformedAndTransportRemainDistinct() {
        val unsupportedRequest = request.copy(coverage = ForecastCoverage(hourlyHours = 1), fields = setOf(ForecastField.DAILY_LOW))
        val noValues = """{"hourly":{"time":["2026-10-03T10:00"]}}"""
        assertEquals(LiveWeatherResult.UnsupportedFields(setOf(ForecastField.DAILY_LOW)), fetch(noValues, unsupportedRequest).first)
        assertEquals(LiveWeatherResult.NoResult, fetch("{}").first)
        assertEquals(LiveWeatherResult.InvalidMapping(emptySet()), fetch("{bad").first)
        assertEquals(LiveWeatherResult.InvalidMapping(setOf(WeatherSection.HOURLY)), fetch("""{"hourly":{"time":["bad"]}}""").first)
        val http = fetch("private response", status = 503).first as LiveWeatherResult.TransportFailure
        assertEquals(ForecastTransportFailure.Kind.SERVICE_UNAVAILABLE, http.failure.kind)
        assertEquals(retrievedAt, http.failure.occurredAt)
        val adapter = OpenMeteoAdapter(endpoint) { throw SocketTimeoutException("private detail") }
        val timeout = OpenMeteoLiveSource(adapter, Clock.fixed(retrievedAt, ZoneId.of("UTC"))).fetch(request) as LiveWeatherResult.TransportFailure
        assertEquals(ForecastTransportFailure.Kind.TIMEOUT, timeout.failure.kind)
    }
}
