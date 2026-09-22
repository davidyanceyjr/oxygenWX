# Plan 016 — Presentation state contract

Status: Completed
Cycle ID: 016-presentation-state-contract
Roadmap item: R1.2
Created: 2026-09-21
Revised: 2026-09-21

## Objective

Establish a typed presentation boundary for a normalized `WeatherBundle` so the
Home presentation can distinguish a complete usable forecast horizon, a usable
partial horizon, field-level unavailable values, and no usable weather
presentation without making Compose infer state from formatted strings. The
independently observable outcome is a deterministic mapper contract with a
source-compatible legacy display adapter and tests that preserve the current
Home meaning while making those states explicit.

## Production boundary

`app/src/main/java/com/oxygen/weather/presentation/`: typed Home presentation
state/value models and the pure mapper boundary from canonical domain data plus
derived data. `docs/ARCHITECTURE.md` and `docs/ROADMAP.md` may receive only the
contract/status clarification required to describe that boundary. The existing
deterministic fixture remains the only caller. The boundary retains
`HomePresentationMapper.map(...)` as a display-only compatibility adapter; the
new `mapState(...)` is the typed result for later application-state work. No UI
or application-state caller changes in this slice.

This slice is deliberately sized below the repository's approximately 45%
context-window limit: one presentation package, its deterministic tests, and
workflow records only. If implementation reveals that UI/application-state
integration is required, stop at the contract and split that work into a
follow-up plan rather than expanding this cycle.

## Functional invariants

- Canonical values remain provider-neutral, metric/unit-stable, nullable, and
  semantically distinct from derived and historical values.
- `Now -> Hourly -> Daily -> Details`, chronological ordering, six-entry
  hourly windows, five-entry daily windows, date jumps, and Details grouping
  retain their current meaning.
- Missing values remain missing; no zero, repeated, interpolated, or plausible
  placeholder weather value is introduced.
- A partial horizon is represented honestly from supplied records; the mapper
  does not pad the requested 72-hour or 10-day target.
- A **complete** usable horizon has at least 72 supplied hourly records and at
  least 10 supplied daily records; supplied entries beyond those display caps
  remain outside the existing Home display model. A usable result with either
  supplied horizon below its target is **partial**. The target is a
  classification rule, not permission to fabricate records.
- A result is **usable** when current conditions contain at least one supplied
  weather fact (condition or measurement), or an hourly/daily record contains
  at least one supplied weather fact. Location, timestamps, provenance, and
  historical/derived values alone do not make weather data usable. When none
  exists, the state is whole-presentation unavailable without guessing a
  transport/cache/loading reason.
- A missing field is distinguishable from an unavailable whole presentation;
  typed field availability covers the current, hourly, and daily weather facts
  rendered by Home. Field-level unavailability does not make otherwise usable
  forecast data unusable.
- Provider DTOs, repository results, refresh failures, cache freshness, and
  transport/loading semantics do not leak into the presentation package in
  this slice.
- Existing visible strings and accessibility summaries remain semantically
  equivalent for the current fixture, including provenance and update context.

## Implementation steps

1. Run the workflow/contract checks and focused presentation tests; inspect the
   current mapper and canonical nullable/horizon behavior before editing.
2. Define the smallest typed contract needed for this boundary: a sealed
   top-level complete/partial/unavailable state; per-horizon completeness for
   partial results; and an available/unavailable presentation field type.
   Attach field states to current, hourly, and daily entries while retaining
   existing display strings and constructor compatibility.
3. Implement pure mapping/classification rules using supplied horizon lengths
   and supplied canonical weather facts. `mapState(...)` returns the typed
   state; `map(...)` keeps its existing `HomePresentation` result as a narrow
   compatibility adapter. Do not classify
   loading, cached/stale, refresh-failed, or cache-write outcomes; those belong
   to R1.2A and later repository/cache slices.
4. Add deterministic tests for complete, independently short hourly/daily and
   current-only horizons, typed missing current/hourly/daily fields, no usable
   current/forecast facts, timestamp-only forecast records, empty Details
   groups, chronological ordering, and unchanged provenance/update mapping.
5. Run focused tests, the full available deterministic test/check commands,
   `git diff --check`, and inspect the final diff. Preserve command output and
   test notes under the cycle evidence directory.

## Acceptance criteria

- `mapState(...)` explicitly returns `Complete`, `Partial`, or `Unavailable`;
  partial results identify each actual supplied horizon as complete or partial.
- Field-level missing values are typed without numeric fabrication and remain
  distinguishable from whole-presentation unavailability.
- Short or empty supplied horizons are classified from actual records and are
  never padded, repeated, or silently treated as complete.
- Derived and historical groups remain separate from normalized current/source
  measurements, including when one group is empty.
- Deterministic tests cover the state matrix and all existing
  `HomePresentationTest` behavior continues to pass or is updated only to
  assert the new explicit contract.
- No provider, repository, cache, refresh, Compose layout, navigation, unit
  preference, or live-network behavior changes.

## Verification and evidence

Minimum verification:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

The focused target is `HomePresentationTest` plus any new presentation-state
test. If `check` cannot run because Android tooling/dependencies are
unavailable, record the exact failure and run the smallest available JVM/test
verification instead. No installed visual evidence is required for this
presentation-only contract slice; do not claim visual or service-level
accessibility verification.

Preserve evidence under
`.codex/test-artifacts/016-presentation-state-contract/`, including
`verification.md` with commands, outcomes, and unverified boundaries.

## Risks and assumptions

- The existing Compose UI accepts `HomePresentation`; the state contract may
  initially be consumed through the retained `map(...)` compatibility adapter
  to avoid turning this slice into a page-rendering rewrite.
- “Complete” is display-target completeness (72 hourly and 10 daily supplied
  records), not a claim that every optional field is populated or fresh.
- “Unavailable” must remain honest without inventing a transport failure
  reason. Network/cache/loading wording is intentionally deferred to R1.2A.
- The current fixture has complete horizons, so sparse/empty fixtures must be
  constructed deterministically in tests rather than inferred from a visual
  run.
- If a clean API requires changes outside `presentation/` and its tests, record
  the dependency and split it before crossing the production boundary.

## Out of scope

- R1.2A loading, cached/stale, refresh-failed-with-cache,
  failed-without-cache, and cache-write outcome presentation states.
- Open-Meteo, MET Norway, repository orchestration, transport, retry, cache,
  location, alerts, or application-state/ViewModel integration.
- Unit conversion/preferences, settings, theme/effects/layout changes, visual
  redesign, pager/navigation changes, or installed screenshot/accessibility
  verification.
- New meteorological derivations, historical providers, forecast heuristics,
  or changes to canonical domain semantics.
