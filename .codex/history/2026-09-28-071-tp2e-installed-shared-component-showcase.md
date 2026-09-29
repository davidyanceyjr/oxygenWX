# History — 071-tp2e-installed-shared-component-showcase

Status: Blocked
Cycle ID: 071-tp2e-installed-shared-component-showcase
Roadmap item: TP.2E-partial1
Closed: 2026-09-28
Plan: .codex/plans/071-tp2e-installed-shared-component-showcase.md
Evidence: .codex/test-artifacts/071-tp2e-installed-shared-component-showcase/

## Outcome

BLOCKED: added the instrumentation-only six-family showcase and measured an installed Atmospheric Subtle case at the approved 360 x 640 dp baseline. The fixed compact viewport cannot display all six groups with the existing supplied component content: current conditions occupy 448 dp, forecast reaches y=636 dp, and source/inspection plus the decorative groups measure zero height at that boundary. Preserved the failing installed capture and measurements. No production code or resources changed; TP.2E remains open.

## Verification

Entry workflow passed. Cycle 070 PASS history/evidence confirmed. Focused connected instrumentation command compiled and ran on API 37; exit code 1, result 1 test/1 failure/0 errors at the planned whole-host fit assertion. The final test output, result XML, device logcat, environment, component inventory, diagnostics and hashes are in .codex/test-artifacts/071-tp2e-installed-shared-component-showcase/. Final six-group bounds were recorded before the failure. The final test-source cleanup compiled successfully with `:app:compileDebugAndroidTestKotlin`. `git diff --check` and post-close workflow validation passed and are recorded in the evidence folder.

## Limitations / not verified

No five-theme Subtle final matrix was captured. Cross-theme equality through all cases, callbacks, target checks beyond the page selector, focused component regression classes, JVM resolver/visual tests, and repository-wide test/build/contract/check were not run after the explicit fit gate failed. The sole Atmospheric PNG is non-final diagnostic evidence. No visual acceptance or TP.2E completion is claimed.

## Follow-up

Keep TP.2E-partial2, partial3, partial4, and TP.3 gated. Continuing requires a deliberate roadmap update with a new bounded outcome for the compact-host fit failure; do not retry by broadening the viewport, removing fixture content, shrinking text, or changing production components within this cycle.
