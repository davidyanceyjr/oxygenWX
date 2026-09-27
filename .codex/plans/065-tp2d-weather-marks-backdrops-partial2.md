# Plan 065-partial2 — TP.2D shared backdrops

Status: Planned
Cycle ID: 065-tp2d-weather-marks-backdrops-partial2
Roadmap item: TP.2D-partial2
Created: 2026-09-26
Depends on: PASS for `065-tp2d-weather-marks-backdrops`
Difficulty: 5/10
Context budget: target at most 35% of a fresh context window; stop before 45%.

## Objective

Complete TP.2D by implementing and verifying the five shared resolved theme backdrops. Confirm that each backdrop remains decorative and preserves caller content and interaction, and that Effects Off is a solid opaque field. Perform a bounded integrated check with the D29 marks completed by the predecessor.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`: `ProductionBackdrop` drawing only; use the resolved `BackdropStyle` and palette without raw theme-ID branches.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ProductionWeatherVisualsTest.kt`: exhaustive backdrop resolution and Effects Off assertions for all themes and effects levels.
- `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/`: installed backdrop rendering/content/pointer checks and bounded integrated mark/backdrop captures.
- `.codex/plans/065-tp2d-weather-marks-backdrops-partial2.md`, `.codex/current.md`, `docs/theme-pack-roadmap.md`, and `.codex/test-artifacts/065-tp2d-weather-marks-backdrops-partial2/`: cycle state, final TP.2D status, and evidence.

Do not change mark treatments completed in partial1, page composition/callers/layout, resolver/catalog tokens, presentation/domain models, weather meaning, data behavior, navigation, or settings. Preserve existing public composable APIs.

## Functional invariants

- Every non-Off backdrop is selected from the resolved appearance and remains condition-neutral decoration. It does not encode weather facts, capture pointer input, or expose independent accessibility meaning.
- Effects Off is an opaque solid canvas with no gradient, grid, scenic overlay, glow, blur, or motion; caller content remains visible, complete, and interactive.
- Five themes retain distinct approved shared backdrop identities as declared in the resolved catalog and supported by the D31 field-level direction. Do not silently substitute page-specific D31 compositions; TP.3 owns those.
- Backdrop changes preserve mark output, supplied content, semantics, touch behavior, and contrast. They do not trigger data fetches or change navigation or weather interpretation.
- No backdrop palette or style token changes. If a visual defect can only be resolved by changing the catalog/token contract or app composition, record it and stop this bounded slice.

## Visual objective and installed conditions

Render the existing resolved shared backdrop styles with clear theme identity while keeping foreground content legible. Verify actual installed output; source inspection and Compose previews are insufficient.

- Exercise all five themes at Standard/Subtle and Standard/Effects Off. Add one representative High-contrast case per backdrop style.
- Capture labeled installed host images for all five themes under Subtle and Effects Off. Include opaque representative foreground content and a semantic/interactable test target to demonstrate that the backdrop stays behind content and does not own input.
- Capture an integrated representative state with each completed mark style on its resolved backdrop at Subtle and Effects Off. Include at least one approved D29 omission and one null-condition case with the supplied text still visible.
- Include one compact 360 × 640 dp, font scale 1.3 state and one RTL state. These targeted checks do not create a full Cartesian matrix or page-composition acceptance.
- Visually inspect backdrop identity, foreground readability, opacity, clipping, and evidence that the marks from partial1 remain unchanged. Record actual device, viewport, density, font scale, direction, contrast, theme, and effects.

## Implementation steps

1. Confirm the partial1 PASS record. Audit `ProductionBackdrop`, all resolved backdrop styles/palettes, current renderer behavior, D31 shared field-level guidance, and the instrumentation host. Save a five-theme expected-style table and initial rendering observations to this cycle's evidence path.
2. Add/extend deterministic tests for every resolved backdrop style and all five themes under Subtle, Full, and Off. Assert Off resolves to `SOLID` regardless of an inconsistent copied style and that the resolved Off canvas is opaque/static according to the existing theme contract.
3. Add installed Compose checks for all five themes at Subtle and Off, plus representative High contrast. Assert caller-supplied foreground text and semantics remain present, an interactive test target receives taps, and Effects Off produces only the opaque canvas. Capture and export the ten theme/effects images.
4. Make only the smallest `ProductionBackdrop` drawing correction demonstrated by the tests and installed visual inspection. Do not introduce arbitrary new numeric tokens or alter `ResolvedTheme` values. If the target visual cannot be met within existing styles, record the exact defect and stop.
5. Run focused JVM and Android instrumentation checks, then `python scripts/dev.py test`, `build`, `contract`, `workflow`, `check`, and `git diff --check`. Capture integrated mark/backdrop examples, including an approved omission and null condition. If installed Android or export is unavailable, record the blocker and do not claim installed rendering passed.
6. Review both partial diffs against their boundaries. Update `docs/theme-pack-roadmap.md` with this partial's exact outcome and close TP.2D PASS only if both partials pass all their gates; otherwise record the blocker and stop before TP.2E.

## Acceptance criteria

- All five approved shared backdrop styles render from `ResolvedTheme`, with no raw theme identity branch or unapproved catalog/token edit.
- Installed Subtle, Effects Off, and representative High-contrast evidence demonstrates theme identity, opaque/static Off behavior, unchanged caller content/semantics, foreground readability, and no input capture.
- Integrated captures confirm predecessor D29 mark behavior remains intact, including an explicit gap and null condition with supplied text present.
- Focused JVM/instrumentation checks and repository `test`, `build`, `contract`, `workflow`, `check`, and `git diff --check` pass and are recorded. Otherwise this partial is blocked and TP.2D remains open.
- No page composition, weather/data meaning, navigation, settings, TP.2E, TP.3, TalkBack service traversal, pixel-match, or release-acceptance claim is included.

## Verification and evidence

Retain under `.codex/test-artifacts/065-tp2d-weather-marks-backdrops-partial2/`:

- partial1 dependency PASS reference and five-theme backdrop resolution/style audit;
- focused JVM/instrumentation output and device/configuration details;
- ten installed backdrop captures (five themes × Subtle/Off), plus the bounded High-contrast, compact/font-scale, RTL, and integrated mark/backdrop captures;
- interaction/content/semantics and Effects Off opacity/static results;
- exact repository command outputs and final combined boundary review;
- any unverified limit, with its reason.

Evidence is shared-component level. It is not TP.2E showcase, page-level TP.3 acceptance, accessibility-service traversal, pixel comparison, or release acceptance.

## Risks and assumptions

- Partial1 passed and the compatible installed test host remains available.
- Existing `BackdropStyle` definitions and semantic palette roles are sufficient to implement the approved shared identities; no token changes are authorized.
- The D31 proposals guide theme field-level identity but do not prescribe page composition or arbitrary pixel values.

## Out of scope

- D29 mapping/treatment changes, already owned by partial1.
- Page-specific D31 scene routing, any page composition or caller/layout changes, and normal-app migration; owned by TP.3.
- Resolver/catalog changes, new tokens or theme identities, product/data changes, settings, navigation, weather semantics, TP.2E showcase, full responsive testing, TalkBack service traversal, pixel-diff acceptance, and release acceptance.
