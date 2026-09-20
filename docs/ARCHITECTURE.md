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

Canonical weather models use metric units and provider-neutral condition identity. Provenance identifies source and semantic data class. `DemoWeatherRepository` is a deterministic development fixture, not the eventual production repository.

## `derived/`

`HistoricalSynthesis` owns experimental explanatory signals. These values are explicitly derived and never become observations, official alerts, or provider forecast products. Empirical percentile calculations require a real sample set rather than silently assuming a normal distribution.

## `presentation/`

The presentation mapper owns text/unit formatting, page-window selection, concise spoken summaries, and grouping for Details. Compose should not parse formatted strings to recover meaning.

## `ui/`

Compose owns layout, interaction, weather marks, atmospheric rendering, and accessibility semantics. The outer Home pager is the sole horizontal-swipe owner. Hourly/Daily window changes are explicit UI actions.

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
