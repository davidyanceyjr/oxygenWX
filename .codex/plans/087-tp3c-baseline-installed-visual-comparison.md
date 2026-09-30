# Plan 087 — Baseline installed visual comparison

Status: Completed  
Cycle ID: 087-tp3c-baseline-installed-visual-comparison  
Roadmap item: TP.3C  
Created: 2026-09-30  
Reviewed: 2026-09-30

**Difficulty: 4/10.** This first portion is a read-only evidence audit and
twenty-cell visual review against already-defined criteria. It spans five
themes and four pages, but does not change production, build/install a new
version, or debug corrections. Estimated use is at most 30% of a fresh context
window; stop before 45%. Any implementation/recapture belongs to the dependent
`TP.3C-partial-A` plan and is not authorized by this cycle.

This split follows `docs/theme-pack-roadmap.md`'s context-budget convention:
cycle 087 retains the original ID for the evidence review; any later dependent
correction plan uses the `TP.3C-partial-A` suffix. It is created only if the
review finds actionable correction work.

## Objective and observable outcome

Compare the twenty installed normal-app Home baseline captures from TP.3A and
TP.3B against the approved five-theme/page references using the executable
checklist and measurement rules in
`docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md`. Record a sourced
disposition for every case and classify any measured in-scope composition
deviation for a separately planned correction pass. This cycle performs no
production edits, recapture, or correction. Its outcome is a complete
twenty-cell evidence review and an explicit recommendation whether TP.3C
passes or requires dependent correction work. Unavailable or incomparable
required evidence is a blocker.

The independent outcome is a reproducible twenty-row comparison ledger with
immutable input identities, measured evidence, and source-grounded dispositions.
This cycle does not perform TP.3D.

## Authority, dependencies, and assumptions

- Product/technical and visual authority: `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md`.
- Theme-pack order and exit: `docs/theme-pack-roadmap.md`, TP.3C. It depends on
  passed TP.3A and TP.3B and requires all twenty 393 × 852 dp baseline cases,
  side-by-side comparison, one visual correction pass, and PASS-only progression
  to TP.3D. UI capture process: `docs/UI_DEVELOPMENT_WORKFLOW.md`.
- TP.3A PASS is recorded in `.codex/history/2026-09-30-085-tp3a-now-hourly-compositions.md`;
  TP.3B PASS is recorded in
  `.codex/history/2026-09-30-086-tp3b-daily-details-compositions.md`. Their
  capture manifests, review notes, and test evidence are under the matching
  `.codex/test-artifacts/085-*` and `.codex/test-artifacts/086-*` directories.
  The existing records identify ten captures apiece at 393 × 852 dp, font
  scale 1.0, en-US/LTR, Standard contrast, with theme-specific effective
  effects. They were produced by different commits/APKs; do not present that
  fact as a visual difference. For any production correction, create one
  current APK and recapture every affected cell with recorded matching
  settings. Any prerequisite image, manifest, or comparison input that fails
  identity/condition validation blocks the affected comparison until resolved.
- Comparison targets are the twenty primary references and integrated
  refinements identified by the checklist, with accepted decisions in the
  design-pack documents. Proposed reference-only details are not pixel-exact
  criteria. Use actual Android insets and checklist tolerances; do not compare
  reference SVG text rasterization directly to Android pixels.
- The ten TP.3A and ten TP.3B capture hashes and metadata are recorded in their
  manifests. Their files must be independently checked for presence and SHA-256
  before comparison. TP.3B explicitly reports reference parity unverified.
- No owner decision is required for routine measurement and disposition against
  the accepted packet. If a finding needs an unapproved design choice, expands
  the production boundary, or conflicts with an unresolved authority, document
  the exact decision and block; do not infer approval.

## Production boundary

Documentation and evidence review only. Do not modify production code, test
code, the capture harness, approved references, TP.3A/TP.3B artifacts, or
screenshots. All comparison artifacts belong under
`.codex/test-artifacts/087-tp3c-baseline-installed-visual-comparison/`.
Keep prerequisite inputs unchanged. If the review identifies a correction
candidate, record its affected cells, criterion, and regression implications as
a handoff to `TP.3C-partial-A`; do not implement or recapture it here.

## Context budget

Target at most 30% of a fresh context window; stop before 45%. Work is limited
to prerequisite evidence validation and a structured review of twenty existing
captures using an already executable checklist. Do not begin code exploration
for potential fixes, broad regression investigation, or new capture work. If
evidence defects make reliable review exceed the estimate, record the exact
blocker and stop. The dependent correction/recapture plan must itself be
reviewed against the same 45% rule before activation.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page names, one outer
  pager as global horizontal-swipe owner, static-tap behavior, and Back
  semantics. Hourly retains six-entry windows/date jumps; Daily retains
  five-entry windows and one-window controls.
- Render only supplied canonical/presentation facts. Preserve units,
  chronology, missing-data behavior, condition identity, source, valid/update
  times, freshness/status, and the distinction between normalized, derived,
  and historical content. No fetch, data rewrite, fabricated value, placeholder,
  or forecast-derived official alert is permitted for visual polish.
- Themes/effects affect presentation only. Preserve callbacks, selected
  forecast, request count, provenance, and accessibility meaning. Weather marks
  and scenes are decorative; required facts remain visible and semantically
  available. Effects Off remains opaque, static, and complete.
- Do not revive retired Atmosphere Deck elements or compositions. The overview
  board composite is not a pixel-parity target; use each approved page/theme
  reference and accepted refinements.

## Visual objective and baseline conditions

Evaluate visual hierarchy, composition, geometry, surfaces, typography, and
theme-specific marks against the approved page/theme reference, while checking
that all expected text and controls remain readable and reachable. Record the
measured content width, gutter, body top, fixed target sizes, text bounds,
scroll/reachability observations, and a screenshot crop for every deviation.
For fixed reference geometry use the checklist allowances (±2 dp for gutters
and widths; ±4 dp for body top or fixed component positions after inset
normalization). Minimum target sizes have no negative tolerance. Content-driven
text/row heights must not clip or overlap. Qualitative hierarchy/art findings
require a written reference-grounded rationale, not a fabricated pixel score.

Primary baseline conditions are 393 × 852 dp, font scale 1.0, en-US, LTR,
Standard layout, Standard contrast, deterministic TP.3 illustrative fixture at
the checklist's specified time and processing path, and each theme's indexed
effective effects setting (Atmospheric/Glass/Instrument Subtle; Minimal
OLED/Terminal Off). Record actual pixel dimensions, density, system insets,
build/APK, device/API, locale/direction, fixture/load state, requested and
effective effects for each image. Capture start state, end-of-scroll where the
page scrolls, hierarchy/semantics, and interaction observations. Exercise
Hourly date/window controls and Daily window controls, then restore window zero
and scroll start for the comparison image. Do not silently change any capture
condition to make a reference fit.

No compact, font-scale 1.3/2.0, RTL, High contrast, additional Effects Off,
sparse/load-state screenshot matrix, or TalkBack service traversal is required
by this baseline cycle; these remain TP.3D or later boundaries. Existing
baseline Effects Off cases are compared under their indexed settings only.

## Implementation steps

1. **Freeze and audit inputs.** Read the TP.3A/TP.3B histories, plans,
   manifests, `review.md` files, source revisions, approved 20 primary
   references, composition contracts, and reference measurement method. Copy
   or inventory each original capture without modifying it; independently
   verify all twenty paths and SHA-256 values, expected theme/page identity,
   393 × 852 dp viewport, font/direction/contrast/effects, fixture/load state,
   and build/device metadata. Record an immutable input inventory and any
   mismatches. A missing/corrupt image or incompatible condition is blocked,
   not replaced by a preview or regenerated without provenance.
2. **Compare the complete matrix.** For each of the twenty theme/page cells,
   conduct an explicit side-by-side visual review against its indexed approved
   page reference and integrated refinements. Use checklist geometry and
   tolerances after inset normalization. Inspect the full scroll end for
   scrollable pages, hierarchy/semantics and interactions, and supplied facts.
   Record reference revision/path, original capture/build identity, measured
   observations, relevant crop, and PASS, FINDING, KNOWN-LIMITATION, or BLOCKED
   with a source-grounded rationale. Separate implementation deviations from
   accepted source limitations and decorative gaps; a known limitation is not
   silently converted to a pass criterion.
3. **Classify findings without editing.** Map each finding to a specific
   accepted criterion and approved reference. Distinguish implementation
   deviations from accepted limitations, source gaps, or subjective
   preferences. For a correction candidate, specify affected cells, relevant
   regression pages/themes, and focused behavior checks for the dependent
   partial plan. Stop and mark blocked if resolution needs an unapproved design
   decision, semantic/navigation/data change, unrelated Rain glyph work, or
   scope expansion.
4. **Reconcile and report.** Complete all twenty dispositions and evidence
   links/hashes, run `git diff --check`, and inspect the plan/evidence diff.
   Record exact commands/results and unavailable checks with causes. Recommend
   PASS only if every case meets criteria without correction; otherwise record
   correction candidates for the dependent plan or precise blockers. TP.3D
   cannot proceed until TP.3C's complete correction/recapture gate passes.

## Acceptance criteria

- TP.3A and TP.3B prerequisite histories and evidence are reviewed; all twenty
  original screenshots are present, hash-valid, correctly identified, and
  condition-compatible with the approved references.
- All twenty original screenshots are present, hash-valid, correctly
  identified, and condition-compatible with the approved references; any
  failed integrity/comparability check is explicitly blocked.
- A twenty-row ledger names each reference revision, source capture identity,
  measurements, hierarchy/scroll/interaction evidence, disposition, and
  rationale. Scrollable pages include start and end review.
- No production/test/harness changes or new captures are made. Each actionable
  finding is mapped to its accepted criterion and handed off with affected
  cells/regression scope for `TP.3C-partial-A`.
- PASS recommendation requires all twenty cases to meet accepted composition
  criteria without correction and applicable review evidence to be complete.
  Any unmet criterion or missing evidence is a blocker. A later correction
  partial must complete one-pass recapture and verification before TP.3C/TP.3D
  progression.
- `python scripts/dev.py workflow` and `git diff --check` are recorded.
  Contract/build checks, instrumentation, install, and new screenshot evidence
  are not required because this cycle makes no app changes and uses existing
  installed captures; prior cycle results are cited, not claimed as rerun.

## Verification and evidence paths

Root: `.codex/test-artifacts/087-tp3c-baseline-installed-visual-comparison/`.

- `input-inventory.md` — source revisions/status, prerequisite plan/history
  links, all twenty capture paths and SHA-256 values, reference paths/identity,
  and complete build/device/fixture/settings metadata plus compatibility audit.
- `comparison-ledger.md` — exactly twenty case rows with target identity,
  source screenshot identity, measurements/findings, evidence/crop links,
  disposition, and final result.
- `original/` — immutable copies or retained paths to all twenty original
  screenshots and source manifests. Never overwrite these.
- `review.md` — visual objective, twenty dispositions, finding classification,
  candidate corrections and dependent-slice handoff, final recommendation,
  blockers, and limitations.
- `commands.md` and `final-checks.md` — exact read-only audit/review commands,
  workflow and diff-check results, and unavailable checks with reason.

Use checklist-derived stable IDs `P-<theme>-<page>` and link original
`<id>-start.png`, `<id>-end.png` when available, `<id>-hierarchy.txt`, and
`<id>-result.md` from the prerequisite cycle directories; do not copy or
modify them. Store finding crops under this cycle's evidence directory with
their source path/hash. Any new capture commands or scripts belong to the
dependent correction partial, not this evidence-only cycle.

## Risks and assumptions

- TP.3A and TP.3B each report ten expected installed images and valid manifests,
  but cycle 087 must independently verify file presence, hashes, metadata, and
  source identity before using them.
- The two prerequisite sets are from distinct builds. If weather fixture,
  renderer, or reference inputs differ in a way that affects comparability,
  document the concrete difference and block or recapture the required matrix
  on one build before attributing any discrepancy to visual implementation.
- Approved reference files include proposed geometry/source gaps. Apply only
  accepted criteria and integrated refinements; proposed or illustrative
  details do not become pixel-exact acceptance requirements by implication.
- Android/system font metrics and insets may differ from SVG renderings.
  Measure actual insets and font conditions and apply the published tolerances;
  record, do not waive, remaining deviations.
- Corrections can regress other themes/pages. One-pass limit requires stopping
  when a second correction or broader design decision is necessary. Any
  correction and recapture is deferred to the dependent partial, where its own
  context budget and regression boundary will be reviewed before activation.
- TP.3B reports lower Now provenance/support details may fall below the initial
  viewport but are vertically reachable. Judge against accepted scroll and
  hierarchy criteria, not an invented requirement that all content fit above
  the fold.

## Out of scope

- New composition, theme, feature, weather/provider/cache/persistence behavior,
  settings, or meteorological meaning; changing fetch behavior to improve a
  screenshot.
- TP.3D responsive/state matrix: compact, large-font, RTL, High contrast,
  sparse/load-state captures, and expanded Effects Off coverage; TalkBack
  service-level verification; release readiness.
- Any second correction pass, unapproved redesign, broad polish, or correction
  based only on subjective taste.
- Unrelated known Rain glyph gaps unless an approved acceptance criterion
  demonstrates a TP.3C blocker. Such work requires its own authorized plan.
- Claiming TP.3D or overall TP.3 completion based on TP.3C evidence alone.
