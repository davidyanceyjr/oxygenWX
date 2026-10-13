# Testing policy

The owner clarified on 2026-10-12 that full suites have their own sessions.

- Run tests that directly cover the changed behavior on each implementation
  slice.
- Plan full relevant unit and connected-test suites as separate verification
  cycles with their own scope, evidence, and history.
- Broaden a focused check only when its result exposes a concrete regression
  risk that needs diagnosis; do not claim a full-suite result from that check.
- Continue boundary-specific checks such as compilation, applicable lint,
  installed visual evidence for UI changes, and `git diff --check`.

## Historical record

Cycle 177 (NAV.5) recorded focused checks in
`.codex/test-artifacts/177-unified-home-action-menu/`. The former fifth-slice
counter is retired; it does not schedule future full-suite runs.
