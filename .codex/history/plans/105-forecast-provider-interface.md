# Plan 105 — Forecast provider interface

Status: Completed
Cycle ID: 105-forecast-provider-interface
Roadmap item: R2.1
Created: 2026-10-03
Reviewed: 2026-10-03

## Objective and observable outcome

Define the provider-neutral request and result boundary that later forecast
adapters can implement. The contract must represent a usable canonical forecast,
requested fields a source cannot supply, an empty/no-result response, and a
transport failure as distinct outcomes. Endpoint configuration is explicit and
injectable. Deterministic tests demonstrate the distinctions and preserve
canonical values and provenance.

Observable outcome: typed contract code and focused tests compile; no network
request, provider-specific wire type, provider selection, or UI behavior is
introduced.

## Production boundary

- Add only the provider-neutral forecast request/result/provider/endpoint
  contract and any narrowly necessary canonical forecast-result model under
  `app/src/main/java/com/oxygen/weather/data/` (use a small `data/provider/`
  package only if it improves ownership without moving canonical types).
- Add focused deterministic unit tests under
  `app/src/test/java/com/oxygen/weather/data/` (or the matching provider
  subpackage).
- Reuse existing canonical record, location, source, and provenance types where
  they can represent the outcome honestly.
- Do not wire a provider into `WeatherRepository`, app state, refresh controls,
  UI, or the development fixture.

## Functional invariants

- Requests are provider-neutral. They identify a geographic forecast point and
  its timezone, preserve an opaque local location identity when supplied, and
  state requested temporal coverage/fields without including provider query
  names or provider identifiers.
- Coordinates, if represented by this contract, must be finite and within
  latitude `[-90, 90]` and longitude `[-180, 180]`. Requested coverage must be
  positive and internally consistent. Do not infer a location from its display
  name or opaque local ID.
- Successful output uses canonical domain values. Preserve location/timezone,
  non-decreasing chronology (including duplicate times), nullable measurements,
  source provenance, valid time, and retrieval time. Missing data remains
  missing; no zero, synthetic timestamp, or filler record is allowed.
- Keep current conditions, forecast records, historical baselines, derived
  values, and official alerts semantically distinct. A provider forecast result
  must not require fabricated historical or derived data.
- Unsupported requested fields, no usable result, and transport failure are
  different typed outcomes. Failure facts are provider-neutral and contain no
  raw response body, secret, or provider DTO. Unsupported-field reporting may
  accompany a successful partial result if that is the canonical result model's
  honest representation; it must not turn absent values into a failure or
  placeholder.
- Endpoint configuration is injected explicitly. It contains no global
  default, hidden provider URL, credential, or network client. Validate an
  absolute endpoint URI with a host, an allowed network scheme, and no embedded
  user information or fragment. Permit `http` only for explicit local/test
  endpoints if required by the repository convention; production provider
  configuration in later slices must use HTTPS.
- Keep the contract transport-agnostic. Do not add coroutines, an HTTP client,
  Android networking code, provider options, retries, or network permission.
- Provider DTOs and decoding rules remain in later provider-adapter slices.
  Compose continues to receive presentation models only.
- Preserve the deterministic fixture and current rendered behavior.

## Implementation steps

1. Inspect current canonical models, architecture, dependency declarations,
   test conventions, and existing forecast horizon/request language in the
   specification and roadmap. Record any necessary contract-model mismatch
   before selecting the success payload type.
2. Resolve the success-payload gate below using the product authority. The
   current `WeatherBundle` requires both `CurrentWeather` and
   `HistoricalBaseline`; the product allows unavailable current facts/partial
   forecast horizons and says historical/derived data is not a 1.0 dependency.
   Do not represent an unavailable baseline or current record with fabricated
   data. Obtain owner direction if a bounded provider-neutral success shape
   cannot be selected without changing established domain semantics or
   broadening this slice. Record the decision in this plan before implementation
   continues.
3. Define the minimal request type: location identity/context (including a
   provider-neutral geographic point and timezone if needed by the later
   provider), requested coverage, and requested fields. Validate only
   invariants owned by the domain contract.
4. Define the provider interface, endpoint configuration, success payload, and
   typed outcomes for unsupported fields, no result, and transport failure.
   Keep payload and error types provider-neutral. Document whether unsupported
   fields may accompany success and how the contract avoids losing a usable
   partial forecast.
5. Add deterministic tests for request validation, endpoint URI acceptance and
   rejection, each distinct result outcome, and success preservation of
   canonical location/timezone, chronology including duplicates, nullable
   values, source, valid time, and retrieval time. Use existing canonical
   fixtures only where they are semantically compatible with the resolved
   success model.
6. Run focused and broader verification, inspect the final diff for provider
   leakage and scope, and preserve exact commands/results under the evidence
   path below.

## Acceptance criteria

- Provider-neutral request, endpoint, provider, success, and result types
  compile in the app source set.
- Success has a canonical representation that does not require fabricated
  current conditions, forecast records, historical baseline, or derived values.
  If the existing model cannot satisfy this, the owner decision is recorded and
  the plan is revised before implementation; the mismatch is not silently
  hidden in an adapter.
- Deterministic tests cover success, unsupported requested fields, no result,
  and transport failure as distinct outcomes, plus invalid and valid endpoint
  configuration.
- Success tests prove preserved canonical values, nullable fields, location
  timezone, chronology, provenance, and valid/retrieval-time metadata.
- Endpoint configuration is injectable, validated, and has no global/provider
  default or embedded credentials.
- No provider-specific DTO or decoding logic enters canonical models,
  `presentation/`, or `ui/`; no provider implementation, HTTP dependency, or
  network call is added.
- The development fixture and existing presentation output remain unchanged.
- The plan remains within one small contract/model/test boundary and is expected
  to consume less than the roadmap's 65% context budget. If resolving the
  success-model mismatch requires a larger domain migration, split/revise before
  activation.

## Verification and evidence

Run from the repository root. The focused class name is fixed by this plan:

1. Focused: `./gradlew :app:testDebugUnitTest --tests`
   `com.oxygen.weather.data.ForecastProviderContractTest`.
2. Broader regression: `python scripts/dev.py test`.
3. Contract/workflow: `python scripts/dev.py contract` and
   `python scripts/dev.py workflow`.
4. Final hygiene: `git diff --check`; inspect changed paths and the complete
   diff for API/package boundaries, fixture/presentation changes, and plan
   scope.

Record exact commands, pass/fail results, unavailable checks and reasons, and
the success-model decision in
`.codex/test-artifacts/105-forecast-provider-interface/verification.md`.
Installed visual evidence is not applicable: this contract-only slice does not
change UI or runtime forecast selection. A compile or preview is not visual
acceptance evidence and is not required for this boundary.

## Risks and assumptions

### Owner decision before implementation

- **Roadmap prerequisite satisfied:** cycle 104 recorded TP.3 PASS and identifies
  R2.1 as the next eligible general-roadmap slice. This is the dependency stated
  by the roadmap note and the current `PLANNED` cycle.
- **Resolved owner decision (2026-10-03):** Add a small forecast-only canonical
  result reusing `WeatherLocation`, `HourWeather`, `DayWeather`, and
  `DataProvenance`. Keep `WeatherBundle` and its current/historical consumers
  unchanged. This permits forecast-only and partial provider successes without
  fabricated current conditions or baselines and remains inside the approved
  production boundary.
- The request needs coordinates for a useful provider boundary, while current
  `WeatherLocation` holds local identity, display name, and timezone only. The
  plan assumes a provider-neutral coordinate value belongs in this request
  contract; it must not turn the local ID into a provider lookup key.
- Endpoint validation assumes an absolute HTTP(S) URI with host, no
  user-information, and no fragment; HTTPS is required for real providers.
  Local HTTP is acceptable only for explicit test/local endpoint injection.
- No asynchronous API or dependency is assumed at this interface boundary;
  R2.2 owns actual transport.

## Out of scope

- Open-Meteo or MET Norway URL/query construction, provider DTOs, JSON
  decoding, HTTP client/transport implementation, retries, timeouts, or network
  permission changes (R2.2 and later).
- Canonical mapping from provider payloads (R2.2A), except a narrowly approved
  provider-neutral result model needed to make R2.1 success honest.
- Repository orchestration, caching, app-state wiring, refresh behavior, or
  selected-location concurrency (R2.3 and later).
- UI, presentation, fixture, settings, location search, official-alert, or
  historical/derived-weather changes.
