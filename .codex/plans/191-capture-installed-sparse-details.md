# Plan 191 — Capture installed sparse Details state

Status: Completed
Cycle ID: 191-capture-installed-sparse-details
Roadmap item: SQ.8
Created: 2026-10-14

## Objective

Resolve SQ.8's installed missing-field Details evidence gap by exercising the
existing debug-only `demo_sparse_partial_horizon_v1` fixture through the real
`MainActivity` -> canonical `WeatherBundle` -> production mapper -> Compose
path, then capture and review compact Glass Full Details at 360x640 dp,
Standard contrast, LTR, font scale 1.0. This is an evidence-only follow-up;
the existing Demo Station fixture and production behavior remain unchanged.

## Production boundary

No production code changes. Verify the existing debug launch-extra pathway
(`oxygen_sparse_fixture=true`) on the physical Pixel 3 XL and preserve the
installed output under `.codex/test-artifacts/191-capture-installed-sparse-details/`.

## Functional invariants

- Missing current values remain absent from the canonical fixture and are
  omitted by the production Details mapper; they are never replaced with
  invented values.
- Retained pressure, cloud cover, visibility, source, update time, and group
  semantics remain exact.
- The fixture remains debug-only and does not change ordinary Demo Station
  data, requests, navigation, or persisted settings.

## Implementation steps

1. Confirm device readiness (screen timeout disabled and Stay Awake enabled),
   installed app identity, theme/effects, contrast, viewport, font scale, and
   locale/direction.
2. Launch the installed debug Activity with the existing sparse fixture extra;
   navigate to Details and capture PNG/XML at the top and scroll end if needed.
3. Review visible facts, omitted missing fields/groups, semantics, readability,
   scroll reachability, and page identity. Hash and record evidence.
4. Run workflow validation and `git diff --check`; do not run tests because no
   code or behavior changes are in this evidence-only cycle.

## Acceptance criteria

- Installed production Activity evidence shows a deterministic missing-field
  Details state from the existing debug-only sparse fixture at 360x640 dp,
  Standard LTR, font scale 1.0.
- Review confirms omitted missing current fields and retained supplied values
  are consistent with production mapper semantics, with no fabricated values.
- Evidence manifest identifies device/API, build, fixture, Glass Full settings,
  locale/direction, viewport, scroll state, and hashes.
- SQ.8's missing-field installed visual criterion can be claimed complete;
  no production fixture behavior was added.

## Verification and evidence

- Physical Pixel 3 XL/API 31 installed Activity, compact viewport 360x640 dp.
- Existing Glass Full available-state evidence remains in cycle 190; this cycle
  adds sparse-state PNG/XML and review notes.
- `python scripts/dev.py workflow` and `git diff --check`.
- No tests or builds: the cycle verifies an already-installed debug fixture
  and makes no source changes.

## Risks and assumptions

- The sparse fixture changes current/hourly/daily inputs, not only Details.
  It is acceptable only if Details missing fields are produced through the
  canonical bundle and production mapper, which the installed state and source
  audit must establish.

## Out of scope

- Production or test source changes, fixture meaning changes, additional
  sparse scenarios, broader visual matrix, four-page Static Full acceptance,
  full test suites, TalkBack, and SQ.9 work.
