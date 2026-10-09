# Superseded initial draft — TP.1D-partial-A-partial-B final pack review

This first draft was split under the 45% context rule. Active upstream plan:
`.codex/history/plans/029-tp-1d-final-integrated-pack-review.md`. Dependent initial
plan: `.codex/history/plans/029-tp-1d-final-integrated-pack-review-partial-A.md`.
Use those plans and the execution head of `docs/theme-pack-roadmap.md` for
implementation and gate status; the text below is retained as planning history.

Status: Superseded; retained as the pre-split draft
Roadmap item: TP.1D-partial-A-partial-B
Parent item: TP.1D
Created: 2026-09-23
Context budget: target at most 35% of a fresh context window; stop before 45% and split a further dependent partial if needed.

## Objective

Review the completed 20-cell design pack as one system, resolve measured
cross-page discrepancies, make TP.3's installed comparison checklist
actionable, and obtain an explicit design-owner decision on one identified
revision. Expand this plan from partial-A's actual handoff and activate it in
its own cycle only after partial-A closes. Assign cycle ID and evidence path
at activation. This is the TP.1D and TP.1 completion gate.

## Production boundary

- Audit all 20 rows and primary references in
  `docs/theme-system/design-pack/INTEGRATED_PACK.md` for shared shell,
  responsive geometry, theme treatment, fact/action parity, source/status
  placement, provenance grouping, traceable art, and open decisions. Resolve
  discrepancies in the pack, relevant `NOW.md`, `HOURLY.md`, `DAILY.md`,
  `DETAILS.md`, `FOUNDATION.md`, `CONTENT_AND_STATE_RULES.md`,
  `SOURCE_DECISIONS.md`, and renders only with measured evidence and an
  explicit old/new/affected-cell record. Regenerate and recheck every render
  affected by a shared change.
- Complete `INTEGRATED_PACK.md`'s TP.3 installed comparison checklist. Each
  row needs device/viewport and font/effects/contrast/RTL condition, theme,
  page and state, target measure or behavior, screenshot/hierarchy evidence
  slot, pass/blocker/deviation fields, and the reference revision. Keep this
  a comparison instrument, not a claim of installed acceptance.
- Finish the design-pack `README.md`, render index/README, source/asset map,
  and decision ledger so the reviewed revision is reproducible. Build an
  owner packet with 20 primary renders, twelve condition examples, fixture
  and source/hash map, measurement and contrast evidence, open decisions
  including D28/D29, and a concise change log. Record a revision label and
  SHA-256 manifest for the exact reviewed design files before requesting
  approval; any post-review edit creates a new revision requiring re-review.
- Record the design owner's explicit approve/revise/reject decision, date,
  named revision and disposition of each unresolved conflict in the pack and
  cycle evidence. An unanswered request is pending, never approval. Update
  `docs/theme-pack-roadmap.md` and close TP.1D/TP.1 only after approval of
  the exact revision and completion of verification. Leave TP.2 gated
  otherwise. No Android production or product-semantic changes.

## Functional invariants

Preserve four named pages, one outer horizontal pager, visible Hourly and
Daily window controls, supplied facts and chronology, missing/partial
behavior, current versus derived/historical provenance, source/update/load
status meaning, and accessibility semantics. Important information stays
visible text. Decorative art cannot add a forecast fact, chart, gauge,
advisory or official alert. Effects Off stays opaque, static and complete;
48 dp control guidance and non-color status cues remain. Static review cannot
authorize a weather refetch or change canonical/presentation data contracts.

## Implementation steps

1. Capture initial status and both completed handoffs. Confirm all 20 rows,
   20 primary SVGs, twelve examples, fixture export, used-asset hashes and
   open decisions are present. Treat missing evidence as a blocker, not an
   inferred pass.
2. Review the full matrix by page and theme. Compare shared shell, fact and
   action maps, source/status and group placement, compact/font 1.3/RTL/wide,
   Effects Off and High contrast. Review the complete documented state matrix:
   loading, live, partial/sparse, cached/stale, retained refresh failure,
   failed without data, nested unavailable and missing fields. Confirm no
   invented official-alert slot. Correct measured discrepancies and recheck
   affected cells, or present bounded options to the owner.
3. Complete the installed comparison checklist and owner packet. Freeze the
   review revision and hashes. Request an explicit owner decision on that
   revision, including D28/D29 and every open conflict. If changes are
   requested, record them, revise within this boundary and issue a new
   revision for review; if the change exceeds the context limit, split again.
4. On approval, record the decision and exact verification/limitations in
   history, mark TP.1D/TP.1 complete in the theme roadmap, and enable TP.2.
   Pending/rejected decisions leave the gate open.

## Verification and evidence

Run workflow and contract; audit links/anchors, all 20 unique cells and
primary renders, twelve labeled examples, SVG metadata/dimensions, used
manifest SHA-256 and revision hashes. Check exact typed fact/action parity
across themes against the mapper export and page contracts, source/status
placement, chronology, missing-data semantics and no unsupported facts.
Rasterize and visually inspect affected references and full/end-of-scroll
captures; calculate actual opaque High contrast text/surface ratios and
verify non-color cues. Run `python scripts/dev.py check` when Android tooling
is available as a regression gate. Run `git diff --check` and inspect tracked
and new outputs. Retain checks, matrix, measured deltas, packet, owner decision
and limitations under the new cycle's evidence path. Report unavailable checks
accurately. Static references do not verify installed visuals or TalkBack;
TP.3 performs installed comparison.

## Acceptance criteria

All 20 cells and 32 references are reviewed with traceable measures, typed
maps, assets and conditions. The TP.3 checklist can be used without guessing
targets or evidence fields. The owner explicitly approves the frozen revision
and dispositions all open decisions; its SHA-256 manifest still matches the
files closed into history. Only then may TP.1D/TP.1 be marked complete and
TP.2 activated.

## Risks and assumptions

The owner may require a different font or weather-mark treatment, which can
invalidate multiple renders. Resolve that before a completion claim and
re-freeze the revision. Approval timing is external; keep the decision
pending without inventing a pass. Split new work before 45% context use.

## Out of scope

No Kotlin/Compose/resources/provider/model edits, installed-app acceptance,
weather refetch, fabricated weather/alert content or TP.2 implementation.
