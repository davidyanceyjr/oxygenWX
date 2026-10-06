package com.oxygen.weather.presentation

import com.oxygen.weather.application.OfficialAlertFailureKind
import com.oxygen.weather.application.OfficialAlertState

/** A concise projection of the selected request's authoritative alert lookup state. */
sealed interface OfficialAlertSummaryPresentation {
    /** Text announced and shown by the summary surface. */
    val summaryText: String

    data object Checking : OfficialAlertSummaryPresentation {
        override val summaryText: String = "Checking for official alerts."
    }

    data object NoActiveAlerts : OfficialAlertSummaryPresentation {
        override val summaryText: String = "No active official alerts."
    }

    data class SingleAlert(
        val eventName: String,
        val severity: String?,
    ) : OfficialAlertSummaryPresentation {
        override val summaryText: String = buildString {
            append("Official alert: ")
            append(eventName)
            append('.')
            severity?.let {
                append(" Severity: ")
                append(it)
                append('.')
            }
        }
    }

    /** Count only: choosing or ranking an alert is outside the summary's contract. */
    data class MultipleAlerts(val count: Int) : OfficialAlertSummaryPresentation {
        init {
            require(count > 1) { "Multiple-alert summaries require at least two alerts." }
        }

        override val summaryText: String = "$count official alerts."
    }

    data object CoverageUnavailable : OfficialAlertSummaryPresentation {
        override val summaryText: String = "Official alert coverage unavailable."
    }

    data class Failure(
        val kind: OfficialAlertFailureKind,
        /** Safe status supplied by the controller; transport exception details are not retained. */
        val status: String,
    ) : OfficialAlertSummaryPresentation {
        override val summaryText: String get() = status
    }
}

/** Maps only normalized controller state; forecast content cannot create an alert summary. */
object OfficialAlertSummaryMapper {
    fun map(state: OfficialAlertState): OfficialAlertSummaryPresentation = when (state) {
        is OfficialAlertState.Loading -> OfficialAlertSummaryPresentation.Checking
        is OfficialAlertState.Supported -> when (state.alerts.size) {
            0 -> OfficialAlertSummaryPresentation.NoActiveAlerts
            1 -> state.alerts.single().let { alert ->
                OfficialAlertSummaryPresentation.SingleAlert(alert.eventName, alert.severity)
            }
            else -> OfficialAlertSummaryPresentation.MultipleAlerts(state.alerts.size)
        }
        is OfficialAlertState.UnsupportedRegion -> OfficialAlertSummaryPresentation.CoverageUnavailable
        is OfficialAlertState.Failed -> OfficialAlertSummaryPresentation.Failure(state.kind, state.status)
    }
}
