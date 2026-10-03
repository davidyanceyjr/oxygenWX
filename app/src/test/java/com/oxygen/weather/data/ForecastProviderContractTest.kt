package com.oxygen.weather.data

import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastProvider
import com.oxygen.weather.data.provider.ForecastProviderResult
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import com.oxygen.weather.data.provider.GeoCoordinates
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ForecastProviderContractTest {
    @Test
    fun requestPreservesOpaqueLocationAndRequiresValidCoordinatesCoverageAndFields() {
        val request = request()

        assertEquals(LocalLocationId("local-location-opaque"), request.location.id)
        assertEquals(ZoneId.of("America/Chicago"), request.location.timeZone)
        assertEquals(41.88, request.coordinates.latitude, 0.0)
        assertEquals(72, request.coverage.hourlyHours)
        assertEquals(10, request.coverage.dailyDays)
        assertEquals(setOf(ForecastField.TEMPERATURE, ForecastField.CONDITION), request.fields)

        assertThrows(IllegalArgumentException::class.java) { GeoCoordinates(Double.NaN, 0.0) }
        assertThrows(IllegalArgumentException::class.java) { GeoCoordinates(0.0, Double.POSITIVE_INFINITY) }
        assertThrows(IllegalArgumentException::class.java) { GeoCoordinates(-90.01, 0.0) }
        assertThrows(IllegalArgumentException::class.java) { GeoCoordinates(0.0, 180.01) }
        assertThrows(IllegalArgumentException::class.java) { ForecastCoverage() }
        assertThrows(IllegalArgumentException::class.java) { ForecastCoverage(hourlyHours = 0) }
        assertThrows(IllegalArgumentException::class.java) { ForecastCoverage(hourlyHours = 73) }
        assertThrows(IllegalArgumentException::class.java) { ForecastCoverage(dailyDays = -1) }
        assertThrows(IllegalArgumentException::class.java) { ForecastCoverage(dailyDays = 11) }
        assertThrows(IllegalArgumentException::class.java) { request(fields = emptySet()) }
    }

    @Test
    fun endpointRequiresExplicitAbsoluteSecureOrLocalTestConfiguration() {
        assertEquals(URI("https://weather.example/api"), ForecastEndpoint(URI("https://weather.example/api")).uri)
        val localEndpoint = ForecastEndpoint(URI("http://localhost:8080/forecast"), allowInsecureLocalTesting = true)
        assertEquals("http", localEndpoint.uri.scheme)
        assertThrows(IllegalArgumentException::class.java) { ForecastEndpoint(URI("/forecast")) }
        assertThrows(IllegalArgumentException::class.java) { ForecastEndpoint(URI("file:///tmp/forecast")) }
        assertThrows(IllegalArgumentException::class.java) { ForecastEndpoint(URI("http:///missing-host")) }
        assertThrows(IllegalArgumentException::class.java) { ForecastEndpoint(URI("https://user:secret@weather.example/api")) }
        assertThrows(IllegalArgumentException::class.java) { ForecastEndpoint(URI("https://weather.example/api#fragment")) }
        assertThrows(IllegalArgumentException::class.java) { ForecastEndpoint(URI("https://weather.example/api?key=secret")) }
        assertThrows(IllegalArgumentException::class.java) { ForecastEndpoint(URI("http://weather.example/api")) }
        assertThrows(IllegalArgumentException::class.java) {
            ForecastEndpoint(URI("http://weather.example/api"), allowInsecureLocalTesting = true)
        }
    }

    @Test
    fun providerReceivesInjectedEndpointAndRequest() {
        val endpoint = ForecastEndpoint(URI("https://weather.example/forecast"))
        val expectedRequest = request()
        val expectedResult = ForecastProviderResult.NoResult
        val provider = object : ForecastProvider {
            override val endpoint = endpoint
            override fun fetch(request: ForecastRequest): ForecastProviderResult {
                assertSame(expectedRequest, request)
                return expectedResult
            }
        }

        assertSame(endpoint, provider.endpoint)
        assertSame(expectedResult, provider.fetch(expectedRequest))
    }

    @Test
    fun successPreservesForecastValuesLocationChronologyAndProvenanceWithoutCurrentOrBaseline() {
        val retrievedAt = Instant.parse("2026-10-03T15:05:00Z")
        val validAt = Instant.parse("2026-10-03T15:00:00Z")
        val source = WeatherSource(WeatherSourceId("provider-neutral-source"), "Forecast source")
        val provenance = DataProvenance(
            dataType = DataType.FORECAST,
            source = source,
            validAt = validAt,
            retrievedAt = retrievedAt,
        )
        val location = WeatherLocation(
            id = LocalLocationId("local-location-opaque"),
            displayName = "Chicago",
            timeZone = ZoneId.of("America/Chicago"),
        )
        val first = HourWeather(
            time = LocalDateTime.of(2026, 10, 3, 10, 0),
            condition = null,
            temperatureC = null,
            dewPointC = null,
            pressureHpa = null,
            windSpeedKph = null,
            precipitationProbabilityPct = null,
            precipitationMm = null,
            cloudCoverPct = null,
        )
        val usableDuplicate = first.copy(temperatureC = 12.5)
        val daily = DayWeather(
            date = LocalDate.of(2026, 10, 3),
            condition = WeatherCondition.PARTLY_CLOUDY,
            lowC = null,
            highC = 18.0,
            precipitationProbabilityPct = null,
            precipitationMm = null,
            windGustKph = null,
            sunshineHours = null,
        )
        val forecast = ForecastData(location, listOf(first, usableDuplicate), listOf(daily), provenance)
        val result = ForecastProviderResult.Success(forecast)

        assertEquals(location, forecast.location)
        assertEquals(ZoneId.of("America/Chicago"), forecast.location.timeZone)
        assertEquals(listOf(first, usableDuplicate), forecast.hourly)
        assertEquals(listOf(daily), forecast.daily)
        assertNull(forecast.hourly.first().temperatureC)
        assertEquals(12.5, requireNotNull(forecast.hourly[1].temperatureC), 0.0)
        assertSame(source, forecast.provenance.source)
        assertEquals(validAt, forecast.provenance.validAt)
        assertEquals(retrievedAt, forecast.provenance.retrievedAt)
        assertSame(forecast, result.forecast)
        assertTrue(ForecastProviderResult.Success(forecast, setOf(ForecastField.PRESSURE))
            .unsupportedFields.contains(ForecastField.PRESSURE))
        assertThrows(IllegalArgumentException::class.java) {
            ForecastData(location, listOf(first, first.copy(time = first.time.minusHours(1))), emptyList(), provenance)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ForecastData(location, emptyList(), listOf(daily, daily.copy(date = daily.date.minusDays(1))), provenance)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ForecastData(location, listOf(first), emptyList(), provenance)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ForecastData(location, listOf(usableDuplicate), emptyList(), provenance.copy(dataType = DataType.OBSERVATION))
        }
    }

    @Test
    fun unsupportedFieldsNoResultAndTransportFailureAreDistinct() {
        val unsupported = ForecastProviderResult.UnsupportedFields(setOf(ForecastField.PRESSURE))
        val noResult = ForecastProviderResult.NoResult
        val failure = ForecastProviderResult.TransportFailure(
            ForecastTransportFailure(ForecastTransportFailure.Kind.TIMEOUT, Instant.parse("2026-10-03T15:05:00Z")),
        )

        assertEquals(setOf(ForecastField.PRESSURE), unsupported.fields)
        assertEquals(ForecastTransportFailure.Kind.TIMEOUT, failure.failure.kind)
        assertFalse(unsupported == noResult)
        assertFalse(noResult == failure)
        assertThrows(IllegalArgumentException::class.java) {
            ForecastProviderResult.UnsupportedFields(emptySet())
        }
    }

    private fun request(
        location: WeatherLocation = WeatherLocation(
            id = LocalLocationId("local-location-opaque"),
            displayName = "Chicago",
            timeZone = ZoneId.of("America/Chicago"),
        ),
        coverage: ForecastCoverage = ForecastCoverage(hourlyHours = 72, dailyDays = 10),
        fields: Set<ForecastField> = setOf(ForecastField.TEMPERATURE, ForecastField.CONDITION),
    ) = ForecastRequest(
        location = location,
        coordinates = GeoCoordinates(41.88, -87.63),
        coverage = coverage,
        fields = fields,
    )
}
