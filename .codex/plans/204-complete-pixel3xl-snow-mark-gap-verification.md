# Plan 204 — Complete Pixel 3 XL Snow mark-gap verification

Status: Completed
Cycle ID: 204-complete-pixel3xl-snow-mark-gap-verification
Roadmap item: SQ.9A-SNOW-R
Created: 2026-10-15

## Objective

Finish the Pixel 3 XL connected and installed verification left incomplete by
cycle 203. Use the existing debug-only `snow_mark_gap` scenario to confirm that
Glass Full shows visible “Snow” condition text and draws no weather mark on
Now, Hourly, and Daily. This cycle records verification only and does not
accept SQ.9A itself.

## Production boundary

Verification of cycle 203's existing implementation, focused test, and debug
APK through the installed app. Do not edit production or test sources. If a
focused test or installed result exposes a concrete implementation defect,
record the evidence and stop; create a new bounded correction plan before any
source change.

The source/build identity is cycle 203's debug APK:
`app/build/outputs/apk/debug/app-debug.apk`, SHA-256
`238aeba7325a4022162aee3245fedf660e20132025c4736aa1f1304df6d8f001`.
Confirm that the artifact still matches this hash before installation; if it
does not, stop and record the mismatch rather than silently rebuilding a
different candidate.

## Functional invariants

- Keep `WeatherCondition.SNOW` and visible “Snow” text on Now, Hourly, and
  Daily; the D29 Snow mapping remains mark-free with no fallback glyph.
- Preserve the standard and sparse fixture paths, source/provenance, valid and
  update times, units, forecast chronology, unavailable behavior, and release
  behavior.
- Keep decorative marks out of accessibility facts. Details remains mark-free
  under its existing contract.
- Do not change weather requests, cache behavior, navigation, or the cycle 203
  debug scenario implementation.

## Implementation steps

1. Inspect cycle 203's plan, history, fixture definition, and evidence index;
   verify the existing APK hash and focused test/source identity.
2. Confirm Pixel 3 XL `crosshatch`, serial `8AKY0M32X`, is connected. Before
   testing, disable screen timeout and confirm Developer Options **Stay Awake**
   is selected. Record device/API and current configuration in this cycle's
   evidence.
3. Run the named `ProductionSnowMarkGapReviewScenarioTest` connected test on
   Pixel 3 XL. Do not run a full connected or unit suite.
4. Install the verified cycle 203 APK; launch MainActivity with
   `oxygen_review_scenario=snow_mark_gap`; set Glass / Standard contrast / Full
   and capture Now, Hourly, and Daily at 360×640 dp, font scale 1.0, en-US/LTR.
   Pair every screenshot with its hierarchy and inspect the condition text and
   mark area. Capture Details only if practical as a mark-free control.
5. Record commands, results, hashes, installed observations, and any unmet
   condition. Run workflow validation and `git diff --check`; inspect the final
   documentation/evidence changes.

## Acceptance criteria

- The cycle 203 APK hash matches the recorded candidate and the named
  `ProductionSnowMarkGapReviewScenarioTest` passes on Pixel 3 XL.
- Installed Glass Full captures for Now, Hourly, and Daily visibly show
  “Snow” and no Snow or fallback mark. Hierarchies are retained alongside the
  screenshots; the screenshot review, not hierarchy alone, establishes the
  visual omission.
- Evidence identifies device model/API/serial, build hash, viewport, font,
  locale/direction, fixture key, theme/effects/contrast, and relevant device
  settings.
- The existing source implementation remains unchanged. Workflow validation
  and `git diff --check` pass.
- If the device is unavailable, the APK identity differs, the connected test
  fails, or any page does not meet the visual condition, record the exact
  blocker and stop. Do not claim SQ.9A-SNOW-R complete and do not proceed to
  SQ.9A acceptance or SQ.10.

## Verification and evidence

Evidence root:
`.codex/test-artifacts/204-complete-pixel3xl-snow-mark-gap-verification/`.

- Run only
  `ProductionSnowMarkGapReviewScenarioTest.snowReviewScenarioKeepsTextAndOmitsGlassFullMarks`
  using the Pixel 3 XL connected-test path.
- Install and exercise the already-built cycle 203 debug APK. Capture Now,
  Hourly, and Daily in Glass / Full / Standard contrast, 360×640 dp, font 1.0,
  en-US/LTR. Save paired PNG/XML captures and a written visual inspection.
- Recheck cycle 203 fixture data and the test/build provenance read-only; do
  not rerun broad suites. Record the focused connected-test output, APK hash,
  device configuration, install/launch commands, workflow output, diff check,
  and evidence index.
- Set screen timeout to 0/disabled and verify Stay Awake before using the
  device. Record restoration of any temporary display overrides afterward.
- Font scale 1.3, RTL, TalkBack service traversal, and Details capture are not
  required for this condition-mark verification; report them as unverified if
  not captured. No full suite is planned in this slice.

## Risks and assumptions

- Cycle 203 reports that the Pixel 3 XL reconnected after it closed, but its
  availability must be checked again. Device loss is a blocker to the planned
  installed exit, not a reason to substitute another device.
- The existing APK may have been replaced since cycle 203. Its SHA-256 must
  match the archived identity; a mismatch requires stopping for a new review
  of candidate provenance.
- The cycle 203 test and APK are expected to represent the same source tree.
  Inspect recorded source/build evidence and report any ambiguity rather than
  silently changing sources or rebuilding.
- Fixture content is development-only and must remain labeled as such; it is
  not an observation or provider forecast.

## Out of scope

- Any production or test source edit, new fixture, build identity, or D29 mark
  mapping change.
- Adding a Snow glyph or fallback mark; changing provider, forecast, cache,
  alert, location, navigation, or release behavior.
- Repeating the full SQ.9A matrix, accepting SQ.9A, or starting SQ.10–SQ.16.
- Pixel 5/Fold substitution, full unit/connected suites, performance,
  large-font/RTL acceptance, and TalkBack service traversal.
