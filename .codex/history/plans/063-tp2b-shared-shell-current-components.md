# Plan 063 — TP.2B shared shell and current-condition component hardening

Status: Completed
Cycle ID: 063-tp2b-shared-shell-current-components
Roadmap item: TP.2B
Created: 2026-09-26
Draft: First draft; owner scope and test-strategy choices incorporated.

**Difficulty: 5/10.** The shared page header, selector, current hero, and metric
tile already exist and are used by the app. The bounded work is to audit and
correct only contract gaps, then establish focused Compose instrumentation
coverage across five themes. The main effort/risk is setting up and running
Android Compose tests in this repository, which currently has no `androidTest`
source set or Android test runner.

**Recommended implementation model:** `gpt-6-luna`, medium reasoning effort,
for the coordinated component, test-infrastructure, and developer-command
updates in this clearly bounded plan. Official OpenAI Docs describe Luna as
the most cost-efficient model and recommend Luna at medium effort for creating
from clear briefs and making coordinated updates; model availability can vary
by Codex CLI installation. See
[OpenAI model selection](https://developers.openai.com/api/docs/guides/model-selection).

**Context budget:** Estimate 35–40% of a fresh context window. Keep this cycle
within the existing shared shell/current-condition component family and its
focused instrumentation setup; do not absorb TP.2C or page-composition work.
No split is expected.

## Objective

Harden the existing production shared page header, named page selector, current
hero, and metric tile against the adopted typed presentation and accessibility
contracts. Preserve their current integration and visual language. Add focused
Compose instrumentation tests that prove visible supplied values and selector
semantics for all five resolved themes, plus supported narrow/large-font,
RTL, High contrast, and Effects Off conditions where relevant. TP.2B exits only
when the three shared families named by the roadmap (header/page identity,
current hero, and metrics) meet their typed-value/callback contracts for all
five themes, focused semantic/value checks pass, and component content and
interactions do not branch on raw theme IDs.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt` —
  audit and make only narrow contract-required corrections to
  `ProductionPageHeader`, `ProductionPageSelector`, `ProductionCurrentHero`,
  `ProductionMetricTile`, and `ProductionSectionSurface` only if a defect in
  their use by those four components requires it.
- `app/src/androidTest/` — add Compose instrumentation tests using typed
  presentation fixtures and each of the five `ResolvedTheme` values.
- `app/build.gradle.kts` — add only the Android test runner and Compose UI test
  dependencies needed for those instrumentation tests, aligned with the
  existing Compose BOM.
- `scripts/dev.py` — add a cross-platform `android-test` command for
  `:app:connectedDebugAndroidTest`, reusing the existing Gradle launcher and
  JDK/SDK selection logic.
- `.codex/history/plans/063-tp2b-shared-shell-current-components.md`,
  `.codex/current.md`, and
  `.codex/test-artifacts/063-tp2b-shared-shell-current-components/` — lifecycle
  state and exact verification evidence.

Do not create a parallel component family. Keep `OxygenWeatherApp` page
composition and its callers unchanged unless a compile-only call-site repair is
unavoidable; any such exception must be explained and remain within the same
component contract. Do not redesign public component APIs or add abstractions
without a concrete failing contract test.

## Functional invariants

- Visible page identity remains `Now`, `Hourly`, `Daily`, `Details`; selection
  remains named and semantically selected. Each selector control keeps a
  minimum target of 48 dp. This slice does not change page order, global pager
  ownership, Back behavior, or forecast-window controls.
- The header and current hero render the supplied title, supporting/location
  string, condition, temperature, apparent temperature, humidity, and dew point
  without parsing or recomputing presentation strings. Missing optional support
  and nullable condition marks are omitted; required supplied unavailable text
  remains visible.
- The hero keeps its spoken summary and visible text facts consistent.
  Decorative weather marks remain non-interactive and do not replace visible
  condition text or add weather meaning.
- Metric tiles render the exact supplied label and headline, and render
  supporting text only when supplied. They do not invent a value or infer
  meaning from label text.
- Components receive `ResolvedTheme`, typed presentation values or supplied
  strings, modifiers, and semantic callbacks only. No component-content or
  interaction branch depends on `WeatherThemeId` or the raw theme identity.
- All five themes keep their declared resolved colors, type, geometry, and
  render-style behavior. High contrast and Effects Off may alter appearance
  resolution according to TP.2A but cannot change labels, facts, selection
  semantics, or weather meaning. Effects Off remains opaque, static, and
  complete.
- Standard Home keeps one horizontal-swipe owner, the outer page pager. This
  slice adds no pager, page transition, fetch, preference, or navigation work.

## Visual objective and layout constraints

**Visual objective:** Preserve the current theme personalities and hierarchy
while ensuring shared components remain complete, readable, and semantically
equivalent when driven by each theme's resolved appearance. This is component
hardening, not a visual restyle or full-page acceptance pass.

- Exercise a compact 360 × 640 dp content area and the 393 × 852 dp reference
  viewport where the test device supports configuration. Respect safe insets
  and allow text to wrap or grow; do not clip facts or shrink system-scaled
  text.
- Exercise a large-font case at font scale 1.3, plus RTL layout direction.
  Mirror physical alignment only; preserve names, supplied string content, and
  semantic meaning.
- Exercise Standard/Subtle for every theme, and representative High contrast
  plus Effects Off resolutions for every theme. Verify that the same supplied
  values and selector semantics remain available. Do not re-test the full
  resolver matrix owned by TP.2A.
- The actual installed state for verification is the Compose test host running
  the `androidTest` APK on the project emulator/device. Also install/launch the
  debug app and capture a compact Now smoke image in the default theme at
  Subtle and Effects Off when that emulator is available. These checks prove
  integration and gross completeness only; TP.2B does not claim visual match,
  pixel acceptance, or the TP.2E showcase result.

## Implementation steps

1. Confirm the plan is active and the worktree has no unrelated changes. Review
   `NOW.md`, `FOUNDATION.md`, the adopted UI contract, the existing component
   implementations, and their current call sites. Record a concise audit of
   the existing behavior and exact contract gaps in cycle evidence. Keep this
   cycle limited to the header/page identity, current hero, and metric tile.
2. Add minimal Android Compose instrumentation support: configure the Android
   test runner and Compose UI test dependencies using the existing Compose
   BOM, then add `python scripts/dev.py android-test` wired through the current
   cross-platform Gradle/JDK environment helper. Keep the new command narrowly
   scoped to `:app:connectedDebugAndroidTest`.
3. Write focused component instrumentation tests using explicit typed test
   presentations. For each of the five resolved theme IDs, assert visible
   header identity/support text, all four page names and the selected tab
   semantics/callback, hero values and visible labels, and metric label/headline
   plus present/absent optional support. Include an unknown/unavailable string
   verbatim and a null decorative mark case. Assert interactive selector
   targets remain at least 48 dp. Use the unmerged semantics tree where needed
   to ensure the hero spoken summary has not hidden individual visible facts.
4. Add narrow High contrast/Effects Off matrix checks for every theme, and
   compact, font-scale 1.3, and RTL cases for wrapping, value visibility, target
   size, and preserved meaning. Keep the assertions semantic/value-focused;
   do not encode raster snapshots or new visual token values in these tests.
5. Make only the smallest code corrections shown necessary by those checks.
   Keep changes inside the named existing components and preserve their
   resolved-style behavior. If the existing APIs cannot satisfy the roadmap
   without a broader refactor, stop and record the precise failing contract
   rather than growing this slice.
6. Run the focused instrumentation suite on the project emulator/device when
   available, plus `python scripts/dev.py test`, `python scripts/dev.py build`,
   `python scripts/dev.py contract`, `python scripts/dev.py workflow`,
   `python scripts/dev.py check`, and `git diff --check`. Capture installed
   compact Now smoke images for Subtle and Effects Off when possible. Preserve
   exact command output and limitations under the cycle evidence directory.
7. Review the final diff against this boundary. Close TP.2B PASS only if all
   acceptance criteria and the focused installed instrumentation checks pass;
   if no compatible emulator/device can be run, retain APK/build evidence,
   state instrumentation behavior is unverified, and close BLOCKED rather than
   inferring a pass. Update `docs/theme-pack-roadmap.md` only when recording the
   exact final PASS/BLOCKED outcome.

## Acceptance criteria

- The existing header/page selector, current hero, and metric tile satisfy the
  visible typed-value and semantic callback contracts for Atmospheric, Glass,
  Minimal OLED, Instrument, and Terminal.
- Selector page names remain visible; the selected page exposes selected/tab
  semantics; controls meet the 48 dp target minimum and invoke the requested
  page callback.
- Hero labels and supplied current-condition strings remain visible and
  accessible alongside its spoken summary. The decorative mark can be absent
  without losing condition meaning.
- Metric label and headline remain exact visible text. Optional supporting
  text renders when present and is absent when null. Supplied unavailable text
  is preserved verbatim.
- Focused Compose instrumentation assertions pass across all five themes,
  with targeted High contrast/Effects Off, compact, large-font, and RTL checks.
  No component content/interaction logic branches on raw theme ID.
- Existing JVM tests, build, source contract, workflow, repository check, and
  diff check pass; instrumentation results are recorded independently from
  JVM results.
- If the project emulator/device is unavailable, the exact blocker and
  instrumentation APK compile result are recorded and the slice is marked
  BLOCKED; no semantic test pass or visual acceptance is inferred from
  compilation.
- No new page composition, weather/presentation meaning, network behavior,
  persisted setting, navigation semantics, raw-theme UI branching, or parallel
  component family is included.

## Verification and evidence

Preserve under
`.codex/test-artifacts/063-tp2b-shared-shell-current-components/`:

- current component-to-contract audit and final changed-file/boundary review;
- focused `androidTest` command and complete output, with emulator/device
  identity and viewport/font/layout configuration;
- test coverage summary mapping each of the five themes and the High
  contrast/Effects Off, compact, font-scale, and RTL cases to assertions;
- output from `python scripts/dev.py test`, `build`, `contract`, `workflow`,
  `check`, and `git diff --check`;
- compact Now smoke screenshots for installed Subtle and Effects Off states
  where an emulator is available, with actual viewport, font scale, theme, and
  effects recorded;
- exact environment blocker and instrumentation APK build evidence if a
  compatible emulator/device is unavailable. Such evidence does not convert
  the required instrumentation execution into a pass.

This slice establishes component-level behavior and semantics. It does not
claim TP.2E's ten-theme/effects showcase, TP.3 page-level visual acceptance,
TalkBack/service-level review, or a production release review.

## Risks and assumptions

- TP.2A completed and unblocked TP.2B. `ProductionPageHeader`,
  `ProductionPageSelector`, `ProductionCurrentHero`, and `ProductionMetricTile`
  already exist and are in the live app path. The owner selected hardening those
  implementations and adding focused tests, not replacing their APIs or
  creating parallel components.
- The repository currently declares only JUnit JVM tests and has no
  `app/src/androidTest` tree, Compose UI-test dependency, instrumentation
  runner, or `dev.py` instrumentation command. Minimal support for those tests
  is part of the approved boundary.
- The official component API receives a `ResolvedTheme`; existing component
  implementation style selection by resolved visual enums remains permitted.
  Raw `WeatherThemeId` switches in component content or interaction are not.
- No component-level design reference fully specifies every responsive state.
  Preserve the accepted semantic/layout invariants and current approved token
  values; do not infer new exact theme geometry or visual identity from a
  screenshot.
- If emulator execution is impossible in the available environment, the
  roadmap's semantic/value test exit remains unverified and the cycle must
  close BLOCKED.

## Out of scope

- TP.2C hourly, daily, Details, or source/freshness component families; TP.2D
  weather marks/backdrops; TP.2E showcase; or TP.3 installed theme comparison.
- Reworking `OxygenWeatherApp` page composition, `HomePresentationMapper`,
  presentation/domain models, forecast content, or current weather values.
- New theme IDs, catalog values, typography/geometry redesign, visual refresh,
  page navigation/pager changes, persisted settings, provider/repository/cache,
  unit preferences, or weather-fetch behavior.
- Screenshot pixel-diff gates, visual owner approval, full TalkBack traversal,
  or release acceptance.
