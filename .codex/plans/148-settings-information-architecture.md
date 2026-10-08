# Plan 148 — Settings information architecture

Status: Completed
Cycle ID: 148-settings-information-architecture
Roadmap item: R5.6
Created: 2026-10-07
Reviewed: 2026-10-07

## Objective and observable outcome

Add a named Settings shell reachable from Home, with Appearance and Units
destinations. Users can move among these routes and return to the exact Home
page that opened Settings. Units uses the Activity-owned `UnitPresetSelection`
and remaps the retained selected forecast through the existing presentation
mapping path. Route changes and preference changes cause no weather refresh,
repository/cache operation, or alert request.

The independently observable outcome is a real installed application flow from
a non-Now Home page through Settings, Appearance, Units, and back to the same
Home page with the selected forecast still intact.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`
  - Replace the direct Home-to-Appearance route with explicit Home, Settings,
    Appearance, and Units route state.
  - Capture the opening Home page once on entry to Settings; retain it while
    moving between Settings destinations; return to it only when leaving the
    Settings flow.
  - Preserve the existing `HorizontalPager` state and the hourly/daily window
    indices while Settings destinations are shown.
  - Keep the current Appearance controls and selected values under Appearance.
    Provide a named, accessible Settings shell with Appearance and Units
    choices. Do not add location, data-source, legal, or product-information
    routes.
  - Add only narrowly scoped private UI components here unless an independently
    reusable Settings control justifies a new UI source file.
- `app/src/main/java/com/oxygen/weather/MainActivity.kt`
  - Pass the effective `UnitPresetSelection` value and a production selection
    callback to `OxygenWeatherApp`.
  - On a unit choice, use the existing selection/store and retained canonical
    forecast state to remap `SelectedForecastPresentationState`, following the
    current `applyUnitPresetForTests` behavior without introducing a second
    unit store or a test-only production path.
- `app/src/androidTest/java/com/oxygen/weather/ui/`
  - Extend or add focused installed application-flow coverage for route state,
    unit selection/remapping, semantics, and operation counters. Prefer the
    existing theme and unit preference test hooks where they provide the
    required observation; add only narrow seams if needed.
- Cycle evidence: `.codex/test-artifacts/148-settings-information-architecture/`.

No other production boundary is included. Existing preference store/schema and
forecast/domain/repository/cache/alert boundaries are dependencies only.

## Functional invariants

- The Home page order remains Now → Hourly → Daily → Details. Settings remains
  outside the Home pager; it does not become a fifth page or acquire horizontal
  swipe ownership.
- The user returns to the same named Home page that opened Settings. Keep the
  in-memory pager, hourly/daily window, and selected forecast state intact.
  Entering Appearance directly from the Home Settings entry is replaced by
  opening Settings first.
- Android Back within Appearance or Units returns to Settings. Android Back
  from the Settings shell returns to the opening Home page. Existing Home Back
  behavior remains unchanged when no Settings route is open.
- Appearance continues to expose the existing five themes, Standard/High
  contrast, Off/Subtle/Full effects, and Standard/Simple layout. Preserve
  existing preference and resolver semantics. Effects Off remains opaque,
  static, and complete.
- Units exposes Metric, US, and UK, displays the effective current selection,
  saves through the existing `UnitPresetSelection`/store boundary, and updates
  the selected forecast presentation via the existing unit-aware mapper. A
  write failure must not be represented as a successful persisted selection;
  preserve the current selection boundary's effective-value behavior.
- Canonical weather values, provider units, chronology, missing values,
  provenance, and alert meaning do not change. Unit conversion stays at the
  presentation boundary.
- Navigation and Appearance/Units changes cause no forecast request or
  refresh, repository call, cache read/write, or official-alert request.
- Route names, actions, and selected choices have meaningful visible labels
  and semantics. Applicable controls meet 48dp target guidance and remain
  usable with large text and RTL; directional navigation mirrors while names
  continue to identify destinations. Theme/layout/effects do not alter
  navigation semantics.
- Do not reintroduce retired Atmosphere Deck language or composition.

## Implementation steps

1. Confirm the existing header affordance, route/back handling, saveable
   `PagerState`, hourly/daily indices, `ThemeAppearanceSurface`, unit selection
   initialization, `applyUnitPresetForTests` mapping behavior, and available
   operation-counter hooks. Record any test seam that cannot observe a required
   operation before changing it.
2. Replace the direct Appearance route with a Settings entry and a small route
   state model. Capture the opening page on the Home → Settings transition,
   keep it stable across Settings → Appearance/Units transitions, and restore
   it on Settings exit. Route-local Back returns to the parent route. Keep
   existing Home pager/window state alive and do not change the global pager.
3. Move the current Appearance surface under its named destination without
   changing its preference callbacks, initial selections, resolver inputs, or
   supported controls. Add the Settings shell links for Appearance and Units.
4. Add Units as a destination using the effective Activity-owned preset and a
   semantic selection callback. Persist through the existing selection and
   remap the retained canonical selected forecast through the existing
   presentation mapper. Preserve honest success/failure state behavior.
5. Extend focused application-flow instrumentation to exercise a non-Now page,
   preserve a non-default hourly or daily window if practical, enter Settings,
   visit both destinations, change Appearance and Units, return to Settings,
   then Home. Assert route identity, origin page/window, effective choices,
   remapped displayed values, canonical input identity, and unchanged
   forecast/repository/cache/alert operation counters.
6. Add focused semantics and interaction assertions for Settings, Appearance,
   and Units labels, destination actions, selected states, Back/return paths,
   and applicable target bounds. Cover preference write failure where the
   current hooks make it observable.
7. Install and inspect the actual `MainActivity → OxygenWeatherApp` flow. At
   360 × 640 dp and font scale 1.0, capture Settings, Appearance, and Units.
   Inspect route/control reachability and critical-content clipping at font
   scale 1.3, in RTL, and with Effects Off; record captures and observations.
   Exercise return to Home from at least Hourly or Daily, including the
   selected page identity. Record the exact emulator/API, layout direction,
   font scale, theme, effects, and selected route/page for each evidence item.
8. Run focused and broader verification below; preserve command output and
   installed evidence in the cycle evidence directory. Inspect final diff and
   `git diff --check`. Do not close the cycle until actual installed evidence
   is recorded or its unverified boundary and reason are explicit.

## Acceptance criteria

- A visible Home control opens a named Settings shell with Appearance and
  Units destinations. No R5.6A/R5.6B destination appears.
- Appearance remains reachable from Settings and exposes all current theme,
  contrast, effects, and layout controls with their existing selection and
  application behavior.
- Units shows Metric, US, and UK; identifies the effective selection; saves
  through the existing preference boundary; and updates the displayed
  selected forecast using existing unit-aware presentation mapping.
- From each of the four Home pages, the Settings flow can return to the page
  that opened it. Focused installed evidence exercises a non-Now origin and
  verifies the original page/window and forecast are retained.
- Android Back from Appearance/Units returns to Settings; Back from Settings
  returns to the origin page; Home Back behavior outside Settings is
  unchanged.
- Navigation and both preference changes leave forecast refresh, repository,
  cache read/write, and alert request counters unchanged. Canonical fixture or
  selected forecast input remains unchanged while presentation reflects the
  selected unit preset.
- Focused semantics/interaction checks cover destination and control names,
  active selections, route return, and applicable minimum target bounds.
- Installed compact captures exist for Settings, Appearance, and Units at
  360 × 640 dp, font scale 1.0, through the normal app path. Large text
  (font scale 1.3), RTL, and Effects Off affected routes are inspected with
  exact outcomes recorded. No new critical clipping or unreachable control
  remains in inspected routes. An unavailable condition is recorded as
  unverified with the exact environment reason, never as a pass.
- No provider, repository, cache, location, alert, canonical-data, provenance,
  chronology, or meteorological meaning changes are included.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/148-settings-information-architecture/`.

- `verification.md`: changed boundary; exact command and result; emulator/API
  and viewport/font/RTL/theme/effects conditions; route path; opening and
  restored Home page/window; selected preferences; operation-counter values;
  observations; unavailable checks and limitations.
- Focused Android instrumentation: run the relevant Settings application-flow
  test plus existing `ThemeAppearanceApplicationFlowTest` and
  `UnitPresetApplicationFlowTest` as applicable, using
  `python scripts/dev.py android-test`. The exact Gradle connected-test filter
  or test-class selection must be recorded from the executed command.
- Focused JVM checks: run relevant preference and presentation mapper tests
  with `python scripts/dev.py test` if implementation changes their shared
  contract; record exact command and result.
- Installed captures: Settings, Appearance, Units at 360 × 640 dp / font
  scale 1.0, plus reviewed affected routes at font scale 1.3, RTL, and Effects
  Off. Capture the return flow from a non-Now page and retain route/state
  observations alongside images.
- Broader checks: `python scripts/dev.py check`,
  `python scripts/dev.py contract`, `python scripts/dev.py workflow`, and
  `git diff --check`; record any command unavailable and the exact reason.
- Final evidence review: inspect the installed path, all retained captures,
  final diff, and repository status before close. Compilation or previews do
  not satisfy installed visual acceptance.

## Risks and assumptions

- Bounded execution: one route owner (`OxygenWeatherApp`), one Activity-owned
  unit callback/state boundary (`MainActivity`), and focused application-flow
  tests. Keep additions within those files and a small number of narrowly
  scoped UI/test files; do not broaden into all Settings content or a general
  navigation framework.
- R5.5 and R5.5A are complete; R5.6 is the next roadmap slice. R5.6A and
  R5.6B depend on this cycle and remain excluded.
- Current Appearance is a Home-header overlay that stores an opening Home
  page and returns there. Reuse the established return behavior while making
  Settings the parent route; route changes must not recreate or reset the Home
  pager/window state.
- `UnitPresetSelection` is initialized and owned by `MainActivity`. The
  current preset application test seam already selects, remaps retained
  forecast state, and exposes counters/hooks; production UI needs a real
  callback/value connection to this boundary, not a new store.
- The current unit selection boundary updates its effective in-memory value
  even if a write reports failure. The Units UI must communicate the actual
  outcome/effective value consistently with that existing behavior; do not
  promise persistence after a failed write.
- Existing application-flow tests already cover Appearance return to each
  Home page and unit remapping across pages separately. Extend the narrowest
  tests to cover their combined Settings route and no-work invariant.
- Installed visual evidence is required for the route objective. If the
  Android environment is unavailable, retain the exact attempted command and
  environment failure and leave installed acceptance explicitly unverified.
- No unresolved product decision is required to implement this scope. A
  route header/back-label detail may follow the existing theme component
  conventions, provided route names, destination semantics, and the stated
  Back hierarchy remain intact.

## Out of scope

- Locations, Data Sources, Privacy, Open Source Licenses, About, or any other
  destination; these belong to R5.6A/R5.6B.
- New preference stores/schemas, persistence migration, durable navigation
  history, or changes to theme/contrast/effects/unit persistence semantics.
- Changes to Home page order/identity, pager ownership, forecast windows,
  selected location, alert routing/content, forecast fetch policy, provider,
  repository, cache, canonical weather values, provenance, or forecast
  meaning.
- Broad Settings compact/large-font matrix (R6.2A), release-wide
  accessibility closure, TalkBack service audit, unrelated clipping fixes,
  and performance profiling.
- Theme redesign or retired Atmosphere Deck elements.
