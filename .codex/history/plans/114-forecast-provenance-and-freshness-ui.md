# Plan 114 — Forecast provenance and freshness UI

Status: Completed
Cycle ID: 114-forecast-provenance-and-freshness-ui
Roadmap item: R2.5
Created: 2026-10-04
Reviewed: 2026-10-04

## Objective and observable outcome

Expose trustworthy forecast source/provenance, available valid and retrieval
times, partial-horizon status, data origin/freshness, and refresh outcome on the
production Now and Details pages. The outcome is a production Compose path
which renders deterministic supplied states and installed captures proving the
displayed facts match those states.

This is a presentation slice. It does not connect the Home screen to a live
repository or implement cache behavior. Cached/stale and failure states must be
rendered from explicit typed state inputs through a bounded presentation seam.

## Roadmap dependency and authority

- R2.1–R2.4 are recorded complete in cycles 105, 107, 110–113; R2.5 is the
  next eligible general roadmap item after TP.3.
- Exit criterion: installed Now and Details captures show source, valid/fetch/
  update times, partial horizon, and refresh state for live, cached/stale, and
  failure cases, with values and provenance matching supplied state.
- `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` govern
  data meaning, Now/Details presentation, and accessibility. `docs/UI_DEVELOPMENT_WORKFLOW.md`
  governs installed visual evidence.
- Existing `LiveWeatherPresentation` carries source identity, origin,
  `retrievedAt`, current/forecast provenance, invalid sections, and unsupported
  fields. `DataProvenance.validAt` and `retrievedAt` are optional independently.
  The current Home renders the development fixture; the live bridge is not
  connected to it. Existing `HomeLoadState` includes cache and retained-failure
  states, but this cycle must not claim repository cache restoration.
- `retrievedAt` is Oxygen's retrieval/fetch time and may also be described as
  the data update time. Do not present these as two independent timestamps.
  `validAt` is the source-stated validity instant, not a forecast horizon or
  inferred observation time. If a required timing fact is absent, display an
  explicit unavailable state or omit it and record the R2.5 exit limitation;
  never synthesize it.

## Production boundary

- Presentation models/formatting in `app/src/main/java/com/oxygen/weather/presentation/`
  for source, optional valid time, retrieval time, origin/freshness/refresh
  status, and partial horizon based only on supplied typed values.
- The smallest production integration needed in `app/src/main/java/com/oxygen/weather/ui/`
  and `app/src/main/java/com/oxygen/weather/ui/themeengine/` to render those
  supplied states on Now and Details. A test/development injection seam may
  supply typed scenarios to this production path; it must not contain provider
  or cache logic.
- Focused deterministic presentation tests and Compose assertions in existing
  `app/src/test/` and `app/src/androidTest/` test infrastructure.
- Installed captures and verification report under
  `.codex/test-artifacts/114-forecast-provenance-and-freshness-ui/`.

No provider, repository, canonical weather model, persistence/cache, location,
network, or refresh-policy changes are in the production boundary.

## Functional invariants

- Preserve supplied weather values, chronology, source identity, provenance,
  origin, freshness, refresh result, and timestamps. Presentation does not
  initiate a fetch or change weather meaning.
- Keep observation, model estimate, forecast, official alert, derived, and
  historical/reference data distinct. This slice adds no alert or derived
  weather content.
- Show missing metadata as unavailable/omitted. Never infer valid time,
  freshness, horizon completeness, or source identity from age, list length,
  display text, or a plausible default.
- A partial horizon and unsupported/invalid sections remain distinct from a
  complete horizon. Render only supplied forecast entries; add no padding.
- Cached/stale state requires explicit typed cache origin/freshness. A refresh
  failure with retained data remains distinct from failure without data.
- Important facts have visible text and matching accessibility semantics;
  color/decorative treatment alone does not communicate status.
- Preserve the four named pages, outer pager navigation, theme semantics,
  weather values, and source provenance. Effects Off remains opaque, static,
  and complete.

## Implementation steps

1. Inspect the exact live success/provenance fields, `HomeLoadState` variants,
   Now/Details production composition, and relevant tests. Record which
   provider fixtures supply `validAt`, retrieval time, section coverage, and
   origin/freshness/failure fields. Do not assume all sources populate all
   optional fields.
2. Define a small typed presentation contract for Now/Details provenance and
   status. Format instants in the selected location timezone. Keep source-stated
   valid time separate from Oxygen retrieval time. Carry a typed unavailable
   outcome for absent metadata rather than reconstructing it from strings.
3. Connect the contract to the production Now and Details content. Supply the
   following scenario states through a deterministic typed fixture seam:
   live combined data; live current-only and forecast-only data; explicit
   partial horizon; explicit cached/stale data; retained-data refresh failure;
   and failure without weather data. Ensure a source/timing-metadata sparse
   case renders honestly. This seam is for review/test scenarios only and must
   not make the demo launch path appear to have live or cached weather.
4. Add mapper tests for exact timezone conversion, source/origin/status,
   optional valid/retrieval time, missing metadata, partial horizon, and
   failure distinctions. Add Compose assertions on both pages for visible
   text and semantics, including absence/unavailable behavior and long text.
5. Install the application and capture the production Now and Details paths
   for the matrix below. Inspect each capture for clipping/overlap and verify
   facts against the fixture manifest. Exercise page navigation to reach
   Details; do not treat a standalone preview or showcase as installed-state
   evidence.
6. Run focused tests, applicable broader checks, inspect `git diff --check` and
   the final diff, then write exact results and limitations to the evidence
   report. Do not close the cycle as complete unless the roadmap exit is met;
   if source metadata cannot supply a required exit fact, record the blocker
   and stop dependent work.

## Visual and installed-state acceptance matrix

Installed production captures must cover both Now and Details for these
distinct states: live combined; live sparse/partial (including current-only
and forecast-only coverage); cached/stale; retained-data refresh failure; and
failure without data. Capture files may pair equivalent Now/Details state
screenshots when the fixture identity is unambiguous. Add one explicit
metadata-missing case with assertions and a capture where it materially
changes the display.

Required conditions:

- Compact baseline: 360x640 dp, font scale 1.0, LTR, selected standard
  production theme, Effects Off.
- Large text: 360x640 dp, font scale 1.3, LTR, Effects Off, including long
  source/location/status text.
- RTL: at least one representative state on both pages at 360x640 dp, font
  scale 1.0, RTL, Effects Off; semantic reading and chronology remain correct.
- Verify the actual installed app build and identify the fixture injection
  mechanism, app/build variant, device/emulator, theme, effects, viewport,
  density, font scale, layout direction, and scenario in the manifest.

This is not a full theme matrix. Use one standard theme for the required
states; Effects Off is mandatory for completeness and must be tested rather
than assumed from another component's evidence.

## Acceptance criteria

- Installed Now and Details captures cover live, cached/stale, retained-data
  refresh failure, and no-data failure, plus sparse/partial live coverage.
  Source, each available valid/retrieval time, partial horizon, and refresh
  state shown in each capture agree with that scenario's typed input.
- Retrieval/fetch/update time is labeled as one event. Valid time appears only
  when source provenance supplies it; absence is not disguised as retrieval
  time or current time.
- Current-only, forecast-only, and combined outcomes remain independently
  renderable. Missing sections/fields are not fabricated. Explicit stale and
  failure labels cannot appear for live states absent matching input.
- Focused presentation tests assert exact values/labels and timezone handling;
  Compose tests assert visible text and semantics on Now and Details for each
  status class, missing metadata, and partial horizon.
- Rendering and theme/effects changes do not invoke weather retrieval or
  change supplied values, provenance, navigation, or meaning.
- Compact, font scale 1.3, RTL, long text, and Effects Off conditions are
  documented in installed evidence. No critical provenance/status content is
  clipped or communicated only by color. Unrun service-level TalkBack remains
  explicitly unverified.
- `verification.md` records commands, outcomes, capture manifest, screenshot
  paths, final diff review, unavailable checks and reasons, and any unmet exit
  criterion. No roadmap completion claim is made for unmet criteria.

## Verification and evidence

Evidence root:
`.codex/test-artifacts/114-forecast-provenance-and-freshness-ui/`

Retain:

- `verification.md`: scenario results, commands/exit codes, environment,
  `git diff --check`, diff review, limitations, and roadmap exit disposition.
- `capture-manifest.md`: one row per screenshot with page, scenario/fixture
  identity, app variant/build, device/emulator, viewport/density, font scale,
  LTR/RTL, theme, effects, and filename.
- Installed Now/Details screenshots named by scenario and condition; do not
  store only cropped previews.

Focused checks:

- `python scripts/dev.py test --tests '*HomePresentation*' --tests '*LiveForecast*'`
  where supported by the script; otherwise run the equivalent Gradle focused
  test task and record the exact command.
- `python scripts/dev.py android-test` (or the repository's equivalent
  connected instrumentation command) for the new focused Compose assertions
  and installed captures, if an emulator/device is available.

Broader checks: `python scripts/dev.py check`; record any unavailable SDK,
dependency, emulator, or network prerequisite and the exact command outcome.
Also run `git diff --check` and inspect the final diff. The workflow check is
required before and after plan review; it does not count as implementation
verification.

## Risks and assumptions

- Provider provenance fields may be absent. The presentation must preserve
  that absence; the roadmap exit requires available source/timing facts to be
  evidenced. If provider fixtures cannot demonstrate the requested valid and
  fetch/update facts, implementation must report the precise unmet exit and
  request a roadmap/owner decision before expanding provider contracts.
- Existing cache-state types may not compose directly with the live
  presentation. Adapt only the minimum typed seam needed to render scenario
  states; do not imply R3 cache restoration exists.
- Installed scenario injection must be clearly identified as deterministic
  review data and must not become a production default or alter launch
  semantics.
- Emulator/SDK availability may constrain installed evidence. In that case,
  report the exact unavailable boundary; compilation or previews do not
  replace installed captures.
- No owner decision is currently required to begin within this boundary.

## Out of scope

- R3 location, saved-location, cache serialization/restoration, offline startup,
  or cache integration.
- Provider endpoint/decoder/mapping changes; repository orchestration;
  fallback/retry/refresh policy; new freshness thresholds.
- New canonical model fields unless an owner-approved roadmap change is made.
- Forecast-value changes, horizon padding/interpolation, provider blending,
  or inferred validity/freshness.
- General visual redesign, theme resolver changes, alert UI, unrelated
  accessibility work, and full theme/layout matrix verification.
- Service-level TalkBack audit and R6 accessibility closure.
