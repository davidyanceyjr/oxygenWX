package com.oxygen.weather.ui

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/** Observes the animator scale without coupling the presentation policy to Android APIs. */
@Composable
internal fun rememberSystemAnimatorScale(overrideScale: Float?): Float? {
    if (overrideScale != null) return overrideScale

    val context = LocalContext.current.applicationContext
    val scale = remember(context) { mutableFloatStateOf(readAnimatorScale(context)) }
    DisposableEffect(context) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                scale.floatValue = readAnimatorScale(context)
            }

            override fun onChange(selfChange: Boolean, uri: android.net.Uri?) {
                scale.floatValue = readAnimatorScale(context)
            }
        }
        val uri = Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE)
        context.contentResolver.registerContentObserver(uri, false, observer)
        scale.floatValue = readAnimatorScale(context)
        onDispose { context.contentResolver.unregisterContentObserver(observer) }
    }
    return scale.floatValue
}

private fun readAnimatorScale(context: android.content.Context): Float =
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    )
