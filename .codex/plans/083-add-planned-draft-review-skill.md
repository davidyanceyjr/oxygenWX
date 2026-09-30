# Plan 083 — Add planned-draft review skill

Status: Completed
Cycle ID: 083-add-planned-draft-review-skill
Roadmap item: DX.4
Created: 2026-09-30

## Objective

Add a standalone project-local Codex skill containing the owner's planned-draft
review prompt verbatim, discoverable under the kebab-case name
`review-planned-draft`.

## Production boundary

Developer workflow only: `.agents/skills/review-planned-draft/` and its picker
metadata. No Android app or runtime behavior changes.

## Functional invariants

- Preserve the prompt's ambiguity stop/wait rule, scope, review requirements,
  context-window split rule, finalization requirements, and difficulty rating.
- Existing skills remain unchanged.
- Use valid kebab-case skill metadata.

## Implementation steps

1. Create the standalone skill and concise picker metadata.
2. Check exact prompt retention, YAML/frontmatter, workflow, and diff.
3. Close the cycle with evidence and limitations.

## Acceptance criteria

- The skill body preserves the supplied prompt verbatim.
- Frontmatter and picker metadata parse and use the matching skill name.
- `python scripts/dev.py workflow` and `git diff --check` pass.
- No production app files change.

## Verification and evidence

Run exact-text presence checks, Ruby YAML parsing for frontmatter and UI
metadata, `python scripts/dev.py workflow`, and `git diff --check`. Retain
results under `.codex/test-artifacts/083-add-planned-draft-review-skill/`.

## Risks and assumptions

- Requested identifier uses an underscore; normalize to valid kebab-case
  `review-planned-draft` while preserving the prompt body exactly.

## Out of scope

- Editing existing skills or changing cycle-tool behavior.
- Performing roadmap analysis or plan review from the prompt itself.
- App, tests, data, or visual presentation changes.
