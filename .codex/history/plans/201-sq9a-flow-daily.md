# Plan 201 — Add Daily marks and unboxed grouping

Status: Completed
Cycle ID: 201-sq9a-flow-daily
Roadmap item: SQ.9A-FLOW-DAILY
Created: 2026-10-15

## Objective

On Glass/Smoked Quartz at Effects Full, show each supported Daily condition
mark beside its existing visible condition text and remove the data-only
enclosure around Standard Daily forecast rows. The shared neutral flow field,
spacing, and hierarchy organize the page. Simple layout keeps its text-first
row structure and divider while adding the supplemental mark. Installed
Pixel 3 XL evidence must show marks do not replace facts, summaries, or usable
controls.

## Production boundary

Limit production changes to Glass/Smoked Quartz Daily at Effects Full:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`: the Standard
  `DailyPage` data-only group and private `SimpleDailyEntry` condition row.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`:
  `ProductionDailyRow` condition text/mark composition.
- Directly affected assertions in
  `app/src/androidTest/java/com/oxygen/weather/ui/ProductionDailyDetailsCompositionTest.kt`,
  its sparse composition fixture, and component-focused tests only if needed.

Do not change presentation models, weather fixtures, providers, background
recipe, or unrelated page/component behavior.

## Roadmap order and dependencies

- `SQ.9A-FLOW-SOURCE` and `SQ.9A-FLOW-BACKDROP` completed in cycles 197 and
  198; `SQ.9A-FLOW-NOW` completed in cycle 199.
- `SQ.9A-FLOW-HOURLY` completed in cycle 200. Its exact verification and
  limitations are recorded in `.codex/history/2026-10-15-200-sq9a-flow-hourly.md`.
- This is the next dependent outcome. `SQ.9A` four-page acceptance remains
  downstream of this slice and is not included.
- Follow `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`,
  `docs/UI_DEVELOPMENT_WORKFLOW.md`, `resources/themes/smoked-quartz/SOURCE.md`,
  and the approved flow-field source and amended SQ.2 matrix. Compare against
  cycle 193's Daily baseline, cycle 198's accepted background, and cycle 200's
  Hourly treatment. Candidate weather-mark SVGs remain optional references,
  not runtime assets.
- Reuse existing `ProductionWeatherMark` and its approved Glass mapping.
  Unsupported or absent condition identity must produce no mark; add no
  fallback glyph or new weather identity.
- Cycle 200's installed Simple layout flow provides a direct precedent for
  changing layouts through Settings > Appearance and returning to the current
  page without changing its selected forecast window. Follow that flow for
  Daily rather than introducing a test-only navigation path.

## Functional invariants

- Preserve actual day order and window membership, date/range labels, condition
  text, low/high values, precipitation, unavailable values, supplied spoken
  summaries, source/freshness, and partial-horizon status.
- Preserve Earlier/Later behavior and enabled/disabled states, page identity,
  the single outer Home pager, Back behavior, navigation, accessibility
  meaning, provenance, request/cache behavior, and alert/forecast meaning.
- Keep the condition label visible and authoritative. Marks are decorative,
  semantics-cleared supplements beside condition text. Missing or unsupported
  conditions omit the mark without changing text or unavailable state.
- **Owner constraint:** keep marks decorative and semantics-cleared, and
  remove only the Glass Full data enclosure while preserving functional
  window controls. Do not remove, restyle, reparent, or change callbacks,
  enabled states, or target sizes of `ProductionWindowControls`.
- In Standard Glass Full, remove only the data-only `ProductionSectionSurface`
  around Daily rows. The date/range header and `ProductionWindowControls` are
  outside that wrapper; verify this immediately before editing. Keep the
  controls functional and visibly usable with their existing targets and
  surfaces.
- In Simple layout, preserve the text-first structure and existing divider.
  Keep Glass None (`OFF`), Glass Subtle, High contrast, reduced motion, and
  other themes/effects behavior unchanged. None remains opaque, static, and
  complete.
- Add no animation, nested horizontal pager, forecast data, request, or cache
  change.

## Implementation steps

1. Inspect `DailyPage`, `SimpleDailyEntry`, `ProductionDailyRow`, and the
   existing Daily composition and sparse tests. Confirm that Standard Glass
   Full wraps the rows in `glass_full_daily_group`, while the window controls
   are outside the wrapper; confirm Simple layout bypasses the wrapper.
2. Remove only the Standard Glass Full data-only wrapper and obsolete tag,
   retaining the five-entry cap and open row spacing.
3. For Glass Full only, place the existing mapped `ProductionWeatherMark`
   beside the condition text in both Standard and Simple Daily rows. Preserve
   summaries and semantics; the mark must remain decorative and unavailable
   for unsupported/null identity.
4. Update
   `ProductionDailyDetailsCompositionTest.glassFullDailyKeepsOneSharedGroupAndExactWindows`
   to assert the wrapper/tag is absent in Standard and Simple, while supplied
   entries, day order, values, summaries, range, and window membership remain
   correct. Exercise both windows; assert Earlier/Later enabled and disabled
   states and the existing 48dp target guidance. Switch to Simple through
   Settings > Appearance and verify its divider and entry summaries remain.
5. Update
   `ProductionDailyDetailsSparseCompositionTest.sparseDailyAndDetailsPreserveUnavailableSupportOmissionAndPartialStatus`
   so the Daily sparse fixture renders with Glass Full selected. Verify a null
   condition identity omits the mark while its supplied text, unavailable
   low/high/precipitation state, spoken summary, source/freshness, and partial
   status remain unchanged. Keep Details assertions intact.
6. Preserve the focused
   `ProductionWeatherVisualsTest.glassStaticFullUsesOnlyTheApprovedStaticRecipeAndMarks`
   mapping and theme/effects-preservation coverage; add no new identity or
   generic fallback. Marks intentionally clear semantics, so test that the
   supplied row spoken summary remains the sole row description and assess
   mark visibility/adjacency in installed screenshots rather than exposing
   mark meaning to accessibility.
7. Run the exact focused checks below on the Pixel 3 XL, build/lint changed
   Kotlin sources, install and inspect the actual app, retain evidence, then
   inspect the diff and workflow record. If device setup or a required state
   is unavailable, record it as UNVERIFIED rather than inferring acceptance.

## Acceptance criteria

- Standard and Simple Glass Full Daily show a mapped mark beside each
  supported condition label. Null/unsupported identities show no mark and
  retain their supplied text and unavailable state.
- Standard Glass Full has no data-only enclosure around the five-row window;
  row grouping remains clear through spacing and hierarchy. Simple retains its
  text-first layout and divider.
- Day order, dates/range, low/high, precipitation, spoken summaries,
  missing-data behavior, source/freshness, partial status, and window controls
  remain unchanged and usable.
- Focused tests verify ordering, text/semantics, missing condition behavior,
  window navigation, unchanged control targets/states, and wrapper removal.
  Marks remain absent from the accessibility tree; each row retains its exact
  supplied spoken summary. Installed evidence shows mark adjacency and no
  clipping, overlap, or horizontal overflow at target states.
- Glass None/Subtle and a representative non-Glass Full state remain
  unchanged. Any unavailable state is explicitly marked UNVERIFIED.
- The visual objective is accepted from the installed app path; compilation or
  preview alone does not pass this criterion.

## Verification and evidence

Evidence root: `.codex/test-artifacts/201-sq9a-flow-daily/`.

Use the repository's Pixel 3 XL for every slice test and installed check. Set
screen timeout to 0/disabled and confirm **Stay Awake** in Developer Options;
record device/API, build identity, viewport, font scale, theme, effects,
contrast, layout, direction, reduced-motion state, timeout, and Stay Awake in
`device-config.md`.

**Execution note (2026-10-15):** The owner directed use of the connected
Pixel 5 instead of an emulator. Pixel 3 XL was not connected, so this slice
used Pixel 5/API 34 for focused connected tests and installed evidence. This
does not satisfy the plan's Pixel 3 XL-specific device condition; retain that
boundary as UNVERIFIED in the cycle evidence and history.

Run these focused checks and retain their exact output and device serial in
`focused-tests.txt`:

```sh
ANDROID_SERIAL=<pixel-3-xl-serial> ./gradlew --no-daemon :app:connectedDebugAndroidTest \
  '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionDailyDetailsCompositionTest#glassFullDailyUsesOpenRowsAndExactWindowsAcrossLayouts'

ANDROID_SERIAL=<pixel-3-xl-serial> ./gradlew --no-daemon :app:connectedDebugAndroidTest \
  '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionDailyDetailsSparseCompositionTest#sparseDailyAndDetailsPreserveUnavailableSupportOmissionAndPartialStatus'

./gradlew --no-daemon :app:testDebugUnitTest \
  --tests 'com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest.glassStaticFullUsesOnlyTheApprovedStaticRecipeAndMarks'
```

Select the Pixel 3 XL for both connected methods; the unit test is a local JVM
check. Do not run the full unit or connected suite; full-suite work requires
its own planned verification cycle and all registered devices.

Installed evidence on Pixel 3 XL should include:

- Glass Full, Standard contrast, Standard layout, LTR at 360×640dp/font 1.0;
- the same state at font scale 1.3, inspecting condition/mark association,
  scrolling, Earlier/Later controls, clipping, and horizontal overflow;
- Glass Full Simple layout at 360×640dp/font 1.0;
- an RTL attempt at compact scale; Glass Full High contrast at compact scale
  and font 1.3; and reduced-motion Full at compact scale. Record actual
  settings and any environment limitation exactly;
- preservation captures for Glass None and Subtle and one representative
  non-Glass Full Daily state at compact scale.

Review the Standard Full controls in both first and later windows: the
date/range context remains present, Earlier/Later callbacks still move exactly
one five-entry window, disabled states remain correct at the bounds, and
visible touch targets remain at least 48dp. `ProductionWeatherMark` uses
`Modifier.clearAndSetSemantics { }`; do not add a test tag/content description
that exposes condition meaning. A null-condition sparse Compose test covers
omission when the installed Demo Station state has no null identity. Mark
pixel-level null omission UNVERIFIED if no corresponding installed fixture is
available.

Retain screenshots, hierarchy evidence, `review-matrix.md` with
PASS/FAIL/UNVERIFIED per criterion, `focused-tests.txt`, exact build/lint and
workflow logs, and SHA-256 hashes. Run `python scripts/dev.py build`,
`python scripts/dev.py lint`, `python scripts/dev.py workflow`, and
`git diff --check`; inspect the final diff. Do not run
`python scripts/dev.py check` or a full suite in this implementation cycle.
Do not claim TalkBack traversal, performance, or motion acceptance.

## Risks and assumptions

- Removing the wrong surface could alter functional window controls; source
  inspection indicates controls are outside the data-only wrapper and this
  must be confirmed before changes.
- Adding marks beside condition text can affect wrapping at compact width or
  large font. Installed evidence is required, with RTL reported from the actual
  rendered state.
- Glass has existing gaps in condition-mark coverage. Preserve those gaps;
  do not add a generic fallback to make every condition illustrated.
- This is one page and two row variants, estimated at 35–45% context. If
  implementation evidence shows work would exceed the 55% split threshold,
  stop and replan before expanding scope; 65% is the hard maximum.

## Out of scope

- Hourly, Now, Details, the later SQ.9A four-page acceptance review, or
  unrelated page/layout redesign.
- Changes to shared background, runtime artwork/assets, theme palette,
  presentation models, forecast/provider data, values, provenance, freshness,
  chronology, navigation, requests, cache, or Settings.
- Functional control surface redesign, other themes, Glass None/Subtle,
  animation, motion lifecycle, performance acceptance, or full-suite testing.
