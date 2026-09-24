# Plan 026 — TP.1B Now and Hourly page designs

Status: Completed
Cycle ID: 026-tp-1b-now-hourly-page-designs
Roadmap item: TP.1B
Created: 2026-09-23
Revised: 2026-09-23

## User-directed measurement clarification — 2026-09-23

Reference art is the input for calculating component dimensions and treatment,
including states the art does not depict. Missing annotations are resolved by
measured proportions, component rules, and render review; they are not automatic
owner-decision blockers. This clarification amends the documentation boundary
below to include `REFERENCE_MEASUREMENT_METHOD.md`, `FOUNDATION.md`,
`THEME_DESIGN_CONTRACT.md`, and the adopted UI specification's visual-acceptance
paragraph. It does not change weather semantics, implementation scope, or the
requirement for TP.1D pack approval.

## Objective

Produce source-traceable, codifiable Now and Hourly designs for Atmospheric,
Glass, Minimal OLED, Instrument, and Terminal. This remains the active parent
plan. Divide its ten theme/page cells into two dependent, roughly equal parts:

1. `TP.1B-partial-A`: Now composition, five theme mappings, and Now states.
   Initial plan: `.codex/plans/026-tp-1b-now-hourly-page-designs-partial-A.md`.
2. `TP.1B-partial-B`: Hourly composition, five theme mappings, window/date
   interactions, and Hourly states. Plan after A is reviewed.

Work on A first. The parent cycle remains active in `.codex/current.md` and
closes only after both parts pass. The partial IDs identify roadmap work
packages, not separate active cycles or completion claims. TP.1C follows TP.1B.

## Production boundary

- Add `docs/theme-system/design-pack/NOW.md` in A and `HOURLY.md` in B.
- Update that directory's `README.md` to index each page and its review status.
- Add `REFERENCE_MEASUREMENT_METHOD.md` and align `FOUNDATION.md` with the
  measurement rule. Update `THEME_DESIGN_CONTRACT.md` and the visual-acceptance
  paragraph of `OXYGEN_UI_SPECIFICATION_ADOPTED.md` to identify the derived
  design pack as the implementation target.
- Append page-specific measured choices and conflicts to `SOURCE_DECISIONS.md`;
  preserve TP.1A entries. Update `docs/theme-pack-roadmap.md` for progress.
- Record each part under `.codex/test-artifacts/026-tp-1b-now-hourly-page-designs/`
  in `partial-A/` and `partial-B/`, then a combined acceptance manifest.
- No Kotlin, Compose, runtime resource, fixture, provider, or cycle-024 edits
  belong to this documentation slice. Preserve the existing worktree.

## Functional invariants and visual objective

- Preserve `Now -> Hourly -> Daily -> Details`, persistent page names, one
  outer horizontal pager, Back behavior, and non-navigating static taps.
- Use only typed `HomeLoadState`, `HomePresentationState`,
  `CurrentPresentation`, `HourlyWindowPresentation`, `HourlyEntryPresentation`,
  and `DateJumpPresentation` facts and actions. Art cannot justify new facts,
  gauges, charts, alerts, or advice.
- Make supplied current temperature and condition primary on Now; keep
  supporting facts and source/update/freshness readable and semantically honest.
- Hourly shows at most six actual chronological entries in a two-column,
  three-row normal-phone composition. Earlier/Later moves one window; a
  represented-date control selects its first window. No padded entries or
  nested pager.
- Preserve loading, live, cached/stale, refresh failure, partial horizon,
  unavailable and missing-field meaning. Important facts remain visible text
  with matching semantics; decoration has no independent weather meaning.
- At 393 × 852 dp, 360 × 640 dp, font scale 1.3, RTL, and Effects Off,
  preserve readable controls and chronology. Controls target at least 48 dp
  where applicable. Effects Off stays opaque, static, and complete.

## Implementation steps

### A — Now, upstream portion

1. Capture initial status. Read TP.1A, adopted UI contract, Now presentation
   fields, and Now reference crops/catalog keys. Record exact source locators,
   conflicts, and authority levels. Staged code and retired Theme B composition
   are not design authority.
2. Apply `REFERENCE_MEASUREMENT_METHOD.md` to the Now references. Author
   `NOW.md` with safe-area-relative 393 × 852 dp layout, ordered slots,
   measured bounds or bounded formulas, typography/spacing roles, scroll
   behavior, and slot-to-typed-field map. Record reference crop dimensions,
   component ratios, conversions, rounded dp/sp choices, and source conflicts.
   Derive a complete proposed value when the art omits a numeric annotation.
3. Give five explicit theme mappings to that composition: surface, text, mark,
   backdrop, and Effects Off treatment by TP.1A token key; document measured
   exceptions and treatment choices with their source and reasoning.
4. Cover normal, compact, large-font, RTL, and supported load/availability
   examples. Record calculated design choices, update the index, and audit five cells.

### B — Hourly, downstream portion

1. Review A's measured shell choices and TP.1A authority. Capture Hourly sources,
   typed windows/entries/date jumps, and B's initial worktree status. Record
   any shared-rule change as a proposed decision with A impact.
2. Author `HOURLY.md` with safe-area-relative bounds, two-column/three-row
   placement and reading order, range/date/window controls, disabled/selected
   states, 48 dp targets, vertical overflow, and slot/callback map. Define
   one-to-five-entry and empty-window behavior without placeholders or dates.
3. Give five theme mappings and the same responsive/state examples as A.
   Mirror RTL alignment without reversing chronology or Earlier/Later meaning.
4. Audit all ten cells for shell consistency, source traceability, model
   coverage, and unsupported reference content. Update the index and roadmap;
   close TP.1B only after both parts pass the combined review.

## Required deliverables and document updates

- `NOW.md` and `HOURLY.md` each specify coordinate origin, safe-area/inset
  assumptions, measured bounds or bounded formulas, content order, type/spacing
  roles, scrolling, and a complete slot-to-model map. Record reproducible
  reference-to-dp/sp calculations. Missing measurements are derived from the
  component scale and verified in renders, not left as owner-decision placeholders.
- Each page has five theme rows with token source keys and explicit exceptions.
  A row may refer to a complete shared composition but cannot say only “use
  theme styling.” All reference paths and locators must resolve.
- Each page covers complete, partial/sparse, loading, cached/stale, failure,
  unavailable, compact, large-font, RTL, and Effects Off states using supported
  facts. Future runtime states are labeled design contracts, not current app
  behavior.
- `README.md` links both pages and their review status.
  `SOURCE_DECISIONS.md` records new page decisions and TP.1A conflicts.
  `docs/theme-pack-roadmap.md` records partial progress and TP.1B closure
  without promoting TP.1 or TP.2.
- Change `docs/SPECIFICATION.md` only if a genuine semantic conflict needs an
  owner decision. The adopted UI specification may receive the bounded
  user-directed visual-acceptance clarification above; do not change its
  functional contract for visual choices.

## Verification and evidence

For each part, run `python scripts/dev.py workflow` and
`python scripts/dev.py contract`; retain exact output. Audit five theme cells,
every slot and callback, source path, state example, responsive rule, and
numeric authority label. Check local Markdown links and ensure staged code is
not cited as authority. Run `git diff --check` and inspect the final diff
against captured initial status, including untracked files. Preserve a
checklist, command outputs, decision delta, and manifest under that part's
artifact directory.

After B, review all ten cells together: common shell, compatible safe-area
geometry, one clear placement per model fact/action, and explicit measured
design choices or specific remaining conflicts. Record the combined matrix and unverified boundary in parent
evidence and history. This documentation slice makes no build, installed
screen, automated Android UI test, or pixel-match claim. TP.1D owns integrated
reference renders and comparison criteria; TP.2/TP.3 own runtime acceptance.

## Acceptance criteria

- Both page files, pack index, decision ledger, and roadmap are coherent and
  source-traceable; each five-theme mapping has complete proposed values and
  treatment rules for owner review.
- Now hierarchy and source/freshness placement are unambiguous. Hourly grid,
  date jump, window controls, disabled state, chronology, and RTL order are
  unambiguous. Neither page needs fabricated data to fit the design.
- State/responsive examples preserve the functional invariants above.
- A, B, and combined evidence state exact checks and limits. TP.1B history
  closes only when both parts pass, without claiming pack approval.

## Context budget

The first draft bundled two layouts, ten cells, controls, and state/responsive
matrices. This likely exceeds 45% during source review and audit. The two
five-cell parts divide output and source review roughly equally: A takes shell
and Now decisions; B takes Hourly interaction complexity. Each targets at most
35% of a fresh context window. Split further before work approaches 45%.

## Risks and assumptions

Reference boards mix pages and show unsupported gauges, charts, UV/AQI, and
advice. Candidate tokens are proposed, not approved. Derive typography, shell
geometry, contrast pairs, and effects from measured references and component
rules; TP.1D validates and approves the proposed values. Escalate only a
specific conflict that remains after measurement and render review.
The split assumes each five-cell audit fits a fresh context window; re-scope
before either reaches 45%. Do not modify existing cycle-024 changes or the
intake archive.

## Out of scope

Daily/Details designs, 20-cell integration, final renders, owner approval,
resolver/Compose changes, settings, forecast/provider/cache/alert behavior,
and navigation redesign.
