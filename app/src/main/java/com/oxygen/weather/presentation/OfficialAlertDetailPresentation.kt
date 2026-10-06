package com.oxygen.weather.presentation

import com.oxygen.weather.application.OfficialAlertState
import com.oxygen.weather.data.OfficialAlert
import java.net.URI
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Display-ready details for a single source-supplied official alert. */
data class OfficialAlertDetailPresentation(
    val issuer: String,
    val eventName: String,
    val severity: String?,
    val effectiveAtText: String?,
    val expiresAtText: String?,
    val description: String?,
    val instructions: String?,
    /** Original source value, retained as text independently of whether it can be opened safely. */
    val sourceUrlText: String?,
    /** An explicit user action is offered only for validated absolute HTTP(S) links. */
    val sourceAction: OfficialAlertSourceAction?,
)

data class OfficialAlertSourceAction(
    val url: String,
    val label: String,
)

/** Projects a detail only when the selected generation has one unambiguous supported alert. */
object OfficialAlertDetailMapper {
    private val timeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a z", Locale.US)
    private const val SOURCE_ACTION_LABEL = "View official alert"

    fun map(state: OfficialAlertState): OfficialAlertDetailPresentation? {
        if (state !is OfficialAlertState.Supported || state.alerts.size != 1) return null
        val alert = state.alerts.single()
        val zone = state.request.location.timeZone
        return alert.toPresentation(zone)
    }

    private fun OfficialAlert.toPresentation(zone: ZoneId) = OfficialAlertDetailPresentation(
        issuer = issuer,
        eventName = eventName,
        severity = severity,
        effectiveAtText = effectiveAt?.formatIn(zone),
        expiresAtText = expiresAt?.formatIn(zone),
        description = description,
        instructions = instructions,
        sourceUrlText = sourceUrl,
        sourceAction = sourceUrl?.takeIf(::isSafeHttpUrl)?.let { url ->
            OfficialAlertSourceAction(url = url, label = SOURCE_ACTION_LABEL)
        },
    )

    private fun Instant.formatIn(zone: ZoneId): String = timeFormatter.format(atZone(zone))

    private fun isSafeHttpUrl(value: String): Boolean = try {
        val uri = URI(value)
        uri.isAbsolute &&
            (uri.scheme.equals("http", ignoreCase = true) || uri.scheme.equals("https", ignoreCase = true)) &&
            !uri.host.isNullOrBlank() && uri.rawUserInfo == null
    } catch (_: Exception) {
        false
    }
}
