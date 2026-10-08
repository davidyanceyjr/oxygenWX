# Plan 151 — Spoken semantics contract

Status: Completed
Cycle ID: 151-spoken-semantics-contract
Roadmap item: R6.1
Created: 2026-10-08

## Objective

Provide concise, provider-neutral spoken summaries for the core Home facts on
Now, Hourly, Daily, and Details. Summaries use the selected Metric, US, or UK
presentation units, name unavailable facts honestly, and expose forecast rows
in chronological order. The result is a testable Compose accessibility contract
for the current Home presentation path; it does not add new weather facts.

## Current implementation findings

- `CurrentPresentation`, `HourlyEntryPresentation`, and
  `DailyEntryPresentation` already carry unit-resolved `spokenSummary` values
  produced by `HomePresentationMapper`.
- Production current/hourly/daily composables expose those summaries through
  Compose semantics, and some existing tests assert their presence/order.
- Details currently uses ordered `MetricGroupPresentation` values with visible
  label/value text, but the presentation model has no concise group/metric
  spoken summary contract. The coverage found so far does not assert the
  complete four-page semantics outcome across all three unit presets, missing
  facts, and chronological ordering.
- The semantic contract belongs at the presentation-to-Compose boundary;
  Compose must continue to receive typed presentation values rather than
  provider or repository objects.

## Production boundary

- `app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt`:
  define or refine concise spoken summaries for current, hourly, daily, and
  Details presentation facts. Any new Details summary must preserve its group
  meaning and must not merge derived or historical values into current
  observation/forecast claims.
- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` and/or
  `app/src/main/java/com/oxygen/weather/ui/themeengine/components/`:
  expose the summaries through the existing page and row semantics without
  hiding important visible facts or changing page layout/navigation.
- Focused deterministic presentation tests and Compose semantics tests under
  `app/src/test/` and `app/src/androidTest/` for the existing Home rendering
  path.
- Evidence: `.codex/test-artifacts/151-spoken-semantics-contract/`.

No provider, domain, repository, cache, alert, persistence, or weather mapping
boundary is in scope.

## Functional invariants

- Preserve the named global pages and order: Now → Hourly → Daily → Details.
- Preserve all canonical values, selected location, provenance, freshness,
  forecast chronology/windows, unit conversion, missing-data behavior, and
  official-alert meaning. Semantics must describe the supplied presentation
  facts and must not infer a value.
- Summaries remain provider-neutral and include only facts already available in
  typed presentation models. Derived and historical Details groups remain
  explicitly identified as such.
- Hourly and Daily semantic row order remains earliest-to-latest and matches
  the visible supplied window. No hidden entries or fabricated horizon are
  added.
- Keep useful visible text and current interactive semantics. This slice does
  not redesign page composition or change navigation/control behavior.

## Implementation steps

1. Inspect existing `spokenSummary` builders, Details groups, semantics
   modifiers, and focused presentation/Compose tests. Record current outcomes
   for Metric, US, UK, and sparse inputs before changing the boundary.
2. Define concise expected spoken summaries for representative current,
   hourly, daily, and Details facts. Include missing required measurements and
   preserve group labels that distinguish current conditions, forecast
   patterns, and historical/reference context.
3. Implement only the missing/refined presentation summaries and wire them to
   existing Compose semantics. Avoid duplicate speech when a parent summary
   already represents the same child facts; preserve headings and chronological
   row traversal.
4. Add deterministic presentation assertions for Metric/US/UK unit wording,
   missing values, and exact summary content. Add Compose semantics assertions
   for all four Home pages, Details group identity, and earliest-to-latest
   Hourly/Daily row order using real presentation models.
5. Run focused JVM and connected semantics checks through the app's existing
   rendering path, then the relevant repository checks. Save exact results and
   any environment limitations under the cycle evidence directory.

## Acceptance criteria

- Current, Hourly, Daily, and Details expose concise spoken summaries for the
  important visible facts represented on each page.
- Summary values use the selected Metric, US, and UK units. Tests cover each
  preset for applicable temperature, wind, precipitation, and other displayed
  measurement facts.
- Missing measurements are spoken as unavailable (or omitted only where the
  visible presentation omits that optional fact); tests prove no missing value
  is spoken as zero or a plausible substitute.
- Details spoken output preserves group/metric labels and keeps derived and
  historical/reference facts distinguishable from current conditions.
- Compose semantics tests assert meaningful labels on all four Home pages and
  prove visible Hourly and Daily entries are exposed in chronological order.
- No navigation, visual composition, forecast content, provider/repository
  operation, or weather meaning changes as a side effect of semantics work.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/151-spoken-semantics-contract/`.

- `verification.md`: changed semantic contract, before/after summary examples,
  unit presets and sparse cases, Compose semantics tree/order observations,
  exact commands/results, emulator/API when applicable, and limitations.
- Focused presentation tests: exact current/hourly/daily/Details summary
  outcomes under Metric/US/UK, missing required and optional fields, and clear
  derived/historical group labels.
- Focused connected Compose tests: the real four-page Home composables expose
  expected summaries; Hourly/Daily rows traverse earliest-to-latest; Details
  labels preserve group identity; no duplicate parent/child speech obscures
  facts. Prefer existing focused test classes and add a narrow test class only
  if current boundaries make reuse unclear.
- Broader checks: `python scripts/dev.py test`, focused connected test via the
  repository developer entry point, `python scripts/dev.py contract`,
  `python scripts/dev.py workflow`, and `python scripts/dev.py check` when
  Android dependencies/device are available; `git diff --check` and final diff
  review. Record exact failures and do not attribute prior unrelated connected
  test failures to this cycle without evidence.
- No screenshot matrix is required by this semantic contract. If test
  diagnostics need a semantics dump or a capture to show traversal, retain it
  with the focused test output; visual resilience remains R6.2/R6.2A.

## Risks and assumptions

- Current/Hourly/Daily summaries already exist, so this may be primarily a
  focused contract and missing Details summary addition. Tests should expose
  actual gaps before changing stable copy.
- Compose semantics merging can cause duplicate or hidden child facts. Validate
  the merged tree from the actual page composables, not only isolated helpers.
- Existing copy is English. Localization is outside this roadmap slice; keep
  wording consistent with current product strings.
- Cycle 149 recorded unrelated failures in its full instrumentation run. Use
  focused classes to establish this slice's result and report exact broader
  check outcomes without weakening acceptance.
- This slice does not claim TalkBack service traversal/speech quality; R6.5
  remains the manual/service-level evidence closure.

## Out of scope

- Official-alert, Settings destination, selected/unavailable-control, or
  non-color-only status semantics beyond semantics needed for the four core
  Home page facts (R6.1A).
- Compact/large-font screenshot matrices, RTL layout verification, theme/layout
  combinations, reduced-motion invariance, and broad accessibility closure
  (R6.2–R6.5).
- TalkBack/manual service traversal or localization.
- Changes to provider/domain values, repository/cache behavior, forecast
  horizons, units/persistence, alerts, navigation, page layout, or visual
  treatment.
