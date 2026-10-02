# History — 096-baseline-correction-and-installed-acceptance

Status: Completed
Cycle ID: 096-baseline-correction-and-installed-acceptance
Roadmap item: TP.3C-partial-A
Closed: 2026-10-01
Plan: .codex/plans/096-baseline-correction-and-installed-acceptance.md
Evidence: .codex/test-artifacts/096-baseline-correction-and-installed-acceptance/

## Outcome

Cycle 096 installed acceptance BLOCKED: ten of twenty cases visually deviate from the approved r4 reference. The complete evidence matrix and per-case records are retained; TP.3D remains ineligible.

## Verification

python scripts/dev.py test, check, android-test (23 passed), contract, catalog, workflow, and git diff --check passed. Installed validator passed 1472 checks; 20 reference/installed pairs reviewed; 175 interaction events captured.

## Limitations / not verified

Visual differences remain in all Hourly and Details cases and Glass/Instrument/Minimal OLED Now cases. Acceptance covered only Standard contrast, LTR, 1.0 font scale on API 37 emulator.

## Follow-up

No automatic retry or polish slice opened. Keep TP.3D ineligible until a separately planned and approved next cycle resolves the blockers.
