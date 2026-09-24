# Plan 035 — Bounded exits for every remaining roadmap slice

Status: Completed
Cycle ID: 035-roadmap-bounded-exits
Roadmap item: Roadmap bounded-exit audit
Created: 2026-09-24
Context budget: Documentation-only audit; keep within one cycle and record exact scope in history.

## Objective

Give every unfinished implementation slice in `docs/ROADMAP.md` and `docs/theme-pack-roadmap.md` an explicit finite exit: an observable deliverable, verification boundary, and rule for unresolved or failed acceptance. Replace open-ended completion wording where it permits indefinite iteration.

## Production boundary

Documentation only: `docs/ROADMAP.md`, `docs/theme-pack-roadmap.md`, this plan, current cycle pointer, and cycle evidence/history. No application code, design references, packet content, or existing evidence is changed.

## Functional invariants

- Preserve specification authority, product meaning, existing dependencies, and current TP.1D status.
- Keep TP.2 gated until TP.1 is explicitly approved; never infer owner approval.
- Every unfinished roadmap slice gets its own finite acceptance/stop condition. Deferred R8 work remains deferred and is not silently promoted to release scope.
- Visual acceptance must name actual installed screenshot comparison evidence; generated SVGs and compilation alone are not visual acceptance.
- If a finite cycle ends without acceptance, record the blocker and stop; do not create an automatic chain of follow-up slices.

## Implementation steps

1. Inventory every PLANNED/ACTIVE remaining entry, plus deferred entries, in both roadmaps.
2. Add concise slice-specific exit criteria with observable artifact/test/capture counts where relevant, plus a terminal blocker rule.
3. Bound TP.1D owner disposition, TP.2 shared renderer work, and TP.3 installed page comparison with explicit inputs, outputs, and capped correction rounds.
4. Reconcile the current plan and cycle pointer to this documentation slice; run workflow/contract/diff checks and inspect the final diff.

## Acceptance criteria

- Every remaining non-DONE item in both roadmaps has an explicit exit criterion in that entry.
- TP.1D has one finite disposition attempt against the pinned packet; missing/ambiguous decisions end that cycle as pending and keep TP.1/TP.2 gated.
- TP.2 and TP.3 have finite page/component/theme matrices and capped correction iterations; unresolved blockers end the slice without an automatic follow-up loop.
- TP.3 explicitly requires installed app screenshots compared side by side with approved references and cannot claim completion from SVGs/builds alone.
- Workflow, contract, `git diff --check`, and final diff review pass; history records exact verification and limitations.

## Verification and evidence

Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and `git diff --check`. Save the unfinished-entry inventory and command outputs under `.codex/test-artifacts/035-roadmap-bounded-exits/`. No Android build/install is needed because this cycle changes no runtime code.

## Risks and assumptions

- A roadmap exit can be finite while still ending blocked when a required external owner decision or environment is missing.
- Specific evidence counts define scope, not a claim that evidence already exists.

## Out of scope

- Owner disposition or changes to the packet.
- Runtime implementation, visual generation, installing the app, or claiming visual acceptance.
- Promoting R8 deferred experiments or changing release/product scope.
