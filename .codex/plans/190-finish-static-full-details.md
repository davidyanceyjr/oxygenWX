# Plan 190 — Finish static Full Details

Status: Blocked (closed)
Cycle ID: 190-finish-static-full-details
Roadmap item: SQ.8
Created: 2026-10-14

## Objective and observable outcome

Complete the installed Glass/Smoked Quartz **Static Full Details** composition on
Standard Home. Retain the existing ordered source/freshness, Conditions,
Forecast pattern, and Historical context information. Use the approved quiet
Details field and restrained grouping, with no more than one readable surface
per semantic group. Available and missing values remain explicit; derived and
historical metrics retain their labels and limitations. This is a Details-only
slice and does not accept Static Full across all four pages.

## Owner design decisions

- **Grouping (2026-10-14):** keep source/freshness as an open header, then use
  at most one quiet surface each for Conditions, Forecast pattern, and
  Historical context. Keep metrics open within each group; do not add
  per-metric cards.
- **Micro-etch (2026-10-14):** use a fine, more continuous integrated etch
  across the Details field. Maintain a text-safe region and readability with
  very low contrast; keep the low-contrast seam away from text.

## Authority and dependencies

- SQ.7 completed in cycle 189. Its history and installed evidence are
  `.codex/history/2026-10-14-189-finish-static-full-daily.md` and
  `.codex/test-artifacts/189-finish-static-full-daily/`.
- Cycle 184 records approval of the SQ.2 source packet. The Details recipe in
  `resources/themes/smoked-quartz/SOURCE.md` governs this slice; its SHA-256 is
  `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`.
- Audit the theme art references as design inputs, not assumed runtime files:
  `resources/themes/smoked-quartz/asset-pack-overview.png`,
  `asset-worksheet-v1.png`, and `backgrounds/details.svg`, together with the
  Details recipe and `ASSET_INVENTORY.md` / `ASSET_MANIFEST.md`. The approved
  Details direction is a native-drawing recipe; candidate SVG artwork is not
  approved for packaging. Details has no weather marks or quartz inclusion
  overlay. Record the applicable source elements, their native-rendering
  counterparts, and intentional omissions in the cycle asset audit.
- SQ.4's Details capture/XML and manifest under
  `.codex/test-artifacts/186-static-mineral-background-foundation/` are the
  same-fixture base-background reference. The existing background recipe is
  part of the baseline; SQ.8 may refine the Details-only Full treatment only
  within the approved quiet field and micro-etch recipe.
- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`, and SQ.8 in
  `docs/UI_CONTEXT_ROADMAP.md` govern product meaning, presentation, and scope.
- SQ.8 is eligible because SQ.7 is recorded complete. SQ.9 remains the later
  four-page static acceptance boundary.

## Production boundary

Change only the Glass + Full + Details presentation and focused coverage:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` — the
  `DetailsPage` Glass Full composition branch, if a page-local layout or
  grouping adjustment is needed. Preserve its typed inputs and group order.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`
  — a narrow opt-in Details group/surface treatment only if the existing
  components cannot express the approved composition without affecting other
  pages or themes.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionDetailsComponents.kt`
  — a narrow opt-in Details-only open presentation for source/freshness facts
  and surface/group adjustment for inspection groups. Preserve existing
  callers and defaults.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionForecastContext.kt`
  — add an opt-in open presentation for Details source/freshness context only;
  preserve the existing surfaced default for other callers, including Now.
- Focused production-path assertions in
  `app/src/androidTest/java/com/oxygen/weather/ui/ProductionHomeCompositionTest.kt`
  and/or `ProductionDailyDetailsCompositionTest.kt` only where current checks
  do not cover Glass Full grouping, exact supplied facts, order, and missing
  values.
- Evidence root: `.codex/test-artifacts/190-finish-static-full-details/`.

No presentation mapping, provider, forecast/alert request, cache, preference,
page-navigation, or non-Glass/non-Full behavior changes are in scope.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page identity, the outer
  Home pager as the sole horizontal-swipe owner, title/page chooser, Back, and
  all existing actions and semantics.
- Preserve source/provenance, valid/update times, freshness, status, units,
  provider-normalized measurements, derived forecast-pattern values, and
  historical/reference values exactly as supplied by presentation models.
  Keep semantic groups in their existing order and never imply that derived or
  historical values are current observations or official products.
- Missing values remain unavailable or omitted according to the existing
  presentation model. Do not synthesize values, labels, source context, or
  placeholder measurements.
- Visual changes do not refetch weather, change cache/request behavior, or
  alter forecasts, alerts, provenance, or accessibility meaning.
- Keep all important facts visible as text with equivalent semantics and
  preserve heading roles. Keep controls usable at 48dp where applicable.
- Full remains static in this slice. No inclusions, weather marks, seam-glint,
  other motion, or data-encoding decoration. High contrast remains opaque and
  readable; Effects None remains opaque, static, and complete; Subtle and all
  other themes/pages retain existing behavior.
- Compact and 1.3 font-scale layouts may scroll vertically; do not clip,
  ellipsize away, or shrink critical facts. RTL may mirror alignment and
  decoration but cannot reorder semantic groups or change their meaning.

## Implementation steps

1. Review the approved SQ.2 Details recipe, the applicable Smoked Quartz art
   sheet/reference images and Details art plate, the inventory and candidate
   manifest, the SQ.4 Details capture/XML and manifest, the current
   `DetailsPage`/inspection components, and existing production composition
   tests. In `baseline-review.md`, record an asset audit mapping the approved
   Details source (deep field, very subtle integrated micro-etch, quiet seam,
   faint grain, text-safe region) to the current native Compose rendering;
   identify intentionally omitted weather marks, inclusion overlay, and
   runtime SVG/bitmap packaging; and note any visual mismatch. Compare the
   installed baseline to the source references at actual size. Keep changes
   within the already accepted Details source and visual scope.
2. Make the smallest Glass + Full + Details-only composition change that
   expresses a deep quiet field and the selected continuous, fine integrated
   micro-etch while preserving a text-safe region and readability. Keep
   source/freshness as an open header in both the forecast-context and fallback
   source/status paths. Add opt-in Details presentation parameters as needed;
   leave existing callers at their current defaults. Use at most one
   restrained surface each for Conditions, Forecast pattern, and Historical
   context, with open metrics inside each group. Retain the distinctions and
   order. Keep all other theme/effect paths and the SQ.4 background behavior
   stable.
3. Add only missing focused production-path coverage for Glass Full group
   boundaries and order, visible supplied values/semantics, and an existing
   missing-field state. Reuse deterministic fixtures and do not change data or
   fixture meaning to improve a screenshot.
4. On the Pixel 3 XL, run the selected connected methods and install the real
   production Activity. Before device use, disable screen timeout and confirm
   Developer Options **Stay Awake**. Capture available and missing-field
   Details at 360×640dp, Standard contrast, font scale 1.0; review available
   Details at font scale 1.3 for scroll reachability and clipping. Record the
   actual device/API/viewport, fixture, app/build identity, selected theme,
   effects, contrast, font scale, locale/direction, and scroll position. If a
   state or exact viewport cannot be produced, record it and leave that
   criterion unclaimed.
5. Compare the Full capture with the SQ.4 Details baseline at actual size.
   Review group order, source/freshness, provenance distinctions, missing
   states, readability, micro-etch/text separation, page identity, and scroll
   reachability. Audit changed paths and run the selected tests, build,
   contract, workflow, applicable focused lint, and `git diff --check`.
   Preserve commands, results, captures, visual notes, and limitations under
   the cycle evidence root.

## Acceptance criteria

- Installed Glass Full Details presents the ordered source/freshness,
  Conditions, Forecast pattern, and Historical context groups using the
  approved quiet composition. Source/freshness remains an open header;
  Conditions, Forecast pattern, and Historical context each use at most one
  quiet readable surface, with metrics open inside each group. No metric gets
  its own card.
- Available and missing-field states preserve exact supplied values,
  unavailable behavior, provenance, units, and data-type distinctions. Derived
  and historical information remains explicitly labeled; no decoration
  communicates values or status.
- The Details background retains its quiet mineral field, fine and continuous
  but very low-contrast integrated micro-etch, and faint grain. Preserve a
  text-safe region and keep the seam away from text. Text remains legible and
  the decoration does not compete with content.
- The asset audit documents how the installed Details rendering follows the
  approved Smoked Quartz art direction and Details recipe, including the
  source-to-native-render mapping and intentional omissions. No unapproved
  candidate asset is packaged or introduced; no weather mark or inclusion
  overlay is added to Details.
- Installed compact captures show available and missing-field Details at
  360×640dp, Standard LTR, font scale 1.0. The available state is also reviewed
  at font scale 1.3; content can scroll without clipping critical facts or
  losing group order. Any unavailable required capture is documented and not
  counted as accepted.
- Focused production-path checks prove group order, visible supplied facts,
  heading/accessibility semantics, and missing-value behavior. Build,
  contract, workflow, applicable lint, and diff checks pass, or their exact
  failures and unverified boundaries are recorded.
- Non-Glass, non-Full, non-Details rendering and navigation/data behavior remain
  unchanged. This slice does not claim four-page Static Full acceptance.

## Verification and evidence

- On the Pixel 3 XL, run these method-level connected selectors:
  ```sh
  ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class='com.oxygen.weather.ui.ProductionDailyDetailsCompositionTest#detailsKeepSuppliedGroupAndMetricOrderUnderEveryTheme'
  ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class='com.oxygen.weather.ui.ProductionDailyDetailsCompositionTest#sparseDailyAndDetailsPreserveUnavailableSupportOmissionAndPartialStatus'
  ```
  The sparse method uses an explicit presentation fixture; use it for
  unavailable semantics, not as evidence of installed production mapping.
  Add one Glass Full Details method to `ProductionDailyDetailsCompositionTest`
  only if current assertions do not exercise that configuration and verify the
  production grouping/semantics. Prefer method-level selectors; if unsupported,
  use the narrowest verified class selector and record any additional methods
  that ran.
- Before each Pixel 3 XL test/installed check, set screen timeout to 0 or
  disabled and confirm **Stay Awake** is selected in Developer Options, per
  repository testing cadence.
- Run `python scripts/dev.py build`, `python scripts/dev.py contract`,
  `python scripts/dev.py workflow`, applicable focused lint for production
  Kotlin changes, and `git diff --check`. Do not run `python scripts/dev.py
  check`, the full unit suite, or the full connected suite; SQ.V owns its
  separately planned verification session.
- Capture installed PNG/XML pairs for Glass Full Standard LTR available Details
  at font scales 1.0 and 1.3, plus a deterministic missing-field Details state
  at font scale 1.0. Capture scroll-end state if content continues below the
  initial viewport. Record actual viewport, device/API, app identity, fixture,
  settings, locale/direction, scroll position, and capture hashes in a
  manifest. Compare the compact available capture with cycle 186's Details
  capture at actual size.
- Preserve `baseline-review.md`, focused logs, build/contract/workflow/lint/diff
  results, capture manifest, PNG/XML pairs, visual review notes, and changed-
  path audit under `.codex/test-artifacts/190-finish-static-full-details/`.
  Record failed/unrun checks separately from passed checks.

## Risks and assumptions

- `DetailsPage` currently composes source/freshness and status context followed
  by presentation-provided nonempty groups. The current all-theme ordering test
  establishes semantic order, but may not assert the Glass Full grouping
  appearance. Add only the assertion needed for this gap.
- The approved Details recipe does not prescribe pixel-exact values. The owner
  selected separate quiet group surfaces and a continuous fine micro-etch for
  this slice; keep adjustments local to Glass Full Details and judge the
  installed result against the SQ.4 baseline and readability constraints.
- The theme chooser currently displays “Glass.” The user-facing name for this
  replacement is “Smoked Quartz”; schedule that label correction in SQ.13's
  appearance/Settings boundary. Preserve `WeatherThemeId.GLASS` and its saved
  preference identity. This Details-only slice must not change the chooser or
  other theme labels, so SQ.8 does not claim the replacement label is complete.
- Existing deterministic sparse/missing-field fixture hooks may cover
  accessibility and visible missing values without exposing a stable manual
  installed state. The current sparse Details composition test supplies a
  presentation directly rather than exercising production mapping. Do not add
  production-only fixture behavior; document any installed capture boundary
  accurately.
- A 1.3 font-scale capture may place later groups below the first viewport.
  Verify vertical reachability and capture the scroll state instead of
  compressing text.
- The current roadmap leaves High contrast and RTL Details installation to
  later acceptance coverage unless directly needed to diagnose a concrete
  issue. The SQ.8 exit is available/missing-field, compact, and large-font
  Details review; do not silently expand it to SQ.9 or SQ.17A.

## Context budget

Target approximately 35–45% of available context for baseline review, a
Details-local composition edit, focused coverage, the bounded installed review,
and closeout. Summarize logs and capture metadata rather than loading large
suite output. If expected work grows beyond 55%, stop before broadening the
production boundary or visual matrix and create dependent partial plans;
65% is the hard maximum.

## Out of scope

- Four-page Static Full acceptance and decorative condition marks/inclusions
  (SQ.9); no weather marks or quartz inclusion overlay in this slice.
- Theme chooser display-name correction from “Glass” to “Smoked Quartz” (plan
  for SQ.13); persisted theme identity remains `GLASS`.
- Static performance Gate B (SQ.10), Subtle/None derivation (SQ.11–SQ.12),
  appearance switching and cross-page interactions (SQ.13–SQ.14), motion
  lifecycle or seam-glint (SQ.15–SQ.16/SQ.18), layout/localization and broader
  appearance acceptance (SQ.17A/SQ.17B), and separately planned full-suite
  verification (SQ.V).
- Provider/domain/presentation mapping, weather values, requests/cache,
  preferences, navigation, official alert behavior, other pages, and
  non-Glass/non-Full rendering.
