# Plan 016 — Presentation state contract

Status: Active
Cycle ID: 016-presentation-state-contract
Roadmap item: R1.2
Created: 2026-09-21

## Objective

Establish a typed presentation boundary for a normalized `WeatherBundle` so the
Home presentation can distinguish usable complete data, a usable but
short/partial horizon, field-level missing values, and no usable presentation
without making Compose infer state from formatted strings. The independently
observable outcome is a deterministic mapper contract and tests that preserve
the current Home meaning while making these states explicit.

## Production boundary

`app/src/main/java/com/oxygen/weather/presentation/`: the typed Home
presentation state/value models and the pure mapper boundary from canonical
domain data plus derived data. The existing deterministic fixture remains the
only caller. The boundary may adapt the current `HomePresentation` models, but
must not move repository/provider/cache facts into Compose.

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
- A missing field is distinguishable from an unavailable whole presentation;
  field-level unavailable values do not make otherwise usable forecast data
  unusable.
- Provider DTOs, repository results, refresh failures, cache freshness, and
  transport/loading semantics do not leak into the presentation package in
  this slice.
- Existing visible strings and accessibility summaries remain semantically
  equivalent for the current fixture, including provenance and update context.

## Implementation steps

1. Run the workflow/contract checks and focused presentation tests; inspect the
   current mapper and canonical nullable/horizon behavior before editing.
2. Define the smallest typed contract needed for this boundary, including a
   sealed/explicit top-level state for usable complete, usable partial, and
   unavailable presentation, plus a typed field-level missing/unavailable
   representation where the current string-only model cannot carry the
   distinction. Keep rendering adapters source-compatible where practical.
3. Implement pure mapping/classification rules using supplied horizon lengths,
   valid canonical records, and explicit missing values. Do not classify
   loading, cached/stale, refresh-failed, or cache-write outcomes; those belong
   to R1.2A and later repository/cache slices.
4. Add deterministic tests for complete, short hourly/daily horizons, missing
   current/hourly/daily fields, no usable forecast/current data, empty Details
   groups, chronological ordering, and unchanged provenance/update mapping.
5. Run focused tests, the full available deterministic test/check commands,
   `git diff --check`, and inspect the final diff. Preserve command output and
   test notes under the cycle evidence directory.

## Acceptance criteria

- A typed mapper result explicitly identifies complete usable data, usable
  partial horizon, and unavailable whole presentation states.
- Field-level missing values are represented without numeric fabrication and
  remain distinguishable from whole-presentation unavailability.
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
  initially be consumed through a narrow adapter to avoid turning this slice
  into a page-rendering rewrite.
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
