# Plan 008 — Theme B semantic appearance resolver

Status: Completed
Cycle ID: 008-theme-b-semantic-appearance-resolver
Roadmap item: R0.5
Created: 2026-09-21
Revised: 2026-09-21
Revision: implementation-ready first revision

## Objective

Complete R0.5 as one Theme B appearance-foundation slice. The independently
observable outcome is the installed existing Home application rendered through
one internal `ResolvedAppearance` contract: semantic Theme B color roles,
existing typography, shared layout/shape values, and the existing Effects Off
or Subtle behavior resolve together before rendering.

This is a migration of shared appearance ownership, not a Theme B page
redesign. The complete deterministic fixture must retain its visible weather
facts, semantics, interaction behavior, and physical layout values while raw
Oxygen palette constants stop leaking into the Home renderer and weather-mark
renderer.

## Production boundary

Production changes are limited to these files and seams:

- add one UI-local appearance contract/resolver file under
  `app/src/main/java/com/oxygen/weather/ui/`; it owns internal
  `ResolvedAppearance`, Theme B semantic colors, the existing typography,
  shared layout/shape tokens, and `ResolvedEffects`;
- modify `OxygenTheme.kt` so it is a Material bridge and composition-local
  provider for the resolved contract, not a source of globally consumed
  `Oxygen*` color constants;
- modify `EffectsConfiguration.kt` only to retain or move the pure
  Effects-Level-to-effects resolution under the new contract without changing
  the public `EffectsLevel` values or launch behavior;
- modify `OxygenWeatherApp.kt` and `WeatherMark.kt` only at existing shared
  appearance-consumption points: root/atmosphere rendering, Material theme,
  selected/inactive action color, weather-mark tint/accent, `GlassPanel`, and
  repeated page/control/panel layout values. Preserve the numerical values and
  component tree unless a mechanical resolver parameter/local read is needed;
- add or update focused JVM tests under `app/src/test/java/com/oxygen/weather/ui/`.

The plan permits no changes to `data/`, `derived/`, `presentation/`, activity
launch selection, build configuration, dependencies, or user-visible page
structure. `OxygenWeatherApp(presentation, effects)` remains source-compatible,
and `EffectsLevel` remains exactly `OFF` and `SUBTLE` for this slice.

## Functional invariants

- Home remains `Now -> Hourly -> Daily -> Details`, with the outer pager as
  the only global horizontal-swipe owner.
- Weather values, units, chronology, unavailable states, provenance,
  source/freshness wording, accessibility semantics, and page behavior do not
  change.
- Theme B is the fixed development default for this slice; no persisted theme
  choice or additional theme implementation is introduced.
- Effects Off resolves to an opaque, static, complete rendering: solid root
  background, fully opaque readable surfaces/outlines, and immediate
  navigation. Effects selection never changes weather meaning or refetches.
- Components do not branch on raw theme identifiers; they consume semantic
  appearance roles and resolved values.
- Important facts remain visible text with their existing meaningful
  semantics; decorative marks remain supplemental.
- Existing physical values remain stable: page gutters, panel insets, control
  target minimums, panel corner/border treatment, typography metrics, and
  weather-mark geometry do not receive visual-polish changes in this slice.
- The normal launch remains Subtle. The existing debug-only
  `oxygen_effects_off` hook continues to select Off only in a debuggable build;
  no preference, settings control, Full effects level, contrast setting, or
  system motion policy is introduced.

## Implementation steps

1. Record the source and installed baseline.

   Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
   the existing unit suite before production edits. Inspect exactly the current
   palette/typography in `OxygenTheme.kt`, effects in
   `EffectsConfiguration.kt`, and their consumers in `OxygenWeatherApp.kt` and
   `WeatherMark.kt`. Retain command output in the cycle evidence directory.
   Treat the installed R0.3 captures as historical comparison material, not as
   new visual acceptance evidence.

2. Define the minimal internal Theme B resolver contract.

   Add one immutable, UI-local `ResolvedAppearance` model and pure
   Theme-B-default resolver. It must expose only roles consumed by current
   rendering:

   - canvas and atmosphere field colors;
   - readable panel/elevated-panel, content, outline, primary-data and
     secondary-data colors;
   - condition/precipitation accents, selected/inactive status treatment, and
     action colors;
   - the existing `Typography` values;
   - shared layout values already repeated by the current renderer: page
     gutter/stack gaps, panel inset, control gap/minimum target, and shared
     panel border/corner values;
   - `ResolvedEffects`, with root-background choice, panel/outline opacity,
     and navigation motion.

   Keep concrete color literals private to this resolver file. Do not create
   unused roles for alerts, loading, cache state, charts, another theme,
   high-contrast mode, or future settings; later slices own those semantic
   states. The resolver receives the existing `EffectsLevel`, not provider
   data, a repository, presentation strings, or a raw theme ID.

3. Bridge the resolved contract into current Compose rendering.

   Have `OxygenWeatherApp` resolve once per `effects` input, pass that result
   into `OxygenTheme`, and provide it through a UI-local composition boundary.
   Map its semantic colors/typography into `MaterialTheme` so existing text and
   buttons keep their current meaning. Replace direct `Oxygen*` color use in
   the atmosphere and weather-mark paths, and make `GlassPanel` take its fill,
   outline, opacity, border, and shape from the resolved contract.

   Migrate only repeated layout values that this shared appearance boundary
   owns; preserve their exact dimensions. Do not tokenize or alter one-off
   page heights, weather-mark drawing geometry, page-specific placement, or
   any condition/data branching. No component may inspect a theme identifier;
   all such selection stays inside the resolver.

4. Preserve and centralize the effects guarantees.

   Keep `OFF` resolving to a solid canvas, fully opaque panel/outline, no
   atmosphere composition, and immediate pager movement. Keep `SUBTLE` at its
   existing atmosphere/translucency/animated-navigation values. Preserve the
   debug launch selector and the normal Subtle default exactly; do not add
   `FULL`, persistence, settings, or system reduced-motion behavior.

5. Add focused deterministic JVM coverage.

   Add a resolver test class, and retain/update `EffectsConfigurationTest` as
   needed, with assertions for:

   - Theme B's default resolved roles and the Material color bridge, including
     opaque readable base colors and the exact existing typography/layout/
     shape values that migrate into the resolver;
   - every role used by the current root, panel, action, and weather-mark
     renderer having a non-transparent concrete value;
   - OFF resolving to solid background, `1f` panel/outline opacity, immediate
     navigation, and no-atmosphere path;
   - SUBTLE retaining the existing atmosphere, opacity, and animated-motion
     values;
   - `LaunchEffectsTest` retaining debug true -> OFF and every other existing
     launch-selection outcome -> SUBTLE;
   - existing `HomePresentationTest`, data, and derived tests passing
     unchanged, demonstrating no weather/presentation rewrite.

   `python scripts/dev.py contract` remains the focused structural regression
   for one outer pager, retired-UI exclusion, and the presentation-only Compose
   boundary. Do not add brittle source-string tests as a substitute for
   installed rendering or accessibility verification.

6. Verify the actual installed UI matrix.

   Build, install, and launch the real debug application. At the project
   360x640 baseline and font scale 1.0, capture each of Now, Hourly, Daily, and
   Details in both normal Subtle launch and debug Effects Off launch. In the
   Off run, exercise and capture Hourly Earlier/Later and date jump, Daily
   Earlier/Later, page-tab navigation, and Android Back from Details through
   Now; verify immediate motion by observation, not by a screenshot claim.
   Capture a UI hierarchy in each effects state to confirm named pages,
   controls, and forecast-entry summaries remain exposed.

   Repeat Now, Hourly, Daily, and Details at the project font scale 1.3 in
   both effects states, inspecting for critical clipping, overlap, or unusable
   48dp controls. Restore the device font scale afterward. RTL and TalkBack
   service traversal are not changed contracts here; record them as unverified
   unless actually exercised. Screenshots are visual evidence only and do not
   replace pure resolver tests or interaction observations.

7. Update documentation from observed implementation and close with evidence.

   During this active cycle, keep R0.5 marked ACTIVE in `docs/ROADMAP.md`. On
   successful closure, change only R0.5 to DONE with its history reference.
   Update `docs/ARCHITECTURE.md`'s `ui/` section to describe the implemented
   UI-local resolved-appearance boundary, the Material bridge, and the rule
   that Compose receives resolved appearance rather than theme IDs or data
   sources. Update the current-candidate/theme-effects wording in
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` only to record the actual fixed
   Theme B resolver/effects implementation while preserving its existing
   future-settings caveats. Update `VERIFICATION.md` only with commands and
   installed observations actually obtained.

   Do not modify `docs/SPECIFICATION.md`: this slice implements its existing
   appearance and accessibility requirements without changing scope. Close the
   cycle only after preserving evidence and recording all unavailable checks in
   the generated history record.

8. Run closure checks and inspect the final diff.

   Run focused tests while iterating, then `python scripts/dev.py test`,
   `python scripts/dev.py contract`, and `python scripts/dev.py check` when
   Android SDK/dependencies permit. Record any wrapper/environment limitation
   and the equivalent command used, if any; do not repair Gradle or change a
   dependency under this plan. Run `git diff --check` and inspect every final
   production, documentation, and cycle-record change before closure.

## Acceptance criteria

- One internal, immutable `ResolvedAppearance` resolver is the Theme B source
  of truth for all current shared color/effect literals, Material theme
  mapping, typography, and migrated shared layout/shape values.
- `OxygenTheme`, the root atmosphere path, `GlassPanel`, and `WeatherMark`
  consume resolved roles. No legacy exported `Oxygen*` palette constant remains
  a rendering dependency outside the resolver implementation.
- Theme B is the fixed default. No public API break, persisted preference,
  additional theme, `FULL` effects level, contrast mode, system-motion policy,
  settings destination, or data behavior is introduced.
- OFF is demonstrably opaque/static/complete in unit and installed evidence;
  SUBTLE demonstrably retains its current behavior. The existing debug launch
  hook and normal Subtle default retain their exact selection behavior.
- The installed compact and 1.3-font matrices show all four pages in both
  effects states. They preserve named pages, readable critical weather/source
  facts, Hourly and Daily explicit controls, chronology, semantic hierarchy,
  and Android Back behavior; any defect or unavailable test is recorded.
- Focused resolver/effects/launch tests, existing deterministic regression
  tests, source contract, and `python scripts/dev.py check` pass when the
  local environment supports them. The final history record distinguishes any
  unavailable verification rather than implying it passed.
- `docs/ARCHITECTURE.md`, the narrow current-candidate implementation wording
  in `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, roadmap state, and
  `VERIFICATION.md` reflect only observed results.

## Context-budget boundary

This remains below the 45% implementation-context limit because it has one
production concern: route the existing shared appearance values through one
resolver without redesigning content. Do not expand it into a shared component
library, a page restyle, status/loading UI, persistent appearance settings, or
RTL/accessibility remediation.

If a required change needs a new reusable monitor component, alters a
page-specific layout value, changes a presentation string/semantic, or cannot
be verified as an existing-value migration, stop and create a dependent plan.
The installed verification matrix is evidence work for this resolver change;
it is not authorization to absorb visual defects unrelated to the resolver.

## Verification and evidence

Evidence path:

```text
.codex/test-artifacts/008-theme-b-semantic-appearance-resolver/
```

Retain exact output for:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

When an installed Android environment is available, retain at least:

```text
.codex/test-artifacts/008-theme-b-semantic-appearance-resolver/
  commands.txt
  focused-tests.txt
  contract-and-check.txt
  install-launch.txt
  subtle-now.png
  subtle-hourly.png
  subtle-daily.png
  subtle-details.png
  off-now.png
  off-hourly.png
  off-hourly-later.png
  off-hourly-date-jump.png
  off-daily.png
  off-daily-later.png
  off-details.png
  subtle-ui-hierarchy.xml
  off-ui-hierarchy.xml
  subtle-large-now.png
  subtle-large-hourly.png
  subtle-large-daily.png
  subtle-large-details.png
  off-large-now.png
  off-large-hourly.png
  off-large-daily.png
  off-large-details.png
  verification-notes.md
```

The notes must name the emulator/device, viewport, font scale, APK/build,
launch extra/effects state, exact interaction observations, command outcomes,
and unverified boundaries. A preview, a screenshot alone, or a byte-identical
claim about dynamic fixture timestamps is not acceptance evidence.

## Risks and assumptions

- The selected Theme B boards are directional, not pixel measurements. This
  resolver preserves current concrete values unless an accessibility defect is
  observed and a separate decision is recorded; it must not invent a visual
  polish target from the boards.
- The existing `EffectsConfiguration` guarantees and R0.3 installed evidence
  are the regression floor. Any OFF translucency, atmosphere composition, or
  animated navigation is a failure of this slice.
- The demo fixture derives its anchor from current time. Static-effects checks
  must distinguish decorative stability from legitimate timestamp changes and
  must not claim byte-identical screenshots.
- Material color roles do not capture weather/condition semantics by
  themselves. The local resolver may bridge them into Material while retaining
  explicit roles for the weather mark and atmosphere renderer.
- There is no Compose UI-test infrastructure in the current build. Pure
  resolver tests, source contract, hierarchy capture, installed interaction,
  and screenshot review are the evidence mix; do not claim automated Compose
  semantics coverage that is not added and run.
- A Gradle wrapper or emulator failure is a verification limitation, not scope
  to repair tooling inside R0.5. Record the attempted command and any
  equivalent verified path.

## Out of scope

- R0.6 shared monitor components or any reusable metric/hourly/daily/details
  component creation.
- Hourly, Daily, Details, or Now page-composition redesign and Theme B page
  polish beyond the resolver integration needed for existing rendering.
- Additional themes, persisted theme/contrast/layout/effects preferences,
  settings UI, RTL/large-font redesign, or service-level TalkBack testing.
- New weather data, presentation models, unit conversion, provider,
  repository, cache, alert, location, derived-signal, or chart-data behavior.
- Nested pagers, navigation redesign, deprecated Atmosphere Deck/art-sheet
  visual language, runtime photographs, or downloaded icon packs.
- Page-specific layout changes, changed spacing/typography values, visual
  polish, a new information hierarchy, or repairs to unrelated compact or
  large-font defects. These require their owning page/accessibility slice.
- Status/loading/cached/error UI, alert state presentation, chart components,
  or token roles that have no current renderer consumer.
