# Plan 009 — Theme B shared monitor components

Status: Completed
Cycle ID: 009-theme-b-shared-monitor-components
Roadmap item: R0.6
Created: 2026-09-21
Revised: 2026-09-21
Revision: implementation-ready second revision

## Objective

Complete R0.6 by extracting the four shared Theme B monitor structures already
present in the Home candidate into a UI-local component boundary:

- `MonitorHeader` — neutral title plus supporting presentation text;
- `HomePageSelector` — named Now/Hourly/Daily/Details selector;
- `MonitorSection` — the existing effects-aware readable surface;
- `ForecastWindowControls` — explicit Earlier/Later actions.

The observable result is the unchanged four-page application using these
components at its real call sites. The extraction must preserve the current
weather values, page composition, dimensions, appearance resolution, semantics,
window behavior, pager ownership, and public application entry point.

`MonitorHeader` is intentionally neutral rather than a location-specific
component. Now passes location plus source/update freshness text; Hourly and
Daily pass the page name plus selected range; Details passes the page name plus
its existing inspection description. This generalizes the existing
`PageHeading` structure without inventing new presentation fields.

## Context-budget boundary

This is one bounded implementation slice under the repository rule that a
slice expected to exceed approximately 45% of the context window must be
split. The production boundary is limited to:

- one new file, `app/src/main/java/com/oxygen/weather/ui/MonitorComponents.kt`;
- the minimum call-site/import/removal edits in
  `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`;
- documentation and cycle evidence required to record the completed slice.

No new test dependency, Android test harness, presentation model, provider
contract, navigation destination, or page-specific visual redesign is allowed.
If implementation requires any of those, stop before expanding this cycle and
split the requirement into R0.6A, R0.6B, or the owning later roadmap slice.

## Production boundary

### New UI component file

Add `MonitorComponents.kt` in the existing `com.oxygen.weather.ui` package.
The components remain UI-local/internal; no new application-facing API is
introduced.

Implement these contracts without provider, repository, persistence, or raw
theme-id inputs:

```kotlin
@Composable
internal fun MonitorHeader(
    title: String,
    supporting: String,
    modifier: Modifier = Modifier,
)

@Composable
internal fun HomePageSelector(
    pageLabels: List<String>,
    selectedIndex: Int,
    appearance: ResolvedAppearance,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
)

@Composable
internal fun MonitorSection(
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)

@Composable
internal fun ForecastWindowControls(
    appearance: ResolvedAppearance,
    canEarlier: Boolean,
    canLater: Boolean,
    onEarlier: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
)
```

The exact implementation must preserve the current behavior:

- `MonitorHeader` retains the existing full-width column, headline/supporting
  typography, two-line supporting-text limit, ellipsis behavior, and Material
  resolved colors.
- `HomePageSelector` retains the existing horizontal row, appearance layout
  insets/gap, equal-weight text buttons, minimum control target, selected and
  inactive semantic colors, selected property, and content description format:
  `"<label> page, <position> of <count>"`.
- `MonitorSection` retains the existing `Surface` content color, Theme B
  surface/outline roles, resolved panel/outline opacity, border width, corner
  radius, and Effects Off opacity/static guarantees. It does not add or remove
  accessibility semantics.
- `ForecastWindowControls` retains the visible `Earlier` and `Later` labels,
  button semantics, enabled/disabled state, equal-weight layout, control gap,
  and minimum target size. It does not add arrow-only gestures or own window
  state.

The new file may import Compose layout/material/semantics APIs currently used
by the private implementations. It must not import anything from
`com.oxygen.weather.data`, a repository, an activity, or a provider adapter.

### Existing app call sites

Modify only `OxygenWeatherApp.kt` to:

1. Pass `HomePage.entries.map { it.label }` into `HomePageSelector`. Keep
   `HomePage`, `PagerState`, coroutine scope, `moveToPage`, Back handling, and
   pager state in `OxygenWeatherApp`; the selector only emits the selected
   index through `onSelect`.
2. Replace every `PageHeading` call with `MonitorHeader` using the exact
   current title and supporting strings.
3. Replace every `GlassPanel` call, including Now, Hourly, Daily, Details,
   compact fact, unavailable, and forecast-entry surfaces, with
   `MonitorSection` using the same modifier and content lambda.
4. Replace both `WindowControls` call sites with
   `ForecastWindowControls`, preserving the existing `selectedIndex`-based
   enabled values and callbacks.
5. Delete the now-unused private `PageTabs`, `PageHeading`, `WindowControls`,
   and `GlassPanel` implementations and their imports. Do not leave a private
   compatibility alias that would make the shared component boundary unclear.

Do not modify `OxygenWeatherApp(presentation, effects)`, `EffectsLevel`,
`HomePresentation`, `HomePage` ordering, page-specific forecast components,
`ResolvedAppearance`, activity launch selection, or any data/presentation
source.

## Functional invariants

- The page order remains `Now -> Hourly -> Daily -> Details`.
- `OxygenWeatherApp(presentation, effects)` remains source-compatible.
- The outer `HorizontalPager` remains the only global horizontal-swipe owner;
  the component extraction adds no pager or horizontal scrolling.
- Static page-selector labels and monitor surfaces do not advance pages. The
  selector advances only through its explicit callback.
- Android Back from Details/Daily/Hourly continues to move one global page
  toward Now; Back from Now remains host behavior.
- Hourly and Daily continue to change windows through visible Earlier/Later
  controls. No nested pager or hidden gesture is introduced.
- Existing Hourly date-jump controls remain unchanged and continue to target
  the first window containing each represented local date.
- Forecast entries, chronology, missing values, spoken summaries, units,
  provenance, source/update wording, and unavailable wording do not change.
- The UI remains presentation-only: no provider DTO, repository, persistence
  object, HTTP client, or raw theme identifier reaches a component.
- Theme B colors, typography, layout/shape values, opacity, and motion remain
  resolved by the existing `ResolvedAppearance`/Material boundary.
- Effects Off remains solid/opaque/static/complete, and Subtle retains the
  existing atmosphere, translucency, and animated navigation behavior.
- Important facts remain visible text; selector and Earlier/Later controls
  retain meaningful semantics and at least the existing 48dp target guidance.

## Implementation steps

1. **Record the pre-change baseline.** Create the cycle artifact directory and
   run from the repository root:

   ```sh
   python scripts/dev.py workflow
   python scripts/dev.py contract
   python scripts/dev.py test
   git diff --check
   ```

   Preserve output in the cycle evidence. Inspect the current private
   implementations and record their exact dimensions, semantics, and call
   sites before editing. Existing unrelated dirty worktree changes belong to
   the prior R0.5 cycle and must not be reset or rewritten.

2. **Implement `MonitorComponents.kt`.** Move the shared implementations with
   the contracts above. Preserve concrete values rather than tuning spacing,
   typography, color, shape, or content descriptions. Keep the component
   implementation free of page navigation and window-index mutation.

3. **Migrate `OxygenWeatherApp.kt`.** Replace the four private component call
   sites and remove their private definitions/imports. Review the resulting
   file to confirm it still contains exactly one `HorizontalPager(`, the same
   `HomePage` enum/order, the same Back handler, and the same page-specific
   content/data flow.

4. **Run focused automated checks.** Run:

   ```sh
   python scripts/dev.py contract
   python scripts/dev.py test
   ```

   The contract check must continue to report one outer pager, presentation-
   only Compose, Oxygen identity, and retired-UI exclusion. The existing JVM
   suite must pass unchanged, including presentation horizon/window/date-jump
   assertions, effects resolution, launch selection, data, and derived tests.

   This repository currently has no configured Compose `androidTest` source or
   Compose UI-test dependency. Do not add a test dependency or create a fake
   source-string test for these rendering wrappers in this slice. Installed
   hierarchy and interaction evidence below is the focused UI verification;
   the pure/domain regression suite remains the automated regression boundary.

5. **Update required documentation from the implemented result.** Make only
   these documentation changes, based on the actual code and evidence:

   - `docs/ARCHITECTURE.md`: extend the `ui/` section to name
     `MonitorHeader`, `HomePageSelector`, `MonitorSection`, and
     `ForecastWindowControls`; state that page/pager/window state remains in
     `OxygenWeatherApp` and components receive presentation text/models,
     `ResolvedAppearance`, and semantic callbacks only.
   - `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`: update the current-candidate
     scope so shared structural monitor components are recorded as implemented;
     retain that application of the Theme B system to each page remains later
     work. Clarify that the neutral header carries page identity and supporting
     presentation text, with Now's supporting text carrying source/update
     context. Do not change product or accessibility requirements.
   - `VERIFICATION.md`: append an R0.6 section only after verification, with
     exact commands, installed observations, evidence path, and unavailable
     RTL/TalkBack or environment boundaries. Do not rewrite prior cycle
     records.
   - `docs/ROADMAP.md`: at closure only, change R0.6 from `PLANNED` to `DONE`
     and add the resulting history-record reference. Do not mark it complete
     before installed and automated evidence are recorded.
   - Do not modify `docs/SPECIFICATION.md` or `docs/UI_DEVELOPMENT_WORKFLOW.md`;
     this slice implements their existing contracts and does not change the
     workflow or release scope.

6. **Verify the installed application.** When the Android SDK/emulator is
   available, install the current debug build and capture evidence under
   `.codex/test-artifacts/009-theme-b-shared-monitor-components/`. Use the
   established 360x640 compact viewport and record the device serial and
   font-scale changes. For both normal Subtle launch and debug Off launch:

   - capture Now, Hourly, Daily, and Details screenshots;
   - dump the UI hierarchy for each page;
   - verify the four named selector items, exactly one selected item, the
     expected `<label> page, <position> of 4` descriptions, and visible
     location/source/update or page/range header text;
   - verify `Earlier` and `Later` are visible with correct enabled state at
     first and last Hourly/Daily windows;
   - verify Hourly date jump, page-selector navigation, and Back from Details
     through Now; confirm the forecast-entry spoken summaries remain present;
   - verify the Off run has a solid/opaque surface and immediate transitions,
     while Subtle retains its existing effect behavior.

   Repeat the four pages in both effects states at font scale 1.3, inspecting
   critical text, selected state, controls, and scroll reachability. Restore
   the emulator font scale to its prior value. If RTL or service-level TalkBack
   traversal is not exercised, record it as unverified; do not claim it from
   screenshots or compilation.

7. **Run closure checks and inspect the final diff.** Run:

   ```sh
   python scripts/dev.py check
   git diff --check
   git status --short --branch
   ```

   `dev.py check` covers workflow validation, the source contract, unit tests,
   lint, and debug APK assembly. If the repository wrapper or Android
   environment fails, preserve the exact failure and run only the documented
   equivalent available check; do not repair Gradle, alter dependencies, or
   claim the unavailable boundary. Inspect every production, documentation,
   evidence, and cycle-record diff before closure.

8. **Close the cycle persistently.** After all required evidence exists, use
   `python scripts/codex_cycle.py close` with an outcome summary, exact
   verification, limitations, and follow-up. The close operation must create
   `.codex/history/2026-09-21-009-theme-b-shared-monitor-components.md`, set
   the plan to `Completed`, return `.codex/current.md` to `IDLE`, and leave the
   roadmap/history as the authoritative record. Do not close on compilation
   alone.

## Tests and verification boundary

The automated boundary is deliberately explicit:

- `python scripts/dev.py workflow` validates the active cycle before edits and
  before closure.
- `python scripts/dev.py contract` checks one outer pager, presentation-only
  Compose imports, application identity, and retired-UI exclusion.
- `python scripts/dev.py test` runs the unchanged deterministic JVM suite;
  no new data, derived, presentation, effects, or launch behavior is expected.
- `python scripts/dev.py check` is the broader closure gate and includes
  source contract, unit tests, lint, and debug APK assembly.
- Installed screenshots, hierarchy dumps, and interaction notes are required
  to verify the visual/semantic component objective; compilation is not a
  substitute for them.
- `git diff --check` and final diff inspection are required.

No new automated test source is required for this extraction because the
component functions are stateless Compose wrappers and the repository has no
Compose UI-test setup. Introducing one would expand dependencies and the
context boundary without testing weather or navigation meaning. If a pure
helper is introduced unexpectedly, test it only if it has a real deterministic
contract; do not introduce a helper solely to create a test.

## Acceptance criteria

The slice is complete only when all of the following are true:

- `MonitorComponents.kt` contains the four internal shared components with
  semantic presentation/callback contracts and no data-layer imports.
- `OxygenWeatherApp.kt` uses all four components; the old private structural
  implementations are removed; its public function signature and page state
  behavior are unchanged.
- The source contract still passes with exactly one outer pager and no
  provider/data imports in the UI package.
- The existing JVM test suite, lint, and debug APK assembly pass through the
  available repository check path, or exact unavailable boundaries are
  recorded.
- Installed compact Subtle/Off evidence covers all four pages, selector
  semantics, header text, window-control boundaries, date jump, Back, and
  representative forecast-entry summaries.
- Installed font-scale 1.3 evidence covers all four pages in both effects
  states with no new critical clipping, overlap, or unusable controls.
- Effects Off remains opaque/static/complete and Subtle remains behaviorally
  unchanged.
- `docs/ARCHITECTURE.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `VERIFICATION.md` reflect only observed implementation/evidence; R0.6 is
  marked DONE in `docs/ROADMAP.md` only at closure; the history record contains
  exact verification and limitations.
- No breaking changes, provider/data behavior, meteorological meaning,
  navigation destination, or adjacent roadmap slice is absorbed.

## Verification and evidence

Expected artifact directory:

`.codex/test-artifacts/009-theme-b-shared-monitor-components/`

Retain at minimum:

- baseline and closure command output;
- screenshots for compact Subtle/Off pages and exercised transitions;
- UI hierarchy XML for representative selector/header/control states in both
  effects modes;
- font-scale 1.3 screenshots/notes for all four pages in both effects modes;
- a short `verification-notes.md` mapping each acceptance criterion to
  evidence or an explicit unavailable limitation;
- device serial, viewport, font-scale restoration, and any environment
  failure details.

## Risks and assumptions

- The current private implementations are the accepted R0.5 visual baseline;
  extraction must preserve their concrete values and not perform polish.
- The neutral `MonitorHeader` is a structural primitive, not a new semantic
  data model. Its supporting text remains supplied by existing presentation
  strings.
- The existing debug launch extra `oxygen_effects_off` remains the only Off
  selection path for installed evidence; no setting or persistence is added.
- UI hierarchy evidence can confirm exposed semantics but cannot claim
  service-level TalkBack speech quality. That boundary must remain explicit.
- A dirty worktree is present from the completed R0.5 implementation. Do not
  reset, revert, or absorb those unrelated changes; inspect overlap carefully.

## Out of scope

- R0.6A metric tiles, hourly forecast tiles, and daily forecast rows.
- R0.6B Details source/freshness or trend/inspection components.
- R0.7–R0.10 page-specific Theme B redesign or composition changes.
- Any forecast value, condition mapping, unit conversion, provenance,
  freshness-state mapping, missing-data behavior, alert behavior, or derived
  meteorology change.
- New Compose UI-test dependencies, Android test infrastructure, or broad test
  harness work.
- New theme IDs, contrast modes, persisted appearance settings, Full effects,
  system motion policy, settings destinations, or layout presets.
- Provider, repository, cache, location, alert, networking, persistence, or
  application-state work.
- Changes to `docs/SPECIFICATION.md` or `docs/UI_DEVELOPMENT_WORKFLOW.md`.
