---
name: oxygen-plan-next-slice
description: Analyze the next eligible Oxygen roadmap slice, draft its bounded plan, and set it as the current PLANNED cycle. Use to begin roadmap planning; does not activate or implement the plan.
---

# Plan the next Oxygen slice

## Primary request

> ...analyze the roadmap for the next slice, build an plan draft, make the draft the current active plan.

Treat this exact owner prompt as the task. Do not paraphrase it into a different
request.

Follow the repository cycle workflow in `docs/CODEX.md`.

1. Read `docs/SPECIFICATION.md`, `docs/ROADMAP.md`, `.codex/current.md`, and
   `AGENTS.md`; run `python scripts/dev.py workflow`.
2. If a cycle is already PLANNED or ACTIVE, report it and work with that cycle
   instead of creating another. Otherwise identify the next eligible roadmap
   item and its dependencies. For theme-pack work, use
   `docs/theme-pack-roadmap.md` as the ordered track.
3. Explain the candidate slice and its boundary briefly. Create a draft plan
   under `.codex/plans/` with outcome, invariants, steps, acceptance criteria,
   verification/evidence, risks, and explicit exclusions. Use the cycle tool to
   establish the cycle when its roadmap item is supported; otherwise document
   the roadmap adjustment needed before starting.
4. Set `.codex/current.md` to point to the new `PLANNED` cycle and run the
   workflow check. Do not activate it or edit production code: plan review is
   the next stage.

Keep the plan to one independently verifiable slice. Respect roadmap ordering,
context-budget slicing, and all product/UI contracts. Do not claim an outcome
complete from planning alone.
