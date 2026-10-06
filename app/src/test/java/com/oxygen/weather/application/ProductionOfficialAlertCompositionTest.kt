package com.oxygen.weather.application

import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.alerts.OfficialAlertProviderResult
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.alerts.nws.NwsHttpRequest
import com.oxygen.weather.data.alerts.nws.NwsHttpResponse
import com.oxygen.weather.data.alerts.nws.NwsTransport
import com.oxygen.weather.data.provider.GeoCoordinates
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionOfficialAlertCompositionTest {
    @Test fun configuredNwsCompositionQueriesSelectedPointOnce() {
        val request = OfficialAlertRequest(
            WeatherLocation(LocalLocationId("selected-location"), "Selected", ZoneId.of("America/Chicago")),
            GeoCoordinates(41.8819, -87.6278),
        )
        val sent = mutableListOf<NwsHttpRequest>()
        val repository = ProductionOfficialAlertComposition.create(
            endpoint = URI("https://alerts.example.test/alerts"),
            userAgent = "Oxygen test client",
            transport = NwsTransport { httpRequest ->
                sent += httpRequest
                NwsHttpResponse(200, """{"type":"FeatureCollection","features":[]}""")
            },
            clock = Clock.fixed(Instant.parse("2026-10-05T12:00:00Z"), ZoneId.of("UTC")),
        )

        val result = repository.fetch(request)

        assertEquals(OfficialAlertProviderResult.Supported(emptyList()), result)
        assertEquals(1, sent.size)
        assertEquals(
            URI("https://alerts.example.test/alerts/active?point=41.8819%2C-87.6278"),
            sent.single().uri,
        )
        assertEquals("Oxygen test client", sent.single().userAgent)
        assertTrue(sent.single().headers.containsKey("Accept"))
    }
}
