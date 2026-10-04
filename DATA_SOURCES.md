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

## MET Norway Locationforecast fallback

The configured fallback uses the MET Norway Locationforecast 2.0 compact JSON
endpoint:

`https://api.met.no/weatherapi/locationforecast/2.0/compact`

Every request must send a unique, identifying `User-Agent`. The application or
domain name is required for identification; MET Norway recommends an optional
version plus a company email address or website with contact information.
Generic client defaults such as `okhttp`, `Dalvik`, and `Java` are prohibited by
the Locationforecast service and can receive `403 Forbidden`.

Forecast output from this fallback must be credited to **MET Norway** (for
example, “Data from MET Norway”). MET Norway documents its open data as licensed
under NLOD 2.0 and CC BY 4.0 unless otherwise specified; attribution must meet
the applicable licence requirements. This implementation note is not a broad
legal or provider-terms audit.

Operational request guidance includes HTTPS, no more than four decimal places
for coordinates, local response caching through the server's cache headers,
and conditional requests with `If-Modified-Since` when `Last-Modified` is
present. Requests must not repeat before `Expires`; traffic above 20 requests
per second per application requires a special agreement.

Authoritative references (verified 2026-10-04):

- [Locationforecast 2.0 reference and identification requirements](https://api.met.no/weatherapi/locationforecast/2.0/documentation)
- [Locationforecast 2.0 OpenAPI schema](https://api.met.no/weatherapi/locationforecast/2.0/swagger)
- [Locationforecast data model](https://docs.api.met.no/doc/locationforecast/datamodel.html)
- [General point-forecast JSON format](https://docs.api.met.no/doc/ForecastJSON.html)
- [MET Weather API Terms of Service](https://docs.api.met.no/doc/TermsOfService.html)
- [Licensing and Data Policy](https://docs.api.met.no/doc/License.html)

The compact-field mapping and unsupported-field boundaries are retained in
`.codex/test-artifacts/113-met-norway-fallback/provider-documentation.md`.
