# Plan 053 — D31 Details atmosphere mapping

Status: Completed
Cycle ID: 053-d31-details-atmosphere-mapping
Roadmap item: TP.1D-D31-partial-C
Dependency: completed cycle `052-d31-daily-atmosphere-mapping`; preserve its fifteen proposed cells and evidence
Created: 2026-09-25
Revision: 2 (reviewed first draft)
Difficulty: 6/10
Recommended Codex CLI model: GPT-6 Luna, medium reasoning effort. The work is a bounded documentary mapping and Python validator extension; Luna is the most token-cost-efficient available model that should handle the source reconciliation and negative-test coverage reliably.
Context budget: target 30–35% of one fresh agent context window; hard stop before 45%. This stays one documentary slice: five Details proposals, a narrow validator extension, focused regression coverage, and two status-document updates. Source reproduction, integrated review, and owner decisions remain separate work.

## Objective

Add exactly five source-traceable, proposed Details atmosphere cells—one per built-in theme—to `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md`. Extend the existing source-aware validator and focused tests from the fifteen-cell Now/Hourly/Daily proposal to the complete twenty-cell theme/page matrix. Preserve all existing fifteen cell objects and their meaning. Synchronize the design-pack index and D31 roadmap progress to the completed cell matrix while keeping review and approval pending.

This cycle produces a proposal, not visual evidence sufficient to approve it. No indexed reference contains a dedicated Details screen. Phone crops, the overview board, and the Glass/Instrument sheets can support only explicitly labeled same-theme derivations of visible atmosphere cues; a theme backdrop can support only direct field-level observations. `DETAILS.md` defines content semantics and proposed treatments, not source evidence.

This is a documentary proposal slice. It does not approve any treatment, complete D31/TP.1D/TP.1, authorize runtime implementation, or release a later roadmap gate.

## Production boundary

Only these paths may change in this cycle:

- `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md`: add the five Details cells, update coverage/method/scope prose, and set ordered metadata for `now`, `hourly`, `daily`, `details`, `cell_count: 20`, and `coverage: "complete"`;
- `scripts/verification/d31_page_mapping.py`: extend canonical page coverage and Details-specific source-locator validation while preserving all prior schema, source-integrity, and completion-claim checks;
- `scripts/verification/test_d31_page_mapping.py`: add 20-cell contract, Details-specific, and prior-cell stability coverage;
- `docs/theme-system/design-pack/README.md`: update only the D31 mapping entry;
- `docs/theme-pack-roadmap.md`: update only the D31 execution progress/head to record the twenty-cell proposal and remaining D31 work;
- `.codex/history/plans/053-d31-details-atmosphere-mapping.md`, `.codex/current.md`, cycle evidence, and its eventual history record.

Do not modify Android production code/resources, source artwork or its manifest, `D31_SOURCE_AUDIT.md`, `DETAILS.md`, prior mapping cells, D29/D28/D32 decisions, immutable TP.1D packets, product semantics, or the broad `scripts/dev.py` command surface. Do not generate art, crops, source reproductions, or page renders.

## Functional invariants

- Atmosphere remains theme- and page-specific presentation only. It cannot alter weather facts, provenance, chronology, navigation, missing-data behavior, accessibility meaning, or Effects Off behavior.
- Details renders only supplied typed presentation strings and groups in supplied order. Keep source, update, outer status, current conditions, derived forecast patterns, and historical context semantically distinct; never imply provider provenance for derived/history groups.
- Preserve the Details contract in `docs/theme-system/design-pack/DETAILS.md`: visible `Details` identity; exact source/update/status text when supplied; groups and metrics in supplied order; no invented groups/metrics, charts, gauges, advice, alerts, or additional actions; wrapped/scrollable content; and honest unavailable/loading behavior.
- There are no dedicated Details atmosphere screens in the source inventory. Theme identity and atmospheric surface cues may inform same-theme proposals, but combined phone crops, overview panels, and component sheets are not treated as Details-page evidence. Explicitly identify these cells as proposed same-theme derivations and disclose the absence of direct Details references.
- Preserve five distinct theme identities. Do not restore retired Atmosphere Deck language or composition.
- Proposed treatment remains decoration-independent and preserves compact layout, font scale 1.3, RTL reading order, High contrast, and opaque/static/complete Effects Off behavior. Details weather art remains absent or strictly decorative; it cannot add a weather fact, metric, status, chart, or action.
- All twenty cells remain `proposed`; `scope.coverage: "complete"` means all canonical theme/page pairs are present only. It makes no review, approval, or D31 completion claim. Integrated review, source reproductions, derivation review, owner decisions, TP.1D/TP.1 closure, packet approval, and TP.2 eligibility remain pending.

## Implementation steps

1. **Lock and inspect inputs.** Review the completed 050–052 plans, history/evidence records, current fifteen-cell mapping/checker/tests, `D31_SOURCE_AUDIT.md`, `DETAILS.md`, adopted Details contract, reference index, and asset manifest. Save the original mapping and parsed fifteen-cell baseline under cycle evidence. Record initial worktree state and eligible source IDs per theme. Stop and record a concrete blocker if an earlier source hash or artifact integrity check fails; do not repair prior slices here.
2. **Review eligible atmosphere evidence for Details derivation.** Inspect the documented same-theme sources at native resolution where practical: per-theme backdrop, phone crop, and available Glass/Instrument sheet; use the cross-theme overview only for the selected theme's shown atmosphere. Reconcile the mapping against the five Details theme treatments already documented in `DETAILS.md`, but do not treat that page-composition contract as visual source evidence. Exclude absent-sheet records from citations. For phone/sheet/overview references, identify a specific visible theme cue that can carry into Details (such as field, surface, rule, or texture) and label the relationship `same_theme_derivation`; never describe the cited region as a Details screen or Details-specific evidence. Backdrop claims remain direct, field-level observations only. Record gaps and limitations explicitly; do not make new numeric measurements.
3. **Add exactly five Details records.** Append `<theme>-details` cells in canonical theme order after the existing fifteen objects. Keep existing fields/schema. Each cell must separate observation, interpretation, and proposed treatment; cite only eligible same-theme or cross-theme sources with the direct-versus-derived relationship accurately marked; identify the absence of a dedicated Details reference; provide theme-distinct palette, scene/backdrop, surfaces, weather-art relationship, Effects Off, High contrast, and responsive/accessibility constraints; and preserve Details provenance/group semantics. Keep atmosphere cues decorative: do not propose weather marks, scene elements, or labels as new data. Do not change prior cell objects or homogenize their wording.
4. **Extend validation narrowly.** Set canonical pages to `now`, `hourly`, `daily`, `details`; expect exactly twenty cells in page-major/theme-minor order and exact `coverage: "complete"`. Keep `status: "proposed; owner review pending"` and every cell's `review_status: "proposed"`. For Details phone/sheet/overview references, require `same_theme_derivation`, a locator identifying a concrete visible cue (for example field, surface, rule, light, or texture), and reject locator wording that asserts a dedicated Details screen. Permit `direct_region` only for a backdrop's field-level evidence. Preserve all prior source-theme/evidence/derivation checks, all non-Details locator checks, and completion/approval/TP.2 claim guards. Keep the rule page-specific so prior fifteen records retain their established evidence classes.
5. **Extend focused tests.** Preserve prior tests and add: valid 20-cell ordered acceptance; missing/duplicate/extra/wrong-theme/page/ID/order cases; incorrect count/pages/coverage; malformed Details fields and evidence classes; unknown, wrong-theme, absent-sheet, uncited-derivation, blank/generic locator cases; rejection of `direct_region` phone/sheet/overview evidence and an asserted dedicated Details locator; acceptance of a same-theme cue derivation and direct backdrop field evidence; and continued rejection of affirmative D31/TP.1D/TP.1 completion, packet approval, and TP.2 eligibility. Add a regression that loads the immutable 052 pre-edit fixture, compares all fifteen parsed prior cells exactly with the first fifteen final cells, and records a canonical JSON SHA-256 digest. Do not duplicate existing negative tests unless the changed Details/page rule creates a new regression risk.
6. **Update status documents.** Update the design-pack README and theme-pack roadmap to state that the twenty theme/page proposal cells are present and remain proposed. Name integrated 20-cell review, source reproductions, derivation review, required owner decisions, D31/TP.1D/TP.1 closure, packet approval, and TP.2 eligibility as pending. Do not turn complete cell coverage into a review or dependency-completion claim.
7. **Verify and close.** Run focused unittest, mapping checker, source-audit checker, `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and `git diff --check`. Review all five Details proposals against `DETAILS.md`, source IDs/regions, theme distinction, and the 15-cell parsed-object preservation result. Retain exact commands/output and review notes under `.codex/test-artifacts/053-d31-details-atmosphere-mapping/`, then close with actual verification and limitations. Do not automatically begin integrated review.

## Acceptance criteria

- The mapping contains exactly twenty cells, ordered as five themes for each page in canonical order: Now, Hourly, Daily, Details. Scope metadata reports the exact ordered pages, count 20, and `coverage: "complete"`.
- Every prior Now/Hourly/Daily object is structurally identical as parsed JSON before and after the change; comparison and canonical digest are preserved in cycle evidence.
- All five Details cells have eligible source citations and accurate evidence classes: phone/sheet/overview cues are same-theme derivations, while backdrop claims are direct field-level observations. Each cell records the absent dedicated Details source, concrete source gaps, separate observation/interpretation/proposal, and no unsupported claim of direct Details visual evidence.
- Details proposals preserve the supplied group/metric ordering, source/update/status separation, honest absence behavior, and theme-specific visual identities. No source image creates new weather content or data semantics.
- The validator accepts the repository artifact and focused tests reject malformed matrix, nested data, source, evidence, locator, derivation, and affirmative completion/approval claims with actionable errors.
- README and roadmap report all twenty proposed cells while integrated review, source reproductions, derivation review, owner decisions, D31/TP.1D/TP.1 completion, packet approval, and TP.2 eligibility remain open.
- Focused tests, source audit, workflow, contract, and diff checks pass with exact evidence retained. No installed rendering, runtime behavior, owner approval, or broader acceptance is claimed.

## Verification and evidence

Retain under `.codex/test-artifacts/053-d31-details-atmosphere-mapping/`:

- `pre-edit-mapping.md` and a parsed JSON baseline for the fifteen existing cells;
- `focused-tests.txt` — `PYTHONPATH=scripts/verification python -m unittest -v scripts/verification/test_d31_page_mapping.py`;
- `mapping-check.txt` — `python scripts/verification/d31_page_mapping.py`;
- `source-audit-check.txt` — `python scripts/verification/d31_source_audit.py`;
- `workflow.txt` — `python scripts/dev.py workflow`;
- `contract.txt` — `python scripts/dev.py contract`;
- `diff-check.txt` — `git diff --check`;
- `source-and-review-notes.md` — eligible source IDs/regions, direct versus derived evidence distinctions, source gaps, five-theme comparison, 15-cell preservation result and canonical digest, and final scope/limitations review.

Android build, installed rendering, and accessibility-service checks are out of scope because this cycle changes no runtime renderer or resources. Record them as unverified, not as visual acceptance.

## Risks and assumptions

- The no-dedicated-Details-source limitation is explicit in both the source audit and `DETAILS.md`; it is handled through proposed same-theme derivations using supported atmosphere evidence, not treated as a blocker.
- `scope.coverage` describes structural cell presence. It becomes `complete` when all twenty canonical cells exist even though review and approval remain pending; `status` and `review_status` carry that distinction.
- `DETAILS.md` contains proposed theme treatments as design inputs, not owner-approved source facts. Keep such choices labeled proposed and do not use them as citations for source observation.
- A defect in prior mapping objects, source audit, or asset hashes is outside this boundary. Record the blocker and stop instead of silently repairing it.
- This slice targets 30–35% of one fresh context. If the work expands into source extraction, new measurements, reproductions, or integrated review, stop and rescope before 45%.

## Out of scope

- Integrated review of the 20-cell pack, supplementary atmosphere source sheets, source reproductions, artwork, crops, or new measurements;
- owner decisions, D31 completion, packet revision/approval, TP.1D/TP.1 closure, TP.2/TP.3 work;
- changes to D28, D29, D32, product semantics, Android runtime, installed accessibility/visual acceptance claims, or unrelated documentation.
