# History — 098-tp3c-hourly-reference-identity-and-acceptance

Status: Completed
Cycle ID: 098-tp3c-hourly-reference-identity-and-acceptance
Roadmap item: TP.3C-recovery-partial-A
Closed: 2026-10-02
Plan: .codex/history/plans/098-tp3c-hourly-reference-identity-and-acceptance.md
Evidence: .codex/test-artifacts/098-tp3c-hourly-reference-identity-and-acceptance/

## Outcome

PASS: resolved the primary-reference identity mismatch and completed installed acceptance for all five Hourly themes on the final APK.

## Verification

Passed: python scripts/dev.py check; python scripts/dev.py android-test (23 instrumentation tests, 0 failures); focused ProductionHomeCompositionTest; python scripts/dev.py contract; python scripts/dev.py catalog; python scripts/dev.py workflow; installed capture and full-resolution comparison review for all five primary Hourly cases; cycle-local validator; approved packet/hash validators; git diff --check. Final APK SHA-256 335fab6da7c3897781883b96f0b04a1ab2930455868a86a0aad33b963f29ff29. Evidence: .codex/test-artifacts/098-tp3c-hourly-reference-identity-and-acceptance/.

## Limitations / not verified

This closes only TP.3C recovery partial-A (Hourly). Details, the all-twenty TP.3C gate, TP.3, and TP.3D remain open/ineligible. The hierarchy report does not independently prove text fit; screenshot review found no clipping at the indexed compact baseline. Large-font, RTL, and High-contrast behavior are outside these five primary cases. Decorative mark art differs by theme and was not part of the accepted weather-meaning criteria. Capture recovery attempts and final successful setup are documented in logs/capture-recovery-note.md.

## Follow-up

Plan the dependent TP.3C recovery partial-B Details slice; do not claim TP.3C complete or start TP.3D before all twenty cases pass.
