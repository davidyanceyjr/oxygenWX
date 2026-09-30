# Plan 084 — TP.2E evidence reconciliation and closure

Status: Completed
Cycle ID: 084-tp2e-evidence-reconciliation-closure
Roadmap item: TP.2E-closure
Created: 2026-09-30
Depends on: PASS for cycles 075, 076, 077, 078 partial 1, and 078 partial 2

**Difficulty: 3/10.** This is a bounded documentation and evidence audit. The
capture matrices and pair reviews already exist; the work is to validate their
coverage and dispositions together, confirm no unresolved contract finding or
integrity failure, then record TP.2E's precise outcome. Expected context use is
about 20% of a fresh context window, below the 45% split threshold.

## Objective

Reconcile the six-family × five-theme Subtle and Effects Off showcase evidence
with both cross-effects reviews, then close TP.2E PASS only if the documented
gates are satisfied. Preserve the known Rain-mark limitation and all unverified
boundaries accurately. This cycle does not claim normal Home integration or
pixel parity.

## Production boundary

- Documentation and review evidence only: this cycle's `.codex/test-artifacts/084-tp2e-evidence-reconciliation-closure/`, this plan, `.codex/current.md`, the new `.codex/history/` entry, and TP.2E status text in `docs/theme-pack-roadmap.md`.
- No production or test code, fixtures, approved design tokens, resolver/catalog, prior-cycle artifacts, or capture images may change.
- The outcome is PASS only if the existing evidence supports closure. If a reproducible contract breach or evidence-integrity failure appears, preserve its exact reference and close BLOCKED; implementation, correction, or recapture needs a separate plan.

## Functional invariants

- Preserve the product contract `Now -> Hourly -> Daily -> Details`, page/window/navigation behavior, supplied weather meaning, chronology, provenance, and accessibility semantics.
- Theme and effects change presentation only. Effects Off remains opaque, static, and complete.
- Weather marks and backdrops remain decorative; visible caller text carries weather meaning. The documented Rain glyph gaps remain limitations only where condition text and semantics are preserved.
- The TP.2E installed evidence applies only to test-only shared-component showcases at 360 × 640 dp, font scale 1.0, LTR, Standard contrast. It does not stand in for a normal-app visual or accessibility check.

## Canonical inputs

Read, but do not modify, these history and evidence records:

- Subtle per-family matrix: `.codex/history/2026-09-28-075-tp2e-per-family-showcase-pages.md` and `.codex/test-artifacts/075-tp2e-per-family-showcase-pages/installed/manifest.txt`, `sha256sums.txt`, `visual-review.md`, and `final-checks.md`.
- Effects Off partial 1: `.codex/history/2026-09-28-076-tp2e-effects-off-per-family-pages.md` and `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages/installed/manifest.txt`, plus cycle-root `visual-review.md` and `sha256.txt`.
- Effects Off partial 2: `.codex/history/2026-09-29-077-tp2e-effects-off-remaining-family-pages.md` and `.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/manifest.txt`, `capture-sha256.txt`, `visual-review.md`, and `commands.md`.
- Cross-effects review partial 1: `.codex/history/2026-09-29-078-tp2e-cross-effects-comparison-review.md` and `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review/` (`source-inventory.md`, `pair-review-matrix.md`, `visual-review.md`, `final-checks.md`).
- Cross-effects review partial 2: `.codex/history/2026-09-29-078-tp2e-cross-effects-comparison-review-partial2.md` and `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review-partial2/` (`source-inventory.md`, `pair-review-matrix.md`, `visual-review.md`, `final-checks.md`).
- Product/component criteria: `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, `docs/theme-system/architecture/COMPONENT_CONTRACT.md`, `docs/theme-system/design-pack/CONTENT_AND_STATE_RULES.md`, and TP.2E in `docs/theme-pack-roadmap.md`.

The initial blocked cycle 076 partial-2 attempt is historical and superseded
by passing cycle 077; retain it as history, but do not count it as a passing
prerequisite or use it as a replacement evidence source.

## Implementation steps

1. **Check prerequisites.** Confirm `.codex/current.md` points to this PLANNED cycle and the workflow check passes. Verify the five listed histories report PASS/Completed with the expected evidence directories and that their summaries agree with their artifacts. Confirm cycle 077 supersedes the blocked partial-2 attempt from cycle 076. If a prerequisite's final status is not passing or evidence is absent, document the exact gap and close BLOCKED.
2. **Inventory source sets.** Read the three capture manifests and checksums. Record each effects set's six family names and five theme names, condition metadata, image count, and canonical artifact directory. Confirm Subtle has 30 captures (cycle 075) and Effects Off has 30 (cycles 076 and 077 combined), all at 360 × 640 dp, font scale 1.0, LTR, Standard contrast, with the declared effects level. Do not recapture or silently substitute files.
3. **Reconcile comparison coverage.** Validate each 078 `pair-review-matrix.md` against its source inventory: partial 1 contains Page identity, Current conditions, and Forecast windows; partial 2 contains Source and inspection, Weather mark, and Backdrop. Each must have 15 unique `(family, theme)` keys. Combine to exactly 30 unique pairs covering all six families × five themes with no overlap or omission. Reconcile every row's two capture paths/hash references to the canonical manifests and the recorded disposition.
4. **Audit outcomes against contracts.** Confirm the two matrices total 24 PASS, six KNOWN-LIMITATION, and zero FINDING. Verify the six Rain limitations correspond to the documented missing Rain glyph mappings (three Forecast Windows pairs and three Weather mark pairs) while caller-visible weather text remains; do not treat the image limitation as a semantic failure or infer authorization for a production correction. Check for any unresolved finding, missing row, inconsistent hash/condition, or claim exceeding the prior evidence. If any exists, retain exact paths/keys/contracts and close BLOCKED.
5. **Write the audit record.** Under this cycle's evidence path, create `source-inventory.md`, `coverage-and-disposition-audit.md`, `commands.md`, and `final-checks.md`. Include the source file list and hashes checked, the two 15-key counts and combined 30-key reconciliation, disposition totals, Rain limitation mapping, prerequisite outcomes, and explicit non-claims. Cite existing installed assertions for the claims they support; no new application test or image inspection is required unless an inconsistency triggers BLOCKED investigation.
6. **Close consistently.** If all gates pass, update the TP.2E execution head in `docs/theme-pack-roadmap.md` to state that the showcase and cross-effects comparison are closed with 24 PASS / six KNOWN-LIMITATION / zero FINDING, and leave TP.3 as the next theme-pack dependency. If blocked, update that head with the exact blocker while keeping TP.2E open. Update this plan and `.codex/current.md` only through the cycle close workflow. Run final checks and inspect the complete diff before recording history.

## Acceptance criteria

PASS only when all conditions hold:

- All five passing prerequisite histories and the specific canonical artifacts above are present and internally consistent.
- The Subtle and Effects Off sets each cover exactly six families × five themes (30 captures); the two cross-effects matrices contain exactly 15 unique rows each, and together cover 30 unique pairs / 60 source captures without overlap, omission, or substitution.
- All capture conditions and hashes reconcile with source manifests. Every comparison row has one supported disposition, and totals reconcile to 24 PASS, six KNOWN-LIMITATION, zero FINDING.
- The six Rain limitations are limited to the documented no-glyph cases with visible weather text/semantics retained; no new unresolved contract finding or evidence-integrity issue remains.
- TP.2E is closed only for the test-only shared-component showcase and cross-effects review. History and roadmap explicitly leave normal-app integration/page composition, pixel parity, TP.3 responsive/state acceptance, and TalkBack service traversal unverified.
- `python scripts/dev.py workflow` and `git diff --check` pass, and the final diff is inspected.

If any condition fails, close BLOCKED with the exact evidence key/path, expected contract or count, observed result, and dependent work stopped. Do not claim TP.2E closure.

## Verification and evidence

Cycle evidence path: `.codex/test-artifacts/084-tp2e-evidence-reconciliation-closure/`.

This evidence-only cycle does not require application tests, new screenshots,
an installed run, or TalkBack service traversal. Validate inventory and
matrices from their retained hashes, manifests, review tables, and histories;
cite prior test/capture checks without claiming they were rerun. Run:

- `python scripts/dev.py workflow` before review and after cycle records are updated;
- focused read-only source reconciliation (document exact commands/scripts and exit status in `commands.md`);
- `git diff --check` after edits;
- final diff inspection, recorded in `final-checks.md`.

If a script is used to validate matrices or hashes, retain it and its output in
the cycle evidence directory. Do not modify any canonical prior-cycle artifact.

## Risks and assumptions

- The histories currently describe cycles 075, 076, 077, and both 078 reviews as passing; confirm five passing records/artifact sets directly. Cycle 076's blocked partial-2 attempt is superseded by 077, not a required pass.
- The 078 matrices' recorded 24 PASS / six KNOWN-LIMITATION / zero FINDING totals and combined coverage are accepted only after reconciliation against the canonical source manifests and individual row references.
- D29 includes intentional no-mark gaps, and prior evidence reports the Rain glyph limitation in three theme styles. If a source shows missing visible condition meaning or a different mapping issue, treat it as a finding rather than extending this exception.
- Existing evidence may be sufficient to close TP.2E's showcase gate but cannot establish normal Home composition or visual parity. If the TP.2E criteria require additional evidence, record the precise gap and block closure rather than expanding this cycle.
- No owner decision is currently required to record the existing Rain cases as known limitations. Do not infer acceptance of any new production change; any correction must be separately authorized and planned.

## Out of scope

- Production or test implementation, correction, resolver/token/D29 change, or recapture.
- New themes, design decisions, or alteration of approved showcase content.
- TP.3 normal-app composition, installed baseline visual comparison, responsive/state regression, or any pixel-parity claim.
- Provider/data behavior, settings persistence, release acceptance, or TalkBack/service-level verification.
