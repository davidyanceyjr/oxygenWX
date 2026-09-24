# Theme design pack: TP.1D cross-pack review

**Status:** proposed review material, not an approved or complete design pack. TP.1A records shared decisions; TP.1B designs Now and Hourly, TP.1C designs Daily and Details, and TP.1D integrates the 20 theme/page cells and records explicit design-owner approval. TP.2 remains gated on TP.1D closure.

Authority order: [product specification](../../SPECIFICATION.md), [adopted UI specification](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md), [theme design contract](../THEME_DESIGN_CONTRACT.md), then this foundation. Reference art and candidate JSON are inputs, not overrides. `accepted by authority` below means the cited written contract already settles the rule; it does **not** mean the design owner approved this pack.

- [FOUNDATION.md](FOUNDATION.md): shared canvas, shell, responsive constraints, and five-theme role/value matrix.
- [REFERENCE_MEASUREMENT_METHOD.md](REFERENCE_MEASUREMENT_METHOD.md): proportional measurement and state-treatment derivation from reference art.
- [CONTENT_AND_STATE_RULES.md](CONTENT_AND_STATE_RULES.md): presentation-model slots, state grammar, accessibility, RTL, and effects.
- [SOURCE_DECISIONS.md](SOURCE_DECISIONS.md): source ledger, conflicts, asset disposition, and owner decisions.
- [NOW.md](NOW.md): TP.1B partial A, proposed Now composition and five theme mappings; measured, checked for model coverage, and reviewed in the upstream integrated renders.
- [HOURLY.md](HOURLY.md): TP.1B partial B, proposed Hourly composition, six-entry grid, date/window controls and five theme mappings; measured, checked for model coverage, and reviewed in the upstream integrated renders.
- [DAILY.md](DAILY.md): TP.1C partial A, proposed Daily composition, five-entry windows, controls, state behavior, and five theme mappings; documentation audit and partial-A individual static-render review passed; owner approval pending.
- [DETAILS.md](DETAILS.md): TP.1C partial B, proposed ordered metric groups, source/update/status separation, state behavior, and five theme mappings; documentation audit and partial-A individual static-render review passed; owner approval pending.

- [INTEGRATED_PACK.md](INTEGRATED_PACK.md): 20-cell cross-page/cross-theme review,
  asset use, typed fixture, measurements and dependent packet handoff. Twenty
  primary SVGs and twelve condition examples are in the [render index](renders/README.md).
  D28/D29/D31 remain explicit owner appearance decisions.
- [TP3_INSTALLED_COMPARISON.md](TP3_INSTALLED_COMPARISON.md): executable future
  installed-app checklist for those 32 references and alternate typed states;
  no installed result or owner approval is claimed.

The upstream Now/Hourly and partial-A Daily/Details individual reviews are
complete. Cycle 029 cross-reviewed all 20 cells and 12 examples; its exact
evidence is under `.codex/test-artifacts/029-tp-1d-final-integrated-pack-review/`.
The dependent packet/decision cycle must freeze the proposed revision and
obtain explicit owner disposition. The full pack is
not approved; TP.2 stays gated.

All measurements called *reference* describe a review target rather than a verified installed rendering. These static design references do not claim installed app or implementation acceptance.
