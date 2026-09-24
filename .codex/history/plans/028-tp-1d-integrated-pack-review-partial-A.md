# Plan 028 — TP.1D-partial-A Daily and Details integration

Status: Completed
Cycle ID: 028-tp-1d-integrated-pack-review-partial-A
Evidence: .codex/test-artifacts/028-tp-1d-integrated-pack-review-partial-A/
Roadmap item: TP.1D-partial-A
Parent item: TP.1D
Created: 2026-09-23
Revised: 2026-09-23
Context budget: target at most 35% of a fresh context window; stop before 45%. Dependent review: TP.1D-partial-A-partial-B.

## Objective

Complete and individually review the five Daily and five Details design cells,
ten primary renders, and six environment examples using the established
integrated-pack schema. The dependent initial plan
`.codex/plans/028-tp-1d-integrated-pack-review-partial-A-partial-B.md`
owns final 20-cell review, owner decision, and TP.1 closure. TP.2 stays gated.

## Production boundary

- Populate only the ten pending Daily/Details rows in
  `docs/theme-system/design-pack/INTEGRATED_PACK.md`. Each row records the page
  contract, shared-shell and component measures or formulas, theme treatment,
  typed fact/action map and delta, state rules, exact source crop locator,
  render locator, and bounded open decision. Preserve reviewed Now/Hourly rows
  unless a documented shared-shell correction is necessary; recheck any
  changed upstream cell and render.
- Extend the asset-use map only for sources used by Daily/Details: manifest
  path/SHA-256, dimensions and crop, theme/page/role, and review-only or future
  runtime-candidate disposition with licensing and semantic conditions.
- Extend `docs/theme-system/design-pack/renders/generate.py`, `fixture.json`,
  `index.json`, and `renders/README.md` with five Daily and five Details primary
  SVGs at 393 × 852 dp and six labeled examples across both pages: compact
  360 × 640 dp, font scale 1.3, RTL, wide 840 × 900 dp, Effects Off, and High
  contrast. Other examples use 393 × 852 dp. Capture full logical body and
  end-of-scroll evidence where needed. Retain the established 24/0/24/0 dp
  reference insets; runtime insets remain dynamic. The generator must remain
  reproducible and preserve Now/Hourly output absent an evidenced correction.
- Update `DAILY.md`, `DETAILS.md`, `FOUNDATION.md`,
  `CONTENT_AND_STATE_RULES.md`, `SOURCE_DECISIONS.md`, and the design-pack
  `README.md` only for measured discrepancies, page-specific fit, source
  traceability or status clarification. Record old/new values and affected
  cells. Correct any remaining TP.1D installed-acceptance claim; TP.3 owns it.
- At closure, record exact outcome and limits in `.codex/history/` and advance
  the `docs/theme-pack-roadmap.md` execution head to the dependent partial.
  Leave TP.1D/TP.1 open. No Android production or product-semantic changes.

## Functional invariants

Keep `Now -> Hourly -> Daily -> Details`, one outer horizontal-swipe owner,
named page selection, and Daily's visible bounded Earlier/Later five-entry
windows. Preserve supplied Daily strings and chronology; never pad a short
window. Details renders supplied groups/metrics in order and keeps current
source/update/status separate from derived and historical context. No
art-derived value, advice, chart, gauge, AQI/UV fact, or official alert enters
either page. Missing content stays omitted or uses typed unavailable wording.
Important facts remain visible text; marks are nonessential. Controls follow
48 dp target guidance. Effects Off is opaque, static and complete. Theme,
contrast and viewport cannot change weather meaning, navigation, provenance
or accessibility meaning.

## Implementation steps

1. Capture initial git status and Plan 028 handoff. Read the adopted UI
   contract, measurement method, Daily/Details contracts, decision ledger,
   asset manifest, tokens and presentation types. Reuse the mapper-derived
   illustrative fixture and exact source/update/status. Export missing Daily
   and Details strings through the same deterministic mapper harness; label
   them as design data. Never transcribe mockup weather values.
2. Measure relevant source crops at original pixel size using the documented
   dp/ratio method. Fill ten rows and asset entries. Carry shared G/S/W,
   header/selector minima and D27 content-driven fit. Derive Daily row/window
   and Details group/provenance layouts from their typed content; do not reuse
   Hourly card geometry or a fixed source-block height. Mark inference.
3. Generate ten primary SVGs and six examples. Check five Daily cells show
   the same selected supplied window, only its actual entries, bounded
   controls, source/update and status. Check five Details cells show the same
   group/metric order, optional support and separate provenance/status.
   Inspect viewport, full-body and end-of-scroll captures; record source
   measure, mismatch, correction and remaining uncertainty per cell.
4. Resolve page-specific discrepancies within authority and update contracts
   with evidence. Carry D28 font and D29 mark decisions to final owner review
   unless supported evidence resolves them. Produce a ten-cell handoff and
   bounded cross-page issue list, without whole-pack acceptance claims.

## Verification and evidence

- Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`.
  Check populated local links/anchors and render paths, ten new unique
  theme/page rows and primary SVGs, dimensions/metadata/visible text, and used
  source hashes against `ASSET_MANIFEST.json`. Compare regenerated existing
  Now/Hourly references to detect unintended changes.
- Audit exact Daily/Details fixture strings and actions against
  `HomePresentation.kt`, `HomeLoadState.kt`, and the mapper export. Check
  same-page cross-theme fact/action parity, actual Daily window size/order,
  Details group/support order, missing-value rules, absent unsupported facts,
  and six environment examples. Review documented loading, live,
  partial/sparse, cached/stale, retained-refresh-failure,
  failed-without-data, nested unavailable and missing-field rules. Render an
  extra state only to settle a concrete fit or meaning risk.
- Rasterize and visually inspect all new SVGs against source crops. Review
  compact and font 1.3 end-of-scroll, RTL ordering, wide 480 dp cap, opaque
  static Effects Off and High contrast non-color cues. Calculate contrast
  from actual opaque rendered text/surface pairs and retain colors/ratios.
  Static references do not verify Android metrics, interaction, TalkBack,
  translated RTL text or installed matching.
- Run `python scripts/dev.py check` when Android SDK/dependencies are available
  as a regression gate. Run `git diff --check` and inspect the full tracked
  diff plus new outputs against initial status. Retain command results,
  asset/hash and link audits, fixture export, cell/example checklist,
  captures, contrast, decision delta and limitations in cycle evidence.
  Record any check that could not run and why.

## Acceptance criteria

Ten Daily/Details rows have reproducible measures, typed maps, source locators,
asset disposition and individually reviewed primary renders. Six examples
demonstrate the named conditions without dropping facts or controls. All
supported strings and order match the mapper export; discrepancies are
corrected with evidence or listed as bounded decisions. This slice may close
with these checks and exact limits, leaving TP.1D/TP.1 open, TP.2 gated and
the dependent partial at the roadmap head.

## Risks and assumptions

Some references omit Daily or Details, so style mappings are measured
proposals, not exact source compositions. D28 font substitution and D29 mark
detail remain review decisions. Shared-shell corrections can invalidate
upstream renders; recheck affected cells or record a blocker. If work nears
45% of a fresh context window, stop at a reviewed handoff and split remaining
work again before continuing.

## Out of scope

No Kotlin/Compose/resources/provider/model edits, refetch, installed-app
acceptance, final 20-cell approval packet or owner decision, TP.2 work, or
fabricated forecast/alert content. Preserve existing worktree changes and
the untracked intake archive.
