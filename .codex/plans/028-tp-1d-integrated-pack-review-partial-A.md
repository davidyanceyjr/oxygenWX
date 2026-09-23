# Initial plan — TP.1D-partial-A Daily and Details pack completion

Status: Draft; dependent on Plan 028 closure
Roadmap item: TP.1D-partial-A
Parent item: TP.1D
Created: 2026-09-23
Revised: 2026-09-23
Context budget: target at most 35% of a fresh context window; stop before 45% and split remaining work if needed.

## Objective

Complete the matching five Daily and five Details cells, their primary renders
and condition examples, then review the 20-cell pack and prepare explicit
design-owner approval. Expand this initial plan from Plan 028's actual
handoff and activate it as a new cycle only after Plan 028 closes. Assign its
cycle ID and evidence path at activation.

## Production boundary

- Complete Daily/Details rows and asset-use entries in
  `docs/theme-system/design-pack/INTEGRATED_PACK.md` using Plan 028's accepted
  schema. Add ten 393 × 852 dp primary SVG renders and six labeled examples
  across Daily and Details: compact, font scale 1.3, RTL, wider window,
  Effects Off, and High contrast. Use 840 × 900 dp for the wider-window
  example and 393 × 852 dp for other examples unless a case sets a different
  viewport. Use the same render conventions and labeled
  illustrative facts as Plan 028, with page-specific supplied data.
- Reconcile shell, source/status placement, theme treatment, and provenance
  grouping across all 20 cells. Update `DAILY.md`, `DETAILS.md`,
  `FOUNDATION.md`, shared rules, and `SOURCE_DECISIONS.md` only for measured
  discrepancies. Finish `README.md` and an installed comparison checklist in
  `INTEGRATED_PACK.md` with device/viewport, page/theme/state, target measure,
  screenshot/hierarchy, pass/blocker, and deviation fields for TP.3.
- Prepare an owner review packet from 20 primary renders, twelve condition
  examples, source/asset map, open decisions, and measured change log. Record
  a revision label and SHA-256 manifest of the reviewed design files before
  asking for approval. Record the owner's explicit decision, date, and that
  exact revision in the pack and cycle evidence. An unanswered request is not
  approval.
- Update TP.1D/TP.1 roadmap status and close the cycle only after approval.
  If withheld/pending, retain the gate and keep TP.2 blocked. No product
  semantic or production code changes.

## Functional invariants

Keep the four named pages, one outer swipe owner, visible forecast window
controls, supplied facts, chronology, provenance, unavailable behavior, and
accessibility meaning. Daily uses only its supplied entries; Details keeps
normalized/current, derived, and historical groups distinct. Effects Off is
opaque/static/complete, with important facts visible as text. Reference art
cannot introduce a forecast, chart, gauge, advisory, or official alert.

## Implementation steps

1. Capture initial worktree state and Plan 028 handoff. Recheck used source
   hashes, art locators, Daily/Details typed fields, and open decisions.
2. Complete ten rows and asset entries. Preserve supplied five-day windows,
   chronology, optional values, source/update/status, and the separation of
   normalized, derived, and historical Details groups.
3. Produce and compare ten primary renders and six condition examples.
   Record source measure, mismatch, resolution, and remaining uncertainty per
   cell. Keep all important facts as visible text.
4. Audit the full 20-cell matrix for shell geometry, fact/action parity,
   source traceability, contrast, responsive behavior, and Effects Off.
   Review the complete state matrix: loading, live, partial/sparse,
   cached/stale, retained-refresh-failure, failed-without-data, nested
   unavailable, missing fields, and no invented official-alert slot.
   Complete the practical TP.3 installed comparison checklist.
5. Present the review packet for owner decision. On approval, record the
   revision and close with exact evidence/limits. Otherwise record requested
   changes and leave TP.1D/TP.1 open.

## Verification and evidence

Run workflow, contract, local-link/render-path, used-asset SHA-256, 20-cell
uniqueness, typed fact/action, same-page cross-theme parity, actual rendered
contrast, SVG dimension/content, and `git diff --check` checks. Inspect the
complete diff and retain command output, comparisons, decision delta, owner
decision, and limits under the new cycle's evidence path. Run
`python scripts/dev.py check` when Android tooling/dependencies are available
and record any limitation. Correct stale references claiming TP.1D installed
acceptance in the page contracts and ledger; TP.3 owns that comparison.

## Acceptance criteria

Acceptance requires
20 reviewed cells and primary renders, twelve condition examples, the asset
map and installed checklist, and explicit approval of a named revision. Open
conflicts must have an explicit owner disposition in that decision.
Documentation renders do not prove installed runtime or TalkBack behavior;
those checks remain TP.3/release work.

## Risks and assumptions

The final cross-pack review and owner feedback may reveal a conflict that
changes earlier proposed measurements. Record the source and affected cells;
if correction work approaches 45% of a fresh context window, split a further
dependent partial before implementation continues. Pending or withheld owner
approval leaves TP.1D/TP.1 open and TP.2 gated.

## Exclusions

No Kotlin/Compose/resources/provider/model changes, app refetch, fabricated
weather/alert content, TP.2 implementation, or installed-app acceptance.
