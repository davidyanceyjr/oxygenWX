# Plan 034 — TP.1D Atmospheric light palette proposal

Status: Completed
Cycle ID: 034-tp-1d-atmospheric-light-palette-proposal
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A-partial-A
Parent item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A
Created: 2026-09-24
Dependency: cycle 033 closes with its theme-specific symbol map and evidence.
Evidence: .codex/test-artifacts/034-tp-1d-atmospheric-light-palette-proposal/
Context budget: Target 30–40% of a fresh context; stop before 45%. Palette contract, focused deterministic checks, and only necessary Atmospheric reference updates.
Difficulty: 5/10 — bounded design-token and static-render work with contrast calculations and deterministic contract checks; no runtime integration.

## Objective

Develop a clearly labeled proposed light palette for the existing Atmospheric theme, alongside the retained dark palette, and document how a future appearance resolver would choose the color variant from system light/dark mode. This is a palette proposal only. Keep D31 open: do not select between its consistent dark-teal option A and shared blue/scenic option B, and do not use this proposal to imply that either option has owner approval.

## Production boundary

Documentation, proposed design-token data, focused deterministic verification, and only the Atmospheric static-reference examples needed to review palette behavior. Approved token catalogs remain unchanged unless a separately labeled proposal file is required. No runtime resolver, Android setting, or packet assembly. Do not modify the frozen cycle-031 packet. Evidence belongs under `.codex/test-artifacts/034-tp-1d-atmospheric-light-palette-proposal/`.

## Functional invariants

- System appearance mode selects palette colors only; it does not change theme identity, layout, typography, weather facts, chronology, provenance, navigation, persistence, or refresh behavior.
- Preserve the current Atmospheric dark palette and its existing proposal status; never silently replace it.
- The light palette is a derived proposal, not a transcription of source pixels. Keep it distinct from both D31 scene options and explain its intended review use.
- Reuse the current semantic color-role vocabulary. Do not add a new theme, user preference, weather meaning, or unsupported role.
- Contrast, accessibility, layout, and effects contracts remain in force for both variants. Effects Off remains opaque, static, and complete.
- D28, D29, D31, owner approval, TP.1D/TP.1 closure, and TP.2 eligibility remain pending.

## Implementation steps

1. Inventory the existing Atmospheric token values, approved source boards/crops, D31 source samples, and the symbol-map/render changes from cycle 033. Record paths and SHA-256 digests for inputs used. Treat missing light-specific source evidence as a constraint, not a reason to label derived colors as sampled.
2. Define a small proposed variant contract that maps system light/dark appearance to the existing Atmospheric semantic roles. Retain the existing dark values unchanged. Derive only the light values needed for the same roles and identify the derivation rule and any values adjusted for legibility.
3. Add a focused deterministic checker for: complete and valid role sets in both candidates; valid mode-to-variant mapping; no mutation of the current dark catalog; documented source/proposal status; contrast of primary/supporting text and status/action labels on their intended opaque surfaces; and unchanged fixture weather facts, symbol identity, and geometry inputs. Use WCAG relative luminance and the project’s stated thresholds; report exact ratios and role/surface pairs. Do not claim runtime accessibility verification.
4. Update the narrow design-pack contract and source-decision record to describe the proposal, mode mapping, derivation limits, contrast results, and the explicit boundary that D31 remains unresolved. Do not rewrite D31’s option A/B description or claim this palette chooses either scene.
5. Update only the Atmospheric static render example(s) required to compare the light proposal with the retained dark candidate. Preserve fixture text/data, symbol source mapping, component geometry, dimensions, and all non-Atmospheric renders. Regenerate deterministically and check the output diff; avoid duplicating every page if one representative surface plus the complete role/contrast contract is sufficient to review the palette.
6. Review the proposal for compact and large-font references, RTL mirroring, High contrast, and Effects Off behavior. Record any static-reference limitations. If the light palette cannot meet the stated contrast checks without changing role semantics or inventing a scene direction, record the gap and keep D31 open rather than forcing a pass.
7. Update the roadmap execution head to hand off to the dependent packet-integration/audit plan only after this slice is closed with its evidence. The downstream packet must include the proposal as proposed material and keep D31 pending.

## Acceptance criteria

- A reader can distinguish the unchanged dark candidate from the derived light proposal, reproduce the source inventory and derivation, and understand that system-mode selection is a proposed color-only rule.
- D31’s two alternatives remain accurately documented and unresolved. No scene, palette family, or owner disposition is selected by implication.
- Deterministic checks cover role completeness, mode mapping, dark-value preservation, contrast, and invariance of fixture facts, symbol map, and geometry. Checks fail clearly when a role is missing, a ratio is below threshold, or an invariant changes.
- Atmospheric comparison references are reproducible and limited to the minimum useful example set; unrelated files and the cycle-031 packet remain unchanged.
- Workflow, project contract checks, focused checker, render-generation reproducibility, and `git diff --check` pass. Evidence records commands, environment/font inventory, input and output digests, exact contrast values, review notes, and limitations.
- No runtime, installed-app, TalkBack, owner-approval, TP.1D/TP.1 completion, or TP.2 eligibility claim is made.

## Verification and evidence

Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`, the focused palette checker, and the deterministic render generator twice to establish stable outputs. Compare generated Atmospheric outputs against the pre-change state and confirm non-Atmospheric indexed references and cycle-031 packet digests are unchanged. Run `git diff --check` and inspect the complete diff.

Store commands and outputs, source inventory/digests, font/tool versions, role/surface contrast table with calculation assumptions, before/after render hashes, reviewed render captures, static compact/large-font/RTL/High contrast/Effects Off notes, and any limitations beneath `.codex/test-artifacts/034-tp-1d-atmospheric-light-palette-proposal/`. Android/install/TalkBack and actual system-mode runtime behavior remain unverified and outside this slice.

## Risks and assumptions

- Approved artwork does not establish a complete separate light palette. The candidate is therefore a visibly marked proposal derived from existing semantic roles, not a source transcription or authority change.
- D31 concerns the overall Atmospheric palette/scene direction. This slice can evaluate contrast and reviewability of a light variant without closing that broader owner choice; the later packet must present the relationship plainly.
- Contrast adjustments may depart from individual sampled pixels. Record those adjustments and do not claim pixel matching.
- A focused checker should use the repository’s existing Python-only verification approach and standard library where practical; avoid adding a dependency solely for this static contract.

## Out of scope

- Resolving D31 or choosing scene option A/B; owner disposition or approval.
- Theme-specific symbol mapping (cycle 033), full packet inventory/manifest reconstruction, revising/freezing the cycle-031 packet, TP.2/TP.3, Android source/runtime, persisted appearance settings, weather/data semantics, other themes, broad page redesign, or changes to primary page geometry.
