# Plan 077 — TP.2E Effects Off remaining family pages capture matrix

Status: Completed
Cycle ID: 077-tp2e-effects-off-remaining-family-pages
Roadmap item: TP.2E-effects-off-per-family-pages-partial2
Created: 2026-09-29
Depends on: PASS for `076-tp2e-effects-off-per-family-pages`; retained under `.codex/history/2026-09-28-076-tp2e-effects-off-per-family-pages.md`.

## Objective

Run and review the 15 Effects Off installed cases for Source and inspection, Weather mark, and Backdrop across all five production themes. This is the restarted second half of cycle 076; it does not close TP.2E.

## Production boundary

Instrumentation and evidence only. The allowed code boundary is `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionSharedShowcaseTest.kt`. Add the focused entry point, cycle-specific export routing, and truthful measured manifest fields. Evidence belongs in `.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/`. No production Kotlin/resources, resolver/catalog/tokens, models/data, normal Home composition/navigation, or provider state may change.

## Functional invariants

- Exactly the selected three families × five themes = 15 installed captures.
- Conditions: 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Effects Off; report actual installed display/density and measured root bounds.
- All themes receive the same fixture facts, inspection group/order, mark identity and text, backdrop content, and callbacks. Theme identity changes appearance only.
- Source and update remain distinct; unavailable values remain verbatim; inspection facts keep fixture order.
- Weather mark and backdrop add no spoken weather meaning. Backdrop remains opaque, static, complete, and permits foreground input.
- Required text fits inside measured root without scrolling, clipping, truncation, or font shrinking. A failure blocks this cycle; no production fix is in scope.

## Implementation steps

1. Verify workflow state, partial 1 PASS evidence, this plan/cycle, focused showcase contracts, local API 37 SDK/AVD availability, and environment. Preserve exact results.
2. Add the focused `effectsOffRemainingFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes()` entry. Reuse current shared fixtures/assertions; add no redundant assertions.
3. Route this cycle's captures and manifest to a distinct 077 destination, preserve existing partial 1/Subtle outputs, and report actual measured values plus installed APK SHA-256.
4. Run the focused instrumentation and required component/JVM regressions. Collect exactly 15 PNGs; verify dimensions and hashes; visually inspect all captures.
5. Run workflow, `git diff --check`, and `python scripts/dev.py check` when available. Inspect final diff, write history, and update the TP.2E head with this cycle's actual outcome. Do not claim TP.2E closure.

## Acceptance criteria

PASS requires all 15 installed cases at the recorded conditions; shared fixture content, provenance/update distinction, unavailable value, semantic stability, fit, decorative behavior, foreground input, and Effects Off opacity checks pass; all PNGs decode and match manifest dimensions/hashes and receive visual review; exact test/workflow/diff/check results and limitations are recorded. Otherwise close BLOCKED with evidence and no completion claim.

## Verification and evidence

Retain environment, test inventory, commands/results/XML/logs, 15 canonical PNGs, manifest, visual review, hashes, diff/workflow/check results, and unverified boundaries under `.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/`. Link to cycle 076 partial 1 and blocked partial 2 evidence; do not duplicate captures.

## Risks and assumptions

- Partial 1 is PASS and its 15 captures remain valid.
- Existing showcase assertions cover assigned family semantics and interactions.
- Repo-local emulator/API 37 and Downloads export remain available; recheck before tests.
- Any runtime/resolver/layout failure requires a separate plan.

## Out of scope

- Changing or rerunning partial 1 captures; Subtle recapture; cross-effects comparison.
- Production correction, TP.2E closure, TP.3, normal Home changes.
- Full effects, large font, RTL, High contrast, alternate viewport, reference parity, TalkBack service traversal, provider/data behavior, release acceptance.


## Closeout — PASS (2026-09-29)

All 15 installed cases passed at the specified conditions. Focused component, backdrop, weather mark, resolver, and visual mapping tests passed; capture hashes/dimensions matched; all 15 captures received visual review. `python scripts/dev.py check` passed. The known Rain glyph coverage limitation is recorded in `visual-review.md`; caller-visible text and semantic invariants pass. TP.2E remains open for cross-effects comparison/review and any separately planned correction.
