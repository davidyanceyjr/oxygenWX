# Effects contract — owner-approved track

This contract extends the completed five-theme design-pack and renderer work. It
governs future effects planning and implementation. The five existing themes
remain **Atmospheric, Glass, Terminal, Instrument, and Minimal OLED**; Daybreak
and its exploratory page composition are not adopted.

## Levels

The user-facing level names are **None**, **Subtle**, and **Full**. `None` is a
label for the existing internal `ThemeEffectsLevel.OFF` identity; this wording
does not require renaming that enum, changing stored preference values, or
migrating user preferences. The current appearance screen still displays
“Effects off”; the approved None wording is a presentation-label correction
for FX.09, with the stored `off` identifier preserved.

- **None:** pleasing, simple, opaque, static, and complete. Decorative artwork,
  weather marks, textures, gradients, glow, and animation are absent. Solid
  theme colors, typography, spacing, functional controls, and the existing
  readable grouping of facts establish hierarchy. Required weather meaning
  remains visible as text and accessible semantics. Functional control icons
  remain when needed to identify or operate a control.
- **Subtle:** clearly visible but restrained gradients, decorative weather
  marks, background elements, textures, and static artwork. It adds visual
  character without competing with weather information. This level is static;
  incidental functional transitions remain governed by the existing motion
  policy.
- **Full:** richer artwork, lighting, depth, and controlled motion, targeting a
  polished, near-game-quality 2D presentation within Android Compose. Direct
  attention through composition, whitespace, focal hierarchy, directional
  ornament, and lighting. Do not add enclosing boxes around weather data to
  create richness. Menus and controls may use solid readable surfaces. Critical
  text remains sharp and stationary. Decorative lines, glyphs, contours, and
  motion never serve as quantitative weather indicators.

Visual ambition is a target, not evidence of acceptance. Full acceptance
requires installed-app evidence and measured performance; compilation and
previews alone do not establish it.

## Palette and theme identity

Effect recipes define geometry, opacity, intensity, layering, texture, and
motion. They do not define fixed hues. Every chromatic effect color comes from
semantic roles in the active theme palette; neutral light/shadow operations may
be used where appropriate. Shared rendering primitives must not bake in gold
sunlight, blue clouds, green phosphor, or other fixed effect colors.

Palette independence does not require identical artwork between themes. Theme
recipes preserve their own visual identity:

- Atmospheric: sky fields, cloud forms, light, and atmospheric depth.
- Glass: reflections and layered illumination.
- Terminal: phosphor texture, glyph art, and directional lighting.
- Instrument: technical ornament and illuminated hierarchy.
- Minimal OLED: selective accents and light over predominantly black fields.

Effect geometry and texture remain supplemental decoration. They cannot add
data, imply measurements, or carry condition, warning, chronology, or status
meaning by color or motion alone. Weather marks that appear in Subtle or Full
must remain decorative and must be suppressed in None.

## Preference, contrast, and motion

Reuse the existing persisted effects preference, pure resolver, reduced-motion
policy, and ambient renderer where appropriate. Extend these completed systems;
do not repeat their implementation or rewrite a saved choice when a device,
theme, contrast setting, or performance fallback changes the effective result.
High contrast and reduced motion are independent overlays on the selected theme
and effects preference. Effective fallback may reduce or remove effects but
does not alter the saved preference, weather, or accessibility meaning.

Full motion is considered only after that theme's static Full treatment passes
installed acceptance and lifecycle controls establish that inactive pages and
backgrounded apps stop drawing. Reduced motion disables motion while retaining
the selected static appearance level when readable and performant.

## Functional invariants

Effects must not change:

- weather values, units, condition identity, or meteorological meaning;
- source, provenance, freshness, or valid/update times;
- forecast chronology, membership, or real horizon length;
- distinctions among measurements, derived signals, and historical context;
- navigation semantics or accessibility meaning;
- forecast/alert request counts, cache contents, or cache operations.

Do not invent values, forecast entries, art-supported measurements, warning
language, or unsupported condition treatments. Preserve visible semantic
distinctions and functional grouping even when decorative containers are
removed. Decoration never replaces a readable label or accessible description.

## Track and evidence

The bounded sequence is tracked in [`UI_CONTEXT_ROADMAP.md`](../UI_CONTEXT_ROADMAP.md)
as FX.01–FX.40 mapped one-to-one to UI.3–UI.42. FX.03–FX.07 must audit
available source art and record missing measurements or theme recipes. The
cycle 169 input archive contains exploratory HTML previews for Atmospheric and
Terminal; they can inform recipe discussion but do not establish approved
reference states, measured geometry, or installed-app results. Its Daybreak
concept is excluded. These previews also cannot supply weather values or
product requirements. Theme-specific installed acceptance starts only after
page integration.
Palette substitution, semantic/request/cache invariance, device/API performance,
reduced-motion, lifecycle, fallback, and exact limitations have dedicated
bounded outcomes.

The owner-approved bottom-corner navigation, page chooser, and Search/Settings/
About menu are separate approved intent awaiting their own bounded planning.
They are not effects requirements and are not part of FX.01–FX.40.
