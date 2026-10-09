# Plan 161 — Cross-theme and layout appearance invariance (R6.4A)

Status: Completed
Cycle ID: 161-cross-theme-layout-appearance-invariance
Roadmap item: R6.4A
Created: 2026-10-09
Evidence: `.codex/test-artifacts/161-cross-theme-layout-appearance-invariance/`

## Objective and observable outcome

Verify the installed production app across the complete R6.4A matrix: 40 Home
cells (five production themes × Standard/Simple layout × Now/Hourly/Daily/
Details) and all seven Settings destinations. Produce exact state readbacks,
screenshot/hierarchy evidence, cell dispositions, and deterministic
appearance-independent state plus transport-request comparisons. The outcome
is a reviewed report establishing whether the supported theme/layout choices
preserve weather values, controls, provenance, and request behavior.

This is an evidence and verification cycle. Production corrections are
permitted only if the matrix demonstrates a concrete contract failure and the
first responsible production owner is supported by evidence. A suspected issue
whose owner cannot be established is recorded for follow-up without a guessed
production edit.

## Authority and dependencies

Follow `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
`docs/UI_DEVELOPMENT_WORKFLOW.md`, and `docs/ROADMAP.md` R6.4A. The current
appearance-independent contract and production request-counter procedure are
documented in the Cycle 158 and 159 plans and histories. R6.4 is complete only
after its typed resolver, production no-refetch, and installed Effects Off /
reduced-motion evidence are considered together. Confirm these records before
using them as predecessor evidence:

- `.codex/history/2026-10-08-158-reduced-motion-appearance-invariance.md`
- `.codex/history/2026-10-09-159-reduced-motion-appearance-flow.md`
- `.codex/history/2026-10-09-160-installed-effects-off-reduced-motion-review.md`

Cycle 156 is the installed compact/large-font capture precedent; Cycle 160 is
the recent production Home capture/readback and disposition precedent. Adapt
their established harnesses and schemas rather than creating a competing
capture framework. Prior evidence does not count as a cell in this cycle's
matrix.

The concrete harness precedent is Cycle 160's `capture_home_matrix.py` and
`validate_home_matrix.py`, Cycle 156's `capture_settings_matrix.py` and
`validate_settings_matrix.py`, and Cycle 159's
`ThemeAppearanceApplicationFlowTest` / `AppearanceSemanticSnapshotTest`.
Extend their schemas and behavior in cycle-local files; preserve predecessor
artifacts unchanged. Use Cycle 160's profile/readback/hash/disposition schema
as the Home manifest base, with layout added to the key. Use Cycle 156's
Settings route and scroll-to-stable conventions. Cycle 158's history date is
2026-10-08, not 2026-10-09.

Resolved state contract: theme and layout are presentation axes only. Across
all five themes and both layouts, the same canonical fixture, units, selected
Home page, selected hourly/daily window, and represented hourly date jump must
produce the same weather facts, unavailable states, provenance/freshness,
control meaning, and navigation state. Layout may change resolved geometry;
it may not change the page model or forecast data. Theme/layout changes and
opening, navigating within, or returning from Settings must retain the opening
Home page and its selected forecast window. This follows the geometry-only
resolver contract in `docs/ARCHITECTURE.md`, the R5.5/R5.5A exits in
`docs/ROADMAP.md`, `ThemeResolver.resolveLayout` in
`app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeResolver.kt`, and
the existing production route-return cases
`appearanceBackAndReturnRestoreEveryOpeningHomePage` and
`settingsRoutesPreserveOpeningPageAndUnitsRemapWithoutWeatherWork` in
`app/src/androidTest/java/com/oxygen/weather/ui/ThemeAppearanceApplicationFlowTest.kt`.

## Production boundary

Verification boundary: the real installed `MainActivity` Home and Settings
paths, persisted five-theme selection, Standard/Simple layout selection, and
the existing fixture-backed presentation and request-counter instrumentation.
The 40 Home cells use the five production themes, both layouts, and all four
named Home pages. Capture each of the seven Settings destinations once through
normal app navigation. No provider, forecast, alert, cache, or fixture
semantics are to be changed to make a cell pass.

The cycle may add capture, validation, or reporting tools only under its
cycle-specific evidence directory. If a qualifying product defect is proven,
first record the affected cell, observed behavior, expected contract, and
first owner. A minimal correction may then be made only within the implicated
appearance/layout/Home/Settings presentation boundary, with a focused
regression assertion and recapture of all affected cells. If the defect
crosses a wider boundary or requires a changed product contract, stop at the
evidence and recommend a separately planned slice.

## Visual objective and installed profile

For every Home cell, verify named page identity; expected fixture facts and
unavailable states; source/provenance and valid/update/freshness context;
page controls and their selected/reachable state; meaningful hierarchy/
accessibility labels; absence of clipping or overlap; and layout-appropriate
composition. Preserve the semantic page order `Now -> Hourly -> Daily ->
Details`, chronological forecast order, and the outer pager as the sole global
horizontal-swipe owner. Simple layout may reduce presentation density but may
not omit required meaning, alter data, or change navigation semantics.

Review the seven Settings destinations for visible destination identity,
reachable content and controls, and truthful selected/persisted state. Changes
to theme or layout must not reset Home selection or cause weather transport.

Use the installed API 37 `oxygen_starter` path at the compact baseline of
360 × 640 dp, font scale 1.0, `en-US`, LTR, Metric units, Standard contrast,
Effects Off, system reduced motion enabled, and the Demo Station development
fixture. Set and read back each requested theme/layout combination. Use the
same baseline for Settings captures and record each destination's actual
readback. Also record device/API, density and display override, activity/root/
window bounds and insets, fixture/location, effective motion, all appearance
selections, selected Home page, and installed APK digest. A requested setting
without observed state readback does not qualify a cell.

Large-font, RTL, other contrast/effects levels, and TalkBack service traversal
remain separate verification boundaries; do not infer their acceptance from
this matrix.

## Functional invariants

- Preserve the four named Home pages, outer-pager swipe ownership, static-tap
  behavior, Back navigation, and visible Hourly/Daily date/window controls.
- Preserve the selected canonical fixture values, units, chronology,
  unavailable states, source/provenance, valid/update times, freshness, and
  separation of forecast, alert, derived, and historical meaning.
- Keep important facts as visible text and meaningful semantics in both
  layouts; decoration cannot carry required weather meaning. Applicable
  interactive targets retain 48dp guidance.
- Theme and layout changes do not refetch weather or official alerts, rewrite
  weather meaning, or change Settings/Home navigation semantics.
- Effects Off remains opaque, static, and complete with system reduced motion
  enabled.

## Implementation steps

1. Inspect the exact precedent scripts and tests named above. In the new
   manifest, use Home keys `<theme-slug>-<layout-slug>-<page-slug>` in fixed
   theme order (`atmospheric`, `glass`, `minimal-oled`, `instrument`,
   `terminal`), layout order (`standard`, `simple`), and page order (`now`,
   `hourly`, `daily`, `details`). Use Settings keys
   `settings-<destination-slug>` for `appearance`, `units`, `locations`,
   `data-sources`, `privacy`, `open-source-licenses`, and `about`. Capture
   Settings at Atmospheric / Standard layout / Standard contrast / Effects Off
   / Metric and use Cycle 156's established normal routes and scroll-to-stable
   pairs. Home cells use the real Home path and require persisted theme and
   layout selected-state readbacks. The cycle-local validator rejects missing,
   duplicate, unexpected, mismatched, or unqualified keys.
2. Build and install on API 37 `oxygen_starter` / `emulator-5554` using
   `python scripts/dev.py build` and `python scripts/dev.py run`; establish the
   specified profile and retain all device/window readbacks. Prove
   the forecast and alert request counters can increment with their existing
   positive controls, then restore the fixture and settle a baseline before
   measuring appearance actions. Keep cache operations separately reported.
3. Drive the production Appearance controls to each of the five themes and
   both layouts.
   Capture one screenshot and matching hierarchy for each of the 40 Home
   combinations, and one screenshot/hierarchy pair for each Settings
   destination. Read back actual theme, layout, page/destination, profile, and
   fixture state for every cell. Add scroll-state pairs when content or a
   control is below the initial viewport. Hash all files and reject missing,
   duplicate, unexpected, mismatched, or unqualified cells in the validator.
4. Extend `AppearanceSemanticSnapshotTest`'s existing typed projection rather
   than creating a second projection. From the same canonical fixture and
   typed presentation state, compare
   appearance-independent values, provenance, unavailable states, controls,
   page/window state, and navigation state across all ten theme × layout
   combinations. Include a noninitial hourly window, the represented-date
   jump selection, and the available daily window state so equality is not
   established only at initial indices. Keep the existing typed contract where
   it covers the case; add only narrow deterministic assertions needed for
   Simple layout or Settings.
5. Measure forecast and official-alert transport deltas for theme/layout
   changes through Appearance and Settings entry/return actions after the
   settled, restored fixture baseline. Keep
   cache operations separate and repeat the independent positive controls.
   A non-sensitive counter, changed fixture, stale asynchronous state, or
   unexplained delta invalidates a no-refetch conclusion. Exercise each layout
   choice and all five theme choices through the persisted production controls;
   after each change and Settings return, read back the persisted selection
   and confirm the opening Home page/window remains selected. Counters must be
   sampled after the UI settles, and any request during the complete action
   interval counts as a nonzero delta.
6. Review all 47 primary cells against the visual objective and invariants.
   Produce a Home contact sheet grouped by theme with Standard and Simple
   adjacent per page, plus a Settings contact sheet, and an evidence-linked disposition for each
   cell, including any below-fold evidence and navigation/action observations.
   Apply the production defect triage above to each suspected mismatch.
7. If a permitted correction is made, add a focused regression assertion,
   rerun its smallest relevant test and the state/counter checks for affected
   actions, then recapture and re-review every affected cell. Otherwise this
   remains an evidence-only cycle.
8. Run the focused validator, typed JVM and production connected
   instrumentation checks (including independent positive controls and
   measured appearance/Settings deltas), broader repository checks, workflow check, and
   diff review below. Record exact results, retained evidence paths, and all
   unverified boundaries in `verification.md`.

## Acceptance criteria

- Exactly 40 valid Home screenshot/hierarchy pairs cover every theme × layout
  × page combination, plus seven valid Settings screenshot/hierarchy pairs.
  Each cell has matching profile, appearance, fixture, page/destination, APK
  identity, file hashes, and reviewed disposition. Required below-fold content
  includes its own evidence.
- At the specified installed profile, no required value, provenance,
  unavailable state, control, page/destination identity, or meaningful
  semantic label is lost, misleading, clipped, or unreachable. Both layouts
  preserve all product and navigation invariants.
- Deterministic checks establish equal appearance-independent weather state,
  provenance, controls, and applicable page/window state across the matrix.
- The typed projection compares canonical fixture facts, mapped visible facts,
  unavailable fields, source/provenance and freshness, and page/window/control
  state for every theme × layout combination, including noninitial hourly and
  daily windows and the selected hourly date jump. Layout may affect resolved
  geometry only; both layouts retain equal weather semantics and
  control/navigation meaning. Production appearance actions and Settings
  entry/return preserve the opening Home page and forecast window.
- Sensitive forecast and alert counters show zero transport delta for settled
  theme/layout/Settings appearance actions; positive controls increment both
  counters independently, and cache effects are reported separately. If
  counters or fixture restoration are untrustworthy, no no-refetch claim is
  made.
- Defect claims include observed cell evidence, expected contract, mismatch,
  and demonstrated first owner before production correction. Every correction
  has a focused regression check and all affected cells are recaptured.
- Any missing emulator, reliable readback, stable fixture, hierarchy,
  transport-counter sensitivity, or capture evidence is reported exactly;
  preserve partial artifacts and do not mark the R6.4A exit as passed.

## Verification and evidence

Use/adapt cycle-local capture and validation scripts. Exact script names and
commands must be recorded after confirming the chosen harness; expected shape:

```sh
python .codex/test-artifacts/161-cross-theme-layout-appearance-invariance/capture_matrix.py
python .codex/test-artifacts/161-cross-theme-layout-appearance-invariance/validate_matrix.py --self-test
python .codex/test-artifacts/161-cross-theme-layout-appearance-invariance/validate_matrix.py
```

The cycle-local capture entry point is `capture_matrix.py`, based on Cycle
160's Home capture script and Cycle 156's Settings route/readback helpers. Its
paired validator is `validate_matrix.py`; `--self-test` follows predecessor
malformed-evidence checks. Add a focused JVM assertion under
`app/src/test/java/com/oxygen/weather/presentation/` only if the existing
typed projection does not already prove Simple-layout equality. Extend the
connected flow beside `ThemeAppearanceApplicationFlowTest` for persisted
theme/layout state, Home selection retention, Settings navigation, and
sensitive forecast/alert counter invariance. Cycle-specific capture tools and
generated evidence stay under this plan's evidence directory.

Review the 47 screenshot/hierarchy pairs and contact sheet(s) manually; retain
the reviewer/date and evidence-linked cell dispositions. Run focused
deterministic JVM/instrumentation checks covering theme/layout state equality
and transport counters, recording exact Gradle task/command. If production is
changed, run the narrowest relevant test while iterating and rerun all
affected capture and state/counter checks.

The focused JVM command is:

```sh
./gradlew --no-daemon :app:testDebugUnitTest \
  --tests com.oxygen.weather.presentation.AppearanceSemanticSnapshotTest \
  --tests com.oxygen.weather.ui.themeengine.ThemeResolverTest
```

The connected instrumentation command uses the existing production flow test
as the counter-sensitive harness, extended with the layout and Settings
actions in this plan:

```sh
./gradlew --no-daemon :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#appearanceUsesActivityOwnerAndRestoresEveryThemeAcrossActivityRecreation
```

Run these with the repository's Android SDK and supported JDK selected, as in
Cycle 159. If the harness is split into a separate test method, record and run
that exact test selector instead; all required assertions and evidence remain
the same.

After matrix review, run:

```sh
python scripts/dev.py contract
python scripts/dev.py check
python scripts/dev.py workflow
git diff --check
```

Preserve under `.codex/test-artifacts/161-cross-theme-layout-appearance-invariance/`:
device/profile and per-cell readbacks; APK digest; exact 47-cell manifest;
screenshots and matching hierarchy files with hashes; any scroll/action
evidence; validator self-test and run output; counter baselines, positive
controls, and deltas; deterministic check results; contact sheets;
evidence-linked dispositions; focused regression results if needed; and
`verification.md` with exact commands, outcomes, and unverified boundaries.
Compilation or previews alone do not satisfy the installed visual objective.

## Risks and assumptions

- The selected harnesses are Cycle 156's Settings route helpers, Cycle 160's
  installed Home capture/validator, and Cycle 159's production flow and typed
  snapshot. If a helper fails against the current app, diagnose and repair the
  cycle-local harness first. If production UI or counter instrumentation
  prevents trustworthy readback, preserve partial evidence and report that
  precise boundary; do not substitute source inspection for installed proof.
- The 47-cell installed matrix, deterministic state proof, and transport
  counter proof are one bounded verification slice. If execution reveals the
  cycle would exceed the roadmap's 65% context budget, stop before expanding
  implementation work and split the remaining obligations into ordered
  partial plans without weakening the roadmap exit.
- Effects Off/reduced motion is selected for a stable opaque baseline; the
  other effects-level behavior remains evidenced by R6.4. Standard contrast
  and Metric units isolate the theme/layout variables under review.
- A visually different theme or simpler composition is not itself a defect;
  judge required meaning, controls, accessibility, and behavior against the
  adopted contract.

## Out of scope

- Production visual redesign or general polish without a demonstrated
  contract failure.
- Large-font, RTL, other contrast/effects levels, or TalkBack service-level
  review; these remain assigned to their own roadmap slices.
- Provider, forecast, alert, cache, location, or fixture redesign; network
  fetching changes; weather-value or provenance changes.
- New Settings destinations, theme families, appearance preferences, or
  product-contract/roadmap changes.
- Claiming R6.5 accessibility evidence closure or any R7 release-hardening
  item.
