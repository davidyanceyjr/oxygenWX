# Plan 171 — Atmospheric Now static effects vertical slice

Status: Planned
Cycle ID: 171-ui6a-atmospheric-now-static-level
Roadmap item: UI.6A / FX.41
Created: 2026-10-11
Revised: 2026-10-11

## Objective

Evaluate the existing Atmospheric Subtle tonal field and role-based soft glow
on the installed Now page against the same page at Effects None. Make the
Now-only None correction required by the Effects contract, then record whether
the existing Subtle field is suitable as the first static Atmospheric
treatment. The observable outcome is the focused implementation and UI checks,
plus paired installed captures and an evidence-backed decision on whether the
vertical slice meets its visual objective.

**Owner decision recorded (2026-10-11):** after reviewing the installed Now
preview, the owner approved evaluating the existing Atmospheric Subtle field
and glow as the complete first treatment for this slice. Do not add a
page-local horizon, new artwork, new source, or new geometry. D31's
page-atmosphere proposal, cycle 169's HTML preview, and renderer constants do
not authorize those additions.

**Owner visual reference:** `feh_045613_000001_Atmospheric-Weather-Pages.png`
shows the intended four-page UI direction (Now, Hourly, Daily, Details),
including the dark teal field, cyan outlined cards, and visible page names.
Use it as visual context when reviewing the installed Now captures. It does
not expand this slice into a four-page redesign or supersede the product and
presentation specifications; the acceptance target remains the existing
Atmospheric Subtle field on Now compared with Effects None.

## Production boundary

Change the Now hero composition in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` so its
decorative condition mark is omitted when effects are OFF. Keep the condition
text and its existing semantics. Add focused installed Compose regression
coverage for this Now behavior.

The existing Subtle field is drawn by `ProductionBackdrop`, which wraps the
app's page content. This cycle evaluates that existing field while Now is
selected; it does not change the shared renderer, resolver, palette, or
preference behavior, and it does not add integrations or changes to other
pages. If implementation investigation shows that the approved Now evaluation
requires changing the shared background for all pages, stop and replan the
production boundary before making that change.

## Functional invariants

- Preserve current weather values, condition identity/text, units, provenance,
  valid/update times, freshness, and missing-data behavior.
- Preserve `Now -> Hourly -> Daily -> Details`, visible page identity, swipe
  and Back navigation, controls, and accessibility meaning.
- At Effects OFF, the condition text and meaning remain available while the
  decorative mark is omitted. At Subtle, the existing decorative mark remains
  non-semantic and the existing field/glow remains static and decorative.
- Preserve the saved effects preference and existing internal `OFF`
  identity; no enum rename or preference migration.
- Preserve request/cache behavior. Appearance changes must not initiate
  forecast, alert, or cache work.
- Do not add fixed effect colors; the existing Subtle field continues to use
  the Atmospheric semantic palette roles.

## Implementation steps

1. **Prepare and capture the baseline.** Reconfirm UI.5 completion and the
   recorded owner decision. Record the source revision/build identity and
   inspect the current Now composition, shared backdrop/resolver path, and
   focused test hooks. On the installed API 37 `oxygen_starter` AVD, use the
   same deterministic Demo Station fixture and capture pre-change Effects
   OFF and Subtle at 360x640dp, font scale 1.0. Also capture the two states at
   font scale 1.3 before changing the None mark. Record selected theme,
   contrast, effects, fixture, viewport, density, font scale, device/API, and
   app build. These are baselines, not acceptance captures.
2. **Correct the Now None state.** Guard the decorative condition mark at
   `NowHero` using the resolved effects state. Do not change condition text,
   temperature, layout hierarchy beyond the removed mark, or mark behavior at
   Subtle. Do not modify `ProductionBackdrop` or global effect resolution.
3. **Add a focused regression.** In the actual `MainActivity`/Compose path,
   verify for Atmospheric Now at OFF that the condition text and temperature
   remain visible and represented in semantics and the decorative mark is
   absent. Verify the mark remains present but decorative at Subtle. Keep
   visible page identity and fixture values unchanged. Assert forecast,
   alert, and cache operation counters do not change when switching OFF and
   Subtle. A test tag or equivalent test-only observation may identify the
   call-site mark; do not give decoration user-facing semantics.
4. **Run focused and regression checks.** Run the new focused Android UI test
   and the existing installed Now appearance-flow test covering the real
   `MainActivity -> OxygenWeatherApp -> ProductionBackdrop` path, weather
   invariance, and request/cache/alert counters. Run relevant existing
   Effects OFF backdrop coverage. Then run `python scripts/dev.py check` when
   the Android SDK/dependencies are available.
5. **Capture the final installed matrix.** Use the same API 37 AVD, real app
   path, and deterministic fixture. Set 360x640dp (945x1680 physical pixels
   at 420 dpi) and capture the following Now states:
   - Standard contrast, LTR, font scales 1.0 and 1.3: OFF and Subtle at each
     scale (four captures).
   - Standard contrast, RTL, font scale 1.0: OFF and Subtle (two captures),
     to cover the hero row/layout direction affected by the mark correction.
   - High contrast, LTR, font scale 1.0: Subtle (one capture), checking text
     and grouping remain readable over the existing field.
   - Animator scale zero, LTR, font scale 1.0: Subtle (one capture), checking
     reduced motion disables motion without rewriting the saved Subtle level
     or removing the static field.
   Confirm Now is selected and record persisted/effective theme, contrast,
   effect, layout, font, locale/direction, animator scale, fixture identity,
   build, AVD, API, display size, and density for each state. Restore AVD
   display/font/animation settings after capture.
6. **Inspect and record evidence.** Compare each OFF/Subtle pair at both font
   scales. Review the temperature/condition hierarchy; supplied condition,
   apparent temperature, supporting measurements, source/update/freshness;
   page identity; semantics; controls; clipping/overlap; static behavior; and
   the visual distinction of the field. Save final PNGs, metadata, test logs,
   request-counter results, comparison notes, scope audit, exact commands,
   and limitations under
   `.codex/test-artifacts/171-ui6a-atmospheric-now-static-level/`. Run
   `git diff --check` and inspect the final diff/changed paths before close.

## Acceptance criteria

- The production diff is limited to the Now condition-mark OFF behavior and
  the focused regression needed to prove it. No shared backdrop, resolver,
  palette, preference, or unrelated page changes.
- The focused installed UI regression proves that OFF omits the Now
  decorative mark while preserving visible/semantic condition text and all
  supplied facts; Subtle retains the mark as decoration. Request/cache/alert
  counters and fixture identity remain unchanged across the comparison.
- The final installed matrix has all eight captures specified in step 5,
  with paired OFF/Subtle captures using the same fixture at both font scales.
  Unsupported states are recorded as unmet, never inferred as passes.
- At compact and large-font conditions, critical facts and controls remain
  readable, visible, and unclipped; Now identity and semantics remain
  available. RTL preserves the facts and their meaning while following layout
  direction. High contrast preserves text/group readability. Reduced motion
  leaves the saved Subtle level intact and retains the static field.
- Installed comparison demonstrates that the existing Subtle field is a
  meaningful, acceptable static treatment on Now, with None opaque, static,
  and complete. If the objective is not demonstrated, record the exact gap,
  do not claim UI.6A passed, and stop before UI.7 or other dependent work
  pending an explicit roadmap update.
- Evidence and exact verification results are retained under
  `.codex/test-artifacts/171-ui6a-atmospheric-now-static-level/`. The closed
  history record states actual verification and all unverified boundaries.

## Verification and evidence

### Focused installed checks

Add a focused test to
`app/src/androidTest/java/com/oxygen/weather/ui/ThemeAppearanceApplicationFlowTest.kt`
(or a narrowly scoped companion test) for the Now OFF/Subtle mark behavior,
fixture/fact preservation, and no forecast/cache/alert work. The test should
capture the eight states in step 5 through the actual `MainActivity` path into
the test app's external-files directory; pull only those named PNGs and test
metadata into this cycle's evidence folder. Suggested focused command:

```sh
ANDROID_HOME="$PWD/.android-sdk" ANDROID_SDK_ROOT="$PWD/.android-sdk" \
  ./gradlew --no-daemon :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#atmosphericNowSubtleAndOffPreserveFactsAndRequests
```

Run the existing real-app installed appearance matrix for backdrop and
request invariance:

```sh
ANDROID_HOME="$PWD/.android-sdk" ANDROID_SDK_ROOT="$PWD/.android-sdk" \
  ./gradlew --no-daemon :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#installedNowAmbientMatrixPreservesForecastAndCapturesActualApplicationPath
```

Run the existing Effects OFF backdrop contract test:

```sh
ANDROID_HOME="$PWD/.android-sdk" ANDROID_SDK_ROOT="$PWD/.android-sdk" \
  ./gradlew --no-daemon :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionBackdropEffectsOffTest
```

### Broader checks and visual artifacts

- Run `python scripts/dev.py check` when SDK/dependencies are available; record
  any unavailable command and why.
- Capture installed states only through the built/installed debug app and the
  real `MainActivity` presentation path. Use the API 37 `oxygen_starter` AVD;
  set `wm size 945x1680` and `wm density 420` for 360x640dp, and set font scale
  and animator scale per matrix. Capture root images from the focused
  instrumentation test and pull the cycle-specific external-files output to
  the host evidence folder. Compose previews, standalone SVG/HTML, and the
  earlier field-only preview are not acceptance evidence.
- Preserve the eight final Now captures, an index with device/build/preferences
  and fixture metadata, focused test output, request/cache/alert counts,
  interaction/semantics review, before/after comparison notes, and scope/diff
  review under
  `.codex/test-artifacts/171-ui6a-atmospheric-now-static-level/`.
- Run `git diff --check`; inspect changed paths and verify no unrelated page,
  renderer, data, provider, or Android resource boundary changed.

## Context budget

Estimated execution use: 40–50% of available context. The production edit and
one focused regression are small; most effort is the eight-state installed
capture/review and evidence recording. No split is expected. If investigation
shows execution will exceed 55%, stop and replan before expanding; 65% remains
the hard ceiling.

## Risks and assumptions

- The installed preview establishes that the existing Atmospheric Subtle
  field is present in the real Now screen and the owner approved evaluating
  it. The final 360x640dp/font-scale comparison is still required to pass the
  visual exit.
- `ProductionBackdrop` is app-level and currently draws the field across
  pages. This cycle evaluates it with Now selected and makes no shared
  renderer change. A need to scope the field to Now would exceed this plan.
- The Now mark currently receives condition identity without an OFF guard.
  The OFF correction must not remove the visible condition text or semantics.
- The captured default emulator state used 411x914dp. Final acceptance must
  use the explicitly configured 360x640dp viewport and record its physical
  size/density; the earlier screenshot is only discussion input.
- The Demo Station is an offline development fixture, not live provider
  verification. No provider, alert-source, network, or live-freshness claim is
  part of this cycle.
- If emulator/device, RTL, high-contrast, reduced-motion, or font-scale states
  fail or cannot be established, record the exact boundary as unmet. Do not
  infer a pass from build success or a screenshot alone.

## Out of scope

- Any theme other than Atmospheric or page other than Now.
- Changing or localizing the existing shared backdrop field, resolver,
  palette, effect recipes, saved preferences, or app-wide effects behavior.
- New horizon geometry, artwork, source references, fixed colors, or
  measurements; motion artwork/lifecycle work or performance profiling.
- Changes to weather values, providers, repositories, cache, provenance,
  forecast/alert requests, navigation semantics, or saved preference format.
- UI.7 evidence review, conditional UI.7A, cross-theme integration, other
  provisional UI.8–UI.17 work, separate navigation/menu intent, release
  hardening, or accessibility-service/TalkBack claims.
