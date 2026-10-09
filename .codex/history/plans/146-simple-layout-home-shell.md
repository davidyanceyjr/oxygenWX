# Plan 146 — Simple layout Home model and navigation shell

Status: Completed
Cycle ID: 146-simple-layout-home-shell
Roadmap item: R5.5
Created: 2026-10-07
Reviewed: 2026-10-07

## Objective

Make Standard/Simple a selectable Home layout choice in the existing Appearance
surface. Simple applies the existing `LayoutPreset.SIMPLE` resolved geometry to
the Home shell while preserving the current selected forecast and the shared
four-page navigation state. Layout selection is presentation-only and must not
request or reload weather. This cycle delivers the Simple shell only; the
reduced Simple Forecast surface remains R5.5A.

## Reviewed implementation decisions

- **Selection entry point:** add Standard/Simple options to
  `ThemeAppearanceSurface` in `OxygenWeatherApp`, alongside theme, contrast,
  and effects. The existing Home header opens this surface and its Back/Return
  route restores the page from which it was opened. Do not add a Settings
  destination or another selection route.
- **Persistence:** the repository has persisted theme, contrast, and effects
  stores, but no layout preference store or application layout state. Keep
  layout selection in `OxygenWeatherApp` as `rememberSaveable` UI state for the
  current saved-activity state. Do not add SharedPreferences, a layout store,
  or claim durable preference persistence in this cycle. A durable layout
  preference is a separate follow-up if required.
- **Home page model/navigation:** keep one `HomePage` model and one
  `HorizontalPager` with the existing Now → Hourly → Daily → Details order.
  Reuse the existing named page selector, page callbacks, Back handling,
  `PagerState`, `hourlyWindowIndex`, and `dailyWindowIndex`; do not create a
  Simple-specific pager, page order, forecast state, or navigation model.
- **Shell/body boundary:** resolve the selected layout into `ResolvedTheme`
  and apply its existing geometry to the shared Home shell. Keep the current
  page body composables and typed presentation models in this cycle. No
  alternate or reduced forecast-page composition is part of R5.5; that belongs
  to R5.5A. This keeps the shell independently observable without inventing a
  second meaning for forecast content.

These decisions follow the current app composition, the resolver's documented
geometry-only layout contract, R5.5/R5.5A boundaries, and the product rule that
appearance cannot change weather or navigation meaning.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`: saved layout
  selection state, resolver input, Appearance choice, and shared shell wiring.
- Focused instrumentation coverage in
  `app/src/androidTest/java/com/oxygen/weather/ui/ThemeAppearanceApplicationFlowTest.kt`
  (or a narrowly named companion test in the same package if separation makes
  the existing class unwieldy).

Reuse `LayoutPreset`, `ThemePreferences`/`ResolvedTheme` contracts, and the
existing resolver geometry. Do not change provider, repository, cache,
forecast-presentation, selected-location, alert, unit, or weather semantics.
Do not add a persistent layout preference boundary.

## Functional invariants

- Preserve the global page order and visible page identity: Now → Hourly →
  Daily → Details.
- Keep the outer Home pager as the sole horizontal-swipe owner. Preserve
  current page and Android Back behavior when opening/returning from Appearance
  and when selecting a layout.
- Preserve selected location/forecast, status, alert state, hourly and daily
  window indices, provenance, freshness, units, missing-data behavior,
  chronology, and accessibility meaning across layout changes.
- Both layout choices render the same supplied `HomePresentation`, status,
  partial-horizon, and forecast-context models. Layout changes do not invoke
  the repository, refresh controller, or cache.
- Appearance layout choices and the current selection have meaningful visible
  labels and selected semantics; interactive targets meet 48dp guidance.
- At compact width and large font, controls remain reachable without clipping
  critical meaning. RTL preserves chronological order and correct directional
  behavior. Effects Off remains opaque, static, and complete.
- Do not restore retired Atmosphere Deck language or composition.

## Implementation steps

1. Add a saveable `LayoutPreset` selection in `OxygenWeatherApp`, defaulting to
   `STANDARD`. Resolve the theme with this layout and ensure layout changes
   only recompute presentation geometry; keep the state out of weather
   request/application controllers.
2. Add clearly named, selected-state Standard and Simple choices to
   `ThemeAppearanceSurface`, preserving existing scroll, Back, and Return
   behavior. Return to the opening global page after selection and returning
   from Appearance.
3. Keep the existing global `HomePage` list, pager, callbacks, and hourly/daily
   index state shared across layouts. Apply the resolved Simple geometry to
   the existing shell; retain current page bodies for this cycle.
4. Extend focused installed-flow instrumentation to select Simple and return
   to Standard while on Now, Hourly, Daily, and Details. Exercise a nonzero
   Hourly window and Daily window, capture selected forecast/page/window facts,
   and assert they remain the same. Assert repository/refresh/cache counters
   are unchanged by layout selection. Reuse the existing
   `ThemeAppearanceApplicationFlowTest` fixture and request-count hooks where
   applicable.
5. Install and inspect the real `MainActivity → OxygenWeatherApp` path in
   Standard and Simple at 360 × 640 dp, font scale 1.0. Inspect font scale 1.3,
   RTL, and Effects Off for affected selection/shell behavior. Record any
   unrun case and its exact cause; distinguish pre-existing R6.2 compact
   large-font Now clipping from regressions introduced here.
6. Run focused verification, relevant repository checks, workflow validation,
   `git diff --check`, and final diff/status inspection. Retain exact outputs
   and visual captures in the cycle evidence directory.

## Acceptance criteria

- Standard and Simple are selectable from the existing Appearance surface and
  their selected state is exposed visibly and semantically.
- Switching to Simple changes the resolved layout to `SIMPLE` and renders the
  shared Home shell; returning to Standard resolves `STANDARD`.
- On each of the four global pages, layout switching retains page identity and
  the same selected forecast/location. On Hourly and Daily it also retains the
  selected window indices and represented window contents.
- Deterministic instrumentation proves no repository/refresh/cache operation
  is caused by layout switching and supplied weather/presentation facts remain
  invariant.
- The installed compact baseline captures both layout states through the real
  app path. Font scale 1.3, RTL, and Effects Off are inspected at affected
  boundaries or listed as unverified with reasons.
- No alternate Simple Forecast content or choice surface is added; R5.5A
  remains independently implementable and verifiable.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/146-simple-layout-home-shell/`.

- `verification.md`: selection route and state lifetime, changed production
  boundary, exact commands and results, device/emulator/API/build details,
  selected weather/page/window facts, checked layout/environment states, and
  limitations.
- Focused installed flow test, expected method:
  `ThemeAppearanceApplicationFlowTest.simpleLayoutSwitchRetainsHomeAndWindowStateWithoutWeatherRequests`.
  Run it with the connected Android test task and the instrumentation runner's
  class/method filters. It must check all four pages, nonzero Hourly/Daily
  windows, selected forecast facts, and unchanged request/refresh/cache
  counters. If the existing fixture cannot observe one of those counters,
  extend only the focused test hook needed to observe it within this boundary.
- Focused JVM regression: `python scripts/dev.py test`, including existing
  `ThemeResolverTest` assertions that Simple preserves semantic appearance
  while resolving its geometry.
- Installed captures through the real `MainActivity → OxygenWeatherApp` path:
  Standard and Simple at 360 × 640 dp, font scale 1.0; review the affected
  shell/Appearance states at font scale 1.3, RTL, and Effects Off.
- Broader checks: focused connected test above; `python scripts/dev.py check`
  when Android SDK/dependencies are available; `python scripts/dev.py contract`;
  `python scripts/dev.py workflow`; `git diff --check`; final diff/status
  review. Record baseline/unrelated failures without attributing them to this
  slice.

Compilation and Compose previews alone do not satisfy the installed visual
criterion. If no emulator/device is available, record attempted install and
capture commands plus the exact environment limitation; do not claim installed
acceptance.

## Visual objective and constraints

The visual objective is to make the Simple choice and its selected state
obvious, and to show that the shared Home shell resolves through the Simple
geometry while preserving named navigation. The change must remain readable at
360 × 640 dp, font scale 1.0 and 1.3; retain useful tap targets and visible page
identity; preserve RTL order/direction; and remain complete with Effects Off.
Capture the actual installed Standard and Simple shell, not a preview. R6.2's
previously recorded compact font-scale-1.3 Now clipping is a baseline
limitation; report only new or worsened clipping from this slice.

## Risks and assumptions

- The resolver already supports `LayoutPreset.SIMPLE` and documents it as
  geometry-only. This plan assumes that the current Simple geometry is the
  intended shell distinction for R5.5; a distinct forecast-page composition
  remains R5.5A.
- `rememberSaveable` retains the selection through recomposition and saved
  Activity state, but not as a durable cross-launch user preference. No
  durable persistence is promised by this cycle.
- Existing full connected suites have had unrelated failures in location,
  forecast, and context flows. Compare against recorded cycles 137–140 and
  report exact failures without weakening the focused acceptance checks.
- Installed emulator/device availability is environment-dependent. Lack of
  installed evidence must be reported as an unverified boundary.

## Out of scope

- Reduced Simple Forecast choice or alternate forecast surface, and
  Standard/Simple forecast-page equivalence (R5.5A).
- Durable layout preference storage or Settings navigation/destinations
  (R5.6+).
- New weather fetching, repository/cache behavior, forecast remapping, units,
  selected-location behavior, alert semantics, or weather meaning.
- Broad cross-theme/layout screenshot matrix, full service-level TalkBack
  audit, and release-wide accessibility closure (R6).
- Unrelated compact/large-font fixes, theme redesign, or retired Atmosphere
  Deck UI restoration.
