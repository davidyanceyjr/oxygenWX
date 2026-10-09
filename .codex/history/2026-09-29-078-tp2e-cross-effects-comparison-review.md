# History — 078-tp2e-cross-effects-comparison-review

Status: Completed — PASS (partial 1 of 2)
Cycle ID: 078-tp2e-cross-effects-comparison-review
Roadmap item: TP.2E-cross-effects-review, partial 1
Closed: 2026-09-29
Plan: .codex/plans/078-tp2e-cross-effects-comparison-review.md
Evidence: .codex/test-artifacts/078-tp2e-cross-effects-comparison-review/

## Outcome

PASS. Reviewed 15 matched Subtle/Effects Off pairs (three showcase families × five themes). Twelve pairs passed. The Atmospheric, Minimal OLED, and Terminal Forecast Windows pairs record the previously documented Rain glyph mapping gap as KNOWN-LIMITATION; condition and precipitation meaning remains visible in text. No new reproducible contract finding was found. TP.2E remains open. The dependent partial 2 plan is eligible.

## Verification

Both selected source sets contained exactly 15 unique family/theme cases. All 30 canonical PNGs decoded as 360 × 640 and matched their source-manifest SHA-256 values. Both capture records identify the same API 37 emulator and APK SHA-256; both use 360 × 640 dp, 160 dpi, font scale 1.0, LTR, Standard contrast, with the expected Subtle and Effects Off levels. Native-resolution pair review confirmed visible fixture facts, controls, ordering, fit, and expected style/effects differences. The detailed source inventory and 15-row matrix are retained in cycle evidence.

Prior installed test evidence: cycle 075 allFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes passed 1/1; cycle 076 effectsOffFirstThreeFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes passed 1/1. Those tests cover text/semantics, bounds/fit, callbacks, dimensions, Effects Off solid backdrop/motion and opacity assertions. Application tests were not rerun for this evidence-only cycle. python scripts/dev.py workflow passed and git diff --check passed.

## Limitations / not verified

TalkBack service traversal, large font, RTL, High contrast, Full effects, other viewport sizes, normal Home integration, production corrections, and TP.2E closure were not verified here. No pixel-parity claim is made.

## Follow-up

Proceed to .codex/history/plans/078-tp2e-cross-effects-comparison-review-partial2.md for the remaining 15 pairs. TP.2E closure remains a separate slice after both comparison halves and any separately accepted correction.
