package com.oxygen.weather.data.alerts

import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.OfficialAlert
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.WeatherSource
import com.oxygen.weather.data.WeatherSourceId
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class OfficialAlertRepositoryTest {
    @Test fun delegatesExactRequestAndPreservesCompleteSupportedResult() {
        val request = OfficialAlertRequest(
            WeatherLocation(LocalLocationId("selected"), "Example", ZoneId.of("UTC")),
            GeoCoordinates(40.25, -75.5),
        )
        val provenance = DataProvenance(
            DataType.OFFICIAL_ALERT,
            WeatherSource(WeatherSourceId("authority"), "Official authority"),
            validAt = Instant.parse("2026-10-05T12:00:00Z"),
            retrievedAt = Instant.parse("2026-10-05T12:01:00Z"),
        )
        val alert = OfficialAlert(
            issuer = "Official authority", eventName = "Flood Watch", severity = "Moderate",
            effectiveAt = provenance.validAt, expiresAt = Instant.parse("2026-10-05T18:00:00Z"),
            description = "Flooding is possible.", instructions = "Monitor conditions.",
            sourceUrl = "https://alerts.example.test/1", provenance = provenance,
        )
        val result = OfficialAlertProviderResult.Supported(listOf(alert))
        var received: OfficialAlertRequest? = null
        val repository = ProviderOfficialAlertRepository(OfficialAlertProvider { query ->
            received = query
            result
        })

        val actual = repository.fetch(request)

        assertEquals(request, received)
        assertEquals(result, actual)
        assertEquals(listOf(alert), (actual as OfficialAlertProviderResult.Supported).alerts)
    }

    @Test fun preservesEmptyUnsupportedAndFailureResults() {
        val request = OfficialAlertRequest(
            WeatherLocation(LocalLocationId("selected"), "Example", ZoneId.of("UTC")),
            GeoCoordinates(40.0, -75.0),
        )
        val expected = listOf(
            OfficialAlertProviderResult.Supported(emptyList()),
            OfficialAlertProviderResult.UnsupportedRegion,
            OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.TRANSPORT),
        )
        expected.forEach { result ->
            assertEquals(result, ProviderOfficialAlertRepository(OfficialAlertProvider { result }).fetch(request))
        }
    }
}
