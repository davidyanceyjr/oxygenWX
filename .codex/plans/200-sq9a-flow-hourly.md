# Plan 200 — Add Hourly marks and unboxed grouping

Status: Completed
Cycle ID: 200-sq9a-flow-hourly
Roadmap item: SQ.9A-FLOW-HOURLY
Created: 2026-10-15

## Objective and observable outcome

On Glass/Smoked Quartz at Effects Full, place each supported Hourly condition
mark beside its existing visible condition text in both Standard and Simple
layouts. In Standard layout, remove the Full-only data enclosure around the
open forecast rows. Let spacing, hierarchy, and the approved shared neutral
flow field organize the entries. In Simple layout, retain its existing text-
first row structure and divider; it has no enclosing forecast-data surface to
remove. Missing or unsupported identities show no mark.

The installed app on the owner-directed connected Pixel 5 is the visual acceptance path. This carries forward cycle 199's recorded device direction; Pixel 3 XL is not required for this slice. At 360×640dp,
the Hourly date/range and controls remain visible and usable, entry order and
forecast facts stay unchanged, and a mark remains supplemental to the
condition text.

## Roadmap order, dependencies, and authority

- The governing active branch is `docs/UI_CONTEXT_ROADMAP.md`. Its next
  eligible entry is `SQ.9A-FLOW-HOURLY`, after `SQ.9A-FLOW-NOW` completed in
  cycle 199. The branch's detailed sequence and cycle 199 closeout govern;
  the main roadmap's summary naming an earlier next step is stale.
- Dependencies `SQ.9A-FLOW-SOURCE`, `SQ.9A-FLOW-BACKDROP`, and
  `SQ.9A-FLOW-NOW` completed in cycles 197, 198, and 199.
- Follow `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`,
  `docs/UI_DEVELOPMENT_WORKFLOW.md`,
  `resources/themes/smoked-quartz/SOURCE.md`, and the amended SQ.2 source and
  acceptance matrix. Compare with cycle 198's accepted shared-backdrop capture
  and cycle 193's Hourly standard, RTL, sparse-data, and controls-visible
  captures. Treat approved source art as reference only; candidate SVGs are
  not runtime assets in this plan.
- The current Glass mark renderer is `ProductionWeatherMark` in
  `ProductionWeatherVisuals.kt`; it uses the resolved theme's semantic palette
  and clears accessibility semantics. Glass maps CLEAR, PARTLY_CLOUDY, CLOUDY,
  RAIN, and STORM. SNOW and null map to no mark. Reuse those mappings; add no
  weather identity or generic fallback.
- No owner decision is presently required. The adopted source permits small,
  optional marks beside already labeled entries and requires marks to remain
  supplemental.

## Production boundary

Glass/Smoked Quartz Hourly at Effects Full, Standard and Simple layouts:

- `OxygenWeatherApp.kt`: Standard `HourlyPage` grouping and the private
  `SimpleHourlyEntry` presentation.
- `themeengine/components/ProductionMonitorComponents.kt`:
  `ProductionHourlyEntry` open Glass Full row and condition-mark placement.
- Directly affected focused Android UI assertions in
  `ProductionHomeCompositionTest.kt`,
  `ProductionHomeSparseCompositionTest.kt`, and, if a component-level visual
  assertion is needed, `themeengine/components/ProductionForecastComponentsTest.kt`.

No weather presentation model, provider, fixture, shared backdrop recipe,
other page, or non-target theme/effects presentation changes belong in this
cycle. Test-only tags may be added to locate the decorative mark in Compose
assertions; they must not add accessible mark meaning.

## Functional invariants

- Preserve the selected window's actual entries and order; local time,
  condition, temperature/unavailable state, precipitation probability/value,
  and the existing spoken summaries.
- Preserve date selection, range label, Earlier/Later behavior, the sole outer
  Home pager, Back behavior, page identity, provenance, freshness, request/cache
  behavior, and all forecast/alert meaning.
- The condition text remains visible and authoritative. Marks remain
  decorative and semantics-cleared; never use color, a mark, or a background
  to encode a value or condition unavailable from text.
- For missing or unsupported identities, omit the mark without substituting a
  generic glyph or changing the existing text or unavailable state.
- In Standard layout, remove only the Glass Full `ProductionSectionSurface`
  around the forecast grid. The date chooser (`TextButton`, outline, and Glass
  elevated surface) is outside that wrapper and remains functional. The
  Earlier/Later `ProductionWindowControls` are also outside it; retain their
  borders, surfaces, callbacks, disabled states, and 48dp target guidance.
- Preserve the existing Simple layout row structure and divider; add only the
  Glass Full supplemental mark there. Preserve Glass None (`OFF`), Glass
  Subtle, and every other theme/effects presentation. None remains opaque,
  static, and complete.
- Add no animation, horizontal pager, forecast data, request, or cache change.

## Implementation steps

1. Review `HourlyPage` and `SimpleHourlyEntry` in `OxygenWeatherApp.kt`,
   `ProductionHourlyEntry` and `ProductionWindowControls` in
   `ProductionMonitorComponents.kt`, and `ProductionWeatherMark` plus
   `weatherMarkTreatment`/`markStyleSignature` in `ProductionWeatherVisuals.kt`.
   Confirm the exact state branches before editing:
   - Standard Glass Full wraps the grid in `ProductionSectionSurface` tagged
     `glass_full_hourly_group`; the individual `sharedGlassFullRow` entries
     are already open, but currently suppress their condition marks.
   - Simple layout bypasses that wrapper and uses `SimpleHourlyEntry`, which
     currently has text and a divider but no mark.
   - The date chooser and Earlier/Later controls are siblings outside the
     data-only wrapper and are functional controls.
2. Remove the Standard Glass Full forecast-grid wrapper and obsolete
   `glass_full_hourly_group` tag. Keep the `sharedGlassFullRow` layout path so
   the six entries remain open and separated by spacing.
3. For Glass Full only, place a small `ProductionWeatherMark` beside the
   condition text in both the `sharedGlassFullRow` path and Simple Hourly row.
   Use the existing `entry.conditionIdentity`; rely on the existing renderer
   to omit SNOW, null, and any unsupported mapping. Keep marks semantics-cleared
   and preserve the entry's existing spoken summary. Do not change other
   themes/effects or the existing Simple divider.
4. Replace
   `ProductionHomeCompositionTest.glassFullHourlyShowsOpenRowsAndPreservesWindowSemanticsAcrossLayouts`
   with assertions that the data-only group tag is absent; the current range,
   entries, summaries, ordering, date selector, and Earlier/Later controls
   remain. Cover Standard and Simple Glass Full; assert the data group wrapper
   is absent and supplied row summaries stay intact in both layouts. Select
   Simple through Settings > Appearance. Mark visibility and association are verified
   from installed screenshots because row semantics intentionally clear child
   nodes; do not add mark accessibility meaning. The existing mark mapping
   test covers supported CLEAR/PARTLY_CLOUDY/CLOUDY/RAIN/STORM and the SNOW
   and null gaps.
5. Extend
   `ProductionHomeSparseCompositionTest.sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries`
   or add a narrowly focused companion assertion to prove its null hourly
   condition retains supplied text/summary and retains its missing state and supplied spoken summary. Keep the existing
   `ProductionWeatherVisualsTest.glassStaticFullUsesOnlyTheApprovedStaticRecipeAndMarks`
   mapping evidence intact. Add a representative preservation assertion for
   Glass None/Subtle and another theme at Full if the existing app test does
   not already establish that this branch is Glass Full-only.
6. Run only the focused connected UI methods on the owner-directed Pixel 5, build and lint the
   changed sources, install the resulting app, capture the evidence matrix, and
   review the final diff. Record any failed or unavailable criterion; do not
   expand this production boundary implicitly.

## Acceptance criteria

- In Glass/Smoked Quartz Full Standard and Simple layouts, each condition with
  an existing Glass mark mapping shows that mark beside unchanged condition
  text. Snow, null, and other unsupported identities show no mark and retain
  their text/unavailable state.
- Standard layout has no Glass Full data-only enclosure around the forecast
  grid. Its six real entries remain visibly grouped through open layout and
  spacing. Simple layout retains its text-first structure and existing divider.
- Date/range, chronological order, actual horizon, temperatures, precipitation,
  spoken summaries, missing-data behavior, and date/window controls remain
  unchanged and usable. No overlap, clipping, or horizontal overflow appears
  at compact or large-font target states.
- Marks are semantics-cleared and do not replace condition text or alter the
  spoken summary. Focused assertions pass for wrapper removal and unchanged semantics; installed
  captures prove mark presence, omission, and Glass Full-only scoping.
- Glass None/Subtle and representative non-Glass Full presentation remain
  unchanged. Any state/device that cannot be exercised is marked UNVERIFIED.
- Installed evidence on the actual app path supports the visual objective;
  a build or preview alone does not pass this criterion.

## Verification and evidence

Evidence root: `.codex/test-artifacts/200-sq9a-flow-hourly/`.

**Device setup:** use the owner-directed connected Pixel 5 for every slice test and installed slice
check. Before testing, set screen timeout to 0/disabled and confirm **Stay
Awake** is selected in Developer Options. Record device/API, build identity,
actual viewport, font scale, theme/effects, contrast, layout direction,
layout preset, reduced-motion state, timeout, and Stay Awake in
`device-config.md`.

**Focused automated checks:** execute changed Android instrumentation methods
separately with `ANDROID_SERIAL` set to the connected Pixel 5 serial and
Gradle's class/method instrumentation filter. The planned focused methods are:

```text
com.oxygen.weather.ui.ProductionHomeCompositionTest#glassFullHourlyShowsOpenRowsAndPreservesWindowSemanticsAcrossLayouts
com.oxygen.weather.ui.ProductionHomeSparseCompositionTest#sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries
```

Use `:app:connectedDebugAndroidTest` for these method filters. Assertions cover group-wrapper removal; null-condition text and spoken
summary; unchanged row semantics; chronology; date jump; and Earlier/Later.
Installed captures cover mark adjacency, supported/missing conditions, and
preservation outside Glass Full. The installed Demo Station fixture has no
Snow/null Hourly entry; rely on the focused mapping/sparse checks for those
omissions and record their pixel-level installed rendering as UNVERIFIED.

Run the narrowly scoped local JVM guard for the already approved Glass mark
mapping and None/Subtle/non-Glass preservation:

```text
com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest.glassStaticFullUsesOnlyTheApprovedStaticRecipeAndMarks
```

Use `:app:testDebugUnitTest --tests` with this exact method only. It verifies
the supported identities and the existing SNOW/null mapping gaps; it is not a
full unit-suite run.
Capture exact command, serial, and PASS/FAIL output in `focused-tests.txt`.
Do not run the full connected suite in this cycle.

**Installed evidence:** capture screenshots and relevant Compose/Android
hierarchy evidence for these bounded cells on the connected Pixel 5:

- Glass/Smoked Quartz, Full, Standard contrast, LTR, Standard layout,
  360×640dp, font scale 1.0 — target acceptance.
- Same target at font scale 1.3 — inspect entry-to-mark/text association,
  vertical scrolling, control access, clipping, overlap, and horizontal
  overflow.
- Glass Full, Simple layout at 360×640dp/font 1.0 — verify text-first rows,
  supplemental marks, and retained divider.
- Attempt RTL at compact scale. Check mark placement and preserve chronological
  order. If the environment remains LTR, report RTL as UNVERIFIED; cycle 199's
  force-RTL attempt did not change the rendered direction.
- Glass Full High contrast at compact scale and font 1.3; inspect mark/text
  readability. Check reduced motion at compact scale to confirm the static
  result is complete.
- Preservation: Glass None and Subtle, plus one representative non-Glass Full
  Hourly state at compact font 1.0. Confirm marks/grouping have not changed
  outside the target. Do not build a full theme/effects Cartesian matrix.

Retain screenshots, hierarchy captures, a `review-matrix.md` with
PASS/FAIL/UNVERIFIED for every criterion, `focused-tests.txt`, verification
logs, and SHA-256 hashes under the evidence root. Record the actual installed
settings and any unavailable state; do not infer a pass from a screenshot or
from an emulator/another device.

**Boundary checks:** run `python scripts/dev.py build`,
`python scripts/dev.py lint`, `python scripts/dev.py workflow`, and
`git diff --check`; inspect the final diff. Do not run `python scripts/dev.py check`
or full unit/connected suites; those require a separate verification cycle.
Do not claim TalkBack service traversal, performance, or motion acceptance.

## Risks and assumptions

- The Standard Full data-only wrapper is distinct from functional controls;
  source inspection shows the date selector and window controls are siblings
  outside it. Removing the wrong surface would affect navigation or usability.
- `ProductionHourlyEntry` currently suppresses marks for
  `sharedGlassFullRow`, and Simple Hourly currently emits no marks. Both call
  paths need explicit Glass Full handling to meet the roadmap while retaining
  their existing layouts.
- Glass has a defined snow gap. No mark should be invented to make every
  condition appear illustrated. Existing focused mapping assertions establish
  supported identities and gaps.
- Moving marks beside condition text can affect compact and RTL line wrapping;
  the installed target and stress captures are required.
- Cycle 199's RTL attempt rendered LTR. Attempt RTL here and preserve the
  actual result; leave it UNVERIFIED if setup still renders LTR.
- The bounded target, preservation, and overlay captures plus focused device
  tests are a single-page scope. Keep context use near 35–45%; split/replan if
  estimated execution exceeds 55%, and do not activate work expected to exceed
  the 65% hard maximum.

## Out of scope

- Daily marks or grouping, Now, Details, and the later SQ.9A four-page
  acceptance review.
- Changes to shared background/flow recipe, artwork or runtime assets, data
  models, fixtures, provider behavior, weather values/meaning, provenance,
  freshness, chronology, navigation semantics, requests, or cache behavior.
- Changes to functional control surfaces, other themes, Glass None/Subtle,
  animation, motion lifecycle, performance acceptance, or unrelated visual
  and accessibility remediation.
- Full unit/connected suites, `python scripts/dev.py check`, and TalkBack
  service acceptance.
