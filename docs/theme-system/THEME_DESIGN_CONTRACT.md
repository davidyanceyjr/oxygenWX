# Production theme design contract

## Built-in personalities

- **Atmospheric** — immersive procedural weather field with restrained container emphasis. Initial migration/verification theme; this is not persisted default-selection behavior.
- **Glass** — restrained layered surfaces over a procedural atmosphere.
- **Minimal OLED** — black-first, low-decoration, typography- and data-dominant presentation.
- **Instrument** — technical monitor treatment; indicators and gauges may express only values or series supplied with suitable semantics, units, ranges, intervals, and provenance.
- **Terminal** — flat, console-like presentation with monospace typography.

The product owner requires exact visual matching to the approved production
designs. The existing boards and crops are the baseline for a design review, but
they do not yet define every exact page/theme composition or availability state.
Complete and approve a codifiable screen design pack before further visual
implementation is accepted. Full boards and extracted image crops remain under
`docs/assets/design-references/production-themes/`; no large concept board
belongs in runtime Android resources.

## Product and interaction invariants

- Standard Home remains `Now -> Hourly -> Daily -> Details`.
- The outer Home pager remains the single global horizontal-swipe owner. Hourly and Daily retain explicit window controls, with six chronological hourly entries and five chronological daily entries per window, including Hourly date jumps.
- Theme, contrast, density, effects, and motion may alter presentation only. They do not alter forecast values, membership, chronology, units, meteorological interpretation, alert meaning, provenance/freshness, missing-data behavior, page identity, navigation semantics, or accessibility meaning.
- Important weather information remains visible text with meaningful semantics. Decoration is never required to understand the forecast. Color is not the sole carrier of state.
- Effects Off resolves to an opaque, static, complete interface.
- Theme changes do not refetch weather. Presentation/domain/provider boundaries and canonical values remain unchanged.
- Missing inputs remain unavailable. Gauges/charts require properly defined presentation inputs; art composition never justifies fabricated values.
- Existing historical Theme B boards may remain as records, but the Theme B sketch is not a production visual acceptance target. Local Glass references are distinct from deprecated upstream dark-glass/gold compositions.

## Future appearance boundary

Theme selection, contrast, effects, and system motion policy resolve to semantic appearance roles and render styles before theme-aware Compose components consume the existing typed presentation models. Components must not branch on a raw theme identity to change weather or interaction meaning. Layout and theming remain presentation concerns.

The future architecture is documented in `docs/ARCHITECTURE.md`. R0.11A establishes authority only; R0.11B and later roadmap slices implement the resolver, components, page migration, cutover, and verification in bounded stages.
