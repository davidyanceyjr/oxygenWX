# Plan 021 — Production theme resolver foundation

Status: Completed
Cycle ID: 021-production-theme-resolver-foundation
Roadmap item: R0.11B
Created: 2026-09-22
Revised: 2026-09-22

## Objective

Implement the additive production theme foundation for Atmospheric, Glass,
Minimal OLED, Instrument, and Terminal. Define stable theme identities, complete
theme definitions, typed semantic/render-style roles, and a deterministic pure
resolver for theme, contrast, layout, and effects inputs. The resolver is
available for later component migration; the existing Theme B renderer remains
the app path and default throughout this cycle.

This cycle implements a usable and testable resolver contract, not only an
identity list. It does not select or display a production theme in the app.

## Production boundary

Production changes are additive and limited to
`app/src/main/java/com/oxygen/weather/ui/themeengine/` for theme models,
catalog, and resolver. Add focused JVM tests under
`app/src/test/java/com/oxygen/weather/ui/themeengine/`. Update only
`docs/ARCHITECTURE.md` and the R0.11B entry in `docs/ROADMAP.md` at closure.

The checked-in `docs/theme-system/tokens/catalog/` files and
`docs/theme-system/THEME_DESIGN_CONTRACT.md` are the design inputs. The
R0.11B `staged-production/` source is a candidate for review and selective
adaptation, not a source tree to copy. Resolve conflicts in favor of the current
repository and approved authority documents.

Do not change `ResolvedAppearance`, `resolveAppearance`, `EffectsLevel`,
`OxygenTheme`, `OxygenWeatherApp`, or current component call sites. Do not add
the staged Compose theme wrapper, CompositionLocal, legacy-effects adapter, or
bridge to the current sketch. Do not apply production appearance at runtime.
The production resolver is additive and remains unreferenced by app composition
until later migration slices.

## Functional invariants

- Themes change presentation only. Weather values, forecast membership and
  chronology, units, provenance/freshness, missing-data behavior, alerts,
  navigation, and accessibility meaning remain independent of theme input.
- Theme identity is a typed stable enum/key. Reusable components receive
  resolved roles/styles, never branch on a raw theme identity to change content
  or interaction behavior.
- Catalog membership is exactly the five approved themes, with a stable,
  explicit definition for each. Catalog ordering is Atmospheric, Glass,
  Minimal OLED, Instrument, Terminal.
- Every definition fully supplies the semantic palette, typography, geometry,
  surface/backdrop/hero/weather-mark styles, and preferred-effects metadata
  required by the future shared-component contract. Missing roles are not
  silently inherited from Theme B or another theme.
- Effects Off resolves every theme to a solid/opaque, static, complete
  appearance: no atmospheric/glass backdrop dependency, no motion, and
  effective panel/outline opacity of 1. Theme token colors remain fully opaque.
- Standard/High contrast and Standard/Simple layout are orthogonal resolver
  inputs. Contrast changes color values only; layout changes geometry only.
  Neither changes theme identity, weather meaning, or interaction semantics.
- Off/Subtle/Full effects are resolver inputs, not persisted settings. Resolver
  output describes the effective appearance policy; it does not claim that
  procedural backgrounds or animations are already rendered.
- Current `ResolvedAppearance` values and app launch/render behavior remain
  unchanged. No runtime selection, weather refetch, or settings behavior is
  introduced.

## Implementation steps

1. Read all five token JSON definitions, the theme design contract, current
   appearance and Effects Off implementation/tests, and the staged R0.11B
   candidate. Record any candidate/token mismatch in the cycle evidence; do
   not import contradictory values. Confirm the additive package can remain
   unused by app composition.
2. Add typed UI-local models for stable theme IDs, contrast/layout/effects
   inputs, semantic palettes, geometry, visual styles, theme definitions, and
   resolved output. Keep the model limited to presentation values and never
   include weather/presentation data, callbacks, persistence, or provider data.
3. Add exactly five catalog definitions. Map the checked-in semantic tokens and
   design personalities into explicit roles. Supply typography and all
   geometry values required by `docs/theme-system/architecture/COMPONENT_CONTRACT.md`;
   keep control targets at least 48dp. Keep display names descriptive only,
   not behavioral keys.
4. Add a pure resolver that takes typed theme, contrast, effects, and layout
   inputs and returns a complete resolved definition. Define High contrast as
   a palette-only overlay, Simple layout as geometry-only adjustment, and
   Effects Off as solid/static/opaque. Subtle/Full may use only the
   theme-defined visual/effects policy; they must not invent runtime motion or
   imply that backgrounds are implemented. No Compose APIs or globals are
   required by the resolver entry point.
5. Add focused deterministic tests for the full catalog and resolver contract
   (listed below). Do not duplicate the existing sketch resolver tests or
   alter them to make the production resolver pass.
6. Update the architecture description to distinguish the current sketch
   renderer from the additive production resolver and document its input/output
   boundary, default non-use, and handoff to R0.11C. Refine only R0.11B in the
   roadmap with this implementation boundary, acceptance evidence, and
   explicit completion record. Do not revise release requirements or later
   roadmap scope.
7. Run focused JVM tests, repository workflow and contract checks, and
   `git diff --check`. Inspect source, tests, docs, and final diff. Record exact
   results and limitations in cycle evidence/history at closure.

## Required tests

Add tests in focused catalog/resolver test files. They must establish:

- The catalog contains every and only `WeatherThemeId` exactly once in the
  approved stable order; each identity resolves to its matching definition and
  accessible display name.
- Every definition supplies every required palette/style/typography/geometry
  role, has valid opacity values, uses opaque semantic colors, and keeps
  applicable control targets at or above 48dp.
- Resolving each theme is deterministic for each supported combination of
  contrast, layout, and effects inputs; repeated calls return equivalent
  results and retain the requested theme identity.
- Effects Off for all themes selects solid backdrop and no motion, sets panel
  and outline opacity to 1, and leaves all semantic content roles present.
- Resolved palettes remain fully opaque for all themes and contrast settings;
  theme effects opacity values remain finite and within 0..1.
- High contrast changes palette only; it retains theme identity, typography,
  geometry, backdrop/surface/hero/weather-mark roles, and effects policy.
- Simple layout changes geometry only, preserves theme/palette/style identity,
  and does not reduce any interactive target below 48dp. Standard layout
  matches the catalog geometry.
- Subtle/Full resolution stays within the declared theme visual policy and
  does not silently resolve to an unsupported style. Tests assert resolver
  policy only, not animation or visual rendering that this slice does not
  implement.
- Review confirms the new catalog/resolver has no app-entry-point or current
  renderer call site; existing `ResolvedAppearanceTest` and
  `EffectsConfigurationTest` remain unchanged and passing.

Do not add screenshot/Compose UI tests: the production resolver is not consumed
by the installed renderer in this slice. Do not claim visual validation.

## Documentation updates required at closure

- `docs/ARCHITECTURE.md`: replace the stale description that calls the current
  resolver the fixed Theme B resolver with an accurate status split. Describe
  the still-active UI-local sketch path, the additive production catalog and
  pure resolved-theme boundary, its no-weather/no-navigation role, and the
  fact that app composition remains on the sketch pending R0.11D–R0.11G.
  Preserve the existing architecture diagram and future component contract;
  align wording with the accepted types and behavior actually implemented.
- `docs/ROADMAP.md`: expand R0.11B to state the typed catalog/resolver scope,
  resolver axes, Effects Off guarantees, non-use by the app, focused test/check
  evidence, and a link to the cycle history. Change R0.11B to DONE only at
  closure after every acceptance criterion passes; do not change other status
  entries.
- `.codex/history/<date>-021-production-theme-resolver-foundation.md`: record
  actual files/boundary, verification commands and results, candidate/token
  reconciliation, and limitations. This is required cycle closure, not a
  substitute for either authority-doc update.

No update is needed to `docs/SPECIFICATION.md` or
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; their five-theme, invariance, and
resolver-boundary requirements already cover this slice. Do not edit staged
candidate source or token JSON to make implementation match an unapproved
candidate.

## Acceptance criteria

- Five complete production definitions exist and are based on approved token
  and theme-system authority, with documented candidate discrepancies handled
  explicitly.
- The typed resolver covers theme, contrast, layout, and effects inputs with
  deterministic semantic output and all tested invariants above.
- The production package is additive and unreferenced from the current app
  rendering path; current `ResolvedAppearance`/effects tests and behavior are
  preserved without source or binary compatibility breaks.
- `python scripts/dev.py test` passes, including the new tests and existing
  appearance tests. `python scripts/dev.py workflow`,
  `python scripts/dev.py contract`, and `git diff --check` pass. If Android
  dependencies prevent a check, the history records the exact command and
  failure; that check is not reported as passed.
- `docs/ARCHITECTURE.md`, R0.11B in `docs/ROADMAP.md`, cycle evidence, and
  history accurately describe the implementation and verified boundary.
- No installed visual evidence is claimed or required: the resolver is not
  wired to app composition and no page is migrated in R0.11B.

## Verification and evidence

Store concise scope notes and focused command outputs under
`.codex/test-artifacts/021-production-theme-resolver-foundation/`. Run at least
the focused resolver/catalog JVM tests while iterating, then the full
`python scripts/dev.py test`, `python scripts/dev.py workflow`,
`python scripts/dev.py contract`, and `git diff --check` before closure. Keep
large logs out of context and record file paths plus outcomes. Update roadmap
status and close to history only after the acceptance checks are complete.

## Context budget

Expected implementation and verification are approximately 35% of one context
window, below the roadmap's 45% ceiling. Keep the slice to three production
files (models, catalog, resolver), two focused JVM test files, two named
authority-document updates, and cycle records. Review token files in bounded
groups, retain command output in artifacts, and summarize results. Do not paste
full candidate files, screenshots, or build logs into context. If the
implementation requires Compose integration, altering current appearance
types, expanding beyond these production files, or otherwise approaches the
45% threshold, stop before widening scope and create a dependent plan.

## Risks and assumptions

- Theme token JSON provides color, spacing, surface, and motion inputs; the
  remaining typography/geometry decisions are documented in approved theme
  references and the staged candidate. Resolve any conflict using the current
  specification and architecture, and record decisions rather than blindly
  copying candidate code.
- The current renderer must remain intact until its migration/cutover slices.
  Any required adapter belongs to a later migration boundary, not this plan.
- JVM test/build availability depends on the host's Android/Gradle setup; record
  exact unavailable checks without claiming success.

## Out of scope

- CompositionLocal, MaterialTheme/provider wrapper, bridge/adapter, or any app
  entry-point/current component wiring.
- Page/shared-component implementation, production renderer migration/cutover,
  preview gallery, installed visual verification, or retiring sketch code.
- Persisted theme, contrast, effects, or layout preference; settings UI and
  system reduced-motion integration.
- Weather, alert, presentation, navigation, accessibility-content,
  repository/provider, network, or cache behavior changes.
- Contrast-ratio redesign of token palettes, new theme personalities, visual
  asset/runtime drawable creation, dependency or build-system changes.
