package com.oxygen.weather

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import com.oxygen.weather.application.SelectedLocation
import com.oxygen.weather.application.SelectedLocationReadResult
import com.oxygen.weather.application.SelectedLocationStore
import com.oxygen.weather.application.SelectedLocationWriteResult
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.ZoneId

/** Versioned, single-record selected-location storage. Callers run these operations off the UI thread. */
class SharedPreferencesSelectedLocationStore(context: Context) : SelectedLocationStore {
    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun read(): SelectedLocationReadResult {
        return try {
            val raw = preferences.getString(RECORD_KEY, null) ?: return SelectedLocationReadResult.Empty
            try {
                val json = JSONObject(raw)
                val version = json.opt("version") as? Number
                if (version?.toInt() != SCHEMA_VERSION || version.toDouble() != SCHEMA_VERSION.toDouble()) {
                    return SelectedLocationReadResult.Invalid
                }
                val id = json.opt("localId") as? String ?: return SelectedLocationReadResult.Invalid
                if (id.isBlank()) return SelectedLocationReadResult.Invalid
                if (!json.has("displayName")) return SelectedLocationReadResult.Invalid
                val displayName = if (json.isNull("displayName")) null else {
                    json.opt("displayName") as? String ?: return SelectedLocationReadResult.Invalid
                }
                if (displayName?.isBlank() == true) return SelectedLocationReadResult.Invalid
                val latitude = (json.opt("latitude") as? Number)?.toDouble()
                    ?: return SelectedLocationReadResult.Invalid
                val longitude = (json.opt("longitude") as? Number)?.toDouble()
                    ?: return SelectedLocationReadResult.Invalid
                if (!latitude.isFinite() || latitude !in -90.0..90.0 ||
                    !longitude.isFinite() || longitude !in -180.0..180.0
                ) return SelectedLocationReadResult.Invalid
                val zoneId = ZoneId.of(json.getString("timeZone"))
                SelectedLocationReadResult.Found(
                    SelectedLocation(
                        id = LocalLocationId(id),
                        displayName = displayName,
                        coordinates = GeoCoordinates(latitude, longitude),
                        timeZone = zoneId,
                    ),
                )
            } catch (_: Exception) {
                SelectedLocationReadResult.Invalid
            }
        } catch (_: Exception) {
            SelectedLocationReadResult.Failure
        }
    }

    override fun save(location: SelectedLocation): SelectedLocationWriteResult = try {
        val record = JSONObject()
            .put("version", SCHEMA_VERSION)
            .put("localId", location.id.value)
            .put("displayName", location.displayName ?: JSONObject.NULL)
            .put("latitude", location.coordinates.latitude)
            .put("longitude", location.coordinates.longitude)
            .put("timeZone", location.timeZone.id)
        if (preferences.edit().putString(RECORD_KEY, record.toString()).commit()) {
            SelectedLocationWriteResult.SUCCESS
        } else {
            SelectedLocationWriteResult.FAILURE
        }
    } catch (_: Exception) {
        SelectedLocationWriteResult.FAILURE
    }

    override fun clear(): SelectedLocationWriteResult = try {
        if (preferences.edit().remove(RECORD_KEY).commit()) SelectedLocationWriteResult.SUCCESS else SelectedLocationWriteResult.FAILURE
    } catch (_: Exception) {
        SelectedLocationWriteResult.FAILURE
    }

    private companion object {
        const val PREFERENCES_NAME = "selected_location_v1"
        const val RECORD_KEY = "selection"
        const val SCHEMA_VERSION = 1
    }
}
