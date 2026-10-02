---
name: oxygen-review-plan
description: Review and complete the current Oxygen PLANNED cycle draft. Use after roadmap analysis to make the initial plan implementation-ready; does not activate the cycle or implement code.
---

# Review the current Oxygen plan

## Primary request

> ...REVIEW the draft and build a fully planned implemenatation from the intial plan.

Treat this exact owner prompt as the task. Do not paraphrase it into a different
request.

Follow `docs/CODEX.md` and the plan requirements in `.codex/README.md`.

1. Read the specification, roadmap, `.codex/current.md`, its referenced plan,
   and `AGENTS.md`; run `python scripts/dev.py workflow`.
2. Confirm the current cycle is `PLANNED`. If it is IDLE, identify the missing
   draft; if ACTIVE, do not replace the active boundary. Report any mismatch.
3. Review the draft against the roadmap's dependencies and exit criteria,
   product/architecture/UI invariants, context budget, and explicit out-of-scope
   work. Enforce a hard maximum of 65% of the available context window for each
   implementation slice; split any slice expected to exceed that ceiling.
   Resolve routine planning gaps using repository authority. Clearly mark
   assumptions and dependencies that need owner input; do not invent approval.
4. Rewrite the referenced plan into a complete implementation plan with a
   bounded objective, production boundary, invariants, ordered implementation
   steps, acceptance criteria, exact focused/broader verification and evidence
   paths, risks, and exclusions. For visual work include viewport, font scale,
   RTL/effects, and installed-state expectations.
5. Run `python scripts/dev.py workflow` and report the plan path and remaining
   decisions. Leave the cycle `PLANNED`; do not activate or implement it unless
   the user explicitly asks to continue to execution.
