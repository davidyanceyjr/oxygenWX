# Plan 029 — TP.1D-partial-A-partial-B cross-pack consistency review

Status: Completed
Cycle ID: 029-tp-1d-final-integrated-pack-review
Roadmap item: TP.1D-partial-A-partial-B
Parent item: TP.1D
Evidence: .codex/test-artifacts/029-tp-1d-final-integrated-pack-review/
Created: 2026-09-23
Revised: 2026-09-23
Context budget: Aim for 30–35% of a fresh context window; stop before 45%. This is the upstream half of the former final-review draft. Dependent half: TP.1D-partial-A-partial-B-partial-A.
Planned implementation difficulty: 6/10

## Objective

Review the existing 20 theme/page design cells as one coherent pack, resolve supported cross-pack discrepancies, and hand off a verified proposed revision. This cycle completes the integrated consistency review only. TP.1D/TP.1 and the TP.2 gate remain open until the dependent packet and owner-decision cycle finishes.

## Production boundary

Documentation, design-reference SVGs, their generator/index/fixture when a demonstrated correction requires it, and cycle evidence. Review `docs/theme-system/design-pack/INTEGRATED_PACK.md`, its 20 primary references and twelve condition examples, the upstream 028 histories/evidence, page and foundation contracts, source decisions, and asset manifest. Correct only demonstrated TP.1D discrepancies. Record old/new values, affected cells, source or measurement basis, regenerated files, and verification. Update the design-pack README/render index only to reflect actual review status or changed references. At closure, advance the execution head of `docs/theme-pack-roadmap.md` to the dependent partial and record exact evidence in `.codex/history/`.

No Android/Kotlin/Compose/resource, weather-model, presentation-data, provider, settings, or navigation changes. The TP.3 installed checklist, frozen owner packet, SHA-256 approval manifest, owner decision, and TP.1/TP.1D completion belong to the dependent partial.

## Functional invariants

- Keep `Now -> Hourly -> Daily -> Details`, one outer horizontal-swipe owner, named page identity, and visible bounded Hourly/Daily window controls.
- Preserve mapper-supplied facts, units, entry count/order, missing/partial behavior, source/update/load status, and normalized versus derived/historical provenance. Do not add official alerts, advice, AQI/UV, charts, gauges, or screenshot-specific weather values.
- Important facts stay visible text with meaningful stated semantics; decorations carry no required meaning. Keep non-color status/selection cues, 48 dp control targets where applicable, and opaque, static, complete Effects Off designs.
- Keep the 20 primary cells and twelve labeled examples traceable to the same exact illustrative fixture. Static references do not prove installed behavior, Android font metrics, interaction, or TalkBack.

## Implementation steps

1. **Establish the baseline.** Record initial git status and preserve unrelated changes. Read the completed 028 and partial-A history/reviews. Count 20 unique matrix cells, 20 primary SVGs and twelve condition examples; validate the fixture export, `renders/index.json`, 23 used-source hashes, local links/anchors, and D28/D29/D31 status. Record absent or stale input as a blocker. Do not call a prior audit a new pass without rerunning it.
2. **Review the whole pack.** For each theme compare all four pages for shell, type, surface, spacing, weather-mark treatment, and source/status placement; for each page compare the five themes for identical typed facts/actions and chronology. Inspect the 20 primary viewport captures and the twelve condition examples, with full-body/end captures where content scrolls. Compare exact source crops and documented measurements, including D31 Atmospheric source/palette fit. Use the state matrix for loading, live, partial/sparse, cached/stale, retained-refresh-failure, failure-without-data, nested unavailable, and missing fields. Record state-contract review separately from visual examples; render an additional state only for a concrete unresolved fit or meaning risk.
3. **Resolve bounded discrepancies.** For each mismatch record a cell, observed versus required treatment, authority/source, measured old/new values, and affected references. Update `INTEGRATED_PACK.md` and only the relevant `FOUNDATION.md`, page contract, `CONTENT_AND_STATE_RULES.md`, `SOURCE_DECISIONS.md`, reference SVGs/generator/index, or asset map. Apply shared changes consistently to every affected cell. Preserve D28 font and D29 mark detail as explicit owner choices unless evidence resolves them; present bounded options and impact rather than silently choosing. Do not turn reference-art-only features into weather facts.
4. **Reproduce and hand off.** Run generation with cycle-029 output for viewport/full/end/bounds captures so completed 028 evidence is not overwritten. If needed, add an explicit output-directory argument to `renders/generate.py`; preserve its deterministic 20+12 tracked SVG/index output. Compare regenerated files and inspect all changed cells plus unchanged shared treatment across the pack. Write a 20-cell review matrix and discrepancy/decision ledger in cycle evidence, identifying the exact proposed files/revision inputs and any unresolved owner choices for the dependent partial. Update the roadmap head and close this cycle with verification and limitations; leave TP.1D/TP.1 open.

## Verification and evidence

- Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`. Reuse/adapt the 028 evidence audits in the cycle-029 evidence directory; verify 20 distinct theme/page cells, 20 primary plus twelve indexed examples, SVG dimensions/metadata, local links/anchors, fixture field/order equality, action/control contract, no unsupported slots, and used-source SHA-256 values against `ASSET_MANIFEST.json`.
- Rasterize and visually inspect all 20 primary references and twelve examples; inspect full/end captures for scrolling cells and any corrected shared treatment. Check compact 360×640, font 1.3, RTL order, 840×900 width cap, Effects Off opacity/static content, and High contrast text/non-color cues. Recalculate opaque text/surface ratios for changed High contrast or Effects Off surfaces; retain sampled colors and ratios.
- Run `python scripts/dev.py check` when Android tooling is available as a regression gate, without treating it as static-design acceptance. Run `git diff --check` and inspect tracked changes and new evidence against the initial status. Record commands/results, per-cell outcomes, source measurements, affected-file delta, outstanding decisions, and checks that could not run under `.codex/test-artifacts/029-tp-1d-final-integrated-pack-review/`.

## Acceptance criteria

- All 20 cells have a recorded cross-page/cross-theme result with source, typed-map, geometry/treatment, and state-contract findings. All 32 existing references have a traced condition and visual review outcome; state coverage is reported as contract review unless a state was actually rendered.
- Every in-scope correction has old/new/affected-cell evidence and regenerated references; remaining D28/D29/D31 or new owner decisions have explicit options and impact. Used assets and fixture remain reproducible and no completed 028 evidence is mutated.
- Cycle history records exact checks and limits. `docs/theme-pack-roadmap.md` points to the dependent initial plan. TP.1D/TP.1 remain active and TP.2 remains gated; there is no owner-approval or installed-app claim.

## Risks and assumptions

- A font, mark, or Atmospheric treatment change can affect many renders. Count the affected cells before editing; if the work would exceed 45% context, create a still narrower dependent partial and preserve the reviewed boundary rather than abbreviating verification.
- Some source art has no dedicated Daily or Details page. Keep documented inference separate from measured source matches.
- The 20 primary SVGs depict a live illustrative fixture. Other state meanings are governed by typed contracts until specifically rendered; do not claim 32 examples cover every state.

## Out of scope

- TP.3 installed comparison/acceptance or runtime visual changes.
- The final comparison checklist, frozen approval packet, owner decision, TP.1D/TP.1 completion, or TP.2 activation.
- Changes to meteorological meaning, data contracts, alerts, providers, cache, location, units, or settings.
