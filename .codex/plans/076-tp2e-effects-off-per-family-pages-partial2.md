# Plan 076-partial2 — TP.2E Effects Off per-family showcase pages, partial 2

Status: Planned
Cycle ID: 076-tp2e-effects-off-per-family-pages-partial2
Roadmap item: TP.2E-effects-off-per-family-pages-partial2
Created: 2026-09-28
Depends on: PASS for `076-tp2e-effects-off-per-family-pages`

**Difficulty: 4/10.** This dependent part reuses the Effects Off test host and capture contract from partial 1 for three more component families. Its primary work is exercising source/inspection, weather-mark, and backdrop behavior across five themes and retaining 15 installed captures.

**Recommended Codex CLI model:** `gpt-6-luna` at medium reasoning; use `gpt-6-sol` at low or medium reasoning if Luna is unavailable.

**Context budget:** target no more than 35% of a fresh context window and stop before 45%. Do not start before partial 1 has a PASS history record.

## Objective

Complete the Effects Off per-family installed matrix for **Source and inspection**, **Weather mark**, and **Backdrop** across Atmospheric, Glass, Minimal OLED, Instrument, and Terminal. Verify 15 cases against the existing fixture, semantics, decorative, foreground interaction, opacity/static, and fit contracts, and retain exactly 15 installed PNGs. This completes the Effects Off capture set only; it does not close TP.2E.

## Production boundary

Instrumentation and evidence only. Expected test boundary: `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionSharedShowcaseTest.kt`, with minimal focused changes only if the partial 1 harness cannot select these three cases. Update this plan/current-cycle record, the TP.2E Effects Off roadmap entry, and evidence under `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages-partial2/`.

Do not change production Kotlin, resources, resolver/catalog/tokens, models/data, normal Home composition/navigation, or provider state. Reuse partial 1's Effects Off host, fixture content, manifest schema, and capture/export approach. Keep the Subtle baseline unchanged.

## Functional invariants

- Matrix: exactly three assigned families × five themes = 15 final installed captures.
- Conditions: 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Effects Off, verified against installed display/density and measured root bounds.
- All themes receive identical source/update facts, inspection groups, mark identity/caller-visible text, backdrop caller content, and callbacks for a family. Theme identity affects appearance only.
- Source and update values remain separately visible; inspection facts and unavailable values remain supplied and honest.
- Weather marks and backdrops remain decorative, with no added spoken weather meaning. Backdrop remains opaque/static/complete and cannot block foreground input.
- All required content remains inside the root without scrolling, clipping, truncation, or font shrinking. A failure blocks this partial with exact evidence; no production fix is included.

## Implementation steps

1. **Dependency and entry audit:** verify partial 1's PASS history, 15 captures and manifest, and current-cycle IDLE state. Since this pre-created plan's filename retains the original ID plus `-partial2`, start it by setting `.codex/current.md` to PLANNED with cycle ID `076-tp2e-effects-off-per-family-pages-partial2`, roadmap item and this plan/evidence path, then run `python scripts/codex_cycle.py activate`. Do not start while partial 1 is ACTIVE or BLOCKED. Inspect the focused source/inspection, mark, and backdrop tests and confirm the device/capture environment. Record dependency and environment evidence.
2. **Extend/select remaining cases:** reuse partial 1's Effects Off showcase mechanism to run only `SOURCE_INSPECTION`, `WEATHER_MARK`, and `BACKDROP` for all five themes. Avoid duplicating content or changing page-family behavior.
3. **Assert contracts:** per theme/family, assert required visible fixture values and all text bounds; compare semantic snapshots across themes; preserve distinct source/update facts and inspection order/unavailable text; assert the weather mark adds no content description; assert backdrop decorative semantics and a successful foreground click. Check image alpha/static/complete contract at the backdrop. For source/mark cases, verify the captured composition is opaque and complete where their surfaces require it, without treating a background as semantic content.
4. **Capture installed matrix:** capture and export 15 cycle-specific PNGs (for example `Download/oxygen-weather-tp2e-076-partial2/`). Decode dimensions against measured root pixels and create a manifest with family/theme/state, viewport/font/direction/contrast/effects, device/API/build, density, app/APK identity, filename and SHA-256. Keep failed attempt outputs marked non-final.
5. **Verify and review:** run focused showcase instrumentation with `:app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionSharedShowcaseTest#effectsOffRemainingFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes`; run `ProductionDetailsComponentsTest`, `ProductionWeatherMarkTest`, and applicable `ProductionBackdrop*Test` through the components package filter. Run JVM `:app:testDebugUnitTest --tests com.oxygen.weather.ui.themeengine.ThemeResolverTest --tests com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest`. Preserve exact commands, counts, logs/XML, and exits. Visually review all 15 captures for visible facts, clipping, opacity, legibility, decoration behavior, and foreground action. Run `python scripts/dev.py workflow`, `git diff --check`, and `python scripts/dev.py check` when SDK/dependencies are available; state any unavailable check and why.
6. **Close partial 2:** inspect final diff/evidence and write the history record. Update the TP.2E sequence to record actual PASS/BLOCKED result for this half and the cumulative 30 Effects Off captures if passed. If both partials pass, make a separately bounded cross-effects comparison/review or other remaining TP.2E work eligible; do not claim TP.2E complete or perform comparison/correction here.

## Acceptance criteria

PASS requires all 15 assigned installed cases run at the recorded conditions; content, provenance/update distinction, unavailable value, decoration, semantic stability, fit, opacity/static/foreground action contracts pass; all 15 PNGs decode and match manifest dimensions/hashes and receive visual review; focused test and repository workflow/diff outcomes are recorded. Combined with partial 1 PASS this yields the 30-case Effects Off matrix, but this plan closes only partial 2.

If the installed environment is unavailable or any required contract fails, retain precise output and captures/bounds and close BLOCKED. No production or resolver changes are permitted under this plan.

## Verification and evidence

Evidence path: `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages-partial2/`. Preserve dependency audit, environment, test inventory, exact commands and result XML/logs, 15 canonical captures, manifest, visual review, hashes, diff/workflow/check outputs, and limitations. Link to partial 1's evidence; do not duplicate its files.

## Risks and assumptions

- Partial 1 passes and leaves the test-only Effects Off selection mechanism usable for the remaining three families.
- Existing typed fixture components and test runner cover source, weather-mark, and backdrop behavior without production changes.
- Device/emulator and external-files/MediaStore export path remain available and are reverified at entry.
- Defects requiring runtime/API/resolver/layout change must be reported and routed to a new plan.

## Out of scope

- Re-running or changing page identity, current, or forecast captures from partial 1.
- Subtle recapture or any cross-effects visual comparison; production corrections; TP.2E closure; TP.3.
- Full effects, large-font, RTL, High contrast, alternate viewport, design-reference pixel parity, TalkBack service traversal, provider/data behavior, and release acceptance.
