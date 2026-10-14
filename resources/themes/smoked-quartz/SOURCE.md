# Smoked Quartz Glass — four-page design source

**Status:** owner review required; design source only. No asset or runtime treatment in this packet is approved for packaging or installed acceptance.
**Scope:** Now, Hourly, Daily, and Details at None, Subtle, and Static Full. Motion is excluded.
**Functional contract:** preserve page order and identity (`Now -> Hourly -> Daily -> Details`), displayed data and units, condition identity, chronology, provenance, freshness, missing-value behavior, controls, request/cache behavior, and accessibility meaning. Weather facts remain readable text with equivalent semantics. Decoration is supplemental and never encodes forecast values, severity, alert, status, or chronology.

## Visual system

### Semantic palette proposals

These are proposed source tokens sampled from the existing concept boards, not measured contrast results or runtime tokens. Every pairing below must be measured in SQ.3 against the actual rendered colors and approved contrast policy before implementation.

| Role | Standard proposal | Use and contrast pair |
| --- | --- | --- |
| Canvas | `#0B1110` | Opaque page field; primary/secondary text use `#EAF2E9` / `#A9BCB0` |
| Elevated canvas | `#111916` | Quiet inset or control surface; primary text `#EAF2E9` |
| Surface | `#18211E` | Grouped readable data surface; primary text `#EAF2E9`, secondary `#A9BCB0` |
| Primary data | `#EAF2E9` | Temperature and page-primary facts on Canvas, Surface |
| Secondary data | `#A9BCB0` | Supporting facts, metadata, labels on Canvas, Surface |
| Action | `#C9DEC9` | Action labels and selected controls on Canvas, Surface; retain text label and shape/state cue |
| Condition / trend | `#D4E1D4` | Decorative marks or noncritical trend accent only; never sole status cue |
| Status / warning | `#EAF2E9` with outlined/filled status surface | Loading, stale, unavailable, alert; differentiate by explicit label/icon and shape, not hue alone |
| High contrast foreground | `#FFFFFF` | Facts, controls, and metadata on opaque `#000000` or `#101010` surfaces |
| High contrast action/status | `#FFFFFF` | Underline/outline and explicit text/state label on opaque black surfaces |

High contrast is a separate opaque palette overlay: remove translucency, texture behind text, gradients crossing text, and low-opacity separators; use solid black/near-black surfaces, white text, and explicit labels/outlines. The actual contrast ratios remain an implementation acceptance check, not established by these proposed hex values.

### Typography and surfaces

- Proposed roles reuse installed assets only: Fira Sans Regular/Medium/SemiBold for page title and prominent values; Noto Sans Regular/Medium for facts, controls, and explanatory copy; Noto Sans Mono Regular/Medium for source and timestamps only when legible. No bundled font change or decorative display face.
- Preserve platform font scaling. At scale 1.3 and for localization/long labels, wrap labels, allow vertical growth/scrolling, and keep numeric values and units together where possible; never clip, ellipsize away a weather fact, or shrink critical text to fit.
- Functional groups keep the current page content order. Use at most one quiet, opaque/tinted glass surface for the page-primary group and simple separation for supporting groups. Do not box every forecast item. Full richness comes from negative space, broad light, layered background forms, and restrained edge reflections, never enclosing data in more panels.
- Decorative marks are hidden from accessibility. Adjacent visible condition text and the existing entry/page spoken summaries remain authoritative. Missing or unsupported mark maps to no mark; no generic symbol may imply a condition.
- Keep text controls labeled by meaning (Earlier/Later, date, page name). Maintain at least 48dp touch targets where applicable. RTL mirrors spatial alignment and directional ornament while dates/forecast entries remain earliest-to-latest.

## Page recipes

| Page | Static Full composition and background | Surface / hierarchy | Decorative candidates and fallback | Functional content that stays primary |
| --- | --- | --- | --- | --- |
| Now | Broad dark smoked field with one off-center diagonal mineral seam and a soft upper-left light falloff; fine grain sparse and uniform. Reserve a low-detail text-safe central region. | Current temperature and condition lead; one restrained glass surface groups supporting current measurements; source/freshness and actions remain distinct and readable. | `backgrounds/now.svg`; clear/partly-cloudy/cloudy/rain/snow/storm marks may appear as small secondary ornaments. Unsupported/missing condition: no mark. Inclusion overlay at low density only outside the text-safe region. No image. | Now identity, location, current value/unavailable state, condition, apparent temperature and available supporting measurements, alert summary when present, source/update/freshness, existing actions. |
| Hourly | Fine, broad horizontal strata recede behind the list; no per-entry line or band may imply amount or timing. Grain remains sparse. | Six real chronological entries per current window in existing order; title/range and visible date/window controls above; no nested horizontal pager. Avoid per-entry cards; use spacing and one grouping field. | `backgrounds/hourly.svg`; optional condition marks only as redundant decoration beside each already labeled entry. Missing/unsupported mark: omit. No image. | Local time, condition/unavailable, temperature/unavailable, precipitation probability when available, date jump and Earlier/Later controls, actual horizon length. |
| Daily | Wide terraced mineral bands, shallow contrast, with a quiet central lane behind the five-day list. Texture does not align with row boundaries or encode temperatures. | Five real chronological days, existing date/condition/low-high/precipitation hierarchy. One shared surface or open rows; visible range and controls. | `backgrounds/daily.svg`; optional redundant condition marks beside visible condition text; omit for missing/unsupported. No image. | Date, condition, low/high/unavailable, precipitation meaning when available, Earlier/Later controls, sparse horizon without padding. |
| Details | Deep quiet field with very subtle integrated micro-etch and one low-contrast seam kept away from text. Grain is faintest here. | Preserve ordered source/freshness, Conditions, Forecast pattern, Historical context groups. Use one surface per semantic group at most; derived and historical labels remain explicit. | `backgrounds/details.svg`; no weather marks or quartz inclusion overlay. No image. | Provider values and units, source/provenance, valid/update/freshness, explicit missing states, derived vs historical distinctions and limitations. |

Existing functional symbols are unchanged: global page selector/page chooser, date chooser, Earlier and Later text controls, home actions/menu, Search, Settings, About, return/back, location permission/use-location, alert action, and any existing saved-location selection controls. Their visible labels and accessibility names remain the source of meaning; any icon stays supplemental. No new decorative asset replaces these controls.

## Effects-level layer policy

| Layer | None | Subtle | Static Full |
| --- | --- | --- | --- |
| Page base mineral field | Retain opaque page-specific base tonal gradient, integrated strata (Details micro-etch), and fine embedded grain per the approved Glass exception. No separate weather marks/icons, inclusions, seam-glint or other overlays/glyphs, added glow, or motion. | Same base, slightly more visible but restrained gradient and integrated strata; grain remains fine. | Same base, strongest permitted tonal depth while preserving text-safe regions and opaque readability. |
| Separate inclusions / seam overlay | Removed. | None by default; if later retained, static, sparse, outside text-safe area, and no stronger than the base strata. Current candidate inclusion overlays remain omitted from the recommended source. | Quartz inclusions may be sparse, static, and outside text-safe regions. Seam highlight is a static edge accent only. Neither implies data. |
| Weather marks | Removed. | Optional, small, low-opacity and redundant to visible labels. | Optional and page-specific as above; still secondary to data. Unsupported/missing conditions produce no mark. |
| Glass surfaces | Opaque usable functional surfaces and the existing readable grouping; no decorative translucency. | One restrained surface for key grouping, static. | Restrained translucent/edge-lit grouping, with backing sufficient for text; no all-row cards. |
| Glow / motion | None. | None. | Glow limited to broad background lighting, not text or controls. Static only; no seam-glint animation approved. |

## Responsive, state, and accessibility decisions

- **Compact F1:** 360×640 dp, 1.0 LTR Standard. Keep title and primary fact visible; scroll page content rather than compressing text. Hourly remains six entries when space permits by existing layout; if vertical scroll is required it scrolls vertically only. Daily remains five entries; no new horizontal owner.
- **Large font F2:** 360×640 dp, scale 1.3 LTR Standard. Permit vertical scrolling and wrapped control labels; preserve full forecast facts and 48dp controls. Date/window controls can wrap or stack. No clipped controls or values.
- **RTL F3:** 1.0 LTR-equivalent viewport, RTL Standard. Mirror text alignment and decorative direction. Keep page sequence and chronological list order unchanged; Earlier/Later remain text-named and act on time order, independent of arrow direction.
- **High contrast F4/F5:** F4 uses 1.0 LTR; F5 uses scale 1.3 LTR on representative Now per source scenario. Solid opaque surfaces and high-contrast tokens replace glass transparency and texture under text. Keep selection, warning, unavailable, and action state explicit in text/shape. F5 decisions generalize as layout rules but only Now is representative in the defined source scenario; other pages remain unverified installed.
- **F6 missing/sparse fixture:** Now omits unavailable measurements and labels current data unavailable when appropriate; Hourly/Daily render only supplied entries and explicit missing fields, never pad; Details labels unavailable fields and preserves source/derived/historical group distinctions. Missing/unsupported weather art is absent.
- **Reduced motion:** independent overlay for Full with system animation scales at 0: show the same Static Full composition; no motion layer. This source does not approve any animation trigger, duration, lifecycle, or implementation.
- **Large/compact/Fold:** source decisions target the 360×640 dp compact viewport and scale 1.3; use available width on Pixel 5 393×777 dp and Pixel 3 XL 411×781 dp. Fold outer posture uses a narrow single-column composition; open inner posture may gain whitespace and wider grouping but not additional columns that change reading order or semantics. Physical layout behavior is not installed-verified for these new recipes.

## Art decision

No authored bitmap/photo is needed. Backgrounds are native drawing recipes, not runtime SVG requirements. Candidate weather marks and overlays are optional decoration only. Existing bundled type assets are referenced without duplication. All candidates remain unapproved pending the exact owner disposition recorded in this cycle.
