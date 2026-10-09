# `.codex` Development Record

This directory is the persistent execution record for Oxygen development. It exists so a new Codex session, human contributor, or future maintainer can reconstruct what is currently being attempted, why, what was verified, and what happened in previous cycles without relying on chat history.

## Required structure

```text
.codex/
  current.md       one pointer/state file for the current or next cycle
  plans/           current, planned, blocked, partial, or otherwise unresolved plans
  history/         closed-cycle outcome records and archived completed plans; do not rewrite old history casually
  test-artifacts/  local installed screenshots/logs/results (often untracked when large)
```

## Lifecycle

1. Read `docs/SPECIFICATION.md` and `docs/ROADMAP.md`.
2. Read `.codex/current.md`.
3. Before production code changes, ensure a bounded plan exists under `.codex/plans/`.
4. If starting a new slice, use `python scripts/codex_cycle.py start ...` or create the equivalent files manually.
5. Work only within the plan’s named production boundary and out-of-scope limits.
6. Record tests/evidence as work proceeds.
7. Run the plan’s focused verification and applicable broader checks.
8. Close the cycle with `python scripts/codex_cycle.py close --summary "..."` or equivalent manual updates.
9. The close operation creates a durable `.codex/history/` record and returns `current.md` to idle.

## Plan requirements

Every plan must identify:

- roadmap item;
- objective and independently observable outcome;
- production boundary;
- functional invariants;
- implementation steps;
- acceptance criteria;
- exact verification/evidence expected;
- risks/assumptions;
- explicit out-of-scope work.

For UI work, also include viewport/font/RTL/effects conditions that matter.

## History requirements

A closed history record must state:

- what changed;
- what did not change;
- verification that passed;
- verification that could not run and why;
- evidence paths when retained;
- follow-up/next roadmap item;
- commit/PR identifiers when known.

Do not claim an installed, accessibility, provider, or release boundary was verified unless the relevant evidence actually ran.

## Routine context and history lookup

For a new task, load the governing specification and roadmap, `.codex/current.md`,
and only the plan named by the current pointer (if any). The active `.codex/plans/`
directory may contain unresolved plans; check status and dependencies before selecting
one. Completed plans are archived under `.codex/history/plans/` beside their closed
cycle records. Open a specific history record, archived plan, or evidence directory
when a dependency, audit, regression investigation, or reconstruction requires it; do
not load the full history or evidence tree as routine context.

## Routine context and history lookup

For a new task, load the governing specification and roadmap, `.codex/current.md`,
and only the plan named by the current pointer (if any). The active `.codex/plans/`
directory may contain unresolved plans; check status and dependencies before selecting
one. Completed plans are archived under `.codex/history/plans/` beside their closed
cycle records. Open a specific history record, archived plan, or evidence directory
when a dependency, audit, regression investigation, or reconstruction requires it; do
not load the full history or evidence tree as routine context.
