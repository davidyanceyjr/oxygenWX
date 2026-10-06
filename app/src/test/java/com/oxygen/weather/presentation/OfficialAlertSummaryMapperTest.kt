package com.oxygen.weather.presentation

import com.oxygen.weather.application.OfficialAlertFailureKind
import com.oxygen.weather.application.OfficialAlertState
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.OfficialAlert
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfficialAlertSummaryMapperTest {
    private val request = OfficialAlertRequest(
        WeatherLocation(LocalLocationId("selected"), "Selected", ZoneId.of("UTC")),
        GeoCoordinates(40.0, -100.0),
    )

    @Test
    fun mapsLoadingAndSupportedEmptyToDistinctStates() {
        val checking = OfficialAlertSummaryMapper.map(OfficialAlertState.Loading(1, request))
        val noAlerts = OfficialAlertSummaryMapper.map(OfficialAlertState.Supported(1, request, emptyList()))

        assertEquals(OfficialAlertSummaryPresentation.Checking, checking)
        assertEquals("Checking for official alerts.", checking.summaryText)
        assertEquals(OfficialAlertSummaryPresentation.NoActiveAlerts, noAlerts)
        assertEquals("No active official alerts.", noAlerts.summaryText)
        assertTrue(checking != noAlerts)
    }

    @Test
    fun mapsSingleAlertWithOnlySourceSuppliedEventAndOptionalSeverity() {
        val withSeverity = OfficialAlertSummaryMapper.map(
            OfficialAlertState.Supported(1, request, listOf(alert("Severe Thunderstorm Warning", "Severe"))),
        )
        val withoutSeverity = OfficialAlertSummaryMapper.map(
            OfficialAlertState.Supported(1, request, listOf(alert("Flood Advisory", null))),
        )

        assertEquals(
            OfficialAlertSummaryPresentation.SingleAlert("Severe Thunderstorm Warning", "Severe"),
            withSeverity,
        )
        assertEquals("Official alert: Severe Thunderstorm Warning. Severity: Severe.", withSeverity.summaryText)
        assertEquals(OfficialAlertSummaryPresentation.SingleAlert("Flood Advisory", null), withoutSeverity)
        assertEquals("Official alert: Flood Advisory.", withoutSeverity.summaryText)
    }

    @Test
    fun mapsMultipleAlertsToCountOnly() {
        val summary = OfficialAlertSummaryMapper.map(
            OfficialAlertState.Supported(
                1,
                request,
                listOf(alert("Flood Warning", "Severe"), alert("Heat Advisory", "Minor")),
            ),
        )

        assertEquals(OfficialAlertSummaryPresentation.MultipleAlerts(2), summary)
        assertEquals("2 official alerts.", summary.summaryText)
    }

    @Test
    fun mapsUnsupportedRegionAndPreservesEveryTypedFailureKindAndSafeStatus() {
        val unsupported = OfficialAlertSummaryMapper.map(OfficialAlertState.UnsupportedRegion(1, request))
        assertEquals(OfficialAlertSummaryPresentation.CoverageUnavailable, unsupported)
        assertEquals("Official alert coverage unavailable.", unsupported.summaryText)

        OfficialAlertFailureKind.entries.forEach { kind ->
            val safeStatus = "Safe status for $kind."
            val summary = OfficialAlertSummaryMapper.map(OfficialAlertState.Failed(1, request, kind, safeStatus))

            assertEquals(OfficialAlertSummaryPresentation.Failure(kind, safeStatus), summary)
            assertEquals(safeStatus, summary.summaryText)
        }
    }

    private fun alert(eventName: String, severity: String?) = OfficialAlert(
        issuer = "Test source",
        eventName = eventName,
        severity = severity,
        effectiveAt = null,
        expiresAt = null,
        description = "Not included in summary",
        instructions = "Not included in summary",
        sourceUrl = "https://example.test/alert",
        provenance = DataProvenance(DataType.OFFICIAL_ALERT),
    )
}
