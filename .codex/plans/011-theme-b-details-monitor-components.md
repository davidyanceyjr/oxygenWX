# Plan 011 — Theme B Details monitor components

Status: Completed
Cycle ID: 011-theme-b-details-monitor-components
Roadmap item: R0.6B
Created: 2026-09-21
Revised: 2026-09-21

## Objective

Complete R0.6B by establishing the reusable Details inspection boundary:

- `SourceFreshnessPanel` displays supplied source/provenance and update/freshness
  presentation strings as explicit visible facts.
- `InspectionMetricGroup` displays one typed `MetricGroupPresentation` with its
  supplied title and metrics, retaining the existing two-column bounded metric
  inspection treatment.

The existing Details page will use both components. This is a narrow
componentization and provenance-visibility change, not the R0.9 Details page
composition redesign. No chart is added: the current presentation contract has
formatted derived values but no typed series with defined interval, provenance,
range, and unavailable-data behavior.

## Context-budget boundary

This is one bounded slice under the roadmap's 45%-of-context rule. It is
limited to `MonitorComponents.kt`, the Details call sites and superseded private
renderer in `OxygenWeatherApp.kt`, focused deterministic regression coverage,
documentation/evidence, and cycle closure. It does not alter weather data,
mapping, grouping, unit formatting, page/pager state, or add a chart/data
contract.

## Production boundary

Add these internal composables in
`app/src/main/java/com/oxygen/weather/ui/MonitorComponents.kt`:

```kotlin
@Composable
internal fun SourceFreshnessPanel(
    sourceLine: String,
    updatedLine: String,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
)

@Composable
internal fun InspectionMetricGroup(
    group: MetricGroupPresentation,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
)
```

Both inputs are already presentation values; neither component accepts a raw
weather model, provenance object, repository, provider DTO, persistence object,
theme ID, callbacks, or state. `SourceFreshnessPanel` gives each supplied line
an explicit visible label and a combined semantic summary. It must not infer
source authority or a time from the strings. `InspectionMetricGroup` owns the
existing opaque section, title, two-column metric arrangement, label/value
visibility, ellipsis limits, and supplied grouping boundary. Its title remains
the visible distinction between source-normalized `Conditions`, derived
`Forecast pattern`, and `Historical context` data.

Modify Details only to replace its existing private `MetricGroup` with
`InspectionMetricGroup` and place `SourceFreshnessPanel(home.sourceLine,
home.updatedLine, ...)` beneath the Details header. Delete only the superseded
private renderer and imports that become unused. Do not change scrolling,
page/pager behavior, group ordering, metric strings, or the Details header.

## Functional invariants

- Page order stays `Now -> Hourly -> Daily -> Details`; exactly one outer
  `HorizontalPager` remains the only global horizontal-swipe owner. No new
  gesture, destination, callbacks, or state is introduced.
- Details remains vertically scrollable. Existing group and metric order are
  supplied by presentation and remain unmodified.
- Source, freshness/update time, observation/model/forecast provenance, derived
  forecast-pattern values, and historical-reference values remain distinct.
  The UI does not parse, calculate, relabel, or fabricate any weather fact.
- All important source/freshness and inspection facts remain visible text and
  meaningful semantics. The source/freshness panel's semantics include both
  supplied values without hiding visible labels and text.
- Missing values remain omitted/unavailable according to supplied presentation
  values; no zero or plausible substitute is introduced.
- Theme B tokens, typography, opaque panel treatment, Effects Off behavior,
  and existing Details visual layout remain unchanged apart from the explicit
  source/freshness panel. Effects Off remains opaque, static, and complete.
- No raw-theme branching or provider/data/repository/persistence imports enter
  components. Existing 48dp control guidance and Back behavior are unchanged.

## Implementation steps

1. Create `.codex/test-artifacts/011-theme-b-details-monitor-components/` and
   preserve baseline output from workflow, contract, and the smallest available
   test command. Record any Java/SDK environment requirement verbatim.
2. Move the existing Details group renderer into `InspectionMetricGroup` and
   implement the text-only source/freshness panel with existing semantic colors,
   typography, spacing, and `MonitorSection`. Do not add a timeline or chart.
3. Make the direct Details call-site migration only. Inspect that group data,
   page/pager state, scrolling, Home navigation, and all other pages are
   unchanged.
4. Run `python scripts/dev.py contract` and `python scripts/dev.py test` using
   a compatible Java/SDK environment if needed. Existing `HomePresentationTest`
   remains focused evidence for grouping, missing values, and derived/history
   separation; add a pure assertion only if a presentation contract changes.
5. Update implemented-fact documentation only: name both components in
   `docs/ARCHITECTURE.md`, add their status to the current-candidate scope in
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, append exact outcomes to
   `VERIFICATION.md`, and at closure change only R0.6B in `docs/ROADMAP.md` to
   DONE with its history link.
6. When Android tooling/emulator are available, build/install and capture the
   installed Details page at 360x640 under Subtle and debug Effects Off; capture
   normal and 1.3 font scales, scroll to all three groups, inspect source/
   freshness and group semantics, selector navigation, and Back from Details.
   Restore altered emulator state. Record RTL and service-level TalkBack as
   unverified unless actually run.
7. Run `python scripts/dev.py check` when available, `git diff --check`, and
   inspect the full diff. Retain command output, screenshots, hierarchy dumps,
   and verification notes before closing with exact results and limits.

## Acceptance criteria

- `SourceFreshnessPanel` and `InspectionMetricGroup` are internal shared UI
  components with only presentation inputs and `ResolvedAppearance`.
- Details uses both; source and update/freshness strings are visible with a
  concise combined semantic summary.
- Conditions, Forecast pattern, and Historical context remain visibly and
  semantically separated by supplied group titles; labels and values remain
  visible/accessible under the existing bounded layout.
- No chart/series contract, provider/domain/presentation mapping change, or
  Details page redesign is introduced.
- Focused and broader checks, installed evidence when available, whitespace
  validation, and a truthful history record are preserved.

## Verification and evidence

- `python scripts/dev.py workflow`
- `python scripts/dev.py contract`
- `python scripts/dev.py test` with a compatible Java/SDK environment
- `python scripts/dev.py check` when available
- `git diff --check` and final diff review
- installed Subtle/Effects Off Details evidence (normal and 1.3 font scales),
  hierarchy dumps, and notes under
  `.codex/test-artifacts/011-theme-b-details-monitor-components/`

## Risks and assumptions

- The fixture currently supplies nonempty source/update strings. The component
  renders supplied strings honestly and does not define future typed
  loading/cached/failed wording; that belongs to R1.2/R1.2A.
- `MetricGroupPresentation` preserves semantic grouping but has no per-metric
  provenance. This cycle maintains group-level separation and does not claim
  finer provenance than the mapper provides.

## Out of scope

- R0.9 Details page-specific Theme B composition, visual tuning, or new
  interactions.
- Any chart, trend-series, interval/range/provenance model, or derived
  calculation.
- Presentation model/mapper, units, provider, repository/cache, alerts,
  location, settings, RTL, TalkBack, or Compose UI-test infrastructure changes.
- Changes to Now, Hourly, Daily, pager, forecast windows, or effects resolution
  beyond regression verification.
