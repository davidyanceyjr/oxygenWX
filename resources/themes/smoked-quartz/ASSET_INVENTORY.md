# Smoked Quartz — asset inventory

**Product theme:** Glass
**Art direction:** Smoked Quartz, with a mineral-strata material language
**Status:** Design worksheet accepted for continued asset definition; the full
theme package is not yet signed off.
**Worksheet:** [`asset-worksheet-v1.png`](asset-worksheet-v1.png) ·
[`SVG source`](asset-worksheet-v1.svg)
**Asset overview:** [`asset-pack-overview.png`](asset-pack-overview.png) ·
[`SVG source`](asset-pack-overview.svg)

This worksheet studies a Compose-native procedural background for the Now
page. It demonstrates gradients, layered sediment curves, sparse grain,
highlights, glass surfaces, a weather mark, palette, type direction, and a
motion cue. It is a design mockup, not a screenshot of the installed app.

## Current concept decisions

- Use abstract smoked mineral / fine dust language, rather than literal rock or
  sand photography.
- Explore generated backgrounds with Compose drawing primitives and restrained
  contrast. Keep the information legible and visually foremost.
- Keep weather facts and provenance from the existing fixture unchanged.
- Treat the named palette values and type pairings on the worksheet as concept
  values until reviewed against the full theme and accessibility states.

## Theme-wide inventory

| Asset group | Current worksheet coverage | Remaining design work |
| --- | --- | --- |
| Page information layouts | Now page only | Define the visual recipe for Now, Hourly, Daily, and Details while keeping their existing content and order |
| Palette | Initial semantic samples | Complete semantic roles, high contrast, disabled/status colors, and final contrast review |
| Backgrounds and gradients | Four page-specific SVG art plates in [`backgrounds/`](backgrounds/) | Review per-page recipes, layer limits, fallback behavior, and None/Subtle/Full treatments |
| Textures | Sediment curves and grain are part of background art; inclusion/glint overlays in [`decorations/`](decorations/) | Review texture vocabulary, density, opacity, and per-page use |
| Glass surfaces | Main fact card and secondary cards in worksheet | Define the surface hierarchy across all four pages, menus, and controls |
| Weather marks | Six matching SVG candidates in [`weather-marks/`](weather-marks/) | Review style, supported condition coverage, and fallback rules |
| Functional icons | Sample controls on overview; existing app controls remain the semantic reference | Review the existing navigation/action inventory without changing its meaning |
| Typography | Existing Fira Sans and Noto Sans family files and licenses referenced in [`typography/FONT_ASSETS.md`](typography/FONT_ASSETS.md) | Confirm roles, scaling, localization, and whether the current bundled faces are approved |
| Imagery | None; procedural/vector direction | Confirm whether any authored images are needed; record “None” when native drawing is sufficient |
| Animation | Three-frame seam-glint study and timing/lifecycle spec in [`motion/`](motion/) | Review trigger, duration/easing, lifecycle, reduced-motion behavior, and static Full first |
| Accessibility and localization | Not fully specified on worksheet | Review large text, compact layout, high contrast, RTL, semantics, and controls |

## Effects intent to detail

| Level | Smoked Quartz design target |
| --- | --- |
| None | Opaque and static; no gradients, texture, decorative weather marks, glow, or animation. Preserve readable grouping and all required facts. |
| Subtle | Restrained static smoked gradients, sediment texture, and optional decorative marks. Keep decoration behind the information. |
| Full | Richer native lighting, layered strata, surface depth, and controlled motion after the static design is accepted. Keep data sharp and stationary. |

These are design targets to refine against the repository effects contract, not
implementation commitments. High contrast and reduced motion must remain
independent overlays.

## Complete-sheet checklist

- [ ] Now, Hourly, Daily, and Details compositions
- [x] Initial page-specific native-drawing background recipe studies (review pending)
- [x] Six decorative weather mark candidates (review pending)
- [x] Quartz inclusion and seam-glint vector studies
- [x] Typeface candidates linked to existing bundled files and licenses
- [x] Seam-glint timing and reduced-motion study
- [ ] Final semantic palette and contrast pairs
- [ ] Functional icon inventory
- [ ] Image/artwork inventory or explicit “None” decision
- [ ] Final None, Subtle, and Full appearance definitions
- [ ] Motion and lifecycle approval
- [ ] Compact, large-text, RTL, and high-contrast review
- [ ] Owner review record and final asset manifest

Use [`../THEME_ASSET_TEMPLATE.md`](../THEME_ASSET_TEMPLATE.md) as the shared
full inventory when continuing this design or starting one for another theme.
