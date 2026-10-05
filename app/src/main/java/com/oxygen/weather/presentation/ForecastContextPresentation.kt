package com.oxygen.weather.presentation

import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
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
    val cachedAt: MetadataValue,
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
            cachedAt = MetadataValue.Unavailable,
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
            cachedAt = MetadataValue.Unavailable,
            status = state.status,
            horizon = null,
        )

    fun mapLive(
        live: LiveWeatherPresentation,
        horizon: ForecastHorizonPresentation?,
        status: StatusPresentation,
    ): ForecastContextPresentation {
        val zone = ZoneId.of(live.timeZoneId)
        val provenance = listOfNotNull(live.currentProvenance, live.forecastProvenance).distinct()
        val sources = provenance.map { it.toSourcePresentation() }.distinct().ifEmpty {
            listOf(ProvenanceSourcePresentation("Forecast", MetadataValue.Available(live.sourceName ?: "Unavailable")))
        }
        val validTimes = provenance.mapNotNull { value ->
            value.validAt?.let { ProvenanceTimePresentation(value.dataType.displayName(), MetadataValue.Available(format(it, zone))) }
        }
        val retrievalTimes = provenance.mapNotNull { value ->
            value.retrievedAt?.let { ProvenanceTimePresentation(value.dataType.displayName(), MetadataValue.Available(format(it, zone))) }
        }.distinctBy { it.instant }.ifEmpty {
            listOf(ProvenanceTimePresentation("Forecast", MetadataValue.Available(format(live.retrievedAt, zone))))
        }
        return ForecastContextPresentation(
            sources = sources,
            validTimes = validTimes,
            retrievalTimes = retrievalTimes,
            origin = PresentedDataOrigin.LIVE,
            freshness = PresentedFreshness.CURRENT,
            refreshOutcome = PresentedRefreshOutcome.NONE,
            cachedAt = MetadataValue.Unavailable,
            status = status,
            horizon = horizon,
        )
    }

    fun mapCached(
        cached: CachedForecastPresentation,
        status: StatusPresentation,
    ): ForecastContextPresentation {
        val zone = ZoneId.of(cached.timeZoneId)
        val provenance = cached.forecastProvenance
        val sources = listOf(provenance.toSourcePresentation())
        val validTimes = provenance.validAt?.let {
            listOf(ProvenanceTimePresentation(provenance.dataType.displayName(), MetadataValue.Available(format(it, zone))))
        }.orEmpty()
        val retrievalTimes = provenance.retrievedAt?.let {
            listOf(ProvenanceTimePresentation(provenance.dataType.displayName(), MetadataValue.Available(format(it, zone))))
        }.orEmpty()
        val hours = cached.hourlyWindows.sumOf { it.entries.size }
        val days = cached.dailyWindows.sumOf { it.entries.size }
        val horizon = if (hours > 0 && days > 0 && (hours < 72 || days < 10)) ForecastHorizonPresentation(
            hourly = if (hours < 72) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
            daily = if (days < 10) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
        ) else null
        return ForecastContextPresentation(
            sources = sources,
            validTimes = validTimes,
            retrievalTimes = retrievalTimes,
            origin = PresentedDataOrigin.CACHED,
            freshness = cached.freshness.toPresentedFreshness(),
            refreshOutcome = PresentedRefreshOutcome.NONE,
            cachedAt = MetadataValue.Available(format(cached.cachedAt, zone)),
            status = status,
            horizon = horizon,
        )
    }

    private fun DataProvenance.toSourcePresentation() = ProvenanceSourcePresentation(
        dataType.displayName(),
        source?.displayName?.takeIf(String::isNotBlank)?.let(MetadataValue::Available) ?: MetadataValue.Unavailable,
    )

    private fun WeatherFreshness.toPresentedFreshness() = when (this) {
        WeatherFreshness.CURRENT -> PresentedFreshness.CURRENT
        WeatherFreshness.STALE -> PresentedFreshness.STALE
        WeatherFreshness.UNKNOWN -> PresentedFreshness.UNKNOWN
    }

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
