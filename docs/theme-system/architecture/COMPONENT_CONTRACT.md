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
- typed effects geometry, opacity, intensity, layering, texture, and motion policy;
- semantic color-role bindings for every chromatic effect.

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
- Shared effects primitives receive resolved parameters and semantic theme colors;
  they do not embed fixed chromatic hues. Theme identity belongs in the selected
  recipe, not weather/data behavior.
- Effects None suppresses decorative weather marks/art as well as backdrop
  effects; required control icons, visible facts, functional grouping, and
  accessibility semantics remain. The UI label None does not rename internal
  `ThemeEffectsLevel.OFF` or require preference migration.
- Full effects use composition, whitespace, artwork, light, and depth. Do not
  add enclosing data panels solely to make Full richer. Keep critical text
  stationary and sharp; menus and controls may retain solid readable surfaces.
- Decorative content is accessibility-hidden only when adjacent text has equivalent meaning.
- Minimum touch target rules remain intact.
- No invented charts/gauges unless the presentation layer supplies the required numeric/series
  semantics. Instrument-style graphics must not manufacture measurements from formatted strings.
