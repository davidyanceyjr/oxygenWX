package com.oxygen.weather.data.alerts

import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.OfficialAlert
import com.oxygen.weather.data.WeatherSource
import com.oxygen.weather.data.WeatherSourceId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfficialAlertProviderTest {
    private val request = OfficialAlertRequest(
        location = WeatherLocation(LocalLocationId("selected-location"), "Example City", ZoneId.of("UTC")),
        coordinates = GeoCoordinates(40.0, -75.0),
    )

    @Test
    fun supportedEmptyResultIsDistinctFromUnsupportedAndFailure() {
        val supported = OfficialAlertProviderResult.Supported(emptyList())
        val unsupported = OfficialAlertProviderResult.UnsupportedRegion
        val failure = OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.TRANSPORT)

        assertEquals(emptyList<OfficialAlert>(), supported.alerts)
        assertEquals(OfficialAlertProviderResult.UnsupportedRegion, unsupported)
        assertEquals(
            OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.TRANSPORT),
            failure,
        )
    }

    @Test
    fun providerReceivesLocationAndReturnsSuppliedOfficialAlertWithoutLosingFields() {
        val effective = Instant.parse("2026-10-05T12:00:00Z")
        val expires = Instant.parse("2026-10-05T18:00:00Z")
        val source = WeatherSource(WeatherSourceId("authoritative-alert-source"), "Alert authority")
        val alert = OfficialAlert(
            issuer = "National Weather Service",
            eventName = "Flood Watch",
            severity = "Moderate",
            effectiveAt = effective,
            expiresAt = expires,
            description = "Flooding is possible.",
            instructions = "Monitor local conditions.",
            sourceUrl = "https://alerts.example.test/notice/1",
            provenance = DataProvenance(DataType.OFFICIAL_ALERT, source),
        )
        var received: OfficialAlertRequest? = null
        val provider = OfficialAlertProvider { query ->
            received = query
            OfficialAlertProviderResult.Supported(listOf(alert))
        }

        val result = provider.fetch(request) as OfficialAlertProviderResult.Supported

        assertEquals(request, received)
        assertEquals(listOf(alert), result.alerts)
        assertEquals("National Weather Service", result.alerts.single().issuer)
        assertEquals("Flood Watch", result.alerts.single().eventName)
        assertEquals("Moderate", result.alerts.single().severity)
        assertEquals(effective, result.alerts.single().effectiveAt)
        assertEquals(expires, result.alerts.single().expiresAt)
        assertEquals("Flooding is possible.", result.alerts.single().description)
        assertEquals("Monitor local conditions.", result.alerts.single().instructions)
        assertEquals("https://alerts.example.test/notice/1", result.alerts.single().sourceUrl)
        assertEquals(source, result.alerts.single().provenance.source)
        assertEquals(DataType.OFFICIAL_ALERT, result.alerts.single().provenance.dataType)
    }

    @Test
    fun optionalSourceFieldsCanRemainAbsentInSupportedResult() {
        val alert = OfficialAlert(
            issuer = "Alert authority",
            eventName = "Statement",
            severity = null,
            effectiveAt = null,
            expiresAt = null,
            description = null,
            instructions = null,
            sourceUrl = null,
            provenance = DataProvenance(
                DataType.OFFICIAL_ALERT,
                WeatherSource(WeatherSourceId("authority"), "Alert authority"),
            ),
        )

        val result = OfficialAlertProviderResult.Supported(listOf(alert))
        val retained = result.alerts.single()

        assertNull(retained.severity)
        assertNull(retained.effectiveAt)
        assertNull(retained.expiresAt)
        assertNull(retained.description)
        assertNull(retained.instructions)
        assertNull(retained.sourceUrl)
        assertEquals(DataType.OFFICIAL_ALERT, retained.provenance.dataType)
    }
}
