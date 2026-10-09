# Plan 028 — TP.1D Now and Hourly integrated pack review

Status: Completed
Cycle ID: 028-tp-1d-integrated-pack-review
Roadmap item: TP.1D (upstream half)
Created: 2026-09-23
Revised: 2026-09-23
Context budget: target at most 35% of a fresh context window; stop before 45% and split remaining work again if needed.

## Objective and handoff

Integrate the five Now and five Hourly design cells into one measurable pack,
produce their ten primary reference renders and bounded responsive examples,
and review them against source art and typed presentation facts. This is the
upstream half of TP.1D. The dependent initial plan is
`.codex/history/plans/028-tp-1d-integrated-pack-review-partial-A.md` for Daily and
Details, final cross-page review, and owner approval. Close this cycle with
exact evidence before activating that item. TP.1 and TP.1D remain incomplete
until partial-A records explicit approval.

## Production boundary

- Create `docs/theme-system/design-pack/INTEGRATED_PACK.md` with a 20-cell
  table schema, populated for Now and Hourly only. Each populated row names
  theme, page, page-contract section, measured shell/component values or
  formulas, token treatment, typed fact/action map link and any delta, state
  rule link, source locator, render locator, and open decision ID. Mark Daily
  and Details rows pending partial-A.
- Add an asset-use section for assets actually referenced by these ten cells:
  manifest path/SHA-256, crop/region, theme/page/role, review-only or possible
  future runtime adaptation, and licensing/semantic condition. A full board
  is never a proposed runtime screen.
- Add ten primary static SVG design renders under
  `docs/theme-system/design-pack/renders/` on a 393 × 852 dp canvas with
  documented insets and supplied illustrative facts. Add six labeled examples
  across Now and Hourly: 360 × 640 dp compact, font scale 1.3, RTL, a wider
  window (840 × 900 dp), Effects Off, and High contrast. Use 393 × 852 dp for
  other examples unless the condition defines a different viewport. Both
  pages must appear among the
  examples. Record the fixture facts, theme, viewport, and condition beside
  each render. These are design references, not installed-app captures.
- Update `README.md` with the pack link and upstream status and
  `SOURCE_DECISIONS.md` with measured resolutions/open decisions. Correct
  `NOW.md`, `HOURLY.md`, `FOUNDATION.md`, and shared rules only when render
  comparison supplies a reason; record old/new values and affected cells.
  Correct existing claims that TP.1D performs installed acceptance: TP.1D
  reviews design renders, while TP.3 compares the installed app.
- Update TP.1D progress in `docs/theme-pack-roadmap.md` and close exact cycle
  evidence into `.codex/history/` after acceptance. Preserve evidence under
  `.codex/test-artifacts/028-tp-1d-integrated-pack-review/`.
- No Kotlin, Compose, Android resources, presentation models, fixtures,
  providers, or product-semantic changes. Preserve existing worktree changes
  and the untracked intake archive.

## Functional invariants

- Standard Home remains `Now -> Hourly -> Daily -> Details`, with one outer
  horizontal pager, named pages, visible Hourly date/window controls, and
  normal Back semantics. Static designs cannot add another swipe owner.
- Every render uses only supplied presentation facts/actions. Do not infer a
  value, unit, update time, alert, entry, chart series, or status from art.
  Preserve chronology, provenance, missing fields, partial horizons, and
  accessibility meaning across themes.
- Important facts are visible text. Decorative marks/backgrounds are
  noninteractive and nonessential. Controls retain 48 dp guidance where
  applicable. Effects Off is opaque, static, and complete; High contrast has
  non-color cues and readable text pairs.
- Apply `REFERENCE_MEASUREMENT_METHOD.md` to rectangles, ratios, dp/sp
  conversion, and review. A mismatch needs a documented correction or a
  bounded open decision, never an undocumented art-driven override.

## Implementation steps

1. Save initial git status, cycle metadata, and changed-file manifest. Read
   the adopted UI contract, foundation, content/state rules, page contracts,
   decision ledger, typed presentation fields, token catalogs, manifest, and
   source crops. Record exact paths, dimensions, crop coordinates, and IDs.
2. Define the integrated table and asset-use schema. Reconcile ten cells with
   one shared shell. Check geometry, type hierarchy, typed slot/action map,
   theme treatment, source/update/status placement, and source locator.
   Populate only used assets and their disposition. Preflight the shared-shell
   handoff against the existing Daily/Details contracts and list conflicts
   for partial-A without claiming those cells reviewed.
3. Produce ten primary SVG renders and six condition examples from the
   documented composition. Derive one explicit illustrative presentation
   fixture from the existing deterministic demo data through the current
   presentation mapper; record the actual display strings and label them as
   design data. Keep each page's facts identical
   across themes. Inspect fit, control visibility, reading order, contrast,
   and Effects Off completeness against measured references.
4. Resolve measured discrepancies within existing authority. Update page
   contracts/ledger only with evidence. Unsupported product-semantic requests
   remain open owner decisions and cannot enter the design as new facts.
5. Complete a ten-cell checklist and render/asset/decision review. Record
   exact verification and limitations. Close this cycle only after its own
   acceptance passes, leaving partial-A next on the roadmap.

## Verification and evidence

- Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`.
  Resolve every Markdown link and asset/render path in populated rows. Check
  each used source asset against `ASSET_MANIFEST.json` path and SHA-256;
  record missing/changed sources rather than silently replacing them. Inspect
  SVG dimensions, viewBox, visible text, and references.
- Audit exactly ten unique theme/page rows and ten primary renders. Check
  visible facts/actions against `HomePresentation.kt` and `HomeLoadState.kt`;
  compare same-page renders for equal weather meaning. Check six examples
  against their named constraint. Review the page contracts' loading, live,
  partial/sparse, cached/stale, retained-refresh-failure,
  failed-without-data, nested unavailable, and missing-field treatments;
  preserve the absence of a current official-alert slot. Calculate contrast
  from actual opaque High
  contrast text/surface pairs and record colors and ratios.
- Run `git diff --check` and inspect the full diff, including untracked
  outputs, against initial status. Retain command output, source/hash and
  local-link audit, cell checklist, example review, changed-file list,
  decision delta, and unresolved mismatches in cycle evidence.
- Run `python scripts/dev.py check` when Android tooling and dependencies are
  available; record an environmental limitation rather than a false pass if
  it cannot run. This broad check is a regression gate, not visual evidence.
- Documentation checks do not claim installed visual, Android build, or
  accessibility-service acceptance. TP.3 owns installed comparison.

## Acceptance criteria

- Ten Now/Hourly cells have reproducible measures, typed maps, source
  locators, asset disposition, and individually reviewed primary renders.
- Six condition examples are present and reviewed. Compact/large-font
  content grows or scrolls, RTL keeps chronology, and Effects Off/High
  contrast keep facts and controls readable.
- Discrepancies are corrected from evidence or listed as bounded owner
  decisions with affected cells/options. No whole-pack approval is inferred.
- If remaining work approaches 45% of a fresh context window, stop at a
  reviewed handoff and add the smallest dependent partial to the roadmap.
  Do not stretch this cycle or advance TP.2.

## Risks and assumptions

- The phone crops combine multiple pages and some theme/page details are
  inferred from component crops. Each inferred dimension remains proposed
  until render comparison and owner review; source conflicts need locators.
- Static SVG rendering can expose composition and fit issues but cannot prove
  Android font metrics, TalkBack traversal, or installed behavior. The pack
  must give TP.3 reproducible targets without claiming its acceptance.
- Ten cells plus sixteen renders is the bounded upstream estimate. If source
  reconciliation or render corrections push this cycle toward 45%, stop and
  plan a smaller dependent partial before continuing.

## Out of scope

Daily/Details integration and renders, final 20-cell approval, production
renderer work, installed visual acceptance, and unrelated product behavior.
