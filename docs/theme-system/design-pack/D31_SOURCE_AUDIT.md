# D31 Source Audit — five-theme atmospheres

**Status:** source audit and proposed design input only. This document records what the indexed sources show; it does not define, decide, or approve any theme or page treatment. Product and accessibility authority remains in `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; D31 scope is in `docs/theme-pack-roadmap.md`; source identity is governed by the production reference index and `docs/theme-system/ASSET_MANIFEST.json`.

## Method and confidence

Follow `REFERENCE_MEASUREMENT_METHOD.md`: use theme-specific phone crops for theme comparison, identify the visible screen region, and treat overview composition as evidence only for its shown atmosphere. Pixel dimensions and SHA-256 values below are taken from the native files and manifest. The numeric colors are direct single-pixel 8-bit RGB reads from each 1440×3200 backdrop at x=720 and y=400, 1600, and 2800; no averaging, resampling, or color-profile conversion was applied. Alpha-bearing pixels are recorded as alpha values where applicable, not composited colors. These samples are point checks, not a complete palette extraction. Phone and board geometry descriptions are qualitative; approximate screen/crop bounds in locators are inspection aids, not dp measurements. No texture or shape is assigned a numeric value unless identified as a direct sample.

## Source inventory

The JSON block is the machine-checked inventory. Locators name native-image regions. `Cross-theme` is used for the shared overview board; the absent sheet records explicitly assert no path or digest. The individual absent-sheet record is not a visual reference.

```json
{
  "inventory": [
    {
      "id": "overview-board",
      "theme": "Cross-theme",
      "source_class": "overview_board",
      "path": "docs/assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png",
      "sha256": "289f97ef8234a6e410bb4918921ba7729b3d2c3ef527f6baa210dd7365348554",
      "dimensions_px": [
        1536,
        1024
      ],
      "locator": "Native board x=0..1535, y=0..1023; five phone columns occupy the central field, with title/footer outside the phone viewports.",
      "coverage": "All five theme atmospheres as composed in the comparison board.",
      "limitation": "Composite board; use extracted phone crops for theme-specific close inspection; board composition is not a page target."
    },
    {
      "id": "atmospheric-phone",
      "theme": "Atmospheric",
      "source_class": "phone_crop",
      "path": "docs/assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png",
      "sha256": "ead18706370d65111ae6d8398f7bd7c2526c32aff000d1935ea59dae19c7e844",
      "dimensions_px": [
        302,
        745
      ],
      "locator": "Full extracted phone crop; inspect inner display from y≈35 to y≈725, excluding bezel/status frame; native pixels.",
      "coverage": "Visible combined Now, hourly strip, and daily preview; theme-specific atmosphere and surface relationships.",
      "limitation": "Small concept screen with combined page content; no dedicated Hourly, Daily, Details, loading, stale, error, or Effects Off state."
    },
    {
      "id": "atmospheric-backdrop",
      "theme": "Atmospheric",
      "source_class": "backdrop",
      "path": "docs/assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png",
      "sha256": "f246dd860b963654d9f96723e06b71ac0a22424a736fd403f18e79e91de97c73",
      "dimensions_px": [
        1440,
        3200
      ],
      "locator": "Full 1440×3200 image; center column x=720 sampled at y=400,1600,2800 (coordinates and RGB in profile).",
      "coverage": "Theme backdrop color field and any scene, grid, glow, or texture.",
      "limitation": "Backdrop is a standalone visual; it does not specify page overlays, responsive cropping, or behavior by weather state."
    },
    {
      "id": "atmospheric-asset-sheet-absent",
      "theme": "Atmospheric",
      "source_class": "absent_asset_sheet",
      "path": null,
      "sha256": null,
      "dimensions_px": null,
      "locator": "Repository source index and manifest search for {theme}/boards/*theme_asset_sheet*; no equivalent file is indexed.",
      "coverage": "Explicit absence of a theme asset-sheet source.",
      "limitation": "No sheet-derived colors, shapes, or treatments may be inferred for this theme."
    },
    {
      "id": "glass-phone",
      "theme": "Glass",
      "source_class": "phone_crop",
      "path": "docs/assets/design-references/production-themes/one-app-many-personalities/extracted/glass_phone.png",
      "sha256": "2fae2ffd8a832e32870d811a57159543a1a5b08d73b7aa533901c56f6ebd94ea",
      "dimensions_px": [
        302,
        745
      ],
      "locator": "Full extracted phone crop; inspect inner display from y≈35 to y≈725, excluding bezel/status frame; native pixels.",
      "coverage": "Visible combined Now, hourly strip, and daily preview; theme-specific atmosphere and surface relationships.",
      "limitation": "Small concept screen with combined page content; no dedicated Hourly, Daily, Details, loading, stale, error, or Effects Off state."
    },
    {
      "id": "glass-backdrop",
      "theme": "Glass",
      "source_class": "backdrop",
      "path": "docs/assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png",
      "sha256": "30c81e3783c0df301360cdec0a3aa08d4970e2a473e4cf084e44b8fa79a6cb56",
      "dimensions_px": [
        1440,
        3200
      ],
      "locator": "Full 1440×3200 image; center column x=720 sampled at y=400,1600,2800 (coordinates and RGB in profile).",
      "coverage": "Theme backdrop color field and any scene, grid, glow, or texture.",
      "limitation": "Backdrop is a standalone visual; it does not specify page overlays, responsive cropping, or behavior by weather state."
    },
    {
      "id": "glass-asset-sheet",
      "theme": "Glass",
      "source_class": "asset_sheet",
      "path": "docs/assets/design-references/production-themes/glass/boards/glass_theme_asset_sheet.png",
      "sha256": "74e834c76e148d561d5f4696c9784b3e1131bd31089f13878d1863954eb85860",
      "dimensions_px": [
        1448,
        1086
      ],
      "locator": "Glass: 08 SCREEN EXAMPLE panel, lower-right (approx x=1100..1435,y=700..1040); plus full sheet backdrop/palette panels. Instrument: corresponding 08 SCREEN EXAMPLE lower-right panel and full sheet.",
      "coverage": "Theme atmosphere as integrated with the sheet screen sample, plus backdrop/palette context.",
      "limitation": "Sheet is a composite specification board; small screen sample and palette swatches do not specify every page/state."
    },
    {
      "id": "minimal-oled-phone",
      "theme": "Minimal OLED",
      "source_class": "phone_crop",
      "path": "docs/assets/design-references/production-themes/one-app-many-personalities/extracted/minimal_oled_phone.png",
      "sha256": "cd0b454612bc2edcc2e3b3989dfc7445d838efed2f45fac31800ead53795517b",
      "dimensions_px": [
        302,
        745
      ],
      "locator": "Full extracted phone crop; inspect inner display from y≈35 to y≈725, excluding bezel/status frame; native pixels.",
      "coverage": "Visible combined Now, hourly strip, and daily preview; theme-specific atmosphere and surface relationships.",
      "limitation": "Small concept screen with combined page content; no dedicated Hourly, Daily, Details, loading, stale, error, or Effects Off state."
    },
    {
      "id": "minimal-oled-backdrop",
      "theme": "Minimal OLED",
      "source_class": "backdrop",
      "path": "docs/assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png",
      "sha256": "3289ae4838bd2fee459921025d00f3c3d33b96d3982efb6afd09e5f17f20ecd3",
      "dimensions_px": [
        1440,
        3200
      ],
      "locator": "Full 1440×3200 image; center column x=720 sampled at y=400,1600,2800 (coordinates and RGB in profile).",
      "coverage": "Theme backdrop color field and any scene, grid, glow, or texture.",
      "limitation": "Backdrop is a standalone visual; it does not specify page overlays, responsive cropping, or behavior by weather state."
    },
    {
      "id": "minimal-oled-asset-sheet-absent",
      "theme": "Minimal OLED",
      "source_class": "absent_asset_sheet",
      "path": null,
      "sha256": null,
      "dimensions_px": null,
      "locator": "Repository source index and manifest search for {theme}/boards/*theme_asset_sheet*; no equivalent file is indexed.",
      "coverage": "Explicit absence of a theme asset-sheet source.",
      "limitation": "No sheet-derived colors, shapes, or treatments may be inferred for this theme."
    },
    {
      "id": "instrument-phone",
      "theme": "Instrument",
      "source_class": "phone_crop",
      "path": "docs/assets/design-references/production-themes/one-app-many-personalities/extracted/instrument_phone.png",
      "sha256": "120a190d45d88af6c317a84dd67bd6e68d22cbb6016c421b48156cbefbe75e1b",
      "dimensions_px": [
        302,
        745
      ],
      "locator": "Full extracted phone crop; inspect inner display from y≈35 to y≈725, excluding bezel/status frame; native pixels.",
      "coverage": "Visible combined Now, hourly strip, and daily preview; theme-specific atmosphere and surface relationships.",
      "limitation": "Small concept screen with combined page content; no dedicated Hourly, Daily, Details, loading, stale, error, or Effects Off state."
    },
    {
      "id": "instrument-backdrop",
      "theme": "Instrument",
      "source_class": "backdrop",
      "path": "docs/assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png",
      "sha256": "82612a2f6abb7be342975c405634293fec81367a275f625fd04e1e1ebaf92b22",
      "dimensions_px": [
        1440,
        3200
      ],
      "locator": "Full 1440×3200 image; center column x=720 sampled at y=400,1600,2800 (coordinates and RGB in profile).",
      "coverage": "Theme backdrop color field and any scene, grid, glow, or texture.",
      "limitation": "Backdrop is a standalone visual; it does not specify page overlays, responsive cropping, or behavior by weather state."
    },
    {
      "id": "instrument-asset-sheet",
      "theme": "Instrument",
      "source_class": "asset_sheet",
      "path": "docs/assets/design-references/production-themes/instrument/boards/instrument_theme_asset_sheet.png",
      "sha256": "0a99c3b80bef14e70358bdc4859d88791e944203d150104427a3ea43bdd39dcd",
      "dimensions_px": [
        1448,
        1086
      ],
      "locator": "Glass: 08 SCREEN EXAMPLE panel, lower-right (approx x=1100..1435,y=700..1040); plus full sheet backdrop/palette panels. Instrument: corresponding 08 SCREEN EXAMPLE lower-right panel and full sheet.",
      "coverage": "Theme atmosphere as integrated with the sheet screen sample, plus backdrop/palette context.",
      "limitation": "Sheet is a composite specification board; small screen sample and palette swatches do not specify every page/state."
    },
    {
      "id": "terminal-phone",
      "theme": "Terminal",
      "source_class": "phone_crop",
      "path": "docs/assets/design-references/production-themes/one-app-many-personalities/extracted/terminal_phone.png",
      "sha256": "f16c891ed79df2f88d6853a3ec03312cb1d4a2b5803ab93b93a7fe27e74733f6",
      "dimensions_px": [
        315,
        745
      ],
      "locator": "Full extracted phone crop; inspect inner display from y≈35 to y≈725, excluding bezel/status frame; native pixels.",
      "coverage": "Visible combined Now, hourly strip, and daily preview; theme-specific atmosphere and surface relationships.",
      "limitation": "Small concept screen with combined page content; no dedicated Hourly, Daily, Details, loading, stale, error, or Effects Off state."
    },
    {
      "id": "terminal-backdrop",
      "theme": "Terminal",
      "source_class": "backdrop",
      "path": "docs/assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png",
      "sha256": "5cb511e9a05b7dbfb7775ea1385961d56ed2d726438d55e5c747f51a661448a2",
      "dimensions_px": [
        1440,
        3200
      ],
      "locator": "Full 1440×3200 image; center column x=720 sampled at y=400,1600,2800 (coordinates and RGB in profile).",
      "coverage": "Theme backdrop color field and any scene, grid, glow, or texture.",
      "limitation": "Backdrop is a standalone visual; it does not specify page overlays, responsive cropping, or behavior by weather state."
    },
    {
      "id": "terminal-asset-sheet-absent",
      "theme": "Terminal",
      "source_class": "absent_asset_sheet",
      "path": null,
      "sha256": null,
      "dimensions_px": null,
      "locator": "Repository source index and manifest search for {theme}/boards/*theme_asset_sheet*; no equivalent file is indexed.",
      "coverage": "Explicit absence of a theme asset-sheet source.",
      "limitation": "No sheet-derived colors, shapes, or treatments may be inferred for this theme."
    }
  ],
  "profiles": {
    "Atmospheric": {
      "sources_used": [
        "overview-board",
        "atmospheric-phone",
        "atmospheric-backdrop"
      ],
      "observation": "Phone crop: bright blue field transitions to a warm horizon and illustrated mountain/forest band in the lower half; hourly/daily previews sit on rounded translucent blue panels. Backdrop center-column direct samples (RGB, 8-bit, single pixel): (720,400)=(31,56,100), (720,1600)=(66,105,160), (720,2800)=(100,153,220), indicating a lighter lower region. The existing SOURCE_DECISIONS.md samples at (150,75), (150,180), (150,620), and (150,680) remain intact and retain their stated scenic-Now/Daily gap limitation.",
      "interpretation": "The phone image combines a scenic blue sky-to-landscape field with comparatively contained forecast panels. The standalone backdrop has a distinct vertical blue gradient and is not identical to all phone regions.",
      "unavailable_evidence": "No theme asset sheet; no dedicated page-specific atmosphere outside the combined preview; no complete scene asset or weather-state variants; backdrop does not define panel opacity or page mapping."
    },
    "Glass": {
      "sources_used": [
        "overview-board",
        "glass-phone",
        "glass-backdrop",
        "glass-asset-sheet"
      ],
      "observation": "Phone and sheet show rounded translucent panels layered over a blue-violet atmospheric field; the phone includes warm vertical light near its right edge and a mountain/forest lower scene. Backdrop center-column samples: (720,400)=(21,30,48), (720,1600)=(48,68,96), (720,2800)=(78,92,144). Values are direct 8-bit RGB samples.",
      "interpretation": "Atmosphere is carried by layered transparency, cool blue/violet depth, and restrained warm light; surface layering is part of the visible look rather than only the background.",
      "unavailable_evidence": "The sheet provides one compact screen example and asset panels, not complete page/state coverage. No weather-state mapping or page-specific scenes are shown."
    },
    "Minimal OLED": {
      "sources_used": [
        "overview-board",
        "minimal-oled-phone",
        "oled-backdrop"
      ],
      "observation": "The phone crop is black-first with thin horizontal separators and no broad filled cards; a small moon/cloud image is confined to the hero. The backdrop is solid black: RGB=(0,0,0) at center-column y=400,1600,2800.",
      "interpretation": "The scene is deliberately sparse and the display field remains visually subordinate to typography and data. The tiny hero image is localized and does not imply a full-screen scenic backdrop.",
      "unavailable_evidence": "No theme asset sheet; no measured alternate backdrop, state or page variants, or evidence for full-screen weather illustration."
    },
    "Instrument": {
      "sources_used": [
        "overview-board",
        "instrument-phone",
        "instrument-backdrop",
        "instrument-asset-sheet"
      ],
      "observation": "Phone and sheet show dark navy surfaces, thin outlined modules, a circular segmented condition gauge and compact chart-like data. The backdrop is a fine rectangular grid over near-black blue; at (720,400)=(5,17,23), (720,1600)=(white with alpha 20/255), and (720,2800)=(8,29,41). The alpha sample is a direct pixel value over a transparent grid cell.",
      "interpretation": "The grid and bounded modules make the atmosphere technical and instrument-like; chart and gauge artwork is visual source evidence only, not authority for weather data fields.",
      "unavailable_evidence": "The sheet/phone do not cover all pages, states, grid scaling, or selection-specific scene behavior. Decorative gauge values cannot authorize a runtime indicator without typed data."
    },
    "Terminal": {
      "sources_used": [
        "overview-board",
        "terminal-phone",
        "terminal-backdrop"
      ],
      "observation": "Phone uses a black field, green monospace text, dotted/dashed rules, and sparse ASCII-like weather marks. The standalone backdrop is a denser rectangular grid; center-column samples at (720,400)=(0,5,0), (720,1600)=(white with alpha 20/255), and (720,2800)=(0,0,0).",
      "interpretation": "The source combines console typography and green accents with a grid texture; the board screen and standalone backdrop show distinct grid densities and should remain separately attributable.",
      "unavailable_evidence": "No theme asset sheet; no complete page-specific layouts, state variants, grid density rules, or accent palette measurements across screens."
    }
  }
}
```

## Theme profiles

The observations below describe visible source evidence. Interpretations are separated and remain hypotheses for later design review. “Unavailable evidence” is explicit for every theme.

### Atmospheric

- **Sources used:** overview board, Atmospheric phone crop, Atmospheric backdrop.
- **Observation:** Phone crop: bright blue field transitions to a warm horizon and illustrated mountain/forest band in the lower half; hourly/daily previews sit on rounded translucent blue panels. Backdrop center-column direct samples (RGB, 8-bit, single pixel): (720,400)=(31,56,100), (720,1600)=(66,105,160), (720,2800)=(100,153,220), indicating a lighter lower region. The existing SOURCE_DECISIONS.md samples at (150,75), (150,180), (150,620), and (150,680) remain intact and retain their stated scenic-Now/Daily gap limitation.
- **Interpretation:** The phone image combines a scenic blue sky-to-landscape field with comparatively contained forecast panels. The standalone backdrop has a distinct vertical blue gradient and is not identical to all phone regions.
- **Unavailable evidence:** No theme asset sheet; no dedicated page-specific atmosphere outside the combined preview; no complete scene asset or weather-state variants; backdrop does not define panel opacity or page mapping.
- **Shape, texture, relationships:** illustrated mountain/forest silhouette and warm horizon are scene elements; the phone forecast panels are rounded and translucent. Qualitative only. No quantitative scene geometry is asserted.

### Glass

- **Sources used:** overview board, Glass phone crop, Glass backdrop, Glass asset sheet.
- **Observation:** Phone and sheet show rounded translucent panels layered over a blue-violet atmospheric field; the phone includes warm vertical light near its right edge and a mountain/forest lower scene. Backdrop center-column samples: (720,400)=(21,30,48), (720,1600)=(48,68,96), (720,2800)=(78,92,144). Values are direct 8-bit RGB samples.
- **Interpretation:** Atmosphere is carried by layered transparency, cool blue/violet depth, and restrained warm light; surface layering is part of the visible look rather than only the background.
- **Unavailable evidence:** The sheet provides one compact screen example and asset panels, not complete page/state coverage. No weather-state mapping or page-specific scenes are shown.
- **Shape, texture, relationships:** rounded panel edges and layered transparency are visible; the backdrop includes a scenic lower region and warm vertical light. Qualitative only.

### Minimal OLED

- **Sources used:** overview board, Minimal OLED phone crop, OLED backdrop.
- **Observation:** The phone crop is black-first with thin horizontal separators and no broad filled cards; a small moon/cloud image is confined to the hero. The backdrop is solid black: RGB=(0,0,0) at center-column y=400,1600,2800.
- **Interpretation:** The scene is deliberately sparse and the display field remains visually subordinate to typography and data. The tiny hero image is localized and does not imply a full-screen scenic backdrop.
- **Unavailable evidence:** No theme asset sheet; no measured alternate backdrop, state or page variants, or evidence for full-screen weather illustration.
- **Shape, texture, relationships:** thin rules separate content; the hero's moon/cloud is a small, isolated image. Qualitative only.

### Instrument

- **Sources used:** overview board, Instrument phone crop, Instrument backdrop, Instrument asset sheet.
- **Observation:** Phone and sheet show dark navy surfaces, thin outlined modules, a circular segmented condition gauge and compact chart-like data. The backdrop is a fine rectangular grid over near-black blue; at (720,400)=(5,17,23), (720,1600)=(white with alpha 20/255), and (720,2800)=(8,29,41). The alpha sample is a direct pixel value over a transparent grid cell.
- **Interpretation:** The grid and bounded modules make the atmosphere technical and instrument-like; chart and gauge artwork is visual source evidence only, not authority for weather data fields.
- **Unavailable evidence:** The sheet/phone do not cover all pages, states, grid scaling, or selection-specific scene behavior. Decorative gauge values cannot authorize a runtime indicator without typed data.
- **Shape, texture, relationships:** fine orthogonal grid, outlined rectangular modules, and a segmented circular gauge are visible. Gauge/chart values are source artwork, not a data specification. Qualitative only except backdrop pixel samples.

### Terminal

- **Sources used:** overview board, Terminal phone crop, Terminal backdrop.
- **Observation:** Phone uses a black field, green monospace text, dotted/dashed rules, and sparse ASCII-like weather marks. The standalone backdrop is a denser rectangular grid; center-column samples at (720,400)=(0,5,0), (720,1600)=(white with alpha 20/255), and (720,2800)=(0,0,0).
- **Interpretation:** The source combines console typography and green accents with a grid texture; the board screen and standalone backdrop show distinct grid densities and should remain separately attributable.
- **Unavailable evidence:** No theme asset sheet; no complete page-specific layouts, state variants, grid density rules, or accent palette measurements across screens.
- **Shape, texture, relationships:** dense orthogonal grid in the backdrop; dotted/dashed rules and console-like symbols in the phone. Qualitative only except backdrop pixel samples.

## Cross-theme comparison and gaps

The sources show five different vocabularies: Atmospheric uses a blue scenic sky-to-landscape field; Glass layers translucent rounded panels over a cool scenic field with warm light; Minimal OLED keeps a solid black field and localizes its small hero image; Instrument combines a dark grid with outlined modules and a segmented gauge; Terminal uses a darker, denser grid with green console text and rules. These descriptions preserve the differences and do not establish selected implementation values.

The overview phone crops combine Now with forecast previews; none supplies dedicated, complete source atmosphere for all Now, Hourly, Daily, and Details page states. Loading, stale, failure, unavailable-data, high-contrast, RTL, compact/large-font, reduced-motion, and Effects Off appearances are not comprehensively shown. No equivalent asset sheets are indexed for Atmospheric, Minimal OLED, or Terminal. These are evidence gaps for later D31 specification and integrated review, not permission to infer source artwork.

## Limitations and handoff

Later D31 work must specify the five-theme × four-page mapping, preserve direct-source versus proposed same-theme derivation, identify any review-proven source gaps before requesting supplementary sheets, and include all proposals in integrated review with explicit owner decisions. The owner-directed overview atmosphere is a reproduction target only where it appears; its composite layout is not a page target. This audit does not select palettes, resolve source gaps, approve a treatment, establish owner approval, or claim runtime reproduction. D31, TP.1D, and TP.1 remain open; TP.2 remains gated.
