# Plan 052 — D31 Daily atmosphere mapping

Status: Completed
Cycle ID: 052-d31-daily-atmosphere-mapping
Roadmap item: TP.1D-D31-partial-B
Dependency: completed cycles `051-d31-page-atmosphere-mapping` and `051-d31-page-atmosphere-mapping-partial2`; preserve their records, evidence, and ten accepted mapping cells
Created: 2026-09-25
Revision: 3 (completed)
Difficulty: 6/10
Context budget: target 30–35% of one fresh agent context window; stop before 45% and re-scope any newly discovered source-audit or artifact work.

## Objective

Add exactly five source-traceable, proposed Daily atmosphere cells—one for each
built-in theme—to `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md`.
Extend the source-aware validator and focused tests from the existing ten
Now/Hourly cells to fifteen Now/Hourly/Daily cells. Preserve the ten existing
cell objects and their meaning. Update the mapping's README and D31 roadmap
progress pointer to report the actual fifteen-cell interim state.

This is a documentary proposal slice. It does not complete D31, approve any
mapping, authorize runtime implementation, or release a later roadmap gate.

## Production boundary

Only these paths may change in this cycle:

- `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md`: add the five Daily
  proposals, accurate explanatory method/scope text, and ordered metadata for
  `now`, `hourly`, `daily` with `cell_count: 15` and `coverage: "partial"`;
- `scripts/verification/d31_page_mapping.py`: extend canonical page coverage
  and Daily-specific source-locator validation while retaining every existing
  Now/Hourly schema, source-integrity, and completion-claim guard;
- `scripts/verification/test_d31_page_mapping.py`: cover the fifteen-cell
  contract, Daily-specific failures, and prior-cell stability;
- `docs/theme-system/design-pack/README.md`: update only the D31 mapping entry
  to describe fifteen proposed Now/Hourly/Daily cells and pending work;
- `docs/theme-pack-roadmap.md`: update only the D31 execution progress/head to
  identify cycle 052 as active and, on completion, its fifteen-cell outcome;
- `.codex/history/plans/052-d31-daily-atmosphere-mapping.md`, `.codex/current.md`,
  cycle evidence, and its eventual history record for this lifecycle.

Do not modify the source audit, asset index/manifest, page composition files,
D29 matrix, prior packets, Android source/resources, weather/product semantics,
or earlier cycle evidence. Do not create/re-export artwork, crops, source
reproductions, or renders. Do not rewrite Now/Hourly proposals to make the
three-page matrix more uniform.

## Functional invariants

- Atmosphere remains theme- and page-specific presentation only. It cannot
  change weather facts, chronology, forecast window behavior, provenance,
  navigation, missing-data behavior, or accessibility meaning.
- Daily remains five actual chronological days per visible window, up to ten
  supplied days total. Rows expose date, condition, numeric low/high or honest
  unavailability, and precipitation meaning when available. Earlier/Later
  changes exactly one five-day window; there is no nested horizontal pager.
- No cell may infer an unsupported measurement, weather state, content slot,
  or interaction from decorative references. Do not treat a compact Daily
  preview as proof of the full page composition.
- Theme identities stay distinct. Do not restore retired Atmosphere Deck
  language, components, or composition.
- Proposed treatments preserve visible facts and controls in compact layouts,
  at font scale 1.3, in RTL, at High contrast, and with Effects Off. Effects Off
  is described as opaque, static, and complete; RTL preserves earliest-to-latest
  chronology.
- All fifteen cells remain `proposed`. Integrated 20-cell review, source
  reproductions, derivation review, owner decisions, D31/TP.1D/TP.1 closure,
  packet approval, and TP.2 eligibility remain pending.

## Implementation steps

1. **Lock and inspect inputs.** Review the two 051 plans, history records,
   evidence notes, current mapping/checker/tests, `D31_SOURCE_AUDIT.md`,
   `DAILY.md`, adopted Daily contract, and theme asset manifest. Record the
   starting worktree and identify the exact inventory IDs available per theme.
   Use the five theme phone crops and overview board; use the Glass/Instrument
   sheets only where their Daily region adds direct evidence; use each theme's
   backdrop for field-level evidence, not Daily layout. If a required source
   file is missing or its identity/hash disagrees with the audit, stop this
   slice and record the concrete blocker rather than silently repairing the
   source audit here.
2. **Inspect Daily evidence.** At native resolution, inspect the Daily-relevant
   preview region for each theme, plus eligible same-theme backdrop and sheet
   references. The overview may corroborate the selected theme's visible
   atmosphere, but its composite layout is not a full-page target. Separate
   direct observation from same-theme proposal. A phone or sheet locator must
   name the Daily/forecast-preview region and visible feature being cited; a
   generic whole-screen locator cannot support a Daily claim. A backdrop
   locator may remain field-level if its `supports` and observation are limited
   to the broad theme field. Record any absent source as a gap, never as visual
   evidence. Do not estimate numeric colors or measurements not already
   directly recorded in the audit.
3. **Add the Daily records.** Append exactly five `<theme>-daily` objects in
   canonical theme order after the existing Now and Hourly groups. Use the
   existing schema without new fields: non-absent `source_refs`, specific
   locators and evidence classes, four observation fields, interpretation,
   four proposed-treatment fields, Effects Off/High contrast/responsive
   constraints, concrete source gaps, cited derivation basis, rationale,
   limitations, and `review_status: "proposed"`. State explicitly when the
   Daily treatment is derived because the source only shows a small preview.
   Update introductory prose so the artifact accurately describes its
   Now/Hourly/Daily coverage and remaining gaps. The original ten cell objects
   must be unchanged when parsed as JSON; do not reorder or normalize them.
4. **Extend validation narrowly.** Set canonical pages to
   `now`, `hourly`, `daily`; expect exactly fifteen cells and the exact ordered
   five-theme × three-page matrix, using stable `<theme>-<page>` IDs and exact
   partial-scope metadata. Apply all current nested-field, source-theme,
   source-class, derivation-citation, evidence-class, and locator rules to all
   pages. Add the Daily-specific locator rule only for phone crops, asset
   sheets, and overview-board references; backdrop sources retain their
   field-level locator behavior. Keep all existing Now/Hourly and D31/TP.1D/
   packet/TP.2 affirmative-claim guards intact.
5. **Extend focused tests.** Preserve all existing regression cases and add:
   - acceptance of a valid fifteen-cell mapping and exact canonical page/theme
     ordering;
   - missing, duplicate, extra, wrong-theme/page, wrong-ID, wrong-order, and
     incorrect count/pages/coverage cases involving Daily;
   - malformed Daily nested fields and evidence classes;
   - unknown, wrong-theme, absent-sheet, uncited-derivation, blank-locator,
     generic-locator, and non-Daily phone/sheet/overview locator cases;
   - continued rejection of affirmative D31/TP.1D/TP.1 completion, packet
     approval, and TP.2 eligibility claims;
   Keep assertions specific enough that validator errors identify the cell,
   field, and offending source/pair. Separately save the pre-edit mapping in
   cycle evidence and compare its ten parsed Now/Hourly objects with the final
   artifact; record that comparison and a canonical digest in the review notes.
6. **Update status documents.** Change the design-pack README's D31 mapping
   entry to say fifteen proposed Now/Hourly/Daily cells and list Details,
   integrated 20-cell review, source reproductions, derivation review, and
   owner decisions as pending. In the theme-pack roadmap, preserve the two
   completed 051 records and add the active cycle-052 plan/evidence pointer;
   when this implementation slice closes, describe only its fifteen-cell
   proposal. Keep D31 and TP.1D/TP.1 open and TP.2 gated. Do not change unrelated
   roadmap decisions or turn pending work into a dependency-completion claim.
7. **Verify and close.** Run the focused unittest command, mapping checker,
   `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
   `git diff --check`. Review the full fifteen-cell artifact against source
   IDs/regions, the Daily product contract, cross-theme distinction, and the
   parsed ten-cell preservation check. Save exact commands/output and source
   review notes under
   `.codex/test-artifacts/052-d31-daily-atmosphere-mapping/`. Update the
   roadmap completion pointer only after those checks pass, then close into
   `.codex/history/` with actual verification and limits. Do not automatically
   activate Details work.

## Acceptance criteria

- The mapping contains exactly fifteen cells, ordered as five themes for Now,
  then five for Hourly, then five for Daily; metadata matches the exact ordered
  page list, count, and `partial` coverage.
- The ten pre-existing Now/Hourly objects are structurally identical as
  parsed JSON values before and after the change; the comparison and canonical
  digest are retained in cycle evidence. Existing validator/test behavior
  remains covered.
- Each Daily cell distinguishes cited observation from interpretation and
  proposal. Every source reference exists, is eligible, belongs to the same
  theme or the permitted cross-theme board, has a useful Daily-specific locator
  where applicable, and supports only the statement claimed. Derivations cite
  their source IDs and disclose missing direct evidence.
- The five Daily proposals retain separate theme identities and the adopted
  five-day/date/condition/low-high/precipitation/window-control contract. No
  full-page composition, numeric measurement, or source fidelity is invented.
- The validator accepts the repository artifact and focused tests reject
  malformed matrix, scope, nested values, evidence, source, locator,
  derivation, and completion/approval cases with actionable errors.
- README and roadmap describe the actual interim fifteen-cell proposal and
  leave Details, integrated review, reproductions, owner decisions, D31/TP.1D/
  TP.1 completion, packet approval, and TP.2 eligibility open.
- Focused tests, mapping checker, workflow, contract, and diff check pass with
  exact output retained. No installed rendering, runtime behavior, owner
  approval, or broader acceptance is claimed.

## Verification and evidence

Retain under
`.codex/test-artifacts/052-d31-daily-atmosphere-mapping/`:

- `focused-tests.txt` —
  `PYTHONPATH=scripts/verification python -m unittest -v scripts/verification/test_d31_page_mapping.py`;
- `mapping-check.txt` — `python scripts/verification/d31_page_mapping.py`;
- `workflow.txt` — `python scripts/dev.py workflow`;
- `contract.txt` — `python scripts/dev.py contract`;
- `diff-check.txt` — `git diff --check`;
- `source-and-review-notes.md` — exact Daily source IDs and regions, direct
  versus proposed choices, source gaps/limits, five-theme comparison, the
  ten-cell parsed-JSON preservation result, and final scope review.

Android build, installed rendering, and accessibility service checks are not
required because this cycle changes no runtime renderer or resources. Record
them as out of scope/unverified in the history entry rather than implying a
visual pass.

## Risks and assumptions

- Some references show only a compact Daily strip. They can support visible
  atmosphere and surface relationships only; `DAILY.md` and adopted product
  requirements govern the full page contract.
- The source-aware checker currently recognizes only Now and Hourly. Extend
  the ordered matrix and page-specific locator guard without weakening prior
  checks or treating broad backdrop evidence as page-layout proof.
- A material defect in an earlier cell, source-audit profile, or source hash is
  outside this boundary. Record it and stop for a separate bounded correction.
- The work is expected to fit 30–35% of a fresh context. If source ambiguity
  requires new asset extraction, source-audit repair, reproductions, or wider
  integrated review, stop before the 45% limit and split that separate work.

## Out of scope

- Details cells, a 20-cell integrated review, source reproductions, artwork,
  crops, supplementary sheets, or new source-audit measurements;
- owner decisions, D31 completion, packet revision/approval, TP.1D/TP.1
  closure, TP.2/TP.3 work;
- changes to D28, D29, D32, product semantics, Android runtime, installed
  accessibility/visual acceptance claims, or unrelated documentation.
