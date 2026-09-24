# Plan 033 — TP.1D proposed weather-symbol source map

Status: Completed
Cycle ID: 033-tp-1d-atmospheric-variants-symbol-map
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A
Parent item: TP.1D-partial-A-partial-B-partial-A-partial-A
Created: 2026-09-24
Revised: 2026-09-24 (context-budget split; symbol-map contract is the upstream portion)
Evidence: .codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map/
Context budget: Target 30–40% of a fresh context; stop before 45%. This is one source-mapping/documentation-render slice, not the full TP.1D revision.

## Objective

Publish a proposed, source-traceable decorative weather-symbol mapping for the existing typed condition families across all five themes. Use only that theme's approved board/crops. Where those sources do not depict a condition family, specify the already-established schematic fallback or no-mark behavior and record the gap. Keep the cycle-031 packet immutable and leave all design decisions unapproved.

## Production boundary

Documentation/design-pack reference files and condition-example SVGs only. Primary page-cell geometry, palette tokens, Android sources, and the frozen cycle-031 packet are outside this boundary. Update `docs/theme-system/design-pack/SOURCE_DECISIONS.md`, the symbol/render contract in `INTEGRATED_PACK.md` and `renders/README.md`, and only the example SVGs/index entries whose symbol depiction changes. Preserve all 20 primary cells and unrelated indexed examples byte-for-byte. Evidence and any new proposed packet material belong under `.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map/`.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, page identity, outer-pager ownership, Back behavior, forecast chronology/windows, supplied values, missing-data behavior, provenance, and accessibility meaning.
- Marks are decorative and non-speaking. Visible condition text and semantics remain authoritative; artwork must not imply a condition absent from the typed identity.
- Keep all five themes distinct. A theme may use its own approved source treatment or the explicitly documented schematic fallback; never borrow another theme's artwork.
- Missing condition identity continues to produce no mark. Do not add condition families or alter the typed fixture's meaning.
- All changed material remains proposed and unapproved. D29 remains pending owner disposition; TP.1D/TP.1 remain open and TP.2 gated.

## Implementation steps

1. Record hashes and inventory for the cycle-031 packet and current source boards/crops before review. Inspect approved source materials for each theme and the six condition identities currently represented by `renders/index.json`; record exact paths, crop/board locators, and source digests. Do not edit the archived packet.
2. Add a compact mapping table to the design-pack contract: theme × supported condition family, source locator, observed treatment versus schematic fallback, and bounds for mark scale/placement. Mark inference explicitly and choose no mark where a fallback would invent weather meaning.
3. Update only affected condition-example SVGs and their index/readme descriptions. Preserve their dimensions, labels, fixture values, page render geometry, and accessible/text treatment. Do not change the 20 primary page-cell renders in this cycle.
4. Add deterministic checks or a small audit script that validates every represented theme/condition mapping has a source locator or explicit fallback, all paths resolve, the index still covers the existing examples, and no unsupported identity was added. Keep checks offline and deterministic.
5. Review the five-theme × six-condition map against approved sources and product semantics; write evidence including changed-file list, source hashes, check output, visual review notes, unresolved gaps, and the immutable cycle-031 packet hash.
6. Close the cycle only with a handoff to the dependent Atmospheric-palette/packet-integration slice. Do not ask for owner disposition of the old cycle-031 packet as part of this cycle.

## Acceptance criteria

- Each mapped mark identifies an approved source from the same theme and distinguishes direct visual evidence from inference. Unsupported depictions are stated as gaps or use an explicitly bounded schematic fallback.
- All and only affected condition examples change; labels, fixtures, page cells, and weather meaning remain stable.
- Deterministic checks cover mapping completeness, local source resolution, example/index coverage, and preservation of the typed condition set; their output is retained under the cycle evidence directory.
- Cycle-031 packet content and digests are unchanged. The current cycle records its initial and final archived-packet digest.
- No approval, Android runtime behavior, installed visual success, or TP.1D/TP.1 closure is claimed.
- Estimated context use: 30–40% (source audit plus one narrow contract/example update). If source inspection reveals a need to create artwork or redesign a primary page cell, record the blocker and plan a separate bounded slice.

## Verification and evidence

- Before and after: `python scripts/dev.py workflow`; after contract changes: `python scripts/dev.py contract`; also run the new focused deterministic mapping check, then `git diff --check` and inspect the diff.
- Regenerate affected examples only with the existing deterministic renderer where supported; preserve command, Python/font environment, generated outputs, and visual review notes under `.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map/`.
- Audit all five themes × the existing six condition identities, the example index, relative links, and source locators. Verify the cycle-031 packet's recorded digest is unchanged.
- `python scripts/dev.py check` is optional for this documentation/reference slice; if run, record its result. No Android install is required. Installed rendering, TalkBack, system palette behavior, RTL runtime, and platform font fidelity remain unverified and are not acceptance claims here.

## Risks and assumptions

- The source manifest has dedicated sheets for some themes but not Atmospheric; approved Atmospheric boards/crops are eligible sources if the locator and depiction can be verified.
- The existing index represents a bounded condition set, not every weather condition supported by the application.
- Approved art may omit one or more conditions. A documented gap is acceptable; an invented symbol is not.
- Palette variants and complete packet reconstruction are intentionally deferred to the dependent slice; do not expand into either during this cycle.

## Out of scope

- Atmospheric light/dark palette design, contrast calculations, system-mode selection, or any runtime theme integration.
- Editing the cycle-031 packet, freezing/approving a new packet, resolving D28/D31, owner disposition, or opening TP.2.
- Changing the 20 primary page cells, page layouts, semantic fixture values, typed presentation models, Android/Kotlin/Compose code, provider/data behavior, or navigation.
- New/commissioned artwork, imported icon packs, theme-to-theme asset reuse, unsupported condition symbols, broad redesign, installed-app acceptance, and claims of owner approval.
