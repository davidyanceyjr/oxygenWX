# Plan 169 — Plan owner-approved effects track and reconcile authority

Status: Completed
Cycle ID: 169-effects-track-roadmap-and-docs
Roadmap item: UI.3
Created: 2026-10-11

## Objective

Add the owner-approved 40-outcome effects sequence to the UI context roadmap with stable FX references and actual UI identifiers, reconcile supporting authority and workflow documents, and record only verified documentation outcomes. No future FX implementation slice is activated or implemented.

## Production boundary

Documentation and cycle records only: `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, `docs/ROADMAP.md`, `docs/UI_CONTEXT_ROADMAP.md`, applicable theme/effects contracts and workflow guidance, plus `.codex` plan/history/evidence. No `app/`, Android resource, build, or runtime preference changes.

## Functional invariants

- Preserve all five existing themes and the Now → Hourly → Daily → Details semantics.
- Effects alter presentation only; keep all owner-listed data, provenance, chronology, request/cache, navigation, and accessibility invariants.
- Preserve persisted preference behavior, the internal `OFF` enum identity, reduced-motion and High contrast overlays, and effective fallback without rewriting saved intent.
- Keep historic approvals, completed evidence, and superseded work as recorded; mark future guidance superseded explicitly.
- Keep owner-approved navigation/menu intent separate from effects planning.

## Implementation steps

1. Inspect governing product/UI specifications, current roadmap/state, UI workflow, theme/design-pack/renderer contracts, effects resolver/preference/lifecycle implementation, and relevant completion evidence.
2. Allocate UI.3–UI.42 to FX.01–FX.40 without consolidating outcomes; write dependencies, bounded acceptance fields, estimates, evidence, limitations, and split rules for each entry.
3. Reconcile product/presentation/theme/workflow authority, retaining historical approvals and distinguishing installed evidence from design previews.
4. Record only those FX outcomes whose documentation work is verifiably complete; preserve untracked user material.
5. Run workflow, documentation/contract checks, inspect final diff and ensure production paths did not change.

## Acceptance criteria

- All 40 FX references have an explicit one-to-one UI mapping and dependencies, with the sequence and boundaries requested by the owner.
- Effects None/Subtle/Full, palette independence, theme-specific identity, saved-preference fallback, no-container Full target, semantic/data/network invariants, and separate navigation track are consistent across authority docs.
- Future-looking conflicting rules are marked superseded; historical evidence/status is unchanged.
- FX.01 and/or FX.02 are closed only if their outcomes are supported by inspected documentation and recorded evidence; all later entries remain planned.
- No production implementation slice is activated and no production source/resource is modified.
- `python scripts/dev.py workflow`, applicable documentation/contract validation, and `git diff --check` pass; final diff is reviewed.

## Verification and evidence

Preserve source inventory, mapping/dependency audit, exact validation commands and output, and final diff review under `.codex/test-artifacts/169-effects-track-roadmap-and-docs/`. No installed visual acceptance is claimed by this documentation cycle. Evidence for future visual outcomes must be retained under each future cycle's own evidence path.

## Risks and assumptions

- Theme design-pack files contain many candidate reference-level rules; edits must supersede only conflicting future effects guidance without rewriting historical decisions or claiming unresolved references are approved.
- Context estimates per future entry are planning judgments. Split before activation above 55% estimated usage, never activate above the repository's 65% hard ceiling.
- Existing renderer lifecycle/performance evidence has known limits; future FX.38 acceptance must define device/API thresholds before results exist.

## Out of scope

- Production rendering, resolver, lifecycle, preference, page integration, performance tuning, or release changes.
- Activating FX.03 or any other future implementation slice.
- Owner-approved bottom-corner navigation, page chooser, Search/Settings/About menu implementation or integration into effects.
- New design/reference approval, invented artwork specifications, visual acceptance claims from HTML previews, release promotion, or pull-request merge.
