# Plan 076-partial2 — TP.2E Effects Off per-family showcase pages, partial 2

Status: Blocked
Cycle ID: 076-tp2e-effects-off-per-family-pages-partial2
Roadmap item: TP.2E-effects-off-per-family-pages-partial2
Created: 2026-09-28
Revised: 2026-09-29
Depends on: PASS for `076-tp2e-effects-off-per-family-pages`

**Difficulty: 3/10.** The existing installed showcase already implements the assigned source/inspection, weather-mark, and backdrop fixtures, fit checks, semantic snapshots, click probe, pixel capture, manifest, and Effects Off alpha assertion. The slice adds a focused test entry point, corrects cycle-specific export naming/metadata if needed, and verifies/reviews 15 installed captures. No production UI or data change is expected.

**Recommended Codex CLI model:** `gpt-6-luna` at medium reasoning. Escalate only if test infrastructure exposes a concrete issue beyond the existing harness.

**Context budget:** target no more than 35% of a fresh context window and stop before 45%. This is the second half of the already split Effects Off matrix and remains independently bounded to three families and five themes.

## Objective

Complete the Effects Off installed matrix for **Source and inspection**, **Weather mark**, and **Backdrop** across Atmospheric, Glass, Minimal OLED, Instrument, and Terminal. Verify the 15 assigned captures against existing fixture meaning, semantic, fit, interaction, and Effects Off contracts. This completes only the second capture half; it does not close TP.2E.

## Production boundary

Instrumentation and evidence only. Expected implementation boundary: `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionSharedShowcaseTest.kt`. Update this plan, `.codex/current.md`, the TP.2E section at the head of `docs/theme-pack-roadmap.md`, and evidence under `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages-partial2/` as lifecycle requires.

Do not change production Kotlin, resources, resolver/catalog/tokens, models/data, normal Home composition/navigation, or provider state. Preserve partial 1's test path and captures. Reuse the existing typed fixtures and checks; add no assertion already provided by `assertPageContent`, `requiredTexts`, `assertRequiredContentBounds`, `semanticSnapshot`, `assertPageFits`, or the backdrop interaction branch unless a focused run demonstrates a specific uncovered contract.

## Functional invariants

- Exactly three assigned families × five themes = 15 final installed captures.
- Conditions: 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Effects Off; record installed display/density and measured root bounds.
- All themes use the same supplied source/update facts, inspection groups, mark identity and caller-visible text, backdrop caller content, and callbacks. Theme identity affects appearance only.
- Source and update time remain distinct; supplied unavailable values stay verbatim; inspection facts retain their fixture order.
- Weather mark and backdrop remain decorative and add no spoken weather meaning. Backdrop remains opaque, static, complete, and allows foreground input.
- All required text remains inside the measured root, without scrolling, clipping, truncation, or font shrinking. A demonstrated failure blocks this slice; no production fix is in scope.

## Implementation steps

1. **Audit entry state:** verify `python scripts/dev.py workflow`, the partial 1 PASS history and evidence, current plan/cycle identity, focused showcase implementation, and emulator/export availability. Record device, display/font settings, JDK/SDK, APK identity, test-to-contract inventory, and exact entry result. Stop with evidence if partial 1 is not PASS or installed verification cannot run.
2. **Add only the focused entry point:** add `effectsOffRemainingFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes()` calling `runShowcase(ThemeEffectsLevel.OFF, setOf(SOURCE_INSPECTION, WEATHER_MARK, BACKDROP), "effects-off-partial2", 15)`. Preserve the existing Subtle and partial 1 entry points. Do not duplicate or rewrite existing shared assertions.
3. **Fix cycle-specific export metadata:** the current Effects Off exporter routes every Effects Off run to `Download/oxygen-weather-tp2e-076-partial1`, and the manifest labels the effect key as `effects-off` although the UI contract calls this state Effects Off. Extend the test-only export routing with an explicit partial2 destination (for example `Download/oxygen-weather-tp2e-076-partial2/`) and have the focused run's manifest identify partial2 and Effects Off. Preserve the existing partial 1 and Subtle destinations and content. Record the APK SHA-256 and actual measured root/display values in retained evidence; do not claim metadata fields that were not measured.
4. **Run assigned contracts:** execute the focused instrumentation for the new entry. Its existing checks must demonstrate required fixture text and text bounds, per-family cross-theme semantic stability, source/update distinction, unavailable inspection content, mark/backdrop decorative semantics, backdrop foreground click, 48dp foreground target, root fit, and full pixel alpha for every Effects Off capture. Inspect any uncovered assertion gap before making a narrowly scoped test-only change.
5. **Retain and review captures:** collect exactly 15 cycle-specific PNGs and a manifest with family/theme/effects, viewport, font scale, direction, contrast, display/root pixels and dp, density, device/API/build, app/APK identity/hash, filenames, and SHA-256. Verify decoded dimensions and hashes against manifest. Visually inspect all 15 for visible facts, clipping, legibility, opaque/static backdrop, decorative treatment, and foreground action. Keep failed attempts separate and explicitly non-final.
6. **Focused regression and closeout:** run applicable `ProductionDetailsComponentsTest`, `ProductionWeatherMarkTest`, and backdrop instrumentation tests; run JVM `ThemeResolverTest` and `ProductionWeatherVisualsTest`. Record exact commands, counts, XML/logs, and exits. Run `python scripts/dev.py workflow`, `git diff --check`, and `python scripts/dev.py check` when environment permits, recording unavailable checks and reasons. Inspect final diff/evidence and write the cycle history. Update the TP.2E execution head with the actual result and cumulative 30-case Effects Off count only if both halves pass. Keep cross-effects review/correction and TP.2E closure as separate work.

## Acceptance criteria

PASS requires all 15 assigned installed cases at the recorded conditions; existing content, provenance/update distinction, unavailable value, semantic stability, fit, decorative, foreground-input, and Effects Off opacity/static/completeness contracts pass; all PNGs decode and match manifest dimensions/hashes and receive visual review; focused tests and workflow/diff/check outcomes and limitations are recorded. With partial 1 PASS, this yields 30 Effects Off captures, but closes only this partial.

If the installed environment is unavailable or a required contract fails, preserve exact output, relevant captures/bounds, and close BLOCKED. Do not make production or resolver changes under this plan.

## Verification and evidence

`.codex/test-artifacts/076-tp2e-effects-off-per-family-pages-partial2/` must retain environment and test inventory, exact commands/results, canonical 15 PNGs, manifest, visual review, hashes, diff/workflow/check outputs, and unverified boundaries. Link to partial 1 evidence rather than duplicating it.

## Risks and assumptions

- Partial 1 has PASS evidence and its test path remains intact.
- Existing typed fixture checks cover the assigned family contracts; any additional assertion must be motivated by a demonstrated gap.
- Emulator and export path remain available and are reverified at entry.
- Any defect needing production, resolver, or layout change is recorded and routed to another bounded plan.

## Out of scope

- Re-running or changing partial 1 families/captures, Subtle recapture, or cross-effects comparison.
- Production correction, TP.2E closure, TP.3, or standard Home changes.
- Full effects, large-font, RTL, High contrast, alternate viewport, reference pixel parity, TalkBack service traversal, provider/data behavior, and release acceptance.


## Closeout — BLOCKED (2026-09-29)

The installed API 37 environment could not be made available. The prior `oxygen_tp2b_api37` AVD is absent; available `oxygen_starter` exited before adb registration. No implementation or capture was produced. Exact evidence and unverified boundaries are in `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages-partial2/` and `.codex/history/2026-09-29-076-tp2e-effects-off-per-family-pages-partial2.md`.
