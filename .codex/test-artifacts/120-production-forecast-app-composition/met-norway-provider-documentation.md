# MET Norway Locationforecast provider documentation

Accessed: 2026-10-04

Scope: authoritative provider facts needed for Plan 113's Locationforecast
fallback. This is an implementation record, not a broad legal, licence, or
provider-terms audit. No live forecast request was used as verification.

## Endpoint and request identity

- Use the HTTPS compact JSON endpoint:
  `https://api.met.no/weatherapi/locationforecast/2.0/compact`.
- `lat` and `lon` are mandatory decimal-degree query parameters. `altitude` is
  optional but recommended for more precise temperature correction and is the
  ground height in whole metres above or below sea level.
- MET Norway's Locationforecast reference says a missing or prohibited
  `User-Agent` receives `403 Forbidden`. Generic defaults including `okhttp`,
  `Dalvik`, `fhttp`, and `Java` are prohibited.
- The Terms of Service requires an identifying `User-Agent` when possible. It
  must identify the application or domain, may include a version, and should
  include a company email address or a website containing contact information.
  A suitable configured shape is therefore
  `OxygenWeather/<version> <contact-url-or-email>`; do not invent or randomize
  the identifier and do not fall back to the HTTP client's default value.

Authoritative sources:

- https://api.met.no/weatherapi/locationforecast/2.0/documentation
- https://api.met.no/weatherapi/locationforecast/2.0/swagger
- https://docs.api.met.no/doc/TermsOfService.html
- https://docs.api.met.no/doc/locationforecast/HowTO.html

## Attribution and licensing

- The Terms of Service says open data require attribution under CC BY 4.0,
  including appropriate credit, a licence link, and an indication when changes
  were made, without implying endorsement.
- MET Norway's Licensing and Data Policy says that, unless otherwise specified,
  its data and products are licensed under NLOD 2.0 and CC BY 4.0. It asks that
  the source be credited as **The Norwegian Meteorological Institute**, shortened
  to **MET Norway**, and suggests “Data from MET Norway” or “Based on data from
  MET Norway.” It also appreciates a link to the download source.
- Provider identity in canonical provenance should therefore be MET Norway and
  must remain distinct from Open-Meteo. Product-visible attribution work is a
  later roadmap slice; this record does not decide its presentation.

Authoritative sources:

- https://docs.api.met.no/doc/TermsOfService.html
- https://docs.api.met.no/doc/License.html

## Request and traffic guidance

- Use HTTPS. Clients must support redirects and gzip/deflate compression.
- Truncate latitude and longitude to at most four decimal places. The
  Locationforecast HOWTO says additional precision harms cache reuse, and the
  Terms say new products can reject five or more decimals with `403`.
- Cache responses until the `Expires` time and use `If-Modified-Since` with the
  exact prior `Last-Modified` value when supplied. Do not issue a HEAD request
  followed by GET because that can double server load.
- Spread requests over time rather than synchronizing a large batch. Mobile
  clients must not continuously refresh while unused.
- More than 20 requests per second per application, aggregated across all app
  installations, requires a special agreement. A `429` response means traffic
  must be reduced immediately.

These are provider request guidelines, not a cache/retry design for Plan 113;
cache, retry, and backoff remain outside this cycle.

Authoritative sources:

- https://docs.api.met.no/doc/TermsOfService.html
- https://docs.api.met.no/doc/GettingStarted.html
- https://docs.api.met.no/doc/locationforecast/HowTO.html

## Compact JSON shape and time semantics

The response is GeoJSON. Forecast data are under `properties`, with
`properties.meta.updated_at`, `properties.meta.units`, and an increasing
`properties.timeseries` array.

- Each timeseries `time` is ISO 8601 UTC. Locationforecast does not return local
  timestamps or accept a timezone parameter. Convert UTC instants using the
  immutable request location's `ZoneId` for canonical local hourly/date values.
- `instant.details` values apply at exactly that timestamp.
- `next_1_hours`, `next_6_hours`, and `next_12_hours` are period objects that
  begin at the enclosing `time`. Their `details` contain period aggregates and
  their `summary.symbol_code` describes that same period.
- Time resolution changes through the horizon, typically from hourly to
  six-hourly. A period object is absent when the remaining horizon or current
  resolution cannot support it, and the last timeseries entry has only
  `instant`. Missing objects or values must remain unavailable; they are not
  zeros.
- `meta.updated_at` is the provider's forecast update time, not the data's valid
  time and not Oxygen's retrieval time. Preserve these concepts separately.

Authoritative sources:

- https://docs.api.met.no/doc/ForecastJSON.html
- https://docs.api.met.no/doc/locationforecast/FAQ.html

## Compact field support and concrete canonical notes

The Locationforecast data model explicitly says **bold** variables are in
`compact.json`; non-bold variables are only in `complete.json`. The shared
OpenAPI definitions describe the JSON family and are broader than the compact
delivery guarantee, so the data-model compact markings control support here.

| Canonical fact | Compact path | Provider unit / validity | Mapping note |
| --- | --- | --- | --- |
| Temperature | `instant.details.air_temperature` | Celsius; instant; 2 m above ground | Map directly to °C. |
| Dew point | Not in compact | `dew_point_temperature` is Celsius and instant in complete only | Unsupported by this compact fallback; keep null/report unsupported. |
| Sea-level pressure | `instant.details.air_pressure_at_sea_level` | hPa; instant | Map directly to canonical hPa. |
| Wind speed | `instant.details.wind_speed` | m/s; instant; 10 m, ten-minute average | Convert to canonical km/h by multiplying by 3.6. |
| Precipitation amount | `next_1_hours.details.precipitation_amount` when present; otherwise provider periods may be six or twelve hours | mm accumulated over the named period | An hourly value is valid only from `next_1_hours`; do not relabel a six-/twelve-hour accumulation as hourly or treat absence as zero. |
| Cloud cover | `instant.details.cloud_area_fraction` | percent; instant; total cloud cover | Map directly to canonical percent. Layer fractions are complete-only. |
| Condition | `next_1_hours.summary.symbol_code` when present | string summary for the named period | Map only documented symbol codes to Oxygen's coarser condition enum. Do not calculate a symbol from other values or use a longer-period symbol as an hourly fact. Day/night/polar-twilight suffixes belong to the provider code, not a different weather meaning. |

Source availability can vary by model, geographic area, and horizon. Even a
documented field must be nullable in decoding and mapping.

## Daily and probability boundary

Locationforecast compact has no native daily object or local-calendar-day
summary. Specifically:

- `air_temperature_min` and `air_temperature_max` are complete-only period
  values, not documented daily extrema, and are unavailable for
  `next_1_hours`. Compact therefore does not directly support canonical daily
  low/high.
- `wind_speed_of_gust` is complete-only, instant data, and is unavailable in
  the long-term forecast after roughly 60 hours. Compact does not support
  canonical daily maximum gust.
- Sunshine duration is not listed in the Locationforecast data model or current
  OpenAPI forecast definitions. Compact does not support canonical daily
  sunshine hours.
- `probability_of_precipitation` is complete-only period data. Compact does not
  support hourly or daily canonical precipitation probability.
- `precipitation_amount` and `symbol_code` are compact period fields, but the
  provider does not publish daily totals or daily condition summaries. Creating
  a calendar-day total or representative condition would be an Oxygen-derived
  aggregation with explicit interval/timezone rules, not a direct provider
  field. Plan 113 should preserve unsupported daily facts honestly unless its
  bounded mapper defines and deterministically tests such an aggregation.

Authoritative sources:

- https://docs.api.met.no/doc/locationforecast/datamodel.html
- https://docs.api.met.no/doc/ForecastJSON.html
- https://api.met.no/weatherapi/locationforecast/2.0/swagger
- https://docs.api.met.no/doc/locationforecast/FAQ.html
