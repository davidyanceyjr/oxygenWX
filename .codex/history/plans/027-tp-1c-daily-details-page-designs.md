# Plan 027 — TP.1C Daily and Details page designs

Status: Completed
Cycle ID: 027-tp-1c-daily-details-page-designs
Roadmap item: TP.1C
Created: 2026-09-23
Revised: 2026-09-23
Context target: each sequential partial at most 35% of a fresh context window; stop and split before any partial reaches 45%.
Implementation difficulty: 6/10

## Objective

Complete source-traceable, measurable Daily and Details page design contracts
for Atmospheric, Glass, Minimal OLED, Instrument, and Terminal, following the
authoritative product semantics and the proposed TP.1A/TP.1B design decisions.
This is a documentation design cycle. It makes no runtime, build,
installed-render, pixel-match, or design-owner-approval claim.

This parent plan remains the active `.codex/current.md` plan while the roadmap
executes two sequential, narrowly bounded work packages:

1. **TP.1C-partial-A — Daily:** five theme treatments, five-day window rules,
   sparse/unavailable cases, and handoff review. Initial plan:
   `.codex/history/plans/027-tp-1c-daily-details-page-designs-partial-A.md`.
2. **TP.1C-partial-B — Details:** typed metric groups, provenance separation,
   five theme treatments, and the combined ten-cell review. Create its initial
   plan only after A has passed review; it must use A's accepted shared-shell
   decisions and remain within the same 35% target.

Finish and review A before beginning B. If source discovery or required output
pushes either work package toward 45%, stop and create a smaller dependent
`partial-[A-Z]` roadmap work package before continuing. The parent cycle closes
only after both parts and their combined review pass.

## Production boundary

- Add `docs/theme-system/design-pack/DAILY.md` in A and `DETAILS.md` in B.
- Update `docs/theme-system/design-pack/README.md` and
  `SOURCE_DECISIONS.md` with status, links, measured choices, and conflicts.
- Update `docs/theme-pack-roadmap.md` at the execution head and TP.1C entry as
  work advances; do not promote TP.1, TP.2, or TP.1D.
- Preserve initial state, source locators, decisions, checklists, exact command
  output, per-part audits, and combined review under
  `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/`.
- Documentation only. Do not change Kotlin, Compose, runtime resources,
  presentation models, providers, fixtures, product semantics, or unrelated
  worktree changes. Do not rewrite pre-existing edits merely to make the diff
  appear slice-only.

## Functional invariants

- Standard Home remains `Now -> Hourly -> Daily -> Details`; the outer pager
  owns global horizontal swipes. Daily window changes use visible controls.
- Daily defines at most five supplied chronological entries per selected
  window. Each visible row maps date, condition, low/high, precipitation, and
  spoken summary to the supplied `DailyEntryPresentation`; missing/unavailable
  values remain explicit and no entry is padded, duplicated, inferred, or
  invented.
- Details maps only the supplied `detailGroups: List<MetricGroupPresentation>`
  and its `MetricPresentation` title/label/value/supporting fields, plus
  supplied source/update/status context. It preserves the groups' supplied
  order and keeps source-normalized/current conditions, forecast-pattern
  derived values, and historical/reference values distinct where those groups
  exist. Do not infer provenance from a theme, label, art, or display value.
- Designs add no gauge, time-series chart, advice, alert, or other unsupported
  data slot. The art cannot expand the presentation model.
- Themes affect presentation only. Preserve values, units, chronology,
  provenance, valid/update time, missing-data behavior, alert meaning,
  navigation, and accessibility meaning. Important facts are visible text;
  decoration remains supplemental and non-interactive.
- Specify 393 × 852 dp primary and 360 × 640 dp compact layouts, font scale
  1.3, RTL, High contrast, partial/missing data, load/refresh states, and
  Effects Off. Preserve system font scaling, readable scroll behavior, named
  controls, 48 dp touch targets where applicable, and opaque/static/complete
  Effects Off behavior.

## Implementation steps

### A — Daily (upstream portion)

1. Record initial worktree status and cycle metadata without changing existing
   edits. Read the adopted UI contract, TP.1A foundation/content rules and
   measurement method, TP.1B `NOW.md`/`HOURLY.md`, Daily typed fields and load
   states in `HomePresentation.kt`/`HomeLoadState.kt`, relevant source crops,
   token catalog entries, and roadmap/history authority. Record exact paths,
   image dimensions/crop locators, and any conflict or unsupported art.
2. Apply the established reference measurement method. Write
   `DAILY.md` with safe-area-relative 393 × 852 dp geometry; content and
   reading order; row bounds or reproducible formulas; type/spacing roles;
   vertical overflow; visible Earlier/Later behavior and selected/disabled
   semantics; and a complete slot/action-to-model map. Reuse the shared shell
   only by explicit link to its measured values; do not silently assume it.
3. Define five explicit theme treatments by token key and source locator.
   Include measured exceptions or resolved proposed values when references
   are silent or conflict. Cover complete, partial, 1–5-entry sparse, empty
   window, no-window, loading, cached/stale, retained-refresh-failure,
   failed-without-data, unavailable field, compact, large-font, RTL, High
   contrast, and Effects Off behavior using supported state facts only.
4. Update README and source-decision ledger. Audit each of five theme cells
   for traceable sources, reproducible measurements, model support, row
   chronology, control behavior, state coverage, responsive/effects behavior,
   and consistency with the TP.1B shell. Save A's checklist, decision delta,
   exact verification output, and review result before B starts.

### B — Details (downstream portion)

1. After A passes review, capture B's initial status and review accepted A
   outputs, TP.1A/TP.1B shared rules, `MetricGroupPresentation` and
   `MetricPresentation`, state/status types, and the relevant source-art
   locators. Treat only an explicit typed group/field and documented mapper
   semantics as evidence for a fact; record absent or conflicting art.
2. Write `DETAILS.md` with safe-area-relative geometry, heading/group/metric
   reading order, measurable bounds or formulas, type/spacing roles, vertical
   scrolling, and exact slot-to-field mapping. Define semantic group treatment
   for source-normalized/current measurements, derived forecast pattern, and
   historical context only when supplied. Keep `sourceLine`, `updatedLine`,
   outer status, and group supporting text in their correct roles; do not
   manufacture a provenance category or timestamp.
3. Specify five explicit theme mappings and complete, sparse/empty-groups,
   loading, cached/stale, retained-refresh-failure, failed-without-data,
   unavailable/missing fields, compact, large-font, RTL, High contrast, and
   Effects Off behavior. Define long-label/value wrapping and overflow without
   hiding provenance or clipping primary content. No unsupported charts,
   gauges, or empty placeholders.
4. Update README and source-decision ledger. Audit all five Details cells,
   then review all ten Daily/Details cells together for common-shell geometry,
   model coverage, provenance clarity, state parity, RTL reading order, and
   absence of semantic duplication. Save B and combined review evidence.

## Required deliverables and document updates

- `DAILY.md` and `DETAILS.md` each identify authority/status, exact source
  paths and crop locators, coordinate/inset assumptions, measurable layout or
  bounded formulas, reproducible reference-to-dp/sp conversions, content and
  accessibility order, scrolling, theme treatment, and a complete typed
  slot/action map.
- Each page provides a five-theme matrix with explicit token keys, surfaces,
  type hierarchy, borders/marks/background/effects, control/status treatment,
  contrast behavior, and any measured exception. “Use theme styling” is not a
  complete mapping. Every derived numeric choice is labeled proposed unless
  directly required by an authoritative contract.
- Both pages distinguish required from optional fields and specify what is
  omitted versus shown as unavailable. State examples are design contracts;
  they do not claim all states currently render in the app.
- README links both files and accurately describes proposed/review status.
  SOURCE_DECISIONS preserves prior entries and appends only decisions and
  conflicts actually discovered. Update theme-pack roadmap with A/B status
  and closure without changing upstream product semantics.
- Do not change `docs/SPECIFICATION.md` or the adopted UI specification unless
  review finds a real semantic conflict that requires a separate owner-directed
  decision; page design values cannot override them.

## Verification and evidence

Each partial must run and preserve exact output for `python scripts/dev.py
workflow`, `python scripts/dev.py contract`, and `git diff --check`. The
documentation review checklist is the slice's focused deterministic check:

- all local Markdown links, referenced source files, token keys, and image/crop
  locators resolve;
- every page slot and action maps to the typed presentation/state model;
- all five theme cells pass source, measurement, semantic, state, compact,
  large-font, RTL, contrast, and Effects Off checks;
- required numbers reproduce from the recorded ratio/formula before rounding;
- no unsupported data, page gesture owner, fabricated entry, silent meaning
  change, or unresolved measurement placeholder remains;
- the final changed-file review is compared with captured initial `git status`
  so pre-existing changes are preserved and are not attributed to this cycle.

After B, run the same workflow/contract/diff checks and a ten-cell combined
matrix audit. Do not run Android build, Android tests, emulator installation,
reference renders, or pixel-match checks for this documentation-only slice;
they are outside its acceptance boundary. Do not claim visual success from
these document checks.

## Acceptance criteria

- Both page contracts, index, source ledger, and roadmap agree and remain
  source-traceable. Measurements are reproducible and all five mappings per
  page have explicit proposed values and behavior.
- Daily windows, chronology, low/high, precipitation, sparse/empty behavior,
  and controls are unambiguous. Details group/provenance behavior follows only
  the supplied model and cannot make derived/historical values look like
  current observations, forecast source facts, or official alerts.
- A, B, and combined ten-cell audits pass with no fabricated data or unresolved
  design placeholder. Each evidence record states checks and limitations.
- TP.1C may close only after A, B, combined review, and verification complete.
  TP.1D still owns integrated 20-cell rendering, installed review, and explicit
  design-owner approval; TP.1 and TP.2 remain incomplete.

## Risks and assumptions

The two five-theme page work packages target at most 35% of a fresh context
each. Daily interaction is bounded by the existing five-day window model;
Details uses a finite supplied metric-group list. These are planning estimates,
not permission to exceed 45%; split before expanding either boundary. Art may
show unsupported information, and candidate tokens remain proposed until
TP.1D review.

## Out of scope

TP.1D integration and final renders; design-owner approval; resolver/Compose
implementation; presentation or canonical model changes; provider, cache,
alert, settings, location, and navigation work; unsupported visualizations;
unrelated pre-existing worktree changes.
