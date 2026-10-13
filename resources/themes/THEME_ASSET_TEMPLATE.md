# [Theme name] — asset inventory and approval sheet

**Theme key / product name:**
**Status:** Exploring / Review requested / Approved for design / Superseded
**Owner review date:**
**Design folder:** `resources/themes/[theme-folder]/`
**Worksheet:** `[filename.svg]` / `[filename.png]`
**Related references:**

This template records the full presentation recipe for one theme. Fill each
section, including sections where the decision is “None.” Keep visual choices
separate from weather facts and behavior. A worksheet is a design artifact; it
does not establish that the design has been implemented or accepted in the app.

## 1. Theme identity

- **One sentence / material concept:**
- **Intended mood and visual focal point:**
- **Distinctive theme traits:**
- **What must stay quiet behind the data:**
- **Approved references and their role:**
- **Known conflicts or open decisions:**

## 2. Information composition and page layouts

Describe the visual hierarchy and page-specific arrangement. Preserve the
approved page order and existing facts, controls, chronology, and provenance.

| Page | Primary information and scan order | Supporting information | Layout / grouping | Background and focal path | Narrow / large-text notes |
| --- | --- | --- | --- | --- | --- |
| Now | | | | | |
| Hourly | | | | | |
| Daily | | | | | |
| Details | | | | | |

- **Shared page identity and navigation treatment:**
- **Loading, partial, missing, stale, and unavailable states:**
- **Functional invariants:** weather values and units; condition identity;
  provenance and times; forecast order and horizon; navigation; accessible
  meanings; network and cache behavior.

## 3. Semantic color palette

Record actual values (such as HEX/alpha or design token references), intended
use, and contrast behavior. Effects colors come from these semantic roles.

| Role | Value(s) | Use / pairing | High contrast behavior | None-level value |
| --- | --- | --- | --- | --- |
| Canvas | | | | |
| Surface | | | | |
| Elevated surface | | | | |
| Primary data | | | | |
| Secondary data | | | | |
| Condition / trend | | | | |
| Status (loading, stale, unavailable, selected, alert) | | | | |
| Action / focus | | | | |
| Text and dividers | | | | |
| Highlights and shadows | | | | |

**Contrast checks / unresolved pairs:**

## 4. Backgrounds, gradients, textures, and surfaces

| Page / component | Recipe and layer order | Native procedural / vector / bitmap | Density and contrast limit | None | Subtle | Full |
| --- | --- | --- | --- | --- | --- | --- |
| Now | | | | | | |
| Hourly | | | | | | |
| Daily | | | | | | |
| Details | | | | | | |
| Menus and controls | | | | | | |

- **Background motifs and permitted variation:**
- **Surface, border, translucency, blur, and shadow recipes:**
- **Texture names, source files, dimensions, and tiling/crop behavior:**
- **Reduced-cost / low-end rendering behavior:**
- **Effects None:** opaque, static, complete; no decorative gradients,
  textures, weather marks, glow, or animation.

## 5. Icons and marks

Keep decorative weather marks distinct from functional controls. List each
condition mark supported by the presentation model and each visible control.

| Name / condition or action | Decorative or functional | Vector / Compose drawing / bitmap | Stroke, size, palette role | Accessible label or adjacent text |
| --- | --- | --- | --- | --- |
| Clear | | | | |
| Partly cloudy | | | | |
| Cloudy | | | | |
| Rain | | | | |
| Storm | | | | |
| Snow | | | | |
| Menu / page navigation / other controls | | | | |

- **Unsupported conditions and fallback:**
- **None behavior:** suppress decorative marks; retain functional icons where
  needed to identify or operate controls.

## 6. Typography

| Role | Typeface / weight | Size and line-height direction | Numeric behavior | Fallback / licensing |
| --- | --- | --- | --- | --- |
| Page identity | | | | |
| Primary weather fact | | | | |
| Supporting fact | | | | |
| Forecast entry | | | | |
| Provenance / timestamps | | | | |
| Controls | | | | |

- **Bundled font files and licenses, or system-font choice:**
- **Large font scaling and truncation/wrapping behavior:**
- **RTL and localized-number considerations:**

## 7. Images and decorative artwork

List every proposed image, illustration, or generated bitmap. Compose-native
procedural art and vectors are valid choices; “None” is valid when intentional.

| Asset | Purpose and placement | File / source | License / attribution | Crop, density, and fallback |
| --- | --- | --- | --- | --- |
| | | | | |

- **Weather photographs:** None / list and justify
- **Artwork never used to communicate:** weather values, alerts, chronology,
  unsupported conditions, or data availability.

## 8. Animation and interaction

Specify motion by element, trigger, duration, easing, direction, and lifecycle.

| Element | Trigger | None | Subtle | Full | Reduced motion / lifecycle behavior |
| --- | --- | --- | --- | --- | --- |
| Background / texture | | | | | |
| Highlight / decoration | | | | | |
| Weather mark | | | | | |
| Page / control transition | | | | | |

- **Static Full appearance approved before motion:** Yes / No / Not applicable
- **Motion stops for inactive pages and backgrounded app:**
- **Performance limits and fallback:**
- **Motion conveys no quantitative weather value or alert urgency:** Yes / No

## 9. Accessibility and environment coverage

- [ ] Important weather facts remain visible text with meaningful semantics.
- [ ] Decoration is optional and does not obscure or carry required meaning.
- [ ] Controls have accessible names based on action, not arrow direction alone.
- [ ] Interactive targets meet 48 dp guidance where applicable.
- [ ] Standard and high contrast are specified.
- [ ] Reduced motion retains the readable static appearance level.
- [ ] Compact viewport reviewed.
- [ ] Large text reviewed.
- [ ] RTL reviewed; forecast chronology remains earliest-to-latest.
- **Other environmental conditions and fallback:**

## 10. Asset manifest and delivery

| File | Type / dimensions | Used by | Status | Source / license |
| --- | --- | --- | --- | --- |
| | | | | |

- **Preferred runtime form:** Compose drawing / vector / bitmap / font / mix
- **Required variants and density guidance:**
- **Naming and folder convention:**
- **Missing assets and owners:**

## 11. Review record

| Review | Decision | Notes / requested changes | Date |
| --- | --- | --- | --- |
| Asset worksheet | | | |
| Page composition | | | |
| Palette and contrast | | | |
| Effects levels and motion | | | |
| Accessibility / localization | | | |

**Design approval scope:**
**Still unresolved:**
**Implementation slice created:** No / link (only after design approval and
workflow planning)
