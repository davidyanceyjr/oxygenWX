# Plan 033B — TP.1D revised packet integration and independent audit

Status: Planned
Cycle ID: 033-tp-1d-atmospheric-variants-symbol-map-partial-B
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A-partial-B
Parent item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A
Created: 2026-09-24
Dependency: cycle 033 symbol mapping and cycle 034 Atmospheric palette proposal close with evidence; owner disposition remains separate and later.
Evidence: .codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/
Context budget: Target 30–40% of a fresh context; stop before 45%. Packet assembly/audit only; no design changes.

## Objective

Assemble a new immutable proposed TP.1D packet revision that integrates the completed theme-specific symbol map and the clearly labeled Atmospheric light-palette proposal alongside the retained dark candidate, then independently verify its source inventory, render coverage, internal links, and SHA-256 manifest. Preserve the cycle-031 packet unchanged and leave owner decisions pending, including both D31 scene options.

## Production boundary

Create a versioned packet beneath `.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/packet/`; update only packet-specific copied docs, source inventory, manifest, and audit/owner guide. Inputs are the reviewed design pack plus completed cycle 033 symbol-map and cycle 034 palette-proposal outputs. Do not modify design source files or seek owner disposition.

## Functional invariants

- Preserve all five themes, 20 primary cells, current condition examples, mapped source distinction, and proposed Atmospheric mode behavior exactly as upstream reviewed. Make clear that system mode selects colors only and the light palette does not resolve D31.
- Cycle-031 packet remains byte-for-byte unchanged; new packet has a unique revision identifier and its own full digest inventory.
- D28/D29/D31 remain explicit pending owner decisions unless directly resolved by the accepted upstream evidence; no decision is inferred from silence.
- TP.1D/TP.1 remain open and TP.2 gated until explicit owner disposition of this exact new revision.

## Implementation steps

1. Record immutable digests for cycle-031 packet and all accepted upstream inputs; choose a unique proposed revision label.
2. Assemble the complete documentation/reference packet with revision change log and an owner guide that names still-open decisions, explicitly presents both D31 options without preference, and records the exact packet digest.
3. Generate a full source inventory and SHA-256 manifest covering every packet file except the manifest itself; include source locators/digests for approved boards/crops.
4. Independently audit 20 primary cells, all indexed examples including both Atmospheric palette modes, symbol-map coverage, relative links/anchors, source inventory completeness, source hashes, manifest coverage, and aggregate digest.
5. Store the independent audit and exact verification boundary under this cycle evidence path. Update the roadmap only to identify the subsequent owner-disposition plan against this immutable revision.

## Acceptance criteria

- New packet is uniquely versioned, complete, self-contained for review, and records the digest in its owner guide and audit.
- All packet files and referenced sources are accounted for; every relative link resolves; every expected primary cell/example is covered; independent manifest verification passes.
- Prior cycle-031 packet digests match their recorded values and are untouched.
- Owner decisions remain pending; no approval, installed-app result, TP.1D/TP.1 closure, or TP.2 eligibility is claimed.

## Verification and evidence

Run workflow and contract checks, packet-local link/anchor audit, independent inventory/manifest recomputation, full cell/example coverage audit, source-digest audit, and `git diff --check`. Preserve commands, outputs, revision identifier, aggregate digest, and limitations under this cycle evidence path. No Android installation is required or claimed.

## Risks and assumptions

- Packet size or link structure may make a one-pass audit difficult; use deterministic scripts and review independently rather than relying on a hand-checked manifest.
- Upstream changes after cycle 034 invalidate this packet candidate and require refreshed digests/review before assembly.
- This plan is documentation-only and cannot convert proposed references into owner-approved design authority.

## Out of scope

- Revisions to approved/current design source files, new visual design decisions, artwork creation, owner disposition/approval, Android runtime or installed comparison, TP.2/TP.3 implementation, or release claims.
