# Plan 017 — Refresh and cache presentation states

Status: Completed
Cycle ID: 017-refresh-cache-presentation-states
Roadmap item: R1.2A
Created: 2026-09-21
Revised: 2026-09-21 (revision 2)

## Objective

Extend the typed Home presentation boundary with a second, outer state layer
for loading and refresh/cache outcome. The new boundary must distinguish
loading, data supplied from live or cached origin, refresh failure while data is
retained, and failure with no retained data. It must supply deterministic,
visible and accessibility-equivalent status wording without inventing weather,
freshness, a cache, source/update metadata, or a failure cause. R1.2's
complete/partial/unavailable weather-content classification remains a nested,
independent value whenever an input supplies a bundle.

The independently observable outcome is a pure
`HomePresentationMapper.mapLoadState(HomePresentationInput)` contract. It
accepts an explicit loading/data/no-data-failure input and returns a
presentation-native outer state; the existing `mapState(bundle, derived)` and
`map(bundle, derived)` APIs remain source-compatible.

## Production boundary

Production code is limited to `app/src/main/java/com/oxygen/weather/presentation/`.
Add a small input algebra in that package: `Loading`, `Data` (a
`WeatherRepositoryResult` plus its already-derived `DerivedWeather`), and
`FailureWithoutData` (a supplied `RefreshFailure`). The input is an
application-to-presentation mapper input, not a UI model. It may consume the
existing canonical/repository facts but must not change their types or
invariants.

Add presentation-native output types only: an outer sealed load state, a
live/cached/retained-data origin representation, a current/stale/unknown
freshness representation, and a status presentation carrying `visibleText` and
`accessibilitySummary`. `Data` results must nest the existing
`HomePresentationState`; `FailureWithoutData` must have status text only and no
`WeatherBundle`, `HomePresentation`, source line, update line, location, or
synthetic weather field. The mapper remains pure. The deterministic fixture is
not rewired; Compose and application-state integration remain unchanged.

Documentation changes are limited to the contract paragraph in
`docs/ARCHITECTURE.md` and the R1.2A status/outcome in `docs/ROADMAP.md`.
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` already requires these states as they
are added and must not be changed to imply rendering that this slice does not
implement. No domain/provider/cache contract changes are expected.

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
  domain meanings. The mapper translates them into presentation-native state
  values rather than making Compose interpret domain enums. Unknown freshness
  remains unknown; failure wording must not claim a cause more specific than
  the supplied `RefreshFailureKind` supports.
- A successful live result remains successful when only its cache write failed;
  cache-write outcome is not a refresh failure and must not change weather
  presentation state or its status wording.
- A failure with no cached data is distinct from R1.2's unusable-weather state
  and from loading. It carries no fabricated `WeatherBundle`, location,
  source/update claim, or weather-content state.
- Mapping a supplied repository result always preserves the nested R1.2
  `Complete`, `Partial`, or `Unavailable` outcome. A repository result whose
  supplied bundle has no weather fact is not silently converted into a
  transport/cache failure.
- Status copy is a first-class presentation value. Its visible and accessibility
  strings are identical in this model so neither surface can drift
  semantically. Compose must consume the typed outer state, not infer it from
  formatted status text.
- No fetching, retry, cache read/write, persistence, provider orchestration, or
  navigation behavior is added.

## Implementation steps

1. Re-run the workflow check; inspect the completed R1.2 mapper, the canonical
   repository-result types, and their tests. Record the exact mapping matrix
   before editing: input class, supplied origin/freshness/failure facts, outer
   output class, nested R1.2 state, and status copy.
2. Add `HomePresentationInput` and the presentation-native outer state models.
   Keep R1.2's `HomePresentationState` untouched as the nested content model.
   Do not expose raw `WeatherDataOrigin`, `WeatherFreshness`,
   `RefreshFailureKind`, `CacheWriteOutcome`, provider DTOs, or persistence
   entities in the output consumed by UI.
3. Implement `mapLoadState(input)` with this precedence:

   | Input | Outer result | Required status facts |
   | --- | --- | --- |
   | `Loading` | loading | `Loading weather data.` |
   | `Data` with no refresh failure and live origin | live data | live origin plus supplied current/stale/unknown freshness |
   | `Data` with no refresh failure and cache origin | cached data | cached origin plus supplied current/stale/unknown freshness |
   | `Data` with a refresh failure, regardless of retained origin | refresh failed with retained data | supplied failure kind, retained live/cache origin, and supplied freshness |
   | `FailureWithoutData` | failed without data | supplied failure kind and an explicit absence of saved weather data |

   Use the domain fact labels literally: current, stale, or unknown freshness;
   network, source, or unspecified refresh failure. Do not derive an age,
   infer a saved forecast, promise retry, or present `occurredAt` without a
   documented display-time policy. For every data-bearing row, call the
   existing `mapState(bundle, derived)` once and retain that exact nested result;
   its source/update/valid-time formatting remains owned by R1.2 mapping.
4. Make the wording deterministic and test it exactly. The visible and
   accessibility strings must be the same model value. Data-bearing status text
   identifies origin and says `Freshness: current`, `Freshness: stale`, or
   `Freshness: unknown`; refresh-failed text additionally identifies only the
   supplied network/source/unspecified failure and says that retained live or
   saved data is being shown. No-data failure text identifies only the supplied
   failure kind and says that no saved weather data is available.
5. Preserve compatibility: leave `mapState(bundle, derived)` and
   `map(bundle, derived)` signatures and output behavior unchanged, do not
   change `WeatherRepositoryResult`, and do not alter the fixture or Compose
   caller. Update `docs/ARCHITECTURE.md` after implementation to name the new
   outer mapper boundary and its nested R1.2 relationship. Keep R1.2A marked
   ACTIVE in `docs/ROADMAP.md` until the cycle is closed.
6. Add a dedicated deterministic presentation-state test file plus only the
   minimal shared fixture helpers it needs. Run the focused new test while
   iterating, then the required broader verification. Preserve command output,
   exact test count/result, final diff inspection, and unverified boundaries
   under `.codex/test-artifacts/017-refresh-cache-presentation-states/`.

## Acceptance criteria

- `mapLoadState` has distinct typed results for loading, live data, cached data,
  refresh failure with retained data, and failure without data; no caller must
  parse strings to identify an outcome.
- Every data-bearing outer state retains the exact nested R1.2 `Complete`,
  `Partial`, or `Unavailable` result. Complete and partial fixture cases remain
  weather-bearing; a no-weather-fact bundle remains nested unavailable rather
  than becoming a no-data refresh failure.
- The live/cache matrix preserves each supplied `CURRENT`, `STALE`, and
  `UNKNOWN` freshness fact exactly. It introduces no age threshold or
  freshness inference.
- The retained-data failure matrix preserves network, source, and unknown
  failure distinctions, explicitly identifies the retained origin, and never
  calls data freshly fetched. A live result with every cache-write outcome,
  including `FAILED`, remains live data rather than refresh failure.
- Failure without data has no weather-content model, location, source line,
  update line, provenance line, or invented cached-data claim. Its exact status
  wording is limited to the supplied failure kind and absence of saved data.
- Each status model has exact, accessibility-equivalent visible text and
  accessibility summary. Existing `mapState`/`map` behavior and the deterministic
  fixture caller remain unchanged.
- `docs/ARCHITECTURE.md` accurately describes the implemented boundary;
  `docs/ROADMAP.md` accurately reflects R1.2A as active during this cycle.
- No UI, application-state, provider, repository orchestration, cache,
  persistence, networking, retry, unit, navigation, or visual behavior changes.

## Verification and evidence

Focused deterministic test while iterating (using the repository Gradle
launcher after ensuring the same JDK 17+ requirement used by `scripts/dev.py`):

```sh
./gradlew --no-daemon :app:testDebugUnitTest --tests com.oxygen.weather.presentation.HomePresentationLoadStateTest
```

Minimum completion commands:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

The new test class must cover this matrix, using deterministic fixture bundles:

- loading;
- each live and cached origin across `CURRENT`, `STALE`, and `UNKNOWN`;
- retained-data refresh failure for each failure kind and both retained origins;
- complete, partial, and nested-unavailable weather-content preservation;
- all three live cache-write outcomes, especially `FAILED`;
- no-data failure for each failure kind, including absence of weather/source/
  update/location fields; and
- exact visible/accessibility status equivalence plus unchanged existing R1.2
  mapper tests.

A forced test rerun is appropriate if Gradle reports all tests up-to-date. This
is a presentation-contract slice; installed visual evidence is not required
unless work expands into Compose rendering. Record unavailable commands and
their exact reason in the cycle evidence file. Do not claim visual, RTL, or
service-level accessibility verification unless performed.

## Risks and assumptions

- `WeatherRepositoryResult` carries a bundle but does not encode loading or a
  failure with no retained bundle, so the explicit input algebra is required.
  It must not weaken or mutate the existing repository-result model.
- Refresh failure plus cached data may have `UNKNOWN` freshness; cache origin
  alone is not proof of staleness. Conversely, an unusual live/stale or
  live/unknown domain fact must be presented faithfully rather than normalized
  away.
- `RefreshFailure.occurredAt` has no established selected-location display-time
  contract. Preserve the domain input but do not surface an event time in this
  slice; a later UI/time-formatting slice may add it deliberately.
- The wording communicates state without promising automatic retry, claiming
  an official-alert update, or inventing a root cause.
- R1.2 availability remains nested, not replaced by outer load status; empty
  Details, optional-field absence, or a partial horizon does not imply refresh
  failure.

## Out of scope

- Any provider/network request, retry/backoff, refresh scheduling, repository,
  cache serialization/read/write, cache retention, or location behavior.
- Application-state/ViewModel integration and changing the fixture caller.
- Compose banners, loading spinners, dialogs, layout/theme/effects, or visual
  redesign.
- Unit conversion/preferences, weather-data freshness thresholds, alerts,
  provenance semantics, or changes to canonical domain models.
- Changing `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, claiming a visible Home
  loading/error state exists, or recording installed visual/accessibility
  evidence for this model-only slice.
