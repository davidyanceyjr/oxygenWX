# Plan 075 — TP.2E per-family installed showcase pages

Status: PASS
Cycle ID: 075-tp2e-per-family-showcase-pages
Roadmap item: TP.2E-per-family-pages
Created: 2026-09-28
Revised from cycle 071 after the owner directed each family to use its own showcase screen.

**Difficulty: 6/10.** The components and component-level contracts already exist. The bounded work replaces the unfit composite with six test-only screens, cross-theme fixture comparisons, 30 installed Subtle captures, and the required visual/evidence review.

**Context budget:** target at most 35% of a fresh context window; stop before 45%. No Home/product page is added. Keep this cycle to instrumentation code, the roadmap/plan/history, and cycle evidence.

## Objective

Create six independent, scroll-free instrumentation showcase screens, one for each existing component family, and capture each screen under all five built-in themes at the approved compact baseline. All screens reuse the same typed values, labels, chronology, availability, provenance, and callbacks; only the resolved theme varies. The expected final Subtle set contains 30 installed PNGs (six screens × five themes).

The screens are:

1. **Page identity:** `ProductionPageHeader` and `ProductionPageSelector`.
2. **Current conditions:** `ProductionCurrentHero` and one representative `ProductionMetricTile`.
3. **Forecast windows:** `ProductionHourlyEntry`, `ProductionDailyRow`, `ProductionWindowControls`, and `ProductionHourlyDateSelector`.
4. **Source and inspection:** `ProductionSourceFreshnessPanel` and `ProductionInspectionMetricGroup`.
5. **Weather mark:** `ProductionWeatherMark` beside matching caller-visible condition text from an existing typed fixture.
6. **Backdrop:** `ProductionBackdrop` as the full-screen field with a labeled decorative sample and tappable foreground action.

## Production boundary

- Add/change only instrumentation test code under `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/`, this plan/current-cycle record, the TP.2E entries in `docs/theme-pack-roadmap.md`, and cycle-specific evidence.
- Do not change production Kotlin, resources, resolver/catalog/tokens, data, presentation models, standard Home navigation, launch behavior, or provider state.
- Reuse the exact existing fixture values in `ProductionSharedComponentsTest`, `ProductionForecastComponentsTest`, and `ProductionDetailsComponentsTest`; expose fixture values within `androidTest` only if needed to share them.
- The six showcase screens are harness states, not app destinations. Standard Home remains Now → Hourly → Daily → Details.

## Functional invariants

- All 30 captures use identical supplied fixture content and callbacks for a given screen; only resolved appearance varies by theme.
- The host passes `ResolvedTheme`, typed presentation values, caller strings, and semantic callbacks. It never branches on raw `WeatherThemeId` to change content.
- Every screen uses the actual 360 × 640 dp viewport at font scale 1.0, LTR, Standard contrast, Subtle effects. It is scroll-free; all visible group bounds and required content fit within its root.
- Page identity, date selection, and Earlier/Later accessible names, selected/enabled states, 48 dp target sizes, and callback meanings remain intact. Enabled page/date/window actions call their callback once; the disabled window action calls none.
- Current condition and unavailable values remain as supplied. Hourly and daily facts preserve caller order. Source/update facts remain separate from inspection facts.
- Weather marks and the backdrop are decorative and do not add spoken weather meaning or block foreground input. The mark screen uses caller-visible text matching the existing typed condition identity.
- Theme changes cannot alter supplied text/semantics, chronology, provenance, or availability.

## Installed condition and entry gate

- Confirm cycle 071's BLOCKED history/evidence and the owner-directed per-family revision in the roadmap.
- Record connected device/emulator, API/build identity, display pixel size/density, calculated dp size, font scale/direction, SDK/JDK/Gradle identity, and APK identity.
- Use the installed instrumentation host at 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Subtle effects.
- Each page must fit by itself through host composition/spacing only. Do not broaden the viewport, scroll, hide content, truncate required facts, shrink text, or split one page into multiple images.
- A failing page fit or semantic contract closes this cycle BLOCKED. Preserve the failing capture and measured bounds; do not add a production fix or weaken the page content.

## Implementation steps

1. **Entry audit:** confirm cycle 071's BLOCKED record, current component APIs, fixture reuse points, existing semantics/callback tests, and installed capture/export pattern. Save the actual environment identity and source/test inventory.
2. **Build independent screens:** implement one screen per named family. Use one active screen at a time under the fixed root; do not put all six groups in one vertically stacked composite. Give each screen a stable tag and visible family label.
3. **Add focused integration assertions:** for each screen/theme, assert the page/group tag and all required fixture text exist and have measured bounds inside the root; preserve chronology and semantic identity. Assert page/date/window selected and enabled states, target sizes, enabled callbacks once, disabled callback zero, decorative no-description behavior, and backdrop foreground input. Compare each screen's semantic text snapshot across the five themes.
4. **Run installed captures:** install and execute the focused instrumentation test on the recorded device. Capture one root PNG for every screen/theme pair. Decode each file and assert non-empty dimensions equal the measured root pixels at the device density. Export the 30 images and a manifest with actual conditions, theme, screen, device/API, display/density, build/APK identity, dimensions, filename, and SHA-256.
5. **Review and regress:** inspect all 30 installed captures for legibility, complete visible content, clipping, and stable supplied meaning. Run the focused existing `ProductionSharedComponentsTest`, `ProductionForecastComponentsTest`, `ProductionDetailsComponentsTest`, `ProductionWeatherMarkTest`, applicable `ProductionBackdrop*Test` classes, and JVM `ThemeResolverTest` / `ProductionWeatherVisualsTest`. Preserve exact commands, exit codes, test counts, result XML/logs, capture hashes, and limitations.
6. **Close this partial:** inspect the final diff/evidence; update this plan, `.codex/current.md`, and the TP.2E source entry; write a cycle 075 history record with PASS or BLOCKED. Set the cycle IDLE only after consistent history/evidence/roadmap disposition. On PASS, identify the next eligible work as a newly bounded Effects Off per-family page matrix. Do not claim TP.2E complete.

## Acceptance criteria

PASS only if all six screens fit individually without scrolling/clipping for every theme; all five themes preserve identical supplied content and semantic/callback contracts per screen; exactly 30 valid installed Subtle PNGs and a complete manifest are retained and visually reviewed; focused existing tests and final diff/workflow evidence record actual outcomes and unverified boundaries.

If any page fails its installed fit/semantic gate or an installed device becomes unavailable, preserve exact evidence and close BLOCKED. Do not reuse cycle 071's failed composite as a passing capture or treat compilation as visual acceptance.

## Verification and evidence

Evidence path: `.codex/test-artifacts/075-tp2e-per-family-showcase-pages/`. Retain `environment.md`, `component-and-test-inventory.md`, focused test outputs, instrumentation XML/log, the 30 final PNGs (or mark any partial set non-final on BLOCKED), `manifest.md`, `visual-review.md`, hashes, diff review, and explicit limitations.

## Risks and assumptions

- Every individual family screen fits the approved viewport with the existing typed fixtures and component APIs. This is a measured gate.
- Cycle 071's missing-data behavior must remain honest; do not substitute a more convenient current value.
- The API 37 emulator and MediaStore/external-files capture path remain available.
- The preplanned composite-dependent cycles 072–074 are superseded by this owner-directed roadmap revision and must not start.

## Cycle 075 result

PASS. The focused installed showcase passed for all six screens across all five themes at the required 360 × 640 dp, font scale 1.0, LTR, Standard/Subtle baseline. Thirty installed PNGs decode at 360 × 640 px and match the runtime manifest hashes. Visual review found no clipping, truncation, or missing required facts; the Current hero's supplied `Unavailable` value wraps across two visible lines. The focused components package passed 16 instrumentation tests, including the showcase and applicable backdrop/Effects Off tests. The focused JVM resolver and weather visual suites passed 10 tests total.

The first instrumentation run stopped at an incorrect exact-text assertion: the current hero exposes the supplied apparent-temperature string with its `Feels` label. The assertion was corrected to the existing rendered text and the full 30-case run passed; no production API or content changed. An initial comma-separated runner filter ran only its first named class; the regression run was repeated using the components package filter, whose result XML confirms all eight component classes ran.

Out-of-scope boundaries remain as listed below. This partial does not close TP.2E; plan the Effects Off per-family page matrix as a new bounded cycle.

## Out of scope

Effects Off captures; ten-case/cross-effects comparison; production correction; repository-wide `test`, `build`, `contract`, or `check`; normal Home composition/navigation; live providers/application state; Full effects; large-font, RTL, or High-contrast matrix; TalkBack service traversal; page pixel parity; TP.2E/TP.3/release acceptance.
