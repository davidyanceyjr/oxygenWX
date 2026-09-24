# Initial plan — TP.1D owner disposition

Status: Planned; activate only after the packet-freeze partial closes
Cycle ID: 032-tp-1d-owner-disposition
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-B
Parent item: TP.1D-partial-A-partial-B-partial-A-partial-A
Dependency: completed packet-freeze cycle 031 and an explicit owner response to its exact revision and SHA-256 manifest
Context budget: Target 10–15% of a fresh context; stop before 45%. If the owner requests design or reference changes, do not absorb them here; write a new bounded upstream plan.

## Objective

Record the design owner's explicit disposition of D28, D29, and D31 against the exact frozen packet revision and manifest digest. Keep TP.1D/TP.1 open and TP.2 gated unless all three decisions explicitly approve that revision.

## Production boundary

Decision record and roadmap/design-pack status only. Add `.codex/test-artifacts/032-tp-1d-owner-disposition/decision.md` beside the immutable cycle-031 packet. The record identifies owner, response date, packet revision, full manifest digest, overall approve/revise/reject status, and a disposition plus rationale for D28, D29, and D31. Do not edit the packet or its manifest.

Update `SOURCE_DECISIONS.md`, design-pack summaries, and `docs/theme-pack-roadmap.md` only to reflect the actual response. If approval is explicit for the exact verified revision and resolves all three decisions, TP.1D/TP.1 may close and TP.2 may become eligible. For pending, partial, revise, or reject, keep both gates open and record the next bounded action. Do not contact a person through external tools.

## Functional invariants

- Do not infer consent from silence, ambiguous language, prior review, or approval of a different packet digest.
- Do not alter the frozen packet after disposition; any change requires a new packet revision and fresh disposition.
- D28 remains about reproducible font families, D29 about bounded schematic mark detail, and D31 about the Atmospheric visual treatment across its affected references.
- Approval of static references does not establish installed rendering, interaction, RTL localization, or TalkBack verification.

## Implementation steps

1. Verify the packet directory and manifest digest still match the completed cycle-031 evidence.
2. Match the owner's response to the packet revision/digest and extract an explicit disposition for each of D28, D29, and D31. If any item is ambiguous or absent, record it as pending and do not advance the gate.
3. Write the decision record with the exact response basis and verification. Update only decision/status summaries and the roadmap gate that the response actually permits.
4. Run workflow and contract checks, inspect the diff, retain evidence, and close with accurate installed-app/accessibility limitations and the next action.

## Acceptance criteria

- The decision record names the exact packet revision and full manifest digest and records owner/date plus a distinct D28/D29/D31 outcome.
- The digest is recomputed against the cycle-031 frozen packet before recording approval.
- TP.1D/TP.1 and TP.2 statuses match the response exactly; pending or non-approval never appears as completion.
- History records the disposition, verification, changed documents, and remaining unverified boundaries.

## Verification and evidence

- Run `python scripts/dev.py workflow` and `python scripts/dev.py contract` after document changes.
- Recompute cycle-031 manifest SHA-256 values and compare the aggregate revision digest before recording the response.
- Run `git diff --check` and inspect the final diff. No Android build, UI test, install, or screenshot is applicable because this slice changes no runtime files; installed acceptance remains unverified.
- Save the decision and exact command results under `.codex/test-artifacts/032-tp-1d-owner-disposition/`.

## Risks and assumptions

- An ambiguous or partial response leaves the design gate open. Ask only for the missing decision in the conversation and preserve the pending record.
- Any requested reference revision invalidates this plan's immutable-input assumption and requires a new scoped upstream plan and packet revision.

## Out of scope

- Re-rendering or revising any reference, asset, checklist, source art, or packet.
- TP.2/TP.3 implementation or installed-app acceptance.
- Weather, provider, settings, navigation, alert, Android, or Compose changes.
