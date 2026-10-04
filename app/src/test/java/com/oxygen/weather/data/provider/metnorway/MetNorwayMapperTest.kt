package com.oxygen.weather.data.provider.metnorway

import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI
import java.time.Instant
import java.time.ZoneId

class MetNorwayMapperTest {
    private val location = WeatherLocation(
        LocalLocationId("local-1"),
        "Chicago",
        ZoneId.of("America/Chicago"),
    )
    private val retrievedAt = Instant.parse("2026-10-03T15:02:00Z")
    private val request = ForecastRequest(
        location,
        GeoCoordinates(41.88, -87.63),
        ForecastCoverage(hourlyHours = 3, dailyDays = 2),
        ForecastField.entries.toSet(),
    )

    @Test fun compactFixtureMapsOnlyDocumentedCanonicalFactsAndHonestProvenance() {
        val mapped = MetNorwayMapper.map(decode(), request, retrievedAt)
        val forecast = requireNotNull(mapped.forecast)
        assertEquals(location, forecast.location)
        assertEquals(DataType.FORECAST, forecast.provenance.dataType)
        assertEquals(MetNorwayMapper.source, forecast.provenance.source)
        assertEquals(retrievedAt, forecast.provenance.retrievedAt)
        assertNull(forecast.provenance.validAt)
        assertEquals(listOf(10, 11), forecast.hourly.map { it.time.hour })
        assertTrue(forecast.daily.isEmpty())

        val first = forecast.hourly.first()
        assertEquals(WeatherCondition.PARTLY_CLOUDY, first.condition)
        assertEquals(12.5, first.temperatureC!!, 0.0)
        assertNull(first.dewPointC)
        assertEquals(1012.2, first.pressureHpa!!, 0.0)
        assertEquals(14.4, first.windSpeedKph!!, 0.000001)
        assertNull(first.precipitationProbabilityPct)
        assertEquals(0.2, first.precipitationMm!!, 0.0)
        assertEquals(75.0, first.cloudCoverPct!!, 0.0)
        assertEquals(WeatherCondition.STORM, forecast.hourly[1].condition)
        assertNull(forecast.hourly[1].temperatureC)
    }

    @Test fun elapsedTimeHorizonDoesNotTreatSparseRowsAsConsecutiveHours() {
        val twoHours = request.copy(coverage = ForecastCoverage(hourlyHours = 2))
        val forecast = MetNorwayMapper.map(decode(), twoHours, retrievedAt).forecast!!
        assertEquals(listOf(10, 11), forecast.hourly.map { it.time.hour })
    }

    @Test fun unsupportedUnitOnlyRemovesThatMeasurement() {
        val original = decode()
        val mapped = MetNorwayMapper.map(
            original.copy(units = original.units + ("air_temperature" to "fahrenheit")),
            request,
            retrievedAt,
        )
        assertNull(mapped.forecast!!.hourly.first().temperatureC)
        assertEquals(1012.2, mapped.forecast.hourly.first().pressureHpa!!, 0.0)
    }

    @Test fun unknownSymbolIsUnavailableWithoutDiscardingSiblingFacts() {
        val original = decode()
        val first = original.timeseries.first()
        val unknown = first.copy(
            nextOneHours = first.nextOneHours!!.copy(summaryCode = string("alien_weather")),
        )
        val mapped = MetNorwayMapper.map(
            original.copy(timeseries = listOf(unknown)),
            request,
            retrievedAt,
        )
        assertNull(mapped.forecast!!.hourly.first().condition)
        assertEquals(12.5, mapped.forecast.hourly.first().temperatureC!!, 0.0)
    }

    @Test fun documentedSymbolFamiliesMapWithoutDependingOnDayNightVariant() {
        val expected = linkedMapOf(
            "clearsky_night" to WeatherCondition.CLEAR,
            "cloudy" to WeatherCondition.CLOUDY,
            "lightrainshowers_day" to WeatherCondition.RAIN,
            "sleetshowers_polartwilight" to WeatherCondition.SNOW,
            "snow" to WeatherCondition.SNOW,
            "rainshowersandthunder_night" to WeatherCondition.STORM,
        )
        expected.forEach { (symbol, condition) ->
            val original = decode()
            val first = original.timeseries.first()
            val response = original.copy(
                timeseries = listOf(
                    first.copy(
                        nextOneHours = first.nextOneHours!!.copy(summaryCode = string(symbol)),
                    ),
                ),
            )
            assertEquals(
                condition,
                MetNorwayMapper.map(response, request, retrievedAt).forecast!!.hourly.single().condition,
            )
        }
    }

    @Test fun deceptiveUnknownSymbolIsUnavailableRatherThanInferredFromItsName() {
        val original = decode()
        val first = original.timeseries.first()
        listOf("notrain_day", "lightrain_day", "cloudy_night").forEach { unknown ->
            val response = original.copy(
                timeseries = listOf(
                    first.copy(
                        nextOneHours = first.nextOneHours!!.copy(summaryCode = string(unknown)),
                    ),
                ),
            )

            assertNull(
                MetNorwayMapper.map(response, request, retrievedAt).forecast!!.hourly.single().condition,
            )
        }
    }

    @Test fun unrequestedCompactFactsCannotTurnUnsupportedOnlyRequestIntoSuccess() {
        val unsupportedOnly = request.copy(
            coverage = ForecastCoverage(hourlyHours = 3),
            fields = setOf(ForecastField.DEW_POINT),
        )
        val mapped = MetNorwayMapper.map(
            decode(),
            unsupportedOnly,
            retrievedAt,
            unsupportedFields = unsupportedOnly.fields,
        )
        assertNull(mapped.forecast)
        assertEquals(unsupportedOnly.fields, mapped.unsupportedFields)
    }

    @Test fun malformedTimestampAndDecreasingChronologyInvalidateHourlySection() {
        val original = decode()
        val malformed = original.copy(
            timeseries = listOf(original.timeseries.first().copy(time = string("not-a-time"))),
        )
        assertEquals(
            setOf(MetNorwayMapping.Section.HOURLY),
            MetNorwayMapper.map(malformed, request, retrievedAt).invalidSections,
        )
        val decreasing = original.copy(
            timeseries = original.timeseries.take(2).reversed(),
        )
        assertEquals(
            setOf(MetNorwayMapping.Section.HOURLY),
            MetNorwayMapper.map(decreasing, request, retrievedAt).invalidSections,
        )
    }

    @Test fun missingAndNullValuesStayUnavailableAndDailyOnlyMakesNoForecast() {
        val mapped = MetNorwayMapper.map(decode(), request, retrievedAt)
        assertNull(mapped.forecast!!.hourly[1].temperatureC)
        val daily = request.copy(
            coverage = ForecastCoverage(dailyDays = 2),
            fields = setOf(ForecastField.TEMPERATURE),
        )
        val dailyMapped = MetNorwayMapper.map(
            decode(),
            daily,
            retrievedAt,
            unsupportedFields = daily.fields,
        )
        assertNull(dailyMapped.forecast)
        assertEquals(daily.fields, dailyMapped.unsupportedFields)
    }

    private fun decode(): MetNorwayResponse {
        val body = javaClass.getResource("/metnorway/complete.json")!!.readText()
        val adapter = MetNorwayAdapter(
            ForecastEndpoint(URI("https://weather.invalid/compact")),
            MetNorwayIdentification("OxygenWeather/1.0", "weather@example.invalid"),
        ) { MetNorwayHttpResponse(200, body) }
        return (adapter.fetch(request) as MetNorwayResult.Success).response
    }

    private fun string(value: String) =
        MetNorwayValue.Scalar(value, MetNorwayValue.Scalar.Kind.STRING)
}
