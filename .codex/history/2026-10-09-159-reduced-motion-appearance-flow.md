# History — 159-reduced-motion-appearance-flow

Status: Completed
Cycle ID: 159-reduced-motion-appearance-flow
Roadmap item: R6.4-partial-A
Closed: 2026-10-09
Plan: .codex/plans/159-reduced-motion-appearance-flow.md
Evidence: .codex/test-artifacts/159-reduced-motion-appearance-flow/

## Outcome

Completed R6.4-partial-A production appearance flow: shared typed test projection, settled fixture semantics, transport-level positive controls, all five themes/two contrast/three effects actions, and zero-delta network/cache verification.

## Verification

API 37 oxygen_starter focused connected test passed (1 test, 0 failures); both transports reached terminal response state and counts 0->1 then stayed 1 through all appearance actions; cache totals stayed read 1/write 0. Focused JVM test, dev.py contract, dev.py check, dev.py workflow, and git diff --check passed. Evidence: .codex/test-artifacts/159-reduced-motion-appearance-flow/verification.md, fixture-diagnosis.md, flow-counts.txt, focused-flow-pass.xml, focused-jvm.log, dev-check.log.

## Limitations / not verified

The exact asynchronous transition behind the former initial text-query race was not isolated. No installed 20-cell Effects Off/reduced-motion visual review, Simple layout, RTL, or TalkBack service traversal was claimed in this portion.

## Follow-up

Proceed to R6.4-partial-B installed Effects Off/reduced-motion 20-cell visual review, citing Cycle 158 and 159 results.
