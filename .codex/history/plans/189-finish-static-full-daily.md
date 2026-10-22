# Plan 189 — Finish static Full Daily

Status: Completed
Cycle ID: 189-finish-static-full-daily
Roadmap item: SQ.7
Created: 2026-10-14

## Objective and observable outcome

Complete the approved Smoked Quartz / Glass **Static Full Daily** composition on
the installed Standard Home path. Present up to five actual chronological
forecast days in the existing window with the supplied date, condition,
low/high, and precipitation facts. Use the approved terraced mineral depth
behind one shared surface containing the Daily rows. Keep the visible range
and Earlier/Later controls usable. This slice accepts Daily only;
it does not establish four-page Static Full acceptance.

## Authority and dependencies

- SQ.6 completed in cycle 188; its closeout and installed evidence are
  `.codex/history/2026-10-14-188-finish-static-full-hourly.md` and
  `.codex/test-artifacts/188-finish-static-full-hourly/`.
- Cycle 184 records owner approval of the exact SQ.2 source packet. Its
  `resources/themes/smoked-quartz/SOURCE.md` Daily recipe governs this slice;
  packet SHA-256 is
  `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`.
- Cycle 186's Daily captures and manifest under
  `.codex/test-artifacts/186-static-mineral-background-foundation/` are the
  same-fixture base-background comparison. Inspect them before editing. Cycle
  188 changed Hourly composition only and does not replace that Daily baseline.
- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`, and SQ.7 in
  `docs/UI_CONTEXT_ROADMAP.md` govern product meaning, presentation, and scope.
- SQ.7 is eligible because SQ.6 is recorded complete. The SQ.4 page-specific
  mineral background remains the base; this cycle changes only the Daily
  composition above it.

## Production boundary

Change only the Glass + Full + Daily composition and focused coverage:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` — the
  Glass/Full branch in `DailyPage`, if a page-local grouping/layout change is
  needed.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`
  — `ProductionDailyRow` styling only if required by the approved Daily
  treatment; keep any variant opt-in at the Glass Full call site.
- `app/src/androidTest/java/com/oxygen/weather/ui/ProductionHomeCompositionTest.kt`
  and, only if needed, the focused Daily test in
  `app/src/androidTest/java/com/oxygen/weather/ui/ProductionDailyDetailsCompositionTest.kt`.
- Evidence root: `.codex/test-artifacts/189-finish-static-full-daily/`.

No forecast/presentation mapping, provider, request/cache, preference, or
navigation change is in scope. Existing workspace changes from the completed
cycle 188 must be preserved and audited separately from this cycle's diff.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page identity, the outer
  Home pager as the sole horizontal-swipe owner, and existing title chooser
  and Back behavior. Daily gains no nested horizontal pager or row swipe.
- Show no more than five actual entries from the selected Daily window. Keep
  earliest-to-latest order; never pad, repeat, interpolate, reorder, or invent
  forecast days. Short horizons and unavailable values remain honest.
- Preserve each supplied date, condition/unavailable state, low/high or
  unavailable state, precipitation meaning when available, visible range,
  Earlier/Later actions and disabled states, and accessibility summaries.
  Keep controls at least 48dp where applicable.
- Preserve source, update time, freshness, provenance, cache state, forecast
  and alert request behavior, and weather meaning. Appearance cannot refetch
  or rewrite forecast data.
- Effects None remains opaque, static, and complete. Subtle and all non-Glass,
  non-Full, non-Daily paths retain their established rendering. High contrast
  uses opaque readable surfaces. Reduced motion preserves the same facts.
- RTL may mirror layout and decoration but must preserve chronological order
  and the text meaning of Earlier/Later. Long content and font scale 1.3 may
  continue by vertical scrolling; no critical fact or control is clipped.
- Terraces and texture remain broad and shallow; they do not align with row
  boundaries or encode temperature, precipitation, or chronology. Condition
  marks are not needed for this slice; visible condition text remains primary.

## Implementation steps

1. Review the approved SQ.2 Daily row and the cycle 186 Daily capture/XML,
   manifest, and background recipe. Record the actual baseline state and the
   accepted composition decisions in `baseline-review.md`; do not reopen the
   art direction or create a new weather-dependent visual encoding.
2. Inspect `DailyPage` and `ProductionDailyRow`. Apply the smallest
   Glass + Full + Daily-only composition: place the existing five-day row
   hierarchy inside one restrained shared surface. Keep individual rows open
   within that group; do not enclose them in separate bubble-like cards or
   button-style outlines. Existing button/control styling may remain. Retain
   the SQ.4 terraced mineral background. Use no motion, inclusion overlay, or
   decorative condition marks in this slice. Keep other themes, effects
   levels, pages, and layout presets stable.
3. Review existing `ProductionDailyDetailsCompositionTest` and
   `ProductionHomeCompositionTest` coverage. Add only a focused production-path
   assertion needed to prove Glass Full Daily row grouping, supplied facts,
   chronology, visible window selection, and Earlier/Later behavior. Do not
   alter a fixture or presentation model to improve a screenshot.
4. Run the directly relevant connected test on the Pixel 3 XL. Before device
   use, set screen timeout to 0/disabled and confirm Developer Options
   **Stay Awake** is selected. Install the production Activity on that device
   and capture first and changed-window Daily states at 360×640dp, font scales
   1.0 and 1.3; include the first window in RTL and High contrast at font scale
   1.0, plus Effects None/Subtle first-window leakage checks. Record the actual
   device/API/viewport, fixture, theme/effects/layout, locale, contrast,
   direction, font scale, scroll position, and build identity. If the exact
   compact viewport cannot be produced, record the measured viewport and leave
   the criterion unclaimed.
5. Compare the same-state Glass Full Standard captures against cycle 186's
   Daily baseline at actual size. Review facts, row order, control visibility
   and reachability, page identity, source/freshness, text contrast, clipping,
   and absence of per-row encoding. Audit the final changed paths and run the
   focused test, `python scripts/dev.py build`, `python scripts/dev.py
   contract`, `python scripts/dev.py workflow`, applicable focused lint for
   production Kotlin changes, and `git diff --check`. Preserve evidence and
   exact outcomes under the cycle evidence root.

## Acceptance criteria

- Installed Glass Full Daily follows the approved SQ.2 hierarchy: up to five
  actual chronological days show date, condition, low/high/unavailable, and
  precipitation meaning when supplied. One restrained shared surface groups
  the list; individual rows have no bubble-like data cards or button-style
  outlines. The terraced background remains shallow and does not compete with
  text or encode weather values.
- Compact Standard LTR captures cover the first and a changed noninitial
  window at 360×640dp and font scale 1.0, then both windows at font scale 1.3.
  Facts and Earlier/Later controls remain visible or vertically reachable,
  with no clipped critical content. A first-window RTL capture preserves
  earliest-to-latest order and Earlier/Later meaning; a High contrast capture
  stays opaque and readable.
- Effects None/Subtle spot checks show no Full-only composition leak.
  Non-Glass, non-Full, non-Daily behavior and the SQ.4 background remain
  unchanged.
- Focused production-path assertions prove the selected window's exact
  supplied rows, chronology, semantics, and window-control behavior. Any
  failed assertion or unproduced required state is recorded accurately and
  does not pass the affected criterion.
- Focused test, build, contract, workflow, applicable lint, and diff checks
  pass, or exact failures and unverified boundaries are recorded. Installed
  evidence is reviewable under the cycle evidence root.

## Verification and evidence

- Run the narrowest connected selector covering the Daily production path on
  the Pixel 3 XL, including existing
  `ProductionHomeCompositionTest#fourPageForecastRowsStayChronologicalAndDetailsKeepGroupIdentity`
  and the relevant Daily window test in
  `ProductionDailyDetailsCompositionTest#dailyWindowsKeepExactSuppliedRowsBoundsAndThemeIndependentState`.
  Add and run one Glass Full Daily focused selector only if existing coverage
  does not exercise that path. Record exact commands and results; do not run a
  whole class or suite when method-level selection is supported.
- Run `python scripts/dev.py build`, `python scripts/dev.py contract`,
  `python scripts/dev.py workflow`, focused lint if production Kotlin changes,
  and `git diff --check`. Do not run `python scripts/dev.py check`, the full
  unit suite, or the full connected suite; SQ.V owns that separately planned
  verification session.
- Capture installed PNG/XML pairs for Glass Full Standard LTR at font scale
  1.0 and 1.3 for first and changed windows; Glass Full RTL and High contrast
  first window at font scale 1.0; and Glass None/Subtle Standard LTR first
  window at font scale 1.0. Capture scroll-end state when controls or rows
  continue below the initial viewport. Record capture hashes and configuration
  in a manifest.
- Preserve baseline review, focused logs, build/contract/workflow/lint/diff
  results, capture manifest, PNG/XML pairs, visual review notes, and changed-
  path audit under `.codex/test-artifacts/189-finish-static-full-daily/`.
- This is a static Daily acceptance slice only. Do not claim formal performance
  acceptance (SQ.10), four-page Static Full acceptance (SQ.9), Fold inner
  display acceptance, TalkBack traversal, or full-suite verification.

## Risks and assumptions

- Daily row grouping/layout is owned primarily by `DailyPage`; a shared row
  component change could leak to other themes or effects levels. Keep any
  presentation option local to the Glass Full call site unless inspection
  proves an isolated component variant is required. The owner selected one
  shared list surface; no further design choice is pending on open rows versus
  per-row surfaces.
- Cycle 186 is the Daily visual baseline; cycle 188's Hourly-only change does
  not alter that state. Confirm the manifest's fixture and viewport before
  pixel/visual comparison.
- The connected-test and installed-check device for this implementation slice
  is the Pixel 3 XL per repository testing cadence. Recent Pixel Fold evidence
  from cycle 188 does not replace this slice's required Pixel 3 XL evidence.
- At font scale 1.3, controls may fall below the first viewport. Verify scroll
  reachability and record scroll state instead of shrinking critical text.
- Existing tests may prove chronology and visible content without proving the
  Glass Full grouping or actual changed-window action. Add only the missing
  assertion; do not widen into general cross-theme acceptance.
- The approved source's palette samples are not measured contrast evidence.
  Judge the installed rendering against the existing contrast/effects policy;
  do not treat the proposal values as validated tokens.

## Context budget

Execution target: approximately 35–45% of available context, including source
and baseline review, a page-local composition edit, focused Daily test output,
the bounded installed capture matrix, and closeout. Summarize logs through
manifests rather than loading full suite output. If expected execution grows
past 55%, split the work before expanding implementation or the visual matrix;
65% is the hard maximum.

## Out of scope

- Now, Hourly, Details composition or four-page Static Full acceptance
  (SQ.5–SQ.6 and SQ.8–SQ.9); SQ.9 remains the later condition-mark and
  four-page acceptance boundary.
- Static performance measurement (SQ.10), Subtle/None derivation (SQ.11–SQ.12),
  appearance switching (SQ.13), cross-page interaction acceptance (SQ.14),
  motion gate/implementation (SQ.15–SQ.16/SQ.18), broader layout/localization
  acceptance (SQ.17A/SQ.17B), or full-suite verification (SQ.V).
- Provider/domain/presentation mapping, weather meaning, requests, cache,
  preferences, page navigation, non-Glass themes, other effects levels, or
  official alert behavior.
- Release hardening from R7 or deferred UI.8–UI.17 work before the Smoked
  Quartz branch closes.
