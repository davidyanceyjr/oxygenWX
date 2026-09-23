# Architecture

The v1.0 UI candidate deliberately separates weather meaning from rendering so the visual rewrite can stabilize before networking and persistence return.

## Data flow

```text
provider / cache / historical source (future)
                |
                v
       provider-neutral domain
                |
        +-------+--------+
        |                |
        v                v
 historical/derived   canonical forecast
        |                |
        +-------+--------+
                v
        presentation mapper
                |
                v
       typed UI presentation
                |
                v
 Now -> Hourly -> Daily -> Details
```

## `data/`

Canonical weather models use metric units and provider-neutral condition identity. A
`WeatherLocation` pairs an opaque local identity with an IANA timezone and an optional display
name; neither field is a provider identifier. `DataProvenance` separately records a provenance
kind, optional opaque source identity/display name, and optional valid and retrieval `Instant`
values. Presentation applies the location timezone when formatting an available instant. Record
times for current and hourly weather remain location-local `LocalDateTime` values, and daily
records remain local `LocalDate` values; they are distinct from the absolute provenance instants.

Source-optional weather conditions and measurements are nullable canonical values. Present
numeric values must be finite, while absent values remain null rather than becoming a sentinel.
`WeatherBundle` preserves supplied chronological list order, permits duplicate timestamps/dates,
and rejects only decreasing chronology. `OfficialAlert` is a separate source record with required
official-alert provenance, so a forecast record cannot be presented as an official warning.

`WeatherRepositoryResult` carries a usable normalized bundle alongside live/cache origin,
freshness, optional refresh-failure facts, and cache-write outcome. These are domain facts, not
Compose loading/error states. `HomePresentationMapper.mapLoadState(...)` translates those facts
into an outer presentation-native load/refresh outcome for loading, live or cached data, refresh
failure with retained data, and failure without data. Data-bearing outcomes nest the exact
`mapState(...)` result, keeping refresh/cache facts independent from complete, partial, or
unavailable weather content. Status text and its accessibility summary are supplied together;
Compose should not parse formatted strings to recover meaning.
`DemoWeatherRepository` is a deterministic development fixture, not the eventual production
repository.

## `derived/`

`HistoricalSynthesis` owns experimental explanatory signals. These values are explicitly derived and never become observations, official alerts, or provider forecast products. Empirical percentile calculations require a real sample set rather than silently assuming a normal distribution.

## `presentation/`

The presentation layer owns text/unit formatting, page-window selection, concise spoken summaries, and grouping for Details. Its additive unit boundary converts canonical metric values for Metric/US/UK display without changing domain models or existing mapper output. Absolute temperature uses an offset conversion while temperature differences use scale only; pressure differences use the pressure conversion factor without an offset. Numeric formatting uses `Locale.ROOT` for stable decimal output and does not depend on device timezone. Applying unit presets to `HomePresentationMapper` is a later slice. The mapper's typed weather-content boundary distinguishes complete, partial, and whole-presentation unavailable weather data; field availability is typed separately from display text. Complete means at least 72 supplied hourly records and 10 supplied daily records, while any shorter usable horizon is partial. Usability requires a supplied current or forecast weather fact, not merely a timestamp, provenance, or derived/history context. `HomePresentationMapper.mapState(...)` maps weather-content state; `mapLoadState(...)` maps the outer load/refresh outcome and nests that exact content state whenever a bundle is supplied. The retained `map(...)` output is a source-compatible display adapter pending later application-state integration. Compose should not parse formatted strings to recover meaning.

## `ui/`

Compose owns layout, interaction, weather marks, atmospheric rendering, and accessibility semantics. The outer Home pager is the sole horizontal-swipe owner. Hourly/Daily window changes are explicit UI actions.

The installed renderer remains the UI-local Theme B sketch. Its existing
`ResolvedAppearance` resolver owns the semantic values consumed by current
components; `OxygenTheme` bridges those values into Material and provides them through
a composition boundary. This is the active app path and its defaults are unchanged.

The additive production theme foundation lives under `ui/themeengine/`. Its typed
catalog contains Atmospheric, Glass, Minimal OLED, Instrument, and Terminal
definitions. The pure resolver accepts theme identity, contrast, layout, and effects
inputs and returns a complete semantic palette, typography, geometry, render-style,
and motion policy. It has no weather, navigation, persistence, provider, or
Compose-runtime inputs, and app composition does not call it. Contrast resolves
through palette values; layout resolves through geometry; Effects Off resolves a
solid backdrop, no motion, and fully opaque panels and outlines. The root backdrop
and motion policy are not rendered by the new production family yet.

The approved future production boundary is:

```text
Theme selection + contrast + effects + system motion policy
                         |
                         v
             semantic resolved appearance
                         |
                         v
              theme-aware Compose components
                         |
                         v
             existing typed presentation models
```

Theme-aware Compose components consume the production resolver's resolved values with the existing typed
presentation models and semantic callbacks. Theme identity must not alter weather meaning,
page/navigation behavior, provenance, missing-data handling, or accessibility semantics.
Theme selection does not enter provider, repository, domain, derived-meteorology, or unit
conversion architecture and does not refetch weather. The existing sketch components
remain the current implementation until R0.11D–R0.11G migrate pages and cut over to the
production family. R0.11C adds the production shared core monitor components. Theme art remains under
`docs/assets/design-references/production-themes/`;
full boards are documentation references, not runtime resources.

The existing sketch monitor structures are `MonitorHeader`, `HomePageSelector`,
`MonitorSection`, and `ForecastWindowControls`. The forecast renderers are `MetricTile`,
`HourlyForecastTile`, and `DailyForecastRow`; they receive formatted strings or typed
presentation entries plus `ResolvedAppearance` only. Details shares `SourceFreshnessPanel`
and `InspectionMetricGroup`, which receive supplied source/update strings or typed
presentation groups plus `ResolvedAppearance` only. Page, pager, and forecast-window state
remains in `OxygenWeatherApp`.
The Hourly page composes the presentation-supplied local-date jumps and
six-entry window through these shared monitor components; its page and window
state remains in `OxygenWeatherApp`.
The Daily page composes each presentation-supplied five-day window through the
shared opaque section and `DailyForecastRow`, with explicit window controls;
its page and window state also remains in `OxygenWeatherApp`.
The Details page composes the supplied `SourceFreshnessPanel` followed by
ordered typed `MetricGroupPresentation` groups in a vertically scrollable
Theme B page; page and pager state remains in `OxygenWeatherApp`.
The Now page composes the supplied `CurrentPresentation`, source/update
context, current metric tiles, and the optional ordered Forecast pattern group
through these Theme B monitor components; page and pager state remains in
`OxygenWeatherApp`.
These sketch components receive presentation text/models, `ResolvedAppearance`, and
semantic callbacks only. Compose receives the resolved appearance and
presentation models, never a raw theme identifier, provider DTO, repository,
or persistence object.

The additive production core component family is under
`ui/themeengine/components/ProductionMonitorComponents.kt`. Its section surface,
page header/selector, current hero, metric tile, hourly entry, daily row, Hourly
date selector, and window controls receive `ResolvedTheme` explicitly with the
existing typed presentation fields and semantic callbacks. They do not depend on
the sketch's `ResolvedAppearance`, a `CompositionLocal`, raw theme IDs, or app
composition. `ProductionComponentsActivity` is merged only from `src/debug/`, has
no launcher filter, and prepares deterministic fixture presentations at the host
boundary; it does not change the normal `MainActivity` or Theme B sketch path.
Details/source groups remain R0.11CA work; marks and production backgrounds
remain R0.11CAA work; page migrations remain R0.11D–R0.11G work.

## Production expansion

Reintroduce features behind interfaces instead of into screen code:

- `WeatherRepository`
- forecast provider adapters
- alert provider adapters
- cache/persistence
- selected/saved location storage
- unit/appearance preferences
- historical reference provider

Provider DTOs and persistence entities should stop at their adapters. UI rendering should remain testable against deterministic presentation fixtures.
