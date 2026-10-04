package com.oxygen.weather.data.provider.openmeteo

import com.oxygen.weather.data.*
import com.oxygen.weather.data.provider.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class OpenMeteoMapperTest {
    private val location = WeatherLocation(LocalLocationId("local-1"), "Chicago", ZoneId.of("America/Chicago"))
    private val retrieved = Instant.parse("2026-10-03T15:00:00Z")

    private fun decode(resource: String = "complete.json"): OpenMeteoResponse {
        val body = javaClass.getResource("/openmeteo/$resource")!!.readText()
        return (OpenMeteoAdapter(ForecastEndpoint(java.net.URI("https://example.test")), OpenMeteoTransport { OpenMeteoHttpResponse(200, body) })
            .fetch(ForecastRequest(location, GeoCoordinates(41.88, -87.63), ForecastCoverage(72, 10), ForecastField.entries.toSet())) as OpenMeteoResult.Success).response
    }

    @Test fun completeFixtureMapsCurrentAndForecastWithHonestProvenance() {
        val mapped = OpenMeteoMapper.map(decode(), location, retrieved)
        val current = mapped.current!!
        val forecast = mapped.forecast!!
        assertEquals(12.5, current.temperatureC!!, 0.0)
        assertEquals(8.0, current.dewPointC!!, 0.0)
        assertEquals(1012.2, current.pressureHpa!!, 0.0)
        assertEquals(14.4, current.windSpeedKph!!, 0.0)
        assertNull(current.precipitationMmPerHr)
        assertEquals(WeatherCondition.CLOUDY, current.condition)
        assertEquals(DataType.FORECAST, mapped.currentProvenance!!.dataType)
        assertEquals(Instant.parse("2026-10-03T15:00:00Z"), mapped.currentProvenance.validAt)
        assertEquals(retrieved, mapped.currentProvenance.retrievedAt)
        assertEquals("open-meteo", mapped.currentProvenance.source!!.id.value)
        assertEquals(location, forecast.location)
        assertEquals(retrieved, forecast.provenance.retrievedAt)
        assertEquals(listOf(10, 11, 11), forecast.hourly.map { it.time.hour })
        assertEquals(null, forecast.hourly[1].temperatureC)
        assertEquals(null, forecast.hourly[2].precipitationProbabilityPct)
        assertEquals(1.0, forecast.daily.first().sunshineHours!!, 0.0)
    }

    @Test fun unsupportedUnitOnlyMakesItsMeasurementUnavailable() {
        val original = decode()
        val hourly = original.hourly!!.copy(units = original.hourly.units + ("temperature_2m" to "°F"))
        val mapped = OpenMeteoMapper.map(original.copy(hourly = hourly), location, retrieved)
        assertNull(mapped.forecast!!.hourly.first().temperatureC)
        assertEquals(8.0, mapped.forecast.hourly.first().dewPointC!!, 0.0)
    }

    @Test fun unknownCodesAndUnsupportedUnitsDoNotCreateFacts() {
        val original = decode()
        val hourly = original.hourly!!.copy(
            variables = mapOf(OpenMeteoVariable.WEATHER_CODE to OpenMeteoValue.ArrayValue(listOf(number("999")))),
            units = mapOf("weather_code" to "unexpected"),
        )
        val mapped = OpenMeteoMapper.map(original.copy(current = null, hourly = hourly, daily = null), location, retrieved)
        assertNull(mapped.forecast)
    }

    @Test fun malformedSectionTimestampIsReportedWhileSiblingSurvives() {
        val original = decode()
        val broken = original.hourly!!.copy(time = OpenMeteoValue.ArrayValue(listOf(string("not-a-time"))))
        val mapped = OpenMeteoMapper.map(original.copy(hourly = broken), location, retrieved)
        assertTrue(OpenMeteoMapping.Section.HOURLY in mapped.invalidSections)
        assertNotNull(mapped.current)
        assertNotNull(mapped.forecast)
    }

    @Test fun decreasingChronologyIsBoundedBecauseCanonicalForecastRequiresOrderedRows() {
        val original = decode()
        val section = original.hourly!!
        val times = (section.time as OpenMeteoValue.ArrayValue).values.toMutableList()
        times[0] = string("2026-10-03T12:00")
        val mapped = OpenMeteoMapper.map(original.copy(hourly = section.copy(time = OpenMeteoValue.ArrayValue(times))), location, retrieved)
        assertTrue(OpenMeteoMapping.Section.HOURLY in mapped.invalidSections)
        assertNotNull(mapped.current)
    }

    @Test fun currentOnlyResponseIsRetainedWithoutManufacturedForecast() {
        val original = decode()
        val mapped = OpenMeteoMapper.map(original.copy(timezone = "UTC", hourly = null, daily = null), location, retrieved)
        assertNotNull(mapped.current)
        assertNull(mapped.forecast)
        assertEquals(10, mapped.current!!.observedAt.hour)
        assertEquals(Instant.parse("2026-10-03T15:00:00Z"), mapped.currentProvenance!!.validAt)
    }

    @Test fun hourlyOnlyAndDailyOnlyForecastsRemainPartialAndUsable() {
        val original = decode()
        val hourlyOnly = OpenMeteoMapper.map(original.copy(current = null, daily = null), location, retrieved).forecast!!
        assertEquals(3, hourlyOnly.hourly.size)
        assertTrue(hourlyOnly.daily.isEmpty())
        val dailyOnly = OpenMeteoMapper.map(original.copy(current = null, hourly = null), location, retrieved).forecast!!
        assertTrue(dailyOnly.hourly.isEmpty())
        assertEquals(2, dailyOnly.daily.size)
    }

    @Test fun sunshineSecondsConvertToHoursAndUnknownCodeIsUnavailable() {
        val original = decode()
        val daily = original.daily!!.copy(variables = original.daily.variables + (OpenMeteoVariable.WEATHER_CODE to OpenMeteoValue.ArrayValue(listOf(number("999"), number("71")))))
        val mapped = OpenMeteoMapper.map(original.copy(daily = daily), location, retrieved)
        assertNull(mapped.forecast!!.daily.first().condition)
        assertEquals(WeatherCondition.SNOW, mapped.forecast.daily[1].condition)
        assertEquals(1.0, mapped.forecast.daily.first().sunshineHours!!, 0.0)
    }

    private fun number(v: String) = OpenMeteoValue.Scalar(v, OpenMeteoValue.Scalar.Kind.NUMBER)
    private fun string(v: String) = OpenMeteoValue.Scalar(v, OpenMeteoValue.Scalar.Kind.STRING)
}
