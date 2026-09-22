# Plan 020 — R0.12 authority completion-state reconciliation

Status: Active
Cycle ID: 020-r0-12-authority-state-reconciliation
Roadmap item: R0.12
Created: 2026-09-22

## Objective

Reconcile the R0.12 roadmap status with its existing completed plan/history evidence. Confirm the documented acceptance is represented in the repository and update the persistent roadmap state only when the evidence supports it. This resolves workflow state; it does not repeat the completed authority implementation.

## Production boundary

Documentation and `.codex/` lifecycle records only: `docs/ROADMAP.md`, the current cycle plan/status, and cycle-specific evidence/history. No Kotlin, tests, runtime resources, or product behavior changes.

## Functional invariants

- Production themes remain Atmospheric, Glass, Minimal OLED, Instrument, and Terminal.
- Theme changes remain presentation-only; weather meaning, chronology, provenance, missing-data behavior, alert meaning, accessibility, and navigation remain invariant.
- Effects Off remains opaque, static, and complete.
- The Theme B renderer remains described as a historical implementation sketch/baseline.
- Do not modify or remove the untracked `oxygenwx-theme-pack-governed-intake.zip` as part of this reconciliation.

## Implementation steps

1. Compare R0.12 acceptance criteria with the existing plan 019, history 019, theme-system documentation, manifests, and repository-owned reference paths.
2. Verify referenced paths and recorded workflow/contract evidence as appropriate; distinguish existing recorded verification from checks newly run in this cycle.
3. If evidence supports completion, update R0.12 from NEXT to DONE and retain only the next eligible roadmap item as NEXT; otherwise document the specific unmet criterion without repeating completed work.
4. Preserve reconciliation evidence and close the cycle with an accurate history record.

## Acceptance criteria

- The inconsistency between R0.12 roadmap status and its existing completion record is resolved explicitly.
- Any status change is supported by inspected repository evidence and accurately described verification.
- The untracked intake ZIP remains untouched.
- No production rendering, application behavior, or runtime resources change.
- Workflow validation and `git diff --check` pass; evidence and limitations are recorded before closure.

## Verification and evidence

Inspect the complete R0.12 documentation, manifest/path coverage, existing plan/history, and git scope. Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and `git diff --check` after any status update. Store command output and scope notes under `.codex/test-artifacts/020-r0-12-authority-state-reconciliation/`; close with exact results. No Android build or installed visual evidence is needed because production rendering is out of scope.

## Risks and assumptions

- Plan 019 and its history claim the R0.12 documentation/assets work was completed, while `docs/ROADMAP.md` still labels it NEXT.
- The intake ZIP is an existing untracked artifact explicitly left untouched by the prior history record; it is not presumed to be an unmet acceptance criterion.
- R0.13 must not begin until the roadmap and evidence are reconciled and this cycle is closed.

## Out of scope

- Reorganizing or regenerating the theme package, copying or deleting the intake ZIP, or changing design assets.
- Production resolver, components, page migration, theme persistence, or any Android source/resource changes.
- R0.13 and later implementation work.
