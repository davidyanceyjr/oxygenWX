# Plan 017 — Refresh and cache presentation states

Status: Active
Cycle ID: 017-refresh-cache-presentation-states
Roadmap item: R1.2A
Created: 2026-09-21
Revised: 2026-09-21

## Objective

Extend the typed Home presentation boundary so application state can report
loading, live/current data, cached data with freshness, refresh failure while
retaining usable data, and failure without cached data. Each state supplies
concise visible and accessibility wording without inventing weather, freshness,
or a failure cause. The prior complete/partial/unavailable weather-content
classification remains independently available inside states that contain
weather data.

## Production boundary

Only `app/src/main/java/com/oxygen/weather/presentation/` and its deterministic
unit tests. Add the smallest pure mapping/input contract needed to convert
existing `WeatherRepositoryResult` facts into presentation states and represent
loading or failure without data, which the current repository-result model
cannot carry. The deterministic fixture remains the only caller; do not change
Compose or application-state integration. No domain/provider/cache contract
changes are expected.

This is one bounded presentation-model slice, expected to remain below the
repository's approximately 45% context-window limit. Stop and split follow-up
work if satisfying the contract requires production changes outside the
presentation package.

## Functional invariants

- `Now -> Hourly -> Daily -> Details`, page/window meaning, chronology,
  provenance, valid/update time, and R1.2 complete/partial/weather-unavailable
  semantics remain unchanged.
- Weather values remain nullable and provider-neutral. Cached or failed-refresh
  states retain and present the supplied weather bundle; they do not replace
  it with placeholders or imply that cached data is newly fetched.
- `WeatherDataOrigin`, `WeatherFreshness`, and `RefreshFailure` retain their
  domain meanings. Unknown freshness remains unknown; failure wording must not
  claim a cause more specific than the supplied `RefreshFailureKind` supports.
- A successful live result remains successful when only its cache write failed;
  cache-write outcome is not a refresh failure and must not change weather
  presentation state.
- A failure with no cached data is distinct from R1.2's unusable-weather state
  and from loading. It carries no fabricated `WeatherBundle` or source/update
  claim.
- Visible state text and accessibility summary are both explicit and
  semantically equivalent. Compose must not infer state from formatted strings.
- No fetching, retry, cache read/write, persistence, provider orchestration, or
  navigation behavior is added.

## Implementation steps

1. Read the completed R1.2 mapper, repository-result domain fields, and existing
   presentation tests; confirm the state matrix and allowed wording against
   specification/roadmap authority.
2. Define a compact sealed presentation state/input contract for loading,
   usable live data, cached data with freshness, refresh failure with retained
   data, and failure without cached data. Reuse R1.2 weather presentation
   states and existing domain failure/freshness facts; do not duplicate provider
   or persistence models.
3. Implement a pure mapper that preserves source, freshness, valid/update time,
   and the nested complete/partial weather state. Add concise visible copy and
   accessibility summaries for every top-level state. Keep any compatibility
   API narrow and avoid changing the current fixture caller.
4. Add deterministic tests for every state, freshness known/stale/unknown,
   failure kinds and retained data, no-data failure, live success with failed
   cache write, and preservation of nested weather/provenance mapping.
5. Run focused presentation tests, workflow/contract checks,
   `python scripts/dev.py check`, and `git diff --check`. Preserve exact outcomes under
   `.codex/test-artifacts/017-refresh-cache-presentation-states/`.

## Acceptance criteria

- Typed presentation results distinguish loading, live/current usable data,
  cached usable data, refresh failure with usable retained data, and failure
  without cache.
- Cached freshness is explicitly current, stale, or unknown according to the
  supplied domain fact; no age threshold or freshness inference is introduced.
- Refresh-failed-with-cache retains and nests the mapped weather presentation
  and does not describe it as freshly fetched.
- Failure-without-cache contains no fabricated weather/source/update values and
  provides honest visible and accessibility wording grounded in the supplied
  failure kind.
- A cache-write failure beside a successful live result does not become a
  refresh-failure state.
- Deterministic tests cover the state matrix and existing R1.2 presentation
  tests continue to pass.
- No UI, application-state, provider, repository orchestration, cache,
  persistence, networking, retry, or visual behavior changes.

## Verification and evidence

Minimum commands:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

Run the focused presentation test target while iterating. A forced test rerun is
appropriate if Gradle reports all tests up-to-date. This is a presentation
contract slice; installed visual evidence is not required unless implementation
expands into Compose rendering. Record any unavailable command and its exact
reason in the cycle evidence file. No visual or service-level accessibility
verification may be claimed unless performed.

## Risks and assumptions

- `WeatherRepositoryResult` always carries a usable bundle, so loading and
  failure-without-cache need a small explicit input representation without
  weakening that domain invariant.
- Refresh failure plus cached data may have unknown freshness; preserve that
  uncertainty instead of treating cache origin as proof of staleness.
- The wording should communicate status without promising automatic retry or
  implying official-alert freshness.
- R1.2 typed weather availability remains nested, not replaced by the load
  status; empty Details or partial horizons do not imply load failure.

## Out of scope

- Any provider/network request, retry/backoff, refresh scheduling, repository,
  cache serialization/read/write, cache retention, or location behavior.
- Application-state/ViewModel integration and changing the fixture caller.
- Compose banners, loading spinners, dialogs, layout/theme/effects, or visual
  redesign.
- Unit conversion/preferences, weather-data freshness thresholds, alerts,
  provenance semantics, or changes to canonical domain models.
