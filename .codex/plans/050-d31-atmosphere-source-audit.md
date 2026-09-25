# Plan 050 — D31 five-theme atmosphere source audit and measurements

Status: Completed
Cycle ID: 050-d31-atmosphere-source-audit
Roadmap item: TP.1D-D31-partial-A
Created: 2026-09-25
Difficulty: 6/10
Context budget: target at most 40% of one agent context window; hard stop at 45%.
Recommended Codex CLI model: GPT-6 Luna, medium reasoning effort. This slice reconciles a bounded set of visual references and produces a traceable audit plus a small deterministic document validator. It does not design page treatments or change runtime behavior.

## Objective

Produce a source-traceable inventory and measured profile of the atmosphere
shown for each built-in theme: Atmospheric, Glass, Minimal OLED, Instrument, and
Terminal. The audit establishes reproducible source evidence for later D31
page-specific work. It does not create or approve the Now, Hourly, Daily, or
Details treatments.

The five-theme overview board is the reproduction target for the atmosphere it
shows for each theme. Each theme's existing backdrop and available theme asset
sheet are additional targets for the atmosphere those sources show. This follows
the D31 direction in `docs/theme-pack-roadmap.md`; the board's composite layout
and unrelated content are not pixel-exact page targets.

## Functional invariants

This is one documentation and verification slice, estimated at 35–40% of one
agent context window. Stop at 45%. Keep implementation to the source audit, its
small structural validator and focused unit tests, the design-pack index, and
cycle records/evidence. Do not expand into per-page treatments or new artwork.

Preserve these invariants:

- Weather meaning, values, provenance, chronology, navigation, accessibility,
  and Effects Off behavior do not change.
- Observations from reference images are separated from interpretation and
  proposed design choices.
- Every visual claim has an exact source path, manifest digest, and useful
  native-image crop/region locator. Absence is recorded as absence, not inferred
  from another theme.
- Use `REFERENCE_MEASUREMENT_METHOD.md` for proportional measurements. Record
  sample coordinates/regions, units, and method for numeric color or geometry
  values; label qualitative descriptions as qualitative.
- Preserve the established Atmospheric samples and their limitations in
  `SOURCE_DECISIONS.md`; do not silently replace or generalize them.
- Keep D29's owner-approved matrix separate. Do not change D28, D29, D32, the
  pinned r2 packet, or any prior decision/history.
- D31, TP.1D, and TP.1 remain open; TP.2 remains gated. Audit completion is not
  owner approval or installed visual acceptance.

## Production boundary

Allowed deliverables:

- Add `docs/theme-system/design-pack/D31_SOURCE_AUDIT.md`.
- Add a scoped deterministic checker and focused tests:
  `scripts/verification/d31_source_audit.py` and
  `scripts/verification/test_d31_source_audit.py`.
- Index the audit in `docs/theme-system/design-pack/README.md` and update the
  D31 execution status/active-slice pointer in `docs/theme-pack-roadmap.md`.
- Preserve verification and measurement evidence under
  `.codex/test-artifacts/050-d31-atmosphere-source-audit/` and close the cycle
  into `.codex/history/` with actual commands, results, and limitations.

Do not change Android production code/resources, source artwork or its manifest,
the immutable TP.1D packet, prior design contracts, unrelated working-tree
files, or the broad `scripts/dev.py` command surface.

## Audit content contract

`D31_SOURCE_AUDIT.md` must include:

1. **Status and purpose:** source audit, proposed design input only; identify
   the authorities and explicitly state the audit does not decide or approve
   any page/theme treatment.
2. **Method:** cite `REFERENCE_MEASUREMENT_METHOD.md`; explain source dimensions,
   screen/crop regions, normalized measurements, color sampling, precision and
   limits. Each numeric value must include a locator and reproducible method.
   Separate direct pixel samples from estimates and qualitative observations.
3. **Source inventory:** one record per applicable source with stable ID,
   theme, source class, repository-relative path, SHA-256 matching
   `ASSET_MANIFEST.json`, pixel dimensions, useful crop/region locator, and
   coverage/limitation. Cover the overview board, the five theme phone crops,
   the five theme backdrops, and the available Glass and Instrument theme asset
   sheets plus only their atmosphere-relevant crops. Record that equivalent
   theme sheets are absent for Atmospheric, Minimal OLED, and Terminal. Do not
   imply those missing sheets are references.
4. **Five theme profiles:** for each theme, identify the sources actually
   used; record measured colors and any codifiable shape, texture, scene,
   surface, and weather-art relationships; distinguish observations from
   interpretation; and list what those sources do not show. Include a clear
   profile even when a source provides no measurable value for a category.
5. **Cross-theme comparison and gaps:** summarize how the sourced atmospheres
   differ without normalizing them into one style. List missing page/state
   evidence as a gap for later D31 work. Do not propose a treatment in this
   slice.
6. **Limitations and handoff:** identify what later page-specific specifications
   and integrated review must decide; cite the D31 roadmap requirements. Do not
   claim palette selection, source-gap resolution, owner approval, or runtime
   reproduction.

## Implementation steps

1. Review the D31 authority and the reference-measurement method. Confirm the
   exact source set from `docs/assets/design-references/production-themes/README.md`
   and `docs/theme-system/ASSET_MANIFEST.json`; inspect existing source
   decisions and theme pack docs before measuring.
2. Inventory the overview board, five theme phone crops, five backdrops, and
   available asset-sheet boards/crops. Record path, manifest hash, dimensions,
   source class, theme, locator, and coverage. Do not edit or regenerate any
   source image.
3. Inspect relevant source regions at native resolution. Record color samples
   with exact coordinates/region and sampling rule; record relative geometry
   where it can be measured; describe shape, texture, and relationships
   qualitatively when the source does not support a reliable numeric measure.
   Maintain a source observation versus interpretation distinction.
4. Write `D31_SOURCE_AUDIT.md` with the inventory, five complete profiles,
   cross-theme distinctions, gaps, and limits defined by the content contract.
   Preserve existing Atmospheric measurements as source-attributed evidence.
5. Implement a small validator that checks the audit's structured inventory
   block against the asset manifest and required coverage. Keep prose and
   judgment human-reviewed; the checker must not infer visual facts.
6. Add focused tests for valid inventory/profile coverage and failure cases:
   missing theme/source class, duplicate source ID, nonexistent path, hash
   mismatch, missing locator or dimensions, an unrecorded absent sheet, and
   missing observation/interpretation/limitation fields. Ensure a failed check
   exits nonzero with the affected record identified.
7. Link the audit from the design-pack README. Mark D31 ACTIVE and point to
   this bounded source-audit slice in `docs/theme-pack-roadmap.md`; retain the
   later page-specific mapping and integrated review as unfinished D31 work.
8. Run the focused validator/tests, workflow, contract, and diff checks. Record
   the actual commands and outputs in the cycle evidence directory. Inspect the
   final diff and confirm no existing reference or unrelated user change was
   modified.

## Acceptance criteria

- All five themes have a complete, source-traceable profile. Every profile
  distinguishes measured observation, interpretation, and unavailable evidence.
- The inventory covers the required overview board/crops, five backdrops, and
  the existing Glass and Instrument sheets. Each inventory path exists, its
  SHA-256 agrees with the asset manifest, and each reference has an actionable
  locator and dimensions. Missing equivalent sheets for the other three themes
  are explicitly represented as absent.
- Measurements can be reproduced from recorded native-image regions and
  methods. No color, geometry, or texture is described as exact when it is an
  estimate or qualitative observation.
- Source gaps are recorded for later page/theme work without filling them with
  invented assets, palette choices, atmospheres, or page composition.
- The new validator and unit tests reject each listed malformed case and pass
  on the completed audit. The audit is indexed in the design-pack README.
- `docs/theme-pack-roadmap.md` identifies D31 as ACTIVE with this slice as the
  current bounded work; it keeps the all-pages mapping, proposed derivations,
  integrated review, owner decisions, and TP.1D/TP.1 gates outstanding.
- `python scripts/dev.py workflow`, `python scripts/dev.py contract`, the
  focused D31 validator/tests, and `git diff --check` pass. Their outputs and
  the source/measurement audit are preserved in the cycle evidence directory.
- No installed-rendering, owner-approval, page-design, or D31 completion claim
  is made.

## Verification and evidence

Retain under `.codex/test-artifacts/050-d31-atmosphere-source-audit/`:

- `workflow.txt` — `python scripts/dev.py workflow`;
- `contract.txt` — `python scripts/dev.py contract`;
- `focused-tests.txt` — focused unit tests and validator output;
- `source-inventory.md` — source paths, manifest hashes, dimensions, locators,
  and coverage/absence audit;
- `measurement-notes.md` — sampling/measurement method and reviewable source
  regions, including explicit estimates and qualitative fields;
- `diff-check.txt` — `git diff --check`.

Do not claim Android build/install, screenshots, visual owner review, TalkBack,
or Effects Off installed verification; those are outside this source-audit
slice. Preserve any newly discovered blocker and its evidence, close without
claiming D31 complete, and stop dependent work pending an explicit roadmap
update.

## Risks and assumptions

- Existing reference coverage differs by theme. The source index and asset
  manifest govern actual paths and hashes; the five backdrops exist, while
  explicit theme asset-sheet boards exist for Glass and Instrument.
- Some atmosphere properties can be sampled numerically; other shape/texture
  evidence is too small or composite to measure reliably. Label each kind
  accurately rather than forcing a numeric scale.
- This first slice records evidence only. Later planning must cover the full
  five-theme × four-page mapping, proposed same-theme derivations, all required
  renders, integrated review, and explicit owner decisions before the D-track
  gate can pass.
- Difficulty is 6/10: source reconciliation and measurement discipline across
  heterogeneous images is careful work, while runtime, page design, new art,
  and installed acceptance remain excluded.

## Out of scope

- Page-specific Now, Hourly, Daily, and Details atmosphere treatments or renders.
- The 20-cell theme/page mapping, source-gap proposals, supplementary theme
  sheets, and the integrated five-theme review.
- New or edited artwork, runtime resources, theme renderer integration, or
  Android changes.
- Owner decisions, D31/D-track completion, TP.1D packet revision/disposition,
  TP.1 closure, TP.2 eligibility, or TP.3 installed comparison.
- Changes to D28/D29/D32, weather semantics, product behavior, or accessibility
  meaning.
