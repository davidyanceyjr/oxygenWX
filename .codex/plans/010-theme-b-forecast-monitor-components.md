# Plan 010 — Theme B forecast monitor components

Status: Completed
Cycle ID: 010-theme-b-forecast-monitor-components
Roadmap item: R0.6A
Created: 2026-09-21
Revised: 2026-09-21
Revision: implementation-ready full-slice revision

## Objective

Complete R0.6A by extracting three forecast-monitor primitives already rendered
by the candidate into the established UI-local component boundary:

- `MetricTile` — a labelled headline/value with optional supporting text;
- `HourlyForecastTile` — one supplied hourly presentation entry, including its
  optional weather mark, optional precipitation text, and spoken summary;
- `DailyForecastRow` — one supplied daily presentation entry, including its
  optional weather mark, precipitation meaning, and spoken summary.

The observable result is the existing Now, Hourly, and Daily rendering using
these primitives at the equivalent, existing call sites. This is an extraction
only: it preserves visual structure and presentation values while making the
entry-level renderers available to R0.7 and R0.8. It does not apply a new
Theme B page composition.

## Context-budget boundary

This remains one bounded slice under the roadmap's approximately 45%-of-context
rule. It is limited to one shared UI component file, the direct call-site and
private-renderer removal edits needed for a behavior-preserving extraction,
focused deterministic presentation regression coverage where it documents an
existing contract, and the closure documentation/evidence listed below.

Do not absorb a presentation-model change, Compose test-harness setup, Details
component, page redesign, provider/data change, or visual tuning. If the
extraction cannot be completed with the existing presentation inputs and
appearance contract, stop and record the dependency for its owning later slice.

## Production boundary

### Shared component contracts

Add the following internal composables to
`app/src/main/java/com/oxygen/weather/ui/MonitorComponents.kt`. They receive
only supplied presentation strings/models and `ResolvedAppearance`; no state,
navigation, window index, provider DTO, repository, persistence object, raw
weather model, or raw theme ID may cross this boundary.

```kotlin
@Composable
internal fun MetricTile(
    label: String,
    headline: String,
    supporting: String?,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
)

@Composable
internal fun HourlyForecastTile(
    entry: HourlyEntryPresentation,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
)

@Composable
internal fun DailyForecastRow(
    entry: DailyEntryPresentation,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
)
```

`MetricTile` deliberately uses the existing supplied strings rather than
introducing a new presentation model. It is the equivalent of the current
Now-page compact fact panel: it renders the label, headline, and optional
supporting line; `supporting == null` omits that line. The Now call sites pass
their already formatted precipitation and wind values unchanged. `MetricGroup`
is a Details concern and remains owned by R0.6B.

`HourlyForecastTile` owns exactly the existing per-entry `MonitorSection` and
its `clearAndSetSemantics { contentDescription = entry.spokenSummary }`.
Within that surface it preserves the current weather-mark conditional,
42dp mark, 10dp mark gap, 12dp inner inset, time/temperature/condition order,
one-line condition ellipsis, and optional `Precip <value>` line using the
resolved precipitation accent. It neither creates entries nor changes the
two-column/three-row window grid.

`DailyForecastRow` owns exactly the existing row content inside the existing
daily-window `MonitorSection`: 54dp day column, conditional 34dp mark with its
8dp gap, one-line ellipsized condition, aligned low/high, precipitation line,
and the entry's existing clear-and-set spoken summary. The daily window retains
the enclosing surface, padding, row weights, and five-entry layout.

All three components use existing Material typography and `ResolvedAppearance`
layout/effects roles. They do not add click handlers, gestures, animation,
scrolling, or an accessibility abstraction that hides existing visible facts.

### Existing call sites

Modify only `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` to
make the behavior-preserving extraction real:

1. Replace `CompactFactPanel` calls in Now with `MetricTile`, passing the exact
   existing labels/headlines/supporting strings, modifiers, and appearance.
2. In `HourlyWindow`, replace each entry's private `MonitorSection`/content
   block with `HourlyForecastTile(entry, appearance, ...)`; retain chunking,
   pair spacer, row weights, and the caller's window layout exactly.
3. In `DailyWindow`, replace each private entry row with
   `DailyForecastRow(entry, appearance, ...)`; retain the enclosing
   `MonitorSection`, column padding, row weights, and window layout exactly.
4. Delete only the superseded `CompactFactPanel` implementation and imports
   made unused by the extraction. Keep `HourlyWindow`, `DailyWindow`,
   `MetricGroup`, page functions, `OxygenWeatherApp(presentation, effects)`,
   and all application/page/window state in place.

No separate page migration is implied by these direct call-site replacements:
the page hierarchy and rendered composition must remain equivalent before and
after the extraction.

## Functional invariants

- Page order remains `Now -> Hourly -> Daily -> Details`; `OxygenWeatherApp`
  remains source-compatible.
- Exactly one outer `HorizontalPager` remains the sole global horizontal-swipe
  owner. No nested pager, horizontal scroll, new gesture, or destination is
  introduced.
- Hourly and Daily retain their visible Earlier/Later controls, selected-window
  behavior, Hourly date jumps, and Android Back behavior. The components do
  not own callbacks or mutate page/window state.
- Hourly entries remain supplied, chronological values in their existing
  windows; daily entries remain supplied, chronological values in their
  existing windows. No padding, repetition, interpolation, sorting, or
  fabricated forecast fact is introduced.
- Label, condition, time/day, temperature, low/high, precipitation wording,
  condition identity, unit text, unavailable text, source/update text, and
  spoken summary remain the supplied presentation values. Rendering does not
  parse, format, calculate, or infer a weather value.
- `MetricTile` omits a null supporting line. It never replaces missing data
  with zero, an empty plausible value, or a synthetic supporting string.
- Important entry facts remain visible text. Hourly and daily entries retain
  their exact concise `spokenSummary` semantics; weather marks remain
  supplemental and conditional on the supplied identity.
- Components receive presentation inputs and `ResolvedAppearance` only. They
  contain no raw-theme branch or provider/data/repository/persistence import.
- Theme B typography, spacing, mark sizes, surface treatment, opacity, and
  Effects Off behavior remain unchanged. Effects Off remains opaque, static,
  and complete; Subtle retains its current effect behavior.
- Existing 48dp guidance for controls and current accessibility semantics are
  not weakened. This slice adds no interactive component.

## Implementation steps

1. **Record and inspect the baseline.** Create
   `.codex/test-artifacts/010-theme-b-forecast-monitor-components/` and run:

   ```sh
   python scripts/dev.py workflow
   python scripts/dev.py contract
   python scripts/dev.py test
   git diff --check
   ```

   Preserve output and record the existing call sites, dimensions, visible
   strings, and semantics noted in the component contracts. Do not reset or
   fold unrelated worktree changes into this cycle.

2. **Add the three shared primitives.** Implement the fixed contracts in
   `MonitorComponents.kt` by moving—not redesigning—the corresponding private
   rendering. Preserve concrete spacing, typography, `TextOverflow`, semantic
   boundaries, `MonitorSection` ownership, and condition-mark behavior. Use
   `appearance.layout` for existing layout values where it is already used;
   do not expand `ResolvedAppearance` solely for this extraction.

3. **Perform only direct call-site migration.** Make the three replacements
   described above, remove only superseded private implementation/imports, and
   inspect the result for one `HorizontalPager(`, unchanged Home page order,
   Back handler, page/window state, and mapper/data flow.

4. **Run the focused regression boundary.** Do not add a Compose test
   dependency, `androidTest` harness, or source-string wrapper test. The
   components are stateless renderers and this repository has no existing
   Compose UI-test boundary. Run `python scripts/dev.py contract` and
   `python scripts/dev.py test`; the latter must retain the existing
   `HomePresentationTest` coverage for six-hour/five-day windows, date jumps,
   actual partial/missing forecast strings, null weather marks, and honest
   unavailable values. Add or amend a deterministic presentation assertion
   only if the completed implementation changes a pure presentation contract;
   do not create a helper or a test merely to make a Compose wrapper testable.

5. **Update documentation from implemented facts only.** After the extraction
   and before closure:

   - update `docs/ARCHITECTURE.md` to name `MetricTile`,
     `HourlyForecastTile`, and `DailyForecastRow` alongside the existing
     shared structural components, state their presentation-only inputs, and
     preserve the statement that page/pager/window state lives in
     `OxygenWeatherApp`;
   - update the `Current candidate scope` in
     `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` to record the three forecast
     components as implemented and to keep page-specific Theme B application
     as future work;
   - append a distinct R0.6A entry to `VERIFICATION.md` with exact command
     outcomes, installed observations, artifact path, and unverified limits;
   - at closure only, change R0.6A in `docs/ROADMAP.md` from `PLANNED` to
     `DONE` and cite the history record; and
   - do not modify `docs/SPECIFICATION.md`, `docs/UI_DEVELOPMENT_WORKFLOW.md`,
     the roadmap scope of dependent slices, or product requirements.

6. **Verify installed rendering where available.** Build/install the current
   app and retain screenshots, UI hierarchy dumps, command output, and a
   concise `verification-notes.md` under the cycle artifact path. On the
   established 360x640 compact viewport, for normal Subtle and debug Effects
   Off launches, capture and inspect:

   - Now with precipitation and wind metric tiles;
   - Hourly first window, Later window, and a date-jump target with representative
     forecast-entry summary semantics;
   - Daily first and Later windows with representative row summary semantics;
   - Details to confirm it has not migrated or changed; and
   - selector navigation and Back from Details through Now, plus Earlier/Later
     enabled-state boundaries.

   Repeat the affected Now/Hourly/Daily states at font scale 1.3 in both
   effects states. Confirm all supplied text remains reachable/legible with no
   newly clipped critical facts, overlap, or unusable control; restore the
   prior font scale. Inspect Effects Off as solid/opaque/static. Exercise RTL
   and service-level TalkBack only if actually available; otherwise record both
   as unverified.

7. **Close with full evidence.** Run `python scripts/dev.py check` when the
   Android SDK/dependencies are available, then `git diff --check` and inspect
   the complete diff, including documentation, artifacts, and history record.
   Preserve any unavailable command/environment failure verbatim rather than
   claiming the boundary passed. Only then close the active cycle with
   `python scripts/codex_cycle.py close`, providing exact outcome,
   verification, limitations, and the next dependent work.

## Tests and verification boundary

- `python scripts/dev.py workflow` validates the active record before edits and
  at closure.
- `python scripts/dev.py contract` must continue to report the Oxygen identity,
  retired-UI exclusion, exactly one outer pager, and presentation-only Compose
  imports.
- `python scripts/dev.py test` runs the deterministic data, derived,
  presentation, effects, and launch suite. The existing
  `HomePresentationTest.missingWeatherFieldsMapHonestlyWithoutZeroPrecipitationOrConditionMark`
  is the focused regression evidence for representative missing hourly/daily
  values and weather marks; this extraction must not weaken it.
- `python scripts/dev.py check` is the broader closure gate: workflow,
  contract, JVM tests, lint, debug APK assembly, and whitespace diff check
  when the Android toolchain is available.
- Installed screenshots and hierarchy/interaction observations are required
  visual and semantic evidence; compilation or a preview is not a substitute.
- `git diff --check` and full final-diff inspection are required in all
  environments.

## Acceptance criteria

- `MonitorComponents.kt` exposes the three named internal components with the
  contracts above and no data-layer/raw-theme dependency.
- Now uses `MetricTile`; existing `HourlyWindow` and `DailyWindow` use the
  respective forecast components, while their page/window hierarchy and state
  remain unchanged.
- The old private compact metric renderer is removed; no duplicate rendering
  implementation or compatibility alias obscures the component boundary.
- Supplied complete and missing presentation strings, condition marks, and
  hourly/daily spoken summaries are preserved without recomputation or
  fabricated fallback values.
- The source contract and deterministic test suite pass; if the production
  extraction introduces a real pure contract change, focused tests cover it.
- Installed compact and 1.3-font evidence covers the listed Subtle/Off states,
  entry semantics, window boundaries, navigation, and unchanged Details, or
  an exact environment limitation is recorded.
- Effects Off remains opaque/static/complete, and no break in public API,
  pager ownership, navigation, forecast semantics, or data/presentation
  architecture is introduced.
- Required documentation reflects actual implementation and verification; the
  roadmap is marked DONE and a history record is written only at successful
  closure.

## Verification and evidence

Expected artifact directory:

`.codex/test-artifacts/010-theme-b-forecast-monitor-components/`

Retain at minimum:

- baseline and closure command output;
- compact Subtle/Off screenshots for Now, Hourly first/Later/date-jump, Daily
  first/Later, and unchanged Details;
- representative hierarchy XML for hourly and daily entry summaries plus
  controls/selector states;
- 1.3-font Subtle/Off screenshots and notes for affected pages;
- `verification-notes.md` mapping each acceptance criterion to evidence or a
  stated limitation; and
- device serial, viewport, font-scale restoration, launch arguments, and any
  exact unavailable-environment error.

## Risks and assumptions

- This is a visual extraction from an installed baseline, so preserving exact
  existing layout/semantics is more important than component API generality.
- The only current typed inputs are the existing presentation entries and
  formatted current strings. Introducing a general metric data contract would
  enlarge the slice; `MetricTile` therefore accepts those supplied strings.
- The shared daily row intentionally does not own a surface because the daily
  window owns one shared `MonitorSection`; moving that ownership would redesign
  the composition.
- The debug `oxygen_effects_off` launch extra remains the available Off
  verification path; no persisted setting is added.
- Hierarchy dumps can establish exposed semantics but cannot establish
  service-level TalkBack traversal or speech quality.

## Out of scope

- R0.6B Details source/freshness, metric-group, trend, inspection, or chart
  components.
- R0.7–R0.10 page-specific Theme B composition/redesign, visual tuning, new
  action model, or nested navigation.
- Presentation-model/API changes, unit conversion, canonical/provider mapping,
  repository/cache/refresh behavior, location, alerts, or derived meteorology.
- New Compose UI-test dependencies, an Android UI-test harness, snapshot-test
  infrastructure, or source-string tests.
- Theme IDs, high contrast, persisted effects/theme settings, Full effects,
  layout presets, or settings work.
- Fabricated forecast entries/values, zero substitution for missing data, or
  claims of RTL/TalkBack verification without direct evidence.
