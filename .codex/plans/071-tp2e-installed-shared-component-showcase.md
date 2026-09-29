# Plan 071 — TP.2E-partial1 showcase host and Subtle captures

Status: Blocked
Cycle ID: 071-tp2e-installed-shared-component-showcase
Roadmap item: TP.2E-partial1
Created: 2026-09-27
Revised: 2026-09-28
Depends on: TP.2D PASS in cycle 070

**Difficulty: 6/10.** The production components and focused tests already
exist. The work is a new test-only composition, cross-family assertions, and
installed capture review within a fixed compact viewport.

**Context budget:** target at most 35% of a fresh context window; stop before
45%. This is the first of four dependent TP.2E slices. Keep the host, its
Subtle matrix, and its evidence within this boundary. Record a blocker and
close this slice if it needs a production contract change or cannot fit the
approved viewport; do not expand the scope.

## Objective

Add a deterministic instrumentation-only host that composes the six
owner-selected shared-component families, verify the supplied content and
interaction contracts, and capture one installed Subtle case for each of the
five themes. This slice delivers the reusable host and first effects matrix;
it does not close TP.2E.

## Production boundary

- Add only instrumentation test code under
  `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/`.
  Do not change production Kotlin, resources, resolver/catalog/tokens, data,
  presentation models, normal-app navigation, or app launch behavior.
- Build one host from the existing components in six visibly labeled groups:
  (1) `ProductionPageHeader` and `ProductionPageSelector`; (2)
  `ProductionCurrentHero` and a representative `ProductionMetricTile`;
  (3) `ProductionHourlyEntry`, `ProductionDailyRow`,
  `ProductionWindowControls`, and `ProductionHourlyDateSelector`; (4)
  `ProductionSourceFreshnessPanel` and
  `ProductionInspectionMetricGroup`; (5) `ProductionWeatherMark`; and (6)
  `ProductionBackdrop`, used for the host background and a visibly labeled
  sample area. Keep the sample decorative and do not attach weather meaning
  to its label.
- Use stable values from existing typed fixtures and the existing resolver.
  Do not create a second weather fixture set just for screenshots. The host
  must not call providers, repositories, application state, or Home navigation.
- Reuse existing assertions where they cover a component contract. The new
  test owns cross-family composition, one-source fixture consistency, whole
  host visibility, and evidence export; it must not copy all component-level
  regression coverage into a monolithic new test.
- If an approved six-family group cannot be composed through the existing
  public component APIs, record the missing API and stop. Production API,
  token, semantic, or component changes belong to a separately approved
  bounded correction slice.

## Functional invariants

- All five captures use the same typed fixture, visible labels, supplied facts,
  chronology, source/update facts, unavailable states, and callback meanings.
  Only resolved appearance varies by theme.
- The host passes `ResolvedTheme`, typed presentation values, caller strings,
  and semantic callbacks. It does not branch on a raw `WeatherThemeId` to
  change content.
- Page identity/selection, date selection, and Earlier/Later accessible names,
  selected/enabled states, target sizes, and callback meanings remain intact.
  Invoking an enabled action calls its callback exactly once; disabled actions
  call none.
- Caller-visible condition text communicates weather meaning. Marks and
  backdrops are decorative, expose no additional spoken weather meaning, and
  do not intercept a foreground control. Source/freshness facts remain
  separate from inspection facts. Missing values remain unavailable or
  omitted exactly as supplied.
- Weather values, chronology, provenance, and accessibility meaning do not
  change with theme.

## Installed condition and fit gate

Use the installed Android instrumentation host at **360 × 640 dp**, font scale
**1.0**, **LTR**, **Standard contrast**, and **Subtle effects**. At cycle start,
record the connected device/emulator, API/build identity, display pixel size,
density, calculated dp size, SDK/JDK/Gradle identity, and APK identity. Confirm
cycle 070's TP.2D PASS history and evidence.

The capture is one complete, scroll-free composite per theme. Every one of the
six labeled groups and all required fixture text must be simultaneously within
the host bounds and legible; no scrolling, clipping, hiding, text shrinking,
or split capture is acceptable. Use only layout arrangement and spacing in the
test host to fit the existing component content. Add host-bound assertions for
all six group tags. If the approved 360 × 640 dp baseline cannot meet this
contract with existing components, retain the measured failing capture and
bounds as evidence and close BLOCKED; do not change the viewport or weaken the
content.

Capture exactly five PNGs, one each for Atmospheric, Glass, Minimal OLED,
Instrument, and Terminal. Record actual viewport, font scale, direction,
contrast, theme, effects, device/API, display size/density, build/APK identity,
and capture filename in the manifest. A Compose preview or emulator-home image
does not count.

## Implementation steps

1. **Entry audit:** confirm cycle 070's PASS record and evidence; inventory the
   six component APIs, typed fixtures, relevant semantics/callback tests, and
   capture/export pattern. Save the inventory, reused-test map, and actual
   installed environment identity. If no compatible installed host exists,
   preserve the exact discovery output and close BLOCKED.
2. **Build the test host:** add one deterministic test-only showcase with six
   stable test tags and visible group labels. Keep the root at the required dp
   size. Do not add production code or duplicate existing fixture semantics.
3. **Add focused cross-family assertions:** for every theme, assert all six
   groups exist in the unmerged semantics tree and their measured bounds are
   inside the root. Assert the shared fixture's page names and selected state;
   current condition and honest unavailable value; hourly and daily visible
   values and chronology; source/update and inspection group identity;
   caller-visible condition text; and the named date/window controls. Exercise
   enabled page/date/window callbacks once and disabled window callbacks zero
   times. Assert applicable control bounds are at least 48 dp. Assert the
   decorative mark/backdrop have no weather content description and that a
   foreground test action still receives input. Compare semantic content
   across the five theme cases so appearance is the only changed input.
4. **Run the installed case:** install and execute the focused instrumentation
   test on the recorded device. Use the existing instrumentation capture
   pattern to write the five final root PNGs to the instrumentation app's
   external-files directory, then pull them into this cycle's evidence folder.
   In the test, decode each PNG and assert it is non-empty and has pixel
   dimensions matching the measured host size and device density. Do not infer
   360 × 640 pixels from a 360 × 640 dp host.
5. **Review and regression:** inspect all five installed images for all six
   groups, legibility, clipping, and stable supplied content. Reuse and run
   focused `ProductionSharedComponentsTest`,
   `ProductionForecastComponentsTest`, `ProductionDetailsComponentsTest`,
   `ProductionWeatherMarkTest`, the applicable `ProductionBackdrop*Test`
   classes, and JVM `ThemeResolverTest` / `ProductionWeatherVisualsTest`.
   The new host test adds only cross-family integration assertions and capture
   validity. Preserve exact commands, exit codes, test counts, instrumentation
   result XML/log, and final image hashes. Do not diagnose a source-only or
   preview render as installed visual success.
6. **Close this partial:** inspect the final diff and evidence; update this
   plan, `.codex/current.md`, and the TP.2E source entry in
   `docs/theme-pack-roadmap.md`; write a cycle 071 history record with PASS or
   BLOCKED, exact verification performed, evidence links, and unverified
   boundaries. Set the cycle to IDLE only after the history/evidence and
   roadmap disposition are consistent. If PASS, identify partial2 as the sole
   next eligible slice. Do not claim TP.2E complete.

## Acceptance criteria

PASS only when all of the following are true:

- The test-only host contains the six named component families and composes
  without production changes.
- Cross-family assertions pass for all five themes with identical supplied
  meaning, availability, hierarchy, labels, chronology, and callbacks.
- The host remains scroll-free and all six complete groups fit within the
  approved installed viewport with no clipping or reduced content.
- Exactly five valid installed Subtle PNGs and a complete actual-condition
  manifest are retained and visually reviewed.
- Focused installed/JVM results, final diff review, and cycle history state the
  actual outcomes and all unverified boundaries.

If any entry condition, fit requirement, semantic assertion, or required
installed gate fails or is unavailable, preserve the exact failure and close
this partial BLOCKED. Do not retry by broadening the viewport, altering facts,
or adding a production fix. TP.2E remains open and dependent slices do not
start.

## Verification and evidence

Evidence path: `.codex/test-artifacts/071-tp2e-installed-shared-component-showcase/`.
Retain `environment.md`, `component-and-test-inventory.md`, focused test
outputs, instrumentation XML/log, exactly five final PNGs, `manifest.md`,
`visual-review.md`, image hashes, diff review, and explicit limitations. Keep
failed/blocked initial captures when they explain the disposition; mark them
as non-final.

## Risks and assumptions

- Cycle 070 is the completed prerequisite; its history and evidence must
  confirm TP.2D PASS.
- Existing component test fixtures provide the content needed by all six
  groups. Keep any unavailable input unavailable; do not invent a replacement.
- The six-family composite can fit the fixed installed viewport through test
  host arrangement alone. This is a measured entry/acceptance gate, not an
  assumed result.
- A compatible installed device/emulator and capture export path are available
  at execution time. If not, close BLOCKED with discovery evidence.

## Cycle 071 disposition — BLOCKED

The test-only host and installed Atmospheric diagnostic were produced, but the
approved compact fit gate failed after an allowed host-only reflow. At 360 x
640 dp the current-condition group measured 448 dp tall; the forecast group
reached the bottom boundary at y=636 dp, and the source/inspection, mark, and
backdrop groups measured zero height at y=636 dp. The installed screenshot,
raw group coordinates, result XML, logs, hashes, and exact conditions are
preserved under `.codex/test-artifacts/071-tp2e-installed-shared-component-showcase/`.

No final five-theme Subtle matrix was captured, and later cross-theme callback
and regression gates were not run. No production source or resources changed.
TP.2E-partial2 and later slices remain ineligible until a deliberate roadmap
update defines a new bounded outcome for this fit boundary.

## Out of scope

Normal Home composition/navigation, app state, providers/repositories/network,
weather or presentation semantics, units, provenance, missing-data policy,
derived values, alerts, persisted settings, resolver/catalog/token changes,
new theme identities, public API redesign, broad restyling, and any production
component change.

Effects Off captures; ten-case cross-effects comparison; any production
correction; repository-wide `test`, `build`, `contract`, `workflow`, and
`check`; page composition or pixel parity; Full effects; large-font, RTL, or
High contrast matrix; TalkBack service traversal; provider/fetch behavior;
TP.2E completion; TP.3 acceptance; release readiness.
