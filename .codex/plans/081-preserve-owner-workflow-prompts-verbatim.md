# Plan 081 — Preserve owner workflow prompts verbatim

Status: Completed
Cycle ID: 081-preserve-owner-workflow-prompts-verbatim
Roadmap item: DX.2
Created: 2026-09-30

## Objective

Revise the three Oxygen workflow skills to use the owner's original prompts
verbatim as their primary requests, preserving repository cycle rules as
supporting constraints.

## Production boundary

Developer workflow content only: the three `SKILL.md` files under
`.agents/skills/`. No Android app behavior or unrelated workflow configuration.

## Functional invariants

- Preserve each owner-provided prompt exactly, including its stage intent.
- Planning creates a current `PLANNED` draft, review completes that draft, and
  execution operates on the reviewed/activated plan.
- Keep the repository lifecycle and scope safeguards as supporting constraints.

## Implementation steps

1. Record the exact three prompt strings in the roadmap and plan.
2. Update each skill so its prompt is the primary task instruction.
3. Verify literal prompt matches, metadata parsing, cycle workflow, and diff.

## Acceptance criteria

- Each skill contains its corresponding prompt verbatim as a prominent primary
  request.
- The earlier paraphrased objective is removed or clearly subordinated.
- Stage boundaries and lifecycle constraints remain intact.
- YAML/frontmatter, workflow, and diff checks pass.

## Verification and evidence

Run exact-string checks for all three prompts, Ruby YAML parsing for skill
frontmatter and UI metadata, `python scripts/dev.py workflow`, and
`git diff --check`. Retain output in
`.codex/test-artifacts/081-preserve-owner-workflow-prompts-verbatim/`.

## Risks and assumptions

- The prompt strings are taken from the user's original request in this
  conversation and should not be grammar-corrected or paraphrased.

## Out of scope

- Changes to cycle-tool behavior or the broader roadmap ordering.
- App, test, data, or visual presentation work.
