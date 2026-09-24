# Plan 031 — TP.1D proposed-pack packet freeze

Status: Completed
Cycle ID: 031-tp-1d-owner-packet-decision
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A
Parent item: TP.1D-partial-A-partial-B-partial-A-partial-A
Created: 2026-09-24
Revised: 2026-09-24
Evidence: .codex/test-artifacts/031-tp-1d-owner-packet-decision/
Context budget: Target 20–30% of a fresh context; stop before 45%. This slice freezes and audits the proposed packet. Owner disposition is a separate dependent slice because it requires external input and may trigger a new reference revision.

## Objective

Create a reproducible, read-only packet for owner review from the cycle 029 proposed design revision and cycle 030 installed comparison checklist. The packet must identify its exact revision, preserve source trace, and pass independent inventory, link, and SHA-256 checks. It does not decide D28, D29, or D31 or close TP.1D/TP.1.

## Production boundary

Cycle evidence and documentation only. Build the immutable payload under `.codex/test-artifacts/031-tp-1d-owner-packet-decision/packet/<revision>/`. Keep a packet index, revision change log, source inventory, and SHA-256 manifest inside the payload. Store independent audit output beside the packet. Do not edit the source design, render, fixture, generator, token, app, or weather files.

Include the following reviewed inputs, using repository-relative paths in the source inventory and packet-local paths for navigable packet content:

- the 20 primary SVG references and twelve indexed example SVGs;
- render index, fixture, and generator;
- integrated pack, Now, Hourly, Daily, Details, foundation, content/state, measurement method, source decisions, and installed comparison checklist;
- design-pack and render indexes/READMEs;
- only the cycle 029/030 measurement, contrast, source, and review evidence needed to support the packet's claims, with retained evidence paths for the rest;
- a concise owner guide with D28, D29, and D31 choices and exact response fields.

Copy source-art evidence into the packet only where the owner guide or a decision depends on viewing it. Otherwise record the repository-relative source path and digest; do not duplicate unrelated cycle captures. The decision record is a later dependent-cycle artifact outside the frozen payload.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, one global horizontal-swipe owner, visible page identity, Back behavior, and visible bounded Hourly/Daily controls.
- Preserve the supplied six chronological hourly and five daily entries, units, null/missing behavior, source/update/valid time, load-state meaning, and derived/history provenance.
- Add no reference-only weather fact, placeholder, fabricated forecast entry, or official-alert claim.
- Label the materials as proposed static design references. A packet, SVG, or checklist is not installed-app, interaction, accessibility-service, translated RTL, or owner-approval evidence.
- Keep D28 fonts, D29 marks, and D31 Atmospheric treatment explicitly open; do not infer a decision.

## Implementation steps

1. **Reconcile exact inputs.** Read the cycle 029/030 histories, evidence indexes, proposed revision and checklist. Record the initial worktree state. Inventory the 20 primary cells and twelve examples from the render index. Compare every copied source with its recorded cycle revision/hash. If a reviewed input has drifted, stop inclusion of that input and record the exact affected-cell/checklist re-review needed; do not silently refresh the proposed revision.
2. **Assemble the packet.** Create the revision-labeled directory and copy only the bounded source set above. Preserve a source inventory containing original path, packet path, source digest, and review provenance. Make packet links relative and packet-local when the target is included. The owner guide must state the proposed status, packet revision, D28/D29/D31 alternatives, affected references, consequence of each choice, and a precise reply template. Include a change log and source-art attribution where relevant.
3. **Freeze and audit independently.** Generate a deterministic SHA-256 manifest over every payload file except the manifest itself. Run an independent audit that checks manifest coverage/digests, 20 unique theme/page cells, twelve distinct indexed examples, every render-index target, packet-local links/anchors, source inventory digests, and consistency of the revision label. Save the audit tool/command and complete output beside the payload. Freeze the payload after the audit; any edit requires a new revision directory and manifest.
4. **Handoff without claiming disposition.** Update the design-pack README/integrated-pack pointer only if needed to expose the frozen packet and its proposed status. Update the theme-pack roadmap to mark this packet-freeze partial complete, identify the dependent owner-disposition plan as next, and keep TP.1D/TP.1 open and TP.2 gated. Record exact verification and limitations in cycle evidence. Do not contact another person with external tools; the packet can be presented to the user in the final response.

## Acceptance criteria

- The packet contains the bounded source set, exact revision label, owner guide, source inventory, change log, and complete manifest.
- Inventory checks find 20 distinct primary cells and twelve indexed examples; all included render targets and packet-local links/anchors resolve.
- An independent recomputation verifies every manifest digest and included-source digest. Audit output is retained under the cycle evidence path.
- D28/D29/D31 remain open and explicit. Roadmap and packet language make no claim of owner approval, installed success, or TP.1 completion; TP.2 remains gated.
- `.codex/current.md` continues to point at this active cycle until it is accurately closed with history.

## Verification and evidence

- Before edits, `python scripts/dev.py workflow` has passed for the existing active cycle. Run it again after edits, plus `python scripts/dev.py contract` for the revised roadmap and linked documentation contracts.
- Run the cycle-local packet audit from a clean invocation after assembly. It must exit nonzero on a missing/extra manifest member, digest mismatch, duplicate/absent cell or example, broken packet-local link, or missing indexed target. Retain its source and full output under `.codex/test-artifacts/031-tp-1d-owner-packet-decision/`.
- Record exact source drift comparison and any required re-review. A drift requiring new visual review is a stop condition for packet freeze, not a waived check.
- Run `python scripts/dev.py check` as a repository regression gate when Android tooling is available; it does not establish visual acceptance and is not a substitute for the packet audit. Run `git diff --check` and inspect the final diff against the initial worktree status.
- This is not a visual implementation slice: do not install/render the app or claim compact, large-font, RTL, Effects Off, touch, or TalkBack verification. Record installed-app and service-level accessibility boundaries as unverified.

## Risks and assumptions

- Cycle 029/030 evidence describes a proposed reference revision; owner choices may make the packet stale. A changed design requires a new, separately reviewed revision before disposition.
- The source art is used for review provenance, not as runtime assets or a licensing determination.
- The packet audit can verify files and links but cannot decide visual quality or owner intent.
- If source drift requires reference redesign/review or the work approaches 45% context, stop and plan a smaller upstream slice before broadening scope.

## Out of scope

- Obtaining/recording owner response or resolving D28/D29/D31; that is the dependent owner-disposition slice.
- Changing design references, design tokens, generator, fixtures, Android/Kotlin/Compose/resources, data models, weather meaning, navigation, alerts, or providers.
- TP.2/TP.3 implementation, installed-app comparison, or closure of TP.1D/TP.1.
