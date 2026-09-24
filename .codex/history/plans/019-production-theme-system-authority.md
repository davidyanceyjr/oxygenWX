# Plan 019 — Production theme-system authority

Status: Completed
Cycle ID: 019-production-theme-system-authority
Roadmap item: R0.11A
Created: 2026-09-22

## Objective

Organize the supplied production-theme design package into permanent repository-owned documentation/reference paths and synchronize the product, UI, architecture, and roadmap authority for the five built-in themes.

## Production boundary

Documentation, design-reference assets, theme token/specification documents, prompts, staged reference materials, roadmap records, and cycle evidence only. No application source, runtime resources, tests, or behavior changes.

## Functional invariants

- Standard Home remains Now -> Hourly -> Daily -> Details, with the outer pager as the sole global horizontal-swipe owner and explicit Hourly/Daily window navigation.
- Themes change presentation only; weather meaning, chronology, provenance/freshness, missing-data behavior, alerts, accessibility, and navigation remain invariant.
- Effects Off remains opaque, static, and complete.
- Compose/presentation/domain/provider boundaries remain unchanged; theme selection never refetches weather.
- Theme art remains design-reference material, and staged candidates remain quarantined outside app source/resource paths.

## Implementation steps

1. Inventory and classify every intake file; inspect collisions and repository asset conventions.
2. Move useful references, tokens, design/architecture documents, prompts, and staged candidates to permanent paths; discard obsolete or superseded package instructions/drafts deliberately.
3. Synchronize specification, adopted UI specification, architecture, and ordered R0/R5/R6 roadmap entries with the approved theme-system direction and permanent asset paths.
4. Verify asset checksums/paths, workflow and contract checks, diff cleanliness/scope, and record evidence.
5. Remove the intake directory only after confirming its remaining contents are unnecessary; close the cycle with actual results.

## Acceptance criteria

- Five built-in themes are consistently named Atmospheric, Glass, Minimal OLED, Instrument, and Terminal.
- At the time of close, R0.11A–R0.11H were ordered after R0.11 and before R1, with R0.11A NEXT and successors PLANNED. After this work was reconciled, R0.11A is DONE and R0.11B is NEXT.
- Design references, token catalog, architecture/contract documentation, useful prompts, and all staged candidates are organized under permanent docs paths.
- Historical Theme B references remain intact; docs describe the current implementation as a sketch/baseline rather than the final production visual target.
- No documentation path points to the removed intake directory.
- No production Kotlin, tests, Android resources, application behavior, or dependencies change.
- Intake directory is removed after complete file accounting.

## Verification and evidence

Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and `git diff --check`; inspect `git status --short` and the full diff. Verify staged file paths do not enter app source/resource paths, all linked assets exist, checksums match, roadmap order/status is correct, and intake directory is absent. Record command results and scope checks under `.codex/test-artifacts/019-production-theme-system-authority/` and in the close history record.

## Risks and assumptions

- Package proposals were prepared against an earlier revision; apply only their approved intent after reconciling current repository authorities.
- Full concept boards are reference material, not pixel-exact or runtime assets.
- Useful implementation candidates may age; staged files are non-authoritative reference scaffolding.

## Out of scope

- Production Kotlin, Android resources, tests, dependencies, renderer/resolver implementation, or application behavior.
- Any R0.11B work or future theme implementation.
- Removing existing Theme B historical references or unrelated cleanup.

## Post-close roadmap reconciliation

The cycle completed the authority/assets slice now identified as R0.11A. Its original close-time verification recorded the then-current roadmap status (R0.11A NEXT); the repaired roadmap now marks R0.11A DONE and R0.11B NEXT. Staged source candidates live under `docs/theme-system/staged-production/` and were not added to Android source/resource roots.
