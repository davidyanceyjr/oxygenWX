# Oxygen Weather Theme Pack Roadmap

## Execution head — TP.1D-partial-A complete; dependent review next

**Completed: TP.1D upstream Now/Hourly integration.** Plan:
`.codex/plans/028-tp-1d-integrated-pack-review.md`. Its ten cells, ten primary
static references and six condition examples are reviewed in
`docs/theme-system/design-pack/INTEGRATED_PACK.md`. Exact verification and
limitations: `.codex/history/2026-09-23-028-tp-1d-integrated-pack-review.md`;
evidence: `.codex/test-artifacts/028-tp-1d-integrated-pack-review/`.
D28 font choice and D29 schematic mark detail remain explicit final-review
decisions. This upstream closure does not complete TP.1D or TP.1.

**Completed: TP.1D-partial-A Daily/Details integration.** Plan:
`.codex/plans/028-tp-1d-integrated-pack-review-partial-A.md`. It owns the ten
Daily/Details cells, ten primary references, six condition examples, and
their individual review. Exact evidence and limitations are in
`.codex/history/2026-09-23-028-tp-1d-integrated-pack-review-partial-A.md` and
`.codex/test-artifacts/028-tp-1d-integrated-pack-review-partial-A/`.
This does not complete TP.1D/TP.1 or claim owner approval.

**Next: TP.1D-partial-A-partial-B final review.** Initial
plan: `.codex/plans/028-tp-1d-integrated-pack-review-partial-A-partial-B.md`.
It owns 20-cell cross-pack review, the TP.3 installed-comparison checklist,
the frozen review packet, and explicit design-owner approval. Expand and
activate it in a separate cycle after partial-A's history is recorded. TP.2
remains gated until this dependent review closes with approval. Each part
targets at most 35% of a fresh context window and stops before 45%.

**Status:** adopted; TP.1 is ACTIVE, TP.1A, TP.1B, and TP.1C complete
**Adopted:** 2026-09-23
**Purpose:** complete the codifiable five-theme design pack, implement its appearance
resolution, and verify the resulting screens through the installed application.
**Theme family:** Atmospheric, Glass, Minimal OLED, Instrument, Terminal.

This roadmap is the governing implementation sequence for the five-theme design
pack, resolver, and renderer until TP.3 is complete. It covers the missing exact page
compositions and theme treatments, then the resolver and renderer work needed to
match those approved designs. Each main TP child slice is a separately planned
and closed workflow cycle. TP.1B/C used bounded `partial-*` work packages
inside their parent cycles. TP.1D uses separately activated dependent
`partial-*` cycles so each has its own context budget and review evidence.
A dependent slice may begin only after its upstream cycle closes with evidence.

## Completed execution head — TP.1C

Parent cycle **TP.1C is DONE** under `.codex/history/2026-09-23-027-tp-1c-daily-details-page-designs.md`.
Its documentation-only work packages and combined ten-cell review passed:

1. **TP.1C-partial-A — Daily page design — DONE:** five theme treatments,
   five-day windows, typed row/action map, responsive/state cases, and
   evidence in `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/partial-A/`.
   Contract: `docs/theme-system/design-pack/DAILY.md`.
2. **TP.1C-partial-B — Details page design — DONE:** ordered typed metric
   groups, provenance separation, five theme treatments, and evidence in
   `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/partial-B/`.
   Contract: `docs/theme-system/design-pack/DETAILS.md`.
3. **Combined Daily/Details review — PASS:** ten cells audited for shell,
   model, source, state, contrast, compact/large-font/RTL, and Effects Off
   consistency. Review: `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/combined-review.md`.

Exact workflow, source-contract, link/token/model, contrast, and diff checks
are retained beneath `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/`.
This closes TP.1C only. TP.1 umbrella remains active; TP.1D owns integrated
20-cell renders and explicit design-owner approval; TP.2 remains gated.

## Completed execution head — TP.1B

The parent plan is `.codex/plans/026-tp-1b-now-hourly-page-designs.md`.
Its two bounded work packages were reviewed inside cycle 026. The next
dependent design slice is TP.1C; TP.1D still owns integrated renders and
explicit design-owner approval.

1. **TP.1B-partial-A — Now page design — DONE:** five theme/Now cells, Now
   states, and shared-shell handoff. Initial plan:
   `.codex/plans/026-tp-1b-now-hourly-page-designs-partial-A.md`.
2. **TP.1B-partial-B — Hourly page design — DONE:** five theme/Hourly cells,
   six-entry layout, represented-date and Earlier/Later controls, and combined
   Now/Hourly review. Plan:
   `.codex/plans/026-tp-1b-now-hourly-page-designs-partial-B.md`.
   Evidence: `.codex/test-artifacts/026-tp-1b-now-hourly-page-designs/`.

Each portion targets at most 35% of a fresh context window, below the 45%
limit. A takes shell/Now decisions; B takes Hourly interaction complexity.
If either exceeds the limit, split it again before broadening production scope.

## Shared product and visual invariants

- Standard Home remains `Now -> Hourly -> Daily -> Details`.
- The outer Home pager remains the only global horizontal-swipe owner. Hourly
  and Daily use visible window/date controls, not nested pagers.
- Themes change presentation only. They do not change supplied values,
  chronology, units, provenance, valid/update time, missing-data behavior,
  alert meaning, page identity, navigation, or accessibility meaning.
- Every important fact remains visible text with meaningful semantics.
  Decorative weather marks, illustrations, and backgrounds are supplemental.
- No reference-only fact, forecast entry, chart series, or official alert is
  fabricated to reproduce a reference composition.
- Theme, contrast, layout density, and effects resolve to semantic appearance
  before reusable Compose components render typed presentation models.
- Effects Off is opaque, static, and complete.
- Existing Android source, project cycle state, and user changes remain intact
  while this file is introduced. Production work starts only in an activated,
  bounded cycle for one slice.

## TP.1 — Codifiable theme design pack — ACTIVE (umbrella)

TP.1 is an umbrella acceptance gate, not one implementation cycle. Its work is
split below to keep each design slice within the repository's approximately
45% context-window limit. Close each slice with its own plan, evidence, and
history record before activating its dependent slice. Only TP.1D can close the
umbrella after explicit design-owner approval.

### TP.1A — Shared design foundation and source audit — DONE

Define and source-trace the shared canvas/Home shell, five-theme semantic role
and token vocabulary, shared content/state rules, responsive constraints, and
reference conflicts. Publish the foundation documents under
`docs/theme-system/design-pack/`. This slice does not design individual pages
or approve the complete pack.

Plan: `.codex/plans/025-codifiable-theme-design-pack.md`.
Foundation: `docs/theme-system/design-pack/`. Evidence:
`.codex/test-artifacts/025-codifiable-theme-design-pack/`. TP.1A records
source/hash, matrix, workflow, contract, and diff checks, plus unresolved owner
decisions. This does not approve the complete pack.

### TP.1B — Now and Hourly page designs — DONE

Define the Now and Hourly page compositions for all five themes, including
shared-composition references, source-mapped content slots, required state
examples, and 393 × 852 dp measurements. Apply the TP.1A foundation. Record
pixel ratios, conversion to dp/sp, and treatments for states absent from art
using the [reference measurement method](theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md).
Record conflicts as explicit decisions rather than silently changing the
foundation. Daily and Details are out of scope. The two dependent portions
passed source/model audits. TP.1D remains responsible for integrated design
render comparison and approval; this documentation closure makes no visual
runtime acceptance claim.

Plan: `.codex/plans/026-tp-1b-now-hourly-page-designs.md`. Designs:
`docs/theme-system/design-pack/NOW.md` and `HOURLY.md`.

### TP.1C — Daily and Details page designs — DONE

Define Daily and Details page compositions for all five themes using the
completed TP.1A foundation and TP.1B decisions. Preserve five-day windows,
chronology, provenance grouping, and current presentation-model boundaries.
Do not add unsupported gauges, charts, or data slots. The active parent plan is
`.codex/plans/027-tp-1c-daily-details-page-designs.md`. Both bounded parts and
the combined ten-cell review are complete under parent cycle 027. Proposed
contracts: `docs/theme-system/design-pack/DAILY.md` and `DETAILS.md`; evidence:
`.codex/test-artifacts/027-tp-1c-daily-details-page-designs/`. This documentation
completion does not claim installed visual success or owner approval; TP.1D
remains required before TP.1 umbrella closure.

### TP.1D — Integrated pack, responsive review, and approval — ACTIVE (upstream complete)

Integrate the four page designs into the 20-cell theme/page matrix. Complete
the asset-use map, compact/large-font/RTL/wider-window/Effects Off examples,
reference renders, and the checklist for later TP.3 installed acceptance. Resolve or visibly
retain conflicts after measuring and reviewing the relevant source art. Revise
proposed values when integrated renders show a mismatch. Record explicit
design-owner approval
before closing TP.1; TP.2 remains gated until then.

Completed upstream plan: `.codex/plans/028-tp-1d-integrated-pack-review.md`.
It integrated Now/Hourly and their static reference renders/examples; exact verification is in cycle 028 history. The dependent
`TP.1D-partial-A` has completed Daily/Details ten-cell integration. Its
dependent `TP.1D-partial-A-partial-B` initial plan at the execution head owns
cross-pack review and explicit approval in a later cycle. The completed part and
dependent review each target at most 35% of a fresh context window. Either
upstream cycle may close without claiming TP.1D/TP.1 completion; activation
is not approval.

### TP.1 shared completion criteria

- Every theme/page cell has a measurable composition or an explicit reference
  to a complete shared composition and theme-specific style mapping.
- Every visible fact maps to an existing presentation fact. Missing, partial,
  loading, cached/stale, supported-alert, accessibility, compact, large-font,
  RTL, wider-window, and Effects Off behavior is specified without invented
  data or changes to product/navigation semantics.
- Reference assets and conflicts are traceable. Unsupported reference-only
  features are excluded or identified as decoration without product meaning.
- Final renders, installed comparison checklist, exact verification, and
  design-owner approval are retained in the relevant evidence and history.
- No slice claims TP.1 completion before TP.1D closes with approval.

## TP.2 — Pack-driven appearance resolution and shared rendering — PLANNED

### Dependency

TP.1 is closed and its design pack is approved.

### Outcome

Make the resolved appearance and shared theme components express the approved
pack exactly. Resolve theme tokens and rendering styles through typed semantic
roles, then implement the common surfaces and decorative visual primitives used
by the page compositions.

### Scope

- Reconcile the token catalog and typed theme resolver with the approved values
  for all five themes, including contrast, layout, and effects variants.
- Implement or refine shared page header/identity, current-condition hero,
  metric, hourly, daily, Details/source groups, weather marks, and backdrops to
  match approved measurements and treatments.
- Cover every declared resolver/render-style variant explicitly; components
  must not branch on a raw theme ID to alter content or interaction semantics.
- Keep marks/backgrounds decorative and preserve text equivalents, touch
  targets, foreground contrast, and no-input-capture behavior.
- Add deterministic tests for token resolution, style exhaustiveness, null or
  missing presentation values, and Effects Off output.
- Keep shared components additive until the page-composition slice uses them.

### Out of scope

No weather-model or presentation-data changes, settings persistence, new theme
identity, provider behavior, or page-navigation redesign.

### Acceptance

- Resolver outputs match approved design tokens and rendering styles for all
  five themes and supported appearance variants.
- Shared component APIs consume resolved appearance, typed presentation values,
  and semantic callbacks only.
- Focused tests cover resolver combinations, null/missing behavior, and
  Effects Off's opaque/static/complete policy.
- An installed debug showcase verifies shared component treatments and visible
  text/semantics, but is not treated as page-level visual acceptance.
- Evidence and any verified limitations are recorded in the cycle artifacts
  and workflow history.

## TP.3 — Approved page compositions and installed theme acceptance — PLANNED

### Dependency

TP.2 is closed with its focused tests and installed shared-component evidence.

### Outcome

Apply the approved compositions to Now, Hourly, Daily, and Details in the normal
application path. Select among all five themes in memory for verification, and
accept the installed app against the approved reference renders.

### Scope

- Migrate all four pages to the resolved production component family and
  approved per-theme composition rules.
- Preserve page identity, outer-pager ownership, Back behavior, Hourly six-entry
  windows/date jumps, Daily five-entry windows, source/freshness, and Details'
  separation of normalized, derived, and historical values.
- Keep theme selection presentation-only and avoid a weather refetch when it
  changes. Preference persistence is not included.
- Exercise all five themes across all four pages at 393 × 852 dp and compare
  against the approved reference renders.
- Verify 360 × 640 dp, large text, RTL where ordering/navigation is affected,
  partial/missing data, and Effects Off.
- Correct visual deviations and preserve installed screenshots, hierarchy,
  device/build metadata, focused checks, and broader check results.

### Out of scope

No new weather facts/data contracts, settings persistence, provider work,
location work, alert behavior, or unrelated release features.

### Acceptance

- The normal app path renders all four approved pages in all five themes.
- Installed captures at the primary viewport match the approved compositions;
  remaining deviations are corrected or specifically documented as blockers,
  and no exact-match completion claim is made while a blocker remains.
- Compact, large-font, RTL, partial/missing-data, and Effects Off checks preserve
  facts, chronology, navigation, semantics, and readability.
- Focused tests, workflow checks, contract checks, repository checks when the
  environment supports them, `git diff --check`, and final diff review pass.
- The cycle history records exact verification, evidence locations, and
  unverified boundaries.

## Workflow use

Use one `TP.*` child item per bounded `.codex` cycle. Keep the current cycle
active until completed or explicitly pivoted through the repository workflow.
Record exact evidence and verification in `.codex/history/` before starting
its dependent child. Do not activate TP.2 until TP.1D closes with explicit
design-owner approval.

`docs/SPECIFICATION.md` and the adopted UI specification continue to govern
product semantics and invariants. For theme-pack sequencing and implementation
acceptance, this roadmap supersedes the theme-specific sequence previously
embedded in `docs/ROADMAP.md`. The general roadmap remains authoritative for
work outside the TP.1–TP.3 theme-pack track.
