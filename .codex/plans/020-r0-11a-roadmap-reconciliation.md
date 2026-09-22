# Plan 020 — R0.11A roadmap reconciliation

Status: Completed
Cycle ID: 020-r0-11a-roadmap-reconciliation
Roadmap item: R0.11A
Created: 2026-09-22

## Objective

Repair the inserted production-theme roadmap IDs and reconcile the completed authority slice with its original plan/history. Keep completed R0.1–R0.11 and later R1+ identifiers stable; append the inserted theme sequence as R0.11A–R0.11H.

## Production boundary

Documentation and `.codex/` lifecycle records only: `docs/ROADMAP.md`, the current cycle plan/status, and cycle-specific evidence/history. No Kotlin, tests, runtime resources, or product behavior changes.

## Functional invariants

- Production themes remain Atmospheric, Glass, Minimal OLED, Instrument, and Terminal.
- Theme changes remain presentation-only; weather meaning, chronology, provenance, missing-data behavior, alert meaning, accessibility, and navigation remain invariant.
- Effects Off remains opaque, static, and complete.
- The Theme B renderer remains described as a historical implementation sketch/baseline.
- Do not modify or remove the untracked `oxygenwx-theme-pack-governed-intake.zip` as part of this reconciliation.

## Implementation steps

1. Compare R0.11A acceptance criteria with the existing plan 019, history 019, theme-system documentation, manifests, and repository-owned reference paths.
2. Verify referenced paths and recorded workflow/contract evidence as appropriate; distinguish existing recorded verification from checks newly run in this cycle.
3. If evidence supports completion, update R0.11A from NEXT to DONE and retain only the next eligible roadmap item as NEXT; otherwise document the specific unmet criterion without repeating completed work.
4. Preserve reconciliation evidence and close the cycle with an accurate history record.

## Acceptance criteria

- The inconsistency between R0.11A roadmap status and its existing completion record is resolved explicitly.
- Any status change is supported by inspected repository evidence and accurately described verification.
- The untracked intake ZIP remains untouched.
- No production rendering, application behavior, or runtime resources change.
- Workflow validation and `git diff --check` pass; evidence and limitations are recorded before closure.

## Verification and evidence

Inspect the complete R0.11A documentation, manifest/path coverage, existing plan/history, and git scope. Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and `git diff --check` after any status update. Store command output and scope notes under `.codex/test-artifacts/020-r0-11a-roadmap-reconciliation/`; close with exact results. No Android build or installed visual evidence is needed because production rendering is out of scope.

## Risks and assumptions

- Plan 019 and its history claim the R0.11A documentation/assets work was completed, while `docs/ROADMAP.md` still labels it NEXT.
- The intake ZIP is an existing untracked artifact explicitly left untouched by the prior history record; it is not presumed to be an unmet acceptance criterion.
- R0.11B must not begin until the roadmap and evidence are reconciled and this cycle is closed.

## Out of scope

- Reorganizing or regenerating the theme package, copying or deleting the intake ZIP, or changing design assets.
- Production resolver, components, page migration, theme persistence, or any Android source/resource changes.
- R0.11B and later implementation work.


The roadmap order is R0.11, then the inserted R0.11A–R0.11H theme sequence, then R1. Existing downstream IDs are unchanged. R0.11A is completed by cycle 019; R0.11B is next because staged candidates are reference material, not implementation.
