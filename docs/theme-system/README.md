# Production theme system

The approved built-in production theme family is **Atmospheric, Glass, Minimal OLED, Instrument, and Terminal**. This is the design and architecture authority for the replacement track; it does not mean the production resolver, components, or renderer are already implemented.

The current Theme B UI is an implementation sketch/baseline being replaced. Its existing repository images remain historical references. The production reference boards are indexed at [`docs/assets/design-references/production-themes/`](../assets/design-references/production-themes/) and enumerated with checksums in [ASSET_MANIFEST.md](ASSET_MANIFEST.md). The product owner requires exact visual matching. Existing boards are the baseline for a design review, but are not yet a complete codifiable specification for every page/theme. Full art boards and crops remain documentation references, not Android runtime resources.

## Contents

- [design-pack/](design-pack/) — TP.1A shared foundation and source decisions; review material, not the complete or approved pack. TP.1B–TP.1D remain required.
- [THEME_DESIGN_CONTRACT.md](THEME_DESIGN_CONTRACT.md) — approved personalities and invariants.
- [architecture/](architecture/) — future shared-component contract and implementation quality gates.
- [tokens/catalog/](tokens/catalog/) — candidate theme token JSON; design input, not runtime configuration.
- [prompts/](prompts/) — reusable instructions for future bounded roadmap slices.
- [staged-production/](staged-production/) — quarantined candidate Kotlin, tests, XML, and resources for later inspection and selective adaptation.
- [ASSET_MANIFEST.md](ASSET_MANIFEST.md) and [ASSET_MANIFEST.json](ASSET_MANIFEST.json) — organized files and SHA-256 digests.

Staged candidates are not authoritative source code and were prepared against an earlier repository revision. Current repository architecture, approved specifications, active cycle plan, and current source take precedence. Nothing under `staged-production/` is production implementation.
