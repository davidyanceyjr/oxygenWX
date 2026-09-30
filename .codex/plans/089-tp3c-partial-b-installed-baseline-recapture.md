# Plan 089 — TP.3C Partial B — installed baseline recapture

Status: Blocked  
Cycle ID: 089-tp3c-partial-b-installed-baseline-recapture  
Roadmap item: TP.3C (recovery prerequisite)  
Created: 2026-09-30

**Difficulty: 7/10.** This is a bounded twenty-case installed capture and
validation run using the deterministic debug mode completed in cycle 088. The
driver must select five themes and four pages, exercise controls, collect
hierarchy and scroll evidence, and independently validate the artifact set.
Target at most 35% of a fresh context window; stop before the repository's 45%
limit. If a valid matrix cannot be completed within this boundary, retain
partial evidence, classify each uncompleted or invalid row, record the exact
blocker, and stop without expanding into comparison or corrections.

## Objective and observable outcome

Using the deterministic capture mode implemented by cycle 088, capture and
validate exactly twenty TP.3 primary theme/page cases from one installed debug
APK and one device configuration. Deliver a condition-valid evidence set for
the later TP.3C comparison and correction slice. This cycle does not compare
the captures with references or claim TP.3C completion.

## Authority and dependencies

- Product and technical semantics: `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and `AGENTS.md`.
- Theme-pack order and boundaries: `docs/theme-pack-roadmap.md`, TP.3A–TP.3D.
- Deterministic setup and per-case contract:
  `docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md`.
- Exact typed fixture:
  `docs/theme-system/design-pack/renders/fixture.json`.
- Prerequisite passed: `.codex/history/2026-09-30-088-tp3c-partial-a-deterministic-baseline-recapture.md`.
  It records debug-only fixed fixture/load-state launch mode through the normal
  Home path, focused checks, installed smoke launches, and release exclusion.
- Earlier comparison cycle 087 found prior TP.3A/TP.3B captures incomparable;
  its findings and recovery sequence are in
  `.codex/history/2026-09-30-087-tp3c-baseline-installed-visual-comparison.md`.
- Visual evidence process: `docs/UI_DEVELOPMENT_WORKFLOW.md`.

Use the checklist's fixed anchor `2026-09-23T09:00:00`,
`America/Chicago`, `Locale.US`, its illustrative LIVE/UNKNOWN state, no refresh
failure, and cache write `NOT_ATTEMPTED`. The fixture remains offline
illustrative data, not a provider response.

## Production boundary

No production code, weather data, layout, theme, reference, or accepted visual
criterion changes are planned. Work is limited to cycle-local capture
automation, invocation/configuration of cycle 088's debug capture mode, and
evidence generation and validation. First verify the normal installed app and
capture mode still satisfy cycle 088's contract. If not, stop and record the
blocker; do not broaden the production boundary in this cycle.

Use one installed debug APK/build and one emulator/device configuration for all
twenty rows. Conditions: target 393 × 852 logical dp, font scale 1.0,
Locale.US/LTR, Standard layout and contrast. Effective effects: Atmospheric,
Glass, Instrument Subtle; Minimal OLED and Terminal Off. Do not force reference
insets: record actual pixel dimensions, density, system insets, and resulting
content viewport for each row. Record package/version, build/APK hash,
device/API, fixture/load-state identity, selected theme/page, and requested/
effective effects. Compare content geometry after normalizing to measured
insets. Record indexed geometry observations and the checklist's ±2 dp fixed
gutter/width and ±4 dp fixed-position tolerance for handoff; those measurements
do not constitute reference comparison or a parity disposition in this cycle.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page names, the outer
  pager as sole global horizontal-swipe owner, Back behavior, and inert static
  taps.
- Preserve Hourly's six actual chronological entries, date jump and bounded
  Earlier/Later controls; preserve Daily's five actual chronological entries
  and bounded controls.
- Preserve fixture facts, units, provenance, valid/update time, freshness,
  missing-data behavior, and separation of normalized, derived, and historical
  information. Do not fetch weather, write cache, or alter supplied values.
- Themes and effects remain presentation-only. Decorative marks/scenes cannot
  replace visible facts or semantic content. Effects Off stays opaque, static,
  and complete.
- Keep the normal Home route and release behavior intact; capture remains
  debug-only as established in cycle 088.

## Implementation steps

1. Read cycle 088's plan/history and capture-mode evidence, the TP.3 checklist,
   render index, and fixture. Confirm the capture mode is available through the
   normal Home route and release exclusion evidence remains valid. Confirm the
   checklist anchor, locale, fixture, and illustrative LIVE/UNKNOWN state. If
   this prerequisite fails, record the evidence and stop before capture.
2. Inspect installed build/device tooling and the existing visible theme/page
   selection path. Establish a cycle-local driver or documented manual
   procedure that invokes the normal app, selects all five themes and four
   pages, and verifies selected identity through hierarchy. Do not add a
   production selector or alter rendering. If the existing route cannot reach
   a required state, mark affected rows Blocked and stop without changing the
   production scope.
3. For each theme/page, exercise applicable controls before restoring the
   baseline: Hourly date choices Wed→0, Thu→3, Fri→7, Sat→11 and bounded
   Earlier/Later; Daily bounded Earlier/Later; global page navigation, Back,
   and inert static/background taps. Confirm no nested horizontal pager owns
   Hourly/Daily. Restore window zero and vertical scroll start before capture.
   Keep interaction observations in the row record and a complete interaction
   log.
4. Capture twenty start images under stable IDs `P-<theme>-<page>`, end-of-
   scroll images for Daily and Details, hierarchy/semantics dumps, interaction
   notes, and a result record for every row. Preserve logs and driver source in
   the cycle evidence root; never overwrite TP.3A, TP.3B, cycle 087, or cycle
   088 evidence.
5. Independently validate exact row IDs/count; one build, device, and
   configuration; fixture facts and chronology; source/update/status;
   theme/page identity; indexed effects; measured viewport/insets; geometry
   observations; expected files and SHA-256 hashes. Each result uses Pass,
   Deviation, Blocked, or Unverified and includes observed values and evidence
   paths. Do not silently normalize failed conditions or assign a parity
   disposition.
6. Run the focused capture/evidence validator (or document the manual
   validation procedure if no validator exists), followed by:

   ```sh
   python scripts/dev.py contract
   python scripts/dev.py check
   git diff --check
   ```

   Inspect the final diff and evidence inventory. Record exact commands and
   results, including unavailable verification. Stop after capture/validation;
   comparison and correction require their own planned slice.

## Acceptance criteria

- Exactly twenty `P-<theme>-<page>` baseline rows are captured from one APK
  and device configuration under the checklist conditions; scrollable pages
  also have end captures.
- Every row has a hierarchy dump, interaction/reachability observations,
  result record, and complete build/device/fixture/theme/page/effects/viewport
  metadata. The manifest records paths and SHA-256 hashes and validates
  against the checklist's expected facts.
- Every row records measured insets/content viewport and geometry observations
  (body top, content width/gutter, applicable control target, text fit, and
  scroll reachability) for the dependent comparison. If a measurement cannot
  be obtained, its reason is recorded and the field remains unverified.
- Hourly and Daily controls, restored baseline window/scroll state, Back, and
  static-tap behavior are exercised and recorded.
- The exact fixture/source/load-state semantics are confirmed with no fetch or
  cache write; no production rendering or weather meaning is changed.
- Focused verification, contract, repository check, diff check, and evidence
  inventory are recorded. Any inaccessible installed check or failed case has
  its cause and remains explicitly blocked/unverified.
- No reference comparison, parity disposition, correction, TP.3C/TP.3 closure,
  or TP.3D acceptance is claimed. This evidence is input to a separately
  planned TP.3C comparison/correction slice.

## Verification and evidence

Evidence root: `.codex/test-artifacts/089-tp3c-partial-b-installed-baseline-recapture/`.

- `commands.md`: exact install, launch, interaction, capture, validation, and
  repository-check commands/results.
- Twenty row records `P-<theme>-<page>-result.md`, matching
  `-start.png`, `-hierarchy.txt`, and `-end.png` where scrollable.
- A manifest with checklist conditions, fixture/load state, measured
  insets/content viewport, geometry observations, file paths, hashes, and
  device/build metadata; preserve interaction logs and driver source in the
  cycle evidence directory.
- `review.md`: independent matrix review, deviations/blockers, limitations,
  and handoff to the later TP.3C comparison slice.
- `final-checks.md`: evidence inventory and final `git diff --check` result.

## Risks and assumptions

- The cycle 088 debug capture mode fixes fixture/load state but may not expose
  theme/page automation. Verify the existing normal-app selector and
  interaction path first; unavailable states remain Blocked. A new production
  control or broader change needs a revised roadmap/plan and is not assumed
  approved.
- Emulator, SDK, instrumentation, or UI automation may be unavailable. Record
  exact capability and evidence, and do not substitute source inspection for
  installed captures.
- Real Android insets and pixel dimensions differ from logical dp targets;
  record measurements rather than forcing reference insets.
- Screenshot success alone does not establish expected values or hierarchy;
  validate each row against the checklist and fixture.

## Out of scope

- Comparing the twenty captures to SVG references, scoring visual parity, or
  deciding TP.3C pass/fail against composition criteria.
- Any visual correction, recapture after correction, reference/fixture/criteria
  changes, or normal-app production composition changes.
- TP.3D compact, large-font, RTL, sparse/missing-data, additional effects,
  contrast, or TalkBack/service-level matrix.
- Provider/network/cache/persistence work or weather-meaning changes.
- Closing TP.3C or TP.3, or claiming release readiness.
