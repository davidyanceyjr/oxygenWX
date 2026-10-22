# Plan 188 — Finish static Full Hourly

Status: Completed (resumed after device blocker cleared)
Cycle ID: 188-finish-static-full-hourly
Roadmap item: SQ.6
Created: 2026-10-14

## Objective

Complete the approved Smoked Quartz/Glass **Static Full Hourly** composition on
the installed Standard Home path. Present the selected six-entry window with
clear chronology and readable weather facts, with date selection and
Earlier/Later actions reachable and usable. Retain the approved static mineral
background from SQ.4 and leave the accepted Now treatment from SQ.5 unchanged.
This is an Hourly-page slice; it does not establish four-page Static Full
acceptance.

## Authority and dependencies

- SQ.4 completed in cycle 186. Its installed API 37 captures and capture
  manifest are the Hourly visual baseline under
  `.codex/test-artifacts/186-static-mineral-background-foundation/`.
- SQ.5 completed in cycle 187. Its installed Glass Full Now captures and
  changed-path audit are evidence of the accepted Now-only treatment under
  `.codex/test-artifacts/187-finish-static-full-now/`.
- Cycle 184 records owner approval of the exact SQ.2 design-source packet,
  including `resources/themes/smoked-quartz/SOURCE.md`; packet SHA-256:
  `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`.
  Follow its Hourly row. This is design-source approval only; runtime assets,
  installed acceptance, performance, and motion remain unapproved/unverified.
- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`, and SQ.6 in
  `docs/UI_CONTEXT_ROADMAP.md` govern meaning, presentation, and this slice.
- Roadmap status reconciliation: SQ.4 is recorded DONE by
  `.codex/history/2026-10-13-186-static-mineral-background-foundation.md`;
  SQ.5 is recorded DONE by
  `.codex/history/2026-10-14-187-finish-static-full-now.md`. The roadmap labels
  are corrected to match those records before this draft is selected.
- SQ.6 is eligible because SQ.5's required exit is recorded complete. SQ.5's
  installed evidence used AVD `oxygen_tp2b_api37` for captures, while cycle
  186's Hourly baseline used `oxygen_starter`; both are API 37 at the target
  viewport. SQ.5 changed Now only, so cycle 186 remains the before-state for
  Hourly. Do not compare its pixels to SQ.5 captures.

## Approved Hourly composition decisions

- Keep the existing compact two-column, three-row arrangement, capped at the
  six actual entries in the selected window. Use open rows inside one shared
  grouping surface for the Glass Full Hourly list; do not retain a separate
  bordered/card surface around each entry. Keep the existing date chooser and
  Earlier/Later controls and callbacks; do not redesign date navigation.
- Keep the range title and date chooser before the entries, and Earlier/Later
  after them. The page scrolls vertically, so controls must remain reachable
  at font scale 1.3 even when they fall below the initial viewport.
- Preserve the SQ.4 broad, fine horizontal strata and sparse grain behind the
  list. Strata must not align with individual rows or imply timing or amount.
  Omit condition marks from the Glass Full Hourly composition, removing them
  from this branch if currently rendered. The source makes them optional and
  SQ.9 owns the later decorative-mark acceptance. Visible condition text and
  entry semantics remain authoritative.
- Use only the source-approved restrained static Full surface/depth treatment.
  Add no motion, separate overlay, glow, or new weather-dependent encoding.

## Production boundary

Change only the Glass Effects Full Hourly composition and focused coverage:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` — the
  Glass/Full branch in `HourlyPage`, if needed for Hourly-only composition.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`
  — `ProductionHourlyEntry` styling only if the approved page treatment
  requires a focused component adjustment. Avoid changing shared behavior for
  other themes, effects levels, or pages.
- `app/src/androidTest/java/com/oxygen/weather/ui/ProductionHomeCompositionTest.kt`
  and, only if needed, a focused relevant test file for the Hourly hierarchy,
  controls, and semantics.
- Evidence root: `.codex/test-artifacts/188-finish-static-full-hourly/`.

No forecast mapping, data model, request/cache, preference, or navigation
changes are included.

## Functional invariants

- Preserve the global `Now -> Hourly -> Daily -> Details` order and the outer
  Home pager as the sole horizontal-swipe owner. Hourly rows and window
  controls do not introduce a nested horizontal pager or swipe navigation.
- Each selected window shows only the represented chronological entries, up to
  six. Do not pad, repeat, reorder, interpolate, or invent forecast entries.
  Preserve partial-horizon and unavailable states honestly.
- Keep visible date selection and Earlier/Later controls connected to their
  existing window callbacks. Keep page identity, selected date/window,
  disabled-control meaning, and semantic chronology accurate.
- Preserve each entry's supplied local time, condition, temperature or
  unavailable state, and precipitation probability when available. Keep
  condition text meaningful without decorative marks; preserve all existing
  semantics and at-least-48dp control targets.
- Appearance changes preserve provenance, valid/update time, freshness,
  weather meaning, cache state, and forecast/alert request behavior.
- Effects None stays opaque, static, and complete; Subtle and all non-Glass,
  non-Full, and non-Hourly paths retain their established presentation.
  Reduced motion cannot remove information. High contrast remains readable and
  does not rely on translucency or texture behind text.
- RTL may mirror spatial ornament only. It must not reverse chronological
  order or Earlier/Later meaning.

## Implementation steps

1. Review the approved SQ.2 Hourly recipe and owner-decision record, the cycle
   186 Hourly captures/XML and manifest, and cycle 187's Now-only changed-path
   audit. Record the baseline and the composition decisions above in
   `baseline-review.md`; do not add alternatives or invent a new art direction.
2. Inspect the actual Glass Full `HourlyPage` and entry rendering. Apply the
   shared-list composition only to Glass + Full + Hourly. If the entry
   component needs a variant, make it opt-in at this call site so its existing
   behavior for other themes, pages, and effects levels remains unchanged.
   Preserve the SQ.4 background, actual six-entry window, date chooser,
   callbacks, provenance, and all other theme/page paths. Omit condition
   marks, motion, overlays, and weather-dependent visual encoding.
3. Add a focused production-flow assertion if existing tests do not prove the
   Glass Full Hourly visible window, all supplied entry facts/semantics,
   window switching, and unchanged chronology. Reuse existing presentation
   fixtures and callbacks; do not change forecast data or introduce a
   screenshot-only fixture.
4. The owner explicitly requested the connected Pixel Fold instead of an emulator.
   Use the physical device and do not launch an AVD. At activation, ADB listed
   only a Pixel 3 XL (API 31). After the initial blocked close, the owner
   reconnected the Fold and the remaining capture matrix was completed on serial
   `37151FDHS0007R` (Pixel Fold / `felix`, Android 17/API 37). The Fold was in
   `CLOSED` state, so compact review used its cover display with a 360×640dp
   logical size override; the unfolded inner display remains unreviewed. Set
   Glass Full, Standard contrast,
   Standard layout, Demo Station, US units, LTR, and font scale 1.0 for the
   baseline comparison. Capture the first and a noninitial
   Hourly window at the compact baseline and at font scale 1.3. Also capture
   the first window in
   RTL at font scale 1.0 and in High contrast at font scale 1.0. Inspect entry
   order, wrapping, scroll reachability, date selection, Earlier/Later
   affordance and state, visible page identity, source/freshness, and absence
   of overlap or clipping. Spot-check the initial Hourly window at Glass None
   and Subtle to detect leakage from the Full-only treatment. Record actual
   installed viewport and all fixture/theme/effects/contrast/font/direction
   settings. Keep units and fixture consistent with cycle 186 when comparing
   baseline captures; record any unavoidable difference.
5. Compare same-state Full Standard captures against cycle 186 at actual size.
   Inspect the focused test result, changed-path diff, and all functional
   invariants. Run required focused tests, build, contract, applicable lint,
   workflow, and `git diff --check`; retain logs, manifest, screenshot/XML
   pairs, and review notes under the cycle evidence path.

## Acceptance criteria

- Installed Glass Full Hourly follows the SQ.2 Hourly composition and the
  SQ.4 background remains intact. The six-entry grid uses open rows in one
  shared grouping surface, without individual entry cards or condition marks.
  All visible facts and controls are legible; strata do not encode weather,
  timing, or amounts or compete with text.
- Compact installed review covers the first and changed noninitial window at
  360×640dp, Standard contrast, LTR, font scale 1.0. The same window states
  are reviewed at font scale 1.3; long content may scroll vertically, with no
  clipped critical facts or controls. A first-window RTL capture preserves
  earliest-to-latest meaning and Earlier/Later semantics; a High contrast
  capture remains opaque and readable.
- The actual presented entries remain chronological and correspond to their
  selected window. Six is the maximum per complete window; a short or partial
  window remains short. Earlier/Later, date selection, disabled states, and
  accessibility semantics operate as before.
- Focused automated checks prove supplied fact visibility, chronology and
  window navigation for the production path, or accurately record a concrete
  failure/blocker without calling the affected criterion passed.
- Glass None/Subtle spot checks show no Full-only treatment leak. Existing
  outer-pager ownership, RTL meaning, forecast/alert request counts, cache,
  provenance, and non-Glass paths remain unchanged.
- Required build/contract/lint/workflow/diff checks and focused tests pass, or
  exact failures and unverified boundaries are recorded. Evidence is complete
  enough for review under the cycle-specific artifact directory.

## Verification and evidence

- Focused connected test: add and run a narrowly scoped method in
  `ProductionHomeCompositionTest` for Glass Full Hourly if the existing
  `nowAndHourlyPreserveSuppliedFactsAndWindowControlsInNormalApp` does not
  exercise Glass Full. Run that existing method as the baseline for facts,
  chronological order, date selection, and Earlier/Later callbacks. Reuse
  focused `ProductionForecastComponentsTest` assertions only if component
  styling changes require direct regression coverage. Record exact selectors
  and results; do not run a whole class or suite unless the selector cannot be
  isolated, in which case document the narrowest available scope.
- Run `python scripts/dev.py build`, `python scripts/dev.py contract`,
  `python scripts/dev.py workflow`, applicable focused lint when production
  Kotlin changes, and `git diff --check`. Do not run `python scripts/dev.py
  check`, the full unit suite, or the full connected suite here; SQ.V remains a
  separate planned verification session.
- Capture PNG/XML pairs from the installed production Activity for Glass Full
  Standard LTR at font scale 1.0 and 1.3, each at the first and a changed
  noninitial window; Glass Full RTL and High contrast at the first window and
  font scale 1.0; and Glass None/Subtle Standard LTR at the first window and
  font scale 1.0 as leakage checks. Record actual viewport, API/device/app
  identity, Demo Station fixture, selected page/window, contrast, effects,
  font scale, locale/direction, scroll positions, and file hashes.
- Review compact first/later window captures at actual size and compare the
  Full baseline with cycle 186. Record focused test commands, outputs,
  screenshot/XML manifest, visual review, changed-path audit, and all
  unverified conditions under
  `.codex/test-artifacts/188-finish-static-full-hourly/`. No keystore or other
  secret evidence applies.
- No other-device or Pixel Fold performance pass is claimed. SQ.10 owns the
  formal static performance gate; SQ.9 owns four-page Static Full acceptance.

## Risks and assumptions

- SQ.5 changes the Now composition only, so the SQ.4 Hourly capture is the
  appropriate before-state. Confirm there are no intervening Hourly changes
  affecting the baseline before implementation.
- `HourlyPage` currently owns layout, date menu, rows, controls, partial
  horizon messaging, and provenance. A global `ProductionHourlyEntry` change
  could leak across themes or layouts; keep styling local unless focused
  evidence shows a component change is necessary.
- Compact two-column layout can constrain the fixed entry content. Preserve
  vertical scrolling and readable text at both font scales; do not shrink
  critical text to force all entries into one viewport.
- Current tests already cover supplied Hourly facts and window controls in the
  normal app. They may not prove the Glass Full visual grouping or installed
  change-window state; add only the missing focused assertion.
- The Home page is vertically scrollable and window controls follow the
  entries. Large-font and changed-window states may place controls below the
  initial viewport; capture scroll positions and verify reachability instead
  of treating below-fold content as missing.
- The owner requested physical Pixel Fold installed evidence instead of an
  emulator. At activation ADB exposed only a Pixel 3 XL on API 31. The owner later reconnected the Pixel Fold, and the remaining evidence was captured there without launching an emulator. The Fold was in its `CLOSED` state; the compact target was reviewed on its cover display using a 360x640 dp logical size override. The unfolded inner display remains outside this capture set. Locale/font controls and fixture state
  may differ from prior evidence; record actual states and unavailable
  conditions, and do not substitute previews for installed acceptance.

## Context budget

Execution target: approximately 35–45% of the available context, including
the approved Hourly source row, SQ.4/SQ.5 baseline review, a narrowly scoped
Hourly composition edit, focused test output, installed capture review, and
closeout. Keep logs and screenshots summarized by manifest; do not load full
suite output. If the task appears to exceed 55%, split before expanding the
production boundary or visual matrix.

## Out of scope

- Daily or Details Static Full composition and four-page Static Full
  acceptance (SQ.7–SQ.9).
- Static performance measurement (SQ.10), Subtle/None derivation (SQ.11–SQ.12),
  appearance switching (SQ.13), cross-page interaction acceptance (SQ.14),
  motion gate or motion implementation (SQ.15–SQ.16/SQ.18), broader
  layout/localization and appearance acceptance (SQ.17A/SQ.17B), and full
  suite verification (SQ.V).
- Changes to the forecast/presentation model, provider, requests, cache,
  preferences, page navigation, non-Glass themes, other effects levels, or
  official alert behavior.
- Any release hardening work from R7 or renewed planning of deferred UI.8–UI.17
  before the Smoked Quartz branch closes.
