package com.oxygen.weather.data

import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LiveWeatherSourceCompositionTest {
    private val location = WeatherLocation(
        LocalLocationId("local-chicago"),
        "Chicago",
        ZoneId.of("America/Chicago"),
    )
    private val request = ForecastRequest(
        location = location,
        coordinates = GeoCoordinates(41.88, -87.63),
        coverage = ForecastCoverage(hourlyHours = 24, dailyDays = 3),
        fields = setOf(ForecastField.TEMPERATURE, ForecastField.PRESSURE),
    )
    private val primarySource = WeatherSource(WeatherSourceId("primary"), "Primary")
    private val fallbackSource = WeatherSource(WeatherSourceId("fallback"), "Fallback")
    private val primaryRetrievedAt = Instant.parse("2026-10-04T12:00:00Z")
    private val fallbackRetrievedAt = Instant.parse("2026-10-04T12:01:00Z")

    @Test fun everyTransportFailureKindUsesTheExplicitFailClosedEligibilityPolicy() {
        val expectedEligibility = mapOf(
            ForecastTransportFailure.Kind.NETWORK to true,
            ForecastTransportFailure.Kind.TIMEOUT to true,
            ForecastTransportFailure.Kind.SERVICE_UNAVAILABLE to true,
            ForecastTransportFailure.Kind.UNKNOWN to false,
        )
        assertEquals(ForecastTransportFailure.Kind.entries.toSet(), expectedEligibility.keys)

        ForecastTransportFailure.Kind.entries.forEach { kind ->
            val primaryResult = transportFailure(kind, primaryRetrievedAt)
            val fallbackResult = fallbackSuccess()
            val execution = execute(primaryResult, fallbackResult)

            assertEquals("primary calls for $kind", 1, execution.primaryCalls)
            assertEquals("fallback calls for $kind", if (expectedEligibility.getValue(kind)) 1 else 0,
                execution.fallbackCalls)
            assertSame(
                "returned result for $kind",
                if (expectedEligibility.getValue(kind)) fallbackResult else primaryResult,
                execution.result,
            )
        }
    }

    @Test fun everyNonTransportResultIncludingUsablePartialSuccessSkipsFallback() {
        val partialSuccess = primarySuccess().copy(
            unsupportedFields = setOf(ForecastField.PRESSURE),
            invalidSections = setOf(WeatherSection.DAILY),
        )
        val primaryResults = listOf(
            primarySuccess(),
            partialSuccess,
            LiveWeatherResult.UnsupportedFields(setOf(ForecastField.PRESSURE)),
            LiveWeatherResult.NoResult,
            LiveWeatherResult.InvalidMapping(setOf(WeatherSection.HOURLY)),
        )

        primaryResults.forEach { primaryResult ->
            val execution = execute(primaryResult, fallbackSuccess())
            assertEquals("primary calls for $primaryResult", 1, execution.primaryCalls)
            assertEquals("fallback calls for $primaryResult", 0, execution.fallbackCalls)
            assertSame(primaryResult, execution.result)
        }
    }

    @Test fun eligibleFailureReturnsTheCompleteFallbackSuccessWithoutProviderBlending() {
        val fallbackResult = fallbackSuccess()
        val execution = execute(
            transportFailure(ForecastTransportFailure.Kind.NETWORK, primaryRetrievedAt),
            fallbackResult,
        )

        assertSame(fallbackResult, execution.result)
        val result = execution.result as LiveWeatherResult.Success
        assertSame(request, result.request)
        assertSame(fallbackSource, result.source)
        assertSame(fallbackResult.current, result.current)
        assertSame(fallbackResult.currentProvenance, result.currentProvenance)
        assertSame(fallbackResult.forecast, result.forecast)
        assertEquals(fallbackRetrievedAt, result.retrievedAt)
        assertEquals(fallbackSource, result.currentProvenance?.source)
        assertEquals(fallbackSource, result.forecast?.provenance?.source)
        assertEquals(setOf(ForecastField.PRESSURE), result.unsupportedFields)
        assertEquals(setOf(WeatherSection.DAILY), result.invalidSections)
        assertEquals(1, execution.primaryCalls)
        assertEquals(1, execution.fallbackCalls)
    }

    @Test fun everyFallbackFailureIsReturnedAsItsExactTypedOutcomeWithoutRetry() {
        val fallbackFailure = ForecastTransportFailure(
            ForecastTransportFailure.Kind.TIMEOUT,
            fallbackRetrievedAt,
        )
        val fallbackResults = listOf(
            LiveWeatherResult.TransportFailure(fallbackFailure),
            LiveWeatherResult.UnsupportedFields(setOf(ForecastField.PRESSURE)),
            LiveWeatherResult.NoResult,
            LiveWeatherResult.InvalidMapping(setOf(WeatherSection.CURRENT, WeatherSection.DAILY)),
        )

        fallbackResults.forEach { fallbackResult ->
            val execution = execute(
                transportFailure(ForecastTransportFailure.Kind.SERVICE_UNAVAILABLE, primaryRetrievedAt),
                fallbackResult,
            )
            assertEquals(1, execution.primaryCalls)
            assertEquals(1, execution.fallbackCalls)
            assertSame(fallbackResult, execution.result)
        }
    }

    @Test fun oneSourceCompatibilityConstructorStillDelegatesExactlyOnce() {
        var calls = 0
        val expected = primarySuccess()
        val repository = LiveWeatherRepository { received ->
            calls++
            assertSame(request, received)
            expected
        }

        assertSame(expected, repository.fetchLive(request))
        assertEquals(1, calls)
    }

    private fun execute(
        primaryResult: LiveWeatherResult,
        fallbackResult: LiveWeatherResult,
    ): Execution {
        var primaryCalls = 0
        var fallbackCalls = 0
        val source = FallbackLiveForecastSource(
            primary = LiveForecastSource { received ->
                primaryCalls++
                assertSame(request, received)
                primaryResult
            },
            fallback = LiveForecastSource { received ->
                fallbackCalls++
                assertSame(request, received)
                fallbackResult
            },
        )
        val result = source.fetch(request)
        return Execution(result, primaryCalls, fallbackCalls)
    }

    private fun primarySuccess() = success(
        source = primarySource,
        temperatureC = 31.0,
        retrievedAt = primaryRetrievedAt,
    )

    private fun fallbackSuccess() = success(
        source = fallbackSource,
        temperatureC = 7.0,
        retrievedAt = fallbackRetrievedAt,
    ).copy(
        unsupportedFields = setOf(ForecastField.PRESSURE),
        invalidSections = setOf(WeatherSection.DAILY),
    )

    private fun success(
        source: WeatherSource,
        temperatureC: Double,
        retrievedAt: Instant,
    ): LiveWeatherResult.Success {
        val validAt = retrievedAt.minusSeconds(60)
        val current = CurrentWeather(
            observedAt = LocalDateTime.of(2026, 10, 4, 7, 0),
            condition = WeatherCondition.CLEAR,
            temperatureC = temperatureC,
            apparentC = null,
            dewPointC = null,
            relativeHumidityPct = null,
            pressureHpa = null,
            windSpeedKph = null,
            windGustKph = null,
            windDirectionDeg = null,
            cloudCoverPct = null,
            visibilityKm = null,
            precipitationMmPerHr = null,
        )
        val currentProvenance = DataProvenance(DataType.MODEL_ESTIMATE, source, validAt, retrievedAt)
        val forecast = ForecastData(
            location = location,
            hourly = listOf(
                HourWeather(
                    time = LocalDateTime.of(2026, 10, 4, 8, 0),
                    condition = null,
                    temperatureC = temperatureC + 1.0,
                    dewPointC = null,
                    pressureHpa = null,
                    windSpeedKph = null,
                    precipitationProbabilityPct = null,
                    precipitationMm = null,
                    cloudCoverPct = null,
                ),
            ),
            daily = emptyList(),
            provenance = DataProvenance(DataType.FORECAST, source, validAt, retrievedAt),
        )
        return LiveWeatherResult.Success(
            request = request,
            current = current,
            currentProvenance = currentProvenance,
            forecast = forecast,
            source = source,
            retrievedAt = retrievedAt,
        )
    }

    private fun transportFailure(
        kind: ForecastTransportFailure.Kind,
        occurredAt: Instant,
    ) = LiveWeatherResult.TransportFailure(ForecastTransportFailure(kind, occurredAt))

    private data class Execution(
        val result: LiveWeatherResult,
        val primaryCalls: Int,
        val fallbackCalls: Int,
    )
}
