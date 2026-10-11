# Production theme system

The five built-in production themes are **Atmospheric, Glass, Terminal, Instrument, and Minimal OLED**. The normal app now uses the production theme renderer. The theme resolver, shared components, page integration, and TP.3 installed acceptance have completed; see the [theme-pack roadmap](../theme-pack-roadmap.md) and the linked cycle histories for exact evidence and limitations. The owner-approved effects extension is separately sequenced in [UI_CONTEXT_ROADMAP.md](../UI_CONTEXT_ROADMAP.md) and governed by [EFFECTS_CONTRACT.md](EFFECTS_CONTRACT.md).

The earlier Theme B UI is a historical implementation sketch, not the active renderer. Production reference boards are indexed at [`docs/assets/design-references/production-themes/`](../assets/design-references/production-themes/) and enumerated with checksums in [ASSET_MANIFEST.md](ASSET_MANIFEST.md). Boards and crops remain documentation references, not Android runtime resources or proof of installed acceptance. Theme recipes still have explicit source-measurement gaps for the new effects levels. The revised UI roadmap consolidates those gaps into one source matrix and gates further theme work on an installed Atmospheric Now slice.

Cycle UI.5's consolidated source matrix and UI.6A decision packet is
[EFFECTS_SOURCE_DECISION_PACKET.md](EFFECTS_SOURCE_DECISION_PACKET.md). It
separates approved D31 page proposals, direct source observations, current
runtime behavior, exploratory cycle 169 previews, and unresolved decisions.

## Contents

- [design-pack/](design-pack/) — historical TP.1A–TP.1D design sources and decisions for the completed theme-pack sequence. Cycle 104 records TP.3 PASS; these materials remain source history and do not supply the new effects-level recipes or installed evidence.
- [THEME_DESIGN_CONTRACT.md](THEME_DESIGN_CONTRACT.md) — approved personalities and invariants.
- [architecture/](architecture/) — future shared-component contract and implementation quality gates.
- [tokens/catalog/](tokens/catalog/) — candidate theme token JSON; design input, not runtime configuration.
- [prompts/](prompts/) — reusable instructions for future bounded roadmap slices.
- [staged-production/](staged-production/) — quarantined candidate Kotlin, tests, XML, and resources for later inspection and selective adaptation.
- [ASSET_MANIFEST.md](ASSET_MANIFEST.md) and [ASSET_MANIFEST.json](ASSET_MANIFEST.json) — organized files and SHA-256 digests.

Staged candidates are not authoritative source code and were prepared against an earlier repository revision. Current repository architecture, approved specifications, active cycle plan, and current source take precedence. Nothing under `staged-production/` is production implementation.
