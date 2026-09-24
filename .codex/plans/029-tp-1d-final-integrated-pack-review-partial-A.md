# Superseded initial plan — TP.1D-partial-A-partial-B-partial-A approval packet and decision

Status: Superseded by active cycle 030 checklist plan and its dependent packet/decision initial plan
Roadmap item: TP.1D-partial-A-partial-B-partial-A
Parent item: TP.1D-partial-A-partial-B
Created: 2026-09-23
Context budget: Aim for 30–35% of a fresh context window; stop before 45%. Review and activate in a new `.codex` cycle after the upstream history exists; assign its own cycle ID and evidence path then.
Planned implementation difficulty: 6/10

## Objective

Turn cycle 029's verified 20-cell design revision into an executable TP.3 installed-comparison checklist and an immutable, reproducible owner-review packet. Obtain an explicit design-owner disposition for that exact packet. Close TP.1D and the TP.1 umbrella and release the TP.2 dependency only if approval and verification are complete.

## Production boundary

- Consume cycle 029's history, 20-cell matrix, change ledger, proposed design files and outstanding D28/D29/D31 decisions. Do not redo its cross-pack review unless the handoff exposes a concrete defect or the reviewed files change.
- Complete a TP.3 installed-comparison checklist in `docs/theme-system/design-pack/INTEGRATED_PACK.md` or a directly linked, versioned design-pack document. Include 20 primary theme/page rows and focused condition rows covering compact 360×640, font 1.3 and a documented large-font stress case, RTL, wide, Effects Off, High contrast, partial/missing data and source/status states. Each row names exact reference revision, theme/page/state, device viewport and density or dp dimensions, font scale, effects/contrast/locale, fixture/load-state setup, action or visual target with measurable tolerance where meaningful, screenshot and semantics/hierarchy evidence slots, and pass/deviation/blocker fields. Do not imply all states have static renders: mark contract-only targets and installed checks distinctly.
- Finish design-pack README, render index/README, integrated pack, source/asset map and decision ledger for the proposed revision. Assemble a read-only snapshot in the new cycle evidence path containing the 20 primary SVGs, twelve condition SVGs, fixture/index/generator, applicable design contracts, source/asset references with hashes, upstream measures and contrast evidence, cross-pack matrix, open decisions, checklist, and concise change log. Record a revision label and SHA-256 manifest of snapshot files; exclude the mutable manifest and later owner-decision record from self-hashing. Preserve the exact packet sent to the owner. A changed reviewed file requires a new snapshot/label and re-review.
- Request and record the design owner's explicit approve/revise/reject decision, date, exact revision/hash-manifest identifier, and disposition of every open conflict. Store the decision outside the hashed snapshot and link it from the pack/roadmap/history. Silence is pending. If revision is requested, make only bounded corrections, regenerate/recheck affected references, freeze a new packet, and request a decision again; split further before the context cap if necessary. Mark TP.1D/TP.1 complete in `docs/theme-pack-roadmap.md` only after approval of the exact packet. Keep TP.2 gated otherwise.

No Android/Kotlin/Compose/resource changes, weather semantics, provider work, TP.2 renderer implementation or TP.3 installed acceptance.

## Functional invariants

Keep the four named pages, outer-pager swipe ownership, visible Hourly/Daily window controls, exact supplied facts/chronology/units, source/update/load-state meaning, missing and partial behavior, current versus derived/historical provenance, and meaningful visible/accessibility text. No art-derived forecast facts or official-alert language. Effects Off stays opaque, static and complete; controls retain 48 dp guidance and non-color cues. Approval of a design packet is not installed-app acceptance.

## Implementation steps

1. Validate cycle 029 closure, its audited proposed revision, exact 20+12 reference inventory, D28/D29/D31 dispositions or options, and source/asset hashes. Record any changed file since handoff and re-audit only its affected cells before using it in a packet.
2. Write the TP.3 checklist against the approved product/UI contract and proposed reference set. Use concrete device/build/fixture instructions and evidence locations; distinguish static visual targets from behavior, semantics, and runtime state checks. Check rows for all 20 primary cells and representative environment/state conditions without inventing a static render where none exists.
3. Resolve documentation links/status, source/asset map and decision ledger, then freeze a versioned snapshot and hash manifest. Independently verify every digest and all 32 indexed references in the snapshot. Produce an owner-facing index naming D28/D29/D31 and any new open decisions, options, affected cells, and the exact response needed.
4. Present the completed packet for a named owner disposition. Record the actual decision. For approval, verify that the packet still matches its manifest, update the roadmap gate and design-pack status without changing the approved snapshot, then close the cycle into history. For pending/revise/reject, retain the gate and next action explicitly; do not mark TP.1D/TP.1 complete.

## Verification and evidence

- Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`; audit local links/anchors and all checklist target/reference paths. Check 20 unique primary cells, twelve indexed examples, exact fixture/typed-map and control semantics, state coverage labels, source/asset SHA-256 values, and all frozen-packet digests. Recheck any reference changed after cycle 029 with viewport/full/end raster review and applicable opaque contrast measurements.
- Inspect checklist instructions for reproducible viewport/font/effects/contrast/RTL/build/state setup and explicit pass/deviation/blocker fields. Use the current static references as targets only. Do not claim installed screenshot, touch, Android font metrics, or TalkBack evidence that was not collected.
- Run `python scripts/dev.py check` when Android tooling is available as a regression gate. Run `git diff --check` and review the final tracked/new-file diff, snapshot inventory, owner decision, roadmap status and unchanged approval manifest. Retain exact commands/results, checklist audit, packet, digest verification, owner disposition and limitations in the new cycle evidence path; record unavailable checks and why.

## Acceptance criteria

- TP.3 has an unambiguous installed-comparison instrument for all 20 theme/page cells plus defined environment and state checks, with reference revision and evidence slots. Contract-only targets are labeled honestly.
- The frozen packet reproduces its 32 references and governing design files; SHA-256 verification passes and the decision points to that exact immutable revision.
- Every open conflict has an explicit owner disposition. Only an approval of the exact verified revision permits TP.1D/TP.1 completion and TP.2 eligibility. Pending, revise or reject keeps the gate open and is recorded without a false completion claim.
- History states exact verification, unverified installed boundaries, and the packet/decision locations.

## Risks and assumptions

- Design-owner response is an external dependency. Prepare the entire reviewable packet before requesting it; a missing response is not consent or a completed gate.
- Font or mark revisions can invalidate many references. Re-scope before 45% context rather than accepting stale hashes or partial visual review.
- Checklist rows prescribe later TP.3 installed work; this documentation cycle does not establish Android runtime appearance.

## Out of scope

- Implementing TP.2/TP.3, installed visual acceptance, or changing application data and navigation contracts.
