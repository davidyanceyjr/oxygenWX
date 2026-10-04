package com.oxygen.weather.data.provider.metnorway

import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import com.oxygen.weather.data.provider.GeoCoordinates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI
import java.time.ZoneId

class MetNorwayAdapterTest {
    private val endpoint = ForecastEndpoint(URI("https://weather.invalid/weatherapi/locationforecast/2.0/compact"))
    private val identification = MetNorwayIdentification("OxygenWeather/1.0", "weather@example.invalid")
    private val request = ForecastRequest(
        WeatherLocation(LocalLocationId("local-only"), "My home", ZoneId.of("America/Chicago")),
        GeoCoordinates(41.12349, -87.12349),
        ForecastCoverage(3, 2),
        ForecastField.entries.toSet(),
    )

    @Test fun requestUsesExplicitCompactEndpointTruncatedCoordinatesAndRequiredIdentification() {
        val query = MetNorwayRequestBuilder.build(endpoint, identification, request)
        assertEquals(
            "https://weather.invalid/weatherapi/locationforecast/2.0/compact?lat=41.1234&lon=-87.1234",
            query.request.uri.toASCIIString(),
        )
        assertEquals("OxygenWeather/1.0 weather@example.invalid", query.request.userAgent)
        assertFalse(query.request.uri.toString().contains("local-only"))
        assertFalse(query.request.uri.toString().contains("My%20home"))
        assertEquals(
            setOf(
                ForecastField.DAILY_LOW,
                ForecastField.DAILY_HIGH,
                ForecastField.DAILY_WIND_GUST,
                ForecastField.DAILY_SUNSHINE_HOURS,
                ForecastField.DEW_POINT,
                ForecastField.PRECIPITATION_PROBABILITY,
            ),
            query.unsupportedFields,
        )
    }

    @Test fun dailyOnlyRequestHonestlyMarksEveryRequestedFieldUnsupported() {
        val daily = request.copy(
            coverage = ForecastCoverage(dailyDays = 2),
            fields = setOf(ForecastField.TEMPERATURE, ForecastField.DAILY_HIGH),
        )
        assertEquals(
            daily.fields,
            MetNorwayRequestBuilder.build(endpoint, identification, daily).unsupportedFields,
        )
    }

    @Test fun fixtureDecodesProviderShapeAndPreservesNullVersusMissing() {
        var sent: MetNorwayHttpRequest? = null
        val result = MetNorwayAdapter(endpoint, identification) {
            sent = it
            MetNorwayHttpResponse(200, fixture("complete.json"))
        }.fetch(request) as MetNorwayResult.Success
        assertEquals(identification.userAgent, sent!!.userAgent)
        assertEquals("celsius", result.response.units["air_temperature"])
        assertEquals("2026-10-03T14:20:00Z", scalar(result.response.updatedAt).value)
        assertEquals(3, result.response.timeseries.size)
        assertSame(
            MetNorwayValue.Null,
            result.response.timeseries[1].instantDetails["air_temperature"],
        )
        assertFalse(
            result.response.timeseries[2].nextOneHours!!.details
                .containsKey("probability_of_precipitation"),
        )
        assertEquals(
            "partlycloudy_day",
            scalar(result.response.timeseries[0].nextOneHours!!.summaryCode).value,
        )
    }

    @Test fun noDataMalformedHttpAndTransportOutcomesStayTypedAndSafe() {
        fun result(body: String, status: Int = 200) = MetNorwayAdapter(endpoint, identification) {
            MetNorwayHttpResponse(status, body)
        }.fetch(request)
        assertEquals(MetNorwayResult.NoData, result(fixture("no_data.json")))
        assertEquals(MetNorwayResult.NoData, result("", 204))
        assertTrue(result(fixture("complete.json"), 203) is MetNorwayResult.Success)
        assertTrue(result("{bad") is MetNorwayResult.Malformed)
        assertTrue(result("[]") is MetNorwayResult.Malformed)
        assertEquals(503, (result("private response body", 503) as MetNorwayResult.HttpError).statusCode)

        val timeout = MetNorwayAdapter(endpoint, identification) {
            throw java.net.SocketTimeoutException("private detail")
        }.fetch(request) as MetNorwayResult.TransportFailure
        assertEquals(ForecastTransportFailure.Kind.TIMEOUT, timeout.failure.kind)
        val network = MetNorwayAdapter(endpoint, identification) {
            throw java.io.IOException("private detail")
        }.fetch(request) as MetNorwayResult.TransportFailure
        assertEquals(ForecastTransportFailure.Kind.NETWORK, network.failure.kind)
    }

    @Test fun configuredIdentityAndWireTypesDoNotLeakFromSafeRendering() {
        val query = MetNorwayRequestBuilder.build(endpoint, identification, request)
        assertFalse(query.toString().contains("weather@example.invalid"))
        assertFalse(query.toString().contains("41.1234"))
        assertFalse(query.request.toString().contains("OxygenWeather"))
        val http = MetNorwayHttpResponse(200, "private response body")
        assertFalse(http.toString().contains("private response body"))
        val result = MetNorwayAdapter(endpoint, identification) { http }.fetch(request)
        assertFalse(result.toString().contains("private response body"))
    }

    @Test fun identificationRejectsBlankAndHeaderInjection() {
        assertFails { MetNorwayIdentification("", "weather@example.invalid") }
        assertFails { MetNorwayIdentification("OxygenWeather/1.0\r\nInjected: true", "weather@example.invalid") }
    }

    private fun scalar(value: MetNorwayValue?) = value as MetNorwayValue.Scalar
    private fun fixture(name: String) =
        javaClass.classLoader!!.getResourceAsStream("metnorway/$name")!!.bufferedReader().use { it.readText() }

    private fun assertFails(block: () -> Unit) {
        try {
            block()
            throw AssertionError("Expected IllegalArgumentException")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
