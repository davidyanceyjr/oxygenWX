# History — 088-tp3c-partial-a-deterministic-baseline-recapture

Status: Completed
Cycle ID: 088-tp3c-partial-a-deterministic-baseline-recapture
Roadmap item: TP.3C
Closed: 2026-09-30
Plan: .codex/plans/088-tp3c-partial-a-deterministic-baseline-recapture.md
Evidence: .codex/test-artifacts/088-tp3c-partial-a-deterministic-baseline-recapture/

## Outcome

Implemented debug-only deterministic TP.3C capture launch using the existing fixture and presentation/load-state path.

## Verification

python scripts/dev.py test passed; python scripts/dev.py contract passed within check; python scripts/dev.py check passed after final source edit; release assemble passed with JDK 27 and local Android SDK; release packaged manifest has no debuggable attribute; installed API 37 emulator ordinary and capture smoke launches passed with screenshot/hierarchy evidence; git diff --check passed. Evidence: .codex/test-artifacts/088-tp3c-partial-a-deterministic-baseline-recapture/.

## Limitations / not verified

Only the startup smoke state was installed and inspected. The twenty-case comparison matrix, visual parity judgment, TP.3C completion, and TP.3D accessibility/environment matrix remain unverified and out of scope. Release runtime install was not performed; release exclusion was verified by selector test, build, and packaged manifest.

## Follow-up

Proceed to the separately planned TP.3C Partial B installed capture matrix; do not infer screenshot parity from this startup smoke evidence.
