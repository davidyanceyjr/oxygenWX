package com.oxygen.weather.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
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

    @Test
    fun weatherRecordsPreserveMissingSourceValuesAndRejectNonFiniteNumbers() {
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))
        val partial = bundle.current.copy(
            condition = null,
            temperatureC = null,
            precipitationMmPerHr = null,
        )

        assertNull(partial.condition)
        assertNull(partial.temperatureC)
        assertNull(partial.precipitationMmPerHr)
        assertThrows(IllegalArgumentException::class.java) {
            bundle.current.copy(temperatureC = Double.NaN)
        }
        assertThrows(IllegalArgumentException::class.java) {
            bundle.hourly.first().copy(precipitationMm = Double.POSITIVE_INFINITY)
        }
        assertThrows(IllegalArgumentException::class.java) {
            bundle.daily.first().copy(lowC = Double.NEGATIVE_INFINITY)
        }
    }

    @Test
    fun bundleRequiresNonDecreasingChronologyButPreservesSparseAndDuplicateEntries() {
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))
        val first = bundle.hourly.first().copy(condition = null, temperatureC = null)
        val duplicate = first.copy()

        val sparseWithDuplicate = bundle.copy(hourly = listOf(first, duplicate))
        assertEquals(listOf(first, duplicate), sparseWithDuplicate.hourly)
        assertThrows(IllegalArgumentException::class.java) {
            bundle.copy(hourly = listOf(bundle.hourly[1], bundle.hourly[0]))
        }
        assertThrows(IllegalArgumentException::class.java) {
            bundle.copy(daily = listOf(bundle.daily[1], bundle.daily[0]))
        }
    }

    @Test
    fun officialAlertsPreserveNullableSourceFieldsAndRequireOfficialProvenance() {
        val provenance = DataProvenance(
            dataType = DataType.OFFICIAL_ALERT,
            source = WeatherSource(WeatherSourceId("alert-authority"), "Alert authority"),
        )
        val alert = OfficialAlert(
            issuer = "National Weather Service",
            eventName = "Flood Watch",
            severity = null,
            effectiveAt = null,
            expiresAt = null,
            description = null,
            instructions = null,
            sourceUrl = null,
            provenance = provenance,
        )

        assertNull(alert.severity)
        assertNull(alert.sourceUrl)
        assertEquals(DataType.OFFICIAL_ALERT, alert.provenance.dataType)
        assertThrows(IllegalArgumentException::class.java) {
            alert.copy(provenance = provenance.copy(dataType = DataType.FORECAST))
        }
    }

    @Test
    fun repositoryResultKeepsUsableLiveDataWhenCacheWritingFails() {
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))
        val result = WeatherRepositoryResult(
            bundle = bundle,
            origin = WeatherDataOrigin.LIVE,
            freshness = WeatherFreshness.CURRENT,
            refreshFailure = null,
            cacheWriteOutcome = CacheWriteOutcome.FAILED,
        )
        val cached = result.copy(
            origin = WeatherDataOrigin.CACHE,
            freshness = WeatherFreshness.STALE,
            refreshFailure = RefreshFailure(RefreshFailureKind.NETWORK, Instant.parse("2026-09-20T18:00:00Z")),
            cacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED,
        )

        assertSame(bundle, result.bundle)
        assertEquals(WeatherDataOrigin.LIVE, result.origin)
        assertEquals(CacheWriteOutcome.FAILED, result.cacheWriteOutcome)
        assertEquals(WeatherDataOrigin.CACHE, cached.origin)
        assertEquals(WeatherFreshness.STALE, cached.freshness)
        assertEquals(RefreshFailureKind.NETWORK, requireNotNull(cached.refreshFailure).kind)
    }

    @Test
    fun completeFixtureRetainsHorizonSizesAndChronology() {
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))

        assertEquals(72, bundle.hourly.size)
        assertEquals(10, bundle.daily.size)
        assertTrue(bundle.hourly.zipWithNext().all { !it.second.time.isBefore(it.first.time) })
        assertTrue(bundle.daily.zipWithNext().all { !it.second.date.isBefore(it.first.date) })
    }
}
