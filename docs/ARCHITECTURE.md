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

The installed app renderer now uses the production `ResolvedTheme` component
family. `MainActivity` owns and persists the five-theme selection, defaulting to
Atmospheric when storage has no recognized choice. `OxygenWeatherApp` resolves
the supplied theme ID and owns the outer pager, page-window state, and
navigation callbacks. Theme changes affect presentation only and do not
refetch weather.

The additive production theme foundation lives under `ui/themeengine/`. Its typed
catalog contains Atmospheric, Glass, Minimal OLED, Instrument, and Terminal
definitions. The pure resolver accepts theme identity, contrast, layout, and effects
inputs and returns a complete semantic palette, typography, geometry, render-style,
and motion policy. It has no weather, navigation, persistence, provider, or
Compose-runtime inputs. App composition selects the requested theme and calls
the pure resolver. High contrast resolves palette roles against the opaque
backgrounds used by current components; layout resolves through geometry; and
effects resolve backdrop/motion and panel/outline opacity.

High contrast uses the approved catalog `content` and `secondaryData` colors.
It verifies primary/content and precipitation text at 4.5:1 on actual opaque
component backgrounds. Supporting text keeps its catalog color only where it
reaches 7:1 on every background where that role is rendered; otherwise the
resolved `secondaryData` role is promoted to resolved `content`, which must
still meet 4.5:1. The finite current inventory includes bare hero/selector
content on `canvas` and page headers/theme-picker text on the catalog
`atmosphereTop`/`atmosphereBottom` backdrop endpoints; the theme menu outline
on `surface` where its width is non-zero; grouped sections on
`surface` (Atmospheric/Instrument), `elevatedSurface` (Glass), or `canvas`
(Minimal OLED/Terminal); disabled-button content on `elevatedSurface`; and the
Instrument/Terminal hero divider on its grouped-section background. A section
outline is included only where `panelBorderWidth` is non-zero. The selected
page text is checked over its translucent action tint composited on each
declared backdrop endpoint and `canvas`;
selected date text and outline use the backdrop endpoints; action-button text is
checked against the opaque `action` fill. Warning/danger are not current
production text roles.

Every resolved High contrast foreground pair is measured with WCAG 2.x sRGB
linearization, alpha compositing, and `(Llighter + 0.05) / (Ldarker + 0.05)`.
Text requires 4.5:1; supporting text uses the 7:1-or-promote policy; rendered
outlines require 3:1 against their actual adjacent opaque backgrounds. A failed
action foreground/border role is promoted to `content`; action-button text uses
whichever of resolved `content` or `canvas` has higher contrast against the
opaque action fill. The resolver rechecks each result and fails closed if the
catalog content role cannot satisfy the required floor. It never mutates the
canonical `ThemeDefinition` or Standard palette.

Standard/Simple changes geometry only and keeps the 48 dp target minimum.
Effects retain their requested identity and declared backdrop/motion behavior,
including the catalog fallback for themes without Full-motion support. High
contrast sets panel and outline opacity to `1f` at every effects level. Effects
Off has final precedence for a solid canvas, no motion, and opaque panels and
outlines for every contrast/layout choice. These axes do not alter weather
meaning or introduce runtime state into resolution. All 60 combinations are
covered by deterministic resolver tests.

The approved JSON theme catalog is a checked design input; typed Kotlin in
`ThemeCatalog.kt` remains runtime authority. Run `python scripts/dev.py catalog`
to validate its schema and compare palette roles (including the intentional
`content` to `primaryData`, manifest `accent` to `action`, and `canvas` to
`actionContent` mappings), effective panel opacity/radius/border and spacing,
preferred motion/full-motion support, theme identity/name, and manifest
backdrop/surface/weather-mark styles. Manifest typography is descriptive
metadata and is not compared with Kotlin typography definitions.

The production boundary is:

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
conversion architecture and does not refetch weather. The production renderer now
covers all four pages and all five built-in visual systems. Its production component
family owns the rendered surfaces and marks; the old Theme B sketch remains only as
unused historical implementation code pending safe cleanup. Theme art remains under
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
boundary; it supplements installed review of the normal app.
Production Details/source components are implemented in
`ProductionDetailsComponents.kt`; they render supplied source/update strings and
ordered `MetricGroupPresentation` groups. The isolated debug showcase exercises
complete, sparse, and long-text fixture states. The normal app also uses these
components. `ProductionWeatherMark` and
`ProductionBackdrop` add provider-neutral decorative marks and resolved static
ambient backgrounds through this same boundary. The mark accepts the existing nullable
`WeatherMarkCondition`; all six conditions and all current mark styles have
explicit rendering paths, while null draws no mark and mark semantics are
cleared so adjacent supplied text carries the meaning. The backdrop consumes
the resolved solid/tonal base, overlay family, and overlay strength, draws
before caller content, and does not own input. Effects Off resolves to an
opaque solid canvas with no overlay; enabled backgrounds use static gradients,
glows, grids, or scan lines. Animation remains future work. All five theme mappings and
the four-page production renderer are exercised in the installed app; the
debug showcase continues to provide focused component fixtures.

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
