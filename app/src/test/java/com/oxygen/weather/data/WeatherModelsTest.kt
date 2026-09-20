package com.oxygen.weather.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class WeatherModelsTest {
    @Test
    fun localIdentityAndSourceIdentityUseTypedValueEquality() {
        assertEquals(LocalLocationId("local-42"), LocalLocationId("local-42"))
        assertEquals(WeatherSourceId("forecast-primary"), WeatherSourceId("forecast-primary"))
        assertNotEquals(LocalLocationId("local-42"), LocalLocationId("local-43"))
        assertNotEquals(WeatherSourceId("forecast-primary"), WeatherSourceId("forecast-fallback"))
    }

    @Test
    fun blankIdentifiersAndDisplayNamesAreRejectedInsteadOfEncodingMissingValues() {
        assertThrows(IllegalArgumentException::class.java) { LocalLocationId(" ") }
        assertThrows(IllegalArgumentException::class.java) { WeatherSourceId("") }
        assertThrows(IllegalArgumentException::class.java) {
            WeatherLocation(LocalLocationId("local-42"), "", ZoneId.of("America/Chicago"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            WeatherSource(WeatherSourceId("forecast-primary"), " ")
        }
    }

    @Test
    fun locationKeepsIanaTimezoneSeparateFromItsLocalIdentityAndDisplayName() {
        val location = WeatherLocation(
            id = LocalLocationId("local-42"),
            displayName = "Harbor",
            timeZone = ZoneId.of("America/Chicago"),
        )

        assertEquals("local-42", location.id.value)
        assertEquals("Harbor", location.displayName)
        assertEquals(ZoneId.of("America/Chicago"), location.timeZone)
    }

    @Test
    fun provenanceKeepsValidityAndRetrievalAsIndependentNullableInstants() {
        val validAt = Instant.parse("2026-09-20T17:00:00Z")
        val retrievedAt = Instant.parse("2026-09-20T17:04:00Z")
        val provenance = DataProvenance(
            dataType = DataType.FORECAST,
            source = WeatherSource(WeatherSourceId("forecast-primary"), "Primary forecast"),
            validAt = validAt,
            retrievedAt = retrievedAt,
        )

        assertEquals(validAt, provenance.validAt)
        assertEquals(retrievedAt, provenance.retrievedAt)
        assertNotEquals(provenance.validAt, provenance.retrievedAt)
        assertEquals("forecast-primary", requireNotNull(provenance.source).id.value)

        val unavailableMetadata = DataProvenance(dataType = DataType.FORECAST)
        assertNull(unavailableMetadata.source)
        assertNull(unavailableMetadata.validAt)
        assertNull(unavailableMetadata.retrievedAt)
    }

    @Test
    fun deterministicFixturePopulatesProviderNeutralLocationAndMetadata() {
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))

        assertEquals(LocalLocationId("demo-station"), bundle.location.id)
        assertEquals("Demo Station", bundle.location.displayName)
        assertEquals(ZoneId.of("America/Chicago"), bundle.location.timeZone)
        assertEquals(DataType.MODEL_ESTIMATE, bundle.currentProvenance.dataType)
        assertEquals(DataType.FORECAST, bundle.forecastProvenance.dataType)
        assertEquals("development-fixture", requireNotNull(bundle.currentProvenance.source).id.value)
        assertEquals(bundle.current.observedAt.atZone(bundle.location.timeZone).toInstant(), bundle.currentProvenance.validAt)
        assertEquals(bundle.hourly.first().time.atZone(bundle.location.timeZone).toInstant(), bundle.forecastProvenance.validAt)
        assertEquals(bundle.currentProvenance.retrievedAt, bundle.forecastProvenance.retrievedAt)
    }
}
