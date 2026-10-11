# Production theme system

The five built-in production themes are **Atmospheric, Glass, Terminal, Instrument, and Minimal OLED**. The normal app now uses the production theme renderer. The theme resolver, shared components, page integration, and TP.3 installed acceptance have completed; see the [theme-pack roadmap](../theme-pack-roadmap.md) and the linked cycle histories for exact evidence and limitations. The owner-approved effects extension is separately sequenced in [UI_CONTEXT_ROADMAP.md](../UI_CONTEXT_ROADMAP.md) and governed by [EFFECTS_CONTRACT.md](EFFECTS_CONTRACT.md).

The earlier Theme B UI is a historical implementation sketch, not the active renderer. Production reference boards are indexed at [`docs/assets/design-references/production-themes/`](../assets/design-references/production-themes/) and enumerated with checksums in [ASSET_MANIFEST.md](ASSET_MANIFEST.md). Boards and crops remain documentation references, not Android runtime resources or proof of installed acceptance. Theme recipes still have explicit source-measurement gaps for the new effects levels; FX.03–FX.07 record that work rather than inventing approved references.

## Contents

- [design-pack/](design-pack/) — TP.1A shared foundation and source decisions; review material, not the complete or approved pack. TP.1B–TP.1D remain required.
- [THEME_DESIGN_CONTRACT.md](THEME_DESIGN_CONTRACT.md) — approved personalities and invariants.
- [architecture/](architecture/) — future shared-component contract and implementation quality gates.
- [tokens/catalog/](tokens/catalog/) — candidate theme token JSON; design input, not runtime configuration.
- [prompts/](prompts/) — reusable instructions for future bounded roadmap slices.
- [staged-production/](staged-production/) — quarantined candidate Kotlin, tests, XML, and resources for later inspection and selective adaptation.
- [ASSET_MANIFEST.md](ASSET_MANIFEST.md) and [ASSET_MANIFEST.json](ASSET_MANIFEST.json) — organized files and SHA-256 digests.

Staged candidates are not authoritative source code and were prepared against an earlier repository revision. Current repository architecture, approved specifications, active cycle plan, and current source take precedence. Nothing under `staged-production/` is production implementation.
