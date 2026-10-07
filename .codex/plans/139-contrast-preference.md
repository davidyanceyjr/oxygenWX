# Plan 139 — Contrast preference

Status: Completed
Cycle ID: 139-contrast-preference
Roadmap item: R5.3
Created: 2026-10-07
Reviewed: 2026-10-07

## Objective and independently observable outcome

Persist the user's Standard or High contrast choice, restore it when the app
starts, and expose it through the existing Appearance destination. A contrast
selection updates the production resolver immediately and changes presentation
cues only. This closes R5.3 as one independently verifiable preference slice;
it does not build the broader Settings information architecture.

## Authority, dependencies, and implementation decisions

- `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` govern
  weather meaning, appearance, navigation, and accessibility. R5.3 in
  `docs/ROADMAP.md` supplies the exit criterion. Cycles 137 and 138 completed
  the Activity-owned theme preference and the existing Appearance destination.
- `ContrastLevel.STANDARD` and `ContrastLevel.HIGH` in
  `ui/themeengine/ThemeModels.kt` and `resolveTheme` in
  `ui/themeengine/ThemeResolver.kt` are the canonical choice and renderer
  behavior. Existing resolver treatment is the acceptance baseline; change its
  palette rules only if a focused failing contract test demonstrates a defect.
- Current ownership is concrete: `MainActivity` constructs
  `ThemePreferenceSelection`, owns the Compose-observed `selectedThemeIdState`,
  and passes the choice and callback into `OxygenWeatherApp`. That app currently
  calls `resolveTheme(themeId, effects = themeEffects)`. Contrast follows this
  same path: an Activity-owned `ContrastPreferenceSelection`, one observed
  contrast state, callback into the existing app, and
  `resolveTheme(themeId, contrast = selectedContrast, effects = themeEffects)`.
  Do not keep a separately saved or independently authoritative contrast state
  in Compose.
- Persist contrast in a separate application-private preference key/adapter,
  using stable `standard` and `high` IDs. Do not change the theme preference
  format or combine the two choices into a new settings schema. Mirror the
  theme preference result behavior: absent, malformed, and unknown IDs
  default to Standard; read failures remain `Failure`; write failures remain
  `FAILURE`; the effective in-session choice updates even if saving fails.
- `ThemeAppearanceSurface` is already the exclusive root replacement route for
  Appearance. Add the contrast section to this surface and reuse the existing
  route, return/Back handling, and route state. Do not add a destination,
  navigation owner, pager, or alter theme selection behavior.
- Existing `ThemeAppearanceApplicationFlowTest` seams provide the integration
  pattern: install preference stores through Activity test hooks and count
  forecast, alert, cache-read, and cache-write operations. The fixture has no
  selected location, so after startup settles its expected operation baseline
  is zero. Assert against that settled baseline; do not add production metrics.
- No owner decision is required to plan this slice. Device/emulator availability
  is an execution-time condition, not a planning dependency.

## Production boundary

Limit production changes to the contrast preference contract/selection owner,
its application-private persistence adapter, Activity-to-Compose ownership and
resolver wiring, and the existing Appearance destination's contrast choice.
Keep weather domain, provider, repository, cache, forecast presentation data,
and alert behavior outside this boundary. Resolver palette changes are
conditional on focused evidence and must remain within R5.3's contrast
acceptance.

## Functional invariants

- Contrast is independent of the selected theme. Standard and High remain
  available under Atmospheric, Glass, Minimal OLED, Instrument, and Terminal.
- A contrast change changes resolved visual treatment only. Weather values,
  condition identity, chronology, unavailable states, provenance, valid/update
  times, freshness, alerts, page identity, navigation, and accessibility
  meaning remain unchanged.
- Contrast reads, writes, restoration, and selection do not trigger forecast or
  alert requests, or mutate canonical weather/cache data.
- Persist stable explicit contrast identifiers. Absent, malformed, or unknown
  values use Standard. Read/write failures remain distinguishable; a failed
  write does not prevent the selected contrast from taking effect in-session.
- Selected state is available as visible text and meaningful single-choice
  semantics. Each option provides at least a 48dp target. Existing theme
  selection, Appearance route return/Back behavior, and Effects Off behavior
  remain intact.
- Effects Off stays opaque, static, and complete for both contrast choices.

## Visual objective and environment

Make Standard/High selection obvious by its label and single-choice semantics
within the current themed Appearance destination, and show that selection
reflected in representative production Home rendering. Keep both contrast
choices readable at 360 × 640 dp, font scale 1.0, LTR, with Effects Off. Also
exercise font scale 1.3 and RTL in focused UI coverage, plus an Effects Off
visual sample. Do not claim service-level TalkBack verification unless it is
run.

## Implementation steps

1. Recheck the files identified above before editing. Trace
   `MainActivity`'s existing theme preference owner through its Compose state
   and callback into `OxygenWeatherApp`'s resolver call, plus the current
   `ThemeAppearanceSurface`, Back handling, test hooks, and
   `ThemeAppearanceApplicationFlowTest` operation counters. Confirm the
   recorded path still matches the source. If it changed, update this plan
   before implementation; do not create a second owner to work around drift.
2. Add a typed `ContrastPreferenceStore` and
   `ContrastPreferenceSelection`, mirroring the theme preference result
   contract while using `ContrastLevel` and Standard fallback. Add a separate
   application-private SharedPreferences adapter with stable `standard` /
   `high` IDs and a narrow fakeable preference seam. Keep storage errors
   observable and keep current-session selection effective after a failed
   write.
3. Add contrast-specific Activity test hooks, instantiate the owner in
   `MainActivity`, initialize the observed effective contrast from its read
   result, and wire selection into the existing `OxygenWeatherApp` parameter
   and resolver call. Preserve the theme owner's lifecycle and persistence;
   Compose must render the supplied contrast and must not save a second copy.
4. Extend the existing `ThemeAppearanceSurface` with labeled Standard/High
   single-choice options, visible selected text/state, stable test tags, and
   48dp minimum targets. Call the Activity owner on selection, keep the user on
   Appearance, and leave the existing theme choices, selected-theme callback,
   route return, and Back behavior unchanged.
5. Add focused JVM tests for selection-owner outcomes and persistence
   round-trips, absent/unknown/malformed IDs, read exceptions, failed commits,
   and both stable IDs. Extend resolver matrix coverage to assert all five
   themes × both contrast choices, including Standard palette identity and the
   existing High contrast/opacity contract. Add focused Compose/instrumentation
   coverage for single selection, immediate resolved contrast, theme/contrast
   independence, Activity recreation, fresh-owner restoration from persisted
   storage, and read/write failure outcomes through the test store.
6. Extend `ThemeAppearanceApplicationFlowTest` or its focused successor using
   the existing zero-request fixture and cache/forecast/alert hooks. Wait for
   ordinary startup to settle, snapshot the canonical weather fixture and
   visible facts (condition, temperature, provenance, update/freshness), then
   open/close Appearance and change contrast under every theme. Assert no
   forecast requests, alert requests, cache reads, or cache writes are added;
   assert the canonical snapshot and visible weather facts are unchanged. Reset
   all global test hooks in teardown. Do not add mutable production metrics.
7. Install and inspect the actual application path at 360 × 640 dp, font scale
   1.0, LTR, Effects Off. Capture the Appearance selected state and a
   representative Now rendering for each of the ten theme/contrast
   combinations. Check at least one unavailable/selection cue remains
   understandable without color alone and verify visible weather facts remain
   the same between contrasts. Exercise font scale 1.3 and RTL in focused UI
   checks; record any unavailable device, large-font, RTL, Effects Off, or
   service-level accessibility condition as unverified rather than passed.

## Acceptance criteria

- Standard and High contrast persist and restore after Activity recreation and
  by constructing a fresh owner/store over the same application preference
  data. Missing, malformed, and unknown IDs default to Standard. Read and
  write failures remain observable; failed writes do not suppress the
  in-session selection.
- Both choices are available in the existing Appearance destination and
  immediately update the production resolver. The selected option is visibly
  named and semantically selected, each option meets 48dp guidance, and
  contrast can be changed independently under all five themes.
- Deterministic tests cover all ten theme/contrast pairs. Standard preserves
  each theme's normal palette behavior; High meets the existing resolver
  contrast/opacity contract.
- Installed captures from the actual app cover Appearance plus a
  representative Now state for all ten combinations at the compact baseline.
  They show non-color selected/unavailable cues and unchanged visible weather
  facts. Effects Off is used for the matrix; font-scale 1.3 and RTL UI
  conditions are explicitly exercised or reported unverified.
- After normal startup reaches a terminal state, opening/closing Appearance
  and changing contrast adds no forecast requests, alert requests, cache reads,
  or cache writes. The canonical weather snapshot remains identical.
- Focused JVM and instrumentation checks, `python scripts/dev.py test`,
  `python scripts/dev.py contract`, `python scripts/dev.py check`,
  `python scripts/dev.py workflow`, and `git diff --check` pass where the
  Android environment supports them. Record exact results and any unverified
  boundary. Installed captures come from the actual app path, never a Compose
  preview.

## Verification and evidence

- Focused JVM verification: contrast selection/store tests and
  `ThemeResolverTest` coverage for the 5 × 2 theme/contrast matrix. Run the
  smallest relevant unit target while iterating; then run the repository test
  command.
- Focused Android verification: contrast adapter/preferences, Activity owner
  initialization and restoration, Appearance option visibility/semantics and
  immediate resolver update, theme/contrast independence, and weather/cache/
  operation invariance in the actual Activity path. Use existing fixture and
  test-hook patterns; no new production counters.
- Broader verification: `python scripts/dev.py test`,
  `python scripts/dev.py contract`, and `python scripts/dev.py check` when the
  Android SDK and dependencies are available. Run
  `python scripts/dev.py workflow` and `git diff --check` before closeout.
  Compare connected-suite failures with cycle 138's recorded baseline and
  distinguish pre-existing failures from regressions.
- Preserve evidence under `.codex/test-artifacts/139-contrast-preference/`:
  focused test outputs, startup and post-selection operation counts, canonical
  and visible-fact comparison, ten Appearance captures, ten representative
  Now captures, and environment metadata (app/build identity, device/emulator,
  viewport, density when available, font scale, layout direction, and effects).
  Record large-font, RTL, Effects Off, and service-level TalkBack outcomes
  explicitly. Do not infer installed visual or service-level acceptance from
  compilation, previews, or Compose semantics tests.

## Risks and assumptions

- R5.3 follows persisted theme selection (R5.2) and its existing Appearance
  surface (R5.2A); both are recorded complete. The broader Settings shell in
  R5.6 is later, so this slice adds the control to the current Appearance
  destination.
- `ContrastLevel` and current resolver palette treatment remain canonical.
  Existing tests and actual rendering establish whether any resolver correction
  is needed; do not expand to a general palette redesign.
- Cycle 138 recorded three connected-test failures matching the cycle 137
  baseline. Compare any connected failures with that record and its retained
  evidence; do not repair unrelated behavior within R5.3.
- Device availability may limit installed visual or service-level
  accessibility evidence. Report the exact boundary; unavailable evidence
  does not convert to a pass.

## Out of scope

- Effects preference, reduced-motion policy, ambient background work, Simple
  layout, or any R5.4+ slice.
- General Settings navigation shell or destinations beyond adding this control
  to the existing Appearance destination.
- Changes to theme catalog/identity, weather meaning, data/provider/repository/
  cache behavior, location, units, alerts, provenance, or fetch policy.
- New themes, broad visual redesign, or unrelated repairs to previously
  failing instrumentation paths.
- Release readiness, a full accessibility audit, or any claim that unrun
  TalkBack, RTL, or large-font conditions passed.
