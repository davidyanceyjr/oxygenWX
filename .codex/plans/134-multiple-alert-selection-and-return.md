# Plan 134 — Multiple-alert selection and return

Status: Completed
Cycle ID: 134-multiple-alert-selection-and-return
Roadmap item: R4.4A
Created: 2026-10-06

## Objective

Allow a user to choose one alert from a supported result containing multiple
authoritative active alerts, inspect its source-supplied detail through the
existing alert detail surface, and return to the same Home page/window. The
selection flow preserves alert provenance and does not request forecast data.

## Production boundary

Extend the existing selected-location alert presentation boundary and
`OxygenWeatherApp` Home composition to expose a typed list of alert summaries
for multiple-alert results, provide an accessible selection surface, and open
the selected alert in the existing detail surface. Keep alert transport,
repository, and normalized alert semantics unchanged. Reuse the current
generation-scoped state publication so records from an obsolete location or
request cannot be selected.

## Functional invariants

- Only the current selected-location `OfficialAlertState.Supported` result can
  expose alert choices. Loading, confirmed no-alert, unsupported-region,
  failure, and stale-generation results expose no choices.
- Each choice and its detail refer to the same source alert. Preserve source
  event, severity when supplied, issuer, body, URL, and available times; do not
  infer severity, timing, warning text, or official status from forecast data.
- Compose receives typed presentation values and callbacks, never provider
  DTOs, repository/controller objects, or rendered strings used to recover
  alert identity. Alert ordering follows the authoritative result order unless
  the current contract explicitly supplies another ordering.
- Selection and detail navigation retain the existing Home pager and its
  page-owned hourly/daily window state. Visible return and Android Back return
  to the same state, with Back first closing detail and then selection if both
  are distinct states.
- Alert selection, opening/closing detail, and returning do not trigger
  forecast or alert refetches. Existing page navigation, alert summary states,
  and R4.4 single-alert behavior remain intact.
- Alert choice controls are named, keyboard/TalkBack accessible, at least
  48dp where applicable, and convey event/severity in text without color-only
  meaning. Long labels remain usable at the focused large-font condition.

## Implementation steps

1. Inspect the existing multiple-alert summary, detail mapper, generation
   gating, and in-composition detail route. Add or extend a presentation model
   that maps each alert in the selected supported result while preserving a
   stable in-result selection identity and only the fields needed by the UI.
   `OfficialAlert` currently has no stable alert ID: its fields are issuer,
   event/severity, times, body, optional source URL, and provenance. In
   particular, `sourceUrl` is optional and is a source action, not an identity
   contract. Use the pair `(OfficialAlertState.Supported.generation,
   inResultIndex)` as selection identity, where `inResultIndex` is the alert's
   zero-based position in that generation's authoritative result list. Carry
   the generation with each choice and resolve a selection only against the
   currently published supported result with the same generation. A new
   request/location generation invalidates all prior choices and selections.
   Do not deduplicate identical-looking records or use display text, URL,
   timestamps, or provenance as a substitute identity.
2. Publish the typed choices beside the existing summary/detail through the
   Activity boundary from the same generation-checked result. Clear choices
   when a new request begins or the selected location changes; do not create a
   parallel fetch or loosen current request arbitration.
3. Add an accessible multi-alert selection surface reachable from the
   existing count-only Now summary. Keep Home composed and block underlying
   interactions while it is visible. Selecting one choice opens that alert in
   the existing detail surface; the chosen detail remains bound to the
   selected request result.
4. Define and implement visible return and Android Back precedence for Home,
   multi-alert selection, and selected detail. Return to the same Home page
   and hourly/daily window. Preserve R4.4 source-opening behavior and safe
   HTTP(S) validation.
5. Add deterministic presentation and UI/navigation tests for two or more
   records, identity/detail correspondence, missing optional values,
   accessibility labels, generation/location changes, selection/detail/back
   sequence, and unchanged forecast/alert request counts.
6. Install and exercise the real Activity/controller/Compose path with a
   deterministic multi-alert transport response. Capture the selection and
   one selected detail at the compact baseline; verify return and unchanged
   request counts. Preserve logs and screenshots under the cycle evidence
   directory.

## Acceptance criteria

- A current supported result with at least two active alerts exposes a
  selection action and an accessible list of distinct source alerts; a
  selected row opens the matching detail content, including available
  issuer/source/body/time fields.
- Loading, no-alert, unsupported-region, failure, and superseded-location
  results cannot expose stale or selectable alert choices. Single-alert
  summary-to-detail behavior remains as implemented in R4.4.
- Selection and detail each have a visible named return action. Android Back
  dismisses detail to the selection list, then selection to the same Home
  page/window; Home page/window controls are not operable behind either
  surface. A subsequent Back follows ordinary Home behavior.
- Focused installed-flow assertions prove the selected alert identity and
  source fields, return sequence, Home state preservation, and no forecast or
  alert refetch caused by selection/open/return.
- Installed evidence exercises the real Activity/controller/Compose path at
  393 × 852 dp, font scale 1.0, LTR, default production theme, Effects Off;
  include at least two alerts with distinct names and one long supplied body.
  Record device/API/build and retain screenshots and logs under
  `.codex/test-artifacts/134-multiple-alert-selection-and-return/`.
- Run focused mapper and installed-flow tests, `python scripts/dev.py test`,
  `python scripts/dev.py contract`, `python scripts/dev.py check` when Android
  tooling is available, `python scripts/dev.py workflow`, and
  `git diff --check`. Record exact results and any unavailable verification.

## Verification and evidence

- Presentation tests map multiple alerts from the current supported state,
  preserve order and stable identity, preserve event/severity/source data, and
  omit unavailable optional values honestly. They reject choices for all
  non-supported states and stale generations.
- Installed tests use deterministic NWS transport responses containing at
  least two distinguishable alerts. Assert count summary opens selection,
  choosing each row maps to its matching detail, detail Back returns to
  selection, selection Back returns to the original Home page/window, and
  interactions cannot alter underlying Home state.
- Assert selection, detail, and return leave both forecast and alert request
  counters unchanged after the deterministic supported result is loaded.
- Capture the selection surface and selected detail through the production
  Activity/controller/Compose path at 393 × 852 dp, font scale 1.0, LTR,
  default production theme, Effects Off. Include a focused 360 × 640 dp,
  font-scale 1.3 check for reachable choice/return controls and wrapped labels.
  Preserve device/API/build details, screenshots, and command logs in the
  cycle evidence directory.
- RTL, cross-theme matrices, and service-level TalkBack traversal remain
  separately unverified boundaries unless explicitly run and recorded.

## Risks and assumptions

- R4.4 currently exposes a single typed detail value for singleton results,
  while the multiple-alert summary is count-only. The implementation should
  add a collection-oriented presentation boundary without sending normalized
  `OfficialAlert` values into Compose.
- The selection surface is intended to be a transient state within the
  existing Home composition, parallel to the detail route, so pager and
  forecast-window state remain alive. Existing location-search behavior must
  not be copied if it recreates or replaces Home state.
- Repository inspection confirms `OfficialAlert` in
  `app/src/main/java/com/oxygen/weather/data/WeatherModels.kt` has no dedicated
  stable ID. `NwsAlertProvider` reads NWS `@id` only into the optional safe
  `sourceUrl`; the normalized model does not retain a separate identifier.
  Therefore the required identity is the request-generation-scoped pair
  `(Supported.generation, inResultIndex)` described in implementation step 1.
  The index is valid only for that exact published result and authoritative
  list order; never persist it across generations or infer identity from
  display fields. If implementation discovers that published result lists can
  mutate without changing generation, stop and resolve that contract before
  relying on this identity.
- Source bodies and event names may be long or absent. Rows should remain
  scannable and detail should continue to use the existing scrollable,
  source-faithful presentation.
- NWS alert ordering is source-owned. Do not imply a severity ranking unless a
  separately approved semantic contract defines one.

## Out of scope

- Changes to NWS transport/decoding, alert repository semantics, or provider
  coverage.
- New alert sorting, filtering, grouping, prioritization, dismissal, or alert
  management/persistence.
- Forecast-derived warnings, new alert sources, background notifications, or
  changes to official-alert wording/meaning.
- Changes to R4.4's source URL safety policy or the global Home navigation
  contract.
- Full RTL, cross-theme, large-font matrix, or TalkBack service traversal;
  retain these as explicit later verification boundaries.
