# Plan 014 — Theme B Details base page

Status: Completed
Cycle ID: 014-theme-b-details-base-page
Roadmap item: R0.9
Created: 2026-09-21
Revised: 2026-09-21 — implementation-ready slice revision; large-font renderer continuation

## Objective

Complete R0.9 by making the Standard Home Details page an explicit Theme B
audit surface. The installed page must visibly and semantically separate:

1. source/freshness facts;
2. current, provider-normalized measurements;
3. derived forecast-pattern signals; and
4. historical/reference context.

The page uses the existing typed `HomePresentation` data and shared monitor
components. It does not add chart data, trend-series inputs, provider logic,
or a new provenance contract.

## Context-budget boundary

This is one bounded page-composition slice under the repository rule that a
slice must remain below approximately 45% of one context window.

Production and focused-test work is limited to:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`, specifically
  `DetailsPage` and a private page-local helper only if needed;
- `app/src/main/java/com/oxygen/weather/ui/MonitorComponents.kt`, limited to
  the existing `InspectionMetricGroup` text overflow behavior required to
  keep long labels and values complete at font scale 1.3; and
- `app/src/test/java/com/oxygen/weather/presentation/HomePresentationTest.kt`,
  only for deterministic Details grouping/omission assertions; and
- the explicitly listed documentation and cycle-evidence files below.

Do not change `ResolvedAppearance`, canonical data,
derived-signal definitions, repositories, providers, navigation state, Gradle
dependencies, or Compose test infrastructure in this slice. If acceptance
requires a shared-component API, a new presentation field, a chart/series
contract, or a domain/mapper change, record the concrete dependency and stop
R0.9 for a dependent slice. Do not widen this plan to absorb it.

Do not paste full screenshots, hierarchy dumps, or Gradle logs into the
working context. Store them under
`.codex/test-artifacts/014-theme-b-details-base-page/` and retain only concise
observations and paths in the evidence/history record.

## Production boundary

The single production boundary is the page-level Details composition in
`OxygenWeatherApp.kt`.

The implementation must:

- keep the existing named `Details` page and outer Home pager;
- retain a vertically scrollable page so all supplied inspection groups remain
  reachable at compact and large font sizes;
- use `ResolvedAppearance` layout/theme/effects values for page spacing and
  all existing monitor surfaces;
- show a concise Details header whose supporting text accurately identifies
  normalized measurements, forecast-pattern signals, and historical context;
- render `SourceFreshnessPanel` first, using the supplied `sourceLine` and
  `updatedLine` without recomputing or relabeling provenance;
- render every supplied `MetricGroupPresentation` in mapper order through
  `InspectionMetricGroup`, preserving the visible group titles and metric
  labels/values; and
- use the existing opaque `MonitorSection` surfaces and existing source-panel
  semantics. Do not add `clearAndSetSemantics` around metric groups if that
  would hide their individual visible labels and values.

The expected direct composition correction is to use the page-level
`layout.pageStackGap` between the header, source/freshness panel, and metric
groups, matching Now and Daily. Any additional renderer change must be tied
to a concrete installed observation and remain within this page boundary.

No renderer code may parse formatted values, invent missing metrics, reorder
groups, classify a value as official, or create chart/trend content.

## Functional invariants

- Home order remains `Now -> Hourly -> Daily -> Details`; the outer Home pager
  remains the only horizontal-swipe owner.
- Android Back from Details still moves to Daily. Static page content does not
  advance the pager. Details adds no nested pager, horizontal scrolling, chart
  gesture, or refetch.
- `SourceFreshnessPanel` remains a distinct first inspection surface. Source,
  update, valid-time, and freshness wording already available in the supplied
  presentation model remains truthful; unavailable metadata stays explicitly
  unavailable.
- The `Conditions`, `Forecast pattern`, and `Historical context` groups remain
  visibly distinct and in mapper order when present. Current measurements,
  derived signals, and historical/reference values cannot masquerade as one
  another, forecasts, observations, official alerts, or provider products.
- Empty groups and missing metrics remain omitted or unavailable according to
  the existing mapper. The renderer never substitutes zero, a plausible value,
  a repeated value, or decorative content.
- Important facts remain visible text and do not depend on weather marks,
  atmospheric artwork, opacity, or motion. Existing source and entry semantics
  remain truthful to visible content.
- Effects Off remains opaque, static, and complete. Compact 360x640 and
  font-scale 1.3 states must keep page identity, source/freshness facts, group
  titles, metric labels, and metric values reachable without critical clipping
  or overlap.
- RTL may mirror physical placement but must preserve the semantic group order
  and metric meaning. TalkBack service traversal is not claimed unless run.

## Implementation steps

1. Create `.codex/test-artifacts/014-theme-b-details-base-page/`. Record the
   pre-edit results of `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, `python scripts/dev.py test`
   (`:app:testDebugUnitTest`), and `git diff --check`. Record the current
   worktree boundary and do not stage or rewrite unrelated changes.

2. Inspect the current installed/source baseline before editing:

   - `DetailsPage` in `OxygenWeatherApp.kt`;
   - `SourceFreshnessPanel` and `InspectionMetricGroup` in
     `MonitorComponents.kt`;
   - `HomePresentationMapper.details` and `MetricGroupPresentation`; and
   - existing Details-related assertions in `HomePresentationTest` and
     `HistoricalSynthesisTest`.

   Confirm that the existing data already supplies the four required semantic
   boundaries. Capture a concise baseline note; do not redesign a component
   merely because it is already adequate.

3. Implement the page-level Details slice in `OxygenWeatherApp.kt`:

   - retain the `fillMaxSize` + `verticalScroll` composition and resolved page
     padding;
   - change page-level spacing to `Arrangement.spacedBy(layout.pageStackGap)`;
   - use header support text that names the normalized/derived/historical
     inspection scope without claiming chart or official-alert content;
   - keep `SourceFreshnessPanel` before all metric groups;
   - keep `home.detailGroups.forEach` in supplied order and pass each group to
     `InspectionMetricGroup` without caps, sorting, filtering, or value
     parsing; and
   - make no changes to shared component signatures or presentation models.

   If the baseline already satisfies one of these points, leave that code
   unchanged and record the fact. If a visual observation exposes a concrete
   page-only issue, make the smallest direct correction and document it.

4. Strengthen deterministic presentation-boundary tests in
   `HomePresentationTest.kt` without adding a Compose UI-test dependency:

   - retain the complete group-order assertion and add exact fixture-label
     assertions: Conditions = `Feels like`, `Humidity`, `Dew point`,
     `Pressure`, `Cloud cover`, `Visibility`; Forecast pattern = `3h
     temperature`, `3h pressure`, `Persistence`, `Volatility`, `Pattern`;
     Historical context = `Seasonal temperature`, `Temperature departure`,
     `Pressure departure`, `Analog years`, `Reference`;
   - retain `sourceAndCurrentSemanticsAreExplicit`,
     `unavailableMetadataUsesExplicitTextInsteadOfPlaceholderValues`, and
     `optionalMissingDetailMeasurementsAreOmittedRatherThanInvented`;
   - add a named `detailsOmitEmptyOptionalGroupsButKeepSourceContext` test
     using `bundle.copy` with all optional current detail fields null,
     `hourly = emptyList()`, and `baseline.copy(temperatureSamplesC =
     emptyList(), analogYears = emptyList())`; assert
     `presentation.detailGroups.isEmpty()` while `sourceLine` remains
     `Model estimate · Offline development fixture` and `updatedLine` remains
     `Updated 12:00 PM`; and
   - leave `HistoricalSynthesisTest` unchanged unless the new fixture exposes
     an existing deterministic derived-signal defect. A defect in derived
     semantics is a stop condition, not a reason to modify it opportunistically.

5. Run the focused and repository gates after implementation:

   ```sh
   python scripts/dev.py workflow
   python scripts/dev.py contract
   python scripts/dev.py test
   python scripts/dev.py check
   git diff --check
   ```

   Record the selected JDK/SDK and exact pass/failure output in the artifact
   notes. If the default host exposes Java 8, rely on the repository's JDK
   selection path and record the selected compatible JDK; do not edit the
   Gradle wrapper or production code as an environment workaround.

6. Verify the actual installed app when Android tooling is available. Use the
   repository emulator path (`scripts/run_emulator.sh`, local
   `.android-sdk`/`.android/avd`, AVD `oxygen_starter`) and the installed debug
   APK. At compact 360x640, capture normal Subtle and debug Effects Off states
   for:

   - Details reached through the named page selector;
   - the header and source/freshness panel;
   - all visible metric group titles and representative metric labels/values;
   - scrolling to the lower historical/reference group; and
   - Android Back returning from Details to Daily.

   Repeat Details at font scale 1.3 in both effects states. Inspect hierarchy
   or semantics for page identity, source/freshness wording, group titles, and
   representative values. Check that Effects Off is solid/static, all lower
   content remains reachable, and no critical text clips or overlaps. Restore
   any changed emulator font scale/settings. Exercise RTL only if the local
   environment supports it; do not claim service-level TalkBack unless it is
   actually run.

   If the emulator reproduces the prior black-frame/ANR failure, preserve the
   raw logs and hierarchy evidence, record the exact failure, and make no
   installed visual claim. This does not authorize widening R0.9 into tooling
   repair.

7. Update only completed facts in the required records:

   - `docs/ARCHITECTURE.md`: state that Details composes the supplied
     source/freshness panel and ordered typed metric groups in a vertically
     scrollable Theme B page; page/pager state remains in `OxygenWeatherApp`,
     and Compose still receives presentation models rather than provider data.
   - `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`: move the Details Theme B page
     composition from future work into current candidate scope. Record the
     visible source/freshness surface, ordered Conditions/Forecast pattern/
     Historical context separation, text/semantic requirement, and explicit
     absence of invented chart/trend data. Leave Now as the remaining Theme B
     page-composition item.
   - `docs/ROADMAP.md`: change only R0.9 from `PLANNED` to `DONE` after all
     acceptance evidence is complete, and point to the R0.9 history record.
   - `VERIFICATION.md`: append an R0.9 record with exact commands, selected
     toolchain, deterministic test coverage, installed viewport/effects/font
     scale observations, artifact path, and every unverified boundary. Do not
     claim screenshot, RTL, or TalkBack success when not run.
   - `.codex/history/2026-09-21-014-theme-b-details-base-page.md`: close the
     cycle using the repository cycle command only after the record states the
     implemented outcome, commands, evidence, limitations, and R0.10 follow-up.

   Do not update README, UI workflow, roadmap items after R0.9, or unrelated
   documentation unless a concrete completed fact from this slice requires it.

## Acceptance criteria

- The source tree contains only the bounded Details page/test/documentation
  changes described above; no shared API, domain, provider, dependency, or
  navigation break is introduced.
- The installed Details page visibly uses the Theme B header, opaque
  source/freshness surface, and ordered inspection metric groups with a
  scrollable lower context surface.
- Visible text and inspected semantics expose Details identity, source/update
  facts, group identity, and representative metric meaning without relying on
  weather marks or effects.
- Deterministic tests prove group order, metric-label boundaries, unavailable
  source/update wording, and omission of empty optional groups without
  invented values.
- Compact and font-scale 1.3 normal/Effects Off evidence is captured when the
  toolchain is available; any emulator, RTL, or TalkBack limitation is stated
  precisely in the evidence and history record.
- `python scripts/dev.py workflow`, `contract`, `test`, and `check`, plus
  `git diff --check`, pass for the final source state, or the history record
  names the exact unavailable command and reason.
- R0.9 is not marked complete until the evidence directory and completed
  history record exist. The next roadmap action remains R0.10 Theme B Now base
  page.

## Verification and evidence

Expected artifacts live under
`.codex/test-artifacts/014-theme-b-details-base-page/`, including:

- baseline/final workflow, contract, test, check, and diff-check logs;
- the deterministic test result and selected JDK/Android SDK note;
- compact Subtle and Effects Off screenshots or an explicit capture failure;
- font-scale 1.3 Subtle and Effects Off screenshots or an explicit capture
  failure;
- page-selector/Back interaction notes;
- hierarchy/semantic dumps for Details identity, source/freshness, group
  titles, and representative metrics; and
- concise visual notes covering clipping, overlap, opacity, scroll reachability,
  and any unverified RTL/TalkBack boundary.

Evidence is not inferred from compilation, a Compose preview, or source review.
Raw large outputs stay in the artifact directory, not in the plan or history
summary.

## Risks and assumptions

- The current repository already contains the typed Details groups and shared
  source/inspection components, so this remains a page-composition slice.
- The current Details renderer is close to the target; the likely production
  delta is page spacing/header copy plus verification, not a new UI system.
- The local emulator previously produced black frames and an ANR during R0.8.
  If that persists, record the limitation rather than claiming visual success
  or repairing emulator infrastructure inside this slice.
- The existing semantic panel summary and visible metric labels are the
  accessibility boundary for this slice. Service-level speech traversal and
  full RTL closure remain separate roadmap evidence.

## Out of scope

- Theme B application to Now, or any change to Hourly/Daily behavior.
- New chart/trend-series data, historical provider integration, live providers,
  repository/cache/refresh states, location, alerts, unit preferences,
  persisted themes/effects, high contrast, Simple layout, or settings.
- Changes to canonical/provider models, derived-signal formulas, presentation
  state contracts, shared monitor-component APIs, pager architecture, or
  Compose UI-test infrastructure.
- Toolchain/emulator repair, release checks, signing, publication, complete
  RTL evidence, or service-level TalkBack verification.
