# Data Sources

## Active in this UI candidate

No live weather service is active.

`DemoWeatherRepository` produces a deterministic development fixture with:

- one model-estimated current condition;
- 72 hourly forecast entries;
- ten daily forecast entries;
- a synthetic historical comparison sample used only to exercise derived UI behavior.

The app labels this source as an offline development fixture. These values must not be presented as real local weather.

## Planned production sources

The adopted Oxygen product direction uses replaceable provider interfaces. Initial production candidates are:

- Open-Meteo for general forecast data;
- MET Norway as a separately attributed forecast fallback;
- NOAA/NWS for United States official alerts;
- an explicitly documented historical/archive provider for percentile and analog context.

Provider terms, attribution, rate limits, fields, provenance, and caching rules must be reviewed again when those integrations are implemented.
