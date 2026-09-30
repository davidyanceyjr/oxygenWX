---
name: rate-plan
description: Rate the implementation difficulty of the current Oxygen plan from 1 to 10 without editing the plan. Use for a plan difficulty estimate, not a plan review or rewrite.
---

# Rate the current plan

Read `.codex/current.md` and the plan it references. Consult the roadmap, relevant implementation, and verification requirements as needed to ground the estimate. If there is no current plan, say so rather than rating an invented one.

Rate the difficulty of implementing the current plan on a scale of 1–10:

- **1:** Trivial, localized change with little risk or uncertainty.
- **5:** Moderate implementation effort requiring multiple coordinated changes or nontrivial testing.
- **10:** Highly complex, risky, ambiguous, or cross-cutting implementation requiring substantial design and validation.

Base the rating on implementation complexity, required code or documentation changes, dependencies, testing or validation effort, ambiguity, and regression risk. Give the score, a concise explanation of the factors driving it, and the one to three biggest sources of implementation difficulty.

Do not modify, rewrite, expand, activate, or otherwise alter the current plan. This skill is an assessment only; do not implement the plan.
