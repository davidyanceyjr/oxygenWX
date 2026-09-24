# Plan 033B — TP.1D revised packet integration and independent audit

Status: Completed
Draft: Revised, revision 2
Cycle ID: 033-tp-1d-atmospheric-variants-symbol-map-partial-B
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A-partial-B
Parent item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A
Created: 2026-09-24
Dependency: cycles 033 and 034 are complete; their records and evidence are inputs. No owner disposition is part of this cycle.
Evidence: .codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/
Context budget: Target 30–40% of a fresh context and stop before 45%. This slice packages and independently audits existing material; it makes no new design decisions.

## Difficulty and recommended implementation model

- **Difficulty: 6/10.** The work is documentation-only, but it combines an immutable reference packet, two completed upstream changes, a versioned copy, and independent integrity and coverage checks.
- **Recommended Codex CLI model: GPT-6 Luna, medium reasoning.** The bounded file assembly and deterministic audit do not need a larger reasoning budget. Model availability may vary by account. See [OpenAI model selection](https://developers.openai.com/api/docs/guides/model-selection).

## Objective

Create an immutable, self-contained proposed TP.1D packet revision based on the frozen cycle-031 packet, incorporating the completed cycle-033 symbol map and cycle-034 Atmospheric palette proposal. Independently verify packet contents, provenance, references, decision state, and digests. Leave D28, D29, and D31 pending, including both D31 scene options, and preserve the prior packet unchanged.

## Production boundary

Documentation and reference artifacts only. Create the packet under `.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/packet/tp1d-proposed-r2-symbol033-palette034/`. Add a deterministic packet assembly/integrity audit tool and its output under this cycle's evidence directory. Update the theme-pack roadmap execution head to record the new handoff after audit. Do not edit approved design-pack sources, Android/runtime code, or the frozen cycle-031 packet.

Packet content is assembled from the cycle-031 packet, current approved design-pack inputs, and the exact cycle-033/034 outputs identified by their history records and evidence inventories. Copy the full existing reference set and design-pack contract needed to evaluate the 20 theme/page cells, the complete indexed example set, both Atmospheric palette modes, and the TP.3 checklist. Add a revision changelog and owner guide. Record packet-relative paths and source path plus SHA-256 for every copied/transformed source. Copying or normalizing a source requires a recorded transformation; never silently rewrite semantics or omit a changed upstream input.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, forecast values/chronology, condition identity, source/provenance, missing-data behavior, navigation, accessibility meaning, and existing geometry.
- Keep all five themes and all 20 primary cells. Preserve the current example index and include the approved cycle-033 mapping plus the cycle-034 proposed Atmospheric light-mode review material. The palette proposal changes colors only; it does not settle D31 or imply approval.
- Cycle-031 remains byte-for-byte unchanged. Its recorded packet manifest must still verify before and after this cycle.
- D28, D29, and D31 remain pending owner decisions unless the cited upstream records explicitly document a resolution; this plan expects no such resolution. Present both D31 options with equal treatment and no recommendation.
- The packet remains proposed and unapproved. TP.1D and TP.1 remain open; TP.2 remains gated. No decision is inferred from silence.

## Implementation steps

1. **Capture inputs and immutable baseline.** Record repository status. Verify every digest recorded for the cycle-031 packet, cycle-033 symbol-map outputs, and cycle-034 proposal/evidence. Save machine-readable input inventory and command output. If any accepted input is missing or does not match its recorded digest, stop assembly, record the mismatch, and do not refresh or bless it within this cycle.
2. **Define packet inventory before copying.** Derive the expected source set from the cycle-031 packet inventory and manifests, then add the exact cycle-033/034 changed sources and required supporting evidence. Record each source path, destination path, source digest, packet digest, and any documented transformation. Include source boards/crops with stable locators and digests. Keep generated comparison evidence to the minimum needed to review the two Atmospheric palette modes; do not copy unrelated build or working files.
3. **Assemble a new revision.** Copy inputs to `tp1d-proposed-r2-symbol033-palette034`. Update packet-local design-pack contracts, render/index descriptions, packet index, owner guide, and changelog only where required to represent the new revision and its added material. Preserve all unrelated primary cells and examples. Do not edit the cycle-031 packet or current authoritative design-pack sources.
4. **Close review fields without making owner decisions.** The owner guide must name the exact revision and manifest digest; explain that it is proposed and unapproved; provide the response choices `approve`, `revise`, or `reject`; list D28/D29/D31 as pending; quote both D31 options faithfully and neutrally; and state that the new palette mapping does not select a scene option. Keep approval, per-decision response, and date fields blank for the owner.
5. **Implement independent deterministic audit.** Add a standard-library Python audit script under this cycle evidence directory (not application source). It must independently recompute packet and source hashes and fail nonzero on: missing/extra packet files; duplicate or missing inventory paths; changed/missing cycle-031 files; mismatched input/source/packet hashes; unrecorded transforms; broken packet-local Markdown links or heading anchors; missing reference assets; incomplete 5×4 primary coverage; incorrect/duplicate index entries or absent indexed targets; missing cycle-033 condition-map coverage; absent light/dark Atmospheric proposal evidence; altered fixture facts/geometry outside the upstream-approved files; owner decision fields implying resolution; or missing/incorrect D28/D29/D31 state. Expected files and allowed diffs must come from explicit inventories, not from the audited output itself.
6. **Run independent review and record handoff.** Review the audit code against the manifest-generation code, inspect the complete source-to-packet diff, and verify packet content can be reviewed without relying on chat or mutable working files. Update only the theme-pack roadmap execution head to mark packet integration/audit complete when evidence passes and identify a fresh owner-disposition plan as the next dependent work, bound to this packet revision and digest. Do not create or decide that disposition in this cycle.

## Acceptance criteria

- The new revision is immutable, self-contained for review, and has a unique revision name, complete source inventory, SHA-256 manifest, and aggregate manifest SHA-256 documented in its owner guide and audit output.
- The inventory accounts for every packet file other than the manifest itself, and traces every copied reference to its source path/digest. The manifest accounts for all packet files using the documented exclusion rule.
- Independent audit passes for exact inventory/manifest coverage and hashes; unchanged cycle-031 packet; 20 unique theme/page cells; the complete current example index plus explicitly inventoried proposal examples; cycle-033 symbol-map coverage; both Atmospheric palette modes; packet-local links/anchors; and owner decision-state consistency.
- Only packet copies, cycle-specific audit/evidence, and the theme-pack roadmap execution head change. Current authoritative design-pack sources and unrelated files remain untouched.
- D28/D29/D31 remain open; no approval, installed-app result, TP.1D/TP.1 completion, or TP.2 eligibility is claimed.
- Context usage stays below 45%; if the audited packet work grows beyond this bound, stop before expanding scope and propose a separately bounded follow-up plan.

## Verification and evidence

Run and retain exact commands, output, tool/Python version, input/output digests, and limitations beneath `.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/`:

- `python scripts/dev.py workflow` before and after the work;
- `python scripts/dev.py contract` after packet/roadmap edits;
- verify the cycle-031 `SHA256SUMS.txt` before and after, and verify accepted upstream evidence files against their recorded inventories;
- run the new independent packet audit from a clean invocation and retain its machine-readable and human-readable results;
- verify the new manifest independently with a second command/path that does not import or call the audit's manifest-generation functions;
- inspect all expected packet-local links, heading anchors, indexed references, source paths, and the full packet diff;
- run `git diff --check` and inspect `git status --short` to confirm the boundary.

No Android build/install, runtime theme behavior, TalkBack, owner approval, or visual acceptance is required or claimed. Record these as unverified boundaries, not failures.

## Risks and assumptions

- The cycle-031 packet is the immutable base; cycles 033 and 034 are the accepted upstream work. Their closed history/evidence is the source of truth for exact changed paths and digests.
- The cycle-034 Atmospheric light palette is a derived proposal and lacks direct light-specific source art. Carry that limitation into the packet; do not describe it as source-approved or use it to resolve D31.
- Cycle 033's condition map may alter indexed example assets while leaving the 20 primary cells unchanged. Expected differences must be based on its reviewed output inventory.
- The palette proposal includes unindexed static review examples. Include only those specifically identified by cycle-034 evidence, and list them explicitly so they do not silently change the established index contract.
- A digest mismatch invalidates the proposed packet inputs. Record the mismatch and hand back for a separately reviewed upstream correction; do not update a baseline digest to make the audit pass.

## Out of scope

- Editing current approved design-pack sources, creating/revising artwork or visual decisions, or changing source semantics.
- Owner disposition/approval, choosing either D31 scene option, resolving D28/D29/D31, closing TP.1D/TP.1, or enabling TP.2.
- Android/Kotlin/Compose/runtime work, installed comparisons, TalkBack/accessibility certification, appearance preferences, weather semantics, provider behavior, or production tests.
- Modifying or regenerating the cycle-031 packet, broad documentation rewrites, unrelated roadmap edits, and unrelated reference/render changes.
