# Plan 092 — TP.3C installed baseline comparison

Status: Completed
Cycle ID: 092-tp3c-baseline-installed-visual-comparison
Roadmap item: TP.3C
Created: 2026-09-30
Evidence: .codex/test-artifacts/092-tp3c-baseline-installed-visual-comparison/

## Objective

Compare all twenty installed baseline theme/page cases with the exact approved
references and deliver a measured, source-linked finding and disposition for
each case. Produce one bounded correction handoff for the dependent
`TP.3C-partial-A` cycle. This first implementation is an evidence and review
slice; it does not change production UI or claim TP.3C visual acceptance.

The combined comparison/correction draft exceeded the roadmap's approximate
45% context-window rule. This split keeps the twenty-case evidence review here
and moves all production correction, recapture, and final acceptance work to
`.codex/plans/093-tp3c-baseline-visual-correction.md`. Reassess context
pressure during execution; if even the review cannot be completed within a
bounded context, retain classified partial evidence and stop without claiming
the comparison exit.

## Authority and dependencies

- Product and presentation: `docs/SPECIFICATION.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; visual process:
  `docs/UI_DEVELOPMENT_WORKFLOW.md`; sequencing and exit:
  `docs/theme-pack-roadmap.md` TP.3A–TP.3D.
- Exact approved target: the r3 TP.1D packet/digest approved in
  `.codex/history/2026-09-25-057-tp1d-r3-owner-disposition.md`: packet
  `tp1d-proposed-r3-d28-d29-d31`, aggregate SHA-256
  `da0dce544cf4fb2d5263dcc6d24fbed9147ca96c52303e6d0395c29e8a57b8c5`.
  The immutable source is
  `.codex/test-artifacts/056-tp1d-packet-revision/packet/tp1d-proposed-r3-d28-d29-d31/`.
  Resolve each cell through that packet's `docs/theme-system/design-pack/`
  `renders/index.json`, primary SVG, `renders/fixture.json`,
  `INTEGRATED_PACK.md`, and `TP3_INSTALLED_COMPARISON.md`. Working-tree
  `INTEGRATED_PACK.md` and `TP3_INSTALLED_COMPARISON.md` differ from the
  approved packet in decision-status prose; use the packet for this review.
  The working-tree index, fixture, and twenty primary SVGs currently hash-match
  the packet. Source boards explain style but cannot override the adopted
  page/data/accessibility contract.
- TP.3A/B supplied normal-app compositions. Cycle 087 found their original
  captures incomparable; cycle 088 supplied the deterministic debug-only
  normal-app launch; cycle 090 completed the replacement installed matrix
  after cycle 089's emulator failure. The input is
  `.codex/test-artifacts/090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd/`.
  Its validator proved capture identity, not reference parity or text fit.
- Cycle 091 changed unit-aware presentation mapping after the captures.
  Verify default Metric fixture facts against the recorded baseline before
  visual judgment. A material mismatch, missing approved reference, or invalid
  capture blocks the affected comparison; do not compare unlike states or
  silently create a new target.
- `TP3_INSTALLED_COMPARISON.md` contains a compressed geometry summary;
  `INTEGRATED_PACK.md` and each indexed primary SVG provide the per-theme
  values. For example, the checklist's common-gutter wording differs from the
  integrated table's 12 dp Instrument/Terminal gutters. Record the discrepancy
  and use the exact per-theme table/SVG when measuring. A contradiction that
  cannot be resolved from the approved packet or higher product authority is
  an owner decision, not a guessed target.

## Production boundary and fixed state

No production-code change. Work is confined to reference/input validation,
twenty installed-versus-reference comparisons, cycle-local measurement and
review artifacts, and a correction handoff. Do not regenerate design
references, alter fixture or capture mode, or implement UI corrections here.

Baseline conditions: normal installed app through cycle 088's debug-only
deterministic entry; 393 × 852 logical dp, font scale 1.0, Locale.US/LTR,
Standard layout and contrast, window zero and vertical scroll start. The
offline illustrative fixture is anchored at `2026-09-23T09:00:00`
America/Chicago with LIVE/UNKNOWN, no refresh failure, and cache write
NOT_ATTEMPTED. Effective effects are Subtle for Atmospheric, Glass, and
Instrument and Off for Minimal OLED and Terminal. Record requested/effective
effects, actual screenshot pixels/density, system insets/content viewport,
device/API, package/version, and APK hash. Normalize measurements to actual
Android insets; do not force the reference's illustrative 24 dp inset.
Cycle 090 used emulator-5554, Android API 37, 393 × 852 screenshot pixels at
160 dpi, approximately 393 × 804 dp content after 24 dp top/bottom bars, and
APK SHA-256 `96ae62cd69a2a8386071c8b5975bab8c2e738f29ad5a1a549068a9623e3929cf`.
Verify these from the retained manifest rather than treating them as new
installed observations. Compact 360 × 640 dp, font scale 1.3/2.0, RTL, High
contrast, and cross-theme Effects Off are TP.3D conditions; this cycle reviews
the exact baseline state only.

## Functional invariants

- Now → Hourly → Daily → Details remains visibly named; the outer pager owns
  horizontal swipes, static taps are inert, and Back from a non-Now page moves
  to the previous global page.
- Hourly has six actual chronological entries per window, represented-date
  jump, and bounded Earlier/Later controls. Daily has five supplied rows per
  window and bounded controls. Baseline captures are window zero.
- Values, units, provenance, valid/update time, freshness, missing-data
  meaning, and Details' normalized/forecast-pattern/historical separation
  remain authoritative. Decoration does not carry required facts. Effects Off
  is opaque, static, and complete; applicable controls target at least 48 dp.

## Implementation steps

1. Verify the immutable packet's 117-file `SHA256SUMS.txt` and approved
   aggregate; hash-check the working index, fixture, and twenty primary SVGs
   against it. Validate cycle 090's `manifest.json` and `SHA256SUMS.txt`:
   exactly twenty unique case IDs, one APK/device/configuration, twenty starts
   and hierarchies, ten Daily/Details ends plus end hierarchies, row results,
   and interactions. Confirm cycle 091's compatibility-default Metric mapper
   output against the exact fixture facts (including displayed units, source,
   update, load state, dates, and Details groups), using focused presentation
   tests and recorded row facts. Do not infer visual parity from those tests.
   List any invalid/stale rows; continue independent valid rows if useful,
   but the comparison exit remains blocked until all twenty are valid.
2. Build a case inventory linking each `P-<theme>-<page>` to exact indexed SVG
   path/hash, installed screenshot path/hash, fixture, viewport/insets,
   settings, build/device metadata, and hierarchy/end/interaction evidence.
   Map the installed `minimal-oled` ID to the approved SVG/index
   `minimal_oled` slug explicitly; never infer a missing reference from that
   spelling difference.
3. Review all twenty pairs at full size, with end-scroll and hierarchy
   inspection for Daily/Details. For any other page whose content fit is
   uncertain, use available hierarchy/scroll evidence and mark what cannot be
   established. Measure inset-normalized gutter, width, body top, fixed
   component positions, control bounds, text bounds/wrapping, and scroll
   reachability; record supplied facts and qualitative hierarchy, surface,
   mark, and atmosphere findings. Use the same named landmark on both sides
   and write down both screen and inset-normalized coordinates; the cycle 090
   `heading_top_normalized_dp` is a heading measurement, not automatically the
   reference body top. Save crops/overlays for material deviations.
4. Apply `TP3_INSTALLED_COMPARISON.md`: fixed gutter/width within ±2 dp,
   fixed body-top/position within ±4 dp after inset normalization, and 48 dp
   minimum targets without negative tolerance. Content-driven heights have
   no fixed-height tolerance; all supplied text must fit without clipping or
   overlap. Do not demand SVG pixel or antialiasing identity. Explain
   qualitative differences with source-linked evidence.
5. Classify every row PASS, DEVIATION, BLOCKED, or UNVERIFIED with an exact
   criterion and evidence. PASS requires all assessed baseline criteria to
   hold; DEVIATION means valid comparable evidence demonstrates a failure;
   BLOCKED means an invalid/conflicting reference or capture prevents a fair
   comparison; UNVERIFIED means a criterion lacks sufficient evidence.
   Explicitly assess cycle 090's five Now rows:
   source, update, and load status must be visible in the initial baseline
   viewport per the approved Now contract, or remain a deviation. No
   production correction occurs in this cycle.
6. Consolidate all correctable, reference-supported findings into one
   prioritized correction handoff naming the affected UI owner and cases.
   Separate owner decisions, missing evidence, and out-of-bound changes.
   Record whether the dependent cycle may start. Inspect the evidence and
   final diff, then close this comparison portion only.

## Acceptance criteria

- All twenty cases have a traceable reference and installed identity/hash,
  condition metadata, measured review, hierarchy/scroll observations,
  initial disposition, and specific reason. A row result includes its page
  facts, landmark measurements and tolerances, target sizes, text-fit and
  reachability judgment, source/status visibility, qualitative visual notes,
  and links to any crop/overlay. The five Now failures and
  previously unverified text fit receive explicit review findings.
- A complete correction handoff identifies each in-bound deviation, affected
  case/component, supporting measurement/reference, and proposed acceptance
  check. It does not pre-authorize changing facts, navigation, or the approved
  target. If all twenty already pass, the handoff records that no edit is
  needed and the dependent cycle performs final evidence closure.
- This portion may close REVIEW COMPLETE when all twenty are validly
  classified with adequate evidence, and every deviation is either actionable
  within the dependent correction boundary or explicitly dispositioned by
  repository authority. A BLOCKED/UNVERIFIED row, missing/conflicting
  reference, invalid input, unresolvable product conflict, or unavailable
  required evidence closes this portion BLOCKED and stops the dependency
  chain. An out-of-bound correction requiring owner choice also blocks rather
  than silently authorizing cycle 093.
- REVIEW COMPLETE is not TP.3C PASS. Only the dependent correction/acceptance
  cycle may claim all-twenty visual acceptance and unblock TP.3D.

## Verification and evidence

Under `.codex/test-artifacts/092-tp3c-baseline-installed-visual-comparison/`
retain `input-inventory.md`, `comparison-matrix.md`, twenty
`P-<theme>-<page>-result.md` records, discrepancy crops/overlays,
`correction-handoff.md`, `commands.md`, and `review.md`. Link to immutable
cycle 090 images, hierarchies, and end captures; do not overwrite them. The
handoff must make the second implementation executable without redoing the
twenty initial comparisons.

`input-inventory.md` records the approved packet ID/digest, relevant packet
hashes, any working-document differences, the explicit `minimal-oled` slug
mapping, cycle 090 validator result, cycle 091 Metric audit, and capture
metadata. `comparison-matrix.md` uses one row per canonical installed case ID
with separate geometry, facts/status, text-fit, and qualitative dispositions.
`correction-handoff.md` groups findings by UI owner, priority, affected case,
reference/measurement, and exact acceptance check; it separately lists
blocked/owner-dependent findings. `review.md` states REVIEW COMPLETE or
BLOCKED and names what cycle 093 may inherit.

Run and record:

```sh
(cd .codex/test-artifacts/056-tp1d-packet-revision/packet/tp1d-proposed-r3-d28-d29-d31 && sha256sum -c SHA256SUMS.txt)
(cd .codex/test-artifacts/056-tp1d-packet-revision/packet/tp1d-proposed-r3-d28-d29-d31 && awk '$2 != "OWNER_GUIDE.md"' SHA256SUMS.txt | LC_ALL=C sort -k2 | sha256sum)
python .codex/test-artifacts/090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd/validate_evidence.py
./gradlew :app:testDebugUnitTest --tests com.oxygen.weather.presentation.HomePresentationTest
python scripts/dev.py test
python scripts/dev.py workflow
python scripts/dev.py contract
git diff --check
```

Compare the printed packet aggregate with the pinned digest above; verify
working index, fixture, and primary-SVG hashes against the packet manifest in
`input-inventory.md`. Run the remaining commands from the repository root.
The focused Home presentation test guards the cycle 091 Metric compatibility
claim; the broader `python scripts/dev.py test` catches related mapping drift.
Inspect the final diff and evidence inventory. The 090 validator's known five
Now deviations do not fail input identity; they remain review findings.
Record any unavailable review and the exact rows it leaves unverified. No
production build, new installed capture, or Compose test is claimed by this
evidence-only portion. `python scripts/dev.py check` is a repository-level
execution gate for code changes, so record it only if voluntarily run here;
its absence does not establish visual acceptance.

## Risks and assumptions

Execution finding (2026-09-30): cycle 091 changed default Metric presentation
strings after cycle 090's installed captures. The current normal-app capture
path still calls `HomePresentationMapper.map` with its Metric default, whose
focused tests expect `28 °C`, `19 °C`, and `Gusts 23 km/h · SW`; the retained
captures and approved fixture show `28°`, `19°`, and `Gusts 23 · SW`.
This is material fixture drift under the dependency rule above. Preserve any
visual observations as provisional, classify affected comparisons BLOCKED,
and close with that blocker if no within-bound evidence can resolve it.
Recapture or reference revision remains outside this evidence-only cycle.

- The approved r3 revision maps to twenty primary SVGs and the working copies
  currently hash-match. Reverify at execution; an actual mismatch requires an
  owner decision and blocks the affected row. No new approval is inferred.
- Cycle 090 used the saved lavapipe AVD. Existing images are review inputs;
  this plan does not rely on a new emulator session. Material fixture drift
  after cycle 091 requires a new bounded capture decision before parity
  judgment, not silent substitution.
- Twenty pairs are a bounded review only because capture/interaction evidence
  already exists. Work through five four-page theme groups, retaining each row
  immediately to disk. Avoid reopening implementation or full responsive-state
  matrices here. If review context pressure exceeds the bound, stop with
  explicit unverified rows rather than compressing visual judgments.

## Out of scope

- The single production correction pass, post-edit installed recapture, and
  TP.3C final PASS; these belong to `TP.3C-partial-A`.
- TP.3D compact, large-font, RTL, Effects Off, and sparse acceptance matrix;
  TalkBack service-level verification.
- Preference persistence, Settings, provider/cache/domain/presentation-mapper
  changes, new facts or alerts, broad redesign, and reference edits.
