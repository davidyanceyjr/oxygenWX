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
) {
    /** Concise spoken identity and source-supplied timing/severity facts for the detail heading. */
    val spokenSummary: String get() = buildString {
        append("Official alert. Event: ")
        append(eventName)
        append(". Issuer: ")
        append(issuer)
        severity?.let { append(". Severity: ").append(it) }
        effectiveAtText?.let { append(". Effective: ").append(it) }
        expiresAtText?.let { append(". Expires: ").append(it) }
        append('.')
    }
}

data class OfficialAlertSourceAction(
    val url: String,
    val label: String,
)

/** A source-ordered alert choice bound to one exact published controller result. */
data class OfficialAlertChoicePresentation(
    val generation: Long,
    val inResultIndex: Int,
    val eventName: String,
    val severity: String?,
    val detail: OfficialAlertDetailPresentation,
) {
    val selectionLabel: String get() = buildString {
        append(eventName)
        severity?.let { append(". Severity: ").append(it) }
    }
}

/** Resolves only within the currently published result-scoped presentation choices. */
object OfficialAlertChoiceResolver {
    fun resolve(
        choices: List<OfficialAlertChoicePresentation>,
        generation: Long,
        inResultIndex: Int,
    ): OfficialAlertChoicePresentation? = choices.firstOrNull {
        it.generation == generation && it.inResultIndex == inResultIndex
    }
}

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

    /** Maps each supported alert in authoritative order; identity is scoped to this result generation. */
    fun mapChoices(state: OfficialAlertState): List<OfficialAlertChoicePresentation> {
        if (state !is OfficialAlertState.Supported || state.alerts.size < 2) return emptyList()
        val zone = state.request.location.timeZone
        return state.alerts.mapIndexed { index, alert ->
            OfficialAlertChoicePresentation(
                generation = state.generation,
                inResultIndex = index,
                eventName = alert.eventName,
                severity = alert.severity,
                detail = alert.toPresentation(zone),
            )
        }
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
