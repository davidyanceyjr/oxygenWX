# Plan 062 — TP.2A resolved appearance policy

Status: Completed
Cycle ID: 062-tp2a-resolved-appearance-policy
Roadmap item: TP.2A-partial2
Created: 2026-09-26

## Objective

Complete TP.2A by implementing and verifying the approved appearance policy in
the pure typed resolver. The resolver must produce deterministic, complete
results for all 60 combinations of five themes, two contrast levels, two layout
presets, and three effects levels. High contrast resolves opaque component
surfaces, meets the approved text contrast and supporting-role promotion rules,
and keeps each actually rendered outline at least 3:1 against its adjacent
opaque component background. Layout and effects remain independent
presentation axes; Effects Off remains opaque, static, and complete.

**Difficulty: 7/10.** The resolver is isolated and has a 60-combination test
base, but this slice must measure contrast for the real component/background
pairs, resolve palette fallbacks without changing semantic roles, and verify
opacity precedence across the settings matrix.

**Context budget:** Estimated at 40% of a fresh context window. Keep
implementation and verification below 45%; do not absorb TP.2B component work.
This bounded resolver, test, and architecture-documentation slice does not
require a split.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeResolver.kt` —
  implement WCAG contrast calculation and high-contrast/effects/layout
  resolution only as required by this policy.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeModels.kt` — only
  if a typed resolved-role representation is necessary; prefer using the
  resolved `ThemePalette.secondaryData` as the effective supporting-text role
  so no redundant model field is added.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ThemeResolverTest.kt` —
  focused tests for contrast math, the 60-cell matrix, opacity, role promotion,
  setting-axis independence, and Effects Off precedence.
- `docs/ARCHITECTURE.md` — document the resolver's final contrast, actual
  component-background, opacity-precedence, and setting-axis policy.
- `docs/theme-pack-roadmap.md`, this plan, `.codex/current.md`, and
  `.codex/test-artifacts/062-tp2a-resolved-appearance-policy/` — lifecycle,
  roadmap state, and exact evidence.

Approved JSON catalog files and design-pack files are immutable in this slice.
Do not change Compose pages/components, persisted preferences, weather data,
or theme identities.

## Functional invariants

- The five approved theme identities, display names, standard palettes,
  typography, baseline geometry, and visual-style mappings remain intact.
- High contrast preserves theme identity, typography, geometry, backdrop and
  component styles, selected effects, and selected layout. It changes only
  palette/opacity policy necessary to meet the stated contrast rules. Resolve
  `panelOpacity` and `outlineOpacity` to `1f` whenever contrast is High; this
  makes existing production section surfaces and borders opaque for Off,
  Subtle, and Full effects without changing Compose code. Never mutate
  `ThemeDefinition.palette` or the Standard resolved palette.
- Use the approved High contrast pairs in
  `docs/theme-system/design-pack/DETAILS.md`: `content` and `secondaryData`
  against opaque backgrounds. The design-pack's WCAG relative luminance
  algorithm is the oracle. Each required text pair must reach at least 4.5:1.
  If supporting `secondaryData` is below 7:1 on any background where it is
  rendered, promote the resolved supporting-text role to `content` for High
  contrast. Preserve typed role identity and all Standard values.
- Audit outline consumers before implementing the rule. Cover each actual
  High contrast outline/divider pair: `ProductionSectionSurface` uses `canvas`
  for Minimal OLED/Terminal, `surface` for Atmospheric/Instrument, and
  `elevatedSurface` for Glass; the theme menu uses `surface` for all themes.
  Where a component has no outline (for example, zero panel border width), do
  not invent one. Resolve each rendered outline opaque and require at least
  3:1 against every actual adjacent opaque background. This 3:1 criterion is
  the owner-selected measurable interpretation of “visibly distinct.”
- Audit visible text-role consumers in the same finite inventory: primary and
  supporting text, precipitation text, selected/action text and borders, and
  action-button foreground on its action fill. Each actual text/background
  pair must meet 4.5:1 in High contrast; supporting text retains the approved
  7:1-or-promote rule. If a text role or action border fails its actual pair,
  resolve that foreground to High contrast `content`; for text on an opaque
  action fill, use whichever of resolved `content` or `canvas` has the higher
  ratio as `actionContent`. Recheck the resolved pair and block if it still
  misses 4.5:1. A resolved foreground may share the `content` color where
  needed, while typed roles, visible labels, selected semantics, and status
  wording retain their meaning. Warning/danger are not currently used as
  production text; if inventory finds otherwise, record the consumer and stop
  for a separately approved policy rather than infer a pairing.
- Effects Off takes precedence for a solid canvas, no motion, fully opaque
  panels/surfaces and outlines, and complete semantic appearance for every
  theme, contrast, and layout. It does not remove, reorder, or reinterpret
  content.
- Standard/Simple changes geometry only. High contrast changes palette and
  resolves panel/outline opacity to opaque. Effects preserve their requested
  identity and determine backdrop/motion; their translucency is suppressed by
  High contrast and Off. Off overrides both axes for solid backdrop, no motion,
  and opaque panels/outlines. Preserve `controlTargetMinimum >= 48.dp` for both
  layouts.
- Resolver remains pure. Weather values, persistence, device/system state,
  page composition, and Compose runtime do not enter resolution.

## Implementation steps

1. Confirm the TP.2A catalog conformance command still passes. Inventory the
   typed palette roles, every current production text/outline consumer, and
   each actual background/effective foreground pairing from existing
   theme-engine components. Record the finite inventory in cycle evidence.
   Keep the six catalog JSON inputs unchanged. Do not change Compose
   components; if current usage cannot be represented by this inventory and
   resolver policy, stop with the exact consumer and blocker.
2. Add a deterministic WCAG relative-luminance and contrast-ratio helper using
   sRGB linearization and `(Llighter + 0.05) / (Ldarker + 0.05)`. Test known
   black/white, equal-color, and boundary cases; avoid duplicating the helper
   algorithm in expected-value assertions.
3. Implement High contrast resolution for the inventoried actual pairs. Ensure
   required text reaches 4.5:1, supporting text reaches 7:1 or resolves to
   `content`, and precipitation/action text and action-button
   foreground/background pairs reach 4.5:1. Promote failing text/action
   foregrounds to resolved `content`; for action-button text, choose whichever
   of resolved `content` or `canvas` has higher contrast against the opaque
   action fill. Recheck each resolved pair and block if still below 4.5:1.
   Preserve semantic meaning
   through typed roles, exact labels, and selection semantics. Make every
   rendered outline opaque and at least 3:1 against its actual adjacent opaque
   background. Resolve panel/outline opacity to `1f` for High contrast at all
   effects levels; keep Standard palettes and canonical definitions unchanged.
   If a current pair cannot meet its threshold within this resolver boundary,
   close BLOCKED with the consumer and exact measured pair instead of changing
   a component or weakening a threshold.
4. Preserve layout and effects identity. Verify Standard/Simple geometry-only
   behavior, requested backdrop/motion behavior, Full-motion fallback for
   unsupported themes, High contrast opacity precedence, and Effects Off
   precedence across every contrast/layout combination. Keep requested theme,
   contrast, layout, and effects identity intact in every resolved result.
5. Expand `ThemeResolverTest` across all 60 combinations. Assert determinism,
   complete typed output, identity, standard palette stability, all inventoried
   High contrast text and outline thresholds against actual opaque backgrounds,
   supporting-role promotion, opacity precedence, layout/effects independence,
   `controlTargetMinimum >= 48.dp`, and Effects Off behavior. Retain or refine
   existing focused resolver assertions rather than duplicating equivalent
   tests. Replace the current `highContrastChangesOnlyPalette` expectation:
   High contrast also forces panel/outline opacity to `1f`, while preserving
   typography, geometry, render styles, selected backdrop/motion identity, and
   the requested effects/layout values. Add focused helper cases for
   black/white, identical colors, translucent inputs after compositing, and
   values adjacent to each threshold.
6. Update `docs/ARCHITECTURE.md` with the implemented resolver boundary and
   actual-background/opacity precedence. Keep it consistent with
   `docs/theme-system/design-pack/DETAILS.md`; do not modify approved catalog or
   design-pack inputs.
7. Run focused theme-engine JVM tests, `python scripts/dev.py catalog`,
   `python scripts/dev.py workflow`, `python scripts/dev.py contract`,
   `git diff --check`, and `python scripts/dev.py check` when the Android SDK
   and dependencies are available. Preserve exact command outputs and any
   environment blocker under the cycle evidence directory.
8. Review the diff against this boundary. Update the TP.2A execution head and
   close only the resolver-policy slice with the exact PASS/BLOCKED result.
   TP.2A closes only if all acceptance criteria pass; TP.2B stays gated on a
   blocker.

## Acceptance criteria

- All 60 theme/contrast/layout/effects combinations resolve deterministically
  with complete typed appearance and their selected identity axes intact.
- Standard resolved palettes equal the approved typed catalog baselines.
- Every inventoried High contrast text pair on actual opaque component
  backgrounds reaches 4.5:1; supporting text reaches 7:1 or resolves to the
  `content` role. The canonical Standard palette is not mutated.
- High contrast surfaces and outlines are opaque; outlines reach 3:1 against
  every actual adjacent opaque background identified by the current-component
  inventory.
- High contrast panel/outline opacity is `1f` for all effects levels. Layout
  changes geometry only; effects preserve selected identity and determine
  backdrop/motion, subject to High contrast/Off opacity precedence. Effects Off
  is solid, static, opaque, and complete for every theme, contrast, and layout.
- Simple geometry retains at least 48 dp control targets. Theme-specific
  unsupported-Full-motion fallback remains as declared by the typed catalog.
- Focused tests, catalog/workflow/contract checks, diff check, and broader
  repository check are recorded with actual outcomes. No pass is inferred from
  a narrower check.
- No Compose composition, weather semantics, preference persistence, approved
  catalog input, or installed visual acceptance claim is included.

## Verification and evidence

Retain under `.codex/test-artifacts/062-tp2a-resolved-appearance-policy/`:

- focused JVM test command and complete output;
- actual text/outline consumer-to-background inventory; WCAG helper
  known-value/boundary results; and a 60-combination policy summary with
  worst-case pair ratios, opacity results, and supporting-role promotions;
- catalog, workflow, contract, diff-check, and broader-check command results;
- hashes or equivalent proof that all six approved JSON catalog inputs are
  unchanged;
- final changed-file list/diff review and exact explanation of any unrun check.

This is a static resolver policy slice. Installed screenshots, visual
comparison, TalkBack/service review, Settings persistence, and page renderer
acceptance are outside this plan; TP.3 owns installed theme comparison.

## Risks and assumptions

- TP.2A catalog schema and Kotlin parity remediation passed in cycles 060–061;
  approved JSON remains the design-input authority and typed Kotlin remains
  runtime authority.
- The source thresholds are those stated in the adopted theme design pack:
  4.5:1 required text, 7:1 supporting-text promotion, and the WCAG 3:1
  non-text contrast criterion chosen by the owner for outlines against actual
  adjacent component backgrounds.
- `ResolvedTheme.palette.secondaryData` can represent the effective
  supporting-text role without adding a parallel color field. If component
  consumers require a different typed contract, stop before widening the model
  boundary and record the precise decision needed.
- If any required pair cannot meet the thresholds while preserving the theme's
  identity and semantic roles within this boundary, close BLOCKED with the
  diagnostic and exact limiting values. Do not weaken thresholds or edit
  approved catalog/design-pack inputs in this cycle.

## Out of scope

- Compose component or page rendering, TP.2B–TP.2E, TP.3 installed visual
  acceptance, screenshot matrices, or accessibility-service review.
- Appearance settings, persistence, device reduced-motion input, user/system
  state, and any fetch or canonical-weather data changes.
- Approved theme JSON/design-pack/reference edits, theme identity changes,
  catalog schema/parity checker changes, or unrelated catalog value updates.
- Forecast, location, provider, alert, provenance, navigation, and theme
  selection behavior.
