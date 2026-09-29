# Plan 076 — TP.2E Effects Off per-family showcase pages, partial 1

Status: Completed
Cycle ID: 076-tp2e-effects-off-per-family-pages
Roadmap item: TP.2E-effects-off-per-family-pages
Created: 2026-09-28
Revised: 2026-09-28

**Revised first-draft plan. Difficulty: 4/10.** The installed showcase harness and fixture contracts already exist. This part adds an Effects Off test path and verifies three existing families across five themes, with 15 installed captures. The emulator capture and visual review are the main effort.

**Recommended Codex CLI model:** `gpt-6-luna` at medium reasoning. This is a bounded Android instrumentation/test and evidence task; Luna is the cost-efficient choice. Use `gpt-6-sol` at low or medium reasoning if Luna is unavailable.

**Context budget:** target no more than 35% of a fresh context window and stop before 45%. This part covers three of six family screens. The dependent plan covers the other three and cannot start before this cycle passes.

## Objective

Extend the test-only per-family showcase to Effects Off for **Page identity**, **Current conditions**, and **Forecast windows** across Atmospheric, Glass, Minimal OLED, Instrument, and Terminal. Verify these 15 cases preserve their existing content, semantics, fit, and applicable callbacks at the approved compact baseline, and retain exactly 15 installed PNGs with a condition/hash manifest.

## Production boundary

Instrumentation and evidence only. The expected test implementation boundary is `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionSharedShowcaseTest.kt`. Update only this plan, `.codex/current.md`, the TP.2E Effects Off roadmap entry, and evidence under `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages/` as lifecycle requires.

Do not change production Kotlin, resources, resolver/catalog/tokens, data, presentation models, normal Home composition/navigation, launch behavior, or provider state. Keep cycle 075's Subtle capture/test path and its fixture contracts intact. Reuse the existing six-page test host; this part must activate only the first three named families in its Effects Off case set.

## Functional invariants

- Matrix: 3 families × 5 themes = exactly 15 final captures. Each family/theme is rendered once as a canonical final capture.
- Conditions: 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Effects Off. Verify actual device density and root bounds; do not infer them from configured modifiers alone.
- All themes receive the same existing typed values, labels, unavailable strings, chronology, selection state, and semantic callbacks for each family. Theme identity can affect resolved presentation only.
- Effects Off rendering remains complete, opaque, and static. Required caller text carries the weather/page meaning without decorative dependence. Existing resolver/component Effects Off policy remains authoritative.
- Page selector selection semantics and 48 dp targets remain intact. For Forecast windows, date/window selected/enabled semantics, chronology, and callback results remain intact. Current facts and unavailable values are unchanged.
- No scrolling, hidden content, truncation, or font shrinking to fit. A failed bound/fit/semantic assertion blocks this part and retains the reproducing capture/log.
- These remain instrumentation-only showcase cases; Standard Home remains Now → Hourly → Daily → Details.

## Implementation steps

1. **Entry audit:** confirm workflow ACTIVE and the cycle 075 PASS evidence. Inspect `ProductionSharedShowcaseTest.kt` capture/filter helpers, the six `ShowcasePage` cases, the existing page/current/forecast component tests, Effects Off resolver assertions, and device/capture availability. Save environment identity and a concise test-to-contract inventory.
2. **Add scoped Effects Off cases:** preserve the existing Subtle test unchanged. Add or factor a focused test path that resolves `ThemeEffectsLevel.OFF` and runs only `PAGE_IDENTITY`, `CURRENT_CONDITIONS`, and `FORECAST_WINDOWS` for all five themes. Keep the same fixture inputs and callback probes; do not duplicate page content or expose new production APIs.
3. **Assert behavior per case:** assert each page tag and required visible fixture text; assert every required text/page bound is nonempty and inside the measured root; collect semantic snapshots and compare them across the five themes within each family; assert the existing selected/enabled states, at-least-48 dp controls, single enabled callbacks, zero disabled callback, and Hourly/Daily chronological order. Exercise Current unavailable facts exactly as supplied. Confirm all five resolved appearances carry the existing Effects Off opaque/static policy through focused resolver tests; assert captured pixels are fully opaque where the host/backdrop contract requires it.
4. **Capture installed matrix:** on the recorded connected emulator/device, capture one root PNG for each selected family/theme case. Use cycle-specific filenames and export paths (for example `Download/oxygen-weather-tp2e-076-partial1/`; never overwrite cycle 075). Decode each PNG; assert nonempty dimensions equal measured root pixels. Retain exactly 15 canonical files and a manifest listing family, theme, effects, contrast, font scale, direction, dp/pixel bounds, device/API/build fingerprint, density, app/APK identity, filename, and SHA-256. Preserve failed-run files separately as non-final evidence.
5. **Run focused verification:** run the focused showcase instrumentation with `:app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionSharedShowcaseTest#effectsOffFirstThreeFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes`; run the existing `ProductionSharedComponentsTest` and `ProductionForecastComponentsTest` through the components package filter. Run JVM `:app:testDebugUnitTest --tests com.oxygen.weather.ui.themeengine.ThemeResolverTest --tests com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest`. Capture exact commands, result XML/logs, counts, and exits. Review all 15 installed images for visible content, clipping, opacity, readability, and appearance integrity. Run `python scripts/dev.py workflow`, `git diff --check`, and `python scripts/dev.py check` when SDK/dependencies are available; record any reason it cannot run.
6. **Close partial 1:** inspect the final diff and evidence. Update this plan/current state, the roadmap head and TP.2E sequence, and a cycle history record with actual PASS/BLOCKED evidence and limitations. On PASS, mark partial 2 eligible and name its plan. Do not claim all 30 cases, cross-effects comparison, correction, or TP.2E closure.

## Acceptance criteria

PASS requires: all 15 installed cases executed under the recorded conditions; only the three assigned family screens and five themes are represented; all screens fit without scrolling/clipping and preserve the same supplied content/semantics per family; relevant selectors/forecast callbacks and chronology pass; Effects Off opacity/static/completeness checks pass; all 15 PNGs decode and match manifest dimensions and hashes; all images are visually reviewed; focused test and workflow/diff outcomes are recorded with limitations.

If an installed environment is unavailable, a test contract fails, or a screen cannot fit without changing required content, preserve exact output/capture/bounds and close BLOCKED. Do not widen this part into production corrections.

## Verification and evidence

Evidence path: `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages/`. Preserve `environment.md`, `component-and-test-inventory.md`, exact commands/result XML/logs, device/build metadata, 15 canonical PNGs, `manifest.txt`, `visual-review.md`, SHA-256 list, diff/workflow/check outputs, and explicit unverified boundaries. The accepted Subtle set from cycle 075 is context only; do not compare images across effects levels.

## Risks and assumptions

- The cycle 075 host can use a distinct Effects Off test path without changing its Subtle behavior or production APIs.
- The connected API 37 emulator, test runner, SDK configuration, and export path remain available; reconfirm at entry.
- The first three families fit individually at the approved viewport with the existing fixtures. This is tested, not presumed as acceptance.
- If a failure requires a production/layout change, record the exact evidence and stop for a separately bounded plan.

## Execution outcome

**Result: PASS.** The focused Effects Off instrumentation passed 15/15 theme/family cases on the API 37 emulator. The existing component package passed 17/17 tests, the focused resolver/visuals JVM tests passed 10/10, and repository `check`, workflow, and `git diff --check` passed. Fifteen installed PNGs were reviewed, decoded at 360 × 640 px, verified against the runtime hashes, and confirmed fully opaque. Details are in `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages/` and the cycle history.

The first focused instrumentation attempt failed only at the final matrix assertion because it still expected all six families after the scope was narrowed to three. The assertion was corrected to the selected family set; the passing rerun generated the canonical captures. The initial host invocation failures (default Java 8 and missing SDK environment) were resolved by using the documented JDK 27 and repository `.android-sdk` paths.

No production source changed. Large-font, RTL, High contrast, Full effects, TalkBack traversal, and cross-effects visual comparison remain unverified and out of scope. Partial 2 is eligible after this cycle closes and `.codex/current.md` returns to IDLE.

## Out of scope

- Source/inspection, weather mark, and backdrop families; assigned to `.codex/plans/076-tp2e-effects-off-per-family-pages-partial2.md` after this part passes.
- Cross-effects comparison with Subtle; any production correction; TP.2E closure; normal Home migration.
- Full effects, large-font, RTL, High contrast, alternate viewport, reference pixel parity, TalkBack service traversal, provider/data behavior, and release acceptance.
- Repository-wide checks beyond workflow/diff and the applicable `check` attempt described above.
