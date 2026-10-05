package com.oxygen.weather.data.provider.openmeteo

import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneRequest
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneResult
import com.oxygen.weather.data.provider.ForecastEndpoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI
import java.time.ZoneId

class OpenMeteoCoordinateTimeZoneLookupTest {
    private val endpoint = ForecastEndpoint(URI("https://weather.invalid/v1/forecast"))
    private val request = CoordinateTimeZoneRequest(41.88, -87.63)

    @Test fun requestUsesForecastEndpointCoordinatesAndAutomaticTimezoneOnly() {
        var requested: URI? = null
        val lookup = lookup { uri ->
            requested = uri
            OpenMeteoHttpResponse(200, """{"timezone":"America/Chicago"}""")
        }

        assertEquals(
            CoordinateTimeZoneResult.Success(ZoneId.of("America/Chicago")),
            lookup.lookup(request),
        )
        assertEquals(
            "https://weather.invalid/v1/forecast?latitude=41.88&longitude=-87.63&timezone=auto",
            requested.toString(),
        )
    }

    @Test fun returnsOnlyValidatedTimezoneAndIgnoresWeatherFields() {
        val result = lookup { OpenMeteoHttpResponse(200, fixture("timezone_lookup.json")) }.lookup(request)

        assertEquals(
            CoordinateTimeZoneResult.Success(ZoneId.of("America/Chicago")),
            result,
        )
        assertTrue(result is CoordinateTimeZoneResult.Success)
        assertFalse(result.toString().contains("12.5"))
        assertFalse(result.toString().contains("2026-10-03"))
    }

    @Test fun classifiesTransportAndHttpFailures() {
        assertFailure(CoordinateTimeZoneResult.Reason.TRANSPORT, lookup { throw java.io.IOException("private") }.lookup(request))
        assertFailure(CoordinateTimeZoneResult.Reason.TRANSPORT, lookup { throw java.net.SocketTimeoutException("private") }.lookup(request))
        assertFailure(CoordinateTimeZoneResult.Reason.HTTP, lookup { OpenMeteoHttpResponse(503, "not json") }.lookup(request))
        assertFailure(
            CoordinateTimeZoneResult.Reason.HTTP,
            lookup { OpenMeteoHttpResponse(200, """{"error":true,"reason":"private"}""") }.lookup(request),
        )
    }

    @Test fun classifiesMalformedMissingAndInvalidTimezoneSeparately() {
        assertFailure(CoordinateTimeZoneResult.Reason.MALFORMED_RESPONSE, result("not json"))
        assertFailure(CoordinateTimeZoneResult.Reason.MALFORMED_RESPONSE, result("[]"))
        assertFailure(CoordinateTimeZoneResult.Reason.MALFORMED_RESPONSE, result("""{"timezone":42}"""))
        assertFailure(CoordinateTimeZoneResult.Reason.MISSING_TIME_ZONE, result("{}"))
        assertFailure(CoordinateTimeZoneResult.Reason.MISSING_TIME_ZONE, result("""{"timezone":null}"""))
        assertFailure(CoordinateTimeZoneResult.Reason.INVALID_TIME_ZONE, result("""{"timezone":""}"""))
        assertFailure(CoordinateTimeZoneResult.Reason.INVALID_TIME_ZONE, result("""{"timezone":"Mars/Olympus"}"""))
        assertFailure(CoordinateTimeZoneResult.Reason.INVALID_TIME_ZONE, result("""{"timezone":"+02:00"}"""))
    }

    @Test fun requestRejectsNonGeographicCoordinates() {
        assertTrue(runCatching { CoordinateTimeZoneRequest(Double.NaN, 0.0) }.isFailure)
        assertTrue(runCatching { CoordinateTimeZoneRequest(91.0, 0.0) }.isFailure)
        assertTrue(runCatching { CoordinateTimeZoneRequest(0.0, 181.0) }.isFailure)
    }

    private fun result(body: String) = lookup { OpenMeteoHttpResponse(200, body) }.lookup(request)

    private fun lookup(response: (URI) -> OpenMeteoHttpResponse) =
        OpenMeteoCoordinateTimeZoneLookup(endpoint, OpenMeteoTransport(response))

    private fun assertFailure(reason: CoordinateTimeZoneResult.Reason, result: CoordinateTimeZoneResult) {
        assertEquals(CoordinateTimeZoneResult.Failure(reason), result)
    }

    private fun fixture(name: String) =
        javaClass.classLoader!!.getResourceAsStream("openmeteo/$name")!!.bufferedReader().use { it.readText() }
}
