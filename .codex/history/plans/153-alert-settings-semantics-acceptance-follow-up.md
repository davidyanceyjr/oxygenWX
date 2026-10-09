# Plan 153 — Alert and Settings semantics acceptance follow-up

Status: Completed
Cycle ID: 153-alert-settings-semantics-acceptance-follow-up
Roadmap item: R6.1A1
Created: 2026-10-08
Reviewed: 2026-10-08
Implementation difficulty: 4/10 — The work is concentrated in existing Settings/alert Compose routes and connected tests. It requires diagnosis across pager state, route state, and test synchronization, plus narrow instrumentation seams and focused installed-path checks. Production risk is low because runtime changes are conditional on demonstrated defects; validation is moderate due to Android instrumentation.

## Objective

Close only the R6.1A spoken-semantics acceptance gaps left by cycle 152: diagnose the two Settings-to-Home return failures, exercise saved-list Loading/Unavailable and missing About package metadata through the installed production routes, and recheck the alert and Settings contract.

Use diagnosis-first handling for the return failures. A failing assertion alone does not establish a product defect. Preserve runtime behavior unless a repeatable production-path observation shows that the app returns to the wrong Home page or otherwise violates the route contract. If the evidence instead identifies test setup/synchronization, make only a test correction supported by that evidence. If the result remains indeterminate after the bounded diagnosis, record the blocker without weakening the assertion or claiming acceptance.

## Verified context

- Settings route state and `leaveSettings` are in `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`; the return callback restores the page index captured when Settings opened.
- `SavedLocationCoordinator.restoreOnce()` publishes Loading before its saved-store read, then publishes Empty, Ready, or Unavailable. `MainActivity` currently constructs the production shared-preferences store directly. A test store factory can drive failure; a latch-controlled read can hold Loading without changing app behavior.
- `AboutSurface` reads app label and version from `PackageManager`, maps absent/blank values to visible “Unavailable,” and currently has no metadata override for instrumentation.
- Cycle 152 tested successful About metadata and saved-list Empty, but did not drive saved-list Loading/Unavailable or absent About metadata through the installed route.
- Cycle 152's two return failures occurred at the final Home page identity assertion after preceding route/state assertions. The recorded evidence does not determine whether the cause is runtime behavior or test setup/synchronization.
- Official alert summary/detail presentation and seven Settings destinations were implemented in cycle 152. This follow-up verifies their acceptance outcomes; it does not redesign their presentation.

## Production boundary

- Existing Settings routes and official-alert summary/detail semantics.
- Focused connected tests: `SettingsDataLocationDestinationsFlowTest`, `ThemeAppearanceApplicationFlowTest`, `SettingsLegalProductDestinationsFlowTest`, `ProductionOfficialAlertSummaryFlowTest`, and the production saved-location flow.
- Narrow test-only injection seams, if required, for saved-store outcomes and About package metadata. Test defaults must delegate to the current production behavior; no user-facing setting or runtime behavior changes.
- Evidence: `.codex/test-artifacts/153-alert-settings-semantics-acceptance-follow-up/`.
- Production changes are limited to a defect demonstrated by the installed route and must preserve existing route, preference, weather, and alert behavior.

## Functional invariants

- Settings destinations remain actions, not selected preferences; selection semantics apply only to persisted choices and active location.
- Loading, empty, unavailable, and ready saved-location states remain distinct and truthful. Missing app label/version remains explicitly unavailable, never fabricated.
- Preserve existing visible facts, roles, actions, navigation meaning, saved preferences, and weather/alert request behavior.
- Official alert distinctions, source ordering, source facts, and provenance remain unchanged. Forecast data cannot create alert semantics.
- Compose continues to consume presentation models rather than provider DTOs or repositories.

## Implementation steps

1. Reproduce both cycle-152 failures independently using the exact focused tests and record command, device/API, route, opening page, layout/font settings, and the final Home selector observation. Capture the visible selector label and, where accessible through the installed test path, the pager's settled page after leaving Settings. Repeat each focused route enough to distinguish deterministic app behavior from transient test synchronization.
2. Trace Settings entry, nested-destination return, `settingsOpeningPage`, and pager settling in the two test flows. Compare the route's captured opening page with the settled Home page. Classify each failure only when evidence supports one of: production route defect, test setup/expectation issue, or unresolved blocker. Do not edit product code or relax assertions before this diagnosis.
3. Add or reuse narrow test-only controls to drive saved-list Loading and Unavailable through the actual `MainActivity` → `OxygenWeatherApp` Locations route. Prefer a controlled/blocking saved-store read for Loading and a deterministic failed read for Unavailable. Add a similarly narrow About metadata override only if the installed route cannot otherwise exercise absent/blank app-label and version values. Defaults must continue using the existing shared-preferences store and PackageManager reads.
4. Assert production merged/unmerged semantics for saved-list Loading, Empty, Unavailable, and Ready as covered; specifically verify Loading and Unavailable are understandable without color and do not fabricate entries. Assert About label/version missing fallbacks are spoken as unavailable while present metadata continues to render normally. Verify route controls remain reachable.
5. Make only evidence-backed changes: a production route correction if step 2 establishes wrong settled-page behavior; a test-only correction if the installed app returns correctly and evidence isolates stale/assertion timing or setup; otherwise leave behavior/assertion intact and record the exact blocker. Do not mask a failure by broadening waits without proving the observed state is transient.
6. Recheck the seven Settings destination semantics and applicable persisted selected/unselected states, saved-location/action states, and official-alert summary/detail outcomes. Confirm passive Settings navigation does not change forecast/alert request counts or selected weather/location. Alert cases retain Checking, no alerts, single/multiple, unsupported, and failure distinctions; detail retains supplied source facts and omits absent optional facts.
7. Run focused JVM and connected checks, followed by the repository checks below when available. Write exact commands/results, observations, device/API, diagnosis, and unverified boundaries to `verification.md`; run `git diff --check` and inspect the plan-bounded diff.

## Acceptance criteria

- Each return failure is independently diagnosed with evidence identifying a production defect, test/setup issue, or reproducible unresolved blocker. Production code changes occur only for a demonstrated defect; test assertions change only for a demonstrated test defect. No result is inferred from the failure message alone.
- The saved-list Loading and Unavailable branches are exercised through the installed production route and have semantics assertions; Empty and Ready behavior remains covered. Status meaning is available without color and missing content is not invented.
- About missing label/version metadata is exercised through the installed production route. Missing values are visibly and semantically unavailable; supplied package metadata still renders as before.
- Seven Settings destination roles/actions and applicable selected/unselected and unavailable states remain verified. Official-alert summary/detail facts and distinctions remain verified on production routes.
- Request counts and selected forecast/location behavior remain unchanged during passive Settings navigation and return.
- Cycle closeout states whether R6.1A's full exit is met. Any blocked/unverified condition is explicit; R6.1A stays PLANNED unless all required acceptance conditions pass.

## Verification and evidence

Evidence path: `.codex/test-artifacts/153-alert-settings-semantics-acceptance-follow-up/`. Create `verification.md` with the state/test inventory, exact failure reproductions and diagnosis, route/pager observations, missing-state semantics observations, commands/results, device/API, and unverified boundaries. Retain focused diagnostics where useful.

Focused connected coverage:

- `SettingsDataLocationDestinationsFlowTest#destinationsShowExactSelectedIdentityUnavailableForecastAndReturnToOpeningPageWithoutOperations`.
- `ThemeAppearanceApplicationFlowTest#settingsRoutesPreserveOpeningPageAndUnitsRemapWithoutWeatherWork`.
- `SettingsLegalProductDestinationsFlowTest` destination flow plus new/extended About missing-metadata and saved-list Loading/Unavailable cases where practical.
- Production saved-location flow for controlled Loading and failed-read Unavailable states.
- `ProductionOfficialAlertSummaryFlowTest` for existing alert contract.

Focused JVM coverage should include any new pure helper logic; do not add redundant model tests for behavior exercised only at the route boundary.

Broader checks after focused acceptance:

- `python scripts/dev.py test`
- `python scripts/dev.py contract`
- `python scripts/dev.py workflow`
- `python scripts/dev.py check` when Android dependencies/device are available
- `git diff --check` and final diff review

Do not report an entire connected suite as passing when only focused methods ran. TalkBack service/manual traversal remains R6.5; compact/large-font and RTL matrices remain later roadmap slices.

## Risks and assumptions

- A Compose test can observe the previous selector semantics before a pager transition settles. This possibility must be proven by comparing observed route/pager state; it is not assumed to explain either failure.
- The saved-store injection belongs only to instrumentation setup and must not expose an app-level configuration path. A controlled read must be released during cleanup so instrumentation cannot hang.
- The About metadata override must be test-only and default to the existing PackageManager lookup. Avoid replacing the production metadata source or changing the product copy.
- If failures cannot be classified or a state cannot be reached within the existing route/test seams, record the smallest reproducible blocker and propose a separate bounded slice rather than broadening this one.

## Out of scope

- Compact/large-font screenshot matrices, RTL chronology matrix, reduced-motion and appearance invariance, TalkBack service/manual verification.
- New Settings destinations/content, navigation redesign, localization, or visual redesign.
- Provider, domain, repository, cache, location selection policy, alert selection/ranking, persistence, or weather request changes except test-only seams required for these route assertions.
- R6.2 and later roadmap work; no claim that R6.1A is complete without its acceptance evidence.
