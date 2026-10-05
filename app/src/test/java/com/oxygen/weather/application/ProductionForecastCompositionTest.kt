package com.oxygen.weather.application

import com.oxygen.weather.data.WeatherSource
import com.oxygen.weather.data.WeatherSourceId
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoMapper
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.LocalLocationId
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.ArrayDeque
import java.util.concurrent.Executor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionForecastCompositionTest {
    private val instant = Instant.parse("2026-10-04T15:00:00Z")
    private val request = ForecastRequest(
        WeatherLocation(LocalLocationId("selected-local-id"), "Selected", ZoneId.of("America/Chicago")),
        GeoCoordinates(41.8819, -87.6278),
        ForecastCoverage(hourlyHours = 72, dailyDays = 10),
        ForecastField.entries.toSet(),
    )

    @Test fun openMeteoCompositionPassesRequestThroughAndPublishesLiveSuccess() {
        val executor = QueueExecutor()
        val events = mutableListOf<LiveForecastState>()
        val calls = mutableListOf<URI>()
        val transport = OpenMeteoTransport { uri ->
            calls += uri
            OpenMeteoHttpResponse(
                200,
                """{"timezone":"America/Chicago","current":{"time":"2026-10-04T10:00","temperature_2m":12},"current_units":{"temperature_2m":"°C"}}""",
            )
        }
        val controller = ProductionForecastComposition.create(
            endpoint = ForecastEndpoint(URI("https://weather.invalid/v1/forecast")),
            transport = transport,
            clock = Clock.fixed(instant, ZoneId.of("UTC")),
            executor = executor,
            onStateChanged = events::add,
        )

        val generation = controller.fetch(request)
        assertEquals(LiveForecastState.Loading(generation, request), controller.state())
        assertEquals(listOf(LiveForecastState.Loading(generation, request)), events)
        executor.runNext()

        val loaded = controller.state() as LiveForecastState.Loaded
        assertEquals(request, loaded.request)
        assertEquals(request, loaded.result.request)
        assertEquals(WeatherSource(WeatherSourceId("open-meteo"), "Open-Meteo"), loaded.result.source)
        assertEquals(OpenMeteoMapper.source, loaded.result.source)
        assertEquals(instant, loaded.result.retrievedAt)
        assertEquals(request.location.id.value, loaded.presentation.locationId)
        assertEquals(listOf(URI("https://weather.invalid/v1/forecast?latitude=41.8819&longitude=-87.6278&current=weather_code%2Ctemperature_2m%2Cdew_point_2m%2Cpressure_msl%2Cwind_speed_10m%2Cprecipitation%2Ccloud_cover&hourly=weather_code%2Ctemperature_2m%2Cdew_point_2m%2Cpressure_msl%2Cwind_speed_10m%2Cprecipitation_probability%2Cprecipitation%2Ccloud_cover&daily=weather_code%2Cprecipitation_probability_max%2Cprecipitation_sum%2Ctemperature_2m_min%2Ctemperature_2m_max%2Cwind_gusts_10m_max%2Csunshine_duration&temperature_unit=celsius&wind_speed_unit=kmh&precipitation_unit=mm&timeformat=iso8601&timezone=America%2FChicago&forecast_hours=72&forecast_days=10")), calls)
        assertEquals(2, events.size)
        assertEquals(loaded, events.last())
    }

    @Test fun openMeteoFailureRemainsTruthfulAndCompositionHasNoMetSource() {
        val executor = QueueExecutor()
        val events = mutableListOf<LiveForecastState>()
        var transportCalls = 0
        val transport = OpenMeteoTransport {
            transportCalls++
            OpenMeteoHttpResponse(503, "private provider body")
        }
        val controller = ProductionForecastComposition.create(
            endpoint = ForecastEndpoint(URI("https://weather.invalid/v1/forecast")),
            transport = transport,
            clock = Clock.fixed(instant, ZoneId.of("UTC")),
            executor = executor,
            onStateChanged = events::add,
        )

        val generation = controller.fetch(request)
        assertEquals(LiveForecastState.Loading(generation, request), controller.state())
        executor.runNext()

        val failed = controller.state() as LiveForecastState.Failed
        assertEquals(request, failed.request)
        assertEquals(LiveFetchFailureKind.TRANSPORT, failed.kind)
        assertEquals("Weather source could not be reached.", failed.status)
        assertFalse(failed.status.contains("private"))
        assertEquals(1, transportCalls)
        assertEquals(2, events.size)
        assertEquals(failed, events.last())
        val compositionParameters = ProductionForecastComposition::class.java.declaredMethods
            .single { it.name == "create" }.parameterTypes.map { it.simpleName }
        assertEquals(
            listOf("ForecastEndpoint", "OpenMeteoTransport", "Clock", "Executor", "Function1"),
            compositionParameters,
        )
        assertTrue(compositionParameters.none { it.contains("MetNorway", ignoreCase = true) })
    }

    private class QueueExecutor : Executor {
        private val tasks = ArrayDeque<Runnable>()
        override fun execute(command: Runnable) { tasks.addLast(command) }
        fun runNext() = tasks.removeFirst().run()
    }
}
