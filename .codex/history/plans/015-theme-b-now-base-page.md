# Plan 015 — Theme B Now base page

Status: Completed
Cycle ID: 015-theme-b-now-base-page
Roadmap item: R0.10
Created: 2026-09-21
Revised: 2026-09-21 — implementation-ready full-slice revision

## Objective

Complete R0.10 by applying the established Theme B page composition to
Standard Home Now. The installed Now page must answer “what is happening?”
first, with a visible `Now` page identity, location and source/update context,
dominant current temperature and condition, readable supporting current facts,
and the supplied optional Forecast pattern group when it exists.

The slice is complete only when the rendered page and inspected semantics
preserve those facts at the compact and large-font states, including Effects
Off, without the retired Atmosphere Deck composition or a new data contract.

## Context-budget boundary

This is one bounded page-composition slice under the roadmap rule that a slice
must remain below approximately 45% of one context window. Work is limited to
the Now composition, the minimum shared text-rendering correction required to
keep Now context complete, deterministic presentation assertions that protect
the supplied Now inputs, and the explicitly listed evidence/document records.

Do not paste screenshots, hierarchy dumps, or build logs into the plan or
history summary. Store raw evidence under
`.codex/test-artifacts/015-theme-b-now-base-page/` and retain concise paths and
observations in the cycle record.

## Baseline audit and concrete gaps

The current source already has the correct presentation inputs and most of the
required visual building blocks, but it is not yet a complete R0.10 slice:

- `NowPage` currently uses the location as the `MonitorHeader` title, while
  the page contract requires the global page identity `Now` to remain visible
  by name.
- The current header support text is a single two-line, ellipsized string.
  The Now source/update context must remain readable at 360x640 and font scale
  1.3; an installed check must not accept an ellipsized source or update line.
- The current optional Forecast pattern renderer uses `pattern.metrics.take(3)`.
  That silently drops supplied derived values. The Now renderer must preserve
  every supplied metric in mapper order; it may not cap the group to make a
  screenshot fit.
- The hero surface uses `clearAndSetSemantics` with `spokenSummary`, but that
  summary does not include the visible dew-point fact. The corrected semantics
  must preserve the existing concise summary and expose every visible hero
  fact without making the weather mark the only explanation.

These are page/rendering corrections. The existing `HomePresentationMapper`
already supplies the required strings, optional group omission, provenance
wording, condition identity, and concise summary; no mapper or domain change
is authorized by this plan.

## Production boundary

The normal production boundary is:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`, limited to
  `NowPage` and a private Now-only helper if one is needed; and
- `app/src/main/java/com/oxygen/weather/ui/MonitorComponents.kt`, limited to
  the existing `MonitorHeader` supporting-text layout to remove its current
  two-line ellipsis for complete Now context, and the existing
  `InspectionMetricGroup` call site needed to render all supplied Forecast
  pattern metrics.

The shared component API must not be changed. A header text-layout adjustment
must be behavior-preserving for Hourly, Daily, and Details: their existing
identity/range text remains visible and their navigation is unchanged. Do not
change `ResolvedAppearance`, canonical data, derived calculations, provider or
repository contracts, presentation model fields, pager architecture, or Gradle
dependencies.

If the required Now facts cannot be rendered from `CurrentPresentation`,
`sourceLine`, `updatedLine`, and the existing optional `MetricGroupPresentation`,
record the missing dependency and stop this slice. Do not invent a field or
widen R0.10 into R1/R4 work.

## Functional invariants

- Standard Home order remains `Now -> Hourly -> Daily -> Details`; the single
  outer `HorizontalPager` remains the only global horizontal-swipe owner.
- The page selector continues to name and select Now. Static content does not
  advance the pager. Android Back from Now retains normal host behavior; Back
  from another page still moves toward Now as before.
- The Now header visibly identifies `Now`, retains the supplied location, and
  exposes the supplied source/provenance and update wording without relabeling
  a model estimate as an observation or forecast.
- Current temperature and condition have the strongest hierarchy. Apparent
  temperature, humidity, dew point, precipitation, and wind remain visible
  text, including the mapper’s explicit unavailable wording.
- The weather mark is supplemental. Adjacent condition text and semantics
  remain sufficient if the mark is absent or hidden from accessibility.
- The optional Forecast pattern group is rendered only when supplied. Every
  supplied metric and its meaning remain visible in mapper order; no `take`,
  sorting, fabricated fallback, or derived-value reinterpretation is allowed.
- The existing `spokenSummary` remains the basis of the concise hero
  announcement. The final hero semantics also expose the visible dew-point
  fact and do not hide visible temperature, condition, apparent temperature,
  or humidity behind an incomplete replacement description.
- Effects Off remains opaque, static, and complete. Theme/effects resolution
  can change presentation only, never weather meaning, provenance,
  accessibility meaning, or navigation semantics.
- Compact 360x640, font scale 1.3, and RTL-capable layouts preserve readable
  primary facts, page identity, usable controls, and semantic order. Existing
  applicable interactive targets retain the 48dp guidance.
- No nested pager, new horizontal gesture, refetch, provider DTO, repository,
  raw theme identifier, official-alert heuristic, or retired Atmosphere Deck
  language is introduced.

## Visual objective and environment matrix

The visual objective is a calm Theme B monitor page: `Now` and the current
condition are the first scan target; location/source/update context is legible
but secondary; apparent/humidity/dew facts and precipitation/wind tiles are
compact supporting surfaces; the optional Forecast pattern is a clearly named
secondary context surface. Visible text is the authority over marks, opacity,
and motion.

The required installed matrix is:

- emulator/device at compact `360x640`, density `160`, font scale `1.0`;
- the same viewport at font scale `1.3`;
- normal Subtle effects and debug Effects Off in both font states;
- Now reached through the named selector and by outer-page navigation;
- page-selector and Now-content hierarchy/semantic inspection;
- a return/back smoke check proving no pager regression; and
- RTL exercise when the local environment supports it, with no TalkBack claim
  unless the service is actually run.

If the known emulator black-frame/ANR condition recurs, preserve the raw
failure evidence and make no installed visual claim.

## Implementation steps

1. Create `.codex/test-artifacts/015-theme-b-now-base-page/` and record the
   pre-edit results of:

   ```sh
   python scripts/dev.py workflow
   python scripts/dev.py contract
   python scripts/dev.py test
   git diff --check
   ```

   Record the clean or dirty worktree boundary and do not rewrite unrelated
   changes. `python scripts/dev.py check` is a final/broader gate, not a reason
   to repeat the full suite during every visual iteration.

2. Inspect and record the current source baseline before editing:

   - `NowPage`, `OxygenWeatherApp`, the outer pager, selector, and Back handler;
   - `MonitorHeader`, `MonitorSection`, `MetricTile`, and
     `InspectionMetricGroup`;
   - `CurrentPresentation`, `HomePresentation`, and mapper grouping; and
   - existing `HomePresentationTest`, appearance tests, source-contract output,
     and the last installed Now evidence.

   Confirm that all Now facts in this plan are already supplied upstream. Do
   not treat a Compose preview or source inspection as visual acceptance.

3. Implement the smallest coherent Now composition correction:

   - keep the vertically scrollable page and resolved page insets/gaps;
   - make the page-level header title `Now`, while retaining the supplied
     location plus source/update context as visible supporting text;
   - remove the header’s Now-context truncation risk with the smallest
     existing-component layout change. Do not change its call contract or
     silently ellipsize source/update wording. Verify that the unchanged
     Hourly, Daily, and Details header/range text still fits and remains named;
   - keep the opaque Theme B `MonitorSection` as the primary surface, with
     temperature and condition dominant, the supplied apparent/humidity/dew
     facts visible, and the supplied condition mark supplemental;
   - preserve the precipitation and wind `MetricTile` surfaces and their
     supplied headline/supporting strings, including unavailable wording;
   - replace the current Forecast pattern cap with an all-metrics rendering
     path using the existing `InspectionMetricGroup` so the title,
     labels, values, mapper order, and visible semantics are not duplicated.
     Render the group only when supplied and do not add a chart or a new
     summary field; and
   - replace the incomplete hero `clearAndSetSemantics` result with semantics
     that retain `now.spokenSummary` and also communicate the visible dew-point
     fact. Keep all visible hero facts available to inspection; do not make a
     decorative mark carry condition meaning.

   Leave any already-correct code unchanged and record that fact in the
   evidence notes. If a compact or large-font observation reveals a concrete
   page-only clipping/overlap issue, fix it within this boundary using resolved
   layout values and supplied text. Do not shorten, parse, reorder, or replace
   meteorological values to improve a screenshot.

4. Strengthen deterministic presentation-boundary tests in
   `app/src/test/java/com/oxygen/weather/presentation/HomePresentationTest.kt`.
   Do not add Compose UI-test infrastructure or duplicate renderer logic.

   Retain the existing exact assertions for source/update, temperature,
   condition, apparent temperature, humidity, dew point, precipitation, wind,
   and condition identity. Strengthen the current summary assertions to the
   fixture’s exact concise summary:

   ```text
   Demo Station, Partly cloudy, 28°, feels like 29°. Humidity 56%. Wind 13 kilometers per hour.
   ```

   Keep the existing exact Forecast pattern label assertion and make its
   completeness explicit: the supplied group contains all five labels in
   order (`3h temperature`, `3h pressure`, `Persistence`, `Volatility`,
   `Pattern`). Add an explicit omission assertion proving that when the
   derived inputs are absent the Forecast pattern group is absent, not padded
   with placeholder values. Keep the unavailable current/source/update test,
   and extend the partial-current fixture with null humidity/dew point plus
   exact `Unavailable` assertions for both fields; the existing optional-detail
   omission test remains separate coverage for empty Conditions grouping.

   These tests protect mapper inputs and omission semantics. They do not claim
   that Compose rendered every label; that claim requires the installed
   hierarchy/evidence step below.

5. Run the focused and repository gates after source/test changes:

   ```sh
   python scripts/dev.py workflow
   python scripts/dev.py contract
   python scripts/dev.py test
   python scripts/dev.py check
   git diff --check
   ```

   Record the selected JDK, Android SDK, exact focused-test result, and any
   unavailable command/reason. Do not repair the toolchain, emulator, or
   unrelated source in this slice.

6. Install and verify the actual debug app when Android tooling is available.
   Use the repository emulator path and current installed APK, preferably
   `oxygen_starter` with the local `.android-sdk`/`.android/avd` setup. Capture
   evidence under the cycle artifact directory, not in the plan.

   At compact 360x640, inspect both Subtle and Effects Off for:

   - selector identity and Now reached by selector and outer navigation;
   - visible `Now`, location, complete source line, and complete update line;
   - dominant temperature/condition, apparent temperature, humidity, and dew
     point, with the weather mark treated as supplemental;
   - precipitation and wind headline/supporting values;
   - the optional Forecast pattern title and every supplied label/value;
   - hero content-description/semantics coverage, including the existing
     spoken summary and dew point;
   - no unintended nested horizontal scrolling or page change from static
     taps; and
   - Android Back from Now using normal host behavior, plus a smoke check that
     navigation from another page back toward Now still works.

   Repeat the affected Now state at font scale 1.3 in both effects states.
   Inspect clipping, overlap, ellipsis on important facts, opacity/static
   behavior, and lower-content reachability. Exercise RTL if supported and
   record physical mirroring versus semantic order. Do not claim service-level
   TalkBack unless it is actually run. Restore any changed emulator font
   scale/settings.

7. Update only completed facts after implementation and acceptance evidence:

   - `docs/ARCHITECTURE.md`: record that Now composes the supplied
     `CurrentPresentation`, source/update context, current metric tiles, and
     optional ordered Forecast pattern group through Theme B monitor
     components; page/pager state remains in `OxygenWeatherApp`, and Compose
     still receives presentation models rather than provider data.
   - `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`: move the Theme B Now page from
     future work into current candidate scope. State the visible `Now`
     identity, current-condition hierarchy, supporting current facts,
     source/update context, optional complete Forecast pattern group, text and
     semantic equivalence, and absence of invented chart/alert content. Remove
     only the now-completed Now item from “Still separate future slices”.
   - `docs/ROADMAP.md`: change only R0.10 from `ACTIVE` to `DONE` after the
     evidence directory and history record are complete, and point to the
     R0.10 history record. Do not advance R1 or any provider/alert/location
     item.
   - `VERIFICATION.md`: append an R0.10 execution record with exact commands,
     selected toolchain, deterministic test coverage, installed viewport,
     effects/font-scale observations, artifact path, and every unverified
     boundary. Do not claim screenshot, RTL, or TalkBack success when not run.
   - `.codex/history/2026-09-21-015-theme-b-now-base-page.md`: close the cycle
     using the repository cycle command only after it states what changed,
     what did not change, all passed/unavailable verification, evidence paths,
     limitations, and the next roadmap item.
   - `.codex/current.md`: leave the lifecycle state consistent with the close
     operation. During this active implementation slice it must continue to
     point to this plan; after a genuinely completed slice the close operation
     may return it to IDLE according to the repository workflow.

   Do not update `docs/SPECIFICATION.md`, `AGENTS.md`, README, the UI workflow,
   or unrelated roadmap items: this slice does not change product authority,
   operating rules, or release scope.

## Acceptance criteria

- Source changes remain within `NowPage`, the justified existing shared-header
  text layout, and the existing shared pattern renderer call site, plus the
  named deterministic test and documentation/evidence records. No API,
  domain, provider, repository, alert, dependency, pager, or presentation
  contract changes are introduced.
- The installed page visibly uses the Theme B monitor composition with a
  named `Now` identity, complete location/source/update context, dominant
  current temperature/condition, supporting current measurements, and the
  optional complete Forecast pattern group without `take(3)`-style loss.
- Visible text and inspected semantics preserve current meaning, unavailable
  wording, source/provenance, update context, concise summary meaning, page
  identity, and existing pager/Back behavior.
- Effects Off is visibly opaque, static, and complete; compact and font-scale
  1.3 Subtle/Effects Off evidence is captured when the toolchain is available.
  Important facts are not accepted as complete when they are ellipsized,
  clipped, overlapped, or only represented by decoration.
- Deterministic tests protect exact Now values, source/update wording,
  concise-summary output, complete optional pattern input, missing-field
  wording, and pattern omission behavior.
- `workflow`, `contract`, `test`, `check`, and `git diff --check` pass for the
  final source state, or the history record names the exact unavailable gate
  and reason.
- R0.10 is not marked complete until its evidence directory and completed
  history record exist. No later roadmap item is marked complete by inference.

## Verification and evidence

Expected artifacts under
`.codex/test-artifacts/015-theme-b-now-base-page/` include:

- baseline and final workflow/contract/test/check/diff-check logs;
- focused deterministic test output and selected JDK/Android SDK note;
- compact Subtle and Effects Off screenshots or explicit capture failures;
- font-scale 1.3 Subtle and Effects Off screenshots or explicit failures;
- selector, outer-navigation, Back, and static-tap notes;
- hierarchy/semantic dumps for Now identity, source/update context, primary
  facts, optional pattern labels/values, and hero summary coverage; and
- concise visual notes covering clipping, overlap, scroll reachability,
  opacity/static behavior, RTL status, and TalkBack status.

Evidence is not inferred from compilation, a Compose preview, or source review.
Raw large outputs remain in the artifact directory; the history summary names
the relevant files and exact limitations.

## Risks and assumptions

- The deterministic fixture already supplies the full CurrentPresentation and
  Forecast pattern inputs required by this slice. If that ceases to be true,
  stop rather than adding a presentation field.
- The shared `MonitorHeader` may be visually adequate after making `Now` the
  title, but source/update truncation at large font is an acceptance failure,
  not an acceptable limitation. Fix only its existing text layout and smoke
  check all other page headers.
- `InspectionMetricGroup` is already a typed presentation-only renderer and
  can preserve all supplied pattern metrics without a new UI model. If its
  existing layout cannot meet the compact/large-font requirement, make the
  smallest page-local layout correction; do not cap or invent values.
- The local emulator may again produce black frames or an ANR. Preserve raw
  evidence and state the visual boundary as unverified; do not turn that into
  toolchain work or a false acceptance claim.

## Out of scope

- Live forecast providers, repository/cache/refresh state, location, official
  alert provider/integration, units, persisted themes/effects, high contrast,
  Simple layout, settings, release checks, signing, and publication.
- Changes to canonical weather models, derived formulas, presentation state
  contracts, mapper semantics, unit conversion, pager architecture, or Compose
  test infrastructure.
- New charts, hourly trend series, alert heuristics, new navigation routes,
  new gestures, or any reintroduction of the retired Atmosphere Deck/art-sheet
  language.
- Full RTL closure and service-level TalkBack verification unless separately
  exercised and recorded as evidence.
