# Plan 091 — Unit-aware presentation mapping

Status: Completed
Cycle ID: 091-unit-aware-presentation-mapping
Roadmap item: R1.3A
Created: 2026-09-30

## Objective and observable outcome

Apply the completed R1.3 `WeatherUnits` conversion and deterministic formatting
boundary to every numeric weather value already exposed by `HomePresentationMapper`.
For each Metric, US, and UK preset, the mapped current, hourly, daily, and Details
strings use the expected units; missing values remain unavailable. The canonical
bundle and derived inputs remain unchanged.

Observable outcome: deterministic mapper tests demonstrate the unit matrix across
the current summary and availability fields, one six-entry hourly window, one
five-entry daily window, and every existing Details group/value that has units.

## Production boundary

- `app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt`
- `app/src/test/java/com/oxygen/weather/presentation/HomePresentationTest.kt`
- Add or update only focused presentation test support if required.

Do not change Compose renderers, data/domain models, derived calculations,
provider/repository/cache behavior, settings, preference persistence, or unrelated
tests. Do not change `WeatherUnits.kt` unless a concrete missing R1.3 conversion
needed by an already-displayed mapper field is discovered; if so, update this plan
and the focused conversion tests before implementation.

## Functional invariants

- Canonical `WeatherBundle`, fixture values, and `DerivedWeather` remain unchanged.
- Mapping is pure presentation work. No network request, cache write, or weather
refetch is introduced.
- Missing inputs remain explicitly unavailable or omitted exactly as the current
field contract specifies; never substitute zero or a plausible value.
- Preserve conditions, chronology, timestamp/date labels, hourly six-entry and
daily five-entry windows, date jumps, provenance, freshness, and ready/partial/
unavailable state behavior.
- Convert absolute temperatures with `WeatherUnits.absoluteTemperature` and
temperature deltas with `WeatherUnits.temperatureDifference`; do not treat a
delta as an absolute temperature.
- Use the existing `UnitPreset` behavior: Metric and UK share conversions where
`WeatherUnits` defines them; US uses its defined customary units. Do not infer
regional conventions beyond the existing API.
- The existing two-argument `map` and `mapState` callers retain their current
default output (Metric). Add an optional preset default or equivalent
compatibility-preserving overload; no preference source is added in this cycle.
- Percentages, compass direction, categorical conditions, labels, time, source,
provenance, freshness, and unitless derived indexes retain their existing meaning.
- Preserve existing public presentation model shape unless a necessary typed
boundary change is required; do not add data solely to improve a screenshot.

## Existing displayed-field inventory

The implementation and tests must account for all current mapper output below:

- **Now/current:** temperature, apparent temperature, dew point, humidity,
  next-six-hour precipitation probability and summed amount, wind speed, gust,
  and direction. The precipitation headline/supporting copy and spoken summary
  must agree with those same converted values.
- **Hourly:** each entry's temperature, precipitation probability, and amount;
  condition and chronology remain unchanged.
- **Daily:** low/high temperatures, precipitation probability, and amount;
  precipitation copy and spoken summary remain consistent.
- **Details / Conditions:** feels-like temperature, humidity, dew point, pressure,
  cloud cover, and visibility.
- **Details / Forecast pattern:** three-hour temperature difference, pressure
  difference, and unitless persistence/volatility indexes.
- **Details / Historical context:** seasonal percentile, temperature departure
  from normal as a temperature difference, pressure departure as a pressure
  difference, analog years, and reference label.

This inventory does not authorize adding currently absent UI fields such as
precipitation rate or sunshine duration.

## Implementation steps

1. Inspect every formatter and `fieldAvailability` construction site in
   `HomePresentation.kt`; record the existing default assertions and fixture
   facts that must remain stable.
2. Thread `UnitPreset` through `map` and `mapState` with Metric as the
   compatibility default. Keep internal helpers explicit about whether a value
   is an absolute temperature, a temperature difference, a speed, a pressure,
   a pressure difference, a distance, a precipitation amount, a percentage, or
   a direction.
3. Replace hard-coded unit strings/rounding only for existing displayed numeric
   weather fields, using `WeatherUnits` typed conversions and formatters. Ensure
   headlines, supporting strings, typed availability fields, and spoken summaries
   describe the same converted value. Preserve current percent clamping/display
   behavior unless that conflicts with the established `WeatherUnits` contract;
   document and test any resolved discrepancy in the implementation.
4. Update `HomePresentationTest` with deterministic Metric/US/UK assertions for
   current, hourly, daily, and Details coverage from the inventory above. Include
   known-zero versus missing distinction, nullable fields, temperature deltas,
   pressure deltas, and unchanged percentage/direction/index semantics.
5. Confirm legacy two-argument calls still produce the same Metric fixture facts,
   including provenance, labels, chronology, grouping, and availability. Run the
   focused test and required workflow/diff checks; record exact outcomes under
   the evidence directory.

## Acceptance criteria

- A deterministic mapper test matrix exercises Metric, US, and UK for current
  values, a six-entry hourly window, a five-entry daily window, and all existing
  unit-bearing Details metrics.
- Tests cover null/unavailable values and ensure known zero remains available;
  absolute temperature and temperature differences use distinct conversions.
- Display strings, `PresentationField` availability text, and spoken summaries
  do not contradict each other about converted values.
- Existing two-argument callers compile and retain Metric output.
- Existing fixture facts change only where their numeric unit/format is expected
  to change; canonical and derived values, ordering, provenance, and grouping do
  not change.
- No UI, storage, provider, cache, or refetch behavior is introduced.

## Verification and evidence

Run from the repository root:

1. Focused: `./gradlew :app:testDebugUnitTest --tests`
   `com.oxygen.weather.presentation.HomePresentationTest` (the Python entry
   point does not expose Gradle test filters).
2. Broader regression: `python scripts/dev.py test`.
3. Lifecycle/contract: `python scripts/dev.py workflow`.
4. Final hygiene: `git diff --check`; inspect the final diff for boundary and
   fixture invariance.

Preserve command lines, pass/fail results, and any unavailable checks in
`.codex/test-artifacts/091-unit-aware-presentation-mapping/verification.md`.
This is a presentation-string/data-mapping slice with no layout or renderer
change; installed screenshots, viewport/font-scale, RTL, and effects checks are
not applicable. If implementation changes Compose rendering or visual
composition, stop and revise the plan before broadening scope.

## Risks and assumptions

- **Dependency satisfied:** R1.3 conversion boundary is DONE; its plan/history
  and `WeatherUnitsTest` define supported units and deterministic formatting.
- **Default assumption:** existing two-argument mapper behavior is Metric, as
  demonstrated by the deterministic capture fixture. Keep that default until a
  later preference-integration slice supplies a selected preset.
- **Details risk:** source, derived, and historical values have different
  semantics and units. Convert numeric values according to the R1.3 type while
  preserving the grouping and provenance semantics.
- **Formatting risk:** current mapping uses field-specific rounding and percent
  clamping. The implementation must compare those rules with `WeatherUnits`
  and preserve product contract consistency; do not silently change percentage
  meaning or create locale-sensitive formatting.
- **Owner input:** none is currently required. If an existing displayed value's
  canonical unit or intended R1.3 type cannot be established from source/model
  definitions and repository docs, record the ambiguity and request owner input
  before inventing a conversion.
- Roadmap bookkeeping needs no change: DX.4 is already DONE with history 083,
  and R1.3A is already NEXT.

## Out of scope

- Unit selection/settings or preference persistence (R5.1).
- Locale-sensitive formatting or localization.
- New weather fields, conversion APIs, or changes to canonical units.
- Compose layout, visual redesign, accessibility redesign, or installed visual
  acceptance work.
- Provider, repository, refresh, cache, network, and weather-fetch behavior.
- Historical/derived algorithm changes or reclassification of data meaning.
