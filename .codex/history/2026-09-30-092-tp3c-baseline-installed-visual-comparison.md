# History — 092-tp3c-baseline-installed-visual-comparison

Status: Completed
Cycle ID: 092-tp3c-baseline-installed-visual-comparison
Roadmap item: TP.3C
Closed: 2026-09-30
Plan: .codex/plans/092-tp3c-baseline-installed-visual-comparison.md
Evidence: .codex/test-artifacts/092-tp3c-baseline-installed-visual-comparison/

## Outcome

BLOCKED: validated the approved packet and cycle 090 captures and retained twenty provisional case reviews, but cycle 091 changed default Metric display strings after the APK/screenshots; no current-state parity verdict or production correction is authorized.

## Verification

Packet 117 hashes and approved aggregate passed; working index/fixture/20 SVGs matched; cycle 090 validator passed 20 rows; focused HomePresentationTest, full unit tests, workflow, contract, python scripts/dev.py check, evidence SHA256SUMS, and git diff --check passed. Evidence: .codex/test-artifacts/092-tp3c-baseline-installed-visual-comparison/.

## Limitations / not verified

All twenty comparisons are BLOCKED by stale installed and approved fixture strings versus current Metric mapper output. No current-build installed recapture or accepted visual/text-fit check was performed; plan 092 excludes it. Historical geometry and Now omissions are provisional. First direct Gradle attempt failed on shell JDK 8; rerun with JDK 27 passed. TP.3C and TP.3D are not complete.

## Follow-up

Resolve approved fixture/reference versus current Metric wording through repository authority, plan a condition-matched installed recapture/recomparison, then reconsider cycle 093 only after a REVIEW COMPLETE history and bounded correction handoff exist.
