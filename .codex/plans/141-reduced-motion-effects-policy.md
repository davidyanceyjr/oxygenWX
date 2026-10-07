# Plan 141 — Reduced-motion effects policy

Status: Completed
Cycle ID: 141-reduced-motion-effects-policy
Roadmap item: R5.4A
Created: 2026-10-07
Reviewed: 2026-10-07

## Objective and observable outcome

Integrate Android's disabled-animation policy into the effective appearance used
by the running Compose UI. When the system animator duration scale is zero, the
resolved theme reports `MotionStyle.OFF` and programmatic Home page movement is
immediate. The saved Off/Subtle/Full choice remains visible and unchanged. When
the scale becomes nonzero while the app is open, the theme's saved motion policy
resumes without Activity recreation, a weather request, or cache mutation.

## Resolved platform decision

Observe `Settings.Global.ANIMATOR_DURATION_SCALE` through a scoped Android
`ContentObserver` at the app composition boundary. Treat `scale == 0f` as system
motion disabled and any finite value greater than zero as enabled. This is a
binary cap; Oxygen does not reinterpret nonzero developer animation multipliers
as preference levels.

The repository uses Compose UI 1.12.1. Its Android implementation reads
`Settings.Global.ANIMATOR_DURATION_SCALE` through a `ContentObserver` and
exposes it as `MotionDurationScale`. However, the connected production-adapter
test changed the global value from 1 to 0 while the Activity remained open and
the value read from `rememberCoroutineScope().coroutineContext[MotionDurationScale]`
remained 1. The failed result is preserved in
`.codex/test-artifacts/141-reduced-motion-effects-policy/diagnostics/`.
Use a scoped observer at Oxygen's app boundary so setting changes reliably
recompose the resolved motion policy. Android API 17 supports the setting and
observer, below Oxygen's API 26 minimum. Keep deterministic scale injection at
the same boundary for focused connected tests.

**Live-signal contract (resolvable unknown):** `OxygenWeatherApp` is hosted
by `MainActivity.setContent` (`MainActivity.kt`), so sampling the scale
only at Activity creation would miss an open-Activity change. Read the
observer value as Compose snapshot state, and make the test override observable
Compose state at that same boundary. A scale change
must recompose the effective theme and navigation policy while preserving the
same Activity instance, `PagerState`, saved preferences, and weather state.
For a navigation callback invoked after the updated composition, use its new
effective policy; a navigation already in flight need not be retroactively
restarted. The connected test must demonstrate the sequence enabled -> disabled
-> enabled in one Activity instance, with a synchronization point after each
state change before exercising navigation. The installed check repeats that
sequence using the actual system setting, which independently proves the
production adapter observes Android's live signal.

**Programmatic-navigation contract (resolvable unknown):**
`OxygenWeatherApp.kt` currently has five `PagerState.moveToPage` call sites:
Home Back, Appearance Back, Search selection return, Appearance Return control,
and the named Home page menu. Route all five through the same effective
`ResolvedTheme.motionStyle` from the current composition. `MotionStyle.OFF`
chooses `scrollToPage`; `SUBTLE` and `FULL` choose `animateScrollToPage`.
Do not decide from the saved `effects` argument, and do not create a second
independent scale decision in any callback. Search dismissal and alert route
Back currently do not move the pager; preserve that behavior. User drag remains
under the outer pager's direct manipulation. For a theme whose resolver already
returns `MotionStyle.OFF` at a positive scale, keep snapping; restoring scale
does not manufacture motion the theme does not support.

Do not use `AccessibilityManager`, touch exploration, or an enabled
accessibility service as a reduced-motion proxy. Do not combine window or
transition animation scale with animator scale: `ANIMATOR_DURATION_SCALE` is
the authority for in-app Compose motion.

If the element is unexpectedly absent, retain enabled motion so an unsupported
composition does not silently override the saved choice. Treat a non-finite or
negative value the same way and cover these defensive cases in unit tests.

## Production boundary

- A platform-neutral policy that combines resolved theme motion with system
  motion availability.
- A Compose adapter at `OxygenWeatherApp` that observes the system animator
  scale, reacts through snapshot state, and permits deterministic injection in
  tests.
- Existing programmatic `PagerState.moveToPage` consumers changed to use
  effective `MotionStyle` instead of saved `ThemeEffectsLevel`.
- A cap that changes only `ResolvedTheme.motionStyle`; every other field,
  including `effects`, remains the resolver output.

No new permission, preference key, service, weather request, or cache operation
belongs in this boundary.

## Functional invariants

- The stored and selected Off/Subtle/Full value is never overwritten by system
  policy and remains the value shown in Appearance.
- Zero system scale yields effective `MotionStyle.OFF` for every theme.
- Positive system scale preserves the existing resolver behavior for all five
  themes and three saved effects levels.
- The cap changes no palette, backdrop, surface, opacity, typography, geometry,
  theme identity, contrast, layout, or selected effects value.
- Weather values, chronology, provenance, freshness, alerts, navigation
  destinations, and accessibility meaning do not change.
- A live scale change does not recreate selection state, fetch weather, or
  mutate forecast/cache state.
- The outer Home pager remains the sole horizontal-swipe owner. User drag stays
  direct manipulation; programmatic navigation snaps when motion is Off.
- Existing Effects Off opaque/static/complete behavior remains intact.

## Implementation steps

1. Add the platform-neutral binary system-motion policy and a function that
   returns the existing `ResolvedTheme` unchanged when motion is allowed, or a
   copy with only `motionStyle = MotionStyle.OFF` when disabled.
2. Add a narrow Compose adapter that observes
   `Settings.Global.ANIMATOR_DURATION_SCALE` with a `ContentObserver` and
   exposes it through Compose snapshot state. Keep an injectable value at the
   app boundary for connected tests; production uses the Android observer.
3. Resolve theme/contrast/effects normally, then apply the cap. Continue passing
   saved effects to Appearance so selection and persistence are not rewritten.
4. Change every programmatic pager return/tab/back path to call `scrollToPage`
   for effective `MotionStyle.OFF` and `animateScrollToPage` otherwise. Use one
   effective value for all five identified call sites; inspect the final diff
   for any newly introduced `scrollToPage`/`animateScrollToPage` caller too.
5. Add pure matrix tests for five themes, three saved levels, and both system
   states, plus absent/invalid signals and equality of every non-motion field.
6. Extend application-flow test hooks to toggle system motion while the Activity
   remains open. Feed the adapter observable state, retain and assert the same
   Activity identity across the enabled -> disabled -> enabled sequence, and
   assert the newly resolved motion policy and actual scroll-vs-animate choice
   after each update. Exercise the Home menu, Home Back, Appearance Back,
   Appearance Return control, and Search selection return under disabled motion;
   exercise a moving path again after re-enabling with saved Full. A test-only
   decision probe may expose the selected navigation branch, but page identity
   alone is insufficient because both branches reach the same destination.
   Assert stored/selected effects and unchanged weather request/cache counters.
7. Install the app and exercise saved Full at animator scales 1, 0, then 1 while
   it remains open. Record page movement, preference retention, and app/request
   state; restore the device's original scale after capture.

## Acceptance criteria

- The 5 themes × 3 saved levels × 2 system states matrix passes: zero always
  yields `MotionStyle.OFF`; positive scale preserves the resolver result.
- Tests prove only `motionStyle` changes. All other resolved fields are equal.
- Missing, negative, NaN, and infinite cases do not disable motion.
- A connected test changes injected policy without Activity recreation and
  observes effective-theme recomposition and the updated page-transition
  choice in both directions; it distinguishes the scroll and animate branches.
- Back, page tabs, and returns from Search and Appearance move immediately when
  disabled; both Appearance Back and its Return control are covered.
  Re-enabling restores behavior from the saved choice/theme ability.
- Appearance shows the saved non-Off choice during and after the override;
  persistence reads/writes remain unchanged.
- Weather operation counters and canonical/cache fixtures remain unchanged.
- Installed production-path evidence shows no visible programmatic page motion
  at scale 0 and restored motion at scale 1 without Activity restart. The record
  establishes that the setting changed while the same Activity stayed open and
  the UI responded before any relaunch or navigation-triggered refresh.

## Verification and evidence

Store commands, results, captures, and notes under
`.codex/test-artifacts/141-reduced-motion-effects-policy/`.

Focused checks:

```sh
./gradlew testDebugUnitTest --tests '*ReducedMotionPolicyTest' --tests '*ThemeResolverTest' --tests '*EffectsPreferenceSelectionTest'
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest
```

If the final class name differs, record the discovered class/method and exact
command. The connected test must cover a live injected-policy toggle, retained
selection, all five programmatic pager entry paths, and weather counters. Use
`ProductionHomeCompositionTest` and `ThemeAppearanceApplicationFlowTest` as
existing page/return test patterns; Search selection is represented in
`ManualLocationSearchFlowTest`. Capture branch decisions or motion timing as
well as final page identity so both pager methods cannot pass indistinguishably.

Broader checks:

```sh
python scripts/dev.py test
python scripts/dev.py contract
python scripts/dev.py check
python scripts/dev.py workflow
git diff --check
```

Installed check: 360 × 640 dp, font scale 1.0, LTR, Standard contrast and layout,
Atmospheric theme, saved Full effects.

1. Record device/emulator, API, build, density, locale, and original animator
   duration scale.
2. With the Activity open at scale `1`, navigate pages and return from Appearance;
   record animated behavior and the Activity/process identity.
3. Set scale `0` without restarting. Repeat page tab, Android Back, Appearance
   Back and Return control, and Search selection return; record immediate
   movement and capture Appearance plus Now showing saved Full and complete
   content. Record the observed system setting and same Activity/process identity
   before navigating; wait for the open composition to reflect the change.
4. Restore scale `1` without restarting; record observed setting, same
   Activity/process identity, resumed motion, and retained Full.
5. Record weather request/cache counters and restore the original device scale,
   including when another verification step fails.

Screenshots prove stable content and selection. A short screen recording or
frame-timestamp/event log proves presence/absence of motion. Record the exact
system UI or `adb shell settings` commands; still screenshots alone do not prove
motion stopped.

Inspect the final diff. Record unavailable SDK/device checks and why. Do not
claim broad large-font, RTL, TalkBack, or full R6.4 matrix verification.

## Risks and assumptions

- The app-level observer intentionally mirrors Compose UI's Android setting
  source because its context scale remained stale in the live connected test.
  Keep the observer scoped to composition and covered by the real-setting flow.
- Compose animations already consume this scale. Oxygen still needs the binary
  cap because current pager routing and future ambient motion must resolve
  through the product's `MotionStyle` boundary.
- Scale zero finishes Compose motion on the next frame rather than literally
  eliminating a frame callback. Acceptance concerns no visible transition and
  effective `MotionStyle.OFF`.
- Current effects are static. R5.4B–R5.4E consumers must use this effective
  motion boundary, but those backgrounds are not introduced here.
- Changing the installed scale may require emulator/ADB privileges. If it cannot
  be changed, automated checks may pass but the installed exit criterion remains
  unverified and the cycle must not claim completion.

## Out of scope

- A persisted or user-facing reduced-motion preference.
- Window/transition scale aggregation.
- Effects preference changes completed in R5.4.
- Ambient backgrounds or theme treatments from R5.4B–R5.4E.
- Weather, cache, forecast mapping, alert, or meteorological changes.
- Broad accessibility, large-font, RTL, TalkBack, or R6.4 verification.
