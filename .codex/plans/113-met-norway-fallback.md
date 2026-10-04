# Plan 113 — MET Norway fallback

Status: Completed
Cycle ID: 113-met-norway-fallback
Roadmap item: R2.4
Created: 2026-10-04
Reviewed: 2026-10-04

## Objective and observable outcome

Compose Open-Meteo as the primary live source with one separately configured
MET Norway Locationforecast source as fallback for an explicit allowlist of
typed primary failures. Deterministic repository and adapter tests show the
call/no-call policy, exact request forwarding, final result/source identity,
and that fallback values are never merged with primary values.

## Roadmap dependency and authority

R2.1–R2.3A are recorded complete (cycles 105, 107, 110, 111, and 112). R2.4
precedes R2.5 provenance/freshness UI. The roadmap exit specifically requires
coverage for eligible terminal failures and proof that ineligible failures do
not call the fallback. Product authority is `docs/SPECIFICATION.md`; provider
identification and attribution must follow current authoritative MET Norway
documentation when implementation begins.

Current seams verified during plan review:

- `OpenMeteoLiveSource` already maps HTTP failures to
  `LiveWeatherResult.TransportFailure(SERVICE_UNAVAILABLE)`, IO failures to
  `NETWORK`, timeouts to `TIMEOUT`, unknown exceptions to `UNKNOWN`, malformed
  responses to `InvalidMapping`, no data to `NoResult`, and usable partial
  results to `Success`.
- `LiveWeatherRepository` currently delegates to one `LiveForecastSource`.
- `ForecastRequest` contains location/timezone, coordinates, coverage, and
  requested canonical fields. `LiveWeatherResult.Success` carries current and
  forecast data, source, retrieval time, provenance, and partial-result facts.

## Production boundary

- `app/src/main/java/com/oxygen/weather/data/` and `data/provider/`: only the
  provider-local MET Norway transport/decoder/canonical mapper and the minimal
  live-source composition needed for primary/fallback selection.
- Deterministic tests under `app/src/test/java/com/oxygen/weather/data/` and
  provider-local subpackages.
- `DATA_SOURCES.md`: add the implemented source identification/attribution and
  verified authoritative references/access date. Broader terms/license review
  is excluded.
- Evidence only under
  `.codex/test-artifacts/113-met-norway-fallback/`.

No Compose, application-state, cache, or location-selection changes.

## Functional invariants

- Open-Meteo remains the sole primary. MET Norway is called at most once and
  only for an explicit allowlist of typed primary transport failures. Failure
  eligibility is never inferred from exception text, response bodies, URLs, or
  weather values.
- **Owner decision approved 2026-10-04:** fallback-eligible transport failures
  are `NETWORK`, `TIMEOUT`, and `SERVICE_UNAVAILABLE`; `UNKNOWN` is ineligible.
  The implementation must encode this decision as an explicit allowlist and
  test every enum member. Do not broaden the approved policy without a new
  owner decision.
- `UnsupportedFields`, `NoResult`, `InvalidMapping`, and any future non-
  transport result do not invoke fallback. A usable partial success remains a
  primary success and does not invoke fallback.
- Forward the same immutable `ForecastRequest` instance, unchanged, to each
  invoked source. Preserve its coordinates, location/timezone, coverage, and
  fields.
- Return the complete successful result of exactly one provider. Never splice
  current or forecast sections across providers. Source, provenance, valid
  times, retrieval time, unsupported fields, and invalid-section facts must
  describe the returned source only.
- If fallback returns no usable result, return its safe typed failure in the
  existing `LiveWeatherResult` vocabulary. Do not expose primary data as
  success or leak response bodies, raw exceptions, or credential-bearing URLs.
- MET Norway endpoint and required identification are explicit configuration;
  no secrets or network-dependent tests are introduced.

## Implementation steps

1. Inspect the provider/repository seams above and record an eligibility table
   for every `ForecastTransportFailure.Kind` and every non-transport result,
   applying the owner-approved allowlist above. Review current official MET
   Norway Locationforecast documentation for endpoint, identification
   header, attribution, rate/request guidance, supported variables and units,
   timezone/timestamps, and terms relevant to this integration. Record exact
   source links and access date in the cycle evidence.
2. Define the smallest MET Norway adapter boundary: explicit endpoint and
   injected transport/clock, provider-shaped decoding isolated from canonical
   mapping, and safe typed outcomes compatible with the existing live result
   vocabulary. Map only documented, supportable facts; preserve missing and
   unsupported values honestly. Do not add current-condition facts unless the
   documented source response and existing canonical contract support them.
3. Add a source composition that invokes primary once, applies the approved
   typed allowlist, and invokes fallback at most once with the same request.
   Keep source-specific adaptation local; avoid retries, recursive fallback,
   shared failure taxonomy expansion, and provider blending.
4. Add fixture tests for MET Norway request construction and identification,
   decoding, canonical field/unit/timezone mapping, chronology, null/missing
   data, provenance/source, retrieval time, and partial results. Inject
   transport and clock; do not call the live service.
5. Add a repository call matrix covering each transport failure enum member,
   each non-eligible result (including partial success), fallback success and
   each fallback failure/no-result/unsupported/invalid-mapping outcome,
   unchanged request identity, exact call counts, and source-isolated returned
   values/provenance.
6. Run focused checks, then the broader repository checks listed below. Save
   command output, eligibility matrix, fixture mapping notes, documentation
   links/access date, and any unavailable verification in the cycle evidence
   directory. Inspect the final diff and whitespace before closeout.

## Acceptance criteria

- A source-level eligibility table matches the owner-approved allowlist and
  repository tests cover every transport enum member plus every non-transport
  result. Future enum additions fail closed until explicitly classified.
- Eligible primary failures call MET Norway once; ineligible results call it
  zero times. Fallback itself is never retried.
- A successful fallback returns MET Norway's complete usable result, with its
  own source/provenance and retrieval/valid-time facts. Tests prove there is no
  primary/fallback value mixing.
- Fallback `TransportFailure`, `UnsupportedFields`, `NoResult`, and
  `InvalidMapping` remain typed safe outcomes with no invented data.
- Request construction/identification and mapping fixtures cover documented
  supported fields, canonical units, request timezone, valid-time chronology,
  missing/null values, partial horizons, source identity, and injected
  retrieval time without live network access.
- `DATA_SOURCES.md` or retained cycle evidence records verified current
  identification/attribution requirements with direct authoritative links and
  access date. This is not a broad legal/source audit.
- Workflow, focused tests, contract, regression tests, applicable `check`,
  whitespace, and final diff outcomes are recorded. Unavailable checks and
  their reason are explicit; no installed-app or visual boundary is claimed.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/113-met-norway-fallback/`.
Retain `verification.md` with exact command lines/results, policy matrix,
documentation URLs/access date, fixture notes, limitations, and unrun checks.

- Focused: `./gradlew :app:testDebugUnitTest --tests
  'com.oxygen.weather.data.WeatherRepositoryTest' --tests
  'com.oxygen.weather.data.provider.metnorway.MetNorwayAdapterTest' --tests
  'com.oxygen.weather.data.provider.metnorway.MetNorwayMapperTest' --tests
  'com.oxygen.weather.data.provider.metnorway.MetNorwayLiveSourceTest'` plus
  the new source-composition test class. Final package/class filters must match
  the implemented names and the exact executed command must be recorded.
- Contract: `python scripts/dev.py contract`.
- Regression: `python scripts/dev.py test`.
- Broader: `python scripts/dev.py check` when Android SDK/dependencies are
  available; otherwise record the concrete environment blocker.
- Lifecycle: `python scripts/dev.py workflow` before and after plan updates and
  during closeout as appropriate.
- Final hygiene: `git diff --check` and inspect the complete diff against this
  plan.
- No installed-app, live-network, visual, or accessibility evidence applies.

## Risks and assumptions

- The live result vocabulary distinguishes four transport kinds but does not
  itself define policy. The owner-approved allowlist defines eligibility for
  this slice; unknown/future categories must fail closed.
- MET Norway's current endpoint, required identification, attribution,
  documented units/fields, timezone behavior, and use conditions need primary
  source verification at execution time. Plan review does not claim those
  current requirements have been verified.
- The Open-Meteo source can return current plus forecast in one result, while
  the provider-neutral `ForecastProvider` contract is forecast-only. Compose
  fallback at the live-source boundary without dropping a successful primary
  result's current data or inventing fallback current data.
- Context budget: expected to fit one bounded cycle under the roadmap's 65%
  limit if limited to adapter, mapping, orchestration, and deterministic
  fixtures. If authoritative schema/mapping work exceeds that boundary, stop
  and draft a dependent partial cycle before expanding production scope.

## Out of scope

- Cache, persistence, offline restoration, stale-data policy, retry/backoff,
  provider blending/quorum, or fallback loops.
- Application-state/ViewModel or Compose changes; R2.5 provenance/freshness UI;
  settings; installed visual/accessibility evidence.
- Location search/selection/persistence or permissions.
- Alerts, historical/derived values, broad provider/architecture refactors,
  dependency upgrades, or changes to canonical weather semantics.
- Full provider terms/license audit or live-network validation.
