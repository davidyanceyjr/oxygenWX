# Plan 067 — TP.2D Effects Off backdrop guarantee resumed

Status: Completed
Cycle ID: 067-tp2d-effects-off-backdrop-resume
Roadmap item: TP.2D-partial2
Created: 2026-09-27
Depends on: PASS for `065-tp2d-weather-marks-backdrops` (verified in cycle 065)
Difficulty: 3/10
Recommended implementation model: `gpt-6-sol` (Codex CLI), the cost-efficient workhorse tier for this bounded Compose instrumentation and test work.
Context budget: target at most 30% of a fresh context window; stop before 45%.

## Objective

Verify and, only if evidence requires it, correct the shared `ProductionBackdrop` Effects Off path. For all five themes it must show an opaque solid canvas, omit every decorative backdrop drawing operation, and preserve supplied visible content, semantics, and interaction.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`: `ProductionBackdrop` Effects Off branch only, if an observed defect requires a production correction.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ProductionWeatherVisualsTest.kt`: deterministic resolved-style/static-policy assertions for all five themes; retain the existing D29 matrix unchanged.
- New `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionBackdropEffectsOffTest.kt`: installed five-theme Effects Off rendering, caller-content/semantics, foreground click, and screenshot checks.
- `.codex/history/plans/067-tp2d-effects-off-backdrop-resume.md`, `.codex/current.md`, `docs/theme-pack-roadmap.md`, and `.codex/test-artifacts/067-tp2d-effects-off-backdrop-resume/`: active cycle record, next-slice sequence, and verification evidence.

Do not change public composable APIs. Do not change non-Off rendering, mark treatments, callers/page layout, resolver/catalog tokens, presentation/domain models, weather meaning/data behavior, navigation, or settings. If the defect requires one of those changes, record it and stop.

## Functional invariants

- Effects Off resolves `BackdropStyle.SOLID` and `MotionStyle.OFF` for all five themes. A defensive copied `ResolvedTheme` with `effects = OFF` and a stale non-solid `backdropStyle` must still render as solid.
- The uncovered backdrop area is the resolved theme's fully opaque `palette.canvas`; no gradient, grid, scenic path, glow, overlay, or motion is drawn.
- Caller content remains visible and semantically available above the backdrop. A click on the foreground test control reaches its callback exactly once.
- Marks remain decorative and unchanged. This cycle does not change the D29 mapping or add a second mark matrix; cycle 065 already verifies the condition/gap/null mapping.
- No weather fetch, navigation behavior, product meaning, or accessibility label is added or changed.

## Visual objective and installed conditions

The visual objective is a complete Effects Off surface: the empty area is visibly the theme's opaque canvas and all supplied foreground content remains legible, visible, and usable.

- Install and render all five themes at Effects Off in one deterministic test host. Use a fixed 360 × 640 dp viewport, LTR, font scale 1.0, Standard contrast, and a neutral foreground fixture containing a visible text label and a tappable control. Capture one labeled image per theme.
- In the instrumentation assertion, sample a fixed unobscured interior point away from foreground children and compare it with that theme's expected canvas ARGB value; require alpha 255. Do not infer opacity from a theme token assertion alone.
- Assert the text semantics node is present and the foreground tap callback increments once for each theme. Assert that no test content comes from a backdrop accessibility node.
- Add one targeted compact/large-font/RTL case at 360 × 640 dp, font scale 1.3, RTL, Effects Off, using a theme whose caller content fits in the viewport. Verify the same content, semantic, and click invariants. This case is responsive evidence for this Off contract, not a broad RTL/layout matrix.
- Do not capture or claim Subtle/Full theme identity or page-level visual acceptance in this cycle.

## Implementation steps

1. Confirm cycle 065 PASS and inspect the existing `ProductionBackdrop`, `resolvedBackdropStyle`, current `ProductionWeatherVisualsTest`, and Android test artifact-export pattern. Record the five expected canvas colors and the exact device/emulator configuration in the cycle evidence directory.
2. Extend focused JVM assertions only where coverage is missing: all five themes at Effects Off resolve to SOLID/OFF, the canvas is opaque, and a stale copied non-solid style is resolved defensively as SOLID. Keep the existing D29 test's expected values unchanged.
3. Add `ProductionBackdropEffectsOffTest`. For each of five themes, compose the component in the fixed host, verify supplied text semantics, perform the foreground click, capture the root image, and assert a fixed unobscured pixel matches the opaque resolved canvas. Add the single compact/large-font/RTL case and export the six labeled PNGs under the cycle artifact directory (and the established installed-evidence destination used by cycle 065 if the harness requires it).
4. Run the focused JVM and Android instrumentation tests. If they reveal a defect, make only the smallest correction inside the Effects Off branch of `ProductionBackdrop`, then rerun both focused checks and inspect all six installed captures. If the guarantee cannot be met within this boundary, save the failing evidence and stop the slice blocked.
5. Run `python scripts/dev.py test`, `python scripts/dev.py build`, `python scripts/dev.py contract`, `python scripts/dev.py workflow`, `python scripts/dev.py check`, and `git diff --check`. Preserve command output, screenshots, pixel assertion values, test results, and device/configuration details under `.codex/test-artifacts/067-tp2d-effects-off-backdrop-resume/`.
6. Review the final diff against this plan. Update the roadmap with the exact cycle-066 outcome and limitations, keep TP.2D open, and leave the next planned backdrop-style slice explicit. Close PASS only when the installed five-theme and targeted RTL/large-font checks plus focused/repository checks pass; otherwise record the exact blocker and stop.

## Acceptance criteria

- The five Effects Off cases render only the opaque resolved canvas behind caller content. Five installed captures and per-case pixel assertions establish this.
- All five cases preserve supplied text/semantics and pass a foreground click exactly once; the targeted compact/large-font/RTL case preserves the same invariants.
- Deterministic tests prove SOLID/OFF resolution for all themes and defensive handling of a stale copied backdrop style. The D29 expected matrix remains unchanged.
- Focused JVM and Android instrumentation checks and repository `test`, `build`, `contract`, `workflow`, `check`, and `git diff --check` pass and are recorded.
- No non-Off visual acceptance, theme-specific backdrop identity acceptance, integrated mark/backdrop matrix, page composition, TP.2E showcase, TP.3, TalkBack service traversal, pixel-match, or release acceptance is claimed.

## Verification and evidence

Retain under `.codex/test-artifacts/067-tp2d-effects-off-backdrop-resume/`:

- cycle 065 dependency PASS reference;
- resolved canvas ARGB and alpha expectation for each theme;
- five standard-host Effects Off captures and one compact/large-font/RTL capture;
- per-theme pixel, visible text/semantics, and click results;
- focused JVM/Android test output, device/build metadata, exact repository command output, and final diff review;
- any unverified boundary with its reason.

The installed captures establish only the shared Effects Off backdrop contract. They do not establish non-Off backdrop identity, mark/backdrop visual composition, Home page composition, TP.2E, TP.3, accessibility-service traversal, pixel-match, or release acceptance.

## Risks and assumptions

- Cycle 065 passed, and the installed Android test host is available. Verify host availability at the start of implementation; if unavailable, capture the precise tooling/device error and close blocked without claiming installed rendering.
- Existing test dependencies support Compose `captureToImage()` and deterministic bitmap pixel sampling, as used by the existing installed mark test.
- Difficulty is 3/10: Effects Off has a single shared branch and existing resolver assertions; the remaining effort is a narrow five-theme installed assertion and artifact capture.

## Out of scope

- Atmospheric, Glass, Minimal OLED, Instrument, and Terminal Subtle/Full backdrop identity and styling review; scheduled as dependent TP.2D slices.
- D29 mapping/treatment changes and broad mark/backdrop integration; cycle 065 owns mark behavior, and later backdrop slices own bounded integration examples.
- Resolver/catalog changes, new tokens/theme identities, page composition/callers/layout, normal-app migration, product/data changes, settings, navigation, and weather semantics.
- TP.2E showcase, full responsive matrices, TalkBack service traversal, pixel-diff acceptance, and release acceptance.


## Resumption context

Cycle 066 closed BLOCKED before implementation because the SDK emulator path and DISPLAY were not discovered from the default shell environment. Cycle 067 resumes the same bounded objective after finding the SDK in repository `.android-sdk`, AVD data under `~/.android/avd`, and a working X display at `:0`. Confirm use of `ANDROID_HOME=$PWD/.android-sdk`, `ANDROID_AVD_HOME=$HOME/.android/avd`, and `DISPLAY=:0` for emulator/device commands. Cycle 066 blocker evidence remains archived at `.codex/test-artifacts/066-tp2d-weather-marks-backdrops-partial2/`; this cycle stores new results only under its own evidence directory.
