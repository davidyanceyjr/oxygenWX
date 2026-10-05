package com.oxygen.weather.application

import com.oxygen.weather.data.LiveWeatherRepository
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoAdapter
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoLiveSource
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import java.time.Clock
import java.util.concurrent.Executor

/** Builds the production live forecast path using the approved Open-Meteo source. */
object ProductionForecastComposition {
    fun create(
        endpoint: ForecastEndpoint,
        transport: OpenMeteoTransport,
        clock: Clock,
        executor: Executor,
        onStateChanged: (LiveForecastState) -> Unit = {},
    ): LiveForecastController {
        val adapter = OpenMeteoAdapter(endpoint, transport)
        val source = OpenMeteoLiveSource(adapter, clock)
        val repository = LiveWeatherRepository(source)
        return LiveForecastController(repository, executor, onStateChanged)
    }
}
