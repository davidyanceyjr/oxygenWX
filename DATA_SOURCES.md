# Data Sources

## Active in this UI candidate

Normal startup displays the labeled `DemoWeatherRepository` development
fixture until a location is selected through manual search. A selected request
is sent to Open-Meteo and rendered with its returned location, weather facts,
source, and retrieval/provenance times. Selection is transient and is not
restored after relaunch; selected-location persistence is a later roadmap item.

Open-Meteo forecast requests use
`https://api.open-meteo.com/v1/forecast`. The free service is limited to
non-commercial use, requires CC BY 4.0 attribution, and publishes request
limits. Open-Meteo's licence page asks for a link next to each location where
its weather data is displayed, a link to CC BY 4.0, and an indication when the
data has been changed. Oxygen formats and converts selected fields for display;
the current Home attribution identifies those adjustments. The forecast API
uses a blended/default model choice whose upstream source may vary. The
Open-Meteo API-wide CC BY statement and any source-specific upstream terms
should be reviewed for the actual release use. Authoritative references
(verified 2026-10-09): [Forecast API documentation](https://open-meteo.com/en/docs),
[Terms of Use](https://open-meteo.com/en/terms),
[Open-Meteo licence and upstream data sources](https://open-meteo.com/en/licence), and
[CC BY 4.0 licence](https://creativecommons.org/licenses/by/4.0/).

`DemoWeatherRepository` provides a deterministic development fixture with:

- one model-estimated current condition;
- 72 hourly forecast entries;
- ten daily forecast entries;
- a synthetic historical comparison sample used only to exercise derived UI behavior.

The app labels this source as an offline development fixture. These values
must not be presented as real local weather or attributed to a selected search
candidate.

## Other planned production sources

The adopted Oxygen product direction uses replaceable provider interfaces.
Remaining candidates are:

- MET Norway as a separately attributed forecast fallback, deferred until the
  production transport implements response caching/conditional requests and
  the app has usable identifying contact metadata;
- NOAA/NWS for United States official alerts;
- an explicitly documented historical/archive provider for percentile and analog context.

Provider terms, attribution, rate limits, fields, provenance, and caching rules must be reviewed again when those integrations are implemented.

### NOAA/National Weather Service active alerts

The R4.2 provider queries `https://api.weather.gov/alerts/active?point={latitude},{longitude}` over HTTPS and requests GeoJSON (`Accept: application/geo+json`). Every request sends a descriptive `User-Agent` identifying Oxygen Weather; NWS says a User-Agent is required and recommends contact information when available. The API is cache-friendly; the Alerts service recommends requests no more frequently than every 30 seconds and may rate-limit abusive traffic. The NWS explicitly permits third-party redistribution of its watches, warnings, advisories, and similar products. Preserve NWS issuer/source attribution in the UI. NWS CAP must not be used to activate the Emergency Alert System.

Point queries resolve both county and zone alerts. A successful GeoJSON FeatureCollection with an empty `features` list means supported with no active alerts. The API has no separate documented unsupported-region result schema; observed out-of-bounds point requests return HTTP 400 with problem type `https://api.weather.gov/problems/InvalidParameter` and detail `Parameter "point" is invalid: out of bounds`. Only this precise response is treated as unsupported; other failures remain failures. This rule is verified against a covered empty point and an out-of-bounds point, and must be revisited if NWS changes its response contract.

Official references (reviewed 2026-10-09): [NWS Alerts Web Service](https://www.weather.gov/documentation/services-web-alerts), [NWS API Web Service](https://www.weather.gov/documentation/services-web-api), and [NWS Alerts Geolocation Guide](https://www.weather.gov/media/documentation/docs/NWS_Geolocation.pdf). NWS recommends no more than one alert request every 30 seconds; the app's controller/cache policy should continue to be checked against that guidance. NWS open-data guidance permits redistribution of alert products and requires an identifying User-Agent; this app sends its name, version, and project URL. Do not use NWS CAP data to activate the Emergency Alert System. Detailed request, response, mapping, limitations, and observed behavior are recorded in `.codex/test-artifacts/129-noaa-nws-us-alert-provider/provider-documentation.md`.

### Open-Meteo geocoding lookup

The manual location-search adapter uses Open-Meteo's Geocoding API at
`https://geocoding-api.open-meteo.com/v1/search`. It accepts a required `name`
(location or postal code), with optional `count` (default 10, maximum 100),
`language`, and `countryCode`. Empty and one-character names return no results;
search matching and qualifier rules are documented by the provider.

Location records are based on GeoNames, which must be credited in the product
when using these results. The free endpoint requires no API key for
non-commercial use. Open-Meteo's current free-service terms limit usage to
fewer than 10,000 calls per day, 5,000 per hour, and 600 per minute, and restrict
the free service to non-commercial purposes. Open-Meteo states the API data is
under CC BY 4.0; preserve appropriate attribution and indicate modifications as
required by that license. Recheck these terms before production integration.

Geocoding results are based on GeoNames. GeoNames asks users of its data or
web services to credit GeoNames with a link or other reference; GeoNames
identifies its database as CC BY 4.0. Search results identify GeoNames and link
CC BY 4.0 alongside the returned place records. The API documentation does not
specify an exact credit phrase or placement. Open-Meteo's terms also
describe the free service as non-commercial and document a commercial API key
path. The owner's distribution/business model must be assessed separately
before release. Authoritative references (verified 2026-10-09):
[Geocoding API documentation](https://open-meteo.com/en/docs/geocoding-api),
[Terms of Use](https://open-meteo.com/en/terms),
[Open-Meteo licence](https://open-meteo.com/en/licence),
[GeoNames](https://www.geonames.org/), and
[CC BY 4.0 licence](https://creativecommons.org/licenses/by/4.0/). The
verification details are recorded in
`.codex/test-artifacts/115-manual-location-search-contracts-and-lookup-adapter/provider-documentation.md`.

## MET Norway Locationforecast fallback

The deferred fallback candidate is the MET Norway Locationforecast 2.0 compact
JSON endpoint. It is not configured in the normal app composition until the
response cache policy and contact identity are ready:

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
