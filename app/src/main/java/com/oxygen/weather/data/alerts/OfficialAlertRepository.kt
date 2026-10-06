package com.oxygen.weather.data.alerts

/** Provider-neutral access to normalized official alerts for one selected location. */
fun interface OfficialAlertRepository {
    fun fetch(request: OfficialAlertRequest): OfficialAlertProviderResult
}

/** Keeps provider selection outside callers while preserving the provider's normalized result. */
class ProviderOfficialAlertRepository(
    private val provider: OfficialAlertProvider,
) : OfficialAlertRepository {
    override fun fetch(request: OfficialAlertRequest): OfficialAlertProviderResult = provider.fetch(request)
}
