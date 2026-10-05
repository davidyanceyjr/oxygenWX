package com.oxygen.weather.data

import com.oxygen.weather.data.provider.GeoCoordinates
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ForecastCacheStoreTest {
    @Test
    fun codecRoundTripsPopulatedAndSparseForecastsExactly() {
        val records = listOf(
            ForecastCacheRecord(populatedForecast("complete"), GeoCoordinates(41.88, -87.63), at("2026-10-05T11:00:00Z")),
            ForecastCacheRecord(sparseForecast("sparse"), GeoCoordinates(-33.9, 151.2), at("2026-10-05T11:00:01Z")),
        )
        assertEquals(records, ForecastCacheCodec.decode(ForecastCacheCodec.encode(records)))
    }

    @Test
    fun cacheUsesOpaqueIdsAndReplacesIdentityWithoutChangingCapacityOrder() {
        val file = MemorySnapshotFile()
        val store = ForecastCacheStoreCore(file, Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")))
        val first = populatedForecast("same", retrievedAt = at("2026-10-05T10:00:00Z"))
        assertEquals(ForecastCacheWriteResult.Success, store.write(first, GeoCoordinates(1.0, 2.0)))
        assertEquals(ForecastCacheWriteResult.Success, store.write(first.copy(location = first.location.copy(displayName = "Renamed")), GeoCoordinates(3.0, 4.0)))
        assertEquals(1, file.records().size)
        val found = store.read(LocalLocationId("same")) as ForecastCacheReadResult.Found
        assertEquals("Renamed", found.record.forecast.location.displayName)
        assertEquals(GeoCoordinates(3.0, 4.0), found.record.requestCoordinates)
        assertEquals(ForecastCacheReadResult.Absent, store.read(LocalLocationId("other")))
    }

    @Test
    fun retainsFiftyLatestSuccessfulWritesAndEvictsOldestWriteOrder() {
        val file = MemorySnapshotFile()
        val store = ForecastCacheStoreCore(file, Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")))
        // Equal cache timestamps deliberately prove eviction follows successful write order.
        repeat(51) { index ->
            assertEquals(ForecastCacheWriteResult.Success, store.write(sparseForecast("id-$index"), GeoCoordinates(0.0, 0.0)))
        }
        assertEquals(ForecastCacheReadResult.Absent, store.read(LocalLocationId("id-0")))
        assertTrue(store.read(LocalLocationId("id-1")) is ForecastCacheReadResult.Found)
        assertTrue(store.read(LocalLocationId("id-50")) is ForecastCacheReadResult.Found)
        assertEquals(50, file.records().size)
    }

    @Test
    fun malformedUnsupportedAndTrailingSnapshotsAreInvalidAndNotOverwritten() {
        listOf(byteArrayOf(1, 2), ForecastCacheCodec.encode(emptyList()) + byteArrayOf(9)).forEach { bytes ->
            val file = MemorySnapshotFile(bytes)
            val store = ForecastCacheStoreCore(file)
            assertEquals(ForecastCacheReadResult.Invalid, store.read(LocalLocationId("x")))
            assertEquals(ForecastCacheWriteResult.InvalidInput, store.write(sparseForecast("new"), GeoCoordinates(0.0, 0.0)))
            assertEquals(bytes.toList(), file.bytes?.toList())
        }
        val unsupported = ForecastCacheCodec.encode(emptyList()).also { it[7] = 2 }
        assertEquals(ForecastCacheReadResult.Invalid, ForecastCacheStoreCore(MemorySnapshotFile(unsupported)).read(LocalLocationId("x")))
    }

    @Test
    fun readFailureAndCommitFailureAreExplicitAndFailedReplacementPreservesOldRecord() {
        val file = MemorySnapshotFile()
        val store = ForecastCacheStoreCore(file)
        val original = populatedForecast("id")
        assertEquals(ForecastCacheWriteResult.Success, store.write(original, GeoCoordinates(0.0, 0.0)))
        file.failNextWrite = true
        assertEquals(ForecastCacheWriteResult.WriteFailure, store.write(original.copy(location = original.location.copy(displayName = "new")), GeoCoordinates(1.0, 1.0)))
        assertEquals(original, (store.read(LocalLocationId("id")) as ForecastCacheReadResult.Found).record.forecast)
        file.failRead = true
        assertEquals(ForecastCacheReadResult.Failure, store.read(LocalLocationId("id")))
        assertEquals(ForecastCacheWriteResult.ReadFailure, store.write(sparseForecast("other"), GeoCoordinates(0.0, 0.0)))
    }

    @Test
    fun injectedPartialWriteFailureLeavesCommittedSnapshotAndConsumesFailpointOnce() {
        val file = MemorySnapshotFile()
        val store = ForecastCacheStoreCore(file)
        val original = populatedForecast("id")
        store.write(original, GeoCoordinates(0.0, 0.0))
        store.failNextWriteAfterPrefix { throw IOException("injected") }
        assertEquals(ForecastCacheWriteResult.WriteFailure, store.write(sparseForecast("id"), GeoCoordinates(2.0, 3.0)))
        assertEquals(original, (store.read(LocalLocationId("id")) as ForecastCacheReadResult.Found).record.forecast)
        assertEquals(ForecastCacheWriteResult.Success, store.write(sparseForecast("id"), GeoCoordinates(2.0, 3.0)))
    }

    @Test
    fun invalidInputAndWrongEmbeddedSnapshotKeyFailClosed() {
        val invalid = assertIllegalArgument { GeoCoordinates(Double.NaN, 0.0) }
        assertTrue(invalid)
        val good = ForecastCacheCodec.encode(listOf(ForecastCacheRecord(sparseForecast("actual"), GeoCoordinates(0.0, 0.0), Instant.EPOCH)))
        val altered = good.copyOf()
        // Snapshot key is the first UTF string after the 12-byte header.
        altered[16] = 'x'.code.toByte()
        assertEquals(ForecastCacheReadResult.Invalid, ForecastCacheStoreCore(MemorySnapshotFile(altered)).read(LocalLocationId("actual")))
    }

    private class MemorySnapshotFile(initial: ByteArray? = null) : ForecastCacheSnapshotFile {
        var bytes: ByteArray? = initial?.copyOf()
        var failRead = false
        var failNextWrite = false
        override fun read(): ByteArray? {
            if (failRead) throw IOException("read failed")
            return bytes?.copyOf()
        }
        override fun writeAtomically(bytes: ByteArray, beforeRemainder: (() -> Unit)?) {
            if (failNextWrite) { failNextWrite = false; throw IOException("commit failed") }
            beforeRemainder?.invoke()
            this.bytes = bytes.copyOf()
        }
        fun records() = bytes?.let(ForecastCacheCodec::decode).orEmpty()
    }

    private fun populatedForecast(id: String, retrievedAt: Instant? = at("2026-10-05T10:30:00Z")) = ForecastData(
        location = WeatherLocation(LocalLocationId(id), "Sample $id", ZoneId.of("America/Chicago")),
        hourly = listOf(
            HourWeather(LocalDateTime.parse("2026-10-05T06:00"), WeatherCondition.CLEAR, 12.25, 5.0, 1013.2, 8.0, 0.0, 0.0, 10.0),
            HourWeather(LocalDateTime.parse("2026-10-05T07:00"), WeatherCondition.RAIN, 11.5, 6.0, 1012.8, 9.0, 80.0, 1.25, 100.0),
        ),
        daily = listOf(DayWeather(LocalDate.parse("2026-10-05"), WeatherCondition.PARTLY_CLOUDY, 4.0, 14.0, 20.0, 0.5, 22.0, 5.25)),
        provenance = DataProvenance(DataType.FORECAST, WeatherSource(WeatherSourceId("open-meteo"), "Open-Meteo"), at("2026-10-05T12:00:00Z"), retrievedAt),
    )

    private fun sparseForecast(id: String) = ForecastData(
        location = WeatherLocation(LocalLocationId(id), null, ZoneId.of("UTC")),
        hourly = listOf(HourWeather(LocalDateTime.parse("2026-10-05T00:00"), null, null, null, null, null, null, null, null)),
        daily = listOf(DayWeather(LocalDate.parse("2026-10-05"), WeatherCondition.SNOW, null, -0.0, null, null, null, null)),
        provenance = DataProvenance(DataType.FORECAST),
    )

    private fun at(value: String) = Instant.parse(value)
    private fun assertIllegalArgument(block: () -> Unit): Boolean = try { block(); false } catch (_: IllegalArgumentException) { true }
}
