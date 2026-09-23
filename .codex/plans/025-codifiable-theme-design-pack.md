# Plan 025 — TP.1A codifiable theme design foundation

Status: Completed
Cycle ID: 025-codifiable-theme-design-pack
Roadmap item: TP.1A
Created: 2026-09-23
Revised: 2026-09-23

## Objective

Create a reviewable, source-traceable foundation for the five-theme design
pack. Resolve shared Home structure, theme token vocabulary, content/state
rules, reference conflicts, and responsive constraints that must be settled
before page designs can be completed. This is a documentation and evidence
slice; it does not approve the complete design pack or start TP.2.

This slice is deliberately limited to shared design decisions. The ten
theme/page designs for Now and Hourly, the ten designs for Daily and Details,
integrated reference renders, and final design-owner approval are dependent
slices in `docs/theme-pack-roadmap.md`.

## Production boundary

- Design authority created by this cycle: `docs/theme-system/design-pack/`.
- Existing authority updated by this cycle: `docs/theme-system/README.md` and
  `docs/theme-pack-roadmap.md` only, except where a source conflict requires a
  narrowly scoped correction approved by the design owner.
- Evidence: `.codex/test-artifacts/025-codifiable-theme-design-pack/`.
- Review approved production reference boards, extracted assets, candidate
  token catalog, intake archive, current presentation models/fixture, and
  applicable product/UI specifications.
- Do not modify Android source, runtime resources, existing cycle-024 files,
  cycle-024 evidence, or the untracked intake archive. Treat staged-production
  code as non-authoritative reference material only.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`; the outer Home pager remains
  the only global horizontal-swipe owner.
- Hourly shows up to six actual chronological entries per window and visible
  date jumps; Daily shows up to five actual chronological days per window.
- Preserve existing presentation facts, units, provenance, valid/update time,
  chronology, official-alert meaning, missing-data behavior, page identity,
  navigation, and accessibility meaning.
- Do not add facts, controls, series, alerts, or states that current product
  contracts and presentation models do not support.
- Important facts remain visible text. Weather marks and backgrounds are
  decorative; Effects Off remains opaque, static, and complete.
- Candidate token JSON and reference boards are inputs, not approved values
  when they conflict with higher-authority product or accessibility contracts.
- Keep all existing user changes intact. In particular, do not stage, revert,
  reformat, or claim cycle-024 production candidates as accepted output.

## Implementation steps

1. Capture the initial working-tree status in the evidence manifest. Inventory
   the five-theme reference boards/crops, `ASSET_MANIFEST.json`, all five
   candidate theme token files and theme manifest, the intake archive's
   `MANIFEST.sha256` and governance proposals, and relevant current
   presentation models/fixture. Verify listed hashes without extracting over
   or modifying project files. Record disagreements and unavailable sources;
   do not copy staged code into production.
2. Create a source and decision ledger. For every adopted foundation decision,
   record the source path and specific board/crop/token key, the selected
   value/rule, its authority level, and any rejected conflicting treatment.
   Mark each item `proposed`, `accepted by authority`, or `open`; do not imply
   owner approval that has not been given.
3. Specify the shared canvas and Home shell: 393 × 852 dp reference viewport,
   font scale 1.0, LTR, edge-to-edge/system-inset treatment, safe content
   bounds, page identity and selector, shared header/content hierarchy, and
   how scrolling interacts with the outer pager. State what changes at 360 ×
   640 dp, font scale 1.3, RTL, and wider windows. Do not prescribe page-specific
   placement beyond the adopted product contract.
4. Reconcile the theme vocabulary and candidate tokens for Atmospheric, Glass,
   Minimal OLED, Instrument, and Terminal. Specify concrete canvas, surface,
   text/data, status/action, typography, spacing, shape, mark, backdrop, and
   effects roles, including units and usage. Separate token values from rules;
   identify unsupported data-visualization treatments (including gauges and
   charts) and any token that remains unresolved. Do not invent numerical
   token values when references do not establish them; list a precise owner
   decision instead.
5. Define the shared content/state grammar that page slices must implement:
   loading, ready, cached/stale, partial horizon, unavailable/missing fields,
   and supported official-alert presentation. Map states only to current
   presentation facts and say when a slot is omitted. Specify visible text,
   semantics, non-color cues, chronology in RTL, touch-target floor, and the
   Effects Off rule. Leave exact page layouts to TP.1B and TP.1C.
6. Produce the foundation documents listed below, update only the two indexes
   named in the production boundary, and assemble the cycle evidence manifest.
   Review every final change against the initial status; preserve unrelated
   modifications and do not report the full pack as approved.

## Required deliverables and document updates

Create under `docs/theme-system/design-pack/`:

- `README.md` — status, authority, file index, and dependency on TP.1B–TP.1D.
- `FOUNDATION.md` — canvas, shared shell, responsive geometry, and exact
  five-theme role/value tables, with source references and unresolved values.
- `CONTENT_AND_STATE_RULES.md` — supported content slots, state treatment,
  semantics, RTL chronology, Effects Off, and explicit non-goals.
- `SOURCE_DECISIONS.md` — reference/token/intake traceability, conflicts,
  asset disposition rules, and open design-owner decisions.

Update:

- `docs/theme-system/README.md` to index the new foundation and label it
  review material, not the complete/approved design pack.
- `docs/theme-pack-roadmap.md` to record TP.1A–TP.1D sequencing and status;
  TP.1 remains the umbrella acceptance gate, with TP.1A active and later
  slices planned.

Do not update `docs/SPECIFICATION.md` or
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` for visual decisions alone. If review
finds a genuine product-contract conflict, record it as an open decision and
identify the authority document that needs owner-directed resolution; do not
silently change product semantics in this cycle.

## Verification and evidence

This cycle changes design documentation only, so it adds no Kotlin, Compose,
runtime asset, or application test. Verification is limited to checks that
prove this bounded design deliverable is complete and internally consistent:

- `python scripts/dev.py workflow` — current plan and workflow structure.
- `python scripts/dev.py contract` — existing source/product contracts remain
  valid; record any pre-existing failure without changing unrelated source.
- Validate all asset-manifest and intake-manifest entries against their
  recorded files/hashes; report missing or mismatched entries without
  rewriting the source manifests.
- Manually audit the five-theme role matrix and shared-state matrix for empty
  required cells, units, traceable sources, authority labels, unsupported
  facts, and unresolved choices. Record the checklist and result.
- Confirm referenced paths exist and that no staged-production item is cited
  as production authority.
- `git diff --check`; inspect the final diff file-by-file and compare it with
  the captured initial working-tree status.

Preserve exact command outputs, source/hash audit, completed matrix checklist,
open-decision list, and a concise manifest under the cycle evidence directory.
No generated screen render, installed-app claim, or application test result is
required for TP.1A. Those belong to the page-design/finalization slices and
must use approved designs and supported presentation facts.

## Acceptance criteria

- All required foundation documents exist and are indexed; each adopted
  decision has a traceable source and authority status.
- Five theme roles are explicitly defined or marked open; unresolved values
  are not guessed or hidden behind adjectives.
- Shared shell, responsive constraints, content/state rules, accessibility,
  and Effects Off behavior are specific enough that TP.1B/TP.1C can design
  pages without reopening their shared assumptions.
- Product/UI invariants above are preserved; no unsupported fact or visual
  treatment requires fabricated data.
- Reference and intake integrity checks, matrix review, workflow, contract,
  and diff checks are recorded with exact outcomes.
- `docs/theme-pack-roadmap.md` shows TP.1A active and TP.1B–TP.1D planned;
  `.codex/current.md` points to this plan as the active TP.1A cycle.
- TP.1 is not described as complete or approved. The full pack remains gated
  on page-specific designs, responsive/state examples, final reference
  renders, design-owner approval, and TP.1D closure.

## Context budget

Target no more than roughly one third of an agent context window; the hard
repository limit is approximately 45%. This slice resolves shared rules and
source conflicts only. It does not author 20 page/theme cells. If the source
audit reveals work likely to exceed the limit, record the issue and split the
remaining audit into a dependent plan before broadening this production
boundary.

## Risks and assumptions

- Reference boards are incomplete and can conflict with product meaning;
  source traceability and explicit open decisions are required.
- Candidate JSON token catalogs are not yet approved design authority.
- The current worktree contains substantial unrelated cycle-024 changes;
  this plan must not absorb or clean them up.
- Final pack approval requires an explicit design-owner record in TP.1D;
  elapsed time, implementation readiness, or agent interpretation is not
  approval.

## Out of scope

- The Now/Hourly and Daily/Details page matrices; these are TP.1B and TP.1C.
- Final 20-cell integrated renders, visual acceptance checklist, and pack
  approval; these are TP.1D.
- Appearance resolver, Compose components, runtime assets, page migration,
  settings persistence, or app behavior.
- Weather models, presentation models, provider behavior, alerts, location,
  units, navigation, or accessibility semantics changes.
- Accepting cycle-024 implementation or its screenshots as proof of design
  correctness.
