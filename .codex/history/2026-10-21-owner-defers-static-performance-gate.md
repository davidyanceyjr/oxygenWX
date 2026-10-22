# Owner decision — Defer static performance measurement

Date: 2026-10-21
Scope: Smoked Quartz static Full performance requirement (SQ.10)

## Decision

The owner removed the standing requirement to measure static performance as a
gate for Smoked Quartz acceptance and downstream static appearance work.
Revisit performance investigation if a performance issue is observed in use.

## Roadmap effect

- The former Gate B static performance result is not required for SQ.11 or the
  motion lifecycle work.
- SQ.10's proposed interaction-feasibility, measurement-feasibility, and scored
  cycles 215–217 are canceled. Cycles 210–214 and their evidence remain valid
  historical records of the prior measurement attempt.
- The cycle 214 `GATED` disposition accurately records the method evidence
  available at that time; it is superseded as a roadmap prerequisite by this
  owner decision. It does not establish a measured performance pass or fail.
- Installed visual acceptance, functional checks, accessibility requirements,
  and the motion lifecycle gate remain in force. No new performance test is
  required absent an observed issue and a bounded replan.

## Updated authority

The current requirement is reflected in `docs/SPECIFICATION.md`,
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
`docs/theme-system/EFFECTS_CONTRACT.md`, `docs/ROADMAP.md`, and
`docs/UI_CONTEXT_ROADMAP.md`.
