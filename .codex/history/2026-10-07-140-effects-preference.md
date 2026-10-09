# History — 140-effects-preference

Status: Completed
Cycle ID: 140-effects-preference
Roadmap item: R5.4
Closed: 2026-10-07
Plan: .codex/history/plans/140-effects-preference.md
Evidence: .codex/test-artifacts/140-effects-preference/

## Outcome

Implemented persisted Off/Subtle/Full effects preference, Activity restoration, Appearance controls, and direct three-level resolver/pager wiring.

## Verification

python scripts/dev.py test, contract, and check passed; focused connected ThemeAppearanceApplicationFlowTest#effectsAreSelectablePersistedAndDoNotTouchWeatherOperations passed 1/1; installed compact Atmospheric screenshots for Appearance and Now across Off/Subtle/Full are in .codex/test-artifacts/140-effects-preference/captures; request counts remained zero; git diff --check passed.

## Limitations / not verified

The extra full connected Android suite was manually stopped while progressing through unrelated Home/alert tests; no full-suite result is claimed. No system reduced-motion, large-font, RTL, or TalkBack verification was run, as planned/out of scope.

## Follow-up

R5.4A system reduced-motion policy is the next effects slice; continue with R5.4B ambient background foundation when dependencies allow.
