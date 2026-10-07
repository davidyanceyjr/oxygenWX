# Plan 147 — Simple Forecast surface

Status: Completed
Cycle ID: 147-simple-forecast-surface
Roadmap item: R5.5A
Created: 2026-10-07

## Objective

Give the selected Simple layout a reduced, readable presentation for its
existing Hourly and Daily forecast pages. Keep the global Now → Hourly → Daily
→ Details navigation and page identity, and let users browse the supplied
forecast horizons with visible date and Earlier/Later controls. The surface
uses the current selected forecast and causes no repository fetch when the
layout or forecast window changes.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`: branch the
  Hourly and Daily page composition on the resolved `LayoutPreset`, preserving
  shared pager/window state and existing typed presentation inputs.
- A narrowly scoped production forecast component file under
  `app/src/main/java/com/oxygen/weather/ui/` only if the reduced rows/cards
  cannot remain clear and testable as private page components.
- Focused application-flow instrumentation in
  `app/src/androidTest/java/com/oxygen/weather/ui/ThemeAppearanceApplicationFlowTest.kt`
  or a narrowly named companion in the same package.

Do not change `HomePresentation`, provider/domain models, repository/cache,
location or alert boundaries. The selected `HomePresentation`, status,
partial-horizon information, provenance, and forecast context remain the
inputs to rendering. R5.5's saved-activity layout selection remains the
selection mechanism; durable layout preference is not introduced here.

## Functional invariants

- Preserve the four named global pages in order: Now → Hourly → Daily →
  Details. The outer Home pager remains the only global horizontal-swipe
  owner; no new Forecast page, nested pager, or replacement of Hourly/Daily
  page identity is added.
- Simple presentation changes layout only. It displays the same selected
  location, weather facts, units, chronology, missing-value states, source,
  freshness, partial-horizon state, and forecast window contents as Standard.
- Hourly retains up to six actual chronological entries per visible window,
  visible date selection for represented dates, and one-window Earlier/Later
  actions. Daily retains up to five actual chronological days per window and
  one-window Earlier/Later actions. Never pad or fabricate entries.
- Retain the current hourly and daily window indices when changing layout;
  selecting a forecast window does not reset the global page or selected
  forecast.
- No layout change, date jump, or window action triggers a refresh, repository
  request, cache operation, or weather remapping.
- Important facts remain visible text with meaningful accessibility
  semantics. Controls meet 48dp target guidance where applicable. RTL keeps
  forecast chronology earliest-to-latest and mirrors directional affordances
  appropriately. Effects Off remains opaque, static, and complete.
- Do not restore retired Atmosphere Deck presentation language.

## Implementation steps

1. Inspect the existing production Hourly/Daily page components, typed
   presentation models, appearance-resolved layout, and cycle 146 application
   flow hooks. Keep the Simple-specific scope within the two forecast page
   bodies and their reusable presentation components.
2. Implement a reduced Simple composition for Hourly and Daily using the
   existing `HourlyWindowPresentation`, `DailyWindowPresentation`, date
   jumps, source/freshness, status, and partial-horizon values. Keep named
   Hourly/Daily page headers and existing window controls; do not add a new
   global Forecast route or alternate forecast data source.
3. Preserve the existing shared pager and window indices. Add no-fetch
   installed-flow coverage that switches Standard/Simple and browses windows,
   comparing the visible forecast facts and location/page/window state while
   checking request/refresh/cache counters.
4. Install and review the real `MainActivity → OxygenWeatherApp` path for
   Simple Hourly and Daily at 360 × 640 dp, font scale 1.0. Inspect affected
   states at font scale 1.3, RTL, and Effects Off; record baseline clipping
   separately from regressions and document any environment-limited case.
5. Run focused checks, the broader repository check when the Android
   environment is available, workflow validation, `git diff --check`, and
   final diff/status inspection. Preserve command output and installed visual
   evidence in the cycle evidence directory.

## Acceptance criteria

- Under Simple layout, Hourly and Daily render reduced surfaces while
  remaining visibly named as Hourly and Daily and preserving the existing
  global navigation model.
- Standard and Simple show identical supplied forecast facts for matching
  selected location and window, including honest missing/partial states,
  provenance, and update information.
- Visible Hourly date and Earlier/Later controls and Daily Earlier/Later
  controls select the expected supplied windows; visible entries remain in
  chronological order and are not fabricated.
- Switching layouts preserves current global page, selected location and
  forecast, and selected hourly/daily window indices.
- Focused installed-flow evidence proves layout/window browsing causes no
  repository fetch, refresh, or cache operation.
- Installed compact captures show the Simple Hourly and Daily surfaces
  through the real application path. Font scale 1.3, RTL, and Effects Off
  affected states are inspected or recorded as unverified with exact reason.
- No provider, repository, cache, location, alert, domain, or presentation
  meaning is changed; no third forecast representation or global navigation
  destination is added.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/147-simple-forecast-surface/`.

- `verification.md`: exact changed boundary, commands/results, device or
  emulator/API details, selected location and forecast/window facts compared,
  request/refresh/cache counters, inspected layout/font/RTL/effects states,
  and known limitations.
- Focused instrumentation: exercise Hourly date selection and window
  navigation plus Daily window navigation in Simple; verify page identity,
  selected window, visible forecast facts, and unchanged weather-operation
  counters across layout changes. Reuse cycle 146 fixture/counter hooks where
  possible, extending only the focused observation hooks required.
- Focused UI/presentation checks: verify Simple rendering consumes the same
  typed entries, availability and chronology as Standard; verify visible
  control labels/semantics and no empty-window fabrication.
- Installed evidence: capture Simple Hourly and Daily at 360 × 640 dp and
  font scale 1.0 using the real app path. Review font scale 1.3, RTL, and
  Effects Off states at affected pages/controls, retaining screenshots and
  observations. A Compose preview or compile alone does not meet this visual
  criterion.
- Broader checks: `python scripts/dev.py check` when SDK/dependencies are
  available, `python scripts/dev.py contract`, `python scripts/dev.py
  workflow`, focused connected test, `git diff --check`, and final diff/status
  review. Record exact failures and environment limits without attributing
  unrelated baseline failures to this slice.

## Risks and assumptions

- The roadmap phrase “Simple Forecast choice/surface” is interpreted under
  the specification's fixed `Now → Hourly → Daily → Details` contract: Simple
  is a reduced rendering of the existing Hourly and Daily pages, not a fifth
  page or a merged Forecast destination. If implementation discovery shows
  that the intended behavior requires changing global page semantics, stop
  and document a roadmap/specification decision before coding that change.
- Existing production hourly/daily components already render six-hour and
  five-day supplied windows with visible controls; the Simple surface can
  reuse those semantic units and vary composition/density only.
- Cycle 146's layout selection is `rememberSaveable` activity state, not a
  durable preference. This cycle does not promote it to durable persistence.
- The known compact font-scale-1.3 Now clipping remains tracked by R6.2. This
  plan evaluates only new or worsened Simple Hourly/Daily clipping and control
  reachability.
- Device/emulator availability may limit installed review; if so, record the
  attempted commands and leave the installed visual exit criterion
  unverified.

## Out of scope

- Changing Now or Details composition, global page order/identity, adding a
  Forecast page, merging Hourly and Daily navigation, or adding nested
  horizontal paging.
- Durable layout preference storage, Settings information architecture, or
  any new preference persistence boundary.
- Provider/domain mapping, repository/network requests, cache, location,
  official alerts, units, provenance semantics, or meteorological meaning.
- Broad five-theme/layout screenshot matrices, release-wide accessibility
  closure, service-level TalkBack audit, unrelated compact/large-font fixes,
  and performance profiling.
- New weather data, forecasts, interpolation, horizon padding, or retired
  Atmosphere Deck elements.
