# Plan 069 — Minimal OLED and Instrument backdrop rendering

Status: Completed — PASS
Cycle ID: 069-tp2d-minimal-oled-instrument-backdrops
Roadmap item: TP.2D-partial4
Created: 2026-09-27
Depends on: TP.2D-partial1/2/3 PASS in cycles 065, 067, and 068
Difficulty: 5/10
Recommended Codex CLI model: `gpt-6-sol`, medium reasoning
Context budget: target at most 30% of a fresh context window; stop before 45%.

## Objective

Verify installed non-Off shared backdrops for Minimal OLED and Instrument against the approved D31 field direction. Exercise Standard/Subtle and High/Subtle for each theme at 360 × 640 dp. Include one intentional D29 no-mark gap with visible caller condition text. Allow one narrow correction pass when installed evidence demonstrates an in-boundary defect.

## Visual objective

Minimal OLED retains a black-first restrained field so typography and data lead. Instrument retains a subtle technical grid behind bounded content. Review the field against separately indexed standalone assets, using `D31_SOURCE_AUDIT.md` and `D31_PAGE_ATMOSPHERES.md`. Distinguish direct source observation from interpretation. Do not infer page composition or pixel matching from reference boards.

## Production boundary

- Inspect and, only if needed, adjust the Minimal OLED and Instrument cases of `ProductionBackdrop` in `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`. No public API changes.
- Add one focused installed test under `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/`, reusing cycle 068 capture patterns where practical. Extend JVM assertions only for a genuinely uncovered contract; keep the D29 matrix unchanged.
- Record findings in this plan, `docs/theme-pack-roadmap.md`, and `.codex/test-artifacts/069-tp2d-minimal-oled-instrument-backdrops/`.
- If the evidence requires resolver/catalog changes, another style, page layout, or new visual direction, record the finding and stop this slice BLOCKED.

## Functional invariants

- Backdrops and marks remain decorative, without weather-bearing semantics or pointer interception. Caller condition text conveys weather meaning even in a no-mark cell.
- Caller semantics and one named foreground action remain available; one click invokes its callback exactly once. The action target is at least 48 dp.
- High contrast uses resolved opaque semantic surfaces, legible content roles, and visible boundaries. Meaning never relies on color alone.
- Effects Off remains opaque, static, and complete. Subtle is static in this slice.
- Weather values, provenance, navigation, fetches, preferences, D29 cells, and other theme styles do not change.

## Source review and installed matrix

Verify both standalone backdrop paths, dimensions, and SHA-256 digests against `docs/theme-system/design-pack/D31_SOURCE_AUDIT.md`. Inspect the images and corresponding `D31_PAGE_ATMOSPHERES.md` entries. Record direct observations separately from interpretation. Stop for owner direction if approved sources leave a material conflict unresolved.

Use a deterministic installed `ProductionBackdrop` host at 360 × 640 dp, font scale 1.0, LTR, Subtle. Capture four labeled PNGs:

| Theme | Contrast | Checks |
| --- | --- | --- |
| Minimal OLED | Standard | Field, condition text, semantics, action, pointer delivery |
| Minimal OLED | High | Same, plus resolved contrast, opaque surface, visible boundary |
| Instrument | Standard | Field, condition text, semantics, action, pointer delivery |
| Instrument | High | Same, plus resolved contrast, opaque surface, visible boundary |

Choose one intentional no-mark D29 cell for one theme and an approved rendered-mark cell for the other. Record exact cells and assert both against the unchanged matrix before constructing the host. For High cases, compute resolved text/surface contrast using the documented WCAG method in `docs/theme-system/design-pack/DETAILS.md`; require 4.5:1, with its `secondaryData` promotion rule if used. Inspect captures as well as token assertions.

## Implementation steps

1. Confirm cycles 065, 067, and 068 passed. Inspect backdrop branches, resolved roles, D29 cells, cycle 068 test, and actual SDK/emulator/display. Record host identity; unavailable installed verification closes BLOCKED.
2. Verify both source assets and prepare a brief field-review checklist grounded in the D31 records.
3. Implement four installed cases with caller text/semantics, 48 dp action, one no-mark gap, one rendered mark, High role checks, and labeled capture export.
4. Run focused installed checks and inspect all captures against standalone assets. Make at most one minimal correction within the two named branches, then rerun focused JVM/installed checks and visual review. Stop on unresolved or out-of-boundary findings.
5. Run the smallest relevant JVM test while iterating, then `python scripts/dev.py test`, `build`, `contract`, `workflow`, and `check`; run `git diff --check` and inspect the final diff. Preserve exact outputs and any unavailable gate.
6. Record PASS/BLOCKED, source identities, per-case findings, correction, limits, and evidence in roadmap/history. Close the cycle only with recorded outcomes; partial5 depends on this slice passing.

## Acceptance criteria

- Four installed cases pass caller text/semantics, decorative mark or no-mark expectations, named 48 dp action, exact-once callback, and no backdrop pointer capture.
- Both High cases pass resolved contrast, opaque surface, and visible-boundary assertions.
- Four installed captures are reviewed against SHA-256-verified standalone assets, with per-case field findings and deviations recorded. No page or pixel parity claim.
- Production corrections stay in the two named branches and one pass. Material ambiguity or out-of-boundary fixes stop BLOCKED.
- Focused JVM/instrumentation and repository checks pass, or exact unverified boundaries and reasons are recorded. Compilation alone does not satisfy visual acceptance.

## Verification and evidence

Evidence path: `.codex/test-artifacts/069-tp2d-minimal-oled-instrument-backdrops/`. Retain dependency references; source paths, dimensions, digests; device/SDK/display/app identity; commands/outputs; four labeled captures; per-case assertions and visual notes; correction captures if any; final diff review; and explicit limits.

Final result: PASS. Both source dimensions and SHA-256 values matched the D31 audit. The installed host passed the four Standard/High Subtle cases on `oxygen_tp2b_api37` at 360 × 640 dp, font scale 1.0, LTR. Initial captures showed an overly bright Instrument High grid; one in-boundary production correction reduced only that grid's opacity. The four final PNGs and per-case field review are in the evidence directory. Focused JVM and instrumentation, repository test/build/contract/workflow/check including lint, and final diff checks passed. The field review records exact contrast ratios and unverified boundaries; no page or pixel parity is claimed.

## Risks and assumptions

- The cycle 068 installed host appears available but must be rediscovered. An unavailable installed gate blocks PASS.
- The D29 matrix contains a suitable no-mark cell for one of these themes; confirm its exact identity before implementation.
- Field-level evidence supports a bounded review. Unresolved material source conflict requires owner direction.
- Difficulty is **5/10**: code scope is narrow; installed visual, contrast, interaction, and source-evidence checks need care.
- `gpt-6-sol` at medium effort is the recommended cost/quality choice for this coding and visual-verification slice. CLI account availability must be checked when implementing.

## Out of scope

- Effects Off changes, Full effects, motion policy, other backdrop branches, or D29 matrix changes.
- Resolver/catalog/tokens, new assets, page composition, settings persistence, weather/data/provider/presentation behavior, navigation, and fetch behavior.
- Large-font/RTL matrices, TalkBack, TP.2E, TP.3 page comparison, pixel equality, and release acceptance.
