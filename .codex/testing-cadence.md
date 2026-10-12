# Testing cadence

The owner approved a focused testing cadence on 2026-10-12.

- Run tests that directly cover the changed behavior on each implementation
  slice.
- Run the full relevant unit and connected-test suites on every fifth
  implementation slice.
- Track completed implementation slices below. Planning, documentation-only,
  and evidence-only cycles do not increment the count.
- On an interim slice, broaden testing only when a focused result exposes a
  concrete regression risk that needs broader diagnosis.
- Continue boundary-specific checks such as compilation, applicable lint,
  installed visual evidence for UI changes, and `git diff --check`.

## Counter

Cycle 177 is the first implementation slice under this cadence. Its focused
checks are recorded in `.codex/test-artifacts/177-unified-home-action-menu/`.

Completed slices since the most recent full relevant suite: **1 of 5**
(Cycle 177, NAV.5). When the fifth slice closes, run the full relevant unit
and connected-test suites, then reset this counter to 0 and record the suite
result here.
