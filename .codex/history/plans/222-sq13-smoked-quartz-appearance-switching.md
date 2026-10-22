# Plan 222 — Reconcile Smoked Quartz appearance switching

Status: Completed
Cycle ID: 222-sq13-smoked-quartz-appearance-switching
Roadmap item: SQ.13
Created: 2026-10-22
Reviewed: 2026-10-22

## Objective

Reconcile the production appearance-selection flow for the Smoked Quartz
replacement theme, persisted as `WeatherThemeId.GLASS`. The theme chooser must
display “Smoked Quartz”; effects must display “None”, “Subtle”, and “Full”;
theme, effects, and contrast selections must resolve and persist across the
Settings/Home route and a target-process stop/relaunch. System reduced motion
must alter only effective motion. Prove appearance actions do not refetch
forecasts or official alerts.

## Production boundary

One appearance-selection boundary: theme/effects labels in the chooser, the
existing theme/effects/contrast preference stores and selection path, and the
typed resolver/system-motion path used by Settings and production Home. Make
only corrections reproduced by the transition checks. Preserve the stored
`WeatherThemeId.GLASS` identity and `glass` preference value. Do not change
other theme presentations or general appearance semantics unless a focused
test demonstrates a shared-path defect required for this exit.

`docs/SPECIFICATION.md` and the effects contract require “None” as the
user-facing label for the existing `OFF`/`off` identity. The effects contract
mentions that this label correction was formerly tracked as FX.09, but the
FX.03–FX.40 sequence is superseded. This plan therefore includes the small
label correction in the existing appearance chooser and makes no enum,
preference, or migration change.

### Confirmed implementation/test owners

- `ThemeCatalog` owns `ThemeDefinition.displayName`; the Glass definition
  currently displays “Glass”. The production appearance screen renders
  catalog theme names.
- `ThemeResolver` resolves theme, contrast, and effects; `ReducedMotionPolicy`
  applies the system animator-scale overlay. `rememberSystemAnimatorScale`
  reads `Settings.Global.ANIMATOR_DURATION_SCALE`.
- `MainActivity` selects and persists theme, contrast, and effects through the
  existing SharedPreferences stores. Their identities remain
  `GLASS`/`glass`, `OFF`/`off`, `STANDARD`/`standard`, and `HIGH`/`high`.
- `ThemeAppearanceApplicationFlowTest` already contains a selected-location
  path with valid Open-Meteo and NWS responses, counters at both transport
  overrides, selected-location clearing, and a restored Demo Station fixture.
  Its existing test also checks appearance and Settings return without
  request/cache changes. Reuse the demonstrated setup after revalidating the
  positive controls and terminal states; do not assume a past test proves
  current seam health.
- The production test hooks expose forecast presentation and alert state
  callbacks. Use `ProductionForecastTestHooks.onPresentationChanged` and
  `ProductionOfficialAlertTestHooks.onStateChanged` to observe terminal
  response states; request callbacks alone are insufficient.

### Appearance state ownership

- Theme, effects, and contrast are app-stored preferences. Read their values
  through the existing SharedPreferences store adapters and capture saved and
  resolved state separately.
- Reduced motion is not an app preference or an Appearance control. Record
  `Settings.Global.ANIMATOR_DURATION_SCALE` readback separately from the saved
  choices and effective resolved `motionStyle`.
- With animator scale zero, effective `motionStyle` must be `OFF`; saved
  theme/effects/contrast and the resolved static appearance level must remain
  unchanged. With a positive scale, effective motion must equal the ordinary
  resolver result. High contrast remains an independent resolver overlay.
- A persistence restart means the target package process is stopped by the
  host and relaunched. Activity recreation alone does not count. Use a
  host-driven two-phase verification: save a known state and exit the first
  instrumentation invocation; force-stop and relaunch the target; then run a
  fresh focused instrumentation invocation to assert restored selected UI and
  store values. Record the target PID before and after the host stop/relaunch.

### Transport measurement contract

- Count forecasts only at `ProductionForecastTestHooks.transportOverride` and
  official alerts only at `ProductionOfficialAlertTestHooks.transportOverride`.
  `ProductionOfficialAlertTestHooks.onRequestFetched` is upstream of alert
  transport and is not an acceptable positive control.
- Before production edits, run the seam preflight on Pixel 3 XL using the
  existing test route: select the deterministic location, require both
  override counters to increment, and wait for the forecast presentation and
  official-alert terminal state. Record response status, terminal state, and
  each counter. The existing cycle 159 evidence and current
  `ThemeAppearanceApplicationFlowTest.persistedAppearanceChoicesAndSettingsReturnRetainHomeWindowWithoutSensitiveRequests`
  implementation are references for setup, not substitutes for this current
  preflight.
- If either transport counter fails to increment or either injected response
  fails to reach terminal state, stop the no-refetch acceptance attempt. Record
  the exact failed seam and close this bounded cycle without a no-refetch
  claim; do not substitute callbacks, UI state, or inferred request counts.
- After successful positive controls, clear the selected location, recreate
  the Activity, wait for the empty selection and Demo Station fixture, wait for
  UI idle, and record forecast/alert transport plus cache read/write baselines.
- Compare both transport counts before and after every measured appearance
  action, Settings/Home return, and process relaunch. Require zero deltas.
  Record cache reads/writes separately; they are not transport counts.

## Functional invariants

- Preserve the four Home pages, visible page identity, navigation, weather
  facts, controls, semantics, provenance, and weather meaning.
- Preserve all stored preference identities and values, especially
  `WeatherThemeId.GLASS` and effects `OFF`/`off`. Renaming labels must not
  rewrite a saved choice or require a migration.
- Saved settings and effective settings remain distinct when the system
  motion overlay applies. Appearance selection and rendering must not rewrite
  saved theme/effects/contrast; there is no saved reduced-motion preference.
- Smoked Quartz None remains opaque, static, and complete. Full, Subtle, and
  High contrast retain their accepted SQ.9A/SQ.11/SQ.12 behavior.
- Appearance does not change forecast/alert values, chronology, provenance,
  cache contents, forecast or alert transport counts, or request behavior.
- Settings return and process relaunch restore theme, contrast, and effects.
  Effective motion follows the current system animator-scale readback and is
  not treated as persisted app state.

## Implementation steps

1. **Baseline and seam preflight.** Inspect the SQ.9A, SQ.11, and SQ.12
   accepted evidence, cycle 159's transport-positive-control evidence, and the
   current appearance-flow instrumentation. Trace chooser display names,
   preference writes/reads, Settings return, resolver inputs/outputs, and the
   host-driven restart path. Run the current dual-transport positive controls
   with valid deterministic responses and terminal-state waits before changing
   production code. Record current labels, IDs, saved/resolved values,
   counters, and exact reproduced defects.
2. **Focused resolver and persistence tests.** Extend the directly relevant
   tests (`ThemeCatalogTest`, `ThemeResolverTest`,
   `ReducedMotionPolicyTest`, theme/contrast/effects selection tests, and the
   three SharedPreferences store tests) only where coverage is missing. Cover
   Smoked Quartz Full/Subtle/None × Standard/High contrast, verify ordinary
   resolver motion and animator scale zero/positive, and prove zero scale
   changes only effective motion. Assert `GLASS`/`glass` and `OFF`/`off`
   storage identity and all three preference stores' read/write behavior.
3. **Smallest production corrections.** Correct only reproduced resolver,
   preference, or Settings defects; update the theme name to “Smoked Quartz”
   and effects label to “None”. Add a focused display-label assertion against
   both the catalog and the rendered Appearance chooser. Do not alter other
   theme presentation or accepted Smoked Quartz visuals without a reproduced
   defect in this selection flow.
4. **Production transition instrumentation.** Add
   `smokedQuartzAppearanceTransitionMatrixUsesProductionStoresAndDualTransportControls`
   in `SmokedQuartzAppearanceCycleTest`. Use the real persisted
   theme/effects/contrast stores. On the installed production Activity,
   exercise all six combinations of three effects levels × two contrast
   levels while selecting Smoked Quartz. For every transition, assert chooser
   label, selected control, store readback, resolved appearance/motion,
   unchanged Demo Station semantic snapshot, and zero forecast/alert transport
   deltas. Keep cache reads/writes in separate assertions and output.
5. **Reduced motion and restart.** Add a separate
   `smokedQuartzAppearanceRestoresAfterHostProcessRestart` assertion method
   in `SmokedQuartzAppearanceProcessRestartTest`, paired with
   `prepareSmokedQuartzAppearanceForHostProcessRestart` as the first phase.
   With a positive animator scale, capture the
   normal effective motion for a saved Full state; set scale to zero and prove
   effective motion becomes `OFF` while the saved Full/contrast/theme and
   static appearance level remain unchanged. Restore the original system
   animator scale in `finally`, record readbacks, and report zero-scale as
   unavailable if the device/setting does not permit it. Verify Settings/Home
   return. For process restart, save a deterministic final state
   (`GLASS` + `HIGH` + `OFF`), exit the first instrumentation invocation,
   host-force-stop and relaunch the target package, then run a fresh focused
   assertion invocation that verifies the restored chooser, Home, preference
   readbacks, and effective motion at the then-current system scale. Activity
   recreation may be recorded separately but cannot replace this sequence.
6. **Installed visual checks and closeout.** On Pixel 3 XL review the actual
   production Activity at 360 × 640 dp, Standard layout, LTR, font scale 1.0;
   include font scale 1.3 and a representative RTL state. Capture the chooser
   with Smoked Quartz selected and Effects None selected, and verify visible
   Home facts/controls/semantics after Settings return and relaunch. Run the
   focused unit and connected-test filters, required build/lint/contract/
   workflow checks, and `git diff --check`. Preserve actual test commands and
   outputs; do not run a full suite or `python scripts/dev.py check` in this
   implementation slice. Rerun workflow after updating the cycle record.

## Acceptance criteria

- The actual Appearance chooser displays “Smoked Quartz” and “None”, while the
  underlying theme/effects selections still read `GLASS`/`glass` and
  `OFF`/`off`; no saved value or schema is migrated.
- Focused resolver coverage passes for Full/Subtle/None × Standard/High
  contrast. System animator scale is tested at zero and positive values where
  available; zero changes effective motion only, and never overwrites saved
  theme/effects/contrast or the static appearance level.
- Installed production switching covers all six effects/contrast states,
  Settings/Home return, and recorded saved/effective values. Important weather
  facts, controls, page identity, and semantics remain present.
- A host-driven target-process stop/relaunch is evidenced by the stop/start
  commands and changed target PID; a fresh assertion verifies restored chooser,
  Home, saved theme/effects/contrast, and effective motion.
- Both forecast and official-alert positive controls increment at their
  respective transport overrides and both responses reach terminal state
  before fixture restoration. From the settled Demo Station baseline, every
  measured appearance action, Settings/Home return, and final process relaunch
  has exactly zero forecast and alert transport deltas. Cache operations are
  reported separately.
- No correction changes the accepted SQ.9A/SQ.11/SQ.12 visual contracts or
  another theme's appearance. Any required shared-path correction has focused
  regression coverage. Failed/unavailable checks are recorded as findings;
  no unrun device, layout, font, RTL, motion, or assistive-technology boundary
  is represented as passed.

## Verification and evidence

Device: Pixel 3 XL for every focused test and installed check. Before use,
disable screen timeout and confirm Developer Options **Stay Awake** is selected.
Use the repository SDK and deterministic Demo Station capture path.

Installed baseline: 360 × 640 dp, Standard layout, LTR, font scale 1.0.
Additional conditions: representative font scale 1.3; representative RTL;
Smoked Quartz None and High contrast; system animator-scale positive and zero
readbacks as supported. Record condition-specific outcomes. TalkBack service
traversal is out of scope and remains unverified.

Focused JVM filters:

```sh
./gradlew --no-daemon :app:testDebugUnitTest \
  --tests com.oxygen.weather.ui.themeengine.ThemeCatalogTest \
  --tests com.oxygen.weather.ui.themeengine.ThemeResolverTest \
  --tests com.oxygen.weather.ui.themeengine.ReducedMotionPolicyTest \
  --tests com.oxygen.weather.application.ThemePreferenceSelectionTest \
  --tests com.oxygen.weather.application.ContrastPreferenceSelectionTest \
  --tests com.oxygen.weather.application.EffectsPreferenceSelectionTest \
  --tests com.oxygen.weather.SharedPreferencesThemePreferenceStoreTest \
  --tests com.oxygen.weather.SharedPreferencesContrastPreferenceStoreTest \
  --tests com.oxygen.weather.SharedPreferencesEffectsPreferenceStoreTest \
  --tests com.oxygen.weather.presentation.AppearanceSemanticSnapshotTest
```

Focused connected tests:

```sh
ANDROID_SERIAL=<pixel3xl-serial> ./gradlew --no-daemon :app:connectedDebugAndroidTest \
  '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.SmokedQuartzAppearanceCycleTest#smokedQuartzAppearanceTransitionMatrixUsesProductionStoresAndDualTransportControls'
```

Run `prepareSmokedQuartzAppearanceForHostProcessRestart` as its own first
instrumentation invocation. Use host-installed APKs and `adb shell am instrument`
for both restart phases: the Gradle connected-test task uninstalls the target
application after each invocation and would erase its SharedPreferences. Build
and install both APKs once, then run the first focused instrumentation command
without uninstalling the target package. If that invocation exits the target
process, host-launch the package once and record that PID before force-stopping
it. Then host-run `adb -s <pixel3xl-serial> shell am force-stop
com.oxygen.weather`, verify `pidof` is empty, launch `adb -s <pixel3xl-serial>
shell am start -W -n com.oxygen.weather/.MainActivity`, record the changed PID,
and run a fresh focused assertion invocation:

```sh
./gradlew --no-daemon :app:assembleDebug :app:assembleDebugAndroidTest
adb -s <pixel3xl-serial> install -r -t app/build/outputs/apk/debug/app-debug.apk
adb -s <pixel3xl-serial> install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s <pixel3xl-serial> shell am instrument -w -r -e class \
  com.oxygen.weather.ui.SmokedQuartzAppearanceProcessRestartTest#prepareSmokedQuartzAppearanceForHostProcessRestart \
  com.oxygen.weather.test/androidx.test.runner.AndroidJUnitRunner
adb -s <pixel3xl-serial> shell am start -W -n com.oxygen.weather/.MainActivity
adb -s <pixel3xl-serial> shell pidof com.oxygen.weather
adb -s <pixel3xl-serial> shell am force-stop com.oxygen.weather
adb -s <pixel3xl-serial> shell pidof com.oxygen.weather
adb -s <pixel3xl-serial> shell am start -W -n com.oxygen.weather/.MainActivity
adb -s <pixel3xl-serial> shell pidof com.oxygen.weather
adb -s <pixel3xl-serial> shell am instrument -w -r -e class \
  com.oxygen.weather.ui.SmokedQuartzAppearanceProcessRestartTest#smokedQuartzAppearanceRestoresAfterHostProcessRestart \
  com.oxygen.weather.test/androidx.test.runner.AndroidJUnitRunner
```

Record exact commands, process IDs, filters, and outcomes. Do not run a full
connected suite or `python scripts/dev.py check` here; full-suite verification
belongs to the separately planned SQ.V session.

Required non-test checks: `python scripts/dev.py build`, applicable
`python scripts/dev.py lint`, `python scripts/dev.py contract`,
`python scripts/dev.py workflow`, and `git diff --check`. Run workflow again
after the final cycle record update.

Evidence directory:
`.codex/test-artifacts/222-sq13-smoked-quartz-appearance-switching/`.
Retain baseline defect notes, seam-preflight response/terminal-state evidence,
state-transition matrix, saved/effective/system-scale readbacks, restart
commands and PIDs, screenshots or recordings, hierarchy/semantics
observations, hashes, focused test outputs, transport/cache counter evidence,
and per-condition PASS/FAIL/UNVERIFIED dispositions. Large artifacts may be
local/untracked; link their exact paths from the cycle history record.

## Risks and assumptions

- SQ.12 is DONE and provides the accepted None visual base. This plan makes no
  visual redesign claim; it verifies the existing Glass selection path.
- Cycle 159 proves that the transport-positive-control pattern worked then.
  Current hook existence is confirmed, but current response terminality still
  requires the pre-edit Pixel 3 XL seam preflight. Failure stops the no-refetch
  claim for this cycle; it does not authorize a weaker measurement method.
- The existing transition instrumentation uses test-owned in-memory stores in
  some methods. The SQ.13 persisted-state path must explicitly use production
  SharedPreferences adapters so Activity recreation and host-driven process
  relaunch test actual saved identities.
- Host-driven force-stop/relaunch is required because an instrumentation
  process cannot serve as proof that it survived killing the target process.
  If the available runner cannot complete the two-phase sequence, record the
  limitation and do not call Activity recreation a restart pass.
- No owner decision remains unresolved: product authority requires the None
  display label, while the obsolete FX sequence does not block this small
  correction within the existing appearance chooser boundary.
- Keep checks to Smoked Quartz appearance transitions. Add a shared-path
  regression case only when a modified shared owner requires it.

## Out of scope

- SQ.14 cross-page interaction acceptance; SQ.15–SQ.17 motion/layout/behavior
  acceptance; SQ.V full suites; static performance measurement.
- Reimplementing Smoked Quartz or changing accepted Full/Subtle/None visuals;
  revisiting SQ.9A/SQ.11/SQ.12 except to verify no regression.
- Renaming `WeatherThemeId.GLASS`, changing stored `glass`/`off` identities,
  preference schema, or migration behavior.
- Changes to other theme presentations, general Settings redesign, unrelated
  navigation, weather semantics, provider/cache logic, or TalkBack service
  acceptance.
