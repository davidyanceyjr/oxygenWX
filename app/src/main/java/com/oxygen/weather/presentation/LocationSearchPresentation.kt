package com.oxygen.weather.presentation

/** Values needed to render manual place search without exposing canonical data to Compose. */
sealed interface LocationSearchPresentation {
    val query: String

    data class Idle(override val query: String = "") : LocationSearchPresentation
    data class Loading(override val query: String) : LocationSearchPresentation
    data class Results(
        override val query: String,
        val candidates: List<Candidate>,
    ) : LocationSearchPresentation
    data class NoResults(override val query: String) : LocationSearchPresentation
    data class Failure(override val query: String, val category: FailureCategory) : LocationSearchPresentation

    data class Candidate(
        val displayName: String,
        val admin1: String?,
        val admin2: String?,
        val admin3: String?,
        val admin4: String?,
        val country: String?,
        val countryCode: String?,
        val timeZone: String,
    )

    enum class FailureCategory { TRANSPORT, HTTP_OR_PROVIDER, MALFORMED_RESPONSE }
}
