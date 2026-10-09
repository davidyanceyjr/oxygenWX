# History — 141-reduced-motion-effects-policy

Status: Completed
Cycle ID: 141-reduced-motion-effects-policy
Roadmap item: R5.4A
Closed: 2026-10-07
Plan: .codex/history/plans/141-reduced-motion-effects-policy.md
Evidence: .codex/test-artifacts/141-reduced-motion-effects-policy/

## Outcome

Implemented and verified a live Android reduced-motion cap. At animator scale zero the effective theme sets MotionStyle.OFF and all five programmatic pager routes snap; the saved effects choice remains unchanged. Added platform-neutral policy tests, live connected flow coverage, installed API 37 evidence, and the verification record.

## Verification

python scripts/dev.py check PASS; ThemeAppearanceApplicationFlowTest connected suite PASS (9 tests, 0 failures); git diff --check PASS. Evidence: .codex/test-artifacts/141-reduced-motion-effects-policy/verification.md and installed captures.

## Limitations / not verified

The emulator software renderer emitted sparse recording frames; branch selection was proven by the connected motion-choice probe and live setting test. Large-font, RTL, TalkBack, and broad R6.4 verification were not run. Compose MotionDurationScale context remained stale in the initial live test; its failure is preserved and the scoped ContentObserver replacement passed.

## Follow-up

R5.4B-R5.4E motion consumers should read effective ResolvedTheme.motionStyle. Original emulator animator scale 1 was restored.
