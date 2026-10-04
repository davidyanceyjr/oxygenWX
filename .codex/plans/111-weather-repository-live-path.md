# Plan 111 — WeatherRepository live path

Status: Completed
Cycle ID: 111-weather-repository-live-path
Roadmap item: R2.3
Created: 2026-10-03
Reviewed: 2026-10-03

## Objective and observable outcome

Implement one provider-neutral repository entry point that accepts an explicit
`ForecastRequest` and returns a typed live outcome composed from the existing
Open-Meteo adapter and canonical mapper. A deterministic test can observe the
request passed through, the single provider invocation, the resulting canonical
forecast and current facts, provenance, and distinct unsupported/no-result/
transport/mapping outcomes. No cache restoration or application/UI state is
introduced.

R2.3 roadmap exit is the scope ceiling: one Open-Meteo live path, origin and
provenance retained, success/failure distinguished, and no cache or UI state.

## Production boundary

- `app/src/main/java/com/oxygen/weather/data/` — a minimal provider-neutral
  repository contract, live result/outcome model, and one repository
  implementation/composition seam.
- `app/src/main/java/com/oxygen/weather/data/provider/openmeteo/` — only the
  narrow adapter needed to expose its existing typed mapping outcome to the
  repository seam. Keep wire DTOs and Open-Meteo details in this package.
- `app/src/test/java/com/oxygen/weather/data/` — deterministic repository
  tests; provider package tests may be added only for the composition boundary.
- Existing provider-neutral provider contract, canonical models, presentation,
  app state, and UI are not production edit targets.

## Functional invariants

- Repository API and returned models contain no Open-Meteo DTOs, response
  bodies, URIs, credentials, or exception text.
- Preserve the explicit request/location identity and timezone, canonical
  values, chronological ordering, nullable fields, source identity, valid time,
  and retrieval time. Do not infer or fill missing values.
- Preserve mapped current facts alongside forecast horizons in the repository
  live result; do not force current data into `ForecastData` or relabel model
  output as an observation.
- Distinguish usable partial success, unsupported fields without usable data,
  no result, safe transport failure, and unusable/invalid mapping. Preserve
  supported sibling sections when mapper output already does so.
- Invoke the configured source once per repository request. No retry, fallback,
  provider blending, implicit endpoint, cache read/write, location mutation, or
  state update.
- Existing fixture repository and presentation behavior remain unchanged.

## Implementation steps

1. Recheck the R2.1, R2.2, and R2.2A closed plans/history/evidence and inspect
   current repository/model consumers. Confirm the seam remains as described:
   `ForecastProviderResult.Success` carries `ForecastData`, while
   `OpenMeteoMapping` separately carries current facts and provenance.
2. Define the smallest neutral repository live outcome and repository
   interface around `ForecastRequest`. Include current facts/provenance
   optionally beside forecast, origin/source identity, retrieval time, and
   safe failure categories. Model unsupported/no-result/transport failure
   explicitly; do not stringify provider errors.
3. Add a provider-local bridge that calls the existing Open-Meteo adapter and
   mapper once and translates the typed mapping into that neutral outcome.
   Preserve current-only mapped output as a success when usable current facts
   exist; preserve forecast-only output when usable forecast exists. If neither
   is usable, map unsupported fields, no result, and transport failure
   truthfully. Invalid section details remain safe and bounded.
4. Add repository tests with an injected fake source/bridge. Cover complete
   success, current-only and forecast-only partial success, sparse/missing
   canonical values and provenance, unsupported fields, no result, transport
   failure, and unusable mapping. Assert exact request pass-through, exactly
   one invocation, location/timezone, safe outcome fields, and no fabricated
   forecast/current values.
5. Review the final package boundary and diff; run focused checks followed by
   the broader checks below. Record exact outcomes and limitations in the
   cycle evidence file.

## Acceptance criteria

- A provider-neutral repository API accepts an explicit request and returns
  typed success/failure outcomes for the configured Open-Meteo live path.
- Current facts from the existing mapper survive composition beside forecast
  data, including a current-only success without synthesized horizons.
- Tests demonstrate origin/provenance, valid/retrieval time, request location
  and timezone, nullable values, and partial data preservation.
- Tests distinguish success, unsupported-without-data, no result, transport
  failure, and unusable mapping; no raw provider/exception details escape.
- The configured bridge is called once; no fallback, retry, cache, or UI/state
  behavior is present.
- All verification results and any unavailable environment boundary are
  recorded under the exact evidence path below. Workflow remains valid and the
  plan remains PLANNED until separately activated.

## Verification and evidence

From repository root, after activation and implementation:

1. Focused: `python scripts/dev.py test` is the supported cross-platform
   wrapper but runs the full unit suite. For iteration, run the direct Gradle
   task with filters for the new repository test and existing
   `OpenMeteoMapperTest`/`OpenMeteoAdapterTest`, using the JDK/SDK selection
   documented by `scripts/dev.py` (the exact filter names should match the
   implemented test class names).
2. Contract: `python scripts/dev.py contract`.
3. Regression: `python scripts/dev.py test`.
4. Broader available check: `python scripts/dev.py check` (unit tests, lint,
   debug assemble); record exact environment/tooling failure if unavailable.
5. Lifecycle/scope: `python scripts/dev.py workflow`, then `git diff --check`
   and inspect the complete diff against this boundary.

Write exact commands, results/counts, any failed attempts and their resolution,
environment limitations, and bridge decisions to
`.codex/test-artifacts/111-weather-repository-live-path/verification.md`.
Installed visual evidence and live-network evidence are not applicable: this
cycle changes neither rendering nor networking behavior beyond deterministic
composition tests. Do not claim either was performed.

## Risks and assumptions

- **Verified prerequisites:** R2.1 cycle 105, R2.2 cycle 107, and R2.2A cycle
  110 are closed. Cycle 110's history/evidence explicitly leaves preservation
  of current facts through repository composition to R2.3.
- **Contract constraint:** the existing `ForecastProvider` success shape holds
  `ForecastData` only. This plan keeps that contract unchanged and composes
  from the provider-local typed `OpenMeteoMapping` result. Do not route through
  the narrower success type if it would discard current facts.
- **Assumption requiring owner review before activation:** a provider-local
  adapter over `OpenMeteoAdapter` plus `OpenMeteoMapper` satisfies R2.3's
  provider-neutral repository requirement while leaving the R2.1 forecast-only
  contract unchanged. If repository-wide composition requires every provider
  to implement a common current-plus-forecast contract, stop before production
  edits and revise roadmap/plan explicitly; that is a broader contract change.
- A usable current-only result is authorized by the already implemented
  mapper outcome and product rule against fabricated horizons. Its repository
  representation must make absent forecast explicit.
- Keep scope within the roadmap item's one-live-path boundary. If inspection
  reveals a neutral model/API redesign, split/revise before activation rather
  than expanding implementation silently.

## Out of scope

- MET Norway fallback, retries, cache/persistence, offline/stale policy, and
  provider blending (R2.4 and R3.x).
- Application state/ViewModel integration, selected-location concurrency, or
  Compose/presentation/UI changes (R2.3A and later).
- Location selection/permissions, alerts, historical/derived weather,
  settings, themes, layout, and effects.
- Open-Meteo query/field expansion, canonical remapping, changing neutral
  forecast semantics, or adding networking dependencies beyond the completed
  adapter.

If implementation must cross these exclusions, stop and revise the roadmap
and plan through the cycle workflow before changing production code.
