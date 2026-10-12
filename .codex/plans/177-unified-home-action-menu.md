# Plan 177 — Unified Home action menu

Status: Completed  
Cycle ID: 177-unified-home-action-menu  
Roadmap item: NAV.5  
Created: 2026-10-12  
Reviewed: 2026-10-12  
Owner decisions: `.codex/test-artifacts/176-specify-unified-home-action-menu/owner-resolution.md`

## Objective

Replace the separate Search and Settings Home header actions with one
three-line “Home actions” menu offering Search, Settings, and About from Now,
Hourly, Daily, and Details in all five themes. Preserve current Search and
Settings state behavior. About opened from Home returns directly to its
originating Home page through both its visible Return action and Android Back;
About opened within Settings returns to Settings through both actions.

## Dependencies and approved decisions

- NAV.4 is complete. The owner selected the three-line menu icon and direct
  About return to the Home page that opened About. The owner resolution is
  recorded in `.codex/test-artifacts/176-specify-unified-home-action-menu/owner-resolution.md`.
- Menu item order is Search → Settings → About, following the approved
  destination sequence and the NAV.4 decision packet.
- `MainActivity` supplies the production `LocationSearchCoordinator`. In a
  sparse/test composition without one, omit Search while keeping Settings and
  About available.

## Production boundary

- Primary production owner: `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`
  for the shared Home header, menu actions, and About return context.
- Focused Home/menu tests:
  `app/src/androidTest/java/com/oxygen/weather/ui/ProductionHomeCompositionTest.kt`.
- Search session tests:
  `app/src/androidTest/java/com/oxygen/weather/ui/ManualLocationSearchFlowTest.kt`.
- Settings and About route tests:
  `app/src/androidTest/java/com/oxygen/weather/ui/SettingsLegalProductDestinationsFlowTest.kt`
  and `app/src/androidTest/java/com/oxygen/weather/ui/SettingsDataLocationDestinationsFlowTest.kt`.
- Existing connected tests that enter Search or Settings directly from the old
  Home header may be migrated to open the same destination through the new
  menu. Keep those edits limited to navigation setup; preserve their test
  assertions and scope.
- `MainActivity.kt` is read-only context for coordinator composition and
  operation hooks. Change it only if focused tests prove a Compose-boundary
  test seam is insufficient.
- Do not change provider, forecast, alert, cache, persistence, location
  selection, weather presentation, theme preferences, or destination content.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page identity, the
  page-title chooser, outer pager as the only global horizontal-swipe owner,
  and existing Home Back behavior.
- Keep Search session/query/selection/cancel/system-Back behavior and its
  opening-page restoration. Do not add query persistence.
- Keep Settings route/scroll state and child-destination return behavior;
  Settings root Return and Back restore the page that opened Settings.
- Preserve About content and the separate return contracts below.
- Menu open/dismiss and navigation to Settings/About do not change weather
  facts, provenance, freshness, alert meaning, forecast/alert requests, cache
  operations, or selected-location behavior.
- Theme, Effects None (`OFF`), High contrast, reduced motion, and RTL do not
  change route or accessibility meaning. Forecast chronology remains
  earliest-to-latest.
- The trigger and each menu row have at least 48dp targets, visible labels,
  and meaningful Compose semantics. The icon is not the accessible name.

## Route contract and state boundary

Keep the menu's expanded state independent from the page-title chooser and
from Search/Settings route state. Use an explicit About return target with two
cases: `Settings` or `Home(openingPage)`; store the opening Home page when
About is opened directly. Do not infer About's origin from `settingsRoute` or
reuse/overwrite `settingsOpeningPage`, since nested About and Settings each
have their own origin state.

| Entry | Visible Return | Android Back |
|---|---|---|
| Home menu → About | Return to the captured Home page | Return directly to the captured Home page |
| Settings → About | Return to Settings | Return to Settings |
| Home menu → Settings root | Return to the captured Home page | Return to the captured Home page |
| Home menu → Search | Existing Search cancel/selection/session behavior | Existing Search dismissal and opening-page restoration |

The About return control's visible text must correctly describe its destination
for both entry paths. Direct About return must not visit Settings first. The
Back handler must dispatch by the explicit About return target before applying
the Settings-root or normal Home-page Back behavior.

## Implementation steps

1. Before edits, record the source revision, emulator/API identity, and
   pre-change results for the affected connected-test classes. Run each class
   separately with the Gradle class filter (use `./gradlew` on Unix-like hosts
   or `gradlew.bat` on Windows):

   ```sh
   ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionHomeCompositionTest
   ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ManualLocationSearchFlowTest
   ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.SettingsLegalProductDestinationsFlowTest
   ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.SettingsDataLocationDestinationsFlowTest
   ```

   Save raw XML/log output plus a method-level pass/fail inventory in
   `.codex/test-artifacts/177-unified-home-action-menu/baseline/`. The broad
   connected-suite command is `python scripts/dev.py android-test`.
2. Reconfirm the NAV.4 owner decision and inspect the shared header, Search
   callbacks, Settings state/scroll state, About rendering, all Back handlers,
   and operation counters. Preserve the baseline results before changing any
   test or production source.
3. Replace the two Home header actions with one theme-resolved three-line menu
   trigger. Keep it independent of the page-title chooser. Offer Search,
   Settings, About in that order; omit only Search when its coordinator is
   absent. Use a readable theme-aware menu surface and 48dp-or-larger targets.
4. Wire Search and Settings through their current callbacks and independent
   origin/session state. Add the explicit About return target and route both
   visible Return and Android Back according to the route table. Preserve
   nested Settings → About behavior.
5. Add/update focused Compose and installed-flow tests for item order,
   semantics, target bounds, dismissal, source-page reachability, all route
   exits, Search session behavior, Settings state, About content, missing
   coordinator behavior, and unchanged operation counters.
6. Run the same focused connected-test commands used for the baseline. Compare
   each method's final result and failure signature. Under the owner-directed
   cadence change, do not run the broad connected suite or `python scripts/dev.py
   check` on this interim implementation slice. Run checks focused on the
   changed Home routes, `python scripts/dev.py contract`, Android test-source
   compilation, `git diff --check`, and inspect the final diff. Full relevant
   unit/connected suites and `python scripts/dev.py check` are reserved for
   every fifth implementation slice, as tracked in `.codex/testing-cadence.md`.
7. Install the real app and capture the menu and route states in the matrix
   below. Record exact device/API/build, commands, results, limitations, and
   evidence paths. Screenshots support visual acceptance; tests establish
   route and operation behavior.

## Regression-baseline and failure attribution

Cycle 175 reported 20 failures in 85 connected tests. Its focused
`ProductionHomeCompositionTest` passed 3/3. Two
`ThemeAppearanceApplicationFlowTest` cases initially failed on off-screen
Settings interactions, then passed targeted reruns after scrolling fixes; the
85-test suite was not rerun after those fixes. The prior result is therefore
historical context, not a complete NAV.5 pre-change baseline. Cycle 175 evidence
is under `.codex/test-artifacts/175-thumb-reachable-quick-navigation/`.

For NAV.5, preserve pre-edit and post-edit XML plus logs for identical focused
test commands. Compare test method names and failure signatures (assertion,
stack, or setup cause), not only aggregate counts. A failure is not considered
pre-existing solely because the total remains below 20. Any newly failing
method, changed failure signature, or failure in an affected route test must
be investigated and fixed or explicitly recorded as an unmet blocker before
closure. The full suite is deferred according to
`.codex/testing-cadence.md`.

## Acceptance criteria

- One accessible three-line “Home actions” trigger replaces the separate
  Search and Settings header controls on all four Home pages, remains separate
  from the page-title chooser, and renders with the selected theme's semantic
  palette and readable menu surface.
- With a production coordinator, the menu presents Search, Settings, About in
  that order. Without a coordinator, only Search is omitted.
- Focused tests verify menu opening, outside-tap and Back dismissal, page
  retention, menu order, semantics, and at least 48dp trigger/row bounds.
- From each Home page, Search opens through its current coordinator/session
  path and cancel/selection/system Back restore the opening page without
  changing query lifecycle. Settings Return/Back restores the opening page
  while retaining current Settings state/scroll behavior.
- Direct About from each Home page shows current About content; visible Return
  and Android Back each restore the exact opening page directly. Nested About
  from Settings returns to Settings through both actions. Route tests prove
  these paths independently.
- Forecast, alert, cache, and selected-location operation counters remain
  unchanged for menu open/dismiss and Settings/About navigation.
- Same-command focused pre/post results and method-level failure signatures
  are retained. No new or changed affected-test failure remains unexplained.
- Installed captures show the unified menu in the core 20-cell page/theme
  matrix and the applicable stress states. Record unrun accessibility/device
  checks; do not claim live TalkBack unless it is performed.
- Focused affected-class comparisons, Android test-source compilation,
  `python scripts/dev.py contract`, and `git diff --check` pass, or the history
  records exact failures and unmet boundaries without claiming NAV.5 passed.

## Installed-state and accessibility matrix

- **Core:** actual app path at 360×640dp, font scale 1.0, LTR; capture all
  four Home pages × five themes (20 menu-open cells). Record emulator/API,
  build identity, and installed state.
- **Large font:** font scale 1.3 on representative Now and Details pages;
  verify the trigger, popup rows, page title, and route actions remain
  reachable without clipping or overlap.
- **RTL:** app-level RTL override on representative Home/menu and route states;
  verify popup anchoring, trigger placement, readable labels, return behavior,
  and preserved page order. System RTL may be separately unverified if the AVD
  cannot enter RTL.
- **Appearance overlays:** verify menu readability and route operation under
  Effects None, High contrast, and reduced motion. Use representative cells
  across themes/pages; record the chosen coverage rather than implying a full
  cross-product unless captured.
- **Accessibility:** automated semantics and target bounds are required.
  Report live TalkBack traversal separately and claim it only if run.
- Retain evidence under `.codex/test-artifacts/177-unified-home-action-menu/`.
  Cycle 175 page-chooser captures are not menu acceptance evidence.

## Verification and evidence

- Baseline and post-change focused connected tests: the four class-filtered
  Gradle commands in Step 1, with raw XML/logs and method-level comparison
  under the cycle evidence directory.
- Broad connected suite and `python scripts/dev.py check` are deferred to the
  fifth implementation slice under `.codex/testing-cadence.md`; this cycle
  runs the affected focused class filters and boundary-specific checks.
- Installed screenshots, capture manifest, device/API/build identity,
  verification log, and failure comparison are retained under
  `.codex/test-artifacts/177-unified-home-action-menu/`.
- Close only through the cycle workflow, recording exact passed/failed/unrun
  checks and limitations in `.codex/history/`.

## Risks and assumptions

- Direct and nested About share `SettingsRoute.ABOUT` today, and the current
  visible label and Back handler both return to Settings. Keep route origin
  explicit so the two owner-approved outcomes cannot be conflated.
- Search is absent only in sparse/test compositions without a
  `LocationSearchCoordinator`; production `MainActivity` supplies it.
- The broad Cycle 175 failures are not fully enumerated in the retained summary
  and the full suite was not rerun after two fixes. Capture a fresh NAV.5
  method-level baseline before editing; do not infer baseline status from the
  old aggregate failure count.
- Reuse the Cycle 175 API 37 AVD if available. App-level RTL override is the
  established installed route when system RTL remains unavailable.
- TalkBack traversal is reported only if run; Compose semantics tests and
  screenshots do not establish service-level speech behavior.

## Out of scope

- Replacing or redesigning the page-title chooser, Home pager, or global page
  order.
- Search query persistence, geocoding, device-location permissions, or changes
  to Search selection/cancel semantics.
- Settings content/preferences, About content, or unrelated destination
  behavior beyond accurate Return labeling and the approved route.
- Provider, forecast, alert, cache, selected-location, units, or theme/effects
  preference behavior changes.
- New gestures, a bottom-corner control, or destinations beyond Search,
  Settings, and About.
- Claims of broad connected-suite, TalkBack, or visual acceptance without
  corresponding evidence.
