# Plan 158 partial B — Installed Effects Off/reduced-motion closure (R6.4-partial-B)

Status: Planned
Cycle ID: 158-reduced-motion-appearance-invariance-partial-B
Roadmap item: R6.4-partial-B
Created: 2026-10-08
Sequence: 3 of 3; after partial-A closes.
Evidence: .codex/test-artifacts/158-reduced-motion-appearance-invariance-partial-B/

## Objective and observable outcome

Review 20 installed Standard Home cells: five production themes × Now,
Hourly, Daily, and Details, all with Effects Off and system reduced motion.
Confirm that required facts, provenance, controls, named page identity, and
semantics remain present and usable, and that Effects Off is opaque and static.
Close the aggregate R6.4 exit only when this installed result and the two
predecessor results all satisfy their stated criteria.

## Authority and dependency

Use docs/SPECIFICATION.md, docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md,
docs/UI_DEVELOPMENT_WORKFLOW.md, docs/ROADMAP.md R6.4, and the preserved broad
plan 158-reduced-motion-appearance-invariance-original.md. Prerequisites:
Cycle 158 has closed with 30 deterministic semantic combinations and
partial-A has closed with a sensitive production no-refetch flow. Reuse the
Cycle 156 installed matrix/capture harness and Cycle 157 page-navigation
evidence as tooling/context; their captures do not count toward these 20 cells.

## Production boundary and visual objective

Exercise the normal installed MainActivity Standard Home path with the
documented Demo Station development fixture. Capture every theme/page cell at
360 × 640 dp, font scale 1.0, en-US, LTR, Standard contrast/layout, Metric
units, Effects Off, and system reduced motion enabled. Read back actual device
and app state per cell. Review legibility, reachability, clipping, page
identity, facts/provenance, labels, and opaque/static appearance. Correct only
a reproduced defect with an established first production owner in the Home
presentation/renderer boundary; no weather/provider/cache mutation.

## Functional invariants

- Preserve Now -> Hourly -> Daily -> Details, visible page names, outer pager
  ownership, static-tap and Back behavior, and Hourly/Daily window controls.
- Preserve values, units, chronology, missing states, provenance, valid/update
  time, freshness, official alert meaning, and derived/reference distinction.
- Keep important facts visible and semantically meaningful. Decorative marks
  cannot carry required meaning. Controls remain reachable and meet applicable
  48dp guidance.
- Effects Off remains opaque, static, and complete; reduced motion removes no
  required fact or control. Appearance changes do not refetch weather, as
  established by partial-A and rechecked if a correction touches that owner.

## Implementation steps

1. Confirm the API 37 oxygen_starter emulator and normal installed app path.
   Adapt the existing Cycle 156 capture/readback/validator tooling to exact
   five-theme × four-page keys; avoid a new capture framework.
2. Build/install the normal app. Establish and read back 360 × 640 dp,
   font scale 1.0, en-US, LTR, Standard contrast/layout, Metric units,
   Effects Off, system reduced motion, and Demo Station fixture. Record actual
   physical/override display, activity/root bounds, device profile, APK SHA-256,
   locale, motion setting, selected appearance, fixture/location state, and
   page identity. A requested setting without observed readback does not
   qualify a cell.
3. Capture one PNG and matching UI hierarchy per theme/page cell. Record
   artifact paths and hashes in a 20-key manifest; validate inventory and
   integrity. Capture scroll evidence when lower-priority content requires it.
4. Review every cell against the typed/visible expectations handed off by the
   prior portions: page name, weather facts and provenance, controls,
   unavailable states, semantic labels, clipping, opacity, and static
   appearance. Record a disposition for each cell and a contact sheet.
5. Correct only a reproduced defect with a proven production owner, add a
   focused regression assertion, and repeat affected tests and captures.
   Complete broader checks and inspect the final diff.

## Acceptance criteria

- Exactly 20 valid installed screenshot/hierarchy pairs have matching theme,
  page, profile, fixture, motion, and appearance readbacks, APK identity,
  hashes, and reviewed dispositions.
- No required fact, control, page identity, provenance, or meaningful semantic
  label is lost, clipped, or unreachable at the specified profile. Effects Off
  is opaque and static under system reduced motion.
- Every correction has a focused regression check and affected cells are
  recaptured. If emulator/fixture/readback/capture evidence cannot be made
  dependable, preserve partial evidence and close R6.4 blocked with the exact
  failed boundary.
- The R6.4 aggregate closure cites Cycle 158's 30-case result, partial-A's
  sensitive transport-counter result, and this 20-cell result. Do not upgrade
  either predecessor's unverified boundary into a pass.

## Verification and evidence

Run the exact installed capture and validator commands, review all 20 cells,
then python scripts/dev.py contract, python scripts/dev.py check, python
scripts/dev.py workflow, and git diff --check; inspect the final diff. Retain
APK digest, profile readbacks, 20 screenshot/hierarchy pairs and hashes,
manifest, contact sheet, dispositions, focused regression results if needed,
and verification.md with exact commands/results and unverified boundaries.
Compilation or Compose previews alone do not satisfy this visual objective.

## Risks and assumptions

- The known emulator profile and Cycle 156 tooling are starting points, not
  proof that the current installed app resolves the requested configuration.
- Reduced-motion and Effects Off must be read back; a setting command alone
  cannot qualify a capture.
- A visual defect may affect more than one theme/page. Recapture every cell
  using the corrected owner and rerun partial-A checks if the correction
  touches appearance application or fetch behavior.

## Context audit

Owner-directed split on 2026-10-08. Conservative execution estimate:
25,000–45,000 combined input/generated tokens, including device setup,
capture tooling, per-cell review, corrections, checks, and evidence. Runtime
context-window size is unconfirmed; no percentage or 65% PASS is claimed.
Confirm before activation.

## Out of scope

- Reimplementing the snapshot contract or transport counters from the prior
  portions, except a focused recheck when a correction touches their owners.
- Simple layout and Settings (R6.4A), RTL chronology (closed R6.3),
  TalkBack service review (R6.5), new themes/effects, live provider behavior,
  and unrelated redesign.
