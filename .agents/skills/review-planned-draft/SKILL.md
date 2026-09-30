---
name: review-planned-draft
description: Review a planned implementation slice as a first revised draft, ensuring bounded context, correctness, compatibility, scope, validation, documentation, and migration coverage.
---

Review the current planned implementation slice as a first revised draft.

Your task is to turn it into a complete, implementation-ready slice while preserving project correctness, compatibility, and scope discipline.

The revised slice must include all required:

- implementation work
- tests and validation
- documentation updates
- migration or compatibility work, if applicable
- cleanup required to leave the affected area internally consistent

Do not introduce breaking changes, unplanned architectural changes, incomplete transitions, speculative work, duplicated behavior, placeholder logic, or low-confidence implementation detail.

Prefer several small, narrowly focused slices over fewer broad slices.

## Ambiguity rule

If any material question remains unanswered, or if implementation depends on an assumption that cannot be verified from the repository, plan, project documentation, or established conventions:

Stop.

Do not revise past the ambiguity and do not choose an answer implicitly.

Ask the user the question and provide exactly three recommended solutions. For each solution, briefly state the relevant tradeoff.

Wait for the user's answer before continuing.

## Context-window constraint

A single implementation slice must be designed to use no more than 45% of the available context window during execution.

Estimate context pressure conservatively from:

- repository exploration required
- number of subsystems or architectural boundaries involved
- expected implementation breadth
- tests and validation required
- documentation and migration work
- likely debugging and integration effort

Do not treat 45% as a target. Prefer materially smaller slices when clean boundaries exist.

If the slice is likely to exceed 45% of the context window and its `<id>` does not contain `partial`:

1. Split it at the cleanest implementation boundary into two approximately equal execution workloads.
2. Preserve dependencies and ensure each part is independently coherent.
3. Keep the first part as the active slice using the original `<id>`.
4. Write the second part to the head of the roadmap source as:

   `<original-id>-partial2.md`

5. Move all work belonging to the second part out of the active slice. Do not duplicate requirements between parts except for minimal dependency/context notes.
6. Ensure partial 1 leaves the repository in a valid, tested state and does not require unfinished partial 2 changes to function correctly.

If a plan whose `<id>` already contains `partial` still exceeds the 45% limit, reduce its scope further at the cleanest boundary while preserving the project's existing partial-plan naming convention. Do not allow the context limit to be bypassed merely because the plan is already partial.

## Review requirements

Before rewriting the plan, verify that the proposed implementation:

- matches the slice's stated objective
- covers all affected production paths
- identifies relevant callers, consumers, interfaces, and invariants
- preserves backward compatibility unless an intentional breaking change is explicitly authorized
- includes failure paths and edge cases relevant to the change
- includes sufficient automated tests
- includes required documentation updates
- leaves no obsolete or contradictory implementation behind
- does not rely on future slices for correctness unless that dependency is explicitly part of an established staged migration
- stays within the intended architectural boundaries of the project

Remove unnecessary speculative detail, but retain enough specificity that the implementation agent does not need to redesign the slice while executing it.

## Plan quality

The final plan should describe outcomes, boundaries, affected areas, implementation steps, validation, and documentation requirements.

Avoid prescribing incidental implementation details unless they are required for correctness or architectural consistency.

Every implementation step should have a corresponding validation path where practical.

The slice must have explicit completion criteria so an implementation agent can determine when the work is finished.

## Finalization

Once the revised draft is complete:

1. Apply the context-window rule and split the slice if required.
2. Write the revised first part as the new current active plan.
3. Write any generated continuation slice to the roadmap source as specified above.
4. Rate the active slice's implementation difficulty from **1 to 10**, where:
   - 1 = trivial, localized, low-risk change
   - 5 = moderate cross-component implementation requiring meaningful testing
   - 10 = highly complex, cross-cutting, migration-heavy, or integration-sensitive work
5. Include a brief justification for the difficulty rating based on implementation complexity, coupling, validation burden, and risk.

The active plan must be complete enough to execute directly without rediscovering its intended design, while remaining narrow enough to stay comfortably within the context-window constraint.
