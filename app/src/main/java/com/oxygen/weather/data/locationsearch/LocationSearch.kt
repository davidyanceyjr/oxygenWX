package com.oxygen.weather.data.locationsearch

import java.time.ZoneId
import java.util.Locale
import java.util.IllformedLocaleException

/** A manual place-search query. Locale affects translated place text only. */
data class LocationSearchRequest(val query: String, val locale: String? = null) {
    init {
        require(query.isNotBlank()) { "Search query must not be blank." }
        locale?.let { tag ->
            require(tag.isNotBlank()) { "Locale must be a BCP-47 language tag." }
            val parsed = try { Locale.Builder().setLanguageTag(tag).build() }
            catch (_: IllformedLocaleException) { throw IllegalArgumentException("Locale must be a BCP-47 language tag.") }
            require(parsed.language.isNotBlank() && parsed.toLanguageTag() != "und") { "Locale must be a BCP-47 language tag." }
        }
    }

    fun canonicalLocale(): String? = locale?.let { Locale.Builder().setLanguageTag(it).build().toLanguageTag().lowercase(Locale.ROOT) }
}

/** Search result identity remains provider identity, not a selected local location ID. */
data class LocationCandidate(
    val providerId: Long,
    val displayName: String,
    val latitude: Double,
    val longitude: Double,
    val timeZone: ZoneId,
    val admin1: String? = null,
    val admin2: String? = null,
    val admin3: String? = null,
    val admin4: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
) {
    init {
        require(providerId > 0) { "Provider place ID must be positive." }
        require(displayName.isNotBlank()) { "Place display name must not be blank." }
        require(latitude.isFinite() && latitude in -90.0..90.0) { "Latitude is outside geographic bounds." }
        require(longitude.isFinite() && longitude in -180.0..180.0) { "Longitude is outside geographic bounds." }
        require(timeZone.id in ZoneId.getAvailableZoneIds()) { "Location timezone must be an IANA zone ID." }
        listOf(admin1, admin2, admin3, admin4, country, countryCode).forEach {
            require(it == null || it.isNotBlank()) { "Optional place identity fields must be null or nonblank." }
        }
    }
}

sealed interface LocationSearchResult {
    data class Success(val candidates: List<LocationCandidate>) : LocationSearchResult {
        init { require(candidates.isNotEmpty()) }
    }
    data object NoResults : LocationSearchResult
    data class Failure(val category: Category) : LocationSearchResult
    enum class Category { TRANSPORT, HTTP_OR_PROVIDER, MALFORMED_RESPONSE }
}

fun interface LocationSearch { fun search(request: LocationSearchRequest): LocationSearchResult }
