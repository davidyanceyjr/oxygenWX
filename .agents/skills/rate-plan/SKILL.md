---
name: rate-plan
description: Assess the current Oxygen implementation plan and recommend the cheapest suitable model, with confidence and a fallback. Does not edit or activate the plan.
---

# Recommend a model for the current plan

Read `.codex/current.md` and the plan it references. Consult the relevant
roadmap, implementation, and verification requirements as needed. If there is
no current plan, say so rather than assessing an invented one.

The primary outcome is a practical recommendation for the **cheapest model
available for this job that is likely to meet the plan's acceptance criteria**.
Do not change, activate, expand, or implement the plan.

## Assess the work

Estimate model needs from the work itself, not from a difficulty score alone.
Consider:

- how bounded and implementation-ready the plan is;
- ambiguity, design decisions, unfamiliar APIs, and cross-cutting changes;
- coding, research, testing, integration, and review demands;
- cost of a subtle error and strength of deterministic verification;
- likely input context, generated work, and debugging/retry effort.

Use repository evidence and, when available, results from comparable Oxygen
tasks to estimate which capability tier is likely to pass. Treat past outcomes
as calibration, not a guarantee. If no comparable results exist, state that
the fit estimate is provisional and explain the main uncertainty.

## Compare models and cost

1. Identify the candidate models actually available in the current Codex
   runtime, or use a model shortlist explicitly supplied by the owner. Do not
   assume API model availability implies availability in Codex.
2. For metered API use, check current official pricing for the candidate models
   and distinguish input, cached-input, output, and separately billed tool
   costs where applicable. Browse official provider documentation because
   model availability and prices change. Do not invent prices.
3. Estimate execution workload as a range when uncertain: input tokens,
   output tokens, and likely retry/debugging turns. Estimate expected cost to
   completion from those amounts and the applicable rates, including the
   effect of likely retries. Context-window capacity is a fit constraint only;
   it is not a proxy for cost or capability.
4. If the task runs through a fixed-subscription Codex product without
   exposed per-task marginal pricing, say that a dollar comparison is
   unavailable. Compare the available models by relative cost/usage tier and
   expected completion reliability instead; do not apply API prices as if
   they were Codex charges.
5. Recommend the lowest-cost candidate with a credible path to passing the
   plan. If a model is likely to need significant supervision or rework, count
   that in its expected cost rather than choosing it solely for a lower
   nominal token rate. Name a stronger fallback and the concrete condition
   that should trigger escalation.

If current model availability, pricing, or runtime billing cannot be
established, report that limitation and give only a qualified relative
recommendation based on verified information. Separate facts, estimates, and
assumptions.

## Response

Keep the recommendation concise and include:

- **Recommended model:** cheapest suitable option, why it fits, and confidence
  (high/medium/low) with the key evidence or uncertainty.
- **Fallback:** next model to try and a specific escalation trigger, such as a
  failed schema decision, repeated test/debug loop, or inability to preserve a
  contract boundary.
- **Cost basis:** expected input/output workload range and expected cost range
  when prices and billing are available; otherwise state what is missing and
  compare relative cost/usage tiers.
- **Fit risks:** the one to three aspects most likely to exceed the suggested
  model's capability.
- **Context fit:** report context-window capacity only when it could constrain
  execution. Keep it separate from workload and cost estimates.
