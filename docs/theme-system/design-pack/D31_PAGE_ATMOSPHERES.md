# D31 page atmospheres — interim Now mapping

**Status: proposed; owner review pending.** This is a five-cell documentary
mapping proposal for Now. It records evidence, interpretation, and proposed
application separately. The combined phone concepts and overview board are not
full-page composition authority. Product meaning, accessibility, and
navigation remain governed by `docs/SPECIFICATION.md` and
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.

## Method and limits

I reviewed the five native extracted phone crops, the overview board, each
theme's native backdrop, and the Glass and Instrument asset sheets at their
source resolution. Phone and sheet locators below point to the Now-relevant
hero/current-condition regions. Backdrop locators identify the audited center
column samples. The samples are source-audit point checks, not complete palette
measurements. No numeric appearance token is proposed here. Phone concepts
combine Now with forecast previews; they support visible atmosphere and surface
relationships only, not a complete page layout, data slot, or forecast
composition. Missing theme sheets are recorded as source gaps and provide no
visual evidence.

## Structured mapping

D31_PAGE_ATMOSPHERES:BEGIN

```json
{
  "schema_version": 1,
  "scope": {
    "themes": ["atmospheric", "glass", "minimal_oled", "instrument", "terminal"],
    "pages": ["now"],
    "cell_count": 5,
    "coverage": "partial"
  },
  "status": "proposed; owner review pending",
  "cells": [
    {
      "id": "atmospheric-now",
      "theme": "atmospheric",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {"source_id": "atmospheric-phone", "locator": "Phone inner display: Now hero from location header through condition text; exclude hourly panel below.", "supports": "Blue sky field, warm horizon, illustrated mountain and forest behind the Now hero; translucent forecast panels begin below it.", "evidence_class": "direct_region"},
        {"source_id": "atmospheric-backdrop", "locator": "Backdrop center column at audited y=400, 1600, and 2800 samples; inspect full vertical field for context.", "supports": "Standalone blue field shifts from darker upper area toward a lighter lower area; it is visually distinct from the phone's illustrated landscape.", "evidence_class": "direct_region"}
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
      "source_gaps": ["No Atmospheric asset sheet or dedicated Now-only page source is indexed.", "No approved standalone landscape scene, weather-state variants, or page-specific surface measurements are available."],
      "derivation_basis": ["atmospheric-phone", "atmospheric-backdrop"],
      "rationale": "This direction retains the blue, scenic, warm-horizon identity visible in the Atmospheric Now crop while acknowledging that its separate backdrop does not contain the illustrated landscape.",
      "limitations": ["The phone is a small composite concept and cannot define the full Now page or exact surface geometry.", "The source review does not establish installed contrast, responsive fit, or owner acceptance."]
    },
    {
      "id": "glass-now",
      "theme": "glass",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {"source_id": "glass-phone", "locator": "Phone inner display: Now hero card from location header through condition and apparent-temperature text.", "supports": "A rounded translucent hero card places white current-condition text over a cool blue-violet field with warm light toward the right edge.", "evidence_class": "direct_region"},
        {"source_id": "glass-backdrop", "locator": "Backdrop full vertical field, including audited center column at y=400, 1600, and 2800.", "supports": "The backdrop shifts through dark blue, blue-gray, and violet-blue regions and includes large warm and cool light forms.", "evidence_class": "direct_region"},
        {"source_id": "glass-asset-sheet", "locator": "08 SCREEN EXAMPLE panel, lower-right; inspect the current-condition hero and visible translucent layers.", "supports": "The sheet's Now example uses translucent layered surfaces over a blue-violet atmospheric field with warm light.", "evidence_class": "direct_region"}
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
      "source_gaps": ["No dedicated Now-only page or loading, stale, error, and Effects Off references are present.", "The sheet and phone do not establish responsive surface opacity or contrast behavior."],
      "derivation_basis": ["glass-phone", "glass-backdrop", "glass-asset-sheet"],
      "rationale": "The proposal carries forward Glass's visible layered transparency, cool depth, and restrained warm light while defining accessible opaque fallbacks as proposed state behavior.",
      "limitations": ["The combined concept includes other page previews and is not authority for Now composition.", "No rendered contrast, blur, or responsive values were measured in this review."]
    },
    {
      "id": "minimal_oled-now",
      "theme": "minimal_oled",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {"source_id": "minimal-oled-phone", "locator": "Phone inner display: Now header, temperature, condition, and localized moon/cloud image above the first separator.", "supports": "The screen is black-first, uses thin horizontal separators, and confines a small moon/cloud image to the Now hero.", "evidence_class": "direct_region"},
        {"source_id": "minimal-oled-backdrop", "locator": "Backdrop center column at the audited y=400, 1600, and 2800 samples; inspect the uninterrupted black field.", "supports": "The standalone backdrop is black at the audited points, with no scenic color field or panel treatment.", "evidence_class": "direct_region"}
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
      "source_gaps": ["No Minimal OLED asset sheet is indexed.", "No dedicated Now-only source or alternate state reference specifies imagery, separators, or responsive behavior."],
      "derivation_basis": ["minimal-oled-phone", "minimal-oled-backdrop"],
      "rationale": "The proposal preserves the source's black canvas and typography-first hierarchy while limiting weather art to an optional local accent.",
      "limitations": ["The concept combines several forecast periods and does not define the final Now content order.", "The backdrop samples are point checks, not full black-level or display-power measurements."]
    },
    {
      "id": "instrument-now",
      "theme": "instrument",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {"source_id": "instrument-phone", "locator": "Phone inner display: top current-condition module from location/time header through condition and apparent-temperature text.", "supports": "The hero uses a dark bounded module with a segmented circular condition graphic, dominant temperature, and explicit condition text.", "evidence_class": "direct_region"},
        {"source_id": "instrument-backdrop", "locator": "Backdrop full field and audited center-column samples at y=400, 1600, and 2800, including the grid texture.", "supports": "A fine rectangular grid sits over a near-black blue field; audited transparent grid samples remain alpha-bearing source values.", "evidence_class": "direct_region"},
        {"source_id": "instrument-asset-sheet", "locator": "08 SCREEN EXAMPLE panel, lower-right; inspect the Now module, outline, and neighboring text labels.", "supports": "The sheet repeats bounded outlined modules, dark navy surfaces, and a technical current-condition treatment.", "evidence_class": "direct_region"}
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
      "source_gaps": ["The reference gauge and chart do not provide typed measurements or valid runtime ranges.", "No page-state references establish grid density, module behavior, or a dedicated Now layout."],
      "derivation_basis": ["instrument-phone", "instrument-backdrop", "instrument-asset-sheet"],
      "rationale": "The proposal keeps Instrument distinct through grid texture, outlined modules, and technical grouping while explicitly preventing decorative instruments from implying unsupported measurements.",
      "limitations": ["The phone concept is a composite and includes unsupported chart and gauge examples outside this proposal.", "The transparent grid sample is not a composited display color or a contrast measurement."]
    },
    {
      "id": "terminal-now",
      "theme": "terminal",
      "page": "now",
      "review_status": "proposed",
      "source_refs": [
        {"source_id": "terminal-phone", "locator": "Phone inner display: Now block from WX location/time header through the condition text and first dashed divider.", "supports": "The Now block uses green monospace text, a localized ASCII-like weather mark, and dashed horizontal separators on a black field.", "evidence_class": "direct_region"},
        {"source_id": "terminal-backdrop", "locator": "Backdrop full field and audited center-column samples at y=400, 1600, and 2800, including the dense rectangular grid.", "supports": "The standalone backdrop uses a dense grid over black; the phone display uses a visibly different sparse dashed-rule treatment.", "evidence_class": "direct_region"}
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
      "source_gaps": ["No Terminal asset sheet or dedicated Now-only page source is indexed.", "No source defines grid density, accessible console control treatment, or alternate page states."],
      "derivation_basis": ["terminal-phone", "terminal-backdrop"],
      "rationale": "The proposal retains Terminal's console-like text hierarchy and explicit separators while treating the denser standalone grid as optional backdrop evidence.",
      "limitations": ["The phone is a small combined-page concept rather than a complete Now specification.", "No numeric grid or type measurements are claimed by this qualitative mapping."]
    }
  ]
}
```

D31_PAGE_ATMOSPHERES:END

## Cross-theme distinction review

The proposals retain five different atmosphere relationships: Atmospheric uses
open blue and a proposed scenic horizon; Glass layers translucent surfaces over
cool, softly lit depth; Minimal OLED keeps a black field and localized imagery;
Instrument groups facts in outlined modules over optional grid texture; Terminal
uses console text and explicit separators over a mostly flat field. These are
documented proposals, not owner-approved treatments. No treatment changes
weather values, provenance, alert meaning, navigation, or accessibility meaning.

## Remaining work

This interim artifact contains only the five proposed Now cells. The dependent
cycle `051-d31-page-atmosphere-mapping-partial2` owns the five Hourly cells.
Daily and Details, integrated review of all twenty cells, reviewable source
reproductions, any proposed derivations, and required owner decisions remain
open. D31, TP.1D, and TP.1 are not complete; packet approval and TP.2 eligibility
remain gated.
