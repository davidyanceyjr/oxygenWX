# Plan 110 — Open-Meteo canonical forecast mapping

Status: Completed
Cycle ID: 110-open-meteo-canonical-forecast-mapping
Roadmap item: R2.2A
Created: 2026-10-03

## Objective and observable outcome

Implement a deterministic, provider-local mapping boundary from the decoded
R2.2 `OpenMeteoResponse` into canonical current, hourly, and daily weather
records. A hand-authored response fixture must produce the same canonical
facts, provenance, and location interpretation on every run. This cycle does
not connect the result to repository orchestration or application state.

## Production boundary

- `app/src/main/java/com/oxygen/weather/data/provider/openmeteo/` — mapping
  implementation and the smallest provider-local result type needed to expose
  mapped current facts alongside `ForecastData`.
- `app/src/test/java/com/oxygen/weather/data/provider/openmeteo/` — focused
  deterministic mapping tests.
- `app/src/test/resources/openmeteo/` — hand-authored mapping fixtures.

Do not change provider-neutral contracts or models, repository orchestration,
application state, persistence/cache, UI, or build dependencies. Reuse the
existing `CurrentWeather`, `HourWeather`, `DayWeather`, `ForecastData`,
`DataProvenance`, and `ForecastProviderResult` contracts. The provider-local
mapping outcome may carry `CurrentWeather?` and `ForecastData?` separately;
it must not change the neutral `ForecastData` shape to embed current weather.

## Functional invariants

- Current response values map to `CurrentWeather`; hourly and daily sections
  map to `HourWeather` and `DayWeather` inside `ForecastData`. Current response
  data is not silently discarded just because `ForecastData` models forecast
  horizons only.
- All mapped values are source forecasts/model output, never observations,
  official alerts, derived values, or historical references. Shared forecast
  provenance uses `DataType.FORECAST`, the stable Open-Meteo source identity,
  its display name, and one supplied/injected retrieval instant. Current
  provenance also uses `FORECAST` and carries the current record's valid time.
- The request's `WeatherLocation` is canonical for local identity and timezone.
  Parse ISO local date/time strings as local wall times; do not reinterpret
  them using the device timezone or replace the selected location timezone
  from response metadata. Retain `LocalDateTime`/`LocalDate` values in the
  canonical models. Convert valid times to instants only where provenance
  requires them, using the requested location zone.
- Convert only fields requested and units documented by Open-Meteo into model
  units: Celsius, km/h, hPa, millimeters, percentage, and sunshine hours.
  Verify the actual response unit metadata and official API semantics during
  implementation; document the exact supported variable/unit table and source
  URL/access date in cycle evidence. Unsupported or unexpected unit metadata
  must not be treated as a known unit.
- Preserve nullable, absent, non-finite, malformed, or unrepresentable
  individual measurements as unavailable. A malformed field does not erase
  unrelated usable fields. Never substitute zero, infer a condition, or
  fabricate a horizon.
- Pair each array value only with the time at the same index. Preserve
  response order and duplicate labels. Short/missing arrays yield unavailable
  values for the corresponding row/field; extra values without a time label
  are ignored. Preserve valid partial horizons as received.
- The existing neutral `ForecastData` constructor rejects decreasing
  chronology. To avoid sorting (which would break index pairing) or changing a
  neutral-model invariant in this cycle, a section with decreasing valid
  timestamps is a bounded invalid-section result; valid chronological sections
  retain exact response order, including duplicates.
- Current precipitation is an interval accumulation in Open-Meteo, while the
  available canonical current field is a rate. Leave it unavailable unless
  the decoded input provides sufficient interval information for a correct
  conversion; do not relabel the accumulation as a rate.
- A malformed/missing timestamp cannot identify a canonical row. Return a
  bounded, safe mapping failure for that section rather than silently dropping
  or fabricating a row. Other independently valid sections may still be
  returned when the mapping outcome supports a partial success. No raw body,
  URI, provider payload, or exception detail may escape in the outcome.
- Map only documented Open-Meteo weather codes. Unknown, null, or invalid
  codes map to unavailable condition. Do not infer precipitation type from
  unrelated fields.
- `ForecastData` must only be constructed when at least one hourly/daily
  weather fact is usable, respecting its existing invariant. Current-only
  mapped data remains available in the provider-local outcome without
  manufacturing a forecast row.
- Mapping is pure/deterministic apart from the explicitly supplied retrieval
  instant; it performs no network request and mutates no application state.

## Implementation steps

1. Inspect R2.2 adapter/decoder, fixture shapes, neutral models, and provider
   contract. Check official Open-Meteo documentation for supported fields,
   units, timestamp format/timezone behavior, and weather-code meanings. Put
   the field-to-canonical-unit and code-to-condition tables in the evidence
   record before or with implementation.
2. Define a provider-local mapping result that can represent current facts,
   forecast horizons, unsupported requested fields, and safe invalid-section
   outcomes without changing neutral interfaces. Keep failures bounded and
   payload-free. Specify how valid sections survive an invalid sibling
   section.
3. Implement pure parsers/converters and mapping for current, hourly, and
   daily fields requested by the existing adapter. Use an injected retrieval
   instant. Preserve the invariants above, including null/index behavior,
   partial horizons, duplicate chronology, invalid timestamps, and
   provider-reported units.
4. Add fixtures/tests for complete, sparse, duplicate-time, missing/null
   variable, unequal-array, unknown-code, unsupported-unit, malformed-time,
   current-only, hourly-only/daily-only, and partial-horizon responses. Assert
   exact values/units, requested location identity/timezone, source identity,
   data type, valid/retrieval times, row order, duplicate preservation,
   unavailable behavior, and safe failure shape.
5. Assess whether the decoded adapter can expose this mapping as a direct
   `ForecastProvider.fetch` implementation without repository orchestration.
   Integrate only if this stays wholly within the provider boundary and its
   outcome can preserve current mapping without losing it. Otherwise leave
   the adapter decoder and mapper as composable provider-local pieces, and
   record the exact bridge required by R2.3. Do not alter neutral contracts to
   force integration.
6. Run the focused tests and planned broader checks; preserve actual outputs,
   official documentation reference/access date, field/unit/code tables, and
   any unverified boundary in the evidence file. Inspect the final diff and
   whitespace check before closing.

## Acceptance criteria

- Deterministic fixtures prove complete, sparse, duplicate-time, and partial
  current/hourly/daily mapping with requested location context and truthful
  available coverage.
- Every supported field has an explicit mapping and canonical unit; null,
  absent, malformed, non-finite, unsupported, and short-array cases remain
  unavailable without corrupting unrelated facts.
- Documented conditions map deterministically; unknown codes remain
  unavailable. Duplicate local times/dates and source order are retained.
- Provenance assertions prove the Open-Meteo source identity/display name,
  `FORECAST` classification, valid time where supplied, and deterministic
  retrieval instant. No forecast value is labeled observation/current
  observation merely because it appears in the response `current` section.
- Invalid timestamps and invalid sections have explicit safe outcomes; usable
  sibling sections are retained where possible, and a wholly unusable result
  does not fabricate data.
- Existing neutral model invariants pass without neutral model/interface
  changes. Any direct `ForecastProvider.fetch` bridge either has focused
  contract evidence or is recorded as a specific R2.3 prerequisite.
- Focused mapping tests, contract tests, and repository checks have recorded
  results; failures or unavailable checks are stated rather than implied to
  pass.

## Verification and evidence

Run, in order:

1. Focused Open-Meteo adapter and mapping tests through the supported
   `python scripts/dev.py test` entry point, or an exact Gradle test filter
   discovered from `scripts/dev.py` if supported. Record exact command/output.
2. `python scripts/dev.py contract` for provider-neutral contract checks.
3. `python scripts/dev.py test` for the full deterministic test suite.
4. `python scripts/dev.py check` when SDK/dependencies are available; if it
   fails for environment reasons, record the failure and reason. Also run
   `python scripts/dev.py workflow`, `git diff --check`, and inspect the full
   diff.

Preserve exact results, official source references/access date, mapping tables,
fixture descriptions/provenance, and limitations in
`.codex/test-artifacts/110-open-meteo-canonical-forecast-mapping/verification.md`.
No installed visual evidence or live-network test applies to this non-UI,
fixture-driven slice.

## Risks and assumptions

- **Dependency:** R2.2 decoder is the input boundary and must be completed
  before this cycle activates. Existing adapter fixtures demonstrate sparse,
  duplicate, null, and unequal-array decoder behavior, but R2.2's recorded
  exit/history should be checked before execution.
- **Roadmap/model seam:** R2.2A calls for current/hourly/daily canonical
  records, while `ForecastData` intentionally carries only hourly/daily data.
  This plan resolves the seam with a provider-local outcome carrying current
  and forecast records separately, without expanding neutral models. If
  repository owners intend a different current-result contract, that is an
  owner decision before execution.
- Open-Meteo documentation may describe units or code groupings that the
  canonical model cannot represent. Unsupported meaning remains unavailable;
  do not widen the domain model within this cycle.
- Open-Meteo time labels represent local times in a response timezone. The
  requested location timezone remains authoritative under the current
  provider-neutral contract. A mismatch between request and response timezone
  does not authorize silently changing canonical location context; record a
  fixture/test and raise the mismatch policy for owner review if it affects
  valid-time provenance.
- The provider result contract requires usable hourly/daily facts for
  `ForecastData`; whether a current-only decoded response is a useful success
  belongs to the later provider/repository result policy, not this cycle.

## Out of scope

- R2.3 repository selection/orchestration, retries, fallback, cache, or
  repository result policy; R2.3A application-state bridge.
- Persistence/offline/stale behavior, location search/selection, refresh UI,
  presentation/UI, alerts, historical/derived weather.
- Changes to provider-neutral domain models/contracts, canonical model
  structure, networking dependencies, permissions, or live-network testing.
- Any theme, visual composition, layout, accessibility, or installed-render
  work.
- Expanding requested fields or forecast horizon beyond the already bounded
  R2.2 request contract.

If implementation requires crossing these exclusions, pause and update the
roadmap/plan through the cycle workflow before changing production code.
