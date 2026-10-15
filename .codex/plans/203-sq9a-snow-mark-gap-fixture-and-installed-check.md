# Plan 203 — Add deterministic Snow mark-gap fixture and installed check

Status: Completed
Cycle ID: 203-sq9a-snow-mark-gap-fixture-and-installed-check
Roadmap item: SQ.9A-SNOW
Created: 2026-10-15

## Objective

Add a named, deterministic, debug-only Snow review scenario and use the real
installed Glass Full path to verify the approved D29 Snow no-mark gap. The
visible condition text must continue to say “Snow” while Now, Hourly, and Daily
draw no weather mark for that condition. This closes the specific installed
state missing from cycle 202; SQ.9A acceptance itself remains a separate review.

## Production boundary

Add a review-fixture scenario through the existing debug-only
`oxygen_review_scenario` selection path (`LaunchEffects.kt` and
`MainActivity.kt`, with a provider-neutral deterministic fixture helper in
`DemoWeatherRepository.kt`). The `snow_mark_gap` scenario is an additive copy
transformation of `DemoWeatherRepository.load(fixedAnchor)`: it changes only
the condition and meteorological values needed to make current, hourly, and
daily conditions coherently Snow, while retaining Demo Station identity, fixed
times, existing horizons/order, units, unaffected weather fields, and
development-fixture provenance. It must not mutate or replace the regular
fixture.

Preserve `MainActivity`'s existing selection precedence: a recognized debug
review scenario selects its scenario bundle; the sparse installed fixture is
applied only when no review scenario is selected. With no review scenario or
sparse request, the regular Demo Station bundle remains unchanged. The Snow
scenario must not change either default behavior or the named sparse fixture.

Do not change `WeatherCondition.SNOW`, the presentation condition label, the
D29 mark signatures, `ProductionWeatherMark` drawing, theme recipes, or release
weather behavior. The named scenario must remain debug-only and must not alter
the standard or sparse fixture paths.

## Functional invariants

- Keep `WeatherCondition.SNOW` as the canonical identity and show “Snow” as
  visible condition text wherever the condition is supplied.
- Preserve the current/hourly/daily entry order, time values, units, source,
  update/freshness labels, provenance type, and navigation/window behavior.
- In the Snow scenario, preserve Demo Station and the `development-fixture`
  source identity and existing model-estimate/forecast provenance types. Keep
  the explicit `Review fixture` development status. Change only condition and
  meteorological fields necessary for coherent Snow values; do not alter the
  ordinary Demo Station fixture.
- Keep the Snow no-mark mapping in D29: no weather glyph is drawn, no generic
  fallback is substituted, and the decoration is not exposed as an
  accessibility fact.
- Keep the development scenario deterministic and internally coherent:
  current/hourly temperature, apparent temperature, and dew point are
  subfreezing; daily lows/highs are below freezing; and cloud cover plus
  precipitation probability/amounts support the represented Snow conditions.
  Keep existing units and development-fixture source/status.
- With no launch extras, `DemoWeatherRepository.load(fixedAnchor)` and the
  selected standard UI state remain unchanged. The sparse launch extra still
  yields its same one-hour, no-current-condition, empty-daily fixture when no
  review scenario is selected. Missing conditions continue to display the
  existing unavailable text and no mark.
- Details remains free of weather marks under its existing contract.

## Implementation steps

1. Reuse `ReviewScenario` and `selectReviewScenario`; add the key
   `snow_mark_gap` without adding a production setting, preference, network
   path, or visible user control. Retain `MainActivity`'s precedence in which
   review-scenario selection suppresses the sparse-fixture transform.
2. Add a deterministic copy-transform for the fixed-anchor Demo Station
   bundle. Set Snow identity on current conditions and every supplied hourly
   and daily entry. Set subfreezing current/hourly temperature, apparent
   temperature, and dew point; set daily low/high values below freezing; and
   set supporting cloud cover and nonzero precipitation probability/amounts.
   Preserve timestamps, list lengths/order, location, units, non-weather
   metadata, and provenance. Keep source identity `development-fixture` and
   model-estimate /
   forecast data types. Leave the regular fixture function and sparse
   transformation unchanged.
3. Add focused unit coverage for debug-only scenario selection (including
   release rejection), deterministic Snow transformation, and visible “Snow”
   mapping. Assert the transform leaves its input/base Demo Station fixture
   unchanged. Reuse `LaunchEffectsTest.sparseFixtureOptionIsDebugOnly` and
   `ProductionHomeSparseCompositionTest.sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries`
   as the sparse-path regression checks. Reuse the existing
   `ProductionWeatherMarkTest` Snow no-mark assertions and add a focused
   connected production-path test only if existing tests do not cover the
   selected scenario's visible condition text and no-mark composition. Keep
   the D29 signature/drawing unchanged.
4. Run the focused host unit tests and the named connected tests on Pixel 3
   XL, plus the debug build. Install the build on Pixel 3 XL, launch the
   scenario via the debug review-scenario extra, select Glass / Standard
   contrast / Full, and capture Now, Hourly, and Daily at
   compact 360×640 dp, font scale 1.0, LTR. Inspect each condition-mark area
   beside the visible “Snow” text. Capture relevant hierarchies and include
   Details as a mark-free control if the installed state is practical.
5. Record fixture values and selected scenario, exact device/build/config,
   screenshot/hierarchy paths, focused test output, and hashes. Run workflow
   validation and `git diff --check`; inspect the final diff.

## Roadmap dependency and context bound

- This is the next eligible `SQ.9A-SNOW` outcome in
  `docs/UI_CONTEXT_ROADMAP.md`, after cycle 202's four-page review. It resolves
  that review's unverified Snow cell using the already approved D29 no-mark
  rule. SQ.9A acceptance remains a subsequent separate review, and SQ.10 stays
  downstream of that acceptance.
- The change is limited to one launch scenario, a copy-transform of one
  existing deterministic fixture, focused selection/data/render checks, and
  three installed page captures. It fits one cycle; keep implementation and
  evidence review within the UI workflow's 55% context threshold, targeting
  35–45%. If actual work exceeds the threshold, checkpoint before continuing.

## Acceptance criteria

- The `snow_mark_gap` scenario is deterministic and selectable only in debug
  builds through the named review path; release builds ignore the request.
- Current, hourly, and daily entries preserve `WeatherCondition.SNOW` and
  present the text “Snow”; source/provenance and chronological order remain
  visible and correct.
- Current installed Glass Full screenshots show the Snow condition text and
  no visible weather mark on Now, Hourly, or Daily. No fallback mark appears.
  The screenshot review pairs with the focused D29 mapping/render test; a
  semantics hierarchy alone is not accepted as proof of visual omission.
- Default Demo Station output and sparse-fixture selection/rendering remain
  unchanged; existing missing-condition text and mark omission remain correct.
- Focused checks pass on Pixel 3 XL, visual evidence is retained, workflow
  validation passes, and `git diff --check` is clean.
- The Snow cell is then available for a separately bounded SQ.9A acceptance
  review. This cycle does not itself mark SQ.9A accepted or unblock SQ.10.

## Verification and evidence

Evidence root:
`.codex/test-artifacts/203-sq9a-snow-mark-gap-fixture-and-installed-check/`.

- Run only named focused tests:
  `LaunchEffectsTest.reviewScenariosAreNamedAndDebugOnly`,
  `LaunchEffectsTest.sparseFixtureOptionIsDebugOnly`,
  `DemoWeatherRepositorySnowReviewFixtureTest.snowScenarioIsDeterministicAndLeavesBaseFixtureUntouched`,
  `ProductionHomeSparseCompositionTest.sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries`,
  existing `ProductionWeatherMarkTest.d29GridKeepsVisibleConditionTextAndCapturesThemesGapsAndModes`, and
  `ProductionSnowMarkGapReviewScenarioTest.snowReviewScenarioKeepsTextAndOmitsGlassFullMarks` for the
  selected scenario's production-path condition text and mark omission. Add the
  last focused test if no current test covers that path. Run these filters
  only; do not run a full unit or connected suite in this slice. Unit command:
  `./gradlew :app:testDebugUnitTest --tests com.oxygen.weather.LaunchEffectsTest.reviewScenariosAreNamedAndDebugOnly --tests com.oxygen.weather.LaunchEffectsTest.sparseFixtureOptionIsDebugOnly --tests com.oxygen.weather.data.DemoWeatherRepositorySnowReviewFixtureTest.snowScenarioIsDeterministicAndLeavesBaseFixtureUntouched`.
  Connected command:
  `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionHomeSparseCompositionTest#sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries,com.oxygen.weather.ui.themeengine.components.ProductionWeatherMarkTest#d29GridKeepsVisibleConditionTextAndCapturesThemesGapsAndModes,com.oxygen.weather.ui.ProductionSnowMarkGapReviewScenarioTest#snowReviewScenarioKeepsTextAndOmitsGlassFullMarks`.
- Debug build and installed Pixel 3 XL captures: Glass Full, Standard,
  360×640 dp, font 1.0, LTR, Now / Hourly / Daily. Use 48dp target guidance,
  readable facts, and accessibility semantics; no TalkBack service claim.
- Font scale 1.3 and RTL are not part of this single-state condition-mark
  omission check; no layout, reading order, or navigation behavior changes.
  Record them as unverified for this slice. Effects are explicitly Glass Full
  for the installed visual objective; Effects None completeness is unchanged
  and is not being reaccepted here.
- Before device use set screen timeout to 0/disabled and confirm Developer
  Options Stay Awake. Record model, API, serial, build hash, viewport, font,
  locale/direction, fixture key, theme/effects/contrast, units, and motion
  settings.
- Retain `device-config.md`, `fixture-definition.md`,
  `installed-observations.md`, paired PNG/XML captures, focused test/build
  output, `workflow.txt`, `diff-check.txt`, `sha256sums.txt`, and an
  `evidence-index.md` distinguishing installed evidence from unit/test
  evidence.
- Do not run `python scripts/dev.py check`; full suites belong in a separate
  verification session.

## Risks and assumptions

- Cycle 202 established that current and forecast Snow conditions are absent
  from the existing installed fixtures and that the installed omission cell
  blocks SQ.9A acceptance. The owner selected a separate Snow fixture and
  installed check on 2026-10-15. The routine design choice is to adapt the
  existing review-scenario path by copying the fixed-anchor Demo Station data;
  preserve the regular fixture and existing sparse launch path as controls.
- The existing D29 implementation and mapping tests specify Snow as a no-mark
  cell across theme mark styles. This slice verifies that behavior through the
  production Glass Full installed path; it does not reopen the approved mark
  design.
- Pixel 3 XL is required by repository policy. Its availability for this
  slice is an external execution risk; if unavailable, report installed
  verification as unmet rather than reclassifying the device requirement as a
  design ambiguity or substituting another device.
- A development fixture is not a provider observation or forecast. Ensure its
  source/status label stays explicit and do not add it to release data paths.
- If the installed app unexpectedly draws a mark for Snow or loses its
  condition label, stop this fixture-only slice and create a separate bounded
  correction plan; do not modify D29 behavior here.

## Out of scope

- Changing the approved D29 Snow no-mark mapping or adding a Snow asset/glyph.
- Live provider mappings, forecast acquisition, persistence/cache behavior,
  location, alerts, or network requests.
- Broad mark matrix, all themes/effects, RTL/High contrast/font-scale matrix,
  performance, Pixel Fold, full suites, or TalkBack service traversal.
- Repeating the full SQ.9A acceptance matrix, accepting SQ.9A, or starting
  dependent SQ.10–SQ.16 work.
