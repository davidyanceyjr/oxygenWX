# Smoked Quartz — asset inventory

**Product theme:** Glass
**Art direction:** Smoked Quartz, with a mineral-strata material language
**Status:** Four-page source decisions owner-approved in cycle 184;
all candidate assets remain unapproved for runtime use.
**Worksheet:** [`asset-worksheet-v1.png`](asset-worksheet-v1.png) ·
[`SVG source`](asset-worksheet-v1.svg)
**Asset overview:** [`asset-pack-overview.png`](asset-pack-overview.png) ·
[`SVG source`](asset-pack-overview.svg)

The worksheet studies a Compose-native procedural background for Now. The
four-page source is [`SOURCE.md`](SOURCE.md). The worksheet and overview remain
design references, not screenshots of the installed app.

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
| Page information layouts | Four page recipes in [`SOURCE.md`](SOURCE.md) | Candidate source; owner disposition pending; installed recipe unverified |
| Palette | Semantic source roles and proposed pairs in [`SOURCE.md`](SOURCE.md) | Candidate values only; measured contrast and runtime token review deferred to SQ.3 |
| Backgrounds and gradients | Four SVG art plates in [`backgrounds/`](backgrounds/) | Candidate drawing references; level/layer policy in SOURCE; no runtime approval |
| Textures | Grain and strata in backgrounds; inclusion/glint overlays in [`decorations/`](decorations/) | Base texture policy defined; separate overlays remain optional Full candidates and unapproved |
| Glass surfaces | Worksheet examples; page hierarchy in SOURCE | Design-source proposal; implementation and contrast review deferred |
| Weather marks | Six SVG candidates in [`weather-marks/`](weather-marks/) | Optional redundant decoration; omit in None and when unsupported/missing; unapproved |
| Functional icons | Existing Home/page/window/action controls in app UI | Keep existing control icons, labels, semantics, and behavior; no replacement candidate |
| Typography | Existing Fira Sans and Noto Sans assets in [`typography/FONT_ASSETS.md`](typography/FONT_ASSETS.md) | Proposed role pairing; files/licenses unchanged; owner review pending |
| Imagery | None | No authored image required; native drawing direction is sufficient |
| Animation | Three-frame seam-glint study and timing note in [`motion/`](motion/) | Draft and explicitly unapproved; excluded from SQ.2 and nonbinding |
| Accessibility and localization | Compact, scale 1.3, RTL, High contrast, reduced motion, missing data in SOURCE | Source decisions only; installed states remain unverified |

## Effects intent to detail

| Level | Smoked Quartz design target |
| --- | --- |
| None | Opaque and static; retain the finished page-specific mineral background's base stone tonal gradient, integrated sediment strata (Details micro-etch), and fine embedded grain. Remove separate decorative marks and icons, inclusions, seam-glint and other overlays or glyphs, added glow, and all motion. Preserve readable grouping, required facts, and usable text controls. This is a Smoked Quartz Glass exception to the general solid-background None rule. |
| Subtle | Restrained static smoked gradients, sediment texture, and optional decorative marks. Keep decoration behind the information. |
| Full | Richer native lighting, layered strata, surface depth, and controlled motion after the static design is accepted. Keep data sharp and stationary. |

See [`SOURCE.md`](SOURCE.md) for the adopted source proposal. The definitions
are not implementation commitments until owner disposition. High contrast and
reduced motion remain independent overlays.

## Complete-sheet checklist

- [x] Now, Hourly, Daily, and Details source compositions (owner review pending)
- [x] Initial page-specific native-drawing background recipe studies (review pending)
- [x] Six decorative weather mark candidates (review pending)
- [x] Quartz inclusion and seam-glint vector studies
- [x] Typeface candidates linked to existing bundled files and licenses
- [x] Seam-glint timing and reduced-motion study
- [x] Proposed semantic palette roles and contrast pairs (numeric contrast still unverified)
- [x] Existing functional control/icon inventory
- [x] Image/artwork inventory: None required
- [x] None, Subtle, and Static Full source definitions
- [ ] Motion and lifecycle approval (out of scope; motion remains draft)
- [x] Compact, large-text, RTL, high-contrast, reduced-motion, and missing-data source review
- [ ] Owner review record and final asset manifest (pending disposition)

Use [`../THEME_ASSET_TEMPLATE.md`](../THEME_ASSET_TEMPLATE.md) as the shared
full inventory when continuing this design or starting one for another theme.
