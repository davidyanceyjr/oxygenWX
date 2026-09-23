# Initial plan — TP.1C-partial-B Details page design

Status: Complete; documentation review and required checks passed under active Plan 027
Parent cycle: `027-tp-1c-daily-details-page-designs`
Roadmap item: TP.1C-partial-B
Created: 2026-09-23, after partial-A review passed

## Objective and boundary

Define a measurable, source-traceable Details page design for Atmospheric,
Glass, Minimal OLED, Instrument, and Terminal, reusing accepted TP.1A/TP.1B
shell decisions and TP.1C-partial-A Daily shell compatibility. Produce
`docs/theme-system/design-pack/DETAILS.md`, update the design-pack index and
source ledger, and record B plus the combined ten-cell audit. This is a
documentation design package only; it makes no runtime, build, installed-render,
or pixel-match claim. The parent Plan 027 remains active through closure.

## Accepted prerequisite

TP.1C-partial-A passed review. `DAILY.md` uses the shared dynamic safe-area
shell, visible named selector, 56 dp header minimum, 48 dp selector/controls,
theme gutter and a vertically scrollable body. B reuses those shell values
without silently replacing them. Its output remains proposed pending TP.1D.

## Implementation

1. Record B initial `git status --short --branch` and parent metadata without
   rewriting prior edits. Read the accepted Daily contract/review, TP.1A/TP.1B
   shared rules, `MetricGroupPresentation`/`MetricPresentation` and mapping in
   `HomePresentation.kt`, `HomeLoadState.kt`, `sourceLine`/`updatedLine`, source
   references, theme catalogs, and Details-related components only as evidence
   of available presentation fields. Inventory exact image dimensions/crop
   locators and absent/conflicting art.
2. Define Details geometry in safe-area-relative 393 × 852 dp using the shared
   shell. Record title/source-freshness region, ordered group/metric flow,
   content-driven metric rows, text/spacing roles, long-label/value wrapping,
   scroll behavior, and reproducible reference ratios/formulas before rounding.
3. Map every visible slot to exact supplied values: `detailGroups` in supplied
   order; each group `title` and metrics; each metric `label`, `value`, optional
   `supporting`; `sourceLine`, `updatedLine`, outer status. Explain Conditions,
   Forecast pattern and Historical context only when supplied as groups. Do not
   infer provenance category, timestamp, units, label, or missing value from
   theme/art. Empty groups produce no invented placeholder panel.
4. Define five numeric/theme-specific treatments and complete, sparse/empty,
   loading, live/cached/stale, retained-refresh-failure, failed-without-data,
   unavailable/missing fields, 360 × 640 dp, font scale 1.3, RTL, High contrast,
   and Effects Off behavior. Specify long text wrap and scrolling without
   clipping, ellipsizing, or hiding source/status. Exclude charts, gauges,
   alerts, advice, and time series.
5. Update README and append only discovered decisions/conflicts to
   `SOURCE_DECISIONS.md`. Audit the five Details theme cells, then review all
   ten Daily/Details cells for shell agreement, typed field coverage, provenance
   clarity, state parity, chronological/layout reading order, and no semantic
   duplication. Preserve exact workflow/contract/diff output, per-part audits,
   combined review, and changed-file comparison under
   `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/partial-B/` and
   the parent evidence directory.

## Functional invariants and focused checks

Details maps only supplied metric groups, metric title/label/value/supporting
text and supplied source/update/status context; group and metric order never
changes. Current/provider-normalized conditions, derived forecast pattern and
historical reference remain distinct only where the typed groups provide those
boundaries. Missing groups are omitted, missing supplied values remain visibly
unavailable as provided, and supporting text is optional. All page facts stay
visible and accessible; decoration is supplemental. The shell retains the four
named pages and sole outer horizontal pager. RTL mirrors visual alignment but
preserves semantic group/metric order. Font scaling is honored; content scrolls.
Effects Off remains opaque, static and complete.

Run and preserve `python scripts/dev.py workflow`,
`python scripts/dev.py contract`, `git diff --check`, a local-link/source/token
and model/state audit, and the combined ten-cell checklist. Do not run Android
build/tests, emulator installation, reference renders, or pixel-match checks;
they are outside this documentation-only acceptance boundary.

## Acceptance and exclusions

B passes when `DETAILS.md`, README, ledger, and roadmap agree; all measurements
reproduce from recorded formulas; every visual element maps to supplied fields;
all five theme cells and required states/environments are complete; and the
combined ten-cell review finds no shell, chronology, provenance, or state
contradiction. Do not close TP.1C until A, B, combined review, and exact checks
pass. TP.1D still owns integrated 20-cell renders, installed review, and design
owner approval. TP.1/TP.2 are not promoted. No product specs, runtime files,
presentation models, providers, fixtures, or unrelated worktree edits are in
scope.
