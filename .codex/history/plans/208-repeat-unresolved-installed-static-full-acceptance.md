# Plan 208 — Repeat unresolved installed Static Full acceptance

Status: Completed
Cycle ID: 208-repeat-unresolved-installed-static-full-acceptance
Roadmap item: SQ.9A
Created: 2026-10-15

## Objective

Repeat outstanding SQ.9A installed acceptance cells after cycle 207's RTL correction. Record a row-by-row disposition for the current APK on the physical Pixel 3 XL. Accept SQ.9A only if every required cell passes.

## Production boundary

Evidence and acceptance only. No production or test source changes. A discovered defect is recorded as a failed cell and requires a separate correction cycle.

## Functional invariants

- Preserve weather values, condition identity/text, missing-data behavior, chronology, source/freshness/provenance, and request/cache behavior.
- Preserve Now → Hourly → Daily → Details, outer pager swipe, Back, page chooser, window controls, semantics, and 48dp guidance.
- Full, Subtle, and None change only presentation; None stays opaque, static, and complete. Reduced motion does not rewrite the saved effect level.

## Implementation steps

1. Record Pixel 3 XL/API, source and APK hash, installed package identity, viewport, font, locale, effective direction, theme/effects, timeout, Stay Awake, and animation settings. Keep timeout disabled and Stay Awake selected while testing. Freshly install the current debug APK. It differs from cycle 205, so recapture candidate-dependent rows.
2. Capture all four Full pages at 360×640dp, font 1.0 and 1.3, Standard/LTR. Include lower-content scroll captures at 1.3 for source/freshness, Hourly and Daily controls, and Details groups. Capture High contrast and sparse states on the current APK.
3. Toggle Developer Options Force RTL visibly; prove Android effective `ldrtl`, run the focused production RTL method, and capture all four installed Full pages with paired hierarchies. Restore original direction through the visible switch and prove effective `ldltr` afterward.
4. Capture four pages each at None and Subtle, font 1.0 Standard/LTR, and inspect decoration suppression and complete facts and controls. Exercise chooser, outer swipe, Android Back, Hourly date/Earlier/Later, and Daily Earlier/Later with before/after evidence.
5. Exercise reduced motion with Full saved; record effective appearance, saved preference, static completeness, and restoration. Run focused existing appearance/no-refetch and motion tests, plus four candidate-dependent composition/sparse methods and the Snow method.
6. Assemble a consolidated PASS/FAIL/UNVERIFIED matrix linking each artifact. Run workflow and `git diff --check`, inspect the final diff, and close with exact acceptance status and limitations.

## Acceptance criteria

- Current APK and device configuration are proven for each current capture/test. No old candidate-dependent PASS is reused across the cycle 207 source change.
- All required SQ.9A cells pass: four pages at Full 1.0/1.3 (including lower content), High contrast, effective device RTL, None and Subtle; supported and Snow/unsupported marks; sparse states; page/window navigation; reduced motion and saved preference; focused semantics and request/cache invariance.
- If any required cell fails or cannot be verified, SQ.9A stays unaccepted and SQ.10–SQ.16 remain gated. No test or screenshot alone substitutes for the other where both are required.

## Verification and evidence

Evidence: `.codex/test-artifacts/208-repeat-unresolved-installed-static-full-acceptance/`; preserve device/settings readbacks, exact commands and method-filtered JUnit results, screenshots paired with UI hierarchies and SHA-256, interaction observations, matrix, and evidence index. Use Pixel 3 XL serial `8AKY0M32X` for all slice tests and captures, with screen timeout 0 and Stay Awake selected. Run focused connected methods from the cycle 206 plan: production RTL; three `ThemeAppearanceApplicationFlowTest` methods; four Glass composition/sparse methods; and `ProductionSnowMarkGapReviewScenarioTest`. Do not run full suites or `dev.py check`; SQ.V owns those.

## Risks and assumptions

- Force RTL requires the visible Developer Options toggle; global setting alone did not establish effective RTL in cycle 206. Capture effective configuration during the run and restore it afterward.
- A new APK hash invalidates candidate-dependent cycle 204/205 evidence. Recapture or rerun each required row on the current build.
- An installed defect requires a separate bounded correction plan.

## Out of scope

- Production/test edits, redesign, SQ.10 performance, SQ.11–SQ.16 implementation, full suites, TalkBack service traversal, and replacing Pixel 3 XL with another device.
