# Plan 085 — Now and Hourly normal-app compositions

Status: Completed
Cycle ID: 085-tp3a-now-hourly-compositions
Roadmap item: TP.3A
Created: 2026-09-30
Reviewed: 2026-09-30

## Objective and observable outcome

Migrate the approved Now and Hourly page compositions into the installed normal
Home renderer. Both pages must work with Atmospheric, Glass, Minimal OLED,
Instrument, and Terminal while preserving typed weather meaning and the existing
four-page navigation contract. Produce exactly ten installed baseline captures
(two pages × five themes) at 393 × 852 dp.

The outcome is composition and functional readiness for TP.3A. It is not a
visual-parity or visual-acceptance result; those gates belong to TP.3C.

## Authority, dependencies, and assumptions

- Roadmap authority: `docs/theme-pack-roadmap.md`, TP.3 and TP.3A. The
  dependency is TP.2 closure; the roadmap records TP.2E closure in cycle 084
  and the earlier TP.2 component-family gates as complete.
- Product and architecture authority: `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and typed Home presentation/load
  state contracts.
- Composition authority: `docs/theme-system/design-pack/NOW.md`,
  `HOURLY.md`, `FOUNDATION.md`, `INTEGRATED_PACK.md`, and
  `REFERENCE_MEASUREMENT_METHOD.md`. Apply integrated refinements where those
  records supersede nominal values. Preserve their proposed layout decisions
  within the accepted product contracts; TP.3C remains the measured comparison
  and correction gate.
- Shared-component boundary: TP.2 production components consume resolved
  appearance, typed presentation values, and semantic callbacks. The debug
  showcase is not the production acceptance surface.
- Capture-effects assumption: use the per-theme primary render's effective
  effects setting (Atmospheric/Glass/Instrument Subtle; Minimal OLED/Terminal
  Off as identified by this draft and design-pack render metadata). Record the
  effective value for every capture. If checked-in primary render metadata
  disagrees, follow that metadata and record the correction in the evidence
  manifest; no owner decision is needed for this routine source-of-truth check.
- The workspace may lack an available Android device/emulator. If so, record
  the exact failed/unavailable installed gate and stop the cycle as BLOCKED;
  a preview or component showcase cannot substitute for TP.3A installed
  evidence.

No owner input is currently required. Do not infer approval for visual parity,
compact/large-font/RTL closure, Effects Off beyond the per-theme baseline, or
TalkBack service-level verification.

## Production boundary

Normal-app Compose composition for Now and Hourly only, in the existing
`OxygenWeatherApp` production presentation path. This includes page-specific
layout, use of TP.2 shared components, page/window/date callbacks, and only the
focused assertions or test hooks needed to verify these pages through the
normal app. It includes the ten required installed captures and their evidence
record.

Do not change provider, repository, cache, persistence, canonical or typed
weather values, fetch behavior, theme resolver/catalog, or global navigation
architecture. Keep the existing named selector and outer pager as the only
global horizontal-swipe owner.

## Functional invariants

- Keep global page identity and order `Now -> Hourly -> Daily -> Details`.
  Preserve page selection, static-tap behavior, outer-pager ownership, and Back
  behavior. Daily and Details implementations remain outside this cycle.
- Render only supplied typed presentation/load-state values. Preserve units,
  chronology, missing-field behavior, condition identity, source/update/status,
  and accessibility meaning. Never parse display strings to recreate values or
  invent values, entries, provenance, alert content, or time context.
- Now gives temperature and condition the strongest hierarchy; includes only
  the approved supplied current/supporting fields and source/update/status;
  optional pattern content appears only when complete supplied data allows it.
  Do not add a forecast preview, chart, gauge, UV/AQI, advisory, or alert slot.
- Hourly shows up to six actual entries from the selected window in supplied
  chronological order, in the approved two-column/three-row composition when
  space permits. Sparse windows remain sparse. Each entry retains local time,
  condition, temperature or its supplied unavailable wording, and precipitation
  when available.
- Hourly Earlier/Later moves exactly one actual window, remains visibly named,
  and is disabled at the bounds. Each represented date jumps to its supplied
  first window. Date choices come only from supplied valid jumps. Do not add a
  nested horizontal pager or infer dates from formatted strings.
- Theme affects appearance only: it cannot change content, callbacks,
  provenance, selection semantics, or request behavior. Use the resolved theme
  and shared TP.2 component contracts. Marks and backgrounds are decorative.
- Important facts remain visible and semantically meaningful. Applicable
  controls meet the 48 dp minimum. Respect dynamic system insets and vertical
  content growth. Effects Off remains opaque, static, and complete wherever it
  is the baseline effective setting.

## Visual objective and environment constraints

Implement the ordered compositions and theme treatment in the approved Now and
Hourly records, including integrated refinements. The baseline capture matrix
is exactly:

| Page | Themes | Viewport | Font scale | Direction | Contrast | Effects |
| --- | --- | --- | --- | --- | --- | --- |
| Now, Hourly | Atmospheric, Glass, Minimal OLED, Instrument, Terminal | 393 × 852 dp | 1.0 | LTR | Standard | Each theme's primary render effective setting; record exact value |

Use the installed normal app at the actual viewport and system insets. Verify
theme and page identity for every image. Long text, sparse input, 360 × 640 dp,
font scale 1.3, RTL, High contrast, a full Effects Off matrix, and service-level
TalkBack checks are not TP.3A acceptance conditions; preserve all functional
invariants and report observed blockers, but leave their systematic coverage
to the dependent roadmap gates.

## Implementation steps

1. Trace the normal Home path, current Now/Hourly models, TP.2 shared component
   APIs, design-pack integrated refinements, and existing installed-test/
   screenshot harness. Record code/reference mismatches before editing.
2. Implement the Now composition from the approved page record. Remove or
   replace any current composition content that conflicts with the record (for
   example, an unapproved next-hours preview); render supplied current facts,
   optional complete pattern group, and source/update/status in the specified
   order. Keep the four-name selector and global callbacks intact.
3. Implement the Hourly composition from its approved record: supplied range,
   represented-date control, row-major six-entry grid with sparse behavior,
   one-window Earlier/Later controls, and supplied source/update/status. Keep
   all content vertically reachable and avoid nested horizontal gestures.
4. Add/update focused automated checks using the normal app presentation path.
   Assert Now's supplied primary/support/source/update/status text, honest
   missing values, and optional-content behavior. Assert Hourly chronological
   entries, sparse/missing values, date jump target, exact one-window Earlier/
   Later changes and boundary states, named page identity, semantic selected/
   disabled states, and that theme changes preserve content/callback state.
   Do not count showcase-only assertions as normal-app coverage.
5. Run the focused checks and source contract. Install and launch the debug
   normal app on a device/emulator configured to 393 × 852 dp, font scale 1.0,
   LTR, Standard contrast. For each of five themes, capture Now and Hourly from
   the real normal app, selecting the primary effective effects setting in the
   matrix. Exercise visible page/date/window controls during functional review.
6. Inspect all ten images for correct page/theme, successful nonblank render,
   visible required content/control presence and reachability, and obvious
   readability or clipping blockers. This review is limited to functional
   readiness and baseline rendering; do not claim reference parity. Retain the
   screenshots, manifest, exact command outputs, and concise findings.
7. Run broader verification, `git diff --check`, and inspect the final diff.
   At cycle close, report passed and unavailable checks precisely. TP.3A may
   close PASS only when all acceptance criteria pass; missing/failed installed
   evidence or a blocking functional/readability issue closes BLOCKED and stops
   the dependent TP.3 chain.

## Acceptance criteria

- Only Now and Hourly normal-app composition changes are in the production
  boundary; existing Daily/Details behavior is not migrated as part of this
  cycle.
- Focused automated checks pass for the Now and Hourly assertions in step 4.
  The checks exercise the production Home path and prove the supplied data and
  semantics, including sparse/unavailable states and Hourly controls.
- Existing Home contract/source checks pass: there remains one outer
  `HorizontalPager`, named global page identity, and no regression to page,
  window, date-jump, or Back semantics. No theme action causes a weather fetch
  or changes the selected forecast data.
- Exactly ten installed normal-app PNGs exist: Now and Hourly for each of the
  five themes at 393 × 852 dp. Every capture has correct page/theme identity,
  no crash/blank output, and all expected controls/content visible or reachable.
  Any functional or readability blocker prevents PASS.
- `python scripts/dev.py test`, `python scripts/dev.py android-test`,
  `python scripts/dev.py contract`, `python scripts/dev.py check`, and
  `git diff --check` results are recorded. `check` includes unit tests, lint,
  and debug assembly. A command already subsumed by a later command may be run
  once, but its exact covered task/result must be identified. If Android
  instrumentation/device support is unavailable, record the reason and do not
  claim installed acceptance or close TP.3A PASS.
- Evidence contains actual installed captures and manifest metadata; previews
  and TP.2 showcase renders are not substitutes. No visual-parity claim is
  made; baseline comparison/correction is reserved for TP.3C.

## Verification and evidence

Run these from repository root:

1. Focused JVM presentation checks: `python scripts/dev.py test` (Gradle task
   `:app:testDebugUnitTest`); identify the exact test classes in the run log.
2. Focused installed Compose/Home checks and installed matrix:
   `python scripts/dev.py android-test` (Gradle task
   `:app:connectedDebugAndroidTest`). The added test class/method names and
   device serial/configuration must be recorded. The ten baseline captures
   must come from launching the installed `com.oxygen.weather/.MainActivity`
   normal path, not a standalone showcase.
3. Architecture guard: `python scripts/dev.py contract`.
4. Broader close gate: `python scripts/dev.py check` (unit test, lint, debug
   assembly) and `git diff --check`. Record unavailable SDK/dependency/device
   prerequisites and command output summaries in the close record.

Evidence directory:
`.codex/test-artifacts/085-tp3a-now-hourly-compositions/`

Retain:

- `captures/now_<theme>.png` and `captures/hourly_<theme>.png` for the ten
  theme/page combinations (names may add a stable case suffix, but no cases
  may be missing or duplicated);
- `capture-manifest.json` with cycle/build identifier, app variant, device and
  Android version, dp and pixel viewport/density, system insets, font scale,
  locale/layout direction, contrast, theme, page, and effective effects for
  each capture, plus the PNG filename and SHA-256 digest;
- focused and broader command logs/results, instrumentation test result output,
  and `review.md` listing each case and functional/readability findings;
- any blocked/unavailable installed attempt and the exact reason.

## Risks and assumptions

- **Normal app differs from showcase:** use the real `MainActivity`/Home path
  for assertions that claim app integration and for all captures.
- **Composition and interaction regressions are coupled:** assert both
  rendered semantics/content and exact callback results; inspect interactions
  on the installed path.
- **Design records contain proposed geometry and known rendering uncertainty:**
  implement their accepted slot/order/measurement direction, use integrated
  refinements, and defer measured reference correction to TP.3C.
- **Rain mark mapping gap from cycle 084:** preserve supplied condition text and
  semantic meaning. Do not expand into glyph correction unless it blocks
  readable/functional TP.3A integration; if it does, record the blocker and
  stop dependent TP.3 work.
- **No installed device or emulator:** this removes the required acceptance
  evidence. Record the exact prerequisite failure and close BLOCKED; source
  inspection, compile, preview, or showcase evidence cannot replace it.

## Out of scope

- Daily and Details normal-app composition migration (TP.3B).
- All-20 baseline comparison against approved references, visual correction,
  and visual acceptance (TP.3C).
- Compact viewport, large-font, RTL, sparse-state capture matrix and broader
  responsive/state regression closure (TP.3D); this does not waive the
  functional honesty/invariants above.
- Provider, repository, cache, persistence, settings, unit preferences, theme
  preferences, new weather values/horizons, or refresh behavior.
- Global navigation redesign, changes to the four-page contract, new theme
  identity, theme-pack edits, or retired Atmosphere Deck presentation language.
- Rain glyph correction except as a documented TP.3A blocker under the risk
  condition above.
- TalkBack service-level verification; report it as unverified unless it
  actually runs.
