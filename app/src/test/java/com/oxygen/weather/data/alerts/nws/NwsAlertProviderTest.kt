package com.oxygen.weather.data.alerts.nws

import com.oxygen.weather.data.*
import com.oxygen.weather.data.alerts.*
import com.oxygen.weather.data.provider.GeoCoordinates
import org.junit.Assert.*
import org.junit.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class NwsAlertProviderTest {
    private val now = Instant.parse("2026-10-05T12:00:00Z")
    private val request = OfficialAlertRequest(WeatherLocation(LocalLocationId("fixture"), "Fixture", ZoneId.of("UTC")), GeoCoordinates(39.7456, -97.0892))

    @Test fun requestUsesPointAndIdentifyingHeaders() {
        val built = NwsAlertRequestBuilder.build(URI("https://api.weather.gov/alerts"), "OxygenWeather/1.0", request)
        assertEquals("https://api.weather.gov/alerts/active?point=39.7456%2C-97.0892", built.uri.toString())
        assertEquals("OxygenWeather/1.0", built.userAgent)
        assertEquals("application/geo+json", built.accept)
        assertEquals(mapOf("User-Agent" to "OxygenWeather/1.0", "Accept" to "application/geo+json"), built.headers)
    }

    @Test fun populatedAndEmptyAreSupportedAndKeepSourceData() {
        val body = """{"type":"FeatureCollection","features":[{"properties":{"senderName":"NWS Test","event":"Test Watch","severity":"Moderate","effective":"2026-10-05T10:00:00Z","expires":"2026-10-05T14:00:00Z","description":"Description","instruction":"Instruction","@id":"https://api.weather.gov/alerts/123","id":"urn:source-id"}},{"properties":{"event":"Test Advisory","@id":"http://evil.invalid/x"}}]}"""
        val result = provider(200, body).fetch(request) as OfficialAlertProviderResult.Supported
        assertEquals(2, result.alerts.size)
        val first = result.alerts.first()
        assertEquals("NWS Test", first.issuer); assertEquals("Test Watch", first.eventName)
        assertEquals("Moderate", first.severity)
        assertEquals(Instant.parse("2026-10-05T10:00:00Z"), first.effectiveAt)
        assertEquals(Instant.parse("2026-10-05T14:00:00Z"), first.expiresAt)
        assertEquals("Description", first.description); assertEquals("Instruction", first.instructions)
        assertEquals("https://api.weather.gov/alerts/123", first.sourceUrl)
        assertEquals(now, first.provenance.retrievedAt); assertEquals(DataType.OFFICIAL_ALERT, first.provenance.dataType)
        assertEquals(Instant.parse("2026-10-05T10:00:00Z"), first.provenance.validAt)
        assertEquals(WeatherSourceId("noaa-nws"), first.provenance.source?.id)
        assertEquals("National Weather Service", result.alerts[1].issuer)
        assertNull(result.alerts[1].severity); assertNull(result.alerts[1].expiresAt)
        assertNull(result.alerts[1].description); assertNull(result.alerts[1].instructions)
        assertNull(result.alerts[1].sourceUrl)
        assertEquals(OfficialAlertProviderResult.Supported(emptyList()), provider(200, """{"type":"FeatureCollection","features":[]}""").fetch(request))
    }

    @Test fun distinguishesCoverageFromMalformedAndHttpFailures() {
        val outside = """{"type":"https://api.weather.gov/problems/InvalidParameter","detail":"Parameter \"point\" is invalid: out of bounds"}"""
        assertEquals(OfficialAlertProviderResult.UnsupportedRegion, provider(400, outside).fetch(request))
        assertEquals(OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.SOURCE), provider(400, "{}" ).fetch(request))
        assertEquals(OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.SOURCE), provider(200, "not json").fetch(request))
        assertEquals(OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.SOURCE), provider(200, """{"type":"FeatureCollection","features":[{}]}""").fetch(request))
        assertEquals(OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.SOURCE), provider(503, "").fetch(request))
    }

    @Test fun thrownTransportFailureIsDistinct() {
        val provider = NwsAlertProvider(transport = NwsTransport { throw java.io.IOException("offline") })
        assertEquals(OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.TRANSPORT), provider.fetch(request))
    }

    private fun provider(status: Int, body: String) = NwsAlertProvider(
        endpoint = URI("https://api.weather.gov/alerts"), userAgent = "OxygenWeatherTest/1.0",
        transport = NwsTransport { NwsHttpResponse(status, body) }, clock = Clock.fixed(now, ZoneId.of("UTC")),
    )
}
