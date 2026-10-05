package com.oxygen.weather.platform

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import com.oxygen.weather.application.CoarseLocationPoint
import com.oxygen.weather.application.ForegroundLocationAcquirer
import com.oxygen.weather.application.ForegroundLocationResult
import com.oxygen.weather.application.LocationAcquisitionCancellation
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

/** One coarse, foreground-only location attempt without a Play Services dependency. */
class AndroidForegroundLocationAcquirer(context: Context) : ForegroundLocationAcquirer {
    private val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val handler = Handler(Looper.getMainLooper())

    override fun acquire(callback: (ForegroundLocationResult) -> Unit): LocationAcquisitionCancellation {
        val attempt = Attempt(callback)
        attempt.begin()
        return LocationAcquisitionCancellation { attempt.cancel() }
    }

    private inner class Attempt(private val callback: (ForegroundLocationResult) -> Unit) {
        private val completed = AtomicBoolean(false)
        private val cancellationSignal = if (Build.VERSION.SDK_INT >= 30) CancellationSignal() else null
        private var listener: LocationListener? = null
        private val timeout = Runnable { finish(ForegroundLocationResult.TimedOut) }

        fun begin() {
            handler.postDelayed(timeout, TIMEOUT_MILLIS)
            try {
                val criteria = Criteria().apply {
                    accuracy = Criteria.ACCURACY_COARSE
                    powerRequirement = Criteria.POWER_LOW
                    isAltitudeRequired = false
                    isBearingRequired = false
                    isSpeedRequired = false
                }
                val provider = if (Build.VERSION.SDK_INT >= 30) {
                    // Prefer the low-power network source; GPS is a one-shot fallback on
                    // devices/emulators where a network provider is unavailable. Coarse-only
                    // permission still lets Android obfuscate a GPS-derived point.
                    sequenceOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
                        .firstOrNull { name -> runCatching { manager.isProviderEnabled(name) }.getOrDefault(false) }
                        ?: manager.getBestProvider(criteria, true)
                } else {
                    manager.getBestProvider(criteria, true)
                }
                if (provider == null) {
                    finish(ForegroundLocationResult.Unavailable)
                    return
                }
                if (Build.VERSION.SDK_INT >= 30) {
                    manager.getCurrentLocation(provider, cancellationSignal, Executor { command -> handler.post(command) }) { location ->
                        finish(location.toResult())
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val oneShotListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) = finish(location.toResult())
                        @Deprecated("Deprecated by Android") override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) = Unit
                        override fun onProviderEnabled(provider: String) = Unit
                        override fun onProviderDisabled(provider: String) = finish(ForegroundLocationResult.Unavailable)
                    }
                    listener = oneShotListener
                    @Suppress("DEPRECATION")
                    manager.requestSingleUpdate(criteria, oneShotListener, Looper.getMainLooper())
                }
            } catch (_: SecurityException) {
                finish(ForegroundLocationResult.Unavailable)
            } catch (_: IllegalArgumentException) {
                finish(ForegroundLocationResult.Unavailable)
            } catch (_: RuntimeException) {
                finish(ForegroundLocationResult.Failed)
            }
        }

        fun cancel() {
            if (!completed.compareAndSet(false, true)) return
            cleanup()
        }

        private fun finish(result: ForegroundLocationResult) {
            if (!completed.compareAndSet(false, true)) return
            cleanup()
            callback(result)
        }

        private fun cleanup() {
            handler.removeCallbacks(timeout)
            cancellationSignal?.cancel()
            listener?.let { try { manager.removeUpdates(it) } catch (_: SecurityException) { } }
            listener = null
        }

        private fun Location?.toResult(): ForegroundLocationResult {
            if (this == null) return ForegroundLocationResult.Unavailable
            val latitudeValue = latitude
            val longitudeValue = longitude
            if (!latitudeValue.isFinite() || latitudeValue !in -90.0..90.0 ||
                !longitudeValue.isFinite() || longitudeValue !in -180.0..180.0
            ) return ForegroundLocationResult.Unavailable
            return ForegroundLocationResult.Point(CoarseLocationPoint(latitudeValue, longitudeValue))
        }
    }

    private companion object { const val TIMEOUT_MILLIS = 20_000L }
}
