# History — 028-tp-1d-integrated-pack-review

Status: Completed
Cycle ID: 028-tp-1d-integrated-pack-review
Roadmap item: TP.1D
Closed: 2026-09-23
Plan: .codex/plans/028-tp-1d-integrated-pack-review.md
Evidence: .codex/test-artifacts/028-tp-1d-integrated-pack-review/

## Outcome

Completed the bounded TP.1D upstream Now/Hourly integration: 20-cell schema with ten populated cells and ten pending Daily/Details cells, 19-source asset-use/hash map, exact mapper-derived illustrative fixture, ten primary SVG references and six reviewed condition examples. Recorded D27 fit refinements and explicit D28 font/D29 mark-detail decisions. Corrected TP.1D design-review versus TP.3 installed-acceptance wording. No Android production changes.

## Verification

Workflow and source-contract checks passed. python scripts/dev.py check passed with local .android-sdk and JDK 27 (Gradle mostly up-to-date; existing 66-test XML suite has zero failures/errors/skips). Render audit passed 16 SVGs, ten unique primary cells, exact per-page mapped field equality, viewport dimensions, text widths, and Effects Off checks. All 19 used manifest hashes and 239 local Markdown links/anchors passed. Visual inspection covered source crops, all ten primary rasterizations, all six examples, and compact/large-font end/full captures. Instrument High contrast actual opaque text pairs: 16.265:1, 14.817:1, 13.430:1. State-contract documentation audit, git diff --check and scoped final diff/new-output review passed. Exact logs, bounds, hashes, fixture harness, cell checklist and limitations are under .codex/test-artifacts/028-tp-1d-integrated-pack-review/.

## Limitations / not verified

Static design evidence only: no installed app, interaction, Android font metrics, translated RTL locale, TalkBack/service, provider or owner-approval acceptance. D28 explicitly leaves font-family choice open; D29 leaves schematic weather-mark detail open for final review. State variants beyond the complete illustrative fixture were audited in contracts, not rendered. Daily/Details integration, full 20-cell review and approval remain unperformed. This upstream closure completes neither TP.1D nor TP.1; TP.2 stays gated.

## Follow-up

Next: review/expand and separately activate .codex/plans/028-tp-1d-integrated-pack-review-partial-A.md for Daily/Details and final cross-pack owner approval. Carry D27 geometry refinements and D28/D29 decisions from INTEGRATED_PACK.md. TP.3 owns installed comparison.
