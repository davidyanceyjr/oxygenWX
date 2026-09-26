# Plan 058-partial2 — TP.2A resolved theme policy and matrix tests

Status: Planned; dependent on TP.2A catalog conformance PASS
Roadmap item: TP.2A-partial2
Dependency: `.codex/plans/058-tp2a-approved-tokens-resolver.md`

## Objective

Implement the approved contrast, layout, and effects policies in the pure
typed theme resolver after catalog conformance is established. Prove that all
five themes resolve deterministically across Standard/High contrast,
Standard/Simple layout, and Off/Subtle/Full effects (60 combinations), without
changing weather semantics or app composition.

## Difficulty

**6/10.** The resolver is isolated and already has a combination-test base,
but the approved high-contrast policy requires correct WCAG calculations and
clear tests for opacity, role promotion, and independent setting axes.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt`,
  `ThemeModels.kt`, and `ThemeResolver.kt` — only where required to implement
  the approved typed policy.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/` — focused catalog and
  resolver tests.
- `docs/ARCHITECTURE.md` and the TP.2A roadmap record at closure.
- This plan's cycle evidence/history under its activated cycle ID.

Do not edit approved JSON/design-pack files, add runtime JSON parsing, modify
Compose pages, or change theme preference persistence in this slice.

## Invariants and policy

- The five approved theme IDs, names, standard palettes, typography, geometry,
  and visual-style mappings remain intact.
- High contrast preserves selected theme identity, typography, geometry,
  visual styles, effect selection, and layout selection. It changes only
  palette/opacity policy necessary for accessible contrast.
- Use the approved design-pack pairs and WCAG relative-luminance method
  (`docs/theme-system/design-pack/DETAILS.md`, High contrast section) as the
  test oracle. Required text pairs meet at least 4.5:1 on their *actual resolved
  opaque background*. Supporting `secondaryData` pairs below 7:1 use `content`
  for the rendered supporting-text role; do not globally recolor warning,
  danger, condition, precipitation, action, or other semantically distinct
  roles merely to improve a ratio. Keep those roles distinct and verify any
  text use has a readable approved pairing.
- High contrast surfaces are opaque. The resolved canvas/surface text pairs
  are computed using resolved palette colors, never by sampling translucent
  backdrops. Outlines are opaque and visibly distinct from adjacent surfaces.
- Effects Off has precedence for a solid canvas, no motion, opaque surfaces and
  outlines, and complete semantic appearance for every theme, contrast, and
  layout. It does not remove or reorder content.
- Standard/Simple changes geometry only. Standard/High contrast changes
  contrast palette/opacity policy only. Effects change backdrop/motion/surface
  effects only. Preserve `controlTargetMinimum >= 48.dp`.
- Resolver remains pure: no weather values, persistence, device state, or
  Compose/page composition enter the resolution.

## Implementation and tests

1. Reconcile the resolver's existing typed values with part-one catalog
   conformance results. Correct only demonstrable drift from approved inputs;
   preserve standard appearance otherwise.
2. Implement a small deterministic WCAG relative-luminance/contrast helper at
   the resolver boundary (or in test utilities if runtime does not need it).
   Avoid duplicating algorithm logic in expected-value assertions; test the
   helper against known black/white and boundary fixtures.
3. Implement High contrast opaque surface/outline resolution and the
   `secondaryData` to `content` rendered-role promotion based on the approved
   7:1 threshold. Do not mutate the canonical standard palette.
4. Expand resolver tests across all 60 combinations, asserting determinism,
   complete palette and styles, identity, correct effects/layout behavior,
   opaque High contrast surfaces/outlines, 4.5:1 required text pairs, 7:1
   supporting role or explicit promotion, and Off precedence.
5. Include regression assertions that Standard contrast retains the approved
   baseline palette and High contrast does not alter theme identity, geometry,
   selected effects/layout, weather models, or page semantics. If no page
   composition changed, record that boundary rather than adding UI tests.
6. Run focused JVM tests, `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, `git diff --check`, and
   `python scripts/dev.py check` when Android dependencies/SDK are available.
   Preserve output and blockers in cycle evidence.

## Acceptance criteria

- All 60 combinations resolve deterministically with supported theme identity
  and complete typed output.
- High contrast meets the approved contrast and opacity rules for every theme;
  promotion is represented as a resolved text role without destroying semantic
  color distinctions.
- Simple layout changes geometry only and retains touch-target guidance.
- Effects Off remains solid, static, opaque, and complete for every contrast
  and layout combination.
- Focused, workflow, contract, diff, and broader-check outcomes are recorded.
- No Compose composition, weather semantics, persistence, catalog input, or
  installed visual acceptance claim is included.

## Evidence and out of scope

Keep focused JVM output, combination matrix summary, contrast calculations,
workflow/contract/check/diff results, and final diff review under this cycle's
`.codex/test-artifacts/<cycle-id>/`. Installed screenshots, TalkBack service
review, settings persistence, theme renderer migration, and TP.3 are outside
this plan.
