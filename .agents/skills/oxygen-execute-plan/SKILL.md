---
name: oxygen-execute-plan
description: Execute the current ACTIVE Oxygen implementation plan, coordinating bounded work through agents when useful, then verify and close the cycle. Use after plan review and activation.
---

# Execute the active Oxygen plan

## Primary request

> ...EXECUTE the plan through agent management of implemented the planned tasks.

Treat this exact owner prompt as the task. Do not paraphrase it into a different
request.

Follow `docs/CODEX.md`, `AGENTS.md`, and the active plan as the implementation
authority.

1. Read `docs/SPECIFICATION.md`, `docs/ROADMAP.md`, `.codex/current.md`, the
   referenced plan, `AGENTS.md`, and applicable focused authority documents.
   Run `python scripts/dev.py workflow`.
2. If the cycle is PLANNED, review its completeness and run
   `python scripts/codex_cycle.py activate` before production edits. If it is
   not ACTIVE after that, or is IDLE, stop and report the state. Never create a
   replacement plan during execution.
3. Execute only the plan's production boundary and exclusions. Break the plan
   into bounded tasks and use agent management for implementation/review tasks
   that can be delegated independently. Assign clear file/scope boundaries,
   monitor results, resolve integration, and inspect every contribution. If
   there is no useful independent task, handle the small slice directly and
   state why delegation would not help.
4. Preserve applicable evidence under the cycle-specific
   `.codex/test-artifacts/` path. Run the plan's focused checks and relevant
   broader checks, including `python scripts/dev.py check` when Android tools
   are available. For visual work, use the installed app and actual required
   viewport/font/RTL/effects conditions. Do not claim unrun acceptance items.
5. Inspect `git diff --check` and the final diff. Close completed work with
   `python scripts/codex_cycle.py close`, recording what passed, what could not
   run and why, evidence paths, limitations, and follow-up. If blocked, record
   the blocker without claiming the roadmap slice complete.
