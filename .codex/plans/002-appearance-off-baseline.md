# Plan 002 — Effects Off baseline (revised)

Status: Completed
Cycle ID: 002-appearance-off-baseline
Roadmap item: R0.3
Created: 2026-09-20
Revised: 2026-09-20

## Objective

Complete the R0.3 Effects Off rendering slice against the real Android launch path. The independently observable outcome is a debug-installed Oxygen Weather app that can be launched in Effects Off, renders an opaque and decoration-free but complete Now → Hourly → Daily → Details Home, and preserves all existing weather meaning, controls, page semantics, and navigation behavior.

This is a rendering/configuration slice only. It does not turn Effects Off into a persisted user preference.

## Production boundary

The deterministic `WeatherBundle` fixture through `MainActivity`, the existing typed presentation mapper, and the existing Compose Home renderer. The change includes:

- an internal resolved-effects contract used by the existing `EffectsLevel` API;
- opaque/static Effects Off branches for the shared background, panels, outlines, and page navigation;
- a debug-only `MainActivity` intent override, `oxygen_effects_off`, for deterministic installed verification;
- focused JVM tests for resolved-effects and launch-selection behavior;
- only the documentation and persistent-cycle records required to describe and close this slice.

Keep `OxygenWeatherApp(effects: EffectsLevel = EffectsLevel.SUBTLE)` source-compatible. Keep the normal launch default Subtle. Do not move provider, repository, fixture, formatting, or navigation ownership into a new layer.

## Functional invariants

- Standard Home remains exactly Now → Hourly → Daily → Details.
- The outer Home `HorizontalPager` remains the only global horizontal-swipe owner. No nested pager or new horizontal scrolling is introduced.
- Hourly and Daily still change windows through visible controls; Hourly date jumps still select the first represented window for that local date.
- Earlier/Later, page tabs, Android Back, page identity, forecast entry order/count, semantics, source/update labels, derived/history grouping, and unavailable behavior retain their current meaning.
- The deterministic fixture and all canonical/presentation weather values remain unchanged. No data is refetched, reformatted for visual effect, padded, or substituted.
- Weather marks remain supplemental. Visible text and semantics remain sufficient to understand conditions without the atmosphere field.
- Effects Off has a solid background, fully opaque content surfaces, and a fully opaque outline treatment. It does not draw the gradient, atmospheric condition field, alpha-based panel translucency, or decorative motion.
- Effects Off navigation is immediate/static. Subtle retains the current animated navigation behavior; this is a presentation choice and not a semantic change.
- The debug launch extra is ignored unless the build is debug and the boolean is true. Release/non-debug behavior remains the normal Subtle default.
- No persisted preference, settings destination, theme/unit behavior, network behavior, or cache behavior is added.

## Implementation steps

1. Establish the source baseline.

   Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and the focused existing tests before changing source. Inspect the current `EffectsLevel`, `GlassPanel`, `AtmosphereBackground`, pager/tab callbacks, and `MainActivity` paths. Record the baseline commands in the cycle evidence directory.

2. Add a small resolved-effects boundary without changing the public call shape.

   Add a UI-local typed resolver/spec (for example, an internal `ResolvedEffects`) selected from `EffectsLevel`. Give OFF explicit values/flags for an opaque background, opaque panel fill, opaque outline, no atmosphere drawing, and immediate navigation. Keep SUBTLE mapped to the current behavior. Keep the resolver free of provider/presentation data and avoid scattered `if (effects == OFF)` decisions where one resolved value is sufficient.

3. Apply the resolver to every existing effect consumer.

   Route the root background/atmosphere, `GlassPanel` fill and outline, and page-tab/Back navigation through the resolved contract. In OFF, do not create the gradient/Canvas atmosphere branch and do not use translucent surface or outline colors. Preserve all panel shapes, content, labels, callbacks, semantics, and scrolling needed for large text. Do not change `WeatherMark` meaning or hide equivalent weather text.

4. Add the real debug launch selection.

   Extract a small pure launch-selection function/configuration seam that accepts `isDebug` and the intent boolean, then use it from `MainActivity` when calling `OxygenWeatherApp`. The real command must be able to select OFF with `--ez oxygen_effects_off true`; an absent/false extra and every non-debug build must select Subtle. Do not add a manifest permission, exported component, persisted value, or user-facing control. Keep the extra name stable and document it only as a verification hook.

5. Add focused automated tests.

   Add JVM tests under `app/src/test/java/com/oxygen/weather/ui/` or the smallest package that owns the pure seams. Cover:

   - OFF resolves to opaque background/surface/outline, no atmosphere, and immediate navigation;
   - SUBTLE retains atmosphere/translucency/animated-navigation behavior;
   - debug + true selects OFF, while debug + absent/false and non-debug + true select Subtle;
   - existing `HomePresentationTest` and `HistoricalSynthesisTest` continue to pass unchanged, demonstrating no weather/presentation rewrite.

   If the existing source contract is extended, assert the single outer pager and the absence of retired UI markers as before; do not replace installed semantics evidence with source-string assertions.

6. Verify the installed state through the real app.

   Build/install the debug APK using the repository entry point first. Launch the normal state and the OFF state using the real activity, including:

   ```sh
   adb shell am force-stop com.oxygen.weather
   adb shell am start -n com.oxygen.weather/.MainActivity --ez oxygen_effects_off true
   ```

   If the repository wrapper is unavailable for the same environmental reason recorded by cycle 001, use the available cached Gradle executable only as an equivalent verification path and record both the attempted and successful commands. Do not alter wrapper/configuration files as a workaround.

7. Exercise the complete OFF interaction matrix at the supported compact baseline.

   At 360×640 and normal font scale, capture Now, Hourly, Daily, and Details. Verify visible page identity, all current/source/update/forecast facts, Hourly date jumps and Earlier/Later, Daily Earlier/Later, page-tab navigation, and Android Back from Details through Now. Capture a hierarchy/semantics dump that retains page/control/forecast-entry meaning. Launch twice after settling and record that the OFF path has no time-dependent decorative animation; do not claim byte-identical screenshots when the fixture clock itself changes.

8. Stress the relevant accessibility/layout boundary.

   Repeat the OFF path at the project large-font condition used by cycle 001 (record the exact font-scale/device setting), inspecting Now, Hourly, Daily, and Details for critical clipping/overlap and usable controls. RTL is not a changed contract in this slice; record it as not exercised rather than implying verification. TalkBack speech/service traversal remains unverified unless actually run.

9. Apply required document and cycle-record updates only from observed results.

   Update `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` to state that the current candidate has an effective debug-selectable Effects Off rendering path while the persisted preference remains future work. Keep its semantic/effects contract unchanged. Do not edit `docs/SPECIFICATION.md`; this slice implements an existing contract and introduces no product-scope change.

   Keep R0.3 marked PLANNED in `docs/ROADMAP.md` until all acceptance evidence is complete. At closure, change only R0.3 to DONE and add its evidence/limitation reference; do not promote unrelated roadmap items. Update `VERIFICATION.md` with the exact commands and actual installed/test results, replacing stale claims only where this cycle supplies evidence. Close the cycle with the generated `.codex/history/<date>-002-appearance-off-baseline.md` record containing the same verified and unverified boundaries.

10. Run closure checks and inspect the diff.

    Run the focused tests, `python scripts/dev.py contract`, `python scripts/dev.py check` when the Android environment permits, `git diff --check`, and a final diff review. The history record must distinguish wrapper/environment failures, large-font observations, RTL status, and TalkBack status rather than converting any of them into unsupported claims.

## Acceptance criteria

- A debug APK launched through `com.oxygen.weather/.MainActivity` with `--ez oxygen_effects_off true` selects Effects Off deterministically.
- Normal launch, absent/false extra, and non-debug behavior remain Subtle; the existing `OxygenWeatherApp` default/API is preserved.
- In OFF, the root is opaque, shared panels and outlines are opaque, the atmosphere gradient/Canvas decoration is absent, and navigation is immediate/static.
- Now, Hourly, Daily, and Details retain all critical weather text, source/update context, derived/history separation, page identity, controls, and meaningful semantics.
- The one-pager/window/date-jump/Back contracts remain functional at the installed compact baseline. No new nested horizontal pager exists.
- The large-font inspection records the exact result and any defect; no critical content is declared complete unless the installed evidence supports it.
- Focused resolver/launch tests, existing deterministic domain/presentation tests, source contract, and applicable broader checks pass.
- Evidence exists under `.codex/test-artifacts/002-appearance-off-baseline/` and the final history record names every verification limitation.
- Required documentation reflects the implemented debug verification hook without claiming a persisted Effects preference or broader settings work.

## Verification and evidence

Run, in order as applicable:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py build
python scripts/dev.py check
git diff --check
```

For installed evidence, use the actual emulator/device and retain at least:

```text
.codex/test-artifacts/002-appearance-off-baseline/
  commands.txt
  focused-tests.txt
  contract-and-check.txt
  install-launch.txt
  off-now.png
  off-hourly.png
  off-hourly-later.png
  off-hourly-date-jump.png
  off-daily.png
  off-daily-later.png
  off-details.png
  off-large-font.png
  off-ui-hierarchy.xml
  verification-notes.md
```

The notes must include device/emulator, viewport, font scale, launch extras, normal-vs-OFF state, navigation/Back observations, exact command outcomes, and unverified boundaries. Screenshots are visual evidence only; pair them with the pure tests and hierarchy/interaction observations.

## Risks and assumptions

- `EffectsLevel.OFF` is currently an API seed, not a complete launch-selected mode. The resolver must centralize the missing guarantees without changing caller compatibility.
- `MainActivity` uses `ApplicationInfo.FLAG_DEBUGGABLE` for the runtime debug gate, avoiding a
  build-configuration dependency while the extracted selector retains deterministic JVM coverage.
- `Canvas` absence and opacity are implementation guarantees that need both focused source/logic coverage and installed inspection; a Compose preview is not acceptance evidence.
- The demo repository derives its anchor from the current clock. Static-effects verification must distinguish decorative stability from legitimate fixture timestamp changes.
- The existing wrapper may still fail to bootstrap as in cycle 001. This is a verification limitation, not permission to change build infrastructure inside R0.3.
- A large-font defect that requires changing layout/content ownership is a follow-up slice unless it is a direct consequence of the new OFF branch and can be fixed without expanding this boundary.
- Service-level TalkBack speech verification is not assumed unless its output is captured.

## Out of scope

- Persisted Off/Subtle/Full preference, DataStore, settings UI, or system reduced-motion preference integration.
- Oxygen/Paper/Terminal themes, high contrast, Simple layout, unit presets, or any appearance information architecture.
- Open-Meteo/MET Norway networking, repository/cache/location behavior, official alerts, or fixture/domain/presentation changes.
- New weather meanings, derived calculations, provider mappings, forecast horizons, or source/provenance semantics.
- General accessibility redesign, RTL implementation changes, TalkBack automation, or release signing/publication.
- Changing Gradle wrapper versions, dependency versions, emulator setup, or unrelated documentation.

## Context-window audit

This remains one roadmap slice, but its work crosses source changes, JVM tests, installed rendering, large-font inspection, and document/history closure. Treat the practical execution load as greater than 60% of one agent context when source excerpts, screenshots/hierarchy output, and command logs are included. Use the numbered steps as handoff boundaries and retain evidence in files; if implementation and installed verification cannot be completed in separate focused passes, break R0.3 into an implementation/test sub-cycle and an installed-evidence/documentation sub-cycle before proceeding. This plan should be broken down further rather than allowing one context to absorb all rendered output and closure edits.
