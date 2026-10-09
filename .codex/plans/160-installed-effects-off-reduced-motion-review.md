# Plan 160 — Installed Effects Off and reduced-motion review (R6.4-partial-B)

Status: Completed
Cycle ID: 160-installed-effects-off-reduced-motion-review
Roadmap item: R6.4-partial-B
Created: 2026-10-09
Sequence: final portion of R6.4; after Cycles 158 and 159.
Evidence: `.codex/test-artifacts/160-installed-effects-off-reduced-motion-review/`

## Objective and observable outcome

Review the actual installed Standard Home path across exactly 20 cells: five
production themes × Now, Hourly, Daily, and Details, with Effects Off and
system reduced motion enabled. Determine whether each page remains complete,
readable, reachable, and semantically understandable, and whether Effects Off
is opaque and static. Produce a verified capture manifest, screenshot and
hierarchy pairs, contact sheet, and evidence-linked disposition for every
cell. This supplies the installed evidence for R6.4; aggregate closure is
permitted only if Cycles 158 and 159 also satisfy their documented exits.

This is an invariance review, not a visual redesign: judge each theme using
the adopted presentation contract and typed/visible expectations, and record
defects without normalizing intentional theme differences.

## Authority and dependencies

Follow `docs/SPECIFICATION.md`,
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, `docs/UI_DEVELOPMENT_WORKFLOW.md`,
and `docs/ROADMAP.md` R6.4. This plan's predecessor handoff is
`.codex/plans/158-reduced-motion-appearance-invariance-partial-B.md`.

Cycle 158 established the typed appearance-independent snapshot and 30
resolver combinations. Cycle 159 exercised production appearance controls
with sensitive forecast and alert transport counters; its API 37 result and
limitations are in
`.codex/history/2026-10-09-159-reduced-motion-appearance-flow.md` and
`.codex/test-artifacts/159-reduced-motion-appearance-flow/verification.md`.
The 20 installed cells remain unverified. Cycle 156's 14-cell installed
capture, readback, validator, and review process is the harness precedent;
its artifacts do not count toward this cycle.

Before aggregate R6.4 closure, confirm the exact Cycle 158 and 159 history
records and cite their results and limitations. This cycle cannot upgrade a
predecessor's unverified criterion.

## Production boundary

Verification boundary: the normally installed `MainActivity` Standard Home
rendering path, theme/effects preferences, system reduced-motion state, and
adapted Cycle 156 capture/readback/validation harness. Test/evidence tooling
may be added under this cycle's evidence directory. Production edits are
allowed only for a reproduced issue whose first owner is demonstrated to be
in the Home presentation or renderer boundary. Do not alter weather,
provider, cache, alert, or fixture behavior to improve captures. If no
qualifying defect is found, this is an evidence-only cycle.

### Defect triage and first-owner decision

For every suspected defect, record the observed cell and evidence, the
expected contract, and the mismatch. First decide whether the observation is
a real loss of required content/behavior or a mismatch with the contract, as
opposed to an intentional theme presentation difference. Then trace the
first responsible production boundary (canonical/presentation model,
appearance resolution, or Compose renderer) using the smallest relevant
source and semantics evidence. Do not edit production until the mismatch and
first owner are both supported by evidence. If ownership remains unclear,
stop the correction path, retain the review evidence, and report that exact
boundary unresolved; do not guess or expand scope. Any correction must be the
smallest one at that owner, include a focused regression assertion, and
trigger recapture of every affected cell.

## Visual objective and installed profile

For each cell, establish that the named page and required weather facts,
source/provenance and valid/update/freshness details, controls, unavailable
states, and meaningful semantics remain present and usable. Check clipping,
overlap, visible control reachability, applicable 48dp target guidance,
chronology, opacity, and static Effects Off output. Decoration must not carry
required weather meaning.

Use the installed app at 360 × 640 dp, font scale 1.0, `en-US`, LTR, Standard
contrast and layout, Metric units, Effects Off, system reduced motion enabled,
and the Demo Station development fixture. Use each of the five production
themes and all four Standard Home pages. Observe and record the actual
viewport/root/window bounds, density, locale/direction, effective reduced
motion, selected theme/contrast/layout/units/effects, fixture/location, page,
and APK digest. A requested setting without observed readback does not
qualify a cell.

Large-font scale 1.3, RTL, Simple layout, other effects levels, Settings, and
TalkBack service traversal are outside this matrix; existing roadmap slices
own those conditions. Do not infer their acceptance from these captures.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page names, the outer
  pager as sole horizontal-swipe owner, static-tap and Back behavior, and
  visible Hourly/Daily window controls.
- Preserve canonical values, selected Metric units, chronology, unavailable
  states, provenance, valid/update times, freshness, official-alert meaning,
  and separation of derived/reference information.
- Keep required facts visible and semantically meaningful. Decorative marks
  cannot carry required meaning. Controls remain reachable and meet
  applicable 48dp guidance.
- Effects Off remains opaque, static, and complete. System reduced motion
  removes no required fact or control.
- Appearance changes do not refetch weather. Cycle 159 establishes this
  invariant; perform a focused recheck only if a correction touches its
  owner.

## Implementation steps

1. Confirm the API 37 `oxygen_starter` emulator and normal installed app
   path. Inspect the Cycle 156 capture/readback and validation scripts and
   adapt them into this cycle's evidence directory with exact five-theme ×
   four-page keys. Do not introduce a separate capture framework. Ensure the
   harness can establish each theme through production Appearance controls,
   navigate through the normal Home path, and read back the effective state
   and page for each cell.
2. Build and install the current app. Establish and read back the profile in
   the Visual objective section. Record physical/override display and density,
   activity/root and app-window bounds, system insets, device/API, APK
   SHA-256, locale/direction, reduced-motion setting, selected theme/effects,
   contrast/layout/units, fixture/location state, and page identity. Record
   both requested and observed values.
3. For each theme/page cell, capture one PNG and matching UI hierarchy after
   state readback. Record file paths and SHA-256 hashes in an exact 20-key
   manifest. Capture additional scroll-state PNG/hierarchy pairs when
   required content or controls are below the first viewport; include those
   paths and hashes in the relevant disposition. The validator must reject
   missing/duplicate/unexpected keys, missing/mismatched files or hashes,
   unqualified readbacks, and absent dispositions.
4. Build a contact sheet and review every cell against the visual objective,
   functional invariants, Cycle 158 typed expectations, and Cycle 159
   production-flow evidence. Record required facts and provenance, controls,
   unavailable states, semantic labels, bounds/reachability, clipping,
   opacity/static state, scroll/action evidence, disposition, and rationale.
   Apply the defect triage and first-owner decision above to each suspected
   issue.
5. If a qualifying defect is proven, make only the smallest correction
   within this plan's boundary; add a focused regression assertion, rerun
   relevant checks, and recapture every affected theme/page cell. If the
   correction touches appearance application or fetch behavior, rerun the
   relevant Cycle 159 checks and compare exact forecast/alert transport and
   cache counts. If reliable ownership cannot be established, do not edit;
   preserve results and identify the unresolved boundary.
6. Run the focused and broader verification below. Inspect the final diff
   and record exact commands/results plus any unverified boundary in
   `verification.md`.

## Acceptance criteria

- Exactly 20 valid screenshot/hierarchy pairs cover all five themes and four
  pages. Each pair has matching theme, page, profile, fixture, motion,
  appearance readbacks, APK identity, and integrity hashes, and has a
  reviewed disposition.
- At the specified profile, no required fact, control, page identity,
  provenance, or meaningful semantic label is clipped, lost, or unreachable.
  Effects Off is opaque and static with system reduced motion enabled.
- Defect claims include observed evidence, expected contract, mismatch, and
  demonstrated first owner before any production edit. Every correction has
  a focused regression check and all affected cells are recaptured.
- If emulator, fixture, effective-setting readback, hierarchy, or capture
  evidence is unreliable, preserve partial evidence and report the exact
  unmet boundary. Do not claim this portion passed or close aggregate R6.4.
- Aggregate R6.4 closure cites Cycle 158's 30-case result, Cycle 159's
  sensitive transport-counter result, and this 20-cell review. Unverified
  predecessor criteria remain unverified.

## Verification and evidence

From the repository root, retain exact output for these focused commands
(the two cycle scripts are adapted from Cycle 156 as step 1):

```sh
python .codex/test-artifacts/160-installed-effects-off-reduced-motion-review/capture_home_matrix.py
python .codex/test-artifacts/160-installed-effects-off-reduced-motion-review/validate_home_matrix.py --self-test
python .codex/test-artifacts/160-installed-effects-off-reduced-motion-review/validate_home_matrix.py
```

Review all 20 PNG/hierarchy pairs and the contact sheet manually; record the
reviewer/date and cell-by-cell dispositions in `dispositions.json`. If
production code changes, run the narrowest relevant existing JVM or
instrumentation regression test and record its exact Gradle task/command;
rerun capture/validation for every affected cell. If appearance/fetch owner
changes, also rerun the relevant Cycle 159 transport-counter check.

After the installed matrix and any correction, run:

```sh
python scripts/dev.py contract
python scripts/dev.py check
python scripts/dev.py workflow
git diff --check
```

Inspect the final diff. Preserve under
`.codex/test-artifacts/160-installed-effects-off-reduced-motion-review/`:
APK digest; device/profile and per-cell setting readbacks; 20 PNG/hierarchy
pairs and hashes; any scroll-state pairs; manifest; validator self-test and
run output; contact sheet; evidence-linked dispositions; focused regression
results if applicable; and `verification.md` with exact commands/results and
unverified boundaries. Compilation or Compose previews alone do not satisfy
the visual objective.

## Context and workload

The predecessor's conservative estimate is 25,000–45,000 combined execution
tokens for device setup, harness adaptation, capture, review, possible
correction, checks, and evidence. Installed review may exceed that range if
state readbacks fail or corrections span several cells. This is a workload
estimate, not a confirmed runtime context-window measurement or a context
audit pass. Before activation, ensure the execution environment can retain
the plan, needed source/evidence excerpts, and review results; if not, split
the work into ordered evidence-only and correction follow-up plans without
weakening this acceptance boundary.

## Risks and assumptions

- The known emulator and Cycle 156 tooling are starting points; current
  configuration and app state must be read back for each cell.
- Effects Off and reduced motion qualify only when their effective states
  are observed, not merely requested.
- The contact sheet aids comparison but does not replace per-cell hierarchy,
  semantics, and reachability review.
- A correction may affect multiple themes/pages. Every affected cell must be
  recaptured; appearance/fetch-owner changes also require the focused Cycle
  159 invariant recheck.
- Aggregate R6.4 remains dependent on exact predecessor acceptance and
  limitations, not just successful captures in this cycle.

## Out of scope

- Reimplementing Cycle 158's typed snapshot/resolver matrix or Cycle 159's
  transport counters, except focused regression/rechecks when a correction
  touches their owner.
- Simple layout and Settings (R6.4A), RTL chronology (R6.3), TalkBack service
  review (R6.5), large-font matrix, new themes/effects, live-provider
  behavior, unrelated visual redesign, or release-gate work.
- Activating this cycle or performing implementation work during plan
  review.
  review.
