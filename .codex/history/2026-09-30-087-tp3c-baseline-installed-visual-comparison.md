# History — 087-tp3c-baseline-installed-visual-comparison

Status: Completed
Cycle ID: 087-tp3c-baseline-installed-visual-comparison
Roadmap item: TP.3C
Closed: 2026-09-30
Plan: .codex/plans/087-tp3c-baseline-installed-visual-comparison.md
Evidence: .codex/test-artifacts/087-tp3c-baseline-installed-visual-comparison/

## Outcome

Execution of the TP.3C comparison audit is complete with outcome BLOCKED. All 20 screenshots were hash- and dimension-verified, but none received an accepted-criteria visual disposition because fixture/load-state evidence was missing or incompatible. This does not complete the TP.3C roadmap slice and does not permit TP.3D progression.

## Verification

python scripts/dev.py workflow passed in ACTIVE state; git diff --check passed; evidence Markdown whitespace scan passed; exactly 20 case rows and 20 input mappings verified. No app tests/build/install/captures were run under this evidence-only plan.

## Limitations / not verified

TP.3A Hourly screenshots show a 12 PM–5 PM window and 12:00 PM update rather than the indexed 9 AM–2 PM / 9:00 AM fixture. TP.3B Daily/Details report development-fixture status and 2:00 PM update rather than LIVE/UNKNOWN and 9:00 AM. TP.3A Now lacks per-case fixture/load identity and installed hierarchy/end-of-scroll evidence. The two prerequisite APKs differ. Therefore no geometry, accessibility, interaction, or visual-parity acceptance is claimed.

## Follow-up

Create and review an authorized evidence/correction plan that establishes one current APK and exact deterministic fixture/load state, recaptures the affected matrix with complete build/device/fixture/settings metadata plus start/end scroll, hierarchy, and interaction evidence, then performs the accepted comparison and any permitted correction pass. Keep TP.3D gated until TP.3C passes.
