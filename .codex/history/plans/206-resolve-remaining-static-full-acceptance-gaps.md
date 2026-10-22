# Plan 206 — Resolve remaining installed Static Full acceptance gaps

Status: Completed
Cycle ID: 206-resolve-remaining-static-full-acceptance-gaps
Roadmap item: SQ.9A
Created: 2026-10-15

## Objective

Finish the still-unverified SQ.9A Static Full acceptance cells on the physical
Pixel 3 XL. Reuse only verified PASS evidence from cycle 205 when its APK
identity still matches the current candidate. Accept SQ.9A only if every
required matrix row passes; otherwise record the exact remaining blocker and
keep SQ.10–SQ.16 gated.

## Production boundary

Evidence and acceptance only. Review the real installed Glass/Smoked Quartz
Full app, its None/Subtle variants, layout direction, large-font behavior, and
global/page-window navigation. Do not change production or test sources. If an
installed state demonstrates a code defect, record the FAIL and stop for a
separate bounded correction plan.

## Functional invariants

- Preserve weather values, units, condition identity/text, forecast membership
  and chronology, source/provenance, valid/update/freshness context, and honest
  unavailable states.
- Preserve the four named pages (`Now -> Hourly -> Daily -> Details`), the
  outer pager as the only global horizontal-swipe owner, Android Back behavior,
  page chooser, Hourly date/window controls, Daily windows, and semantics.
- Keep weather marks supplemental to adjacent text. Missing and unsupported
  conditions receive no fallback mark; Details has no weather mark.
- Appearance and system motion settings do not refetch weather, change weather
  meaning, reorder forecast entries, or rewrite the saved effects preference.
- None remains static and complete under the Smoked Quartz-specific None
  contract. Subtle excludes Full-only flow-field treatment and art.

## Implementation steps

1. Preflight Pixel 3 XL `crosshatch` serial `8AKY0M32X`, API/model, display
   dimensions/density, font scale, locale/direction, units, theme, contrast,
   effects, reduced-motion/animator state, and Developer Options **Stay Awake**.
   Record the original screen timeout and Stay Awake state, then set timeout
   to 0/disabled and confirm Stay Awake is selected. Restore both original
   values after all device checks and record the readback. Recompute the
   candidate debug APK SHA-256. Cycle 205's candidate was
   `238aeba7325a4022162aee3245fedf660e20132025c4736aa1f1304df6d8f001`.
   Use the connected Pixel 3 XL as the installed rendering target, never an
   emulator; use `DISPLAY=:0` for GUI screenshot inspection when needed.
2. Apply this evidence-reuse gate before changing device settings or capturing
   new states. APK identity is necessary but not sufficient: read the exact
   SHA-256 from cycle 205's `sha256sums.txt`, confirm the connected target is
   Pixel 3 XL serial `8AKY0M32X` / `crosshatch` / API 31, and compare each
   candidate row's recorded configuration and scenario with the criterion it
   would satisfy now. Reuse is row-by-row only when all applicable fields match:
   installed candidate hash; physical device and API; viewport/density; font
   scale; locale and effective direction; unit preset; theme, contrast, and
   effects; fixture/scenario; and the tested interaction or method. Link the
   exact prior artifact and matrix row. A matching hash does not turn a prior
   UNVERIFIED row into evidence, and it does not cover a different state.
   Cycle 205's only eligible candidate-dependent PASS rows are its four
   Full/Standard/LTR/font-1.0 page captures, four Full/High-contrast/LTR/
   font-1.0 captures, four sparse/missing-data captures, supported-mark
   observations, page-chooser observation, and four named focused test results
   in its matrix. Reuse each only if the row's listed configuration and
   scenario also match; cycle 205's large-font captures are explicitly
   UNVERIFIED and cannot satisfy the required scrolled large-font cells. Its
   outer-swipe, Android Back, None, Subtle, RTL, and reduced-motion rows are
   UNVERIFIED and cannot be carried forward. Cycle 204 may supply only the
   three Snow text/no-mark cells, after confirming its APK hash equals the
   current candidate and its device/configuration/scenario match the matrix
   cells. If the APK hash or device identity does not match cycle 205, do not
   carry any cycle 205 or 204 row into the current matrix; recapture/rerun all
   candidate-dependent required rows on the Pixel 3 XL. If a row-specific
   configuration cannot be established, treat that row as not reusable.

   First establish that the APK being reviewed is the installed candidate:
   record a fresh install of the hashed debug APK or hash the APK read back
   from the device. A local-file hash alone does not prove installed identity.
   Cycle 204 and 205 each record candidate SHA-256
   `238aeba7325a4022162aee3245fedf660e20132025c4736aa1f1304df6d8f001`
   in their `sha256sums.txt`. For each eligible prior PASS, record the exact
   prior matrix row, artifact paths, and matching configuration/scenario in
   the new matrix, then mark `PASS (reused)`. For a prior UNVERIFIED row, a
   missing/mismatched field, a different APK, or an unmatched device,
   capture/rerun the required state in this cycle and decide from that new
   evidence. Artifact existence alone cannot establish PASS. The owner chose
   row-by-row reuse of proven matches for this cycle.
3. At compact 360×640 dp, Glass Full / Standard / LTR, font 1.3, capture all
   four pages through the installed app. Include scrolled captures and paired
   hierarchies for Now source/freshness, all six Hourly entries plus date and
   window controls, all visible Daily rows plus window controls, and every
   Details semantic group. Inspect clipping, reachability, and text safety.
4. Establish and prove RTL on the physical Pixel 3 XL before collecting any
   RTL evidence. This device-level proof is distinct from the separate
   `appearanceRemainsUsableWithLargeFontAndRtl` test: that test deliberately
   applies an app-level layout-direction override and cannot establish the
   device RTL acceptance rows. Before changing Force RTL, record the device
   locale and the exact current `settings get global debug.force_rtl` result,
   preserving an unset (`null`) value distinctly from `0`. Enable Android's
   **Force RTL layout direction** Developer Option, read back the setting as
   `1`, and capture the Developer Options toggle visibly enabled. Then
   force-stop and relaunch Oxygen Weather. Save the setting readback, app
   relaunch evidence, and toggle screenshot. These establish the requested
   device setup but do not by themselves prove the running app is RTL.

   While Force RTL remains enabled, run
   `ProductionRtlNavigationTest.installedRtlHomeNavigationAndChronology`
   using the exact focused command in step 8. The test's `@Before` clears
   `LocationSearchTestHooks.layoutDirectionOverrideForTests`, and its
   `assertRootDirection(LayoutDirection.Rtl)` checks the running Home
   semantics; its remaining assertions cover RTL start placement, chronological
   Hourly/Daily descriptions, Details group order, and Back navigation. Only a
   passing result with `debug.force_rtl=1` read back for the same run counts as
   the automated device-driven direction proof. The separate appearance-flow
   test remains required in step 8 for its own acceptance coverage, but its
   app-level RTL override does not replace this proof.

   Still with Force RTL enabled, relaunch if instrumentation closed or changed
   the app state, then capture the installed Full / Standard / font-1.0 app on
   Now, Hourly, Daily, and Details. Pair every capture with a hierarchy and
   independently inspect for mirrored placement, visible page identity,
   reachable facts/controls, earliest-to-latest chronology, and semantic
   Details order. The test result and screenshots are independent required
   proofs; neither substitutes for the other. Keep Force RTL enabled until the
   four captures and RTL test output are saved. Then restore the exact original
   `debug.force_rtl` value and locale, relaunch the app, and record readbacks
   confirming restoration. If the original global setting was unset (`null`),
   delete the temporary global setting rather than replacing it with `0`. If
   Force RTL cannot be enabled/read back, or the focused test fails to assert
   RTL after the single retry below, mark the affected RTL rows UNVERIFIED and
   close without accepting SQ.9A. If RTL is proven but an installed page fails
   its RTL presentation, mark that page FAIL and use the separate
   correction-plan rule. Do not substitute an instrumentation-only direction
   override for device RTL acceptance evidence.

   If `debug.force_rtl=1` is confirmed but the focused test reports an LTR
   production Home direction, force-stop and relaunch the app, confirm the
   setting still reads `1`, and rerun that same focused method once. Save the
   first result at the step-8 path and the retry with an `-attempt2` filename,
   each with its contemporaneous setting readback. If the second run still fails
   to establish RTL, restore original settings and close with all four RTL
   rows UNVERIFIED. Do not continue an open-ended retry loop or substitute
   the appearance-flow test's app-level override. The owner chose this single
   relaunch/test retry for cycle 206.
5. At compact 360×640 dp, Standard contrast / LTR / font 1.0, capture Now,
   Hourly, Daily, and Details in Effects None and Subtle. Inspect the installed
   artwork and marks directly: None follows the opaque Smoked Quartz stone,
   integrated strata/grain exception while omitting separate marks, inclusions,
   glow, overlays, glyphs, and motion; Subtle shows only approved restrained
   static decoration and no Full-only flow field. Confirm each page's facts,
   controls, groups, and semantics remain complete. Pair captures with
   hierarchies or concrete semantics observations.
6. Exercise and record installed interactions: open the page chooser; move
   between pages with outer swipes; use Android Back from Hourly and Daily;
   exercise Hourly date jump and Earlier/Later windows; exercise both Daily
   windows. Capture before/after states and pair them with hierarchy or a
   specific interaction observation. Keep scroll gestures separate from page
   swipes and confirm the visible page name for each artifact.
7. Verify reduced-motion behavior on the installed app with Full still saved:
   apply the supported system reduced-motion/animator-scale setting, record
   effective versus saved effects state, and confirm the screen remains
   complete and static. Restore system animation settings afterward.
8. On Pixel 3 XL serial `8AKY0M32X`, run only the listed focused existing
   instrumentation methods. Use the connected instrumentation runner with
   `ANDROID_SERIAL=8AKY0M32X` and a method-qualified
   `android.testInstrumentationRunnerArguments.class` filter; retain the exact
   command, log, test count, and exit status. RTL command:

   ```sh
   ANDROID_SERIAL=8AKY0M32X ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionRtlNavigationTest#installedRtlHomeNavigationAndChronology'
   ```

   Run the RTL method during step 4 while `debug.force_rtl=1` is set and before
   restoring the original value. Save its output under
   `tests/com.oxygen.weather.ui.ProductionRtlNavigationTest-installedRtlHomeNavigationAndChronology.txt`
   together with the contemporaneous setting readback. Then restore the
   original RTL setting/locale as specified in step 4 before running the other
   methods below. Run the same focused-task form for
   `com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#appearanceRemainsUsableWithLargeFontAndRtl`,
   `#effectsAreSelectablePersistedAndDoNotTouchWeatherOperations`, and
   `#liveSystemMotionOverrideCapsPagerMovementWithoutChangingSavedEffectsOrWeather`.
   The exact method-filtered commands are:

   ```sh
   ANDROID_SERIAL=8AKY0M32X ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#appearanceRemainsUsableWithLargeFontAndRtl'
   ANDROID_SERIAL=8AKY0M32X ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#effectsAreSelectablePersistedAndDoNotTouchWeatherOperations'
   ANDROID_SERIAL=8AKY0M32X ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#liveSystemMotionOverrideCapsPagerMovementWithoutChangingSavedEffectsOrWeather'
   ```

   The `appearanceRemainsUsableWithLargeFontAndRtl` method sets an app-level
   direction override for its own large-font/layout flow. It remains required
   as a separate focused check but cannot be cited as proof for the four
   device-RTL matrix rows. Reuse cycle 205's four focused Glass/sparse tests
   only under the row-by-row reuse gate in step 2; rerun them if their candidate
   hash or tested conditions do not match. If rerunning is required, use these
   focused method filters, each prefixed with `ANDROID_SERIAL=8AKY0M32X`:

   ```sh
   ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionHomeCompositionTest#glassFullNowUsesOpenSupportingFactsAndPreservesSemantics'
   ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionHomeCompositionTest#glassFullHourlyShowsOpenRowsAndPreservesWindowSemanticsAcrossLayouts'
   ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionDailyDetailsCompositionTest#glassFullDailyUsesOpenRowsAndExactWindowsAcrossLayouts'
   ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionDailyDetailsSparseCompositionTest#sparseDailyAndDetailsPreserveUnavailableSupportOmissionAndPartialStatus'
   ```

   Do not run a full suite.
9. Update the matrix with PASS/FAIL/UNVERIFIED for every required SQ.2/SQ.9A
   cell, exact evidence paths, device/build identity, and concise observation.
   Hash retained screenshots, inspect the evidence index, run workflow
   validation and `git diff --check`, then record the acceptance disposition
   in cycle history. Close without acceptance if any required row is FAIL or
   UNVERIFIED.

## Acceptance criteria

- The current candidate identity is recorded. Reused cycle 205 and cycle 204
  rows have exact hash and state matches; no other earlier captures are used
  to satisfy new visual states.
- All four pages have installed compact Full/Standard/LTR evidence at font
  scales 1.0 and 1.3, including scrolled lower content and controls. Evidence
  may be reused only under the exact APK/device/configuration match in step 2;
  otherwise it is recaptured in this cycle.
- All four pages have current installed Full/Standard/RTL/font 1.0 captures
  whose direction is established by the enabled device Force RTL setting,
  confirmed by the focused test's production semantics assertion with no app
  override, and visibly inspected in each installed capture. The original
  setting and locale are restored and verified afterward.
- All four pages have current installed None and Subtle captures that directly
  establish suppression/non-leakage while retaining complete facts, controls,
  and semantic groups.
- Installed page chooser, outer swipe, Android Back, Hourly date/window
  changes, and both Daily windows pass with before/after evidence and focused
  semantics/interaction observations.
- Reduced motion leaves the saved Full preference unchanged and renders a
  complete static screen; no weather request/cache behavior changes.
- Existing PASS rows remain supported by exact installed evidence or focused
  tests, and every required row in the consolidated matrix is PASS. Only then
  may the cycle accept SQ.9A. Any failure or unresolved evidence closes the
  cycle without acceptance and keeps dependent SQ.10–SQ.16 work gated.
- No production or test source changes occur in this cycle.

## Verification and evidence

Evidence root: `.codex/test-artifacts/206-resolve-remaining-static-full-acceptance-gaps/`.

- Keep the Pixel 3 XL screen timeout disabled and Developer Options
  **Stay Awake** selected while testing and capturing. Record both settings.
- Keep the device Force RTL change reversible: record the original global
  setting and locale before the run, then restore and verify those exact values
  even if an RTL test or capture fails.
- Preserve candidate/reuse APK hashes, device configuration, original/restored
  system settings, per-state screenshots and hierarchies, interaction
  observations, focused test output, a consolidated acceptance matrix, and an
  evidence index.
- Keep evidence organized under the cycle root with `device-config.md`,
  `acceptance-matrix.md`, and `evidence-index.md`; use state-named folders for
  `large-font`, `rtl`, `none`, `subtle`, `navigation`, and `reduced-motion`.
  Give each page capture a stable page/state filename with its paired
  hierarchy, and reference the exact paths from its matrix row. Save focused
  instrumentation output under `tests/<fully-qualified-method>.txt` and link
  it from the relevant rows.
- Run only the focused instrumentation cases listed above plus cycle 205's
  exact Glass/sparse tests when its identity reuse gate fails. No full unit or
  connected suite is planned; SQ.V remains the separate full-suite session.
- Run `python scripts/dev.py workflow` and `git diff --check`. Do not run
  `python scripts/dev.py check` in this acceptance slice.
- Use installed screenshots as visual evidence and tests/hierarchies for
  semantic behavior. Do not infer a successful state from a preference label,
  source inspection, or a screenshot with an unconfirmed device direction.

## Risks and assumptions

- Android 12 Force RTL may fail to reach the running Compose activity. The
  focused test's `LayoutDirection.Rtl` assertion with app-level override cleared
  and the installed screenshots are separate required proofs. If either fails,
  do not count the RTL cells; retain UNVERIFIED and stop acceptance.
- Large-font content and controls may require vertical scrolling. Capture the
  scroll positions needed to establish complete content instead of treating
  below-viewport content as clipped or complete without evidence.
- If a visual defect is found, this acceptance-only boundary cannot absorb its
  correction. Record exact state, device, screenshot, and behavior for a new
  production plan.
- Current cycle 205 evidence may be reused only after the APK hash and state
  comparison. Any candidate mismatch requires current installed recapture.

## Out of scope

- Production or test source edits, new fixture/scenario work, renderer changes,
  acceptance-scope changes, or visual redesign.
- Performance measurement (SQ.10), implementation/derivation work for
  Subtle/None (SQ.11–SQ.12), appearance reconciliation (SQ.13), new navigation
  implementation (SQ.14), motion lifecycle/art (SQ.15–SQ.16), Pixel Fold
  performance, or broad theme acceptance.
- Full unit or connected suites (SQ.V), TalkBack service traversal, and use of
  an emulator or another phone in place of the required Pixel 3 XL.
- Waiving a required matrix row or accepting SQ.9A with any FAIL or UNVERIFIED
  criterion.
