package com.oxygen.weather.data.locationsearch.openmeteo

import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearchRequest
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import org.junit.Assert.*
import org.junit.Test
import java.net.URI
import java.time.ZoneId

class OpenMeteoLocationSearchTest {
    private fun fixture(name: String) = javaClass.classLoader!!.getResource("locationsearch/openmeteo/$name")!!.readText()
    private fun adapter(body: String, status: Int = 200) = OpenMeteoLocationSearch(LocationSearchTransport { LocationSearchHttpResponse(status, body) })
    private val request = LocationSearchRequest("München, Bayern & x", "de-DE")

    @Test fun requestEncodesQueryAndCanonicalLocale() {
        val uri = OpenMeteoLocationSearchRequestBuilder.build(request).toASCIIString()
        assertTrue(uri.contains("name=M%C3%BCnchen%2C%20Bayern%20%26%20x"))
        assertTrue(uri.contains("language=de-de"))
        assertTrue(uri.contains("format=json"))
        assertFalse(uri.contains("count="))
        assertFalse(OpenMeteoLocationSearchRequestBuilder.build(LocationSearchRequest("x")).toString().contains("language="))
    }

    @Test fun mapsValidCandidatesAndPreservesOrderWhileDiscardingInvalid() {
        val result = adapter(fixture("candidates.json")).search(request) as LocationSearchResult.Success
        assertEquals(listOf(2950159L, 999L), result.candidates.map { it.providerId })
        val berlin = result.candidates.first()
        assertEquals("Berlin", berlin.displayName)
        assertEquals(52.52437, berlin.latitude, 0.0)
        assertEquals(13.41053, berlin.longitude, 0.0)
        assertEquals(ZoneId.of("Europe/Berlin"), berlin.timeZone)
        assertEquals("DE", berlin.countryCode)
        assertNull(berlin.admin2)
    }

    @Test fun onlyEmptyResultsMeansNoResults() {
        assertEquals(LocationSearchResult.NoResults, adapter(fixture("empty.json")).search(request))
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE), adapter(fixture("all_invalid.json")).search(request))
    }

    @Test fun classifiesHttpProviderTransportAndMalformedResponses() {
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.HTTP_OR_PROVIDER), adapter("not json", 503).search(request))
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.HTTP_OR_PROVIDER), adapter("{\"error\":true}").search(request))
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE), adapter("{}").search(request))
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE), adapter("{bad").search(request))
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE), adapter("{\"results\":{}}").search(request))
        val transportFail = OpenMeteoLocationSearch(LocationSearchTransport { throw java.io.IOException("private detail") }).search(request)
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.TRANSPORT), transportFail)
        val huge = OpenMeteoLocationSearch(LocationSearchTransport { LocationSearchHttpResponse(200, " ".repeat(OPEN_METEO_GEOCODING_MAX_RESPONSE_BYTES + 1)) }).search(request)
        assertEquals(LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE), huge)
    }

    @Test fun contractRejectsInvalidRequestsAndCandidates() {
        assertThrows(IllegalArgumentException::class.java) { LocationSearchRequest("  ") }
        assertThrows(IllegalArgumentException::class.java) { LocationSearchRequest("x", "en_US") }
        assertThrows(IllegalArgumentException::class.java) { LocationCandidate(1, "Name", Double.NaN, 0.0, ZoneId.of("UTC")) }
        assertThrows(IllegalArgumentException::class.java) { LocationCandidate(1, "Name", 0.0, 181.0, ZoneId.of("UTC")) }
        assertThrows(IllegalArgumentException::class.java) { LocationCandidate(1, " ", 0.0, 0.0, ZoneId.of("UTC")) }
        assertThrows(IllegalArgumentException::class.java) { LocationCandidate(1, "Name", 0.0, 0.0, ZoneId.of("+02:00")) }
    }

    @Test fun lookupCallsOnlyItsInjectedGeocodingTransport() {
        var calls = 0
        var requested: URI? = null
        val lookup = OpenMeteoLocationSearch(LocationSearchTransport { uri -> calls++; requested = uri; LocationSearchHttpResponse(200, fixture("empty.json")) })
        assertEquals(LocationSearchResult.NoResults, lookup.search(LocationSearchRequest("Chicago")))
        assertEquals(1, calls)
        assertTrue(requested.toString().startsWith("https://geocoding-api.open-meteo.com/v1/search?"))
    }
}
