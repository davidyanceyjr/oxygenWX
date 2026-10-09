# Plan 106 — Open-Meteo primary forecast provider

Status: Completed  
Cycle ID: 106-open-meteo-primary-forecast-provider  
Roadmap item: R2.2  
Created: 2026-10-03

## Objective and observable outcome

Implement the Open-Meteo `/v1/forecast` request builder, injectable transport,
and provider-specific response decoder for current, hourly, and daily fields
requested by the existing `ForecastRequest` contract. Deterministic fixtures
must prove the explicit field/unit/timezone configuration, nullable and sparse
wire data preservation, and requested 72-hour/10-day bounds.

Observable outcome: the provider adapter package exposes a deterministic
provider-specific result for the later R2.2A canonical mapper. Tests prove that
the request matches the typed input and documented endpoint schema, and that
decoded values retain their original timestamps, dates, units, and missing
fields. This cycle does not return a canonical `ForecastProviderResult.Success`.

## Dependencies and contract boundary

- R2.1 is DONE. Consume `ForecastRequest`, `ForecastEndpoint`,
  `ForecastField`, `ForecastCoverage`, and `ForecastTransportFailure` as
  applicable; do not change those contracts in this cycle.
- `ForecastProvider.fetch` currently returns canonical `ForecastData` on
  success. R2.2 forbids canonical mapping, so this adapter is an internal
  Open-Meteo request/transport/decoder boundary and does not implement
  `ForecastProvider` yet. R2.2A owns conversion of decoded wire values to
  `ForecastData` and the later public provider bridge.
- R2.1 has no distinct malformed-response outcome. Keep malformed payloads
  distinguishable in the provider-specific internal result; R2.2A must decide
  their public mapping before repository wiring in R2.3. Do not silently label
  a decode error as a transport failure in this cycle.
- R2.3 owns repository selection and live-path orchestration. R2.4 owns MET
  Norway fallback.

## Production boundary

- Add provider-specific request, response, decoder, and internal result types
  under `app/src/main/java/com/oxygen/weather/data/provider/openmeteo/` (or the
  established equivalent package if repository conventions require it).
- Add the narrowest injectable HTTP transport seam consistent with current
  dependencies and project async conventions. Keep URI construction separate
  from transport and JSON decoding. Do not add an HTTP dependency without
  updating this plan first.
- Add sanitized JSON fixtures and deterministic unit tests under
  `app/src/test/`; tests must not access the live network.
- Do not modify provider-neutral contract, canonical weather records,
  repository, application state, presentation, UI, manifest, or settings.

## Request and field contract

Build query parameters from `ForecastRequest` and the injected
`ForecastEndpoint` only. Encode latitude/longitude with stable formatting,
URL-encode the IANA `location.timeZone.id`, use ISO8601 time format, and request
canonical units explicitly (`temperature_unit=celsius`,
`wind_speed_unit=kmh`, `precipitation_unit=mm`). Do not include credentials,
location display names, or opaque local IDs.

Map only requested canonical fields to the Open-Meteo variable names needed at
the granularities represented by the request. The initial expected mapping to
confirm against the official schema is:

| `ForecastField` | Current/hourly request variable | Daily request variable |
| --- | --- | --- |
| CONDITION | `weather_code` | `weather_code` |
| TEMPERATURE | `temperature_2m` | — |
| DEW_POINT | `dew_point_2m` | — |
| PRESSURE | `pressure_msl` | — |
| WIND_SPEED | `wind_speed_10m` | — |
| PRECIPITATION_PROBABILITY | `precipitation_probability` | `precipitation_probability_max` |
| PRECIPITATION_AMOUNT | `precipitation` | `precipitation_sum` |
| CLOUD_COVER | `cloud_cover` | — |
| DAILY_LOW | — | `temperature_2m_min` |
| DAILY_HIGH | — | `temperature_2m_max` |
| DAILY_WIND_GUST | — | `wind_gusts_10m_max` |
| DAILY_SUNSHINE_HOURS | — | `sunshine_duration` (seconds on wire) |

Request `current` values for requested fields supported by the endpoint's
current section; request `hourly` only when `coverage.hourlyHours` is present,
and `daily` only when `coverage.dailyDays` is present. Add
`forecast_hours=<hourlyHours>` and/or `forecast_days=<dailyDays>` only for the
requested horizon. Open-Meteo requires timezone when daily variables are
requested, and its timezone parameter also controls local timestamp/date
labels. If a requested field has no variable at a given granularity, omit that
variable there; do not fabricate an equivalent or infer it from another
granularity. Record any confirmed unsupported requested-field case in the
provider-specific result for R2.2A handling.

## Functional invariants

- Use the injected HTTPS endpoint from `ForecastEndpoint`; no fixed host,
  credential, or live network is embedded in tests. Local HTTP is permitted
  only through an explicitly injected loopback test endpoint allowed by the
  existing contract.
- Request coordinates, named timezone, requested fields, units, ISO8601 time
  representation, and horizons deterministically. Query parameter ordering
  must be stable for fixture assertions.
- Keep Open-Meteo variable names and JSON DTOs in the adapter package. Never
  leak provider DTOs into canonical, presentation, repository, or UI packages.
- Decode time arrays and each requested field without sorting, padding,
  interpolation, deduplication, or canonical conversion. Preserve source
  ordering, duplicate labels, timezone, raw timestamp/date strings, nullable
  elements, absent arrays, and unit metadata for R2.2A.
- Preserve provider response error/malformed/no-data cases in typed
  provider-specific outcomes. Do not expose raw response bodies or secrets in
  public errors. Transport failures may use the existing neutral failure facts;
  decoder failures stay distinguishable until their public policy is resolved
  in R2.2A.
- Do not request fields outside the typed request except a documented
  endpoint-required parameter. Do not infer official alerts, source
  provenance, observation/model-estimate semantics, or canonical values in
  this adapter.

## Implementation steps

1. Inspect R2.1 types, `ForecastData` fields for R2.2A, JSON/parser
   dependencies, networking/async conventions, and the official Open-Meteo
   Forecast API schema. Record the schema URL, access date, exact variables,
   response shape, and units in the cycle evidence notes. Verify the table
   above and revise it if documentation differs; do not implement mapping.
2. Add provider-specific wire request/response types plus a pure deterministic
   URI/query builder. Cover requested field selection, omitted granularities,
   timezone encoding, units, and bounds in query tests.
3. Add an injectable transport and typed internal adapter outcomes. Separate
   transport failure from HTTP/API error, empty/no-data response, malformed
   JSON, and valid partial wire response. Avoid retry/fallback/repository/cache
   behavior.
4. Add sanitized fixture JSON for complete current/hourly/daily data, nullable
   fields, missing optional arrays, unequal/sparse arrays, repeated labels,
   timezone/unit metadata, empty/no-data, provider API error, and malformed
   JSON. Do not retain personal location data or credentials.
5. Add decoder/adapter tests for the fixtures, including preservation of
   ordering/nullability/labels/units, stable request construction, and
   sanitized error output. Add a source/package boundary assertion if an
   existing repository contract-test pattern supports it without broadening
   production scope.
6. Run the exact focused and broader verification below; inspect changed paths
   and diff for canonical mapping or repository/UI leakage; save commands,
   results, schema provenance, and unavailable checks in the cycle evidence
   directory.

## Acceptance criteria

- Request tests prove exact coordinates, current/hourly/daily variable lists,
  IANA timezone, explicit units/time format, and only the coverage values
  supplied by `ForecastRequest`, including 72 hours and ten days.
- Fixture-backed decoder tests cover the planned provider fields and response
  structure, nullable/missing values, timezone, raw timestamps/date labels,
  unit metadata, source order, sparse arrays, empty/no-data, API error, and
  malformed JSON.
- A deterministic injected transport proves successful body delivery and
  distinct transport failure behavior without network access.
- Provider-specific DTOs and query/JSON identifiers remain inside the
  Open-Meteo adapter package. R2.1 public contracts and canonical models are
  unchanged; no canonical mapping, provider interface implementation,
  repository wiring, cache, fallback, presentation, UI, or manifest change is
  present.
- Errors exposed outside decoding do not contain raw bodies, endpoint secrets,
  or decoded DTOs. Any dependency addition has first been reflected in the
  plan and supported by the repository dependency policy.
- The complete slice remains bounded to R2.2 and is estimated below the
  roadmap's 65% context budget; current rough estimate is 40% of one focused
  implementation context, including fixture tests and integration review. If
  schema/transport work requires contract
  changes or canonical mapping, stop and update the plan before expanding.

## Verification and evidence

Run from the repository root:

1. Focused: `./gradlew :app:testDebugUnitTest --tests com.oxygen.weather.data.provider.openmeteo.OpenMeteoRequestTest --tests com.oxygen.weather.data.provider.openmeteo.OpenMeteoDecoderTest --tests com.oxygen.weather.data.provider.openmeteo.OpenMeteoAdapterTest`
   (use `gradlew.bat` on Windows; update class names to match the implemented test
   files and record the final exact command). These tests must use
   fixture/injected transport only. The current `scripts/dev.py test` command
   runs the full unit-test task and does not accept Gradle test filters.
2. Broader unit regression: `python scripts/dev.py test`.
3. Contracts and workflow: `python scripts/dev.py contract` and
   `python scripts/dev.py workflow`.
4. Diff hygiene: `git diff --check`; inspect changed paths and full diff for
   field leakage, mapping/wiring scope, unsafe endpoint configuration, and
   raw-body/secret exposure.
5. Do not run installed visual checks: no UI boundary changes in this cycle.
   Record unavailable checks and why; do not claim provider/live-network,
   canonical mapping, or installed behavior verified.

Preserve exact commands/results, schema URL/access date, sanitized fixture
provenance, and any unavailable checks in
`.codex/test-artifacts/106-open-meteo-primary-forecast-provider/verification.md`.

## Risks and assumptions

- **Schema assumption to reverify before implementation:** the official
  [Open-Meteo Forecast API documentation](https://open-meteo.com/en/docs),
  checked during this plan review on 2026-10-03, supports the variable mapping above, timezone-local
  ISO8601 labels, `forecast_hours`, `forecast_days`, and the stated response
  metadata. Schema verification is an implementation prerequisite; this plan
  review does not treat the documentation lookup as fixture evidence.
- **Unit assumption:** request Celsius, km/h, and mm explicitly so the later
  mapper receives known wire units. `sunshine_duration` is seconds and any
  conversion belongs to R2.2A.
- **Result-contract dependency:** the existing public outcome type has no
  malformed-response value and couples success to canonical data. R2.2 keeps
  parse outcomes internal. Before R2.2A closes, the owner/reviewer must resolve
  how malformed provider data maps to the public contract (extend its neutral
  failure model or select an existing truthful outcome); R2.2 must not decide
  this by mislabeling errors.
- **No approval is presumed** for new dependencies or changes to the neutral
  result contract. Either requires a revised plan before implementation.
- If Open-Meteo returns absent/mismatched arrays for a field, preserve them in
  the wire result and let R2.2A apply the canonical model's honest missing-data
  policy; do not repair provider data here.

## Out of scope

- Canonical mapping, unit conversion, provenance assignment, chronological
  normalization/validation, and partial-horizon product semantics (R2.2A).
- Implementing the public `ForecastProvider` adapter method before canonical
  mapping exists; WeatherRepository/provider selection/application state
  (R2.3/R2.3A).
- MET Norway fallback, retries, arbitration, blending, cache, persistence,
  selected-location behavior, and offline/stale handling (R2.4/R3).
- UI/presentation, visual/accessibility evidence, settings, permissions,
  manifest/network-security changes, and live-network integration tests.
- Unrequested provider products, historical data, alerts, derived signals,
  alternate models, and unrelated documentation/legal review.
