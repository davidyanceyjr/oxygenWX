# Plan 107 — Open-Meteo primary forecast provider

Status: Completed  
Cycle ID: 107-open-meteo-primary-forecast-provider  
Roadmap item: R2.2  
Created: 2026-10-03  
Reviewed: 2026-10-03

## Objective and independently observable outcome

Complete the R2.2 Open-Meteo adapter boundary: deterministic construction of
the `/v1/forecast` request from the R2.1 provider-neutral contracts, injected
HTTP transport, and lossless decoding of current/hourly/daily response sections
into provider-shaped internal values. The observable outcome is deterministic
request and fixture/transport evidence for the supported request fields,
timezone and units, nullable/sparse response data, 72-hour/10-day limits, and
distinct error outcomes. No canonical weather mapping occurs in this cycle.

R2.2 is still PLANNED in the roadmap. Although the adapter and tests are already
present in the repository and cycle 106 has verification notes, there is no
cycle-106 history record. Treat those as existing implementation/evidence to
reconcile against this cycle's acceptance criteria, not as proof that R2.2 is
closed. Do not duplicate or replace conforming code. Make only bounded R2.2
corrections if verification exposes a gap, then close cycle 107 with the actual
results and limitations.

## Production boundary

Permitted production paths, only if a verified R2.2 gap requires a correction:

- `app/src/main/java/com/oxygen/weather/data/provider/openmeteo/` — request
  builder, injected transport, wire values/DTOs, decoder, and provider-specific
  outcomes.
- `app/src/test/java/com/oxygen/weather/data/provider/openmeteo/` and
  `app/src/test/resources/openmeteo/` — deterministic adapter tests and
  sanitized schema-shaped fixtures.

The expected existing focused test is
`OpenMeteoAdapterTest`. Keep R2.1 neutral contracts unchanged. This cycle does
not implement `ForecastProvider.fetch`'s canonical result bridge: the adapter
returns `OpenMeteoResult` and R2.2A owns canonical mapping.

## Functional invariants

- Build the request only from the explicitly injected `ForecastEndpoint` and
  `ForecastRequest`. Use stable coordinates, the location's IANA timezone,
  ISO8601 time labels, and explicit Celsius, km/h, and mm units. Never send the
  local location ID, display name, credentials, or unrelated fields.
- Request only the applicable requested variables. Omit hourly or daily
  parameters when that coverage is absent; cap requested horizons at 72 hours
  and 10 days. Report requested field/granularity combinations the source
  cannot provide as unsupported.
- Keep Open-Meteo variable names, raw response shapes, decoding, and wire units
  inside the adapter package. Do not convert units or create canonical values.
- Retain response timezone/UTC offset, per-section unit metadata, raw time/date
  labels, source order, duplicates, nulls, absent properties, and unequal or
  sparse arrays as represented on the wire. Do not sort, deduplicate, pad,
  interpolate, or invent values.
- Keep malformed payload, provider/API error, non-success HTTP status, no-data,
  and transport failure distinguishable. Result/error string representations
  must not expose response bodies or query values.
- Keep forecast data distinct from observations, model estimates, alerts,
  historical values, and derived values. Do not alter R2.1 contracts, repository
  selection/orchestration, retry/fallback, cache, or UI behavior.

## Implementation steps

1. Reconcile the existing R2.2 files and cycle-106 artifact with the current
   R2.1 contracts, this plan, and the R2.2 roadmap exit. Record which criteria
   are already satisfied and any precise gaps before editing. Confirm that all
   changes remain inside the production boundary.
2. Recheck the official Open-Meteo Forecast API documentation during execution.
   Record the direct source URL and access date, request parameter/variable
   names, units, response section/unit structure, timezone representation, and
   documented horizon parameters in the cycle evidence. The 2026-10-03 cycle
   106 note is a useful starting point, not a substitute for this check.
3. Verify request behavior with deterministic tests: coordinates; selected
   current/hourly/daily variables; explicit units and time format; timezone
   encoding; omitted coverage; unsupported combinations; and maximum horizons.
   If a gap exists, correct the query builder without changing neutral
   contracts.
4. Verify injected-transport and decoder behavior with sanitized fixtures and
   inline malformed/error cases. Cover current/hourly/daily sections, time/date
   labels, metadata and units, nullable/missing values, duplicate labels,
   source order, unequal arrays, no-data, provider/API error, HTTP failure, and
   transport timeout/network failure. Keep any fixture hand-authored and free
   of credentials, personal location names/IDs, and captured private response
   data.
5. Run the focused adapter test, the broader checks below, and inspect changed
   paths and complete diff for provider leakage, unnecessary changes, or scope
   expansion. Record exact commands/results and any unverified boundary.
6. This plan was reviewed while PLANNED. When this plan is invoked for
   execution, activate it before production edits and close R2.2 only after the
   roadmap exit is met and the durable history records actual verification;
   otherwise record the blocker/limitation without claiming R2.2 complete.

## Acceptance criteria

- Deterministic request assertions cover requested current/hourly/daily
  variables; coordinates; IANA timezone; Celsius, km/h, mm, and ISO8601;
  omitted granularities; unsupported combinations; and the 72-hour and 10-day
  maxima.
- Fixture/decoder assertions cover all documented requested wire fields and
  preserve section and unit metadata, timezone/UTC offset, labels, ordering,
  duplicates, null versus absent values, and sparse/unequal arrays.
- Injected transport tests prove no live network is needed and distinguish
  success, no-data, malformed response, provider/API error, HTTP failure, and
  transport failure.
- Query/result/error diagnostics expose no raw body, endpoint query secrets, or
  private response detail.
- Provider DTOs, variable identifiers, and decoder remain adapter-local; R2.1
  neutral contracts and canonical mapping remain unchanged. No repository,
  cache, application-state, presentation/UI, manifest, or network-permission
  integration is included.
- Evidence and diff inspection explicitly reconcile pre-existing adapter work
  and cycle-106 evidence; no duplicate implementation is introduced.
- Expected effort remains below the roadmap's 65% context ceiling. If a neutral
  contract change, canonical mapping, or HTTP dependency is necessary, stop and
  revise the plan before broadening the boundary.

## Verification and evidence

From the repository root, run and record:

1. Focused adapter test (the Python entry point has no test-filter option):
   `./gradlew :app:testDebugUnitTest --tests com.oxygen.weather.data.provider.openmeteo.OpenMeteoAdapterTest`
   (use `gradlew.bat` on Windows; the Gradle task is the same).
2. Broader unit tests: `python scripts/dev.py test`.
3. Contract and lifecycle checks: `python scripts/dev.py contract` and
   `python scripts/dev.py workflow`.
4. Repository check when the Android SDK/dependencies are available:
   `python scripts/dev.py check`. Record an unavailable environment and reason
   rather than implying a pass.
5. `git diff --check`, followed by changed-path and full-diff inspection.

Preserve exact commands/results, schema source/access date, fixture provenance,
reconciliation of existing implementation, and any unavailable check in
`.codex/test-artifacts/107-open-meteo-primary-forecast-provider/verification.md`.
Installed visual evidence is not applicable because this cycle changes no UI;
live-network behavior and canonical mapping are explicitly unverified here.

## Risks and assumptions

- The Open-Meteo API schema can change. Execution must recheck the official
  documentation and adjust only the adapter/tests inside this boundary.
- Cycle 106's verification artifact describes the same adapter boundary and
  reports tests passing, but no cycle-106 history record exists. Existing files
  and notes are not an accepted closure; cycle 107 must record what is verified
  now and close the R2.2 roadmap item through the normal history process.
- Production endpoint configuration must be HTTPS. Explicit loopback HTTP may
  be used only in tests if supported by the R2.1 endpoint contract.
- The adapter currently contains its own strict JSON reader. It may remain
  within this slice if it passes the required cases; introducing a general JSON
  library/dependency is not assumed or required.

## Out of scope

- Mapping decoded provider data to canonical forecast records, canonical unit
  conversion, provenance assignment, or partial-horizon product behavior
  (R2.2A).
- Implementing the neutral provider success bridge or integrating a provider
  with `WeatherRepository`/application state (R2.3 and later).
- MET Norway fallback, retries, caching, offline/stale behavior, location
  search/selection, refresh UI, presentation, visual/accessibility work,
  settings, manifest or permission changes, live-network tests, alerts, and
  historical/derived weather.
