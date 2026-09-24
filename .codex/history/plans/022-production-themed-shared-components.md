# Plan 022 — Production themed shared components: core monitor

Status: Completed
Cycle ID: 022-production-themed-shared-components
Roadmap item: R0.11C
Created: 2026-09-22
Revised: 2026-09-22

## Objective and observable outcome

Build the first reusable production Compose component family on the R0.11B
`ResolvedTheme` contract: a section surface, page header and global selector,
current-condition hero, metric tile, hourly entry, daily row, and forecast
window/date controls. An installed debug-only showcase must render these actual
production components from existing typed presentation values, with interaction
and accessibility evidence. The normal four-page app remains on the Theme B
sketch until R0.11D–R0.11G.

Atmospheric is the visual reference for this slice. The components must also
accept any of the five resolved themes without raw theme-ID branches. Effects
Off must make every in-scope component solid, opaque, static, and readable; the
root background renderer belongs to R0.11CA.

## Production boundary

Read `docs/SPECIFICATION.md`, `docs/ROADMAP.md`, `.codex/current.md`, and run
`python scripts/dev.py workflow` before production edits. Use
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
`docs/theme-system/THEME_DESIGN_CONTRACT.md`,
`docs/theme-system/architecture/COMPONENT_CONTRACT.md`, the approved Atmospheric
tokens/reference, and the implemented `ThemeModels.kt`/`ThemeResolver.kt` as
the component contract. Inspect the R0.11C staged source only for candidate
ideas. Record each adopted or rejected idea that affects behavior or tokens in
cycle evidence; do not bulk-copy staged code, its `WeatherTheme.current` wrapper,
unapproved alpha values, alternate hero variants, marks, or resource files.

Production edits are confined to new files under
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/`. A narrowly
needed helper under `ui/themeengine/` is allowed only if it applies existing
resolved roles without changing resolver inputs or catalog definitions. Keep
each public component's signature explicit: `ResolvedTheme`, the existing
presentation model or supplied display text, semantic callbacks where
applicable, and an optional `Modifier`. No runtime theme provider,
`CompositionLocal`, app-wide Material wrapper, or new presentation type is
needed. Component-private helpers may share typography, surface, and semantic
behavior; avoid a second general UI framework.

Test-only edits may add a small debug Activity and debug manifest entry under
`app/src/debug/` plus a focused stdlib Python/adb verification script under
`scripts/verification/`. The debug Activity must have no launcher filter, take
no network/provider input, and leave `MainActivity` and normal launch behavior
unchanged. It uses `DemoWeatherRepository` and `HomePresentationMapper` only to
prepare fixture presentation at the debug host boundary; production components
receive the resulting typed values. Do not add an Android test framework,
Gradle dependency, or production test hook merely for this slice. If the
existing UI test infrastructure changes before implementation, use its
smallest equivalent and record the choice.

Cycle records may change under `.codex/test-artifacts/` and `.codex/history/`.
The authority-document updates named below occur at closure after the actual
implementation is checked.

## Functional invariants

- Components render supplied typed presentation values and resolved appearance.
  They do not read provider DTOs, repositories, cache, persistence, theme IDs,
  system settings, or launch intents. The test host alone prepares a fixture.
- Theme, contrast, layout, and effects change presentation only. They cannot
  change formatted weather facts, unit meaning, forecast order/membership,
  provenance, freshness, missing-data wording, callbacks, or accessibility
  meaning. Do not parse display strings to infer measurements.
- Page selector displays `Now`, `Hourly`, `Daily`, `Details` in supplied order,
  exposes selected state and page identity, and emits the chosen page index.
  The caller owns the outer pager. Components add no horizontal pager/scroll.
- Earlier/Later controls emit the corresponding callbacks only when enabled.
  Hourly date controls display every supplied `DateJumpPresentation`, expose
  represented date and selected state, and emit its `windowIndex`; the caller
  owns window selection. No synthetic dates, padded entries, or index math that
  changes forecast membership enters the components.
- Current temperature and condition remain visually strongest in the hero.
  Apparent temperature, humidity, and dew point are visible as supplied;
  generic metric tiles carry other supplied facts. Hourly and daily entries
  show their actual text fields, including `Unavailable` and optional
  precipitation behavior. Use the entry's `spokenSummary` as its semantic
  summary without manufacturing missing weather meaning.
- Decorative marks are outside this slice. Text and semantics must stand alone.
  Selection, disabled state, and unavailability cannot rely on color alone.
- Targets for page/date/window controls are at least 48dp. At 360x640dp,
  font scales 1.0 and 1.3, and RTL, critical text remains readable and
  reachable by vertical scrolling when needed. RTL must not reverse supplied
  chronological entry order or change Earlier/Later meaning.
- Effects Off uses resolved solid/opaque surface values and no component
  animation. The showcase supplies an opaque canvas. Full app background and
  page completeness are later migration/atmosphere verification boundaries.
- The production components remain unreferenced by `MainActivity`,
  `OxygenWeatherApp`, and existing sketch components during this cycle.

## Implementation steps

1. Inspect the exact fields of `CurrentPresentation`, `HourlyEntryPresentation`,
   `DailyEntryPresentation`, `DateJumpPresentation`, current sketch component
   behavior, and R0.11B geometry/palette/style output. Capture a short
   baseline: current app entry-point references, compact viewport, installed
   tool availability, and any candidate differences that affect this slice.
2. Implement a theme-resolved section surface with padding, corner/border and
   text colors from `ResolvedTheme`. Use its `surfaceStyle` only to choose a
   supported surface treatment; never derive semantic content from style.
   Ensure Effects Off does not apply panel or outline transparency. Set
   Material typography/colors locally or pass resolved values directly so no
   caller must install a new global provider.
3. Implement the page header and selector. Header accepts visible title and
   supporting text. Selector accepts labels, selected index and callback,
   preserves order, and provides visible as well as semantic selection. Keep
   48dp targets and allow labels to remain discernible on the compact viewport
   and at 1.3 font scale.
4. Implement the Atmospheric current hero and generic metric tile. Hero uses
   `CurrentPresentation` and the resolved primary/secondary data roles; use
   availability-aware supplied strings, never numeric assumptions. A hero
   style may vary placement/typography within the approved resolved policy,
   but it must retain the same facts/semantics for every theme. Defer
   Instrument gauges, Terminal glyphs, and decorative marks to their
   appropriately scoped slices.
5. Implement hourly and daily entry components from their typed presentation
   entries. Keep time/date, condition, temperature/low/high, and precipitation
   visible. Omit nullable hourly precipitation; preserve the daily supplied
   unavailable wording. Lay out long condition and unavailable text without
   one-line truncation of critical facts. Keep entry spoken summaries intact.
6. Implement Earlier/Later and Hourly date controls as stateless components.
   Accept supplied enabled/selected state; do not compute windows or mutate
   entries. Date controls remain visible, vertically wrappable if necessary,
   and expose date and selected semantics.
7. Add the debug-only installed showcase with deterministic fixture states:
   complete, sparse/unavailable, long text, selector/window callbacks, and
   Atmospheric Subtle/Off. Its visible controls may switch these test states;
   no test switch is present in normal app UI. Use the actual production
   components and presentation mapper output rather than duplicate drawing or
   hand-written plausible forecast values.
8. Add focused automated assertions and installed captures below. Iterate on
   layout from captured output, then run broader repository checks. Inspect
   each changed file and the final diff. Update named docs to the verified
   state and close the cycle only after the evidence and limitations are
   recorded.

## Verification and evidence

Use the debug host and a focused adb/UI-hierarchy script for assertions; the
repository currently has JVM tests but no `app/src/androidTest`, Compose UI
test dependency, or instrumented runner. Keep the script deterministic and
fail on missing nodes, wrong callbacks, or undersized targets. It must cover:

- Four named page targets in order, exactly one selected target, selected
  semantics, and the emitted index for each target. The host exposes the
  callback result for inspection; it does not implement an extra pager.
- Earlier/Later enabled and disabled behavior, plus a date jump to its
  supplied `windowIndex`. Verify selected date semantics and no callback from
  a disabled control. Assert controls' bounds are at least 48dp using recorded
  device density; inspect any merged semantics that obscure the target.
- Visible current, metric, hourly, and daily facts against the exact typed
  fixture output. Include missing condition/temperature, absent hourly
  precipitation, daily unavailable precipitation, short horizon, and long
  location/condition text. Confirm no zero, fake mark, repeated row, or
  invented forecast entry appears.
- Entry semantic summaries equal supplied `spokenSummary`; decorative
  content contributes no required weather information. Page/date/window
  controls remain meaningfully named in the hierarchy.
- Re-resolve the same fixture with Atmospheric Subtle and Off, High contrast,
  and Simple layout in the host; assert visible weather text and callback
  meaning are unchanged. Spot-check one additional catalog theme to catch
  accidental raw-theme assumptions; full alternate-theme visual acceptance
  belongs to R0.11F.
- Read-only source check that the normal app entry point and sketch composition
  have no production component references and that no horizontal pager or
  weather-string parsing was introduced in the new component files. Existing
  presentation/resolver JVM tests must continue to pass.

Installed visual evidence is required when the Android environment works.
Build/install the debug APK and explicitly launch the debug host. Record APK
identity, device serial, 360x640dp/density, font scale, layout direction,
resolved theme/contrast/layout/effects, and fixture state. Capture the core
components at font scale 1.0 and 1.3 for Atmospheric Subtle and Off; capture
RTL at 1.3 and a long/sparse state. Inspect scrolling, clipping, overlap,
text contrast, selection visibility, and Effects Off surface opacity/static
behavior. Save screenshots and hierarchy dumps; restore altered device
settings. Use screenshots for presentation findings and hierarchy/callback
results for functional findings. Do not call the normal four-page production
renderer visually verified by this isolated host.

Run the focused adb script while iterating, `python scripts/dev.py test`, and
`python scripts/dev.py check` when SDK/dependencies are available. Also run
`python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
`git diff --check`. `check` does not install or run the adb script, so record
those outcomes separately. If a device or dependency is unavailable, retain
the exact failed command/reason and leave the affected installed/automated
criterion explicitly unverified; compilation cannot replace it.

## Documentation and cycle records required at closure

- `docs/ARCHITECTURE.md`: identify the new additive core component files and
  explicit `ResolvedTheme`/presentation/callback boundary; describe the
  debug-only host separately from the normal Theme B app path. Keep R0.11CA
  background/Details/marks and page migration as future work.
- `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`: update only the implementation
  status paragraph so it accurately distinguishes the historical Theme B
  sketch, the implemented production core components, and the still-unmigrated
  pages. Do not revise product meaning or visual authority.
- `docs/ROADMAP.md`: mark R0.11C DONE only after the actual acceptance
  evidence is recorded, summarize the implemented boundary and limitations,
  link its history record, and move the next active recommendation to R0.11CA.
  Do not mark R0.11CA or any page migration complete.
- `.codex/test-artifacts/022-production-themed-shared-components/`: retain a
  concise candidate-decision note, exact commands/results, host fixture and
  device configuration, callback/semantics/bounds output, screenshot names,
  and visual observations. Large output stays in files, not chat.
- `.codex/history/<date>-022-production-themed-shared-components.md`: record
  files changed, actual tests and installed conditions, any failures or
  unverified boundary, unchanged normal launch path, and R0.11CA follow-up.
  Close with the cycle script after doc/status and evidence review.

No change is needed to `docs/SPECIFICATION.md`, approved theme tokens,
`docs/theme-system/THEME_DESIGN_CONTRACT.md`, or the component contract unless
implementation exposes a real authority mismatch. Resolve and document such a
mismatch before broadening production behavior.

## Acceptance criteria

- Every in-scope component exists, compiles, and uses only existing typed
  presentation input, resolved semantic appearance, and callbacks. The Hourly
  date selector is included; Details panels, marks, and root backgrounds are
  not silently deferred into this cycle's claims.
- Installed host and focused assertions establish the exact visible facts,
  entry summaries, callbacks, selected/disabled semantics, 48dp targets, and
  sparse behavior listed above. If unavailable, closure records the precise
  missing evidence and does not claim that criterion passed.
- Compact 1.0/1.3, RTL, and Effects Off captures show readable, reachable
  content for Atmospheric. Off component surfaces are opaque and static on the
  host's solid canvas. Other-theme spot checks confirm contract compatibility,
  without claiming R0.11F visual acceptance.
- `python scripts/dev.py test`, `python scripts/dev.py check`, workflow,
  contract, and `git diff --check` pass where available, with exact exceptions
  in history. The normal app launch, sketch pages, fetch behavior, data and
  presentation semantics remain unchanged.
- Named architecture/UI/roadmap updates and durable cycle evidence match the
  implementation and verified scope. No claim of full Home page or release
  readiness follows from this component slice.

## Risks and assumptions

The current R0.11B resolver supplies all expected semantic roles and 48dp
geometry; inspect before adding a helper. If a required role is absent, record
the need and make only a minimal additive helper inside this boundary. A broad
resolver redesign requires a dependent plan. Debug-host fixture construction
must remain isolated from production components. A local emulator failure is
an evidence limitation, not a reason to wire components into `MainActivity`.

## Out of scope

R0.11CA Details/source panels, weather marks, root/background
renderer; any page migration/cutover; new weather, provenance, alert,
navigation, persistence, provider, cache, unit, or presentation contracts;
runtime theme selection/settings; generated imagery, runtime drawable packs,
gauges/charts, dependency upgrades, and redesign of the current sketch.

## Context budget

Expected work is about 40% of one context window, below the roadmap's 45%
ceiling. Review only relevant candidate sections and concise command output;
keep screenshots, hierarchy dumps, and logs in the cycle artifact directory.
If implementation genuinely exceeds the 45% boundary, split the unfinished
dependent work into a suffixed roadmap plan before expanding scope. Do not
quietly drop a required component, test, or document update to fit the budget.
