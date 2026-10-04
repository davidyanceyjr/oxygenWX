package com.oxygen.weather.presentation

import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherRepositoryResult
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** A metadata value that keeps absence explicit through the Compose boundary. */
sealed interface MetadataValue {
    data class Available(val value: String) : MetadataValue
    data object Unavailable : MetadataValue
}

data class ProvenanceSourcePresentation(val dataType: String, val source: MetadataValue)

data class ProvenanceTimePresentation(val dataType: String, val instant: MetadataValue)

enum class PresentedDataOrigin { LIVE, CACHED, UNAVAILABLE }

enum class PresentedRefreshOutcome { NONE, FAILED_WITH_RETAINED_DATA, FAILED_WITHOUT_DATA }

/** Source and timing facts are projected from supplied provenance without inferring missing data. */
data class ForecastContextPresentation(
    val sources: List<ProvenanceSourcePresentation>,
    val validTimes: List<ProvenanceTimePresentation>,
    val retrievalTimes: List<ProvenanceTimePresentation>,
    val origin: PresentedDataOrigin,
    val freshness: PresentedFreshness,
    val refreshOutcome: PresentedRefreshOutcome,
    val status: StatusPresentation,
    val horizon: ForecastHorizonPresentation?,
)

object ForecastContextMapper {
    private val instantFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")

    fun map(
        result: WeatherRepositoryResult,
        content: HomePresentationState,
        state: HomeLoadState,
    ): ForecastContextPresentation {
        val bundle = result.bundle
        val provenance = listOf(bundle.currentProvenance, bundle.forecastProvenance).distinct()
        val sources = provenance.map { it.toSourcePresentation() }.distinct()
        val validTimes = provenance.mapNotNull { it.validAt?.let { instant ->
            ProvenanceTimePresentation(it.dataType.displayName(), MetadataValue.Available(format(instant, bundle.location.timeZone)))
        } }
        val retrievalTimes = provenance.mapNotNull { value ->
            value.retrievedAt?.let { instant ->
                ProvenanceTimePresentation(value.dataType.displayName(), MetadataValue.Available(format(instant, bundle.location.timeZone)))
            }
        }.distinctBy { it.instant }
        return ForecastContextPresentation(
            sources = sources.ifEmpty { listOf(ProvenanceSourcePresentation("Weather", MetadataValue.Unavailable)) },
            validTimes = validTimes,
            retrievalTimes = retrievalTimes,
            origin = if (result.origin == WeatherDataOrigin.LIVE) PresentedDataOrigin.LIVE else PresentedDataOrigin.CACHED,
            freshness = when (state) {
                is HomeLoadState.LiveData -> state.freshness
                is HomeLoadState.CachedData -> state.freshness
                is HomeLoadState.RefreshFailedWithRetainedData -> state.freshness
                else -> PresentedFreshness.UNKNOWN
            },
            refreshOutcome = when (state) {
                is HomeLoadState.RefreshFailedWithRetainedData -> PresentedRefreshOutcome.FAILED_WITH_RETAINED_DATA
                is HomeLoadState.FailedWithoutData -> PresentedRefreshOutcome.FAILED_WITHOUT_DATA
                else -> PresentedRefreshOutcome.NONE
            },
            status = state.status(),
            horizon = content.horizonOrNull(),
        )
    }

    fun mapFailure(state: HomeLoadState.FailedWithoutData): ForecastContextPresentation =
        ForecastContextPresentation(
            sources = listOf(ProvenanceSourcePresentation("Weather", MetadataValue.Unavailable)),
            validTimes = emptyList(),
            retrievalTimes = emptyList(),
            origin = PresentedDataOrigin.UNAVAILABLE,
            freshness = PresentedFreshness.UNKNOWN,
            refreshOutcome = PresentedRefreshOutcome.FAILED_WITHOUT_DATA,
            status = state.status,
            horizon = null,
        )

    private fun DataProvenance.toSourcePresentation() = ProvenanceSourcePresentation(
        dataType.displayName(),
        source?.displayName?.takeIf(String::isNotBlank)?.let(MetadataValue::Available) ?: MetadataValue.Unavailable,
    )

    private fun format(instant: Instant, zone: ZoneId): String =
        "${instant.atZone(zone).format(instantFormatter)} ${zone.id}"

    private fun com.oxygen.weather.data.DataType.displayName(): String = when (this) {
        com.oxygen.weather.data.DataType.OBSERVATION -> "Observation"
        com.oxygen.weather.data.DataType.MODEL_ESTIMATE -> "Model estimate"
        com.oxygen.weather.data.DataType.FORECAST -> "Forecast"
        com.oxygen.weather.data.DataType.OFFICIAL_ALERT -> "Official alert"
        com.oxygen.weather.data.DataType.DERIVED -> "Derived"
        com.oxygen.weather.data.DataType.HISTORICAL_REFERENCE -> "Historical reference"
    }

    private fun HomeLoadState.status(): StatusPresentation = when (this) {
        is HomeLoadState.Loading -> status
        is HomeLoadState.LiveData -> status
        is HomeLoadState.CachedData -> status
        is HomeLoadState.RefreshFailedWithRetainedData -> status
        is HomeLoadState.FailedWithoutData -> status
    }

    private fun HomePresentationState.horizonOrNull(): ForecastHorizonPresentation? = when (this) {
        is HomePresentationState.Complete -> null
        is HomePresentationState.Partial -> horizon
        is HomePresentationState.Unavailable -> null
    }
}
