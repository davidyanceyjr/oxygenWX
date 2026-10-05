package com.oxygen.weather.data

import android.content.Context
import android.util.AtomicFile
import com.oxygen.weather.data.provider.GeoCoordinates
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/** Single-snapshot forecast cache. Callers should perform reads and writes off the UI thread. */
class AndroidForecastCacheStore(context: Context) : ForecastCacheStore {
    private val core = ForecastCacheStoreCore(
        AtomicForecastCacheSnapshotFile(
            File(context.applicationContext.filesDir, FILE_NAME),
        ),
    )

    override fun read(id: LocalLocationId): ForecastCacheReadResult = core.read(id)

    override fun write(
        forecast: ForecastData,
        requestCoordinates: GeoCoordinates,
    ): ForecastCacheWriteResult = core.write(forecast, requestCoordinates)

    internal fun failNextWriteAfterPrefixForTest() {
        core.failNextWriteAfterPrefix { throw IOException("Injected forecast cache write failure.") }
    }

    private companion object {
        const val FILE_NAME = "normalized_forecast_cache.bin"
    }
}

internal class AtomicForecastCacheSnapshotFile(
    file: File,
) : ForecastCacheSnapshotFile {
    private val atomicFile = AtomicFile(file)

    override fun read(): ByteArray? {
        val input = try {
            atomicFile.openRead()
        } catch (_: FileNotFoundException) {
            return null
        }
        return input.use(InputStream::readBytes)
    }

    override fun writeAtomically(bytes: ByteArray, beforeRemainder: (() -> Unit)?) {
        var output: java.io.FileOutputStream? = null
        try {
            output = atomicFile.startWrite()
            if (beforeRemainder != null && bytes.isNotEmpty()) {
                val prefixLength = (bytes.size / 2).coerceAtLeast(1).coerceAtMost(bytes.size)
                output.write(bytes, 0, prefixLength)
                beforeRemainder()
                if (prefixLength < bytes.size) output.write(bytes, prefixLength, bytes.size - prefixLength)
            } else {
                output.write(bytes)
            }
            atomicFile.finishWrite(output)
        } catch (failure: Exception) {
            output?.let { atomicFile.failWrite(it) }
            if (failure is IOException) throw failure
            throw IOException("Unable to commit forecast cache snapshot.", failure)
        }
    }
}
