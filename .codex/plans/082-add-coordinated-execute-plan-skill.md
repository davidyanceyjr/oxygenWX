# Plan 082 — Add reusable plan-selection and execution skills

Status: Completed
Cycle ID: 082-add-coordinated-execute-plan-skill
Roadmap item: DX.3
Created: 2026-09-30

## Objective

Add standalone project-local Codex skills for the user-supplied first-draft
plan-selection and coordinating-agent execution prompts, preserving both texts
verbatim and making them discoverable by valid kebab-case names (`slice-select`,
`execute-plan`).

## Production boundary

Developer workflow only: `.agents/skills/execute-plan/` and
`.agents/skills/slice-select/` plus their skill UI metadata. No Android app or
runtime behavior changes.

## Functional invariants

- Preserve each supplied prompt text exactly, including ambiguity handling,
  delegation policy, subagent boundaries/output contract, and Android
  SDK/emulator note.
- Existing Oxygen workflow skills remain unchanged.
- Valid skill names use lowercase letters, digits, and hyphens.

## Implementation steps

1. Create standalone `execute-plan` and `slice-select` skills with concise
   picker metadata.
2. Verify exact prompt retention in both skills, YAML/frontmatter, workflow,
   and diff checks.
3. Close the cycle with evidence and limitations.

## Acceptance criteria

- Each skill body preserves its corresponding user prompt verbatim.
- Both skills use valid metadata and discoverable kebab-case names.
- `python scripts/dev.py workflow` and `git diff --check` pass.
- No production app files change.

## Verification and evidence

Run exact-string checks for both prompts, Ruby YAML parsing for frontmatter and
UI metadata, `python scripts/dev.py workflow`, and `git diff --check`. Retain
results under `.codex/test-artifacts/082-add-coordinated-execute-plan-skill/`.

## Risks and assumptions

- Requested identifiers use underscores; valid skill names are normalized to
  kebab-case (`execute-plan`, `slice-select`).

## Out of scope

- Edits to the existing Oxygen-prefixed workflow skills.
- Shell aliases, app code, or automatic task execution.
