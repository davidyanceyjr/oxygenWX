# History — 100-tp3c-now-cases-and-full-baseline-gate

Status: Completed
Cycle ID: 100-tp3c-now-cases-and-full-baseline-gate
Roadmap item: TP.3C-recovery-partial-C
Closed: 2026-10-02
Plan: .codex/plans/100-tp3c-now-cases-and-full-baseline-gate.md
Evidence: .codex/test-artifacts/100-tp3c-now-cases-and-full-baseline-gate/

## Outcome

PASS: resolved the Glass, Instrument, and Minimal OLED Now deviations, then reconciled all twenty primary theme/page cases against the approved r4 packet using one frozen installed APK. Recorded per-case decisions, screenshots, hierarchies, interaction evidence, and immutable input checks under cycle evidence.

## Verification

Passed focused Compose instrumentation, python scripts/dev.py test, python scripts/dev.py check, python scripts/dev.py android-test on API 37 oxygen_starter, python scripts/dev.py build, contract, catalog, packet and hash validators, cycle-local twenty-case identity/completeness validator, workflow, and git diff --check. Final build SHA-256 matched the frozen installed APK 98b05a1daa60193dc52c1f99b87eb8301022dab6603d92119485b1a71162ad5b; all 20 primary cases have reviewed PASS records.

## Limitations / not verified

This gate covers the indexed 393x852, font-scale 1.0, en-US/LTR, Standard contrast/layout cases and static screenshots. It does not establish compact, large-font, RTL, temporal-motion, or TalkBack service-level behavior; those remain under their separate roadmap verification.

## Follow-up

TP.3C recovery partial-C baseline gate is complete. TP.3D remains gated and was not started.
