# D31 page atmospheres — owner-approved Now/Hourly/Daily/Details mapping

**Status: owner-approved documentary proposal.** The design owner approved all
twenty mapped cells and the overall set on 2026-09-25; the exact revision and
decisions are recorded in [D31_OWNER_DECISION.md](D31_OWNER_DECISION.md). This
remains a documentary design target, not installed-rendering acceptance. It records source observations,
interpretation, and proposed application separately. Combined phone concepts,
screen-example mockups, and the overview board are not full-page composition
authority. Product meaning, accessibility, and navigation remain governed by
`docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.

## Method and limits

The Now, Hourly, and Daily cells use locators tied to their visible phone, sheet, or overview cues and distinguish preview evidence from page composition authority. Details has no dedicated indexed source: each proposed cell cites a concrete same-theme phone cue as a derivation plus that theme's backdrop as direct field-level evidence. The backdrops do not establish page overlays. No new numeric measurements are introduced. The adopted forecast contract remains authoritative where small composite previews vary or omit content. All twenty proposals preserve Effects Off completeness, High contrast boundaries, compact/font-scale-1.3 readability, RTL reading order, and the Details source/update/status and supplied-group semantics. Complete coverage means only that every canonical theme/page pair is present. The integrated review, source reproductions, derivation review, and owner decisions are complete. D31 is owner-approved; TP.1D/TP.1 closure, packet approval, and TP.2 eligibility remain pending.

## Structured mapping

D31_PAGE_ATMOSPHERES:BEGIN



```json
{
  "schema_version": 1,
  "scope": {
    "themes": [
      "atmospheric",
      "glass",
      "minimal_oled",
      "instrument",
      "terminal"
    ],
    "pages": [
      "now",
      "hourly",
      "daily",
      "details"
    ],
    "cell_count": 20,
    "coverage": "complete"
  },
  "status": "owner-approved; documentary proposal",
  "cells": [
    {
      "id": "atmospheric-now",
      "theme": "atmospheric",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "atmospheric-phone",
          "locator": "Phone inner display: Now hero from location header through condition text; exclude hourly panel below.",
          "supports": "Blue sky field, warm horizon, illustrated mountain and forest behind the Now hero; translucent forecast panels begin below it.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "atmospheric-backdrop",
          "locator": "Backdrop center column at audited y=400, 1600, and 2800 samples; inspect full vertical field for context.",
          "supports": "Standalone blue field shifts from darker upper area toward a lighter lower area; it is visually distinct from the phone's illustrated landscape.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The phone uses a bright blue upper field with warm light near the horizon; the standalone backdrop is a vertical blue field.",
        "scene_backdrop": "The phone's Now hero shows an illustrated mountain and forest horizon. The separate backdrop has no matching landscape scene.",
        "surfaces": "Rounded translucent blue forecast panels are visible below the phone's hero.",
        "weather_art_relationship": "The phone places a small partly-cloudy mark beside the dominant temperature and condition; the landscape sits behind the hero."
      },
      "interpretation": "Atmosphere is carried by open blue space, a warm horizon, and a scenic lower edge, while information panels stay visually contained. The standalone gradient and phone scene are separate source treatments.",
      "proposed_treatment": {
        "palette": "Propose a blue-led field with restrained warm horizon accents; treat the audited backdrop samples as qualitative evidence rather than a complete palette.",
        "scene_backdrop": "Propose a spacious sky-like field and a restrained scenic horizon only where an approved scene source exists; do not imply the standalone gradient contains the phone landscape.",
        "surfaces": "Keep Now facts readable over the field with bounded, restrained surfaces where needed; exact opacity and geometry remain design choices for integrated review.",
        "weather_art_relationship": "Use any weather mark as secondary to visible temperature and condition text; do not make scene artwork carry forecast meaning."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static blue canvas and complete readable Now content; omit scenic effects without removing facts or controls.",
        "high_contrast": "Select readable semantic foreground and surface roles while preserving the blue-led theme identity and all status meanings.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow content to wrap or scroll; keep temperature, condition, provenance, and chronology understandable without scene art."
      },
      "source_gaps": [
        "No Atmospheric asset sheet or dedicated Now-only page source is indexed.",
        "No approved standalone landscape scene, weather-state variants, or page-specific surface measurements are available."
      ],
      "derivation_basis": [
        "atmospheric-phone",
        "atmospheric-backdrop"
      ],
      "rationale": "This direction retains the blue, scenic, warm-horizon identity visible in the Atmospheric Now crop while acknowledging that its separate backdrop does not contain the illustrated landscape.",
      "limitations": [
        "The phone is a small composite concept and cannot define the full Now page or exact surface geometry.",
        "The source review does not establish installed contrast, responsive fit, or owner acceptance."
      ]
    },
    {
      "id": "glass-now",
      "theme": "glass",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "glass-phone",
          "locator": "Phone inner display: Now hero card from location header through condition and apparent-temperature text.",
          "supports": "A rounded translucent hero card places white current-condition text over a cool blue-violet field with warm light toward the right edge.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "glass-backdrop",
          "locator": "Backdrop full vertical field, including audited center column at y=400, 1600, and 2800.",
          "supports": "The backdrop shifts through dark blue, blue-gray, and violet-blue regions and includes large warm and cool light forms.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "glass-asset-sheet",
          "locator": "08 SCREEN EXAMPLE panel, lower-right; inspect the current-condition hero and visible translucent layers.",
          "supports": "The sheet's Now example uses translucent layered surfaces over a blue-violet atmospheric field with warm light.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "Cool blue-violet tones dominate the backdrop and hero region, with localized warm light near the right side.",
        "scene_backdrop": "The phone and sheet show atmospheric light with a scenic lower edge; the standalone backdrop contains large soft light forms.",
        "surfaces": "The phone hero is a rounded translucent panel; the sheet repeats layered glass-like surfaces around information.",
        "weather_art_relationship": "A weather mark sits within the hero near the current temperature and condition; nearby text states the weather meaning."
      },
      "interpretation": "Glass atmosphere comes from the interaction between layered translucent surfaces and a cool, softly lit background, rather than from transparency alone.",
      "proposed_treatment": {
        "palette": "Propose cool blue-violet semantic roles with restrained warm highlights; do not treat point samples as a complete palette.",
        "scene_backdrop": "Propose a softly varied blue-violet field with optional subdued scenic depth where supported by approved assets.",
        "surfaces": "Use a small number of translucent or translucent-appearing hero surfaces only when text contrast remains clear; provide opaque equivalents for Effects Off and high contrast.",
        "weather_art_relationship": "Keep weather marks localized to the Now hero and subordinate to temperature and condition text; do not infer new measurements from sheet graphics."
      },
      "state_constraints": {
        "effects_off": "Replace transparency and lighting effects with opaque surfaces and a static canvas while preserving every Now fact and control.",
        "high_contrast": "Use stronger semantic surface boundaries and readable text roles; retain the layered Glass identity without relying on blur or color alone.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, let hero text wrap and surfaces grow; keep text and controls legible without relying on background separation."
      },
      "source_gaps": [
        "No dedicated Now-only page or loading, stale, error, and Effects Off references are present.",
        "The sheet and phone do not establish responsive surface opacity or contrast behavior."
      ],
      "derivation_basis": [
        "glass-phone",
        "glass-backdrop",
        "glass-asset-sheet"
      ],
      "rationale": "The proposal carries forward Glass's visible layered transparency, cool depth, and restrained warm light while defining accessible opaque fallbacks as proposed state behavior.",
      "limitations": [
        "The combined concept includes other page previews and is not authority for Now composition.",
        "No rendered contrast, blur, or responsive values were measured in this review."
      ]
    },
    {
      "id": "minimal_oled-now",
      "theme": "minimal_oled",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "minimal-oled-phone",
          "locator": "Phone inner display: Now header, temperature, condition, and localized moon/cloud image above the first separator.",
          "supports": "The screen is black-first, uses thin horizontal separators, and confines a small moon/cloud image to the Now hero.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "minimal-oled-backdrop",
          "locator": "Backdrop center column at the audited y=400, 1600, and 2800 samples; inspect the uninterrupted black field.",
          "supports": "The standalone backdrop is black at the audited points, with no scenic color field or panel treatment.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The phone and backdrop use a black-first field with high-contrast light text and small restrained accents.",
        "scene_backdrop": "The backdrop is uninterrupted black; a small moon/cloud image appears only in the phone's Now hero.",
        "surfaces": "Thin horizontal rules separate content; broad filled cards are absent from the phone's Now hero.",
        "weather_art_relationship": "The localized moon/cloud image sits beside the hero information and remains smaller than the temperature and condition text."
      },
      "interpretation": "Minimal OLED makes the data and typography the atmosphere; weather imagery is a small local accent rather than a full-screen scene.",
      "proposed_treatment": {
        "palette": "Propose a black-first canvas with readable light text and restrained semantic accents; keep warning and selection meaning explicit in text or shape as well as color.",
        "scene_backdrop": "Propose an uninterrupted black field without full-screen scenic imagery.",
        "surfaces": "Use spacing and thin separators to group Now facts before introducing filled surfaces; any new surface is a reviewable design choice.",
        "weather_art_relationship": "Keep any decorative condition mark localized and secondary; visible condition text remains the authoritative weather description."
      },
      "state_constraints": {
        "effects_off": "Retain the opaque black canvas, static separators, text, and controls; omit optional glow or image effects only.",
        "high_contrast": "Preserve a black-first appearance with high-contrast semantic text, borders, selection, and status cues that do not rely on color alone.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow text and groups to wrap or scroll while keeping the black field and separator grouping; do not shrink critical text."
      },
      "source_gaps": [
        "No Minimal OLED asset sheet is indexed.",
        "No dedicated Now-only source or alternate state reference specifies imagery, separators, or responsive behavior."
      ],
      "derivation_basis": [
        "minimal-oled-phone",
        "minimal-oled-backdrop"
      ],
      "rationale": "The proposal preserves the source's black canvas and typography-first hierarchy while limiting weather art to an optional local accent.",
      "limitations": [
        "The concept combines several forecast periods and does not define the final Now content order.",
        "The backdrop samples are point checks, not full black-level or display-power measurements."
      ]
    },
    {
      "id": "instrument-now",
      "theme": "instrument",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "instrument-phone",
          "locator": "Phone inner display: top current-condition module from location/time header through condition and apparent-temperature text.",
          "supports": "The hero uses a dark bounded module with a segmented circular condition graphic, dominant temperature, and explicit condition text.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "instrument-backdrop",
          "locator": "Backdrop full field and audited center-column samples at y=400, 1600, and 2800, including the grid texture.",
          "supports": "A fine rectangular grid sits over a near-black blue field; audited transparent grid samples remain alpha-bearing source values.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "instrument-asset-sheet",
          "locator": "08 SCREEN EXAMPLE panel, lower-right; inspect the Now module, outline, and neighboring text labels.",
          "supports": "The sheet repeats bounded outlined modules, dark navy surfaces, and a technical current-condition treatment.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The sources use near-black navy fields with light text and restrained amber, green, and blue accents.",
        "scene_backdrop": "The standalone backdrop has a fine rectangular grid; no scenic landscape is visible in the Now module.",
        "surfaces": "The phone and sheet use bounded dark modules with fine outlines and compact labels.",
        "weather_art_relationship": "The phone's segmented circular condition graphic surrounds the dominant temperature; condition text is also visible beneath it."
      },
      "interpretation": "Instrument's atmosphere is built from a technical grid and bounded information modules. Its illustrative gauge should remain decoration and does not establish a measured range or data contract.",
      "proposed_treatment": {
        "palette": "Propose dark navy semantic surfaces with restrained functional accents; exact color roles and contrast pairs remain subject to pack review.",
        "scene_backdrop": "Propose a subtle static grid as optional texture behind the Now content, with an opaque plain-field alternative.",
        "surfaces": "Use bounded outlined modules to group current facts; do not import the reference gauge, chart, or unsupported data slots as functional UI.",
        "weather_art_relationship": "If a condition graphic is retained, make it decorative and secondary to visible temperature and condition; do not encode an invented numeric range."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static navy canvas; keep outlines, labels, and all Now facts complete without grid or motion effects.",
        "high_contrast": "Strengthen semantic outlines, text, and status cues; preserve distinctions without depending on the grid or accent color.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, let modules stack and labels wrap; maintain readable text and avoid requiring a gauge for weather meaning."
      },
      "source_gaps": [
        "The reference gauge and chart do not provide typed measurements or valid runtime ranges.",
        "No page-state references establish grid density, module behavior, or a dedicated Now layout."
      ],
      "derivation_basis": [
        "instrument-phone",
        "instrument-backdrop",
        "instrument-asset-sheet"
      ],
      "rationale": "The proposal keeps Instrument distinct through grid texture, outlined modules, and technical grouping while explicitly preventing decorative instruments from implying unsupported measurements.",
      "limitations": [
        "The phone concept is a composite and includes unsupported chart and gauge examples outside this proposal.",
        "The transparent grid sample is not a composited display color or a contrast measurement."
      ]
    },
    {
      "id": "terminal-now",
      "theme": "terminal",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "terminal-phone",
          "locator": "Phone inner display: Now block from WX location/time header through the condition text and first dashed divider.",
          "supports": "The Now block uses green monospace text, a localized ASCII-like weather mark, and dashed horizontal separators on a black field.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "terminal-backdrop",
          "locator": "Backdrop full field and audited center-column samples at y=400, 1600, and 2800, including the dense rectangular grid.",
          "supports": "The standalone backdrop uses a dense grid over black; the phone display uses a visibly different sparse dashed-rule treatment.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The phone uses green monospace text and marks on black; the standalone backdrop is black with a fine pale grid.",
        "scene_backdrop": "The phone Now block is mostly a flat black field; the separate backdrop carries a denser rectangular grid.",
        "surfaces": "Dashed horizontal rules separate text groups; the phone does not use rounded filled cards in its Now block.",
        "weather_art_relationship": "A small ASCII-like weather mark accompanies the large temperature and condition text in the Now block."
      },
      "interpretation": "Terminal atmosphere comes from console typography, explicit separators, and compact text groups. The phone and standalone backdrop use different grid densities, so neither should be silently substituted for the other.",
      "proposed_treatment": {
        "palette": "Propose a black-first canvas with restrained green accents and accessible semantic text/status roles; no complete measured accent palette is claimed.",
        "scene_backdrop": "Propose a mostly flat black Now field; treat any grid as optional texture and keep it distinct from the phone's sparse content separators.",
        "surfaces": "Use clear text grouping and static rules rather than rounded filled cards; ensure controls remain recognizable and adequately sized.",
        "weather_art_relationship": "A simple decorative text-like mark may supplement the condition, while visible condition text remains authoritative and no legacy instrument or page-rail language is introduced."
      },
      "state_constraints": {
        "effects_off": "Keep an opaque black canvas with static text and separators; all facts and controls remain present without texture or motion.",
        "high_contrast": "Use semantic foreground, border, and status distinctions with text or shape cues in addition to green color.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow console text groups to wrap or scroll, keep controls targetable, and preserve chronological text order."
      },
      "source_gaps": [
        "No Terminal asset sheet or dedicated Now-only page source is indexed.",
        "No source defines grid density, accessible console control treatment, or alternate page states."
      ],
      "derivation_basis": [
        "terminal-phone",
        "terminal-backdrop"
      ],
      "rationale": "The proposal retains Terminal's console-like text hierarchy and explicit separators while treating the denser standalone grid as optional backdrop evidence.",
      "limitations": [
        "The phone is a small combined-page concept rather than a complete Now specification.",
        "No numeric grid or type measurements are claimed by this qualitative mapping."
      ]
    },
    {
      "id": "atmospheric-hourly",
      "theme": "atmospheric",
      "page": "hourly",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "atmospheric-phone",
          "locator": "Native phone crop inner display: Hourly panel from its label and Now column through the six visible temperature values; exclude the Now hero and daily rows.",
          "supports": "A rounded translucent blue panel groups a labeled hourly strip with six time columns, weather marks, and temperature labels over the scenic blue field.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "atmospheric-backdrop",
          "locator": "Standalone backdrop full vertical field; compare its blue gradient with the separate scenic phone-panel treatment.",
          "supports": "The standalone backdrop is a blue vertical field and does not include the phone crop landscape or establish the strip surface.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The strip sits over a blue scenic phone field; its translucent blue surface and white labels keep the row visually contained.",
        "scene_backdrop": "The phone landscape remains visible around and through the forecast panel; the separate backdrop is only a blue gradient.",
        "surfaces": "One rounded translucent panel contains the HOURLY label and six columns, with no visible divider between individual columns.",
        "weather_art_relationship": "Each visible time has a small condition mark above a numeric temperature; the source row shows six columns including Now."
      },
      "interpretation": "Atmospheric carries its open blue and scenic identity into Hourly with a contained translucent strip, while the weather marks stay paired with explicit time and temperature.",
      "proposed_treatment": {
        "palette": "Propose a blue-led field with clear light text and restrained warm scenic accents; the reference does not define exact colors or contrast values.",
        "scene_backdrop": "Propose retaining open sky-like space or an approved restrained scene behind Hourly; the standalone gradient and phone landscape remain distinct source treatments.",
        "surfaces": "Propose a single readable translucent-appearing forecast region when contrast permits; use an opaque equivalent for Effects Off and high contrast.",
        "weather_art_relationship": "Keep marks small and secondary to visible time, condition text, and temperature; the adopted six-entry page and its controls come from the product contract, not this preview."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static canvas and surface; preserve the Hourly page identity, visible date and Earlier/Later controls, and all actual entries without scenic effects.",
        "high_contrast": "Use clear semantic text and surface boundaries while retaining the open blue Atmospheric identity; do not rely on the landscape to separate entries.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow entry content to wrap or scroll while preserving earliest-to-latest order and readable time, condition, temperature, and available precipitation."
      },
      "source_gaps": [
        "No dedicated Hourly page, loading/stale/error state, or Atmospheric asset sheet is indexed.",
        "The combined phone preview does not establish the adopted six-entry page layout, date/window controls, entry geometry, or a complete scenic asset."
      ],
      "derivation_basis": [
        "atmospheric-phone",
        "atmospheric-backdrop"
      ],
      "rationale": "The proposal applies the source-visible blue scenic field and contained translucent strip while leaving the adopted page composition and navigation to the written UI contract.",
      "limitations": [
        "The phone preview shows a compact six-column strip as part of Now, not a complete Hourly page or its navigation.",
        "Backdrop samples are qualitative identity evidence and do not establish a composited panel color, contrast, or responsive crop."
      ]
    },
    {
      "id": "glass-hourly",
      "theme": "glass",
      "page": "hourly",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "glass-phone",
          "locator": "Native phone crop inner display: Hourly tabs and forecast strip below the Now hero, including visible time labels, marks, and temperatures.",
          "supports": "A rounded translucent forecast panel follows the Hourly/Daily/Details selector; the phone shows five time columns over a cool blue-violet field with warm light.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "glass-asset-sheet",
          "locator": "04 CORE COMPONENTS panel, lower-right Compact Forecast Row sample; inspect its time, condition-mark, and temperature grouping.",
          "supports": "The compact forecast-row sample uses separated time columns with small marks and temperatures inside a bounded glass-style surface.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "glass-backdrop",
          "locator": "Standalone backdrop full vertical field, including large warm and cool light forms behind the phone concept.",
          "supports": "The backdrop contains cool blue-violet depth with soft warm/cool light forms; it does not specify Hourly surface opacity.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The phone strip uses light text over a dark translucent-looking surface against a cool blue-violet field with warm light toward the right.",
        "scene_backdrop": "Soft light forms remain visible around the compact strip; the phone also shows a scenic edge below the preview.",
        "surfaces": "The phone places its five visible forecast columns in a rounded bounded panel below a separate segmented page selector; the asset sheet shows a compact row component with column groupings.",
        "weather_art_relationship": "Small weather marks sit between each time label and temperature; the phone provides five columns, while the sheet component is a compact example rather than a page specification."
      },
      "interpretation": "Glass atmosphere comes from layered surfaces over cool, softly lit depth; the Hourly preview suggests a bounded row that remains visually connected to that background.",
      "proposed_treatment": {
        "palette": "Propose cool blue-violet roles with restrained warm highlights and readable foreground text; no complete palette or composited panel value is asserted.",
        "scene_backdrop": "Propose a softly varied atmospheric field where supported by approved assets, with light forms subordinate to forecast legibility.",
        "surfaces": "Use a limited translucent-appearing forecast surface only when contrast remains clear; provide opaque, static equivalents for Effects Off and high contrast.",
        "weather_art_relationship": "Keep each decorative mark secondary to visible time, condition text, and temperature. Follow the adopted six-entry window and controls; do not copy the preview’s five-column count or nested segmented selector."
      },
      "state_constraints": {
        "effects_off": "Replace transparency and background lighting with an opaque surface and static canvas while retaining all Hourly facts and window/date controls.",
        "high_contrast": "Strengthen semantic text and surface boundaries; preserve Glass identity without relying on blur, translucency, or color alone.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, let entries wrap or scroll and retain earliest-to-latest order, controls, and visible data meaning."
      },
      "source_gaps": [
        "No dedicated Hourly page or Hourly loading, stale, error, Effects Off, high-contrast, or RTL reference is available.",
        "The phone preview shows five columns and a nested selector, neither of which defines the adopted six-entry page or its navigation."
      ],
      "derivation_basis": [
        "glass-phone",
        "glass-asset-sheet",
        "glass-backdrop"
      ],
      "rationale": "The proposal retains the directly visible layered Glass surface and cool light field while replacing preview-only count/navigation with the adopted Hourly contract.",
      "limitations": [
        "The phone and sheet are compact composite/component examples, not an Hourly page specification.",
        "No opacity, blur, responsive geometry, contrast ratio, or installed behavior was measured."
      ]
    },
    {
      "id": "minimal_oled-hourly",
      "theme": "minimal_oled",
      "page": "hourly",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "minimal-oled-phone",
          "locator": "Native phone crop inner display: Hourly preview, NEXT 6 HOURS separator and line plot from the first Now value through the final 15 label.",
          "supports": "The black-first phone uses thin rules, six labeled time positions, white temperature labels, and an amber line with point markers in its hourly preview.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "minimal-oled-backdrop",
          "locator": "Standalone backdrop full black field; compare its uninterrupted field with the localized phone separators and hourly plot.",
          "supports": "The standalone backdrop is black at audited points and contains no scenic field or broad filled forecast card.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The hourly preview uses white labels and a restrained amber plot on a black field.",
        "scene_backdrop": "The standalone backdrop is uninterrupted black; the phone keeps the hourly visualization local between thin horizontal separators.",
        "surfaces": "The preview does not use a filled card: a centered NEXT 6 HOURS label sits between rules, followed by an open line plot with six points and time labels.",
        "weather_art_relationship": "The displayed hourly preview presents temperatures and time positions in a line plot, without a condition mark or visible condition text for each point."
      },
      "interpretation": "Minimal OLED keeps Hourly typography and values prominent, using small amber plot accents and separators instead of a broad filled surface.",
      "proposed_treatment": {
        "palette": "Propose a black-first canvas with readable light text and restrained semantic accents; warning and selection meanings must also use text or shape.",
        "scene_backdrop": "Use an uninterrupted black field without full-screen scenic imagery; keep any optional accent local to forecast content.",
        "surfaces": "Prefer spacing and thin static separators to filled cards where they support grouping; do not carry the source line plot forward without an approved typed series contract.",
        "weather_art_relationship": "For the adopted six actual entries, show time, condition, temperature, and available precipitation as visible text; a mark may remain a secondary local accent. The preview does not establish entry count or complete page composition."
      },
      "state_constraints": {
        "effects_off": "Retain the opaque black canvas, static text, separators, Hourly controls, and all actual entries; omit optional glow or motion only.",
        "high_contrast": "Preserve the black-first identity while providing clear text, boundaries, selected states, and status cues beyond amber color alone.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow text wrapping or scrolling; preserve chronological order and readable entry facts without requiring the plot."
      },
      "source_gaps": [
        "No dedicated Hourly page or state-specific Minimal OLED reference is indexed.",
        "The preview’s plotted temperatures do not supply per-entry condition text, precipitation, series provenance, or a runtime chart contract."
      ],
      "derivation_basis": [
        "minimal-oled-phone",
        "minimal-oled-backdrop"
      ],
      "rationale": "The proposal preserves Minimal OLED’s black field, open spacing, thin separators, and restrained amber accent without converting an illustrative plot into an unsupported data visualization.",
      "limitations": [
        "The source is a compact combined concept and does not establish adopted six-entry page geometry, control placement, or date selection.",
        "No measured typography, spacing, contrast, font-scale behavior, or installed rendering is claimed."
      ]
    },
    {
      "id": "instrument-hourly",
      "theme": "instrument",
      "page": "hourly",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "instrument-phone",
          "locator": "Native phone crop inner display: HOURLY/DAILY/RADAR selector and the six-column forecast strip directly below it.",
          "supports": "The technical phone concept places six time, condition-mark, and temperature columns in a dark bounded strip below a segmented selector.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "instrument-asset-sheet",
          "locator": "04 CORE COMPONENTS panel, lower-right Compact Forecast Row sample; inspect technical labels, marks, and temperature columns.",
          "supports": "The component sample groups forecast times, marks, and temperatures in a compact dark row with a defined boundary.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "instrument-backdrop",
          "locator": "Standalone backdrop full vertical field; inspect the fine grid separately from the phone forecast module.",
          "supports": "The standalone field uses a fine rectangular grid on near-black navy; the phone strip itself is a bounded dark module.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "Light labels and yellow/blue weather accents sit on near-black navy surfaces against a technical dark field.",
        "scene_backdrop": "The standalone backdrop has a fine grid; the phone places the forecast strip in a bounded module rather than exposing grid detail as part of each entry.",
        "surfaces": "Six forecast columns appear within a thinly outlined dark strip below a segmented Hourly/Daily/Radar selector. The selector is preview navigation, not the adopted global page control.",
        "weather_art_relationship": "Each visible time position pairs a small weather mark with a numeric temperature; the phone shows six columns including Now."
      },
      "interpretation": "Instrument carries its technical identity through a bounded forecast module, fine structural lines, and restrained functional accents.",
      "proposed_treatment": {
        "palette": "Propose dark navy semantic surfaces with restrained functional accents and explicit readable labels; exact roles remain subject to integrated review.",
        "scene_backdrop": "A subtle static grid may sit behind content where it does not compete with text; Effects Off uses a plain opaque field.",
        "surfaces": "Use clear boundaries for forecast grouping and keep the six-entry page readable; do not reproduce unsupported chart/gauge elements or nested Hourly/Daily/Radar navigation.",
        "weather_art_relationship": "Keep condition marks decorative and secondary to visible time, condition text, temperature, and available precipitation. Use the adopted outer page identity and visible date/window controls."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static canvas and complete forecast module; retain Hourly identity, date/window controls, and every actual entry without grid or motion.",
        "high_contrast": "Strengthen semantic outlines, labels, selected states, and status cues; preserve distinctions without depending on the grid or accent color.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow entries to wrap or stack; preserve earliest-to-latest chronology, readable facts, and targetable controls."
      },
      "source_gaps": [
        "No dedicated Hourly page or loading, stale, error, Effects Off, high-contrast, or RTL Instrument reference exists.",
        "The concept selector and six-column preview do not establish the adopted page navigation, control positions, or responsive entry geometry."
      ],
      "derivation_basis": [
        "instrument-phone",
        "instrument-asset-sheet",
        "instrument-backdrop"
      ],
      "rationale": "The proposal retains Instrument’s bounded technical grouping and optional grid identity while replacing preview-only nested navigation with the product contract.",
      "limitations": [
        "The compact phone is a combined concept, not an Hourly page specification or data-source contract for its decorative charts.",
        "Grid density, outlines, colors, dp geometry, contrast, and responsive behavior were not measured for a rendered app."
      ]
    },
    {
      "id": "terminal-hourly",
      "theme": "terminal",
      "page": "hourly",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "terminal-phone",
          "locator": "Native phone crop inner display: HOURLY FORECAST block from its heading through the six time, mark, and temperature columns, ending at the dashed divider.",
          "supports": "The phone shows a labeled hourly block with six compact time columns, green text-like weather marks, temperatures, and dashed separators on black.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "terminal-backdrop",
          "locator": "Standalone backdrop full field; inspect dense grid as separate background evidence from the phone’s sparse dashed forecast separators.",
          "supports": "The standalone field has a dense rectangular grid, while the phone forecast block uses sparse dashed rules on a mostly flat black field.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "Green monospace-like labels and values appear on black, with no warm or scenic backdrop in the hourly block.",
        "scene_backdrop": "The phone uses a mostly flat black forecast field; the standalone backdrop carries a denser grid and is visibly different from the phone block.",
        "surfaces": "A HOURLY FORECAST heading, six aligned time columns, and dashed horizontal separators structure the preview without rounded cards.",
        "weather_art_relationship": "Small text-like condition marks accompany each visible time, with a temperature beneath; the source strip shows Now plus five times."
      },
      "interpretation": "Terminal’s Hourly treatment uses console typography, aligned text groups, and explicit separators to keep the forecast dense and scan-friendly.",
      "proposed_treatment": {
        "palette": "Propose a black-first field with readable monospace-like text and restrained green accents; preserve distinct status and selection meaning with text or shape.",
        "scene_backdrop": "Keep the forecast field mostly flat; any optional grid remains subtle and separate from the sparse content rules.",
        "surfaces": "Use clear static separators and grouped text rather than rounded filled cards; ensure date and Earlier/Later controls remain recognizable and adequately sized.",
        "weather_art_relationship": "Use any text-like mark only as decoration beside visible time, condition, temperature, and available precipitation. Show the adopted six actual entries and never copy values from this concept."
      },
      "state_constraints": {
        "effects_off": "Keep an opaque black canvas with static text and rules; preserve the complete hourly facts and controls without texture or motion.",
        "high_contrast": "Use semantic foreground, boundary, and status distinctions with text or shape in addition to green color.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow console groups to wrap or scroll; preserve earliest-to-latest chronology and usable controls."
      },
      "source_gaps": [
        "No Terminal asset sheet or dedicated Hourly page/state source is indexed.",
        "The combined strip does not define full-page controls, responsive layout, status states, or typography measurements."
      ],
      "derivation_basis": [
        "terminal-phone",
        "terminal-backdrop"
      ],
      "rationale": "The proposal preserves Terminal’s visible console hierarchy and sparse separators while treating its dense standalone grid as separate optional backdrop evidence.",
      "limitations": [
        "The phone preview is part of a composite screen and does not establish the adopted six-entry page composition.",
        "No numeric grid, typography, contrast, responsive, or runtime behavior is claimed."
      ]
    },
    {
      "id": "atmospheric-daily",
      "theme": "atmospheric",
      "page": "daily",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "atmospheric-phone",
          "locator": "Daily forecast preview in the lower inner phone display, approximately x=24..285 and y=584..703 px: four date rows with condition marks and low/high values.",
          "supports": "The compact Daily preview uses separated date rows with small condition marks and low/high values on a rounded blue panel; four mockup rows do not define the required horizon.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "overview-board",
          "locator": "Atmospheric phone column, lower Daily forecast preview around board x=28..295 and y=584..703 px; inspect its row separators and warm scenic edge.",
          "supports": "The overview corroborates the Atmospheric phone preview’s scenic sky-to-landscape field and contained Daily rows; its composite screen is not a complete page target.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "atmospheric-backdrop",
          "locator": "Standalone blue backdrop full vertical field, considered separately from the Daily preview and its scenic phone background.",
          "supports": "The standalone backdrop is a blue vertical field and does not define the Daily row surface or scene composition.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The compact list appears over a blue scenic phone field with warm horizon light; the backdrop is a separate blue gradient. No numeric palette values are taken from the preview.",
        "scene_backdrop": "The preview sits in the lower part of a phone composition with mountain and forest imagery; the standalone field contains no landscape detail.",
        "surfaces": "Four short date rows sit in a rounded, translucent-appearing blue forecast panel with horizontal separators.",
        "weather_art_relationship": "Each visible date row pairs a small condition mark with low/high values; the mockup does not show the adopted five-day rows, precipitation line, or Earlier/Later controls."
      },
      "interpretation": "Carry Atmospheric’s open blue field, warm scenic horizon, and contained forecast surface into Daily while keeping the landscape secondary to readable rows.",
      "proposed_treatment": {
        "palette": "Propose a blue-led field with clear light text and restrained warm accents; exact palette and contrast values remain for integrated review.",
        "scene_backdrop": "Propose a calm sky-like field behind the list and use scenic detail only where it does not compete with date and forecast text; no complete scene is sourced here.",
        "surfaces": "Propose a restrained rounded, translucent-appearing group only when row text remains clear; use opaque surfaces in High contrast and Effects Off.",
        "weather_art_relationship": "Keep optional marks beside visible date and condition text, low/high values, and available precipitation meaning. Follow the five-day window and named controls from the written contract, not the four-row preview."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static canvas and row surface; retain Daily identity, exact supplied dates and values, precipitation meaning, source/status text, and named Earlier/Later controls.",
        "high_contrast": "Use readable semantic text and visible row boundaries without relying on the landscape or translucent panel to separate values.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, let row content wrap or scroll while preserving earliest-to-latest order, visible labels, and control names."
      },
      "source_gaps": [
        "No dedicated Daily page, full five-day window, precipitation treatment, Earlier/Later control state, or Atmospheric asset sheet is indexed.",
        "The compact phone/board preview shows four rows only and does not define responsive, loading, stale, failure, or unavailable states."
      ],
      "derivation_basis": [
        "atmospheric-phone",
        "overview-board",
        "atmospheric-backdrop"
      ],
      "rationale": "This proposal uses only the source-visible scenic field and separated Daily preview rows; the complete page structure and five-day behavior remain governed by the adopted contract.",
      "limitations": [
        "The phone and overview are the same compact composite concept, not independent full-page references.",
        "The backdrop is field-level evidence only; no exact panel opacity, colors, crop, or numeric appearance measurements are proposed."
      ]
    },
    {
      "id": "glass-daily",
      "theme": "glass",
      "page": "daily",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "glass-phone",
          "locator": "Daily tab in the segmented selector beneath the Now hero; the visible forecast strip below it is Hourly, with no Daily rows in this phone crop.",
          "supports": "The phone identifies a Daily destination in the selector but does not show a Daily list or establish its row treatment.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "glass-asset-sheet",
          "locator": "08 SCREEN EXAMPLE lower-right phone mockup: the “5 Day Forecast” list below the Hourly strip, including its five visible date rows and marks.",
          "supports": "The screen example shows a compact five-day list with dated rows, condition marks, and low/high values inside the Glass presentation; it remains a composite mockup.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "overview-board",
          "locator": "Glass phone column, Daily tab in the segmented selector above the Hourly forecast strip; no Daily rows are visible in this board column.",
          "supports": "The overview shows the Glass selector’s Daily label and its cool blue-violet field with warm light; it supplies no Daily list composition.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "glass-backdrop",
          "locator": "Standalone Glass backdrop full vertical field with cool blue-violet depth and soft warm/cool light forms; field evidence only.",
          "supports": "The backdrop carries soft cool depth and warm/cool light forms but does not define a Daily surface or row opacity.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The phone and screen example use light text over dark, translucent-looking surfaces against cool blue-violet depth with warm light; no composited Daily color is measured.",
        "scene_backdrop": "Soft light forms remain visible around the compact screen example; the phone selector itself does not show a Daily scene or list.",
        "surfaces": "The asset-sheet screen example groups a five-day list in a rounded bounded surface; the separate phone shows a Daily selector label but only Hourly rows.",
        "weather_art_relationship": "The five-day screen example places small condition marks with dates and low/high values. It does not establish precipitation meaning or the product’s window controls."
      },
      "interpretation": "Glass Daily can extend the sourced layered-surface and softly lit depth treatment, using the five-day example only as a compact row reference.",
      "proposed_treatment": {
        "palette": "Propose cool blue-violet roles with restrained warm highlights and readable foreground text; exact values remain unapproved.",
        "scene_backdrop": "Propose a softly varied field where approved assets permit, with light forms subordinate to date and forecast facts.",
        "surfaces": "Use one restrained translucent-appearing Daily group only when text and separators remain legible; resolve High contrast and Effects Off with opaque surfaces.",
        "weather_art_relationship": "Keep marks decorative beside visible date, condition, low/high, and available precipitation meaning. The adopted five-day windows and visible Earlier/Later controls come from the product contract, not the mockup selector."
      },
      "state_constraints": {
        "effects_off": "Replace transparency and background lighting with opaque surfaces and a static canvas while retaining every supplied Daily fact, source/status text, and window control.",
        "high_contrast": "Use opaque, clearly bounded rows and readable text; do not depend on blur, translucent layering, or color alone.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow row fields to wrap or scroll and retain earliest-to-latest order and named controls."
      },
      "source_gaps": [
        "The phone crop has a Daily selector but no Daily forecast rows; only the asset-sheet composite shows a five-day list.",
        "No dedicated page states, precipitation details, Earlier/Later behavior, large-font, RTL, High contrast, or Effects Off source reference is available."
      ],
      "derivation_basis": [
        "glass-phone",
        "glass-asset-sheet",
        "overview-board",
        "glass-backdrop"
      ],
      "rationale": "This proposal combines the sheet’s visible five-day row example with the Glass field and surface language while leaving the full page and interaction to the adopted contract.",
      "limitations": [
        "The screen example is one compact composite, not a complete app page or proof of all five product fields.",
        "The standalone backdrop cannot support claims about translucency, opacity, crop, or contrast of a composed Daily screen."
      ]
    },
    {
      "id": "minimal_oled-daily",
      "theme": "minimal_oled",
      "page": "daily",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "minimal-oled-phone",
          "locator": "Daily preview below the metric tiles in the lower phone display, approximately x=35..277 and y=535..666 px: four ruled rows with condition marks and low/high values.",
          "supports": "The compact Daily preview uses a black field, thin horizontal rules, text-first date/value rows, and small marks; four rows are not a horizon target.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "overview-board",
          "locator": "Minimal OLED phone column, lower Daily preview below the metric tiles, around board x=655..895 and y=565..700 px; inspect ruled text rows.",
          "supports": "The board corroborates the Minimal OLED black-first, low-decoration Daily preview with thin separators and localized marks.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "minimal-oled-backdrop",
          "locator": "Standalone OLED backdrop full uninterrupted black field, considered separately from the phone’s ruled Daily rows.",
          "supports": "The backdrop is a solid black field and does not prescribe Daily row structure or page-state treatment.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The preview uses a black-first field with bright text and a small warm mark accent; the standalone backdrop is solid black.",
        "scene_backdrop": "No broad scene appears behind the Daily rows; the phone’s small localized hero image is above this preview and is not a Daily scene.",
        "surfaces": "Four text rows are separated by thin horizontal rules without broad filled cards.",
        "weather_art_relationship": "A small condition mark precedes each visible date, with high/low values aligned at the opposite side; precipitation and window controls are absent."
      },
      "interpretation": "Keep Minimal OLED Daily typography- and data-dominant, extending its black field and thin ruled list without importing the preceding chart or localized hero image.",
      "proposed_treatment": {
        "palette": "Propose the black canvas with high-legibility theme text roles and restrained condition accents; do not infer numeric color tokens from the mockup.",
        "scene_backdrop": "Keep the broad Daily field visually quiet and black-first; do not extend the localized hero moon/cloud into this page.",
        "surfaces": "Propose a low-decoration full-width list with thin separators and no filled card by default; High contrast retains visible boundaries and readable text.",
        "weather_art_relationship": "Marks remain optional and secondary to visible date, condition, low/high, and available precipitation text. Preserve the five-day window and named controls from the product contract."
      },
      "state_constraints": {
        "effects_off": "Retain the same opaque black canvas and static ruled list with every supplied fact and named control present.",
        "high_contrast": "Keep a black field with readable text and stronger visible row/control boundaries; selection, disabled, and unavailable meanings must include words or semantics.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, allow rows to grow or scroll; keep labels readable and chronological order earliest-to-latest."
      },
      "source_gaps": [
        "No dedicated Daily page, complete ten-day horizon, precipitation line, Earlier/Later controls, or Minimal OLED asset sheet is indexed.",
        "The composite preview’s four rows and absent state examples do not define compact, large-font, RTL, loading, or stale behavior."
      ],
      "derivation_basis": [
        "minimal-oled-phone",
        "overview-board",
        "minimal-oled-backdrop"
      ],
      "rationale": "The treatment extends source-visible black canvas and thin ruled rows while excluding unrelated chart and hero content.",
      "limitations": [
        "Phone and overview show the same small composite, not a complete Daily page.",
        "No exact spacing, type scale, palette measurement, or row density is proposed from the cropped concept."
      ]
    },
    {
      "id": "instrument-daily",
      "theme": "instrument",
      "page": "daily",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "instrument-phone",
          "locator": "DAILY option in the HOURLY/DAILY/RADAR selector in the middle of the inner phone display; content below is an Hourly strip, not Daily rows.",
          "supports": "The phone visibly names a Daily selector option but does not show a Daily list or establish a Daily row pattern.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "instrument-asset-sheet",
          "locator": "08 SCREEN EXAMPLE lower-right phone: DAILY option in the HOURLY/DAILY/RADAR selector above the Hourly strip and charts; no Daily rows are shown.",
          "supports": "The sheet repeats the Daily selector label within Instrument’s bounded technical modules but provides no Daily forecast-row example.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "overview-board",
          "locator": "Instrument phone column, DAILY selector option above the six-column Hourly strip around board x=948..1213 and y=448..599 px; no Daily rows follow it.",
          "supports": "The overview shows the Daily tab within Instrument’s dark technical selector and outlined modules; it does not show Daily forecast content.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "instrument-backdrop",
          "locator": "Standalone Instrument backdrop full vertical field with a fine rectangular grid; field-level evidence, not Daily content evidence.",
          "supports": "The backdrop has a fine grid over a dark field, supporting broad theme identity only.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The phone and sheet use a dark field, restrained colored accents, and pale text; the separate backdrop has a fine grid. No Daily-specific palette is shown.",
        "scene_backdrop": "The backdrop is an orthogonal grid field; phone/sheet Daily selectors sit among technical modules, but no selected Daily scene is visible.",
        "surfaces": "The selector and neighboring modules use thin outlined rectangular boundaries; the content below remains an Hourly strip and charts rather than a Daily list.",
        "weather_art_relationship": "A Daily option is visible in the selector, but no Daily date, condition, low/high, or precipitation rows are shown. Gauge and chart art are unrelated and excluded."
      },
      "interpretation": "Propose a restrained Instrument Daily treatment from its dark grid and outlined-module vocabulary, while treating all row structure and forecast content as derived from the written contract.",
      "proposed_treatment": {
        "palette": "Propose dark technical surfaces with readable semantic text and limited accents; exact Daily colors and contrast remain for integrated review.",
        "scene_backdrop": "Use the Instrument grid only as subtle background decoration behind bounded content; keep it absent under Effects Off.",
        "surfaces": "Propose clearly bounded rectangular row groupings and visible separators, without gauges, chart axes, AQI tiles, or derived indicators.",
        "weather_art_relationship": "Use optional marks only beside supplied condition text. Show the contract’s date, numeric low/high or honest unavailability, precipitation meaning, and named window controls without source-invented fields."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static canvas and bounded row surfaces with all supplied Daily facts, source/status text, and named controls present.",
        "high_contrast": "Strengthen text and panel boundaries with opaque surfaces; grid and accent color cannot carry selection, status, or data meaning.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, let technical rows expand or scroll; preserve all visible facts, control labels, and earliest-to-latest chronology."
      },
      "source_gaps": [
        "No source shows an Instrument Daily list, selected Daily state, precipitation treatment, or Earlier/Later controls; visible Daily tabs are selector labels only.",
        "No dedicated Daily state, responsive, loading/stale, High contrast, or Effects Off reference is indexed."
      ],
      "derivation_basis": [
        "instrument-phone",
        "instrument-asset-sheet",
        "overview-board",
        "instrument-backdrop"
      ],
      "rationale": "Instrument identity is grounded in its sourced grid and outlined modules, while the Daily composition is explicitly proposed because all Daily sources stop at a selector label.",
      "limitations": [
        "Neither the phone, asset sheet, nor board supplies a Daily content region; no row geometry or source fidelity is claimed.",
        "Backdrop evidence supports only a broad grid field and cannot establish module, color, or responsive behavior."
      ]
    },
    {
      "id": "terminal-daily",
      "theme": "terminal",
      "page": "daily",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "terminal-phone",
          "locator": "DAILY FORECAST block in the lower inner phone display, approximately x=38..282 and y=561..671 px: four ruled monospace date rows with values and marks.",
          "supports": "The compact Daily preview uses monospace date/value rows between dashed rules with small condition marks; four sample rows do not define the full horizon.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "overview-board",
          "locator": "Terminal phone column, lower DAILY FORECAST block around board x=1252..1517 and y=607..713 px; inspect dashed separators and four monospace date rows.",
          "supports": "The overview corroborates the Terminal preview’s green monospace text, dashed rules, and compact Daily date/value rows.",
          "evidence_class": "direct_region"
        },
        {
          "source_id": "terminal-backdrop",
          "locator": "Standalone Terminal backdrop full vertical field and dense rectangular grid; inspect separately from the phone’s sparse Daily separators.",
          "supports": "The standalone backdrop is a denser grid than the phone’s dashed row rules and does not define Daily list styling.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The Daily preview uses green monospace text on black with sparse separators; the standalone backdrop has a denser rectangular grid.",
        "scene_backdrop": "The compact phone combines a black console field with dashed rules; the separate full-field backdrop adds a denser grid texture.",
        "surfaces": "Four date/value rows appear between dashed horizontal rules without card surfaces.",
        "weather_art_relationship": "Small console-like condition marks appear at the right of visible date and low/high values; no precipitation line or Earlier/Later state is shown."
      },
      "interpretation": "Carry Terminal’s explicit monospace rows and rule-based grouping into Daily while keeping symbols subordinate to text and keeping backdrop grid density distinct from separators.",
      "proposed_treatment": {
        "palette": "Propose a black-first field, readable green/neutral text roles, and restrained status accents; the source does not establish exact tokens or contrast pairs.",
        "scene_backdrop": "Keep any denser grid subdued behind content and remove it in Effects Off; do not treat dashed list rules as backdrop evidence.",
        "surfaces": "Propose flat rows with explicit horizontal rules and no rounded cards or fabricated prompt syntax.",
        "weather_art_relationship": "Optional marks remain secondary to date, condition, labeled low/high, and available precipitation text. Keep the five-day window and visible named controls required by the product contract."
      },
      "state_constraints": {
        "effects_off": "Keep an opaque black static layout with text, rules, all supplied values, source/status, and named controls intact; remove grid/cursor effects.",
        "high_contrast": "Use strong text and visible separators on opaque surfaces; status, selection, and disabled states require words or semantics beyond green color.",
        "responsive_accessibility": "At compact width, font scale 1.3, and RTL, wrap or scroll field groups without reversing chronology or hiding named controls."
      },
      "source_gaps": [
        "No dedicated Daily page, complete horizon, precipitation line, Earlier/Later controls, or Terminal asset sheet is indexed.",
        "The phone/overview preview has four rows and no large-font, RTL, loading/stale, High contrast, or Effects Off state."
      ],
      "derivation_basis": [
        "terminal-phone",
        "overview-board",
        "terminal-backdrop"
      ],
      "rationale": "This proposal preserves Terminal’s source-visible console typography and sparse Daily separators while deriving the full page from written product requirements.",
      "limitations": [
        "The phone and overview are the same compact concept, not independent full-page evidence.",
        "The backdrop grid is denser than the phone separators; no exact grid scale, row pitch, colors, or responsive layout is inferred."
      ]
    },
    {
      "id": "atmospheric-details",
      "theme": "atmospheric",
      "page": "details",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "atmospheric-phone",
          "locator": "Phone crop, Now hero: blue sky field and warm horizon behind current-condition text; this is a same-theme cue, not a Details screen.",
          "supports": "The source shows blue upper field and warm horizon light; apply only this visible theme cue as a same-theme derivation for proposed Details grouping.",
          "evidence_class": "same_theme_derivation"
        },
        {
          "source_id": "atmospheric-backdrop",
          "locator": "Standalone Atmospheric backdrop field, including the audited center-column samples; field-level atmosphere only, not a Details composition.",
          "supports": "The standalone backdrop supplies the Atmospheric field treatment independently of the phone composition.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The same-theme phone cue uses blue upper field and warm horizon light; the separate backdrop is a field-only reference.",
        "scene_backdrop": "The cited phone is a combined concept and contains no dedicated Details screen; the backdrop supports only its own field treatment.",
        "surfaces": "The visible cue is blue upper field and warm horizon light; no source shows a Details group or provenance panel.",
        "weather_art_relationship": "The phone cue is part of a Now hero or block; no weather art is proposed as a Details fact, metric, status, chart, or action."
      },
      "interpretation": "Atmospheric atmosphere can carry into Details through the cited field or grouping cue, but this is a proposed same-theme derivation rather than direct Details evidence.",
      "proposed_treatment": {
        "palette": "open blue atmosphere with restrained warm horizon; keep group surfaces light and bounded. Preserve semantic text/status contrast; exact colors remain proposed.",
        "scene_backdrop": "Use a restrained Atmospheric field treatment only as decoration; Details facts remain understandable without it.",
        "surfaces": "Apply open blue atmosphere with restrained warm horizon; keep group surfaces light and bounded to full-width ordered groups without changing supplied strings or order.",
        "weather_art_relationship": "Keep weather art absent or strictly decorative; render only supplied source/update/status and ordered metric groups, with no invented data or actions."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static theme canvas and complete supplied Details content; remove optional texture, light, transparency, and motion.",
        "high_contrast": "Use opaque readable semantic text and visible boundaries; status and selection meaning cannot rely on color alone.",
        "responsive_accessibility": "At compact width and font scale 1.3, wrap and scroll all content; in RTL mirror alignment while preserving supplied group/metric order and source/update/status separation."
      },
      "source_gaps": [
        "No indexed source is a dedicated Details screen; phone, sheet, and overview references do not establish Details composition.",
        "No source establishes Details-specific group surfaces, provenance treatment, loading/stale state, or responsive behavior."
      ],
      "derivation_basis": [
        "atmospheric-phone",
        "atmospheric-backdrop"
      ],
      "rationale": "The proposal carries the Atmospheric identity through the concrete same-theme cue and its separately cited backdrop while preserving the Details audit contract.",
      "limitations": [
        "This is a proposed same-theme derivation, not direct Details visual evidence or owner approval.",
        "Source references do not establish installed contrast, responsive fit, or runtime rendering."
      ]
    },
    {
      "id": "glass-details",
      "theme": "glass",
      "page": "details",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "glass-phone",
          "locator": "Phone crop, Now hero card: translucent surface against cool blue-violet field and warm light; this is a same-theme cue, not a Details screen.",
          "supports": "The source shows cool blue-violet field, warm light, and layered translucent hero surface; apply only this visible theme cue as a same-theme derivation for proposed Details grouping.",
          "evidence_class": "same_theme_derivation"
        },
        {
          "source_id": "glass-backdrop",
          "locator": "Standalone Glass backdrop field, including the audited center-column samples; field-level atmosphere only, not a Details composition.",
          "supports": "The standalone backdrop supplies the Glass field treatment independently of the phone composition.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The same-theme phone cue uses cool blue-violet field, warm light, and layered translucent hero surface; the separate backdrop is a field-only reference.",
        "scene_backdrop": "The cited phone is a combined concept and contains no dedicated Details screen; the backdrop supports only its own field treatment.",
        "surfaces": "The visible cue is cool blue-violet field, warm light, and layered translucent hero surface; no source shows a Details group or provenance panel.",
        "weather_art_relationship": "The phone cue is part of a Now hero or block; no weather art is proposed as a Details fact, metric, status, chart, or action."
      },
      "interpretation": "Glass atmosphere can carry into Details through the cited field or grouping cue, but this is a proposed same-theme derivation rather than direct Details evidence.",
      "proposed_treatment": {
        "palette": "cool blue-violet depth with restrained warm highlights; use readable layered surfaces with opaque fallbacks. Preserve semantic text/status contrast; exact colors remain proposed.",
        "scene_backdrop": "Use a restrained Glass field treatment only as decoration; Details facts remain understandable without it.",
        "surfaces": "Apply cool blue-violet depth with restrained warm highlights; use readable layered surfaces with opaque fallbacks to full-width ordered groups without changing supplied strings or order.",
        "weather_art_relationship": "Keep weather art absent or strictly decorative; render only supplied source/update/status and ordered metric groups, with no invented data or actions."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static theme canvas and complete supplied Details content; remove optional texture, light, transparency, and motion.",
        "high_contrast": "Use opaque readable semantic text and visible boundaries; status and selection meaning cannot rely on color alone.",
        "responsive_accessibility": "At compact width and font scale 1.3, wrap and scroll all content; in RTL mirror alignment while preserving supplied group/metric order and source/update/status separation."
      },
      "source_gaps": [
        "No indexed source is a dedicated Details screen; phone, sheet, and overview references do not establish Details composition.",
        "No source establishes Details-specific group surfaces, provenance treatment, loading/stale state, or responsive behavior."
      ],
      "derivation_basis": [
        "glass-phone",
        "glass-backdrop"
      ],
      "rationale": "The proposal carries the Glass identity through the concrete same-theme cue and its separately cited backdrop while preserving the Details audit contract.",
      "limitations": [
        "This is a proposed same-theme derivation, not direct Details visual evidence or owner approval.",
        "Source references do not establish installed contrast, responsive fit, or runtime rendering."
      ]
    },
    {
      "id": "minimal_oled-details",
      "theme": "minimal_oled",
      "page": "details",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "minimal-oled-phone",
          "locator": "Phone crop, Now header and hero through first separator: black-first field and thin rule; this is a same-theme cue, not a Details screen.",
          "supports": "The source shows black-first phone field with thin horizontal separators; apply only this visible theme cue as a same-theme derivation for proposed Details grouping.",
          "evidence_class": "same_theme_derivation"
        },
        {
          "source_id": "minimal-oled-backdrop",
          "locator": "Standalone Minimal OLED backdrop field, including the audited center-column samples; field-level atmosphere only, not a Details composition.",
          "supports": "The standalone backdrop supplies the Minimal OLED field treatment independently of the phone composition.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The same-theme phone cue uses black-first phone field with thin horizontal separators; the separate backdrop is a field-only reference.",
        "scene_backdrop": "The cited phone is a combined concept and contains no dedicated Details screen; the backdrop supports only its own field treatment.",
        "surfaces": "The visible cue is black-first phone field with thin horizontal separators; no source shows a Details group or provenance panel.",
        "weather_art_relationship": "The phone cue is part of a Now hero or block; no weather art is proposed as a Details fact, metric, status, chart, or action."
      },
      "interpretation": "Minimal OLED atmosphere can carry into Details through the cited field or grouping cue, but this is a proposed same-theme derivation rather than direct Details evidence.",
      "proposed_treatment": {
        "palette": "black-first field with typography and thin rules carrying group boundaries; avoid broad filled cards. Preserve semantic text/status contrast; exact colors remain proposed.",
        "scene_backdrop": "Use a restrained Minimal OLED field treatment only as decoration; Details facts remain understandable without it.",
        "surfaces": "Apply black-first field with typography and thin rules carrying group boundaries; avoid broad filled cards to full-width ordered groups without changing supplied strings or order.",
        "weather_art_relationship": "Keep weather art absent or strictly decorative; render only supplied source/update/status and ordered metric groups, with no invented data or actions."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static theme canvas and complete supplied Details content; remove optional texture, light, transparency, and motion.",
        "high_contrast": "Use opaque readable semantic text and visible boundaries; status and selection meaning cannot rely on color alone.",
        "responsive_accessibility": "At compact width and font scale 1.3, wrap and scroll all content; in RTL mirror alignment while preserving supplied group/metric order and source/update/status separation."
      },
      "source_gaps": [
        "No indexed source is a dedicated Details screen; phone, sheet, and overview references do not establish Details composition.",
        "No source establishes Details-specific group surfaces, provenance treatment, loading/stale state, or responsive behavior."
      ],
      "derivation_basis": [
        "minimal-oled-phone",
        "minimal-oled-backdrop"
      ],
      "rationale": "The proposal carries the Minimal OLED identity through the concrete same-theme cue and its separately cited backdrop while preserving the Details audit contract.",
      "limitations": [
        "This is a proposed same-theme derivation, not direct Details visual evidence or owner approval.",
        "Source references do not establish installed contrast, responsive fit, or runtime rendering."
      ]
    },
    {
      "id": "instrument-details",
      "theme": "instrument",
      "page": "details",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "instrument-phone",
          "locator": "Phone crop, top current-condition module: bounded dark surface and fine outline; this is a same-theme cue, not a Details screen.",
          "supports": "The source shows bounded dark module with fine technical outlines and grid field; apply only this visible theme cue as a same-theme derivation for proposed Details grouping.",
          "evidence_class": "same_theme_derivation"
        },
        {
          "source_id": "instrument-backdrop",
          "locator": "Standalone Instrument backdrop field, including the audited center-column samples; field-level atmosphere only, not a Details composition.",
          "supports": "The standalone backdrop supplies the Instrument field treatment independently of the phone composition.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The same-theme phone cue uses bounded dark module with fine technical outlines and grid field; the separate backdrop is a field-only reference.",
        "scene_backdrop": "The cited phone is a combined concept and contains no dedicated Details screen; the backdrop supports only its own field treatment.",
        "surfaces": "The visible cue is bounded dark module with fine technical outlines and grid field; no source shows a Details group or provenance panel.",
        "weather_art_relationship": "The phone cue is part of a Now hero or block; no weather art is proposed as a Details fact, metric, status, chart, or action."
      },
      "interpretation": "Instrument atmosphere can carry into Details through the cited field or grouping cue, but this is a proposed same-theme derivation rather than direct Details evidence.",
      "proposed_treatment": {
        "palette": "near-black navy field, restrained grid texture, and ordered outlined panels; no gauge or chart. Preserve semantic text/status contrast; exact colors remain proposed.",
        "scene_backdrop": "Use a restrained Instrument field treatment only as decoration; Details facts remain understandable without it.",
        "surfaces": "Apply near-black navy field, restrained grid texture, and ordered outlined panels; no gauge or chart to full-width ordered groups without changing supplied strings or order.",
        "weather_art_relationship": "Keep weather art absent or strictly decorative; render only supplied source/update/status and ordered metric groups, with no invented data or actions."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static theme canvas and complete supplied Details content; remove optional texture, light, transparency, and motion.",
        "high_contrast": "Use opaque readable semantic text and visible boundaries; status and selection meaning cannot rely on color alone.",
        "responsive_accessibility": "At compact width and font scale 1.3, wrap and scroll all content; in RTL mirror alignment while preserving supplied group/metric order and source/update/status separation."
      },
      "source_gaps": [
        "No indexed source is a dedicated Details screen; phone, sheet, and overview references do not establish Details composition.",
        "No source establishes Details-specific group surfaces, provenance treatment, loading/stale state, or responsive behavior."
      ],
      "derivation_basis": [
        "instrument-phone",
        "instrument-backdrop"
      ],
      "rationale": "The proposal carries the Instrument identity through the concrete same-theme cue and its separately cited backdrop while preserving the Details audit contract.",
      "limitations": [
        "This is a proposed same-theme derivation, not direct Details visual evidence or owner approval.",
        "Source references do not establish installed contrast, responsive fit, or runtime rendering."
      ]
    },
    {
      "id": "terminal-details",
      "theme": "terminal",
      "page": "details",
      "review_status": "proposed",
      "source_refs": [
        {
          "source_id": "terminal-phone",
          "locator": "Phone crop, Now block through first dashed divider: green monospace text and rule on black; this is a same-theme cue, not a Details screen.",
          "supports": "The source shows green monospace text and dashed group separators on black; apply only this visible theme cue as a same-theme derivation for proposed Details grouping.",
          "evidence_class": "same_theme_derivation"
        },
        {
          "source_id": "terminal-backdrop",
          "locator": "Standalone Terminal backdrop field, including the audited center-column samples; field-level atmosphere only, not a Details composition.",
          "supports": "The standalone backdrop supplies the Terminal field treatment independently of the phone composition.",
          "evidence_class": "direct_region"
        }
      ],
      "observation": {
        "palette": "The same-theme phone cue uses green monospace text and dashed group separators on black; the separate backdrop is a field-only reference.",
        "scene_backdrop": "The cited phone is a combined concept and contains no dedicated Details screen; the backdrop supports only its own field treatment.",
        "surfaces": "The visible cue is green monospace text and dashed group separators on black; no source shows a Details group or provenance panel.",
        "weather_art_relationship": "The phone cue is part of a Now hero or block; no weather art is proposed as a Details fact, metric, status, chart, or action."
      },
      "interpretation": "Terminal atmosphere can carry into Details through the cited field or grouping cue, but this is a proposed same-theme derivation rather than direct Details evidence.",
      "proposed_treatment": {
        "palette": "black console-like field with green semantic accents and static rules; do not add console content fields. Preserve semantic text/status contrast; exact colors remain proposed.",
        "scene_backdrop": "Use a restrained Terminal field treatment only as decoration; Details facts remain understandable without it.",
        "surfaces": "Apply black console-like field with green semantic accents and static rules; do not add console content fields to full-width ordered groups without changing supplied strings or order.",
        "weather_art_relationship": "Keep weather art absent or strictly decorative; render only supplied source/update/status and ordered metric groups, with no invented data or actions."
      },
      "state_constraints": {
        "effects_off": "Use an opaque static theme canvas and complete supplied Details content; remove optional texture, light, transparency, and motion.",
        "high_contrast": "Use opaque readable semantic text and visible boundaries; status and selection meaning cannot rely on color alone.",
        "responsive_accessibility": "At compact width and font scale 1.3, wrap and scroll all content; in RTL mirror alignment while preserving supplied group/metric order and source/update/status separation."
      },
      "source_gaps": [
        "No indexed source is a dedicated Details screen; phone, sheet, and overview references do not establish Details composition.",
        "No source establishes Details-specific group surfaces, provenance treatment, loading/stale state, or responsive behavior."
      ],
      "derivation_basis": [
        "terminal-phone",
        "terminal-backdrop"
      ],
      "rationale": "The proposal carries the Terminal identity through the concrete same-theme cue and its separately cited backdrop while preserving the Details audit contract.",
      "limitations": [
        "This is a proposed same-theme derivation, not direct Details visual evidence or owner approval.",
        "Source references do not establish installed contrast, responsive fit, or runtime rendering."
      ]
    }
  ]
}

```

D31_PAGE_ATMOSPHERES:END

## Cross-theme distinction and Details limitation review

The five theme directions remain distinct: Atmospheric uses open blue and a
proposed scenic horizon; Glass layers translucent surfaces over cool, softly lit
depth; Minimal OLED keeps a black field and localized imagery; Instrument groups
facts in outlined modules over optional grid texture; Terminal uses console text
and explicit separators over a mostly flat field. Phone, sheet, and overview
cues are limited to the atmosphere they visibly show and do not define full-page
composition. Details proposals apply those cues by same-theme derivation only;
none treats the source as a Details screen. Each preserves exact supplied source,
update, and outer status separation and supplied group/metric order. No treatment
changes weather values, provenance, alert meaning, navigation, or accessibility
meaning.

## Owner review and remaining gates

All twenty canonical cells remain proposal records, and the integrated owner
review approved them as presented, including the five same-theme Details
derivations. The exact decisions are in [D31_OWNER_DECISION.md](D31_OWNER_DECISION.md).
The documentary review and owner decisions are complete. TP.1D/TP.1 closure, a
new exact packet revision and its explicit approval, and TP.2 eligibility remain
pending. No installed rendering or accessibility-service acceptance is claimed.
