# History — 138-theme-selection-appearance-surface

Status: Completed
Cycle ID: 138-theme-selection-appearance-surface
Roadmap item: R5.2A
Closed: 2026-10-06
Plan: .codex/history/plans/138-theme-selection-appearance-surface.md
Evidence: .codex/test-artifacts/138-theme-selection-appearance-surface/

## Outcome

Added a focused Appearance destination for all five catalog themes, with accessible single-choice state, immediate Activity-owner/resolver updates, return and Back restoration, and preserved Hourly/Daily window selection. Updated route regression coverage and presentation documentation.

## Verification

Unit tests, contract, repository check, ACTIVE workflow validation, diff check, focused Appearance/Daily/Home/sparse-Home instrumentation, and installed visual captures passed. The full API 37 connected suite ran 59 tests with three failures, all matching methods in cycle 137's six-failure baseline; exact results are retained in the cycle evidence.

## Limitations / not verified

Full connected instrumentation is not green: ManualLocationSearchFlowTest.actualActivityRouteShowsLoadingOrderedDisambiguatedResultsAndSelectsOnlyExplicitly, ProductionForecastCompositionFlowTest.selectedFailureClearsFixtureWeatherAndKeepsCandidateIdentity, and ProductionForecastContextTest.showsProvenanceTimesOriginRefreshPartialHorizonAndStatusAccessibly fail as in the cycle 137 baseline. Service-level TalkBack was not exercised. See .codex/test-artifacts/138-theme-selection-appearance-surface/verification.md.

## Follow-up

Proceed to R5.3 contrast preference. Preserve the recorded connected-suite baseline comparison and run service-level accessibility review when the planned accessibility audit scope reaches it.
