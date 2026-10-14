# Testing policy

The owner clarified on 2026-10-12 that full suites have their own sessions.

- Run tests that directly cover the changed behavior on each implementation
  slice. Use the Pixel 3 XL for every implementation-slice test and installed
  slice check.
- Plan full relevant unit and connected-test suites as separate verification
  sessions with their own scope, evidence, and history. A full connected-suite
  session requires all registered devices: Pixel 3 XL, Pixel 5 (`redfin`), and
  Pixel Fold (`felix`). Do not run full-suite tests as part of each slice.
- While any device is being used for testing, set its screen timeout to 0 or
  disabled and confirm **Stay Awake** is selected in Developer Options. This
  applies to the Pixel 3 XL during slice work and to all three registered
  devices during a full connected-suite session.
- Broaden a focused check only when its result exposes a concrete regression
  risk that needs diagnosis; do not claim a full-suite result from that check.
- Continue boundary-specific checks such as compilation, applicable lint,
  installed visual evidence for UI changes, and `git diff --check`.

## Historical record

Cycle 177 (NAV.5) recorded focused checks in
`.codex/test-artifacts/177-unified-home-action-menu/`. The former fifth-slice
counter is retired; it does not schedule future full-suite runs.
