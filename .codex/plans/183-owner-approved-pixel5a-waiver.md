# Plan 183 — Record owner-approved device matrix and performance gate decisions

Status: Completed
Cycle ID: 183-owner-approved-pixel5a-waiver
Roadmap item: SQ.1A
Created: 2026-10-13

## Objective

Record the owner's decision to waive Pixel 5a as a mandatory matrix device and accept the enrolled Pixel 5 (`redfin`) in its place. Reconcile cycles 180–182 into the accepted physical matrix (Pixel 3 XL, Pixel 5 `redfin`, and Pixel Fold `felix`) and update the roadmap to reflect the now-complete matrix. Also record the owner's adoption of candidate performance gate B with the Pixel Fold as the required physical performance-evidence device and the API 37 emulator as supplemental comparison, so SQ.1/SQ.2 can advance to bounded planning.

## Production boundary

Documentation and workflow state only. Do not modify application code, runtime assets, Android resources, or device settings. Preserve cycles 179–182 as historical records of the scope and decisions known at their close dates; record the new owner decision in this cycle and link it as the current authority.

## Functional invariants

- Pixel 5 `redfin` is not relabeled as a Pixel 5a; it is explicitly accepted by the owner as a substitute matrix member.
- The accepted readiness matrix retains Pixel 3 XL and Pixel Fold in addition to Pixel 5.
- Performance readiness limits do not become runtime or test claims before the relevant planned measurement and visual acceptance gates.
- Gate B is policy, not evidence that Static Full or conditional motion passes.

## Implementation steps

1. Reconcile the installed device records and diagnostics in cycles 180, 181, and 182 against the accepted three-device matrix; identify any missing required cells.
2. Record the owner's Pixel 5a waiver / Pixel 5 substitution and the selected candidate B caps and Pixel Fold physical evidence scope, including the emulator's supplemental role.
3. Update `docs/UI_CONTEXT_ROADMAP.md` and the Smoked Quartz branch status in `docs/ROADMAP.md`; keep earlier history statements as time-correct records and add the current decision's superseding authority.
4. Preserve a decision record and changed-path audit under `.codex/test-artifacts/183-owner-approved-pixel5a-waiver/`.
5. Run workflow, lifecycle validation, and `git diff --check`; close the cycle and review the history.

## Acceptance criteria

- The Pixel 3 XL, Pixel 5 (`redfin`), and Pixel Fold (`felix`) each have installed four-page offline-fixture evidence and interpretable capture/diagnostic records in cycles 180–182.
- The exact Pixel 5a waiver and Pixel 5 substitution, plus the adopted Gate B thresholds and Pixel Fold physical performance scope, are recorded as owner decisions.
- Roadmap text no longer presents the fulfilled SQ.1/SQ.1A or owner-decision dependency as blocked; SQ.2 is eligible for its next bounded plan.
- The record says clearly that gate selection is not a measured pass, and any unspecified motion presentation duration remains a later design/lifecycle boundary.
- No production paths change; workflow, cycle validation, and `git diff --check` pass.

## Verification and evidence

Use `.codex/test-artifacts/183-owner-approved-pixel5a-waiver/` for `owner-decisions.md`, `device-matrix-reconciliation.md`, `changed-path-audit.md`, `workflow.txt`, and `validation.txt`. Link the verified evidence directories and cycle histories 180–182. No app tests, build, or new physical-device connection are required; all device evidence already exists. Validate local links changed in the documentation.

## Risks and assumptions

- Interpret the owner's `no` response to “Should the Pixel 5a remain required ... or waive it and accept the Pixel 5 plus Pixel Fold as the device matrix?” as waiving the Pixel 5a slot and accepting Pixel 5 as the substitute. Preserve that exact wording and interpretation in the decision record.
- Gate B is adopted as listed in cycle 179's authority matrix. Keep its frame, memory, interaction, and motion-increment values exact. The candidate's phrase “within the approved motion window” does not adopt a numeric animation duration; later motion design/lifecycle review remains responsible for that detail.
- Candidate B uses the 16.67 ms 60 Hz deadline. The physical Fold is the required named device; closed outer and opened inner displays are distinct evidence configurations. API 37 emulator measurements are supplemental only.

## Out of scope

- Running Full design performance measurements or claiming the Static Full / motion gates pass.
- Selecting a new motion duration, approving candidate artwork, or implementing any Smoked Quartz source/runtime work.
- Closing SQ.2 design review; this cycle only records the gate/scope decisions and makes SQ.2 eligible for a new bounded plan.
- Enrolling additional devices or changing historical cycle records.
