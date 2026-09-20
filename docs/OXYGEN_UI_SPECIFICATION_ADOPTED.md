# Adopted Oxygen UI Specification — v1.0 Candidate

This document is the local implementation contract for the clean-room UI rewrite. It is based on the Oxygen prototype's UI/product specifications at the pinned upstream commit recorded in `upstream/OXYGEN_SOURCE_REFERENCE.md`, but is written for this codebase rather than copied from the upstream implementation.

## Product principle

Weather state may shape the atmosphere of the app, but decoration must never be required to understand the forecast. Data remains the interface; weather provides the visual atmosphere.

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

## Weather marks and atmosphere

Weather marks and procedural scenes express provider-neutral condition identity. They are supplemental. Adjacent text/semantics must communicate the same weather meaning.

The initial Oxygen visual references used by this candidate are:

| Role | Value |
| --- | --- |
| Sky Top | `#07151D` |
| Sky Bottom | `#153444` |
| Atmospheric Glow | `#86E4F0` |
| Glass | `#23414D` |
| Glass Strong | `#17313C` |
| Outline | `#7FC1CE` |
| Chart Accent | `#8DE7F1` |
| Precipitation | `#79BFFF` |

No downloaded weather photographs or runtime icon packs are required for the core experience.

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

Implemented in this first rewrite:

- Standard four-page pager.
- 72-hour/10-day demo presentation horizons.
- Hourly six-entry windows and date jumps.
- Daily five-day windows.
- New programmatic weather marks and atmospheric field.
- Typed presentation mapper.
- Visible source/update context.
- Derived/history separation in Details.
- Basic screen-reader summaries and semantic page tabs.
- An effective, debug-selectable Effects Off rendering path for installed verification. It is solid,
  opaque, static, and complete; it is not a persisted user preference.

Still separate future slices:

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
