# Plan 187 — Finish static Full Now

Status: Completed
Cycle ID: 187-finish-static-full-now
Roadmap item: SQ.5
Created: 2026-10-14

## Objective and observable outcome

Finish the installed Glass/Smoked Quartz **Static Full Now** composition on the real Home path. Current temperature and condition lead in a quiet text-safe field; one restrained surface groups supporting measurements; source/freshness, official alert summary when present, and existing actions stay distinct and readable. Apply static depth, spacing, and broad light from the approved SQ.2 source without motion or changes to weather meaning. This is a Now page review, not four-page Static Full acceptance.

The intended hierarchy is fixed by the approved SQ.2 Now recipe: retain the
global Now/location header and actions; present temperature and condition in an
open primary field with apparent temperature subordinate; place available
humidity, dew point, precipitation, and wind facts together in one supporting
group; keep alert summary and source/freshness outside that group. Omit only
facts that are unavailable under the existing presentation contract. A
supported condition mark may remain a small redundant ornament; it is never
required, and missing/unsupported conditions have no mark.

## Authority and dependencies

- SQ.4 is closed in `.codex/history/2026-10-13-186-static-mineral-background-foundation.md`; its installed captures under `.codex/test-artifacts/186-static-mineral-background-foundation/` are the same-fixture baseline.
- SQ.2's owner-approved design packet is `resources/themes/smoked-quartz/SOURCE.md`, SHA-256 `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`; approval is recorded in cycle 184. The packet's old header remains historical text, not a new approval gate.
- Reuse the same-fixture Now baseline from `.codex/test-artifacts/186-static-mineral-background-foundation/full-1.0-ltr-now.png` and its XML; use the cycle 186 capture manifest to confirm state and device metadata. Do not treat those captures as Static Full composition acceptance.
- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and `docs/theme-system/EFFECTS_CONTRACT.md` govern meaning, accessibility, and effects. `docs/UI_CONTEXT_ROADMAP.md` SQ.5 defines this slice.
- Preserve the SQ.4 native background recipe as the base. SQ.5 may tune Now-only static presentation above it, but must not redo Hourly, Daily, or Details.
- No additional owner decision or design approval is a prerequisite. The numeric treatment of light and edge accents remains an implementation judgment bounded by SQ.2, semantic palette roles, and installed text legibility.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`: the Now-only composition (`NowPage`, `NowHero`, `NowSupportingMeasurements`, and Now provenance grouping) and a narrow Glass Full branch. Keep existing content order, presentation inputs, and non-Glass behavior.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`: add a dedicated Now supporting-group surface if the shared surface cannot express the approved single-group treatment without changing other pages. Avoid changing global `ProductionSectionSurface` behavior for all themes/pages.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`: only a Now-specific static light/edge accent if the approved composition requires it and it can remain outside text-safe regions. Do not alter the SQ.4 four-page recipe or add animation.
- Focused tests may be added or amended under `app/src/androidTest/java/com/oxygen/weather/ui/` and `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/` for Now facts, grouping, semantics, and levels. Add an assertion for the single supporting group if existing tests do not prove it. Do not rewrite unrelated tests to fit a visual change.
- Evidence root: `.codex/test-artifacts/187-finish-static-full-now/`.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible Now identity, the outer pager as sole horizontal-swipe owner, page chooser, Back, menu, and other actions. Controls retain meaningful labels and at least 48dp targets where applicable.
- Keep current value/unavailable state, condition, apparent temperature, available humidity/dew point/precipitation/wind facts, partial-horizon notes, authoritative alert summary, source/update/freshness, and all existing spoken semantics. Do not invent values or hide facts to improve the composition.
- Appearance-only changes never refetch forecasts/alerts, touch cache or preferences, reinterpret conditions, or change provenance or alert meaning. Decorative marks are redundant to visible condition text, hidden from accessibility, and absent for missing/unsupported conditions.
- High contrast stays opaque with no texture or gradient behind text. Effects None remains opaque, static, and complete; Subtle and other themes retain their existing presentation. Reduced motion cannot remove information.
- At compact 360×640dp and 1.3 font scale, critical facts may continue by vertical scroll; no clipping, destructive ellipsis, or shrinking critical text to fit. RTL mirrors spatial ornament without changing page order or meaning.

## Implementation steps

1. Review the SQ.4 baseline capture and manifest alongside SQ.2's Now row and its responsive/effects rules. Record a short baseline comparison in the evidence directory covering the current boxed hero/tile treatment, text-safe region, source/alert separation, and the hierarchy above. Do not reopen the approved source or derive a new design direction.
2. Implement the smallest Glass + Full + Now composition branch. Open the hero field, keep temperature/condition dominant and apparent temperature subordinate, and group all available supporting measurements in no more than one restrained surface. Keep alert, source/freshness, and actions distinct. Use existing semantic palette/geometry roles and native Compose drawing only; keep the SQ.4 background recipe and all non-Glass, non-Full, and non-Now paths stable. Add no motion, imagery, or required weather mark.
3. Add or amend focused UI assertions so they cover the supporting group's single-container boundary and retain visible/accessibility facts. Reuse the existing assertions for supplied facts, unavailable current values, sparse fields, and source-supplied alert; do not add a new fixture or alter provider behavior to create a screenshot state.
4. Build/install through the production Activity, capture the defined Now matrix, and compare Full Standard captures with cycle 186 at actual size. Check wrapping, scroll reachability, marks, fact order, alert/source separation, and visible page/actions. Make another production change only for a defect visible in the captured state or exposed by a focused assertion.
5. Inspect the changed-path diff for leaks into other themes/effects/pages and for data, navigation, preference, request, or provenance changes. Run the focused test selectors and required build/contract/lint/workflow/diff checks. Preserve commands, results, visual review, capture manifest, and any unverified boundary under the cycle evidence path.

## Acceptance criteria

- The installed Glass Full Now page meets SQ.2's hierarchy: temperature and condition lead in an open primary field; apparent temperature is subordinate; all available supporting measurements share at most one restrained readable surface; source/freshness, alert, and actions remain distinct. No all-fact card grid or decoration competes with the facts.
- The approved broad dark field, off-center seam, soft upper-left light falloff, and sparse grain remain static with a quiet text-safe region. Any mark is secondary to condition text; absent on unsupported/missing conditions. No image, quartz inclusion, motion, or seam-glint is required.
- Installed Glass Full Now captures are reviewed at 360×640dp LTR, Standard contrast, font scale 1.0 and 1.3; High contrast at font scale 1.0 and 1.3; and RTL at font scale 1.0. Glass Effects None and Subtle at font scale 1.0 are spot-checked against the SQ.4 baseline to confirm the Full-only treatment does not leak. These states pass visible/semantic content and legibility checks, allow vertical scrolling, and show no clipped controls or critical facts. The SQ.4 manifest confirms the API 37 `oxygen_starter` emulator can provide 360×640dp.
- Deterministic missing/unavailable state is checked with the existing sparse fixture; authoritative alert-present state is checked with the existing source-supplied alert fixture. Preserve alert test evidence and at least one installed state capture when the fixture flow exposes it. Never fabricate an official alert or treat an offline Demo Station with no alert as alert-present evidence. If a required state cannot be rendered through existing hooks, record the exact blocker and do not claim that state accepted.
- Focused tests pass, debug build and relevant source contract/lint checks pass, and `git diff --check` is clean. No non-Glass, non-Full, non-Now, pager, provider, cache, preference, or meteorological meaning change is present.

## Verification and evidence

- Run selected connected tests (method-level selectors; not the full connected suite): `ProductionHomeCompositionTest#nowAndHourlyPreserveSuppliedFactsAndWindowControlsInNormalApp`; `ProductionHomeCompositionTest#glassFullNowKeepsOneSupportingGroupAndFactSemantics` (add this focused method); `ProductionHomeSparseCompositionTest#unavailableHeroValueFitsGlassAndMinimalOledWithoutChangingSemantics` and `#sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries`; and `ProductionOfficialAlertSummaryFlowTest#showsOneSourceSuppliedAlertWithoutChangingForecastResult`. Use the Gradle instrumentation `class` selector with `#method` through the host's Gradle wrapper; record the exact command and result for each selector. Do not run unrelated methods from those classes.
- Run `python scripts/dev.py build`, `python scripts/dev.py contract`, `python scripts/dev.py workflow`, applicable focused lint (`python scripts/dev.py lint` if production Kotlin changes), and `git diff --check`. Do not run `python scripts/dev.py check`, full unit tests, or the full connected suite in this slice; reserve them for separately planned SQ.V verification.
- Install the debug app through the production Activity on API 37 `oxygen_starter`. Target 360×640dp and capture PNG plus hierarchy XML pairs for: Glass Full Standard LTR at font 1.0 and 1.3; Glass Full High contrast LTR at 1.0 and 1.3; Glass Full Standard RTL at 1.0; and Glass None/Subtle Standard LTR at 1.0. Capture missing-data and alert-present states only through deterministic existing fixture paths; the automated sparse/alert tests are required even if test setup cannot be held for a separate manual capture. Record app/build identity, device/API/actual viewport, fixture, theme/effects/contrast, font/direction, and scroll positions proving below-fold facts. Compare same-state Full Standard captures with the SQ.4 same-fixture Now capture at actual size.
- Preserve the baseline comparison, capture manifest, PNG/XML pairs, exact focused command output, visual review notes, and changed-path audit under `.codex/test-artifacts/187-finish-static-full-now/`. Record every failed or unrun boundary and distinguish it from a pass.

## Context budget

Execution target: approximately 40–50% context, including the plan, focused
source/test reads, the bounded Compose edit, selected test/build output, the
seven-state installed capture review, and closeout. Keep baseline review to the
cycle 186 Now evidence and SQ.2 Now source. If execution estimates exceed 55%,
stop before expanding the matrix or implementation boundary and replan this
cycle into dependent partial plans; 65% is the hard maximum.

## Risks and assumptions

- The current Now UI uses a large enclosed hero plus four individually boxed measurements. Meeting the approved single-group hierarchy may need a Now-specific composable; global component changes would risk other pages/themes.
- Full-only composition must not silently alter None/Subtle grouping or high-contrast readability. High contrast should use opaque surfaces even if Full Standard uses restrained translucency.
- The exact light/edge parameters are not prescribed numerically. Judge them on installed captures and preserve text contrast; avoid new fixed hues or weather-dependent geometry.
- The sparse current-value test and source-supplied alert test already exist. They prove deterministic UI behavior but may not provide a stable manual capture session; do not widen this plan to add test-only production behavior. Record which state is established by automation versus installed visual capture.
- The configured AVD may not expose an exact 360×640dp viewport after system insets. Record actual app viewport and preserve the requested target; do not claim compact acceptance from a larger viewport.
- A bounded connected test can require a running API 37 emulator and method-level runner filtering. If a selector is unsupported by the installed Gradle/runner configuration, record the failed invocation and use the narrowest verified class invocation only if its additional methods remain within the slice; otherwise leave that check unverified for replanning.

## Remaining decisions

None before activation. The exact light/edge values are a bounded installed-review judgment under the approved SQ.2 source and do not require a new owner approval. Any unavailable emulator viewport or fixture state must be reported with evidence rather than resolved by inventing a value, alert, or broader scope.

## Out of scope

- Hourly, Daily, Details Static Full composition and four-page acceptance (SQ.6–SQ.9).
- Formal static performance Gate B (SQ.10), Subtle/None derivation (SQ.11–SQ.12), appearance switching/cross-page acceptance (SQ.13–SQ.14), motion gates or implementation (SQ.15–SQ.16/SQ.18), broad layout/localization acceptance (SQ.17A/SQ.17B), and full-suite verification (SQ.V).
- Provider/domain/presentation mapping, forecast or alert requests, cache, preferences, page navigation, non-Glass themes, bundled fonts, runtime SVG/bitmap art, and official alert content changes.
