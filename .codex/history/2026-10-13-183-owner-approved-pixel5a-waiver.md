# History — 183-owner-approved-pixel5a-waiver

Status: Completed
Cycle ID: 183-owner-approved-pixel5a-waiver
Roadmap item: SQ.1A
Closed: 2026-10-13
Plan: .codex/plans/183-owner-approved-pixel5a-waiver.md
Evidence: .codex/test-artifacts/183-owner-approved-pixel5a-waiver/

## Outcome

Recorded the owner's waiver of Pixel 5a as a required matrix member, accepting the ADB-identified Pixel 5 (redfin) as its replacement while retaining the Pixel 3 XL and 2023 Pixel Fold. Reconciled cycles 180–182 as the complete owner-approved physical baseline matrix. Recorded the owner's selection of Gate B, with Pixel Fold as the required named physical performance-evidence device and API 37 emulator evidence supplemental; SQ.1/SQ.1A are complete and SQ.2 is eligible for planning.

## Verification

Reviewed device matrices, paired four-page captures, and raw diagnostic records in cycles 180–182; same APK hash is retained across all three. Owner decisions recorded in the cycle decision artifact. Updated UI and main roadmaps; manual local path check for linked cycles 179–182 passed. python scripts/dev.py workflow passed; python scripts/codex_cycle.py validate passed; git diff --check passed. No production paths changed.

## Limitations / not verified

Pixel 5 remains identified as Pixel 5/redfin and is not represented as a Pixel 5a; the substitution is by owner-approved scope. Gate B is a selected policy, not evidence that static Full or motion passes. Its later measurement requires the physical Pixel Fold; the emulator is supplemental. The motion candidate timing window remains a later design/lifecycle decision. No devices were attached during this documentation cycle, and no fresh measurements were performed.

## Follow-up

Prepare a bounded SQ.2 plan for owner review of the four-page static Full design source using Gate B as the selected future performance gate; do not implement before design approval.
