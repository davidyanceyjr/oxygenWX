package com.oxygen.weather.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.data.provider.GeoCoordinates
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidForecastCacheAtomicFileTest {
    @Test
    fun interruptedAtomicReplacementKeepsPriorSnapshotForNewCore() = withUniqueSnapshotFile { file ->
        val original = forecast(temperatureC = 8.0)
        val replacement = forecast(temperatureC = 19.0)
        val core = core(file)

        assertEquals(ForecastCacheWriteResult.Success, core.write(original, requestCoordinates))
        core.failNextWriteAfterPrefix { throw java.io.IOException("Injected partial write failure.") }

        assertEquals(ForecastCacheWriteResult.WriteFailure, core.write(replacement, requestCoordinates))

        val reopened = core(file)
        val read = reopened.read(original.location.id)
        assertTrue("Expected the prior forecast after reopening", read is ForecastCacheReadResult.Found)
        assertEquals(original, (read as ForecastCacheReadResult.Found).record.forecast)
    }

    @Test
    fun successfulAtomicReplacementIsReadableAfterReopening() = withUniqueSnapshotFile { file ->
        val original = forecast(temperatureC = 8.0)
        val replacement = forecast(temperatureC = 19.0)
        val core = core(file)

        assertEquals(ForecastCacheWriteResult.Success, core.write(original, requestCoordinates))
        assertEquals(ForecastCacheWriteResult.Success, core.write(replacement, requestCoordinates))

        val reopened = core(file)
        val read = reopened.read(replacement.location.id)
        assertTrue("Expected the replacement forecast after reopening", read is ForecastCacheReadResult.Found)
        assertEquals(replacement, (read as ForecastCacheReadResult.Found).record.forecast)
    }

    private fun core(file: File) = ForecastCacheStoreCore(AtomicForecastCacheSnapshotFile(file))

    private inline fun withUniqueSnapshotFile(block: (File) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.filesDir, "forecast-cache-atomic-test-${UUID.randomUUID()}.bin")
        try {
            block(file)
        } finally {
            file.delete()
            File(file.path + ".bak").delete()
            File(file.path + ".new").delete()
        }
    }

    private fun forecast(temperatureC: Double) = ForecastData(
        location = WeatherLocation(LocalLocationId("atomic-file-location"), "Test Location", ZoneId.of("UTC")),
        hourly = listOf(
            HourWeather(
                time = LocalDateTime.of(2026, 10, 5, 12, 0),
                condition = WeatherCondition.CLOUDY,
                temperatureC = temperatureC,
                dewPointC = null,
                pressureHpa = null,
                windSpeedKph = null,
                precipitationProbabilityPct = null,
                precipitationMm = null,
                cloudCoverPct = null,
            ),
        ),
        daily = emptyList(),
        provenance = DataProvenance(DataType.FORECAST, validAt = Instant.parse("2026-10-05T12:00:00Z")),
    )

    private companion object {
        val requestCoordinates = GeoCoordinates(41.0, -87.0)
    }
}
