# Adopted Oxygen UI Specification — v1.0 Candidate

This document is the local implementation contract for the clean-room UI redesign.
It supersedes the visual direction of the earlier Oxygen prototype and its Base
Art Sheet v0.2. The upstream material recorded in
`upstream/OXYGEN_SOURCE_REFERENCE.md` is historical research, not a visual
reference or a screen-composition source.

## Product principle

Oxygen is a modern, themed, component-based weather monitor. Weather state may
shape the atmosphere of the app, but decoration must never be required to
understand the forecast. Data remains the interface; visual treatment helps
users recognize state, scan information, and reach useful detail.

## v1 visual direction

The Home experience is four purposeful weather-data screens, not a copied
dashboard: Now, Hourly, Daily, and Details. Each screen uses a coherent set of
theme-resolved components for hierarchy, state, and navigation.

- **Aesthetic and functional:** calm, contemporary information design with
  strong typographic hierarchy, intentional spacing, and a clear action model.
- **Themed components:** surfaces, type, status treatments, weather marks,
  charts/indicators, and controls consume semantic appearance tokens. A theme
  changes their presentation consistently without changing weather meaning.
- **Visual weather monitor:** compact indicators and weather-state treatments
  communicate conditions, trends, freshness, availability, and selected state;
  visible text and semantics always provide the equivalent meaning.
- **Progressive detail:** a weather fact, summary, or indicator may link to a
  relevant inspection surface when that link has a clear label, target, and
  navigation outcome. It must not conceal required information or compete with
  the primary page task.
- **Gestures with feedback:** horizontal swiping remains owned by the outer
  Home pager. Any other gesture must have an explicit affordance and immediate,
  accessible state feedback; it cannot be the only route to weather data or a
  core action.

### Deprecated visual language

Do not use the upstream Base Art Sheet v0.2, its photographic weather scenes,
dark-glass/gold card examples, or its dashboard composition as an implementation
or review target. Those materials are deprecated in this repository. Likewise,
the earlier local screenshot baselines remain historical verification evidence,
not acceptance references for the redesigned UI.

### Selected visual references

The initial selected direction is **Theme B — dark data monitor**. These
project-local, non-runtime assets record the selected concept and its base page
anatomy for future visual review:

- `docs/assets/design-references/oxygen-theme-b-storyboard-v1.png` — four-page
  storyboard showing the shared theme across Now, Hourly, Daily, and Details.
- `docs/assets/design-references/oxygen-theme-b-base-pages-v1.png` — the base
  component composition for those four pages before later interaction or visual
  refinement.

They establish direction and component intent, not pixel-exact implementation
measurements. Product, accessibility, data-semantics, and navigation contracts
remain authoritative where an image is ambiguous.

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
Theme + contrast + effects + system motion policy
    -> resolved appearance
    -> Compose rendering
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

## Current candidate scope

Historical implementation baseline (not a visual acceptance target):

- Standard four-page pager.
- 72-hour/10-day demo presentation horizons.
- Hourly six-entry windows and date jumps.
- Daily five-day windows.
- New programmatic weather marks and atmospheric field.
- Typed presentation mapper.
- Visible source/update context.
- Derived/history separation in Details.
- Basic screen-reader summaries and semantic page tabs.
- A fixed development-default Theme B `ResolvedAppearance` boundary owns the
  current semantic colors, typography, shared layout/shape values, Material
  bridge, and Off/Subtle effects resolution.
- Shared structural monitor components are implemented: the neutral header,
  named Home page selector, opaque section surface, and explicit forecast-window
  controls. The header carries visible page identity plus supporting
  presentation text; on Now, that supporting text carries source/update
  context.
- Shared forecast monitor components are implemented: `MetricTile`,
  `HourlyForecastTile`, and `DailyForecastRow`. They render supplied
  presentation strings/entries and resolved appearance values; page-specific
  composition and state remain owned by the page functions.
- Shared Details monitor components are implemented: `SourceFreshnessPanel`
  presents supplied source/update text as explicit inspection facts, and
  `InspectionMetricGroup` renders the supplied semantic group boundary and
  metric values. They do not create chart or trend-series data; the Details
  page-specific Theme B composition remains future work.
- An effective, debug-selectable Effects Off rendering path for installed verification. It is solid,
  opaque, static, and complete; it is not a persisted user preference.

Still separate future slices:

- application of the Theme B system to each page, including page-specific
  forecast composition and Details monitor components;

- live provider/repository/cache path;
- official alerts;
- location and saved locations;
- unit preferences;
- persisted Oxygen/Paper/Terminal themes;
- high contrast preference;
- persisted Off/Subtle/Full effects preference;
- Simple layout;
- installed screenshot/accessibility evidence matrix;
- release signing/publication.
