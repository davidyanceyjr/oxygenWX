package com.oxygen.weather.application

import com.oxygen.weather.data.alerts.OfficialAlertRepository
import com.oxygen.weather.data.alerts.ProviderOfficialAlertRepository
import com.oxygen.weather.data.alerts.nws.NwsAlertProvider
import com.oxygen.weather.data.alerts.nws.NwsTransport
import com.oxygen.weather.data.alerts.nws.UrlConnectionNwsTransport
import java.net.URI
import java.time.Clock

/** Builds the production normalized alert repository around the authoritative NWS adapter. */
object ProductionOfficialAlertComposition {
    fun create(
        endpoint: URI = URI("https://api.weather.gov/alerts"),
        userAgent: String = "OxygenWeather/1.0 (https://github.com/davidyanceyjr/oxygenWX)",
        transport: NwsTransport = UrlConnectionNwsTransport(),
        clock: Clock = Clock.systemUTC(),
    ): OfficialAlertRepository = ProviderOfficialAlertRepository(
        NwsAlertProvider(endpoint = endpoint, userAgent = userAgent, transport = transport, clock = clock),
    )
}
