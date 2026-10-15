# Plan 194 — Correct RTL and Glass High contrast failures

Status: Completed
Cycle ID: 194-correct-rtl-glass-high-contrast
Roadmap item: SQ.9A-CORR
Created: 2026-10-14

## Objective

Trace and correct the two concrete SQ.9A failures recorded by Cycle 193, then
capture installed Pixel 3 XL evidence for the affected acceptance cells. Find
why Home content stays left-aligned under RTL across Now, Hourly, Daily, and
Details, then make the approved spatial alignment and directional art mirror
without reversing forecast chronology or Details semantic group order. Resolve
Glass High contrast to the already-approved opaque black/white palette on all
four pages, including the representative large-font Now case. The corrected
cells must pass before the separate SQ.9A acceptance outcome can close.

This is a production correction slice, not final SQ.9A acceptance. Cycle 193's
passing evidence remains available for unaffected cells; this plan makes no
claim that combining old and new evidence automatically passes the full
acceptance matrix.

## Authority and evidence inputs

- Product and UI invariants: `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `docs/theme-system/EFFECTS_CONTRACT.md`.
- Governing branch order: `docs/UI_CONTEXT_ROADMAP.md`, especially
  SQ.2, SQ.9, SQ.9A-CORR, and SQ.9A.
- Approved visual rules: `.codex/test-artifacts/184-sq2-four-page-static-full-source/source-review-matrix.md`.
  F3 requires RTL alignment/art direction to mirror while keeping forecast
  chronology earliest-to-latest and Details group order unchanged. F4 requires
  opaque black/white across all pages; F5 extends that treatment to Now at
  font scale 1.3.
- Failure evidence: `.codex/test-artifacts/193-accept-installed-four-page-static-full/acceptance-matrix.md`,
  paired captures/hierarchies for F3, F4, and F5, and Cycle 193's device and
  semantics records. All pages failed F3; all pages failed F4; Now failed F5.
- Device: physical Pixel 3 XL, the SQ.9A review device. The Pixel Fold
  performance gate is not part of this slice.

## Production boundary

Limit production changes to the existing Home composition and shared
presentation/theme resolution needed to correct:

1. app content direction/alignment for Glass Static Full in RTL on Now,
   Hourly, Daily, and Details; and
2. the Glass High contrast palette and any Glass Full surfaces that must
   become opaque black/white to satisfy the approved F4 rule.

Use the current installed failure evidence to identify the owning Compose
containers and semantic palette roles before editing. The approved Smoked
Quartz source permits white (`#FFFFFF`) foregrounds on opaque black (`#000000`)
or near-black (`#101010`) surfaces for High contrast. It requires removal of
translucency, texture behind text, gradients crossing text, and low-opacity
separators. Record which approved surface value is used for each affected
semantic role; do not retain the current Glass tint or infer new colors from
the screenshots. Prefer start/end-aware layout APIs and the existing Compose
layout-direction behavior after tracing the source of left alignment; do not
reverse actual forecast data, chronological order, or Details' semantic group
sequence. Keep the High contrast correction in the resolved appearance seam
and scope it to Glass so other theme palettes remain unchanged. Preserve
semantic roles for text, actions, outlines, precipitation, and selected
controls.

Focused production-path tests should update `ThemeResolverTest` and the
existing `ProductionRtlNavigationTest` or a narrowly scoped companion
instrumented test. The RTL test must explicitly select Glass Full and verify
the real Home path rather than passing only because Compose's default theme
is exercised. Add resolver assertions for the opaque Glass High contrast
roles and unchanged Glass Standard/non-Glass behavior. Do not broaden the
weather model, fixture, provider, preference, or request/cache boundaries.

## Functional invariants

- Keep global page order and identity `Now -> Hourly -> Daily -> Details`,
  outer-pager swipe ownership, chooser selection, Android Back, and visible
  page names.
- In RTL, mirror physical layout/alignment and decorative direction where
  directional; keep Hourly and Daily entries earliest-to-latest and retain
  Details' Conditions → Forecast pattern → Historical context semantic order.
- Preserve weather values, units, condition text, missing-data behavior,
  forecast membership/chronology, provenance, valid/update/freshness data,
  alert meaning, and the visible semantics of pages and controls.
- In Glass High contrast, use the approved opaque black/white treatment on the
  four pages and retain explicit labels/state meaning. Do not rely on color
  alone. Keep functional controls legible and usable.
- Preserve Glass Standard appearance and all non-Glass theme behavior. Keep
  None (`OFF`) opaque/static/complete and prevent Full decoration from leaking
  into None or Subtle.
- Do not change weather requests, cache behavior, navigation semantics,
  saved appearance preferences, or the persisted Glass theme identity.
- Maintain compact 360×640 dp behavior, font-scale 1.3 resilience for F5,
  and applicable 48dp control-target intent. Use installed rendering as the
  visual authority; previews alone do not establish success.

## Implementation steps

1. Read Cycle 193's failing captures and hierarchies alongside the exact
   SQ.2 F3/F4/F5 source rules. Trace how Android/resource direction reaches
   Home composition, any app-level direction override, page containers and
   children using physical left/right alignment or padding, and custom
   directional art. Separately trace Glass High contrast from the resolver's
   semantic roles to the surfaces/text rendered on each page. Record the
   observed cause and file/function path for each failure in `investigation.md`
   before changing code; do not assume that Compose's ambient direction is the
   cause. Stop and replan if the evidence shows a broader redesign or a source
   ambiguity. The contrast tokens and opacity treatment are already approved;
   no owner decision is pending.
2. Add focused regression assertions for the chosen layout-direction and
   palette fixes. Exercise the production Home path with Glass and Full
   explicitly selected and read back. Cover Now, Hourly, Daily, and Details
   direction/alignment, assert visible Hourly/Daily entry semantics remain
   earliest-to-latest and Details groups remain Conditions → Forecast pattern
   → Historical context, and assert Glass High contrast resolves the approved
   opaque roles. Assert Standard Glass and non-Glass palettes remain unchanged.
   Keep tests narrow to the two failures.
3. Implement the smallest coherent changes in the existing composition and
   resolver seams. Do not introduce a new visual system, change weather
   values, or modify fixture data to make screenshots look correct.
4. Run the focused JVM resolver test and focused Pixel 3 XL instrumentation
   tests for the real Glass Full Home path and RTL/page behavior. Before any
   device use, confirm the physical device is Pixel 3 XL (`crosshatch`), set
   screen timeout to disabled/0, and confirm Developer Options **Stay Awake**
   is selected. Record device/API/build identity, commands, and exact results.
   Use focused commands (adjust a class name only if a narrowly scoped
   companion class is needed, and record that change):

   ```sh
   ./gradlew --no-daemon :app:testDebugUnitTest \
     --tests com.oxygen.weather.ui.themeengine.ThemeResolverTest
   ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionRtlNavigationTest
   ./gradlew --no-daemon :app:connectedDebugAndroidTest \
     -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionGlassAppearanceCorrectionTest
   ```

   The companion test command is required only if a separate class is added;
   if both assertions fit in `ProductionRtlNavigationTest`, run that class
   once and record the exact command.
5. Build/install the real app on Pixel 3 XL and recapture the failed cells at
   compact 360×640 dp using the deterministic Demo Station fixture and
   Standard layout, Metric units, Glass Full:

   - F3: Android `ar-XB` with English retained as the second language, font
     scale 1.0, Standard contrast; capture Now, Hourly, Daily, and Details.
   - F4: `en-US`, font scale 1.0, High contrast; capture all four pages.
   - F5: `en-US`, font scale 1.3, High contrast; capture Now.

   Select Glass, Full, and contrast through the installed Appearance UI and
   read back the selected state; do not edit preference storage directly for
   installed evidence. Pair each screenshot with a same-state hierarchy or
   semantics dump and hash. On F3, exercise the page chooser, outer pager,
   Android Back, Hourly Earlier/Later/date selection, and Daily Earlier/Later.
   Confirm control labels retain their meaning and Earlier/Later actions
   continue to move through time in chronological order regardless of the
   mirrored layout. Confirm Details' visible and semantic group order remains
   Conditions → Forecast pattern → Historical context. Record any unavailable
   state as UNVERIFIED with the reason.
6. Compare each capture with the Cycle 193 failure and the approved SQ.2
   source. For RTL, verify page-specific start/end alignment and directional
   decoration mirror while the page sequence, Hourly/Daily earliest-to-latest
   entry order, and Details Conditions → Forecast pattern → Historical context
   order remain unchanged. For High contrast, inspect the semantic palette
   mapping for facts, metadata, precipitation, actions, outlines, and selected
   controls, plus representative opaque canvas/group pixels against the
   approved `#FFFFFF` foreground and `#000000`/`#101010` surfaces; confirm no
   translucent surface, text-crossing gradient, or texture remains behind
   critical text. This is a source-conformance check, not a new numeric
   contrast-audit cycle. Recheck F1 Standard LTR Full on all pages and the
   existing Glass None/Subtle behavior with focused regression coverage or
   targeted installed captures for any shared boundary the fix touches; do
   not expand into a full theme/effect Cartesian matrix.
7. Run the changed-boundary build, lint if Kotlin production sources change,
   contract and workflow checks, and `git diff --check`; inspect the final
   diff. Do not run `python scripts/dev.py test`,
   `python scripts/dev.py android-test`, or `python scripts/dev.py check` as
   full-suite substitutes. Full relevant suites remain in separately planned
   SQ.V.
8. Preserve the correction disposition and all evidence under
   `.codex/test-artifacts/194-correct-rtl-glass-high-contrast/`. Update the
   roadmap/history only when closing this cycle. SQ.9A acceptance remains a
   separate outcome and must explicitly reconcile the retained Cycle 193
   matrix with these corrected cells.

## Acceptance criteria

- The recorded source trace identifies why RTL direction/alignment fails and
  the production path corrected. Focused automated tests exercise Glass Full
  through the production Home path on all four pages, verify RTL layout
  direction/alignment, and preserve Hourly/Daily earliest-to-latest order plus
  Details Conditions → Forecast pattern → Historical context order.
- Focused automated tests prove Glass High contrast resolves to opaque
  black/white semantic treatment while Standard Glass and non-Glass theme
  behavior remain unchanged.
- Installed Pixel 3 XL captures for F3 pass on Now, Hourly, Daily, and Details:
  content/art direction mirrors, visible page/control identity remains, and
  chronology/group semantics stay in the required order.
- Installed Pixel 3 XL captures for F4 pass on all four pages and F5 passes
  on Now: critical text uses opaque white on approved black/near-black
  surfaces; critical text has no translucency, texture, or crossing gradient
  behind it; labels, controls, and weather facts remain legible and present.
- Focused tests prove that Glass High contrast applies opaque approved roles;
  Glass Standard and all non-Glass theme palettes remain unchanged. Any
  visible UI surface added by the corrected role mapping is opaque.
- Targeted LTR Standard Full and Glass None/Subtle regression review finds no
  unintended changes. Other themes remain unchanged by focused resolver and
  composition coverage.
- `ThemeResolverTest`, focused Pixel 3 XL instrumentation, `python
  scripts/dev.py build`, applicable `python scripts/dev.py lint`, `python
  scripts/dev.py contract`, `python scripts/dev.py workflow`, installed
  evidence, and `git diff --check` are recorded truthfully. Do not run the full
  suite in this implementation cycle. Any unavailable check or matrix cell
  has a specific reason.
- This cycle claims only correction completion. It does not mark SQ.9A
  accepted; a separate SQ.9A acceptance disposition must pass before SQ.10.

## Verification and evidence

Evidence root: `.codex/test-artifacts/194-correct-rtl-glass-high-contrast/`.
Retain:

- `investigation.md` with failed-cell-to-code-path mapping and approved rule;
- `device-config.md` with Pixel 3 XL (`crosshatch`) identity/API (expected
  Pixel 3 XL/API 31 unless the device reports otherwise), app/build, Demo
  Station fixture, timeout and Stay Awake state, 360×640 dp viewport,
  font scale, `ar-XB`/`en-US` locale and direction, Metric units, Standard
  layout, Glass theme, Full effects, and contrast for each capture group;
- focused JVM and Android test commands/results and relevant failure diffs;
- `screenshots/rtl-{now,hourly,daily,details}.png`,
  `screenshots/high-{now,hourly,daily,details}-1.0.png`, and
  `screenshots/high-now-1.3.png`, with paired files under `hierarchies/`;
- `sha256sums.txt` covering all final screenshots and hierarchy dumps;
- `correction-matrix.md` with F3/F4/F5 page-level PASS/FAIL/UNVERIFIED,
  actual observations, criterion/source links, interaction outcomes, and
  evidence filenames;
- targeted regression review and evidence for LTR Standard Full on all pages
  and for any None/Subtle shared boundary changed by the correction;
- build/lint/contract/workflow output, `git diff --check`, and final diff notes.

The cycle-specific path is new. Cycle 193's raw captures remain preserved as
the before-state and must not be copied over or edited.

## Risks and assumptions

- The evidence locates an app-wide RTL failure across all pages and a Glass
  High contrast color failure across all pages. The approved scope is Glass
  Static Full on those pages; do not broaden RTL redesign to other themes or
  routes unless source tracing proves that shared code change is necessary.
- The owner-approved packet resolves the color decision: `#FFFFFF` foreground
  on opaque `#000000` or `#101010` surfaces. Implementation must record its
  mapping of those approved tokens to semantic roles, remove the listed
  transparency/texture/crossing-gradient treatments under critical text, and
  preserve explicit non-color labels. No owner decision is currently pending.
- Installed screenshots establish rendering, not live request/cache
  invariance or service-level TalkBack behavior. No such claims are required
  by this correction slice.
- Snow has no installed fixture coverage in Cycle 193. Keep it UNVERIFIED in
  installed review unless the existing app path can produce it; do not invent
  a fixture or condition as part of this correction.
- If numeric contrast audit, new design authority, fixture changes, or broad
  layout restructuring is required to meet the approved packet, stop and
  create a revised bounded plan instead of expanding this one.

## Out of scope

- Final SQ.9A four-page Static Full acceptance closeout; SQ.10 performance;
  SQ.11 Subtle; SQ.12 None; SQ.13 appearance switching; SQ.14 interaction
  reconciliation; SQ.15–SQ.16 motion; SQ.17A/B layout/localization and
  appearance acceptance; SQ.18 motion performance; SQ.V full suites.
- Other themes' RTL or High contrast redesign, broad app-wide localization,
  new design/source decisions, typography or weather-content redesign,
  candidate SVG/bitmap/font assets, fixture/provider/weather/persistence
  changes, new navigation, and unrelated page composition work.
- Full connected/unit suites, performance testing, live request/cache
  instrumentation, service-level TalkBack traversal, Pixel Fold performance,
  and claims for unsupported installed conditions.

## Context estimate

Estimated workload: 30,000–45,000 combined tokens for source/evidence audit,
two focused production corrections, regression checks, nine affected-cell
Pixel 3 XL captures, targeted interaction/regression checks, and closeout.
This targets one independently verifiable slice and is expected to remain
within the 35–45% context target. If either failure requires a broader shared
layout/accessibility redesign or materially exceeds the 55% context planning
threshold, stop and split/replan before expanding beyond the two recorded
failure causes; 65% remains the hard maximum.
