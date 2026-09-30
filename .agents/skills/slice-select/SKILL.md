---
name: slice-select
description: Analyze the next active roadmap slice and create its broad first-draft implementation plan, including verified context, dependencies, validation, difficulty, and model recommendation.
---

Analyze the next slice in the active roadmap and create a first-draft implementation plan.

The first draft is a broad planning artifact. Its purpose is to capture enough verified structure, dependencies, affected areas, tests, documentation, and implementation metadata for the review stage to produce the final execution-ready plan.

Do not over-design the implementation at this stage.

## Ambiguity rule

If any material question is unanswered, or if the roadmap slice depends on an assumption that cannot be verified from the repository, roadmap, project documentation, or established conventions:

Stop.

Ask the user the question and provide exactly three recommended solutions, each with a brief tradeoff.

Wait for the user's answer before continuing.

Do not silently choose an assumption.

## Analysis scope

Inspect only enough project context to establish:

- the slice objective and intended outcome
- relevant architectural boundaries
- likely affected components, modules, interfaces, and data
- important dependencies and ordering constraints
- existing implementation patterns that should be preserved
- likely compatibility or migration concerns
- relevant tests and validation surfaces
- documentation that may require updates
- known risks, open questions, and integration concerns

Prefer targeted repository reads over broad exploration.

Do not perform implementation work.

Do not attempt to resolve low-level implementation details that are better handled during review or execution.

## First-draft plan

The draft should contain, where applicable:

- slice ID and objective
- scope and explicit exclusions
- relevant existing architecture/context
- affected components and likely files/modules
- data/schema/API/interface changes
- implementation workstreams in dependency order
- compatibility or migration considerations
- required test coverage and validation
- required documentation updates
- dependencies on earlier or later roadmap work
- risks and unresolved concerns
- completion criteria

Distinguish verified repository facts from tentative implementation expectations.

Avoid speculative detail, placeholder architecture, or unnecessary prose.

The draft should be broad enough for the review stage to reason about the full slice without requiring repository rediscovery, but should not prescribe incidental implementation details.

## Difficulty and model recommendation

When the draft is complete:

1. Rate implementation difficulty from **1 to 10**, where:
   - 1 = trivial and localized
   - 5 = moderate cross-component work with meaningful validation
   - 10 = highly complex, cross-cutting, migration-heavy, or integration-sensitive

2. Briefly justify the rating based on:
   - implementation breadth
   - coupling
   - architectural uncertainty
   - migration or compatibility risk
   - validation burden

3. Recommend the most token-cost-efficient Codex CLI model that is likely to implement the slice successfully.

Choose the least expensive capable model rather than the most capable model by default.

Base the recommendation on the slice's actual reasoning requirements, coupling, ambiguity, and expected execution depth.

If a less expensive model is appropriate for most of the implementation but a stronger model may be needed during review or difficult integration work, state that explicitly.

## Finalization

Write the completed first draft as the new current active plan (`current.md`).

Do not modify roadmap scope except where necessary to accurately represent the existing slice.

The review stage is responsible for converting this first draft into the final bounded, execution-ready implementation plan.
