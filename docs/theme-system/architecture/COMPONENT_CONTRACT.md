# Production Component Contract

The production visual system should expose a small theme-independent component vocabulary.
Names may adapt to repository conventions; responsibilities should not drift.

## Semantic appearance

Resolved roles should cover at least:

- canvas/background;
- primary/secondary data;
- surface/elevated surface;
- outline/divider;
- action/selected/inactive/status;
- condition and precipitation accents;
- typography roles;
- page/section spacing;
- surface geometry;
- surface/background/hero/weather-mark render styles;
- motion/effects policy.

## Shared components

- page identity/header;
- global page selector;
- current-condition hero;
- metric fact/tile;
- hourly forecast entry;
- daily forecast row;
- forecast window controls;
- source/freshness inspection panel;
- Details metric group;
- weather condition mark;
- root/background renderer.

## Constraints

- Presentation models and semantic callbacks only.
- No repositories/provider DTOs/network/persistence.
- No raw theme-ID branches that change content or interaction semantics.
- Decorative content is accessibility-hidden only when adjacent text has equivalent meaning.
- Minimum touch target rules remain intact.
- No invented charts/gauges unless the presentation layer supplies the required numeric/series
  semantics. Instrument-style graphics must not manufacture measurements from formatted strings.
