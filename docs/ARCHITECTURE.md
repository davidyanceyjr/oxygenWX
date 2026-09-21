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
Compose loading/error states; the presentation-state mapping belongs to its later roadmap slice.
`DemoWeatherRepository` is a deterministic development fixture, not the eventual production
repository.

## `derived/`

`HistoricalSynthesis` owns experimental explanatory signals. These values are explicitly derived and never become observations, official alerts, or provider forecast products. Empirical percentile calculations require a real sample set rather than silently assuming a normal distribution.

## `presentation/`

The presentation mapper owns text/unit formatting, page-window selection, concise spoken summaries, and grouping for Details. Compose should not parse formatted strings to recover meaning.

## `ui/`

Compose owns layout, interaction, weather marks, atmospheric rendering, and accessibility semantics. The outer Home pager is the sole horizontal-swipe owner. Hourly/Daily window changes are explicit UI actions.

Shared rendering consumes the UI-local `ResolvedAppearance` contract. The fixed
Theme B resolver owns semantic colors, typography, shared layout/shape values,
and effects resolution; `OxygenTheme` bridges those values into Material and
provides the contract through a composition boundary. The shared monitor
structures are `MonitorHeader`, `HomePageSelector`, `MonitorSection`, and
`ForecastWindowControls`. The forecast renderers are `MetricTile`,
`HourlyForecastTile`, and `DailyForecastRow`; they receive formatted strings
or typed presentation entries plus `ResolvedAppearance` only. Page, pager, and
forecast-window state remains in `OxygenWeatherApp`. These components receive
presentation text/models, `ResolvedAppearance`, and semantic callbacks only.
Compose receives the resolved appearance and presentation models, never a raw
theme identifier, provider DTO, repository, or persistence object.

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
