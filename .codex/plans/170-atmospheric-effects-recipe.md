# Plan 170 — Audit effects sources and prepare a decision packet

Status: Completed
Cycle ID: 170-atmospheric-effects-recipe
Roadmap item: UI.5 / FX.03
Created: 2026-10-11
Revised: 2026-10-11

## Objective

Deliver one source-backed matrix for Atmospheric, Glass, Terminal, Instrument,
and Minimal OLED, plus a decision packet recommending a bounded first
implementation slice for UI.6A. The observable outcome is a single linked
document that distinguishes adopted authority, source observations, current
runtime behavior, exploratory material, and unsupported assumptions, and makes
any owner decision needed by UI.6A explicit.

This cycle audits evidence and proposes a next boundary. It does not create or
approve five effects recipes and does not activate UI.6A.

## Production boundary

Documentation and cycle evidence only:

- Create one consolidated source/decision document at
  `docs/theme-system/EFFECTS_SOURCE_DECISION_PACKET.md`.
- Link it from `docs/theme-system/README.md`.
- Make a narrowly necessary UI effects-track roadmap correction only if the
  verified outcome or an unresolved owner decision requires it.
- Preserve the source inventory, hashes where available, matrix, decision
  packet, audit notes, and exact verification results in
  `.codex/test-artifacts/170-atmospheric-effects-recipe/`.

Do not edit application source, Android resources, runtime behavior, renderer,
resolver, preferences, or installed app state. Before editing, record the
existing worktree state; preserve unrelated user work, including the already
present untracked `feh_045613_000001_Atmospheric-Weather-Pages.png`.

## Functional invariants

- Effects do not change weather values or meaning, units, provenance, valid or
  update time, forecast membership or chronology, alert meaning, navigation,
  accessibility meaning, or forecast/alert request and cache behavior.
- Effects None remains opaque, static, and complete through the existing
  internal `OFF` identity. No enum rename or preference migration is proposed.
- Preserve the five adopted themes and `Now -> Hourly -> Daily -> Details`.
  Do not revive retired Atmosphere Deck presentation language or composition.
- Keep evidence classes distinct: owner-approved decisions, direct source
  observations, existing implementation behavior, exploratory discussion
  input, and gaps/proposals. An absent reference is not approval.
- D31 approval is cited only for the exact twenty page-atmosphere proposals
  and reviewed revision documented in `D31_OWNER_DECISION.md`; it does not
  approve effects levels, measurements, or installed rendering. The five
  Details proposals remain same-theme derivations with no dedicated Details
  source.
- Cycle 169's Atmospheric and Terminal HTML files are exploratory discussion
  inputs only. Their styling, fixed colors, mock weather, page composition,
  and scripted interactions are not requirements. Exclude Daybreak.

## Implementation steps

1. **Confirm prerequisites and protect the worktree.** Verify `.codex/current.md`
   remains PLANNED for this cycle and the workflow check passes. Confirm UI.3
   and UI.4 completion from
   `.codex/history/2026-10-11-169-effects-track-roadmap-and-docs.md` and the
   UI roadmap. Record the initial `git status --short`; do not discard or
   rewrite pre-existing changes outside this plan.
2. **Inspect the bounded source set.** Review the asset manifest and indexed
   production references; `D31_SOURCE_AUDIT.md`, `D31_PAGE_ATMOSPHERES.md`,
   `D31_INTEGRATED_REVIEW.md`, `D31_OWNER_DECISION.md`, and their exact cited
   panels; theme token catalogs and manifest; `EFFECTS_CONTRACT.md`; current
   effects preference/resolver/renderer ownership and focused tests under
   `app/src`; and cycle 169's preview inventory and unchanged archive. Record
   exact paths/identifiers, hashes where the repository supplies them, and
   what each source can and cannot establish. Do not inventory all 60
   theme/page/effect combinations.
3. **Build one compact five-theme matrix.** For each theme, record source
   identity (including available board/crop/backdrop or HTML preview), source
   class and hash/manifest reference, supported page/effect-level coverage,
   relevant existing runtime treatment and code owner, applicable semantic
   palette-role inputs, and explicit gaps. Mark unsupported page, level,
   measurement, state, or artwork evidence as missing instead of filling it
   with inferred values. Avoid converting token values or current renderer
   behavior into new normative recipes.
4. **Prepare the UI.6A decision packet.** Recommend the smallest source-backed
   Atmospheric Now implementation that can be judged in the installed app.
   Name one non-None static level candidate, the existing None comparison,
   source basis, semantic palette roles, minimum expected production owner
   boundary, functional invariants, focused checks, and installed capture
   conditions. Use 360×640dp at font scales 1.0 and 1.3 for the same real Now
   fixture and record build/device/API identity. Include RTL, High contrast,
   and reduced-motion cases where the proposed treatment affects them. State
   explicitly that installed acceptance requires before/after captures and
   focused interaction/semantics evidence. If the sources and authority leave
   a material choice unresolved, list concrete options and mark the owner's
   choice pending; do not silently choose or infer approval.
5. **Publish and audit.** Link the single document from
   `docs/theme-system/README.md`. Keep the effects roadmap aligned with the
   evidence-gated UI.5 → UI.6A → UI.7 sequence, the conditional UI.7A gate,
   the 13–17 total-cycle target, and the rule that post-UI.7 slots remain
   provisional. Do not restore the superseded FX.04–FX.40 decomposition.
   Save the final source list, hashes where available, matrix, open decisions,
   exact command outputs, scope audit, and limitations under the cycle evidence
   directory.

## Context budget

Target 35–45% of the available execution context for this bounded audit and
packet. Estimate confidence is medium because the indexed source set is large
but the deliverable is one compact matrix and one single-page decision packet.
If execution is forecast above 55%, split only the source inventory/matrix
into an explicitly ordered dependent partial plan before activation; retain
65% as the hard maximum and do not drop any acceptance obligation to fit.

## Acceptance criteria

- `docs/theme-system/EFFECTS_SOURCE_DECISION_PACKET.md` contains one compact
  matrix covering all five adopted themes with source identity, source class,
  page/effect coverage, current renderer ownership/treatment, palette-role
  inputs, and explicit evidence gaps.
- The packet cites UI.3/UI.4 dependencies and the exact relevant D31 approvals
  without extending their scope. The cycle 169 previews are labeled
  exploratory; Daybreak is excluded; Details source derivations are explicit.
- The UI.6A packet proposes one bounded Atmospheric Now static-level slice,
  identifies its None comparison and capture/check conditions, and lists any
  exact owner decision that remains. An unresolved material choice stays
  pending and gates UI.6A.
- The source/decision document is linked from the theme-system README and all
  links introduced by this cycle resolve locally.
- The roadmap retains the 13–17 total-cycle target and leaves post-UI.7 work
  provisional until installed vertical-slice evidence is reviewed.
- No production code/resource, new normative five-theme recipe, guessed
  measurement, unapproved color/art, or claim of installed visual acceptance
  is added. The final scope audit confirms no `app/src` path changed.
- Cycle 170 remains the current UI.5 cycle through verification and is closed
  after its evidence is recorded; UI.6A remains PLANNED and is not activated.

## Verification and evidence

This is a documentation/source-audit cycle. No app installation, screenshot,
Android build, or runtime test is acceptance evidence for UI.5. Record exact
outputs and any unavailable check in
`.codex/test-artifacts/170-atmospheric-effects-recipe/verification.md`, along
with the source inventory and hashes, matrix, open decisions, limitations,
initial/final worktree review, and changed-path boundary check.

Run these checks after editing:

1. `python scripts/verification/d31_source_audit.py` — source inventory and
   manifest hashes remain valid.
2. `python scripts/verification/d31_integrated_review.py` — the D31 panel and
   owner-decision record remains structurally valid.
3. `python scripts/dev.py workflow` — current cycle is ACTIVE during execution;
   close it after verification is recorded. UI.6A remains PLANNED.
4. `python scripts/codex_cycle.py validate` — plan/current record requirements
   pass.
5. `python scripts/dev.py contract` — repository source-contract check passes
   (documentation-only work is not represented as a runtime verification).
6. Manually audit local Markdown paths and anchors introduced or changed in
   the new packet and theme-system README; record the checked link count and
   result.
7. `git diff --check`; inspect the final diff and changed paths; verify no
   `app/src` or Android resource path changed and that existing unrelated
   worktree content is preserved.

No application tests are required for this documentation-only boundary.

## Risks and assumptions

- Source coverage differs by theme and page. A board, crop, backdrop, or token
  catalog does not establish a complete page-specific effects recipe.
- The cycle 169 preview archive is available at
  `.codex/test-artifacts/169-effects-track-roadmap-and-docs/inputs/weather-design-previews.zip`;
  its recorded inventory provides SHA-256 values and limits. Treat the archive
  as immutable discussion input.
- Current tokens and runtime renderer behavior are implementation context,
  not approval for new effects treatments.
- UI.5 can recommend a candidate, but approval for any material visual choice
  not already settled by authority belongs to the owner before UI.6A starts.
- Installed evidence in UI.6A may show the proposed boundary or level is
  unsuitable; UI.7 decides the subsequent scope based on that evidence.

## Out of scope

- Any production source/resource change, effect implementation, theme recipe,
  shared effects model, renderer, resolver, preference behavior, or app install.
- Five standalone recipe documents; a comprehensive 60-cell page/level
  matrix; new artwork; guessed measurements; inferred owner approval; or
  treating exploratory preview styling/content as requirements.
- UI.6A implementation/activation, installed visual acceptance, performance
  profiling, accessibility-service execution, cross-theme integration,
  release work, or changes to weather, navigation, provenance, or
  request/cache behavior.
- Reopening cycle 169 outcomes or changing unrelated pre-existing worktree
  files.
