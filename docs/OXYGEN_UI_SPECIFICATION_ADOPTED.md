# Adopted Oxygen UI Specification — v1.0 Candidate

This document is the local implementation contract for the clean-room UI redesign.
It supersedes the visual direction of the earlier Oxygen prototype and its Base
Art Sheet v0.2. The upstream material recorded in
`upstream/OXYGEN_SOURCE_REFERENCE.md` is historical research, not a visual
reference or a screen-composition source. The normal four-page app still uses the
Theme B implementation sketch; its pages have not migrated and it is not the
production visual acceptance target. The additive production resolver and R0.11C
core monitor components are implemented and verified through an isolated debug
showcase. The R0.11CA Details/source components are also implemented in that
isolated host. These production components are not yet referenced by normal app
composition.

## Product principle

Oxygen is a modern, themed, component-based weather monitor. Weather state may
shape the atmosphere of the app, but decoration must never be required to
understand the forecast. Data remains the interface; visual treatment helps
users recognize state, scan information, and reach useful detail.

## Production visual direction

The production visual system is a shared component family with five built-in
presentations:

- **Atmospheric** — immersive procedural weather field with restrained container emphasis.
- **Glass** — layered, restrained translucent surfaces over a procedural atmosphere.
- **Minimal OLED** — black-first, low-decoration, typography- and data-dominant presentation.
- **Instrument** — technical monitor treatment with bounded, source-supported data indicators.
- **Terminal** — flat, console-like presentation with monospace typography.

Atmospheric is the initial implementation target. This does not establish a persisted
user default; persistence and selection are later roadmap work. The personalities and
shared component references are indexed in
[`docs/assets/design-references/production-themes/`](assets/design-references/production-themes/).
They guide visual exploration and component intent, not pixel-exact measurements or
mandatory screen composition. Start with the [One App. Many Personalities board](assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png), [Glass asset sheet](assets/design-references/production-themes/glass/boards/glass_theme_asset_sheet.png), and [Instrument asset sheet](assets/design-references/production-themes/instrument/boards/instrument_theme_asset_sheet.png); the full reference set is listed in the theme-system asset manifest.

Use semantic Compose components, procedural drawing, and vectors where practical. Do
not paste full art-board screenshots into runtime UI, add large concept boards to Android
resources, or invent weather data to fill a reference composition. Glass as a local
production theme is distinct from deprecated upstream dark-glass/gold dashboard examples.
Where an image conflicts with product, accessibility, meteorological, navigation, or
data-semantics requirements, the written repository authority wins.

The earlier selected Theme B reference images remain historical records of the current
implementation sketch, not production acceptance targets:

- `docs/assets/design-references/oxygen-theme-b-storyboard-v1.png`
- `docs/assets/design-references/oxygen-theme-b-base-pages-v1.png`

## Standard Home page model

```text
Now -> Hourly -> Daily -> Details
```

The outer Home pager is the sole horizontal-swipe owner. Static taps do not advance pages. Android Back from a non-Now page moves to the previous global page; Back from Now uses normal host behavior.

## Forecast horizons

The target presentation accepts:

- up to 72 chronological hourly forecast entries;
- up to ten chronological daily entries.

Provider/repository layers must preserve real timestamps and dates. UI code must not pad, interpolate, repeat, or fabricate forecast entries to fill a visual window.

### Hourly

- Six actual chronological entries per visible window.
- Stable two-column by three-row compact composition at normal phone sizes.
- Each entry exposes time, condition text/mark, temperature, and precipitation probability when available.
- Earlier/Later changes exactly one six-entry window.
- One visible control per represented local date jumps to the first window containing that date.
- No nested horizontal pager.

### Daily

- Five actual chronological days per visible window.
- Two windows for a complete ten-day horizon.
- Each row exposes date label, condition, numeric low/high, and precipitation meaning.
- Earlier/Later changes exactly one five-day window.

## Now

Now should answer "what is happening?" before secondary detail. Current temperature and condition receive the strongest hierarchy. Supporting values such as apparent temperature, humidity, dew point, near-term precipitation, wind, source type, and update time remain readable and text-equivalent.

## Details

Details is the audit surface. Source-normalized measurements are grouped separately from derived forecast-pattern and historical-context signals. A derived metric cannot masquerade as an observation, provider forecast, or official product.

## Card/component boundary

Reusable UI surfaces receive typed presentation data and semantic callbacks. They do not receive provider DTOs, repositories, persistence objects, HTTP clients, or raw theme identifiers. Formatting and unit conversion belong upstream of rendering.

## Weather marks and monitor states

Weather marks, state indicators, and optional procedural atmosphere express
provider-neutral condition identity and monitor state. They are supplemental.
Adjacent text/semantics must communicate the same weather meaning.

The following roles are semantic appearance inputs, not a prescribed legacy
palette. Their concrete values are selected by the active theme:

| Role | Intended use |
| --- | --- |
| Canvas | app background and broad weather-state field |
| Surface / elevated surface | readable grouped information and interaction |
| Primary data | current temperature and page-primary facts |
| Secondary data | supporting measurements and provenance |
| Condition / trend | weather marks and data-monitor indicators |
| Status | loading, stale, unavailable, selected, and alert states |
| Action | discoverable routes to additional information |

No downloaded weather photographs or runtime icon packs are required for the core experience. Do not recreate a visual treatment merely because it appears in the deprecated art sheet.

## Theme/effects contract

Presentation configuration must not change weather semantics. The long-term resolver is conceptually:

```text
Theme selection + contrast + effects + system motion policy
    -> semantic resolved appearance
    -> theme-aware Compose components
    -> existing typed presentation models
```

Effects Off must resolve to an opaque, static, complete interface. High contrast is an overlay on a selected theme rather than a separate weather interpretation.

## Availability and honesty

Each page must have an explicit answer for loading, ready, cached/stale, partial horizon, missing field, and unavailable data as those states are added. Missing data is never represented by zero unless the source actually reports zero.

## Accessibility

- Important facts are visible text plus meaningful semantics.
- Decorative marks/scenes may be hidden from accessibility only when equivalent adjacent semantics exist.
- Forecast entries expose concise spoken summaries.
- Interactive targets should be at least 48dp where applicable.
- Chronology remains earliest-to-latest in both LTR and RTL.
- Controls should be named by meaning (for example, Earlier/Later) instead of depending on arrow direction alone.
- Large text may use localized overflow/scrolling rather than clipping or shrinking critical content beyond readability.

## Current implementation sketch/baseline

The following describes the currently implemented Theme B sketch for repository context.
It is historical implementation state and is not a production visual acceptance target:

- Standard four-page pager.
- 72-hour/10-day demo presentation horizons.
- Hourly six-entry windows and date jumps.
- Daily five-day windows.
- New programmatic weather marks and atmospheric field.
- Typed presentation mapper.
- Visible source/update context.
- Derived/history separation in Details.
- Basic screen-reader summaries and semantic page tabs.
- A fixed development-default Theme B `ResolvedAppearance` boundary currently
  owns semantic colors, typography, shared layout/shape values, the Material
  bridge, and Off/Subtle effects resolution. It remains sketch implementation
  pending the planned production resolver and cutover.
- Shared structural monitor components are implemented: the neutral header,
  named Home page selector, opaque section surface, and explicit forecast-window
  controls. The header carries visible page identity plus supporting
  presentation text; on Now, that supporting text carries source/update
  context.
- Shared forecast monitor components are implemented: `MetricTile`,
  `HourlyForecastTile`, and `DailyForecastRow`. They render supplied
  presentation strings/entries and resolved appearance values; page-specific
  composition and state remain owned by the page functions.
- The current-sketch Hourly page composition is implemented: it presents every
  supplied local-date control, a two-column six-entry forecast grid, and
  explicit Earlier/Later controls. Date controls expose their represented date
  and selected state through semantics, while entries retain visible text and
  concise spoken summaries.
- The current-sketch Daily page composition is implemented: it presents the supplied
  five-day window in an opaque monitor section with visible range identity,
  separated forecast rows, and explicit Earlier/Later controls. Rows retain
  visible date, condition, low/high, precipitation, and concise spoken summary
  semantics; sparse windows are rendered without padding.
- Shared Details monitor components are implemented: `SourceFreshnessPanel`
  presents supplied source/update text as explicit inspection facts, and
  `InspectionMetricGroup` renders the supplied semantic group boundary and
  metric values. The current-sketch Details composition is implemented as a
  vertically scrollable page with the source/freshness surface first, followed
  by ordered Conditions, Forecast pattern, and Historical context groups.
  Important facts remain visible text and semantics; no chart or trend-series
  data is invented.
- The current-sketch Now composition is implemented with a visible `Now` identity,
  location/source/update context, dominant current temperature and condition,
  readable apparent/humidity/dew-point, precipitation and wind facts, and the
  optional complete ordered Forecast pattern group. Visible text and inspected
  semantics carry the same meaning as the supplied presentation models; no
  chart or alert content is invented.
- An effective, debug-selectable Effects Off rendering path for installed verification. It is solid,
  opaque, static, and complete; it is not a persisted user preference.

Still separate future slices:

- live provider/repository/cache path;
- official alerts;
- location and saved locations;
- unit preferences;
- persisted selection among Atmospheric, Glass, Minimal OLED, Instrument, and Terminal;
- high contrast preference;
- persisted Off/Subtle/Full effects preference;
- Simple layout;
- installed screenshot/accessibility evidence matrix;
- release signing/publication.
