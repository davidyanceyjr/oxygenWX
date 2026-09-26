# D31 integrated atmosphere review

**Status: owner-approved D31 proposal.** The design owner approved all twenty
theme/page atmosphere proposals and the overall set on 2026-09-25. Decisions
apply to the exact revisions identified in [D31_OWNER_DECISION.md](D31_OWNER_DECISION.md).
This does not approve a TP.1D packet, claim installed visual acceptance, close
TP.1D/TP.1, or unblock TP.2. Product meaning and accessibility are governed by
[`SPECIFICATION.md`](../../SPECIFICATION.md) and
[`OXYGEN_UI_SPECIFICATION_ADOPTED.md`](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md).

## Review protocol

The owner review is complete. For the decision record, see
[D31_OWNER_DECISION.md](D31_OWNER_DECISION.md). The exact source panels and
corresponding proposal in [`D31_PAGE_ATMOSPHERES.md`](D31_PAGE_ATMOSPHERES.md)
remain the reviewed basis. Distinguish what a source
visibly shows from the proposal's interpretation and treatment. Consider the
page contract, compact width, font scale 1.3, RTL, High contrast, and Effects
Off. Decorative atmosphere cannot carry weather meaning or reduce readable
facts. The decision fields below record the owner's approve decision for each cell.
The integrated disposition is separately recorded as approve; it was stated
explicitly and is not inferred from the cell decisions.

Panels are exact-source reproductions, not visual acceptance claims. Overview
regions are native crops from the board; the two sheet panels are native crops
of their cited screen-example region; backdrop panels link the original native
files. Nothing is recolored or redrawn. The overview's composite layout,
phone content, and neighboring sheet examples are not page-composition targets.

## Source reproduction inventory

| Panel | Theme/source | Native region and reproduction | Review locator |
|---|---|---|---|
| [Overview · Atmospheric](d31-integrated-review/overview-atmospheric.png) | Atmospheric · [overview board](../../assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png) | x=28, y=128, 264×712; untouched crop | Board's first phone display region; composite context only |
| [Backdrop · Atmospheric](../../assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png) | Atmospheric · standalone backdrop | 1440×3200; native file reference | Full field; distinct from the board's scenic landscape |
| [Overview · Glass](d31-integrated-review/overview-glass.png) | Glass · overview board | x=337, y=128, 266×712; untouched crop | Board's second phone display region |
| [Backdrop · Glass](../../assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png) | Glass · standalone backdrop | 1440×3200; native file reference | Full field and light forms |
| [Glass sheet example](d31-integrated-review/sheet-glass-screen-example.png) | Glass · [asset sheet](../../assets/design-references/production-themes/glass/boards/glass_theme_asset_sheet.png) | x=1100, y=365, 333×660; untouched crop | 08 SCREEN EXAMPLE panel, including heading and phone |
| [Overview · Minimal OLED](d31-integrated-review/overview-minimal-oled.png) | Minimal OLED · overview board | x=644, y=128, 258×712; untouched crop | Board's third phone display region |
| [Backdrop · Minimal OLED](../../assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png) | Minimal OLED · standalone backdrop | 1440×3200; native file reference | Full black field; does not establish full-screen weather art |
| [Overview · Instrument](d31-integrated-review/overview-instrument.png) | Instrument · overview board | x=943, y=128, 272×712; untouched crop | Board's fourth phone display region |
| [Backdrop · Instrument](../../assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png) | Instrument · standalone backdrop | 1440×3200; native file reference | Full grid field; decorative gauge values are not data authority |
| [Instrument sheet example](d31-integrated-review/sheet-instrument-screen-example.png) | Instrument · [asset sheet](../../assets/design-references/production-themes/instrument/boards/instrument_theme_asset_sheet.png) | x=1123, y=395, 312×639; untouched crop | 08 SCREEN EXAMPLE panel, including heading, phone, and border |
| [Overview · Terminal](d31-integrated-review/overview-terminal.png) | Terminal · overview board | x=1257, y=128, 255×712; untouched crop | Board's fifth phone display region |
| [Backdrop · Terminal](../../assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png) | Terminal · standalone backdrop | 1440×3200; native file reference | Full grid field; distinct from the board screen's grid density |

Exact source/output digests, native dimensions, bounds, and reproduction types follow and are machine-readable in [`panels.json`](d31-integrated-review/panels.json).

### Verifiable panel identities

This source ledger repeats each panel’s exact source and output identity for a standalone audit. SHA-256 values refer to the unchanged source asset and exact output file respectively.

| Panel | Source path · SHA-256 · native px | Crop bounds (x,y,w,h) | Output path · SHA-256 · px | Reproduction |
|---|---|---|---|---|
| `overview-atmospheric` | `docs/assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png` · `289f97ef8234a6e410bb4918921ba7729b3d2c3ef527f6baa210dd7365348554` · 1536×1024 | 28,128,264,712 | `docs/theme-system/design-pack/d31-integrated-review/overview-atmospheric.png` · `0eb037a7fbfdbfa76b4bf97ed8812400fbc59720a92910d17da6b8c9c8f14d72` · 264×712 | untouched_crop |
| `overview-glass` | `docs/assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png` · `289f97ef8234a6e410bb4918921ba7729b3d2c3ef527f6baa210dd7365348554` · 1536×1024 | 337,128,266,712 | `docs/theme-system/design-pack/d31-integrated-review/overview-glass.png` · `9943e835942156303f9df99baffbca15ef3648b54b9ce373c1241d5559f7190d` · 266×712 | untouched_crop |
| `overview-minimal-oled` | `docs/assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png` · `289f97ef8234a6e410bb4918921ba7729b3d2c3ef527f6baa210dd7365348554` · 1536×1024 | 644,128,258,712 | `docs/theme-system/design-pack/d31-integrated-review/overview-minimal-oled.png` · `43044695d22b9969b6882c38517b512399e4f08bf015b11b2ca9ed3dd77dde59` · 258×712 | untouched_crop |
| `overview-instrument` | `docs/assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png` · `289f97ef8234a6e410bb4918921ba7729b3d2c3ef527f6baa210dd7365348554` · 1536×1024 | 943,128,272,712 | `docs/theme-system/design-pack/d31-integrated-review/overview-instrument.png` · `fef82e68a06e4b3a85c3a5d2df5d074b3f55da7972521f3e93a0d438ac8427af` · 272×712 | untouched_crop |
| `overview-terminal` | `docs/assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png` · `289f97ef8234a6e410bb4918921ba7729b3d2c3ef527f6baa210dd7365348554` · 1536×1024 | 1257,128,255,712 | `docs/theme-system/design-pack/d31-integrated-review/overview-terminal.png` · `5ce01d607afa347f8973338ddbf03ad51f98d38d2b63bb4dfbe4236668e42e9f` · 255×712 | untouched_crop |
| `backdrop-atmospheric` | `docs/assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png` · `f246dd860b963654d9f96723e06b71ac0a22424a736fd403f18e79e91de97c73` · 1440×3200 | — | `docs/assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png` · `f246dd860b963654d9f96723e06b71ac0a22424a736fd403f18e79e91de97c73` · 1440×3200 | native_reference |
| `backdrop-glass` | `docs/assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png` · `30c81e3783c0df301360cdec0a3aa08d4970e2a473e4cf084e44b8fa79a6cb56` · 1440×3200 | — | `docs/assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png` · `30c81e3783c0df301360cdec0a3aa08d4970e2a473e4cf084e44b8fa79a6cb56` · 1440×3200 | native_reference |
| `backdrop-minimal-oled` | `docs/assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png` · `3289ae4838bd2fee459921025d00f3c3d33b96d3982efb6afd09e5f17f20ecd3` · 1440×3200 | — | `docs/assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png` · `3289ae4838bd2fee459921025d00f3c3d33b96d3982efb6afd09e5f17f20ecd3` · 1440×3200 | native_reference |
| `backdrop-instrument` | `docs/assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png` · `82612a2f6abb7be342975c405634293fec81367a275f625fd04e1e1ebaf92b22` · 1440×3200 | — | `docs/assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png` · `82612a2f6abb7be342975c405634293fec81367a275f625fd04e1e1ebaf92b22` · 1440×3200 | native_reference |
| `backdrop-terminal` | `docs/assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png` · `5cb511e9a05b7dbfb7775ea1385961d56ed2d726438d55e5c747f51a661448a2` · 1440×3200 | — | `docs/assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png` · `5cb511e9a05b7dbfb7775ea1385961d56ed2d726438d55e5c747f51a661448a2` · 1440×3200 | native_reference |
| `sheet-glass-screen-example` | `docs/assets/design-references/production-themes/glass/boards/glass_theme_asset_sheet.png` · `74e834c76e148d561d5f4696c9784b3e1131bd31089f13878d1863954eb85860` · 1448×1086 | 1100,365,333,660 | `docs/theme-system/design-pack/d31-integrated-review/sheet-glass-screen-example.png` · `5cefa61735f03574c37fb0234d9c73e98e7242c8c89a7a6d615ec045716a59f1` · 333×660 | untouched_crop |
| `sheet-instrument-screen-example` | `docs/assets/design-references/production-themes/instrument/boards/instrument_theme_asset_sheet.png` · `0a99c3b80bef14e70358bdc4859d88791e944203d150104427a3ea43bdd39dcd` · 1448×1086 | 1123,395,312,639 | `docs/theme-system/design-pack/d31-integrated-review/sheet-instrument-screen-example.png` · `0d45bb3429b436f1b3b3d91cc46588cd4be326a245b7a6a635dfcbb8050f0148` · 312×639 | untouched_crop |

`python scripts/verification/d31_integrated_review.py` checks the source
manifest identity, hashes, dimensions, crop bounds, panel inventory, proposal
references, and the explicit owner decision record. It does not assess visual
similarity or installed rendering acceptance.

## Twenty-cell proposal review

Each row links one unique cell in the structured proposal. The cited panel set
is the same-theme context for review, not proof that every source depicts that
page. The five Details entries are explicitly derived because no dedicated
Details atmosphere source is indexed.

| Cell | Proposal and review focus | Source panels | Owner decision |
|---|---|---|---|
| `atmospheric-now` | [Proposal](D31_PAGE_ATMOSPHERES.md): Does the open blue field, warm horizon, and scenic lower edge remain secondary to Now facts? | [overview](d31-integrated-review/overview-atmospheric.png), [backdrop](../../assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `glass-now` | [Proposal](D31_PAGE_ATMOSPHERES.md): Are layered surfaces and cool/warm lighting legible while preserving an opaque fallback? | [overview](d31-integrated-review/overview-glass.png), [backdrop](../../assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png), [sheet](d31-integrated-review/sheet-glass-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `minimal_oled-now` | [Proposal](D31_PAGE_ATMOSPHERES.md): Does a black-first field keep the localized hero art from implying full-screen scenery? | [overview](d31-integrated-review/overview-minimal-oled.png), [backdrop](../../assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `instrument-now` | [Proposal](D31_PAGE_ATMOSPHERES.md): Do grid and bounded modules remain decorative, with no unsupported instrument values? | [overview](d31-integrated-review/overview-instrument.png), [backdrop](../../assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png), [sheet](d31-integrated-review/sheet-instrument-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `terminal-now` | [Proposal](D31_PAGE_ATMOSPHERES.md): Does the console field preserve readable Now facts and keep its grid distinct from the board treatment? | [overview](d31-integrated-review/overview-terminal.png), [backdrop](../../assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `atmospheric-hourly` | [Proposal](D31_PAGE_ATMOSPHERES.md): Is the scenic atmosphere restrained around six chronological entries and visible window controls? | [overview](d31-integrated-review/overview-atmospheric.png), [backdrop](../../assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `glass-hourly` | [Proposal](D31_PAGE_ATMOSPHERES.md): Do translucent forecast surfaces preserve legibility and visible Earlier/Later/date controls? | [overview](d31-integrated-review/overview-glass.png), [backdrop](../../assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png), [sheet](d31-integrated-review/sheet-glass-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `minimal_oled-hourly` | [Proposal](D31_PAGE_ATMOSPHERES.md): Do separators and sparse art support six chronological rows without implying missing data? | [overview](d31-integrated-review/overview-minimal-oled.png), [backdrop](../../assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png) | **approved** — decision: approve; rationale: owner typed “apporve”; interpreted as approve without correction |
| `instrument-hourly` | [Proposal](D31_PAGE_ATMOSPHERES.md): Does the technical grid remain behind text and preserve exact forecast order and controls? | [overview](d31-integrated-review/overview-instrument.png), [backdrop](../../assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png), [sheet](d31-integrated-review/sheet-instrument-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `terminal-hourly` | [Proposal](D31_PAGE_ATMOSPHERES.md): Do console rules aid scanning without changing chronological or RTL reading order? | [overview](d31-integrated-review/overview-terminal.png), [backdrop](../../assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `atmospheric-daily` | [Proposal](D31_PAGE_ATMOSPHERES.md): Does the landscape treatment stay subordinate to five-day windows, dates, and low/high values? | [overview](d31-integrated-review/overview-atmospheric.png), [backdrop](../../assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `glass-daily` | [Proposal](D31_PAGE_ATMOSPHERES.md): Do glass layers keep five daily rows and precipitation meaning readable? | [overview](d31-integrated-review/overview-glass.png), [backdrop](../../assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png), [sheet](d31-integrated-review/sheet-glass-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `minimal_oled-daily` | [Proposal](D31_PAGE_ATMOSPHERES.md): Does the sparse field preserve five dates, unavailable values, and visible window actions? | [overview](d31-integrated-review/overview-minimal-oled.png), [backdrop](../../assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `instrument-daily` | [Proposal](D31_PAGE_ATMOSPHERES.md): Do grid and modules remain decoration while daily values and chronology stay explicit? | [overview](d31-integrated-review/overview-instrument.png), [backdrop](../../assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png), [sheet](d31-integrated-review/sheet-instrument-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `terminal-daily` | [Proposal](D31_PAGE_ATMOSPHERES.md): Do terminal rules and typography keep five-day weather facts readable at compact/large text? | [overview](d31-integrated-review/overview-terminal.png), [backdrop](../../assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `atmospheric-details` | [Proposal](D31_PAGE_ATMOSPHERES.md) · **same-theme derivation**: With no dedicated Details source, does the blue/scenic theme field remain restrained behind grouped measurements and provenance? | [overview](d31-integrated-review/overview-atmospheric.png), [backdrop](../../assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `glass-details` | [Proposal](D31_PAGE_ATMOSPHERES.md) · **same-theme derivation**: With no dedicated Details source, do glass-like boundaries keep source, freshness, and supplied groups distinct? | [overview](d31-integrated-review/overview-glass.png), [backdrop](../../assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png), [sheet](d31-integrated-review/sheet-glass-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `minimal_oled-details` | [Proposal](D31_PAGE_ATMOSPHERES.md) · **same-theme derivation**: With no dedicated Details source, do separators preserve group hierarchy without implying unsupported values? | [overview](d31-integrated-review/overview-minimal-oled.png), [backdrop](../../assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `instrument-details` | [Proposal](D31_PAGE_ATMOSPHERES.md) · **same-theme derivation**: With no dedicated Details source, do grid/modules remain subordinate to measured and provenance labels? | [overview](d31-integrated-review/overview-instrument.png), [backdrop](../../assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png), [sheet](d31-integrated-review/sheet-instrument-screen-example.png) | **approved** — decision: approve; rationale: approved as presented during owner review |
| `terminal-details` | [Proposal](D31_PAGE_ATMOSPHERES.md) · **same-theme derivation**: With no dedicated Details source, do terminal rules preserve readable provenance and distinguish supplied from derived values? | [overview](d31-integrated-review/overview-terminal.png), [backdrop](../../assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png) | **approved** — decision: approve; rationale: approved as presented during owner review |

### Cross-page identity and coherence### Cross-page identity and coherence

For each theme, consider whether the same atmosphere remains recognizable from
Now through Details without copying a composite phone layout, weakening page
identity, changing source/freshness meaning, or hiding the page's controls.
Confirm that contrast, compact width, font scale 1.3, RTL, and Effects Off
preserve complete text-based weather meaning. These checks are review prompts;
no rendered or accessibility-service result is claimed here.

## Owner disposition

Overall D31 disposition: **approved** — decision: approve; rationale: “approve the overall set.”
This approval applies only to the reviewed proposal revision identified in the
owner decision record. TP.1D packet approval remains separate and pending.
