package com.oxygen.weather.application

import com.oxygen.weather.data.alerts.OfficialAlertRepository
import com.oxygen.weather.data.alerts.OfficialAlertProviderResult
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.OfficialAlert
import java.util.concurrent.Executor

enum class OfficialAlertFailureKind { TRANSPORT, SOURCE, UNKNOWN }

/** State for the selected location's independent official-alert lookup. */
sealed interface OfficialAlertState {
    val generation: Long
    val request: OfficialAlertRequest

    data class Loading(override val generation: Long, override val request: OfficialAlertRequest) : OfficialAlertState
    data class Supported(
        override val generation: Long,
        override val request: OfficialAlertRequest,
        val alerts: List<OfficialAlert>,
    ) : OfficialAlertState
    data class UnsupportedRegion(override val generation: Long, override val request: OfficialAlertRequest) : OfficialAlertState
    data class Failed(
        override val generation: Long,
        override val request: OfficialAlertRequest,
        val kind: OfficialAlertFailureKind,
        val status: String,
    ) : OfficialAlertState
}

/** Arbitrates alert requests independently of forecast work. Provider calls run on [executor]. */
class OfficialAlertController(
    private val repository: OfficialAlertRepository,
    private val executor: Executor,
    private val onStateChanged: (OfficialAlertState) -> Unit = {},
) {
    private val lock = Any()
    private var nextGeneration = 0L
    @Volatile private var currentState: OfficialAlertState? = null

    fun state(): OfficialAlertState? = currentState

    /** The invocation order defines the selected request, independent of executor ordering. */
    fun fetch(request: OfficialAlertRequest): Long {
        val loading = synchronized(lock) {
            val generation = ++nextGeneration
            OfficialAlertState.Loading(generation, request).also {
                currentState = it
                // Publish while holding the same lock used by terminal transitions. This also
                // makes Loading observable before work can start, even for a direct executor.
                onStateChanged(it)
            }
        }
        try {
            executor.execute {
                val result = try {
                    repository.fetch(request)
                } catch (_: Exception) {
                    OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.UNKNOWN)
                }
                val terminal = when (result) {
                    is OfficialAlertProviderResult.Supported -> OfficialAlertState.Supported(loading.generation, request, result.alerts)
                    OfficialAlertProviderResult.UnsupportedRegion -> OfficialAlertState.UnsupportedRegion(loading.generation, request)
                    is OfficialAlertProviderResult.Failure -> result.category.toFailureKind().let { kind ->
                        OfficialAlertState.Failed(loading.generation, request, kind, kind.safeStatus())
                    }
                }
                complete(loading.generation, terminal)
            }
        } catch (_: Exception) {
            complete(
                loading.generation,
                OfficialAlertState.Failed(
                    loading.generation,
                    request,
                    OfficialAlertFailureKind.UNKNOWN,
                    OfficialAlertFailureKind.UNKNOWN.safeStatus(),
                ),
            )
        }
        return loading.generation
    }

    private fun complete(generation: Long, state: OfficialAlertState) {
        synchronized(lock) {
            if (currentState?.generation == generation) {
                currentState = state
                onStateChanged(state)
            }
        }
    }
}

private fun OfficialAlertProviderResult.Category.toFailureKind(): OfficialAlertFailureKind = when (this) {
    OfficialAlertProviderResult.Category.TRANSPORT -> OfficialAlertFailureKind.TRANSPORT
    OfficialAlertProviderResult.Category.SOURCE -> OfficialAlertFailureKind.SOURCE
    OfficialAlertProviderResult.Category.UNKNOWN -> OfficialAlertFailureKind.UNKNOWN
}

private fun OfficialAlertFailureKind.safeStatus(): String = when (this) {
    OfficialAlertFailureKind.TRANSPORT -> "Official alerts could not be reached."
    OfficialAlertFailureKind.SOURCE -> "The official alert source could not provide a result."
    OfficialAlertFailureKind.UNKNOWN -> "Official alerts could not be loaded."
}
