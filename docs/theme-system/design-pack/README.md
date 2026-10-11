# Theme design pack: TP.1D cross-pack review

**Current status:** the TP.1D pack and TP.2/TP.3 implementation and acceptance
gates later closed; cycle 104 records TP.3 PASS. The pending-gate statements
below describe the state at the time of those reviews and are retained as
historical context. They are not current workflow instructions. See the
[main roadmap](../../ROADMAP.md) and
[cycle 104 history](../../../.codex/history/2026-10-03-104-tp3-theme-pack-exit-gate.md)
for the later disposition. This pack remains useful as source/design history;
the current effects contract governs new None/Subtle/Full work.

> **D29 weather art has a complete, owner-approved 30-cell matrix.**
> [WEATHER_ART.md](WEATHER_ART.md) records the design owner's as-presented
> approval, including Terminal's proposed tokens and explicit no-mark omissions.
> This approves D29's matrix only; TP.1D packet approval, TP.1D closure, and
> TP.2 eligibility remain pending their separate gates.

**Status at the time of review:** proposed review material, not yet a complete design pack. TP.1A recorded shared decisions; TP.1B designed Now and Hourly, TP.1C designed Daily and Details, and TP.1D integrated the 20 theme/page cells and recorded explicit design-owner approval. The remaining implementation and acceptance gates later closed; cycle 104 records TP.3 PASS. The current status and governing sequence are linked above.

**Effects reconciliation:** the owner-approved [effects contract](../EFFECTS_CONTRACT.md)
now governs future None/Subtle/Full work. Earlier candidate opacity, blur,
backdrop, weather-mark, and `supportsFull` prescriptions in the foundation and
page packs are historical design inputs, not binding future effects recipes
where they conflict. In particular, do not add enclosing data panels merely
for Full richness; keep functional grouping and readable control surfaces.
The revised UI roadmap records theme source coverage in one consolidated
decision packet before implementation or states. Existing reference renders (including HTML/SVG) are design artifacts,
not installed-app evidence. Completed D29/D31 owner approvals and TP evidence
remain historical records and are not revoked by this reconciliation.

**Current shell note:** the named persistent page-selector rows in TP.1
reference designs were superseded by UI.2; the selected page title now opens
direct page selection. Historical design measurements may still inform the
page title treatment but do not restore the removed row.

Authority order: [product specification](../../SPECIFICATION.md), [adopted UI specification](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md), [theme design contract](../THEME_DESIGN_CONTRACT.md), then this foundation. Reference art and candidate JSON are inputs, not overrides. `accepted by authority` below means the cited written contract already settles the rule; it does **not** mean the design owner approved this pack.

- [FOUNDATION.md](FOUNDATION.md): shared canvas, shell, responsive constraints, and five-theme role/value matrix.
- [REFERENCE_MEASUREMENT_METHOD.md](REFERENCE_MEASUREMENT_METHOD.md): proportional measurement and state-treatment derivation from reference art.
- [CONTENT_AND_STATE_RULES.md](CONTENT_AND_STATE_RULES.md): presentation-model slots, state grammar, accessibility, RTL, and effects.
- [WEATHER_ART.md](WEATHER_ART.md): complete D29 shared mark contract and owner-approved 30-cell theme/condition matrix, including direct sources, same-theme proposals, explicit omissions, and integrated review; the broader TP.1D pack remains unapproved.
- [Rain mark proposals (cycle 079)](proposals/RAIN_MARKS_079_REVIEW.md): owner-approved visual proposals for the three D29 Rain source-gap cells; the D29 matrix and runtime behavior remain unchanged pending separate implementation work.
- [SOURCE_DECISIONS.md](SOURCE_DECISIONS.md): source ledger, conflicts, asset disposition, and owner decisions.
- [D31_SOURCE_AUDIT.md](D31_SOURCE_AUDIT.md): source-traceable five-theme atmosphere inventory, measured backdrop samples, qualitative profiles, explicit source gaps, and audit limits; it does not approve page treatments.
- [D31_PAGE_ATMOSPHERES.md](D31_PAGE_ATMOSPHERES.md): cycle 053 mapping of twenty source-traceable Now, Hourly, Daily, and Details atmosphere proposals, one per page and built-in theme. The owner approved the cells and overall set on 2026-09-25; the five Details cells remain explicitly identified as same-theme derivations because no dedicated Details reference is indexed.
- [D31_INTEGRATED_REVIEW.md](D31_INTEGRATED_REVIEW.md): cycle 054 source-panel package and twenty-cell review guide, with exact crops/native backdrop references indexed in [d31-integrated-review](d31-integrated-review/README.md). All decisions are recorded in [D31_OWNER_DECISION.md](D31_OWNER_DECISION.md). This is documentary design approval only; packet approval and installed visual acceptance remain open.
- [NOW.md](NOW.md): TP.1B partial A, proposed Now composition and five theme mappings; measured, checked for model coverage, and reviewed in the upstream integrated renders.
- [HOURLY.md](HOURLY.md): TP.1B partial B, proposed Hourly composition, six-entry grid, date/window controls and five theme mappings; measured, checked for model coverage, and reviewed in the upstream integrated renders.
- [DAILY.md](DAILY.md): TP.1C partial A, proposed Daily composition, five-entry windows, controls, state behavior, and five theme mappings; documentation audit and partial-A individual static-render review passed; owner approval pending.
- [DETAILS.md](DETAILS.md): TP.1C partial B, proposed ordered metric groups, source/update/status separation, state behavior, and five theme mappings; documentation audit and partial-A individual static-render review passed; owner approval pending.

- [INTEGRATED_PACK.md](INTEGRATED_PACK.md): 20-cell cross-page/cross-theme review,
  asset use, typed fixture, measurements and dependent packet handoff. Twenty
  primary SVGs and twelve condition examples are in the [render index](renders/README.md).
  D28 and D31 remain explicit owner appearance decisions; the separate D29
  weather-mark matrix is approved in [WEATHER_ART.md](WEATHER_ART.md).
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
