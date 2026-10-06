package com.oxygen.weather.presentation

import com.oxygen.weather.application.OfficialAlertState
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.OfficialAlert
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfficialAlertDetailMapperTest {
    private val request = OfficialAlertRequest(
        WeatherLocation(LocalLocationId("selected"), "Selected", ZoneId.of("America/Chicago")),
        GeoCoordinates(41.9, -87.6),
    )

    @Test
    fun mapsOneSupportedAlertAndFormatsTimesInRequestedLocationZone() {
        val state = OfficialAlertState.Supported(7, request, listOf(alert()))

        assertEquals(
            OfficialAlertDetailPresentation(
                issuer = "National Weather Service",
                eventName = "Severe Thunderstorm Warning",
                severity = "Severe",
                effectiveAtText = "Oct 6, 2026 8:15 AM CDT",
                expiresAtText = "Oct 6, 2026 9:30 AM CDT",
                description = "Source supplied description.",
                instructions = "Source supplied instructions.",
                sourceUrlText = "https://alerts.example.test/warning/1",
                sourceAction = OfficialAlertSourceAction(
                    url = "https://alerts.example.test/warning/1",
                    label = "View official alert",
                ),
            ),
            OfficialAlertDetailMapper.map(state),
        )
    }

    @Test
    fun returnsNoDetailUnlessSupportedStateContainsExactlyOneAlert() {
        assertNull(OfficialAlertDetailMapper.map(OfficialAlertState.Loading(7, request)))
        assertNull(OfficialAlertDetailMapper.map(OfficialAlertState.Supported(7, request, emptyList())))
        assertNull(OfficialAlertDetailMapper.map(OfficialAlertState.Supported(7, request, listOf(alert(), alert()))))
        assertNull(OfficialAlertDetailMapper.map(OfficialAlertState.UnsupportedRegion(7, request)))
    }

    @Test
    fun keepsMissingTimesAndOptionalSourceTextMissing() {
        val detail = OfficialAlertDetailMapper.map(
            OfficialAlertState.Supported(7, request, listOf(alert().copy(
                effectiveAt = null,
                expiresAt = null,
                severity = null,
                description = null,
                instructions = null,
                sourceUrl = null,
            ))),
        )!!

        assertNull(detail.effectiveAtText)
        assertNull(detail.expiresAtText)
        assertNull(detail.severity)
        assertNull(detail.description)
        assertNull(detail.instructions)
        assertNull(detail.sourceUrlText)
        assertNull(detail.sourceAction)
    }

    @Test
    fun sourceActionAcceptsOnlyAbsoluteHttpOrHttpsLinksWithHost() {
        val candidates = listOf(
            "http://alerts.example.test/a" to true,
            "HTTPS://alerts.example.test/a" to true,
            "/relative/path" to false,
            "javascript:alert(1)" to false,
            "ftp://alerts.example.test/a" to false,
            "https:///missing-host" to false,
            "https://user:password@alerts.example.test/a" to false,
            "https://bad host/a" to false,
        )

        candidates.forEach { (url, safe) ->
            val detail = OfficialAlertDetailMapper.map(
                OfficialAlertState.Supported(7, request, listOf(alert().copy(sourceUrl = url))),
            )!!
            assertEquals(url, detail.sourceUrlText)
            assertEquals(safe, detail.sourceAction != null)
            if (safe) assertEquals(url, detail.sourceAction?.url)
        }
    }

    private fun alert() = OfficialAlert(
        issuer = "National Weather Service",
        eventName = "Severe Thunderstorm Warning",
        severity = "Severe",
        effectiveAt = Instant.parse("2026-10-06T13:15:00Z"),
        expiresAt = Instant.parse("2026-10-06T14:30:00Z"),
        description = "Source supplied description.",
        instructions = "Source supplied instructions.",
        sourceUrl = "https://alerts.example.test/warning/1",
        provenance = DataProvenance(DataType.OFFICIAL_ALERT),
    )
}
