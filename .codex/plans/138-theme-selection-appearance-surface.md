# Plan 138 — Theme selection Appearance surface

Status: Completed
Cycle ID: 138-theme-selection-appearance-surface
Roadmap item: R5.2A
Created: 2026-10-06
Reviewed: 2026-10-06

## Objective and independently observable outcome

Expose Atmospheric, Glass, Minimal OLED, Instrument, and Terminal in a focused,
accessible `Appearance` destination reachable from Home. A user can identify
the selected theme, select any catalog theme, see the production renderer
update immediately, and return to the same Home page. Selection continues
through cycle 137's Activity-owned preference boundary. Entering, leaving, or
using this destination does not perform forecast/official-alert work or alter
weather facts.

## Production boundary

Production changes are limited to the Home theme entry, one transient
Appearance route/surface in `OxygenWeatherApp`, and focused tests/documentation
needed to establish the route and invariants. Reuse `ThemeCatalog`,
`WeatherThemeId`, `selectedThemeId`, `onSelectTheme`, `resolveTheme`, and the
Activity-owned persistence from cycle 137. The existing resolved-theme path
remains the sole renderer input and preference path.

Treat Appearance as a transient destination, not a fifth Home page or a new
pager. Replace the current theme-selection dropdown with an entry to the
focused destination. Keep the theme entry discoverable from the existing Home
header. Preserve the opening Home page index and restore it on explicit return
and Android Back. Do not add the general Settings shell planned for R5.6.

## Authority, dependencies, and planning decisions

- `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` govern
  meaning, navigation, accessibility, and appearance invariants. R5.2A in
  `docs/ROADMAP.md` supplies the installed exit criterion.
- R5.2 is complete in cycle 137. Its Activity-owned `ThemePreferenceSelection`
  is authoritative for effective selection and durable stable theme IDs. No
  new persistence owner or preference format is in scope.
- Current entry point is `ThemePicker` in `OxygenWeatherApp.kt`. Current
  transient routes are location search and official-alert routes; the outer
  `HorizontalPager` owns Home page swipes. Keep route state in the existing UI
  boundary and do not introduce a second pager or a page-swipe gesture.
- Initial-inspection composition finding: `OxygenWeatherApp` renders
  `LocationSearchRoute` as a root replacement and returns from the
  `ProductionBackdrop` content before composing Home. In contrast, official
  alert selection/detail surfaces are drawn over the Home `Box`; Home remains
  composed underneath while its semantics are cleared. These are different
  composition patterns, not interchangeable examples.
- Route arbitration decision (preserved): while Appearance is open, show its
  surface in place of Home content and disable Home/alert interactions. Use
  the root replacement composition pattern used by location search, not the
  alert overlay pattern. Appearance, search, and alert routes are mutually
  exclusive: the Home-only entry is unavailable while another transient route
  is active, and route transitions must not leave two transient surfaces open.
  Android Back and the visible return control close Appearance first; Back
  dismissal must take precedence over alert dismissal and Home-page Back
  behavior. Search and alert routing otherwise retain their existing behavior.
  The pager remains at the page that opened Appearance and that page is
  restored on explicit return and Back.
- Because the exclusive root replacement disposes page content, the Hourly and
  Daily window indices must be retained above those page composables so opening
  Appearance does not reset the visible forecast window on return. This keeps
  the existing window-control state intact without adding theme state or a
  second navigation owner.
- Cycle 137 evidence reported six failures in existing connected tests and
  recorded that its no-operation fixture had no cache record. These are
  baseline limitations to compare against, not accepted failures for this
  slice. Reuse the established forecast/cache/alert hooks and add a non-empty
  cache snapshot only if it can be done within the existing fixture boundary;
  at minimum assert no cache reads/writes after the settled startup baseline.
- No owner decision is required to plan this slice. Actual-device availability
  is an execution-time condition, not a planning dependency.

## Functional invariants

- Theme selection changes resolved visual appearance only. Weather facts,
  canonical/cache values, selected location, provenance, valid/update times,
  freshness, alerts, chronology, missing values, and forecast meaning remain
  unchanged.
- Theme selection calls the existing Activity callback. All five options come
  from `ThemeCatalog`; selected state comes from `selectedThemeId`; resolved
  visuals come from `resolveTheme` and the existing production renderer.
- Opening/closing Appearance and selecting themes cause no forecast or alert
  request and no forecast-cache read/write. Normal startup work is measured
  separately after it reaches a terminal state.
- All four named Home pages, visible page identity, page-menu navigation,
  outer-pager swipe ownership, and existing search/alert behavior remain
  coherent. Returning restores the Home page that opened Appearance.
- The selected theme has visible name text and selected semantics; meaning is
  not conveyed by color alone. Every option and the return control provide at
  least 48dp interactive targets.
- The appearance destination remains usable at 360 × 640 dp, font scale 1.3,
  and RTL. RTL mirrors layout as appropriate without changing theme meaning or
  Home chronology. Effects Off remains opaque, static, and complete.
- Appearance is a focused theme-selection surface. No visual redesign or
  additional preference controls are included.

## Visual objective

Provide a clear, themed, readable surface where the selected theme is obvious
by name and selection semantics, then show that selection reflected by the
existing Home renderer. The destination must remain complete under Effects Off.

## Implementation steps

1. Inspect the exact header/ThemePicker, pager state, alert/search routing,
   Activity callback, catalog labels, and existing instrumentation fixtures.
   The initial inspection is complete: search is a root replacement, while
   alert surfaces overlay the composed Home tree. Preserve the route arbitration
   above by composing Appearance as an exclusive root replacement, matching
   search; do not implement it as an alert-style overlay. Confirm in the
   inspected code that its Home-only entry and Back precedence keep transient
   routes exclusive before beginning implementation. If those facts differ
   from this inspection, stop and update this plan before coding.
2. Implement the focused Appearance destination and Home entry. Render a
   visible `Appearance` identity, one single-choice control per catalog theme,
   visible selection, and a named return control. Render it in place of Home,
   outside the alert overlay branch, with one Appearance route state guarded
   against active search/alert routes; do not require replacing their existing
   state model. Save the opening pager index. Do not add a pager or separate
   selected-theme state. Route Back to Appearance dismissal before existing
   alert/Home Back behavior.
3. Wire selection to `onSelectTheme` without closing the destination, so the
   Activity owner updates the resolved production renderer immediately while
   selected semantics stay synchronized. Return restores the opening page.
4. Add focused Compose/instrumentation coverage for opening from Home,
   appearance identity, all five options and exactly-one-selected semantics,
   callback/renderer update, explicit return, Back, and preservation of each
   opening Home page. Cover route interactions so Appearance cannot overlap
   search or alert routes; confirm existing search/alert path tests remain
   intact.
5. Extend the production-path regression using the existing
   `ThemePreferenceApplicationFlowTest` patterns and hooks. After startup is
   settled, snapshot visible weather/provenance facts and forecast/cache/alert
   operation counters; open/close Appearance and select every theme; assert
   stable facts, selected preference/resolved theme, and unchanged counters.
   Do not add mutable production metrics. Reset every global hook in teardown.
6. Install and review the actual app at 360 × 640 dp, font scale 1.0, LTR,
   Effects Off. Open Appearance, capture its default selected state, select
   each theme and capture readable selected state plus the corresponding
   visible production renderer, then return to the same Home page. Review the
   Appearance surface at font scale 1.3 and RTL; verify no clipping, overlap,
   inaccessible controls, or altered meaning. Record Effects Off behavior and
   explicitly mark service-level TalkBack as unverified unless exercised.

## Acceptance criteria

- Home opens the focused Appearance destination; the page clearly identifies
  itself as `Appearance` and lists exactly the five themes from the catalog.
- Exactly the Activity-selected theme is visibly and semantically selected.
  Selecting every theme invokes the existing owner, immediately resolves the
  matching production theme, and retains cycle 137 persistence behavior.
- Every option and return control meets the 48dp target guidance and remains
  usable at the compact viewport and font scale 1.3. RTL remains usable and
  selected-state meaning is preserved. Effects Off is opaque, static, and
  complete.
- Explicit return and Android Back restore the Home page that opened the
  destination. Existing Home page identity, sole outer swipe ownership, page
  menu, location search, and official-alert routes remain valid.
- Installed evidence shows all five selections through the real Activity and
  renderer, plus return to the originating page. Evidence records build/app
  identity, device/emulator, viewport, density where available, font scale,
  layout direction, and effects level.
- Once ordinary startup reaches its terminal state, entering/leaving the
  route and selecting themes adds no forecast requests, alert requests, cache
  reads, or cache writes. Weather values and visible provenance/semantics stay
  invariant.
- Focused instrumentation, unit tests, source contract, and repository checks
  pass. Existing failures are compared with the cycle 137 baseline; any new
  failure or unrun installed/accessibility condition is reported exactly and
  is not represented as a pass.

## Verification and evidence

Focused automated verification:

- Add or extend
  `app/src/androidTest/java/com/oxygen/weather/ui/ThemeAppearanceApplicationFlowTest.kt`
  for route, selected semantics, Activity callback/renderer, return/Back,
  origin-page restoration, weather/provenance invariance, and zero incremental
  forecast/cache/alert operations.
- Run the focused Android instrumentation class during iteration with
  `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest`
  (use `gradlew.bat` on Windows).
- Run `python scripts/dev.py test` and `python scripts/dev.py android-test`;
  then run `python scripts/dev.py contract`, `python scripts/dev.py check`,
  `python scripts/dev.py workflow`, and `git diff --check`. `check` covers
  unit tests, lint, and debug assembly; `android-test` is the connected suite.
- Inspect the final diff and retain exact command results. Compare connected
  test failures with the six failures recorded by cycle 137 at
  `.codex/test-artifacts/137-persisted-theme-preference/full-connected-android-tests.xml`.

Retain evidence under
`.codex/test-artifacts/138-theme-selection-appearance-surface/`, including:

- focused instrumentation log and operation-counter/fact-invariance result;
- unit, connected, contract, check, workflow, and diff-check logs/results;
- installed screenshots named to identify state, for example
  `appearance-<theme>-360x640-fs1.0-ltr-effects-off.png`, and the corresponding
  visible Home renderer captures;
- at least one Appearance capture at font scale 1.3 and one RTL capture, with
  exact environment/build metadata in `verification.md`;
- explicit RTL, large-font, and service-level TalkBack outcomes. Do not use a
  Compose preview or compilation as proof of installed visual acceptance.

If no usable Android device/emulator or SDK is available, preserve the exact
command/output and leave installed visual acceptance open; do not claim the
R5.2A installed exit criterion passed.

## Risks and assumptions

- R5.6 later adds the general Settings information architecture. The focused
  route is intentionally temporary and should remain a reusable destination
  without pre-implementing that shell.
- Route state must not become stale when the app is recreated or a transient
  alert/search state changes. Keep the entry inaccessible outside Home and
  ensure Android Back consumes Appearance before Home pager behavior.
- Existing Activity integration tests use a deterministic no-selected-location
  fixture and count forecast/cache/alert hooks. Preserve normal startup
  behavior in production; establish operation baselines only after startup is
  settled. If a selected-location fixture is needed to demonstrate a fully
  rendered Home return, use the existing selected-location fixture path rather
  than changing production weather behavior.
- Cycle 137's six connected failures may still occur. The implementation must
  not broaden scope to repair unrelated location-search, composition/touch,
  forecast-flow, or forecast-context failures; document any newly introduced
  failure distinctly.
- RTL screenshot and service-level TalkBack execution depend on installed
  environment support. Their outcomes must be reported rather than assumed.

## Out of scope

- General Settings shell or destinations beyond focused theme selection.
- Contrast, effects, system reduced-motion, Simple layout, units, location,
  data-source, legal, privacy, license, or About controls.
- New themes, catalog/resolver behavior, visual redesign of existing themes,
  or changes to persisted theme IDs/storage.
- Any weather/provider/repository/cache behavior change, forecast refetch,
  altered weather value, changed provenance, or semantic reinterpretation.
- Additional page/swipe owner, nested pager, or change to the four-page Home
  contract.
- Service-level TalkBack audit beyond focused Compose semantics checks; record
  this boundary as unverified unless it is actually performed.
