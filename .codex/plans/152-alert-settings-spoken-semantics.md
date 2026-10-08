# Plan 152 — Alert and Settings spoken semantics

Status: Completed
Cycle ID: 152-alert-settings-spoken-semantics
Roadmap item: R6.1A
Created: 2026-10-08
Reviewed: 2026-10-08

## Objective and observable outcome

Expose concise, accurate spoken semantics for the official-alert summary and
detail paths and for the seven destinations reachable from the Settings list:
Appearance, Units, Locations, Data Sources, Privacy, Open Source Licenses, and
About. Users can identify each destination, understand actual selected and
unavailable states, and understand status without depending on color. Alert
semantics preserve the authoritative lookup state and source-supplied facts.

The observable outcome is a focused Compose semantics test run through the
production `OxygenWeatherApp` destinations, backed by deterministic presentation
tests. Visible wording, values, controls, navigation, persistence, and weather
or alert behavior remain unchanged.

## Verified implementation context

- Settings is implemented in `ui/OxygenWeatherApp.kt`; the seven entries on
  `SettingsSurface` are the destinations listed above. They are navigation
  buttons, not persisted selection choices. Do not add a fabricated selected
  state to these entries.
- Actual selection applies to the persisted choices on Appearance (theme,
  contrast, effects, and layout), Units (Metric/US/UK), and to the active
  saved location. Existing choice rows set Compose `selected` and include
  selected/not-selected text in their descriptions. Verify that coverage
  through production semantics rather than rewriting it without a demonstrated
  gap.
- Existing explicit unavailable/status content includes selected/saved
  Locations states, forecast/alert source detail availability, Privacy policy
  availability, missing font-license content, About package metadata fallback,
  and location action results. Several are visible text only today; warning
  text uses warning color, so tests must establish its meaning from text and
  semantics, not color.
- `OfficialAlertSummaryPresentation` already distinguishes Checking,
  NoActiveAlerts, SingleAlert, MultipleAlerts, CoverageUnavailable, and Failure.
  The summary is already placed in a button or text semantic on Now. Do not
  merge, rank, select, or infer alerts in this slice.
- `OfficialAlertDetailPresentation` carries issuer and event as required
  strings and severity, effective/expiry text, and other source fields as
  nullable values. Detail currently renders separate visible field labels and
  values, with no concise aggregate spoken summary. `mapChoices` preserves
  authoritative source order; a multiple-alert summary is count-only.
- Existing installed-flow tests cover Settings route content and operations
  (`SettingsDataLocationDestinationsFlowTest`,
  `SettingsLegalProductDestinationsFlowTest`) and official alert rendering
  (`ProductionOfficialAlertSummaryFlowTest`). Reuse or extend those production
  paths; do not substitute isolated mock-only UI for acceptance evidence.

## Production boundary

- `app/src/main/java/com/oxygen/weather/presentation/OfficialAlertSummaryPresentation.kt`
  and `OfficialAlertDetailPresentation.kt`: add or refine concise semantics
  only where the existing typed presentation does not already express the
  required source facts and lookup states.
- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` and narrowly
  required presentation/component files: expose meaningful labels and state
  descriptions for the seven Settings destinations and their existing state
  controls through Compose semantics.
- Focused deterministic tests under `app/src/test/` and real production-path
  Compose semantics tests under `app/src/androidTest/`.
- Evidence directory: `.codex/test-artifacts/152-alert-settings-spoken-semantics/`.

No provider, domain, repository, alert selection policy, persistence, or
navigation changes are in scope.

## Spoken contract to implement and verify

### Settings destinations and states

Each of the seven Settings list entries exposes its existing visible title and
summary as one actionable destination, without redundant parent/child speech.
The destination entries themselves have no selected state. On their
destinations:

- Existing persisted choice rows expose their choice name and explicit
  selected/not-selected state. Assert Compose `selected` where the row is a
  selection control; the state must also be understandable from a spoken
  label/state description, not its dot or color.
- Locations identify selected-location loading, no selection, unavailable,
  and selected identity; saved-place rows identify the active place as
  selected. Saved-list loading, empty, unavailable, and ready states remain
  distinguishable. Action result/error text is spoken as status.
- Data Sources distinguishes unavailable forecast details from present source
  details and exposes official-alert source details only when supplied.
- Privacy announces that policy content is unavailable.
- Open Source Licenses identifies available license entries and unavailable
  license content; the scope notice remains available to assistive technology.
- About identifies the app name and version, and says unavailable only for a
  missing value; it must not speak a fabricated value.
- Appearance and Units expose the chosen persisted options and existing
  save/error state, if present. No new persistence or failure behavior is
  introduced.

Do not add an invented global unavailable state to destinations that have no
such state. Use the current typed/presentation state and visible content as the
source of truth. A status may remain in visible text and also have a concise
state description, but should not be announced twice through duplicate merged
parent/child nodes.

### Official alerts

- Summary semantics retain distinct outcomes: “Checking for official alerts”;
  “No active official alerts”; one alert with event and supplied severity;
  multiple-alert count without suggesting a selected alert; unsupported
  coverage; and the controller-provided failure status.
- Detail semantics identify event and issuer and include severity, effective
  time, and expiry time only when the source supplied them. Missing optional
  fields are omitted when the visible detail omits them; never fill them with
  placeholder severity, issuer, or time.
- If an aggregate detail summary is needed, construct it from the typed detail
  presentation and merge the detail fields once. Preserve the visible field
  labels, link action, and reading order without duplicate speech. Keep
  description/instructions and source text reachable.
- Multiple-alert choices preserve source order. They remain actions to open
  individual alerts, not selected alerts until a user opens one.

## Functional invariants

- Official alert meaning and provenance come only from authoritative alert
  state and source-supplied facts; forecast content cannot create alert
  semantics.
- Preserve Checking, supported no-alert, unsupported-region, and failure
  distinctions, source order, and count-only multiple-alert summaries.
- Missing source facts remain omitted/unavailable according to the existing
  visible presentation; no invented value or implied certainty is introduced.
- Settings semantics describe existing destinations and state; destination
  identity is not confused with a selected persisted option.
- Important status and selection meaning is available without color or
  decorative glyphs. Keep useful visible labels and current interactive
  semantics; avoid duplicate parent/child announcements.
- Compose continues to receive typed presentation data, never provider DTOs or
  repositories. No weather requests, alert requests, navigation, preference
  persistence, or meteorological meaning change as a side effect.
- Interactive targets and appearance behavior remain as implemented; this is
  not a visual redesign or viewport-resilience slice.

## Implementation steps

1. Inspect the production Settings routes, typed state inputs, existing choice
   row semantics, alert summary/detail semantics, and the three existing
   installed-flow test classes. Record a destination/state inventory and the
   current merged and unmerged semantics outcomes in the cycle evidence.
2. Add focused before-change assertions or record exact existing outcomes for
   the alert summary states and each destination. Identify actual gaps rather
   than changing copy or semantics already satisfying the contract.
3. Define only the missing typed spoken labels/state descriptions. Keep
   optional alert fields nullable and Settings unavailable states tied to
   existing state. Do not add a general Settings model layer unless inspection
   demonstrates that a typed boundary is needed.
4. Wire missing semantics into existing production Compose routes. Use
   merged-node inspection to ensure destination/status text is not lost or
   spoken twice, and that clickable choice controls retain role, selected
   state, and action.
5. Add deterministic JVM assertions for all six alert summary states, present
   and missing detail severity/effective/expiry facts, and any new Settings
   presentation helper. Add connected Compose assertions for all seven
   destinations, applicable selection/unavailability/status cases, alert
   summary/detail states, source order, and lack of misleading duplicate or
   hidden essential speech.
6. Run the focused tests, then broader checks below. Record exact commands,
   results, connected device/API, semantics-tree observations, and limitations
   in `verification.md`. Run `git diff --check` and inspect the plan-bounded
   final diff. Do not claim TalkBack service/manual traversal.

## Acceptance criteria

- Each of the seven production Settings destinations exposes its identity and
  actionable purpose. No destination is falsely represented as a selected
  preference.
- Appearance and Units persisted choices, and active saved location, expose
  selected/not-selected state in semantics and in understandable spoken
  wording. Tests verify each applicable choice family and the active location;
  unrelated values do not need an exhaustive preference cross-product.
- Applicable unavailable and status cases are distinguishable by text and
  semantics without relying on color: at minimum selected/saved-location
  unavailable, forecast-source unavailable, privacy unavailable, license
  content unavailable, About missing metadata fallback, and a location action
  status/error. Empty and loading location states remain distinct.
- Alert summary tests cover Checking, no alerts, one alert with and without
  severity, multiple alerts with no implied selection, unsupported coverage,
  and failure. Spoken meanings agree with `OfficialAlertSummaryPresentation`.
- Alert detail exposes event/issuer and supplied severity/effective/expiry
  facts; omitted nullable facts are neither announced nor represented by
  placeholder values. Description/instructions/source and return/source
  controls remain reachable.
- Real Compose semantics assertions cover the production alert path and all
  seven Settings destinations, retain control roles/selected state, and show
  no duplicate parent/child speech that obscures the facts.
- Existing visible labels/values, alert and weather meaning, routes, controls,
  saved preferences, and request behavior remain unchanged.

## Verification and evidence

Evidence path: `.codex/test-artifacts/152-alert-settings-spoken-semantics/`.
Create `verification.md` containing the destination/state inventory, expected
and observed spoken outcomes, relevant merged/unmerged semantics excerpts,
exact commands and results, emulator/device and API level, and unverified
boundaries. Preserve focused test output or diagnostics there when useful.

Focused JVM tests:

- `OfficialAlertSummaryMapperTest` — all six lookup outcomes and single-alert
  severity present/absent.
- `OfficialAlertDetailMapperTest` — supplied and absent severity/time facts,
  and multiple-alert source ordering/count behavior.
- Any new Settings presentation helper tests for exact labels and honest
  missing/unavailable outcomes.

Focused connected tests (reuse/extend existing classes where practical):

- `ProductionOfficialAlertSummaryFlowTest` — production summary and detail
  path, missing/present facts, multiple alerts and state distinctions.
- `SettingsDataLocationDestinationsFlowTest` — Locations and Data Sources,
  selected/unavailable/status semantics.
- `SettingsLegalProductDestinationsFlowTest` — Privacy, Open Source Licenses,
  and About unavailable/available content semantics.
- Add or extend a focused Appearance/Units route test if existing connected
  coverage does not assert production choice-role and selected semantics. At
  least one selected and one unselected choice per family; do not build an
  unrelated visual matrix.

Broader checks after focused acceptance:

- `python scripts/dev.py test`
- `python scripts/dev.py contract`
- `python scripts/dev.py workflow`
- `python scripts/dev.py check` when Android dependencies and device are
  available
- `git diff --check` and final diff review

This is a semantics slice: no screenshot matrix is required. Large-font,
compact viewport, RTL, appearance/effects matrix, and TalkBack service/manual
verification belong to R6.2, R6.2A, R6.3, R6.4/R6.4A, and R6.5 respectively.
Connected tests must use the actual installed app path; a preview is not
acceptance evidence. Record broader or environmental failures exactly and do
not attribute unrelated failures to this cycle without evidence.

## Risks and assumptions

- The seven destinations have no navigation selection model today. This plan
  treats their identity as a destination label and reserves selected semantics
  for actual persisted choices and active location. That follows current
  implementation and the R6.1A requirement without adding state.
- Some unavailable states are currently visible text without explicit
  Compose `stateDescription`; tests may show visible text alone already gives
  a sufficient spoken outcome. Add explicit semantics only where the actual
  production tree or assertions expose a gap, avoiding duplicate speech.
- Compose's merged tree can combine clickable labels and descendants
  differently across controls. Assert roles and selection in the appropriate
  tree and inspect both merged/unmerged forms when diagnosing hidden or
  repeated facts.
- Detail issuer/event are non-null strings in the presentation model; validate
  current provider normalization and test fixtures before treating blank
  strings as valid source facts. Do not change provider/domain contracts here.
- Connected instrumentation has had unrelated failures in prior cycles. A
  focused passing result establishes only its stated path; broader failures
  and TalkBack manual/service traversal remain explicit limitations.

## Out of scope

- Home Now/Hourly/Daily/Details semantics completed in R6.1.
- Compact/large-font screenshots, RTL verification, theme/layout combinations,
  reduced-motion invariance, and accessibility evidence closure (R6.2 through
  R6.5).
- TalkBack service/manual traversal, localization, visual redesign, new
  destination content, or changed destination navigation.
- Changes to alert providers, selection/ranking/merging, forecast/provider
  data, domain/repository/cache, persistence, or weather request behavior.
