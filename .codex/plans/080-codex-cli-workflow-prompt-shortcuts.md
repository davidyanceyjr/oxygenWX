# Plan 080 — Codex CLI workflow prompt shortcuts

Status: Completed
Cycle ID: 080-codex-cli-workflow-prompt-shortcuts
Roadmap item: DX.1
Created: 2026-09-30

## Objective

Provide three short, reusable choices in Codex CLI for roadmap analysis and
draft creation, plan review, and active-plan execution, removing the need to
search terminal history for their full text.

## Production boundary

Developer workflow only: add project-local skills under `.agents/skills/` and
document their use in `docs/CODEX.md`. No Android app or runtime behavior changes.

## Functional invariants

- The existing persistent cycle lifecycle and its files remain authoritative.
- Draft/review stages do not silently start implementation.
- Execution follows the activated plan's scope, verification, and closeout rules.
- Agent delegation is available for suitable bounded implementation tasks, not
  mandated for tasks that do not benefit from parallelism.

## Implementation steps

1. Add a DX.1 roadmap entry and activate this bounded workflow slice.
2. Create three discoverable, concise project skills for the requested stages.
3. Document how to select them through Codex CLI `/skills`.
4. Validate skills, workflow state, and patch formatting; close with actual results.

## Acceptance criteria

- The three skill names and stage purposes are clear from discovery metadata.
- Each skill provides the corresponding workflow prompt and respects cycle state.
- The `/skills` invocation is documented in `docs/CODEX.md`.
- Workflow and documentation checks pass; no production app files change.

## Verification and evidence

Run the skill creator's `quick_validate.py` against all three skills,
`python scripts/dev.py workflow`, and `git diff --check`. Retain command output
under `.codex/test-artifacts/080-codex-cli-workflow-prompt-shortcuts/`.

## Risks and assumptions

- Assumes the user runs the Codex CLI TUI and wants selectable reusable prompts;
  official CLI documentation confirms the `/skills` picker.
- Repo skills are discovered from `.agents/skills/` when Codex starts in the
  repository. A running session may need to restart to discover new skills.

## Out of scope

- Global shell aliases or terminal keybindings.
- Automating an entire lifecycle in one prompt or bypassing owner decisions.
- Changes to app, tests, data, or visual presentation.
