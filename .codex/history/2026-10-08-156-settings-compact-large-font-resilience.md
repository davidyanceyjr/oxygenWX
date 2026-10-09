# History — 156-settings-compact-large-font-resilience

Status: Completed
Cycle ID: 156-settings-compact-large-font-resilience
Roadmap item: R6.2A
Closed: 2026-10-08
Plan: .codex/history/plans/156-settings-compact-large-font-resilience.md
Evidence: .codex/test-artifacts/156-settings-compact-large-font-resilience/

## Outcome

Completed installed R6.2A Settings compact/large-font review across seven destinations and two profiles. No critical layout defect was reproduced and no production change was warranted. The full 14-cell matrix, scroll/return evidence, and eight Appearance control actions are preserved under .codex/test-artifacts/156-settings-compact-large-font-resilience/.

## Verification

Passed: python scripts/dev.py build; python scripts/dev.py workflow; python scripts/dev.py contract; cycle validator with --self-test; git diff --check. The focused appearance instrumentation method was attempted twice but stopped at its initial missing '28 °C' fixture assertion before checking Appearance or operation counters; report preserved as instrumentation-failure.xml. Full details in verification.md.

## Limitations / not verified

Visual acceptance covers only 360x640dp, LTR, font scales 1.0 and 1.3, Atmospheric/Standard/Standard/Off/Metric, Demo Station. TalkBack traversal, RTL, other viewport/settings combinations are outside scope. Appearance weather/cache/alert operation-counter invariant remains unverified because the existing instrumentation method failed before those assertions.

## Follow-up

Recheck the appearance-flow operation-counter invariant after correcting or establishing the instrumentation fixture expected by ThemeAppearanceApplicationFlowTest.visibleWeatherFactSnapshot; do not infer that invariant from visual captures.
