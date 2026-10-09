# Plan 054 — D31 integrated atmosphere review package

Status: Completed
Cycle ID: 054-d31-integrated-atmosphere-review-package
Roadmap item: TP.1D-D31-review-package
Created: 2026-09-25
Revision: 2 (reviewed first draft)
Difficulty: 6/10
Recommended Codex CLI model: GPT-6 Luna, medium reasoning effort. This is a bounded source reconciliation, static review artifact, and focused validator update.
Context budget: target 35–40% of a fresh agent context window; hard stop before 45%. If inspection shows the source-panel set or validation cannot fit this boundary, stop with measured scope/evidence and propose a split before expanding work.

## Objective

Prepare a decision-ready integrated review package for the twenty proposed D31
theme/page atmosphere cells. Reproduce the exact overview-board atmosphere
regions, the five standalone theme backdrops, and the atmosphere-relevant
regions of the available Glass and Instrument sheets. Connect each panel to
the existing source-traceable proposal and its limitations. Supply a complete
cell-level review checklist, explicit pending decision fields, and repeatable
static validation. This cycle prepares materials; it does not conduct or imply
owner review or approval.

## Production boundary

Documentation, static design-pack assets, and narrow package validation only:

- Add `docs/theme-system/design-pack/D31_INTEGRATED_REVIEW.md` with protocol,
  source reproduction inventory, twenty-cell matrix, derivation review, and
  owner decision fields.
- Add only needed static panels under
  `docs/theme-system/design-pack/d31-integrated-review/`. Use exact source
  pixels, native crops where possible, and clearly labeled comparison layouts
  only where side-by-side review requires them.
- Add a small source/crop manifest and deterministic validator under
  `scripts/verification/` plus focused unit tests. The validator must verify
  source identity against `ASSET_MANIFEST.json`, source and output hashes,
  declared dimensions/crop bounds, panel inventory coverage, cell references,
  and pending decision state. It is an artifact-integrity check, not an image
  similarity or visual-approval test.
- Update the D31 entry in
  `docs/theme-system/design-pack/README.md` and the D31 progress/head in
  `docs/theme-pack-roadmap.md` to report package readiness only after checks
  pass; keep all proposals and gates unresolved.
- Preserve exact review/audit/verification evidence under
  `.codex/test-artifacts/054-d31-integrated-atmosphere-review-package/` and
  close the completed cycle in `.codex/history/`.

Do not edit Android production code/resources, source artwork or its manifest,
the immutable TP.1D packet, any of the twenty mapping cell objects, D29
decisions, or unrelated working-tree changes.

## Functional invariants

- All twenty mapping cells stay `proposed` until an explicit integrated owner
  review records decisions. Empty or omitted decisions mean pending.
- Keep source observation, interpretation, proposal, and source gap distinct.
- Every reproduced panel identifies exact source path, manifest SHA-256,
  native dimensions, crop/region locator, and whether pixels are an untouched
  crop or a labeled comparison arrangement. Never recolor or redraw a source.
- Cover all five themes and four pages. Mark same-theme derivations distinctly;
  Details remains a derivation where no dedicated Details source exists.
- Preserve product semantics, forecast facts, navigation, chronology,
  provenance, accessibility meaning, contrast requirements, and Effects Off
  completeness. References remain decorative.
- The overview board's composite layout and unrelated content are not fidelity
  targets; reproduce only the atmosphere regions required by D31.
- Do not close D31, TP.1D, or TP.1, approve the r2 packet, or unblock TP.2.

## Implementation steps

1. **Establish exact inputs.** Read the 050–053 histories/evidence, D31 source
   audit, twenty-cell mapping, reference measurement method, Details contract,
   asset index/manifest, and design-pack roadmap. Capture initial `git status`.
   Verify current mapping/audit validators and every eligible source digest.
   Record source IDs, native dimensions, required regions, and reuse candidates
   in cycle evidence. Do not repair prior artifacts in this slice.
2. **Fix the minimum panel inventory.** Create an explicit table for five
   overview-board theme atmosphere regions, five theme backdrops, and the
   atmosphere-relevant Glass and Instrument sheet regions. Reuse already
   extracted phone crops only when they are exactly the board regions required
   and traceable to the board; otherwise create the smallest faithful crop.
   Backdrops may be linked as native images without copying. Sheet panels must
   isolate the cited region without presenting adjacent unrelated examples as
   D31 targets. Record source bounds and resulting dimensions before making
   panels. If a faithful required region cannot be located, document the exact
   gap and stop rather than substituting another source.
3. **Create the integrity manifest and validator.** Declare source ID/path,
   expected source digest/dimensions, crop bounds where applicable, output
   path/digest/dimensions, reproduction kind, and human-readable locator for
   every panel. Add a focused validator that checks manifest schema, actual
   source hashes against the asset manifest, crop bounds, output presence/hash
   and dimensions, required panel/theme coverage, and links from the review
   matrix. Keep it deterministic and dependency-light; do not introduce image
   processing dependencies solely for validation. Negative validation cases
   must cover altered source digest, invalid bounds, missing/duplicate panel,
   wrong theme/source, mismatched output hash/dimensions, and incomplete
   review-cell coverage.
4. **Write the integrated guide.** Include purpose and pending status; a short
   review protocol; per-theme source reproduction checklist; a five-theme by
   four-page matrix linked to each existing proposal cell and relevant panel;
   separate source-backed and same-theme-derived cells; all five Details
   derivations and their absent dedicated-source limitation; cross-page
   identity/coherence questions; and exactly twenty owner decision fields
   (approve/revise/reject plus rationale or pending) and one overall D31
   disposition. Every initial field is explicitly pending. Make clear that
   owner answers are not inferred from silence.
5. **Audit fidelity and coverage.** Inspect every panel at native resolution
   and in the integrated guide. Confirm panel pixels match the declared source
   crop; labels remain legible; no crop implies a page composition; matrix
   links cover the twenty unique cells exactly once; every derivation has a
   review question; and source gaps/limitations remain visible. Record the
   audit and any limitations. Do not resolve proposal disagreements through
   undocumented interpretation.
6. **Update status and close.** Index the guide and panel directory in the
   design-pack README. Update only D31 progress wording/head in the theme-pack
   roadmap to say the integrated package is prepared and owner review is next;
   retain unresolved proposals, packet approval, TP.1D/TP.1, and TP.2 gates.
   Run focused unit tests and validator, D31 mapping/source audit validators,
   `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
   `git diff --check`. Inspect the complete diff and confirm unrelated staged
   or working-tree changes are untouched. Record exact outcomes/limitations in
   cycle evidence and history.

## Acceptance criteria

- The guide links all twenty unique theme/page proposals without editing their
  mapping objects and includes twenty pending cell decisions plus one pending
  integrated disposition.
- Panels cover the five overview-board atmospheres, five standalone backdrops,
  and relevant regions of both available theme sheets. Any absent or
  unusable target has an exact evidence-backed limitation and blocks a
  readiness claim.
- Every panel has verifiable source identity, digest, dimensions, region/crop,
  reproduction kind, and output identity. Original pixels are preserved.
- All same-theme derivations, especially Details, remain proposals with source
  basis, limitations, and explicit review questions.
- The validator and negative tests reject corrupted provenance, crop/output
  metadata, missing/duplicate/wrong-theme panels, missing cell links, and
  non-pending initial decisions. They do not claim perceptual fidelity or owner
  acceptance.
- README and roadmap describe package readiness only after evidence passes;
  D31, TP.1D/TP.1, packet approval, and TP.2 remain unresolved/gated.
- Focused checks, workflow, contract, and diff check results and limitations
  are recorded accurately. No installed-app visual acceptance is claimed.

## Verification and evidence

Retain under `.codex/test-artifacts/054-d31-integrated-atmosphere-review-package/`:

- `initial-status.txt`, `source-panel-audit.md`, and `matrix-coverage.md`;
- exact output from focused validator tests, standalone validator, D31 mapping
  check, D31 source audit, workflow, contract, and diff check;
- `package-review.md` recording native-resolution pixel/source review,
  readability, limitations, and explicit unverified boundaries.

No Android build/install, owner decision, TalkBack review, TP.1D packet
approval, or TP.3 installed screenshot comparison is required or claimed.

## Risks and assumptions

- The existing extracted board phone crops may provide faithful atmosphere
  regions; validate their relationship to the board before reuse.
- A manifest and side-by-side source panels make the material reviewable, but
  static checks cannot decide whether a proposal is aesthetically acceptable.
- This slice stops at a ready package. Owner review and any resulting proposal
  revision remain a separate bounded activity after the user supplies a
  disposition.

## Out of scope

- Owner review, approval/revision/rejection decisions, or marking any cell
  accepted.
- New design proposals, supplementary artwork, or redesign of the twenty cells.
- Runtime renderer/theme changes, Android visual verification, or TP.3 work.
- TP.1D/TP.1 closure, new immutable packet assembly/approval, or TP.2 start.
- Unrelated roadmap slices or cleanup of pre-existing working-tree changes.
