# Initial plan — TP.1D-partial-A-partial-B-partial-A-partial-A owner packet and decision

Status: Draft; dependent on cycle 030 checklist closure
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A
Parent item: TP.1D-partial-A-partial-B-partial-A
Created: 2026-09-24
Context budget: Target 30–35% of a fresh context window; stop before 45%. Review cycle 030's handoff, then create and activate a separate `.codex` cycle with its own ID and evidence path.
Planned implementation difficulty: 7/10

## Objective

Freeze the verified proposed design and cycle-030 installed comparison checklist as one reproducible owner packet. Obtain and record an explicit design-owner disposition for that exact revision. Close TP.1D/TP.1 and release the TP.2 dependency only if the exact packet is approved and all open decisions are resolved.

## Production boundary and required document updates

- Consume cycle 029's reviewed 20-cell revision and cycle 030's checklist, audits and handoff. Re-audit only files that changed after their recorded review; do not silently adopt drift.
- Build a read-only snapshot under the new cycle's `.codex/test-artifacts/` path: 20 primary SVGs, twelve condition SVGs, render index/fixture/generator, integrated and page/foundation/content contracts, checklist, source decisions, source/asset map with used-source hashes, upstream measurement/contrast/cross-pack evidence, a change log and owner-facing index. Label the revision and write a SHA-256 manifest for every snapshot file. Keep the manifest and later decision outside the hashed payload. Verify every digest, local link and 32 indexed references from the snapshot.
- State D28/D29/D31 options, affected cells, consequences and exact response requested in the owner index. Record owner identity, date, approve/revise/reject/pending outcome, exact revision and manifest digest, and disposition of each open choice in a decision record outside the snapshot. A changed reviewed file requires a new labeled packet and affected-cell review before another decision. Silence remains pending.
- Update design-pack `README.md`, `INTEGRATED_PACK.md`, render README/index description, `SOURCE_DECISIONS.md`, and `docs/theme-pack-roadmap.md` only to reflect actual packet and decision status. TP.1D/TP.1 cannot close and TP.2 cannot activate without approval of the exact verified packet. No Android production or weather-semantic changes.

## Functional invariants

Preserve the four-page contract, one outer pager, visible Hourly/Daily controls, supplied facts/chronology/units, missing and load-state meaning, source/update/valid time, provenance, visible accessible text, Effects Off completeness and non-color cues. Static packet approval does not establish installed visual or interaction acceptance.

## Implementation steps

1. Check both upstream closures, 20+12 inventory, input hashes, checklist links and D28/D29/D31 options. Record any changed reviewed file and resolve its affected-cell audit before freezing.
2. Assemble and independently verify the immutable snapshot, revision label, SHA-256 manifest and owner index. Include exact source-art attribution and a concise change log. Keep owner decision outside the hashed snapshot.
3. Present the complete packet for explicit owner disposition. Record the response against the manifest digest and each conflict. For requested revisions, make only bounded supported corrections, regenerate/review affected references, freeze a new revision and request a fresh disposition; split again before 45% if necessary.
4. On approval, reverify snapshot and update pack/roadmap gates before closing history. For pending/revise/reject, record status and next action with TP.1D/TP.1 open and TP.2 gated. Silence is never approval.

## Verification and evidence

- Run workflow and contract checks; audit snapshot inventory, 20 unique cells, twelve examples, local links, exact fixture/index fields and source/asset hashes. Independently recompute every manifest digest and verify the decision identifies that revision and digest.
- For changed references after cycles 029/030, inspect affected viewport/full/end captures and relevant contrast. Review owner index for D28/D29/D31 options and response fields.
- Run `python scripts/dev.py check` when Android tooling is available as a regression gate; run `git diff --check` and inspect final tracked/new-file diff. Record exact checks, packet files, manifest, decision, unavailable checks and installed limits in the new cycle evidence.

## Acceptance criteria

- Frozen packet reproduces all 32 references and governing documents; every payload digest and reference path verifies. The owner decision identifies its exact immutable revision and resolves each open choice.
- TP.1D/TP.1 close and TP.2 becomes eligible only after explicit approval of that revision. Pending/revise/reject retains the gate and a concrete next action.
- Cycle history states exact verification, owner disposition, packet location and unverified installed-app boundaries.

## Risks and assumptions

- Owner response is external. Finish the reviewable packet before requesting it; leave the gate pending until a real response arrives.
- Font, mark or Atmospheric scene revision may affect many renders and invalidate hashes. Re-scope before 45%.

## Out of scope

- TP.2/TP.3 implementation or installed acceptance; weather, provider, settings, navigation or alert changes.
