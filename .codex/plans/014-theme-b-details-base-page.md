# Plan 014 — Theme B Details base page

Status: Active
Cycle ID: 014-theme-b-details-base-page
Roadmap item: R0.9
Created: 2026-09-21

## Objective

Apply the established Theme B monitor composition to the Standard Home Details
page. The installed page must make the audit boundary obvious: source-normalized
current measurements, source/freshness facts, derived forecast-pattern signals,
and historical-reference context remain visibly and semantically distinct.
The slice must use the presentation data already available and must not invent
chart, trend-series, provenance, or unavailable-value inputs.

## Context-budget boundary

This is one bounded page-composition slice under the roadmap rule that an
implementation slice should remain below approximately 45% of one context
window. Production work is limited to the Details page and its direct layout
helpers in `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`, plus
focused deterministic assertions only if the current tests do not protect the
Details presentation boundary. Existing shared monitor components and
`ResolvedAppearance` are inputs, not a reason to redesign their contracts.

Do not paste full emulator screenshots or accessibility hierarchies into the
working context; store them under the cycle artifact directory and retain only
short observations and paths in the cycle record. If the objective requires a
new presentation model, chart-series contract, shared component API, or test
harness, record that dependency and stop at this boundary for a follow-up
slice rather than widening R0.9.

## Production boundary

The Details page composition is the only production boundary:

- retain the visible `Details` page identity and the outer Home pager contract;
- compose the existing `SourceFreshnessPanel` followed by the supplied
  `InspectionMetricGroup` values on opaque Theme B section surfaces;
- preserve the supplied group order and labels (`Conditions`, `Forecast
  pattern`, and `Historical context` when data exists), with no renderer-side
  fabrication or reclassification;
- keep current/source-normalized measurements separate from derived and
  historical values, and keep source/freshness text explicit; and
- route layout, type, color, surface, shape, and effects behavior through
  `ResolvedAppearance` and existing Material/theme bridges.

The renderer must remain presentation-only. Do not change canonical weather
models, repositories, provider behavior, mapper semantics, unit conversion,
pager state, or the shared monitor-component contracts unless a concrete
acceptance failure proves a direct, minimal correction is required.

## Functional invariants

- Home page order remains `Now -> Hourly -> Daily -> Details`; the outer Home
  pager remains the sole horizontal-swipe owner and Android Back from Details
  still returns to Daily.
- Details remains a vertically inspectable surface. It adds no nested pager,
  horizontal scrolling, static-tap page advance, chart gesture, or refetch.
- Source/freshness information remains visible text and meaningful semantics;
  unavailable source or update values remain explicitly unavailable.
- Provider-normalized current measurements, derived forecast-pattern values,
  and historical/reference values cannot be presented as interchangeable
  observations, forecasts, official products, or alerts.
- Empty/missing metric groups are omitted according to the existing mapper;
  the renderer never substitutes zero, a plausible value, or a decorative
  placeholder for missing data.
- Important metric labels and values remain visible without requiring weather
  marks or decorative effects. Existing section and source semantics remain
  truthful to the visible content.
- Effects Off remains opaque, static, and complete. Compact 360x640 and
  large-font presentation must keep critical labels, values, and page identity
  readable without clipping or unusable controls. RTL may mirror placement but
  must not reorder the semantic group contents.

## Implementation steps

1. Create `.codex/test-artifacts/014-theme-b-details-base-page/` and record
   baseline workflow/contract/test and `git diff --check` results. Preserve
   unrelated existing worktree changes.
2. Inspect the current installed Details page and the existing
   `DetailsPage`, `SourceFreshnessPanel`, `InspectionMetricGroup`, and
   `HomePresentation` contracts against the adopted Details anatomy. Record
   what already satisfies the objective before changing code.
3. Implement only the smallest direct Details composition update needed for
   the objective: retain the header, source/freshness panel, and supplied
   metric groups; improve grouping, spacing, labels, or semantics only where a
   concrete compact/large-font/effects-off acceptance observation requires it.
   Do not add chart/trend data or a new navigation model.
4. Add or adjust focused deterministic assertions only if needed to protect
   group order/separation and honest omission of unavailable groups/metrics.
   Preserve the existing source/freshness and derived/history tests; do not
   move provider or derived logic into Compose.
5. Run the focused and broadest available checks, then install the actual app
   and capture compact Details evidence, large-font evidence, Effects Off
   evidence, page-selector navigation, and Android Back to Daily. Inspect
   visible text, section separation, semantics, clipping, overlap, and the
   absence of invented chart/trend content. Record RTL and service-level
   TalkBack boundaries explicitly if they are not exercised.
6. Update completed-fact documentation only: the adopted UI specification,
   architecture/verification notes, roadmap status, and the cycle evidence.
   Close the cycle only after the history record states exact commands,
   installed observations, artifact paths, and unverified boundaries. Do not
   advance R0.10 or any later roadmap item.

## Acceptance criteria

- The installed Details page visibly uses the Theme B header, opaque section
  surfaces, source/freshness panel, and inspection metric groups.
- Current/source-normalized measurements, derived forecast-pattern values, and
  historical context are separated by visible group identity and meaningful
  semantics; the source/freshness facts remain distinct from all three.
- Existing complete and missing-data fixtures continue to preserve truthful
  values, group omission, unavailable wording, and provenance meaning. No
  chart or trend series is fabricated to fill the page.
- Details remains reachable through the existing named page selector and outer
  pager, and Android Back returns to Daily without nested horizontal paging.
- Compact, large-font, and Effects Off installed evidence is captured when
  tooling is available. Any unavailable verification boundary is recorded
  rather than inferred from compilation or preview output.
- A completed `.codex/history/` record and cycle-specific evidence exist before
  R0.9 is marked done.

## Verification and evidence

Expected artifacts live under
`.codex/test-artifacts/014-theme-b-details-base-page/`, including command logs,
screenshots, and concise visual/semantic notes. Run at minimum:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

When Android tooling is available, use the repository's actual emulator and
installed debug app at the established compact 360x640 viewport. Repeat the
affected Details states at font scale 1.3 in normal and debug Effects Off
launches, and restore any changed emulator settings. Do not claim service-level
TalkBack or RTL verification unless it is actually run.

## Risks and assumptions

- The current code already has the presentation-only Details data and shared
  source/inspection components; this plan assumes R0.9 can remain a page-level
  composition slice.
- The existing mapper owns group membership and missing-field omission. A
  concrete mapper or domain gap is a dependency to record, not an invitation
  to widen this visual slice.
- Installed visual evidence may be unavailable if the local Android toolchain
  or emulator cannot start; record the limitation and do not claim visual
  acceptance from compilation alone.

## Out of scope

- Theme B application to Now or any other Home page.
- New chart/trend-series inputs, historical provider work, live networking,
  repository/cache/refresh states, location, alerts, unit preferences, theme
  persistence, high contrast, Simple layout, or settings.
- Changes to canonical/provider models, derived-signal definitions, pager
  architecture, shared monitor-component APIs, or Compose test infrastructure.
- Service-level TalkBack verification, complete RTL/accessibility evidence
  closure, release checks, signing, or publication.
