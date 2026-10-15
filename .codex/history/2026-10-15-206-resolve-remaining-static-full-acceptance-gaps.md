# History — 206-resolve-remaining-static-full-acceptance-gaps

Status: Completed
Cycle ID: 206-resolve-remaining-static-full-acceptance-gaps
Roadmap item: SQ.9A
Closed: 2026-10-15
Plan: .codex/plans/206-resolve-remaining-static-full-acceptance-gaps.md
Evidence: .codex/test-artifacts/206-resolve-remaining-static-full-acceptance-gaps/

## Outcome

SQ.9A not accepted: physical Pixel 3 XL Force RTL setting reached 1, but production Home remained LTR in both permitted focused attempts. No source edits.

## Verification

APK SHA-256 matched cycles 204/205 and was freshly installed; Pixel 3 XL identity and 360x640dp override checked; two method-filtered ProductionRtlNavigationTest runs each executed one test and failed the RTL root-direction assertion; original global RTL value, locale, viewport, timeout, Stay Awake, and animation settings were read back; workflow and git diff --check passed. Evidence: .codex/test-artifacts/206-resolve-remaining-static-full-acceptance-gaps/.

## Limitations / not verified

Mandatory RTL device proof failed after the single planned retry. No RTL page captures were accepted; remaining large-font, None, Subtle, navigation, reduced-motion, and three appearance focused methods were not run. Earlier PASS rows were not promoted to cycle-206 acceptance.

Post-close correction: cycle 207 diagnostics showed that changing the global RTL value alone had not established the effective device direction. A later device check found the reverse restoration gap: the global value was 0 while Android's configuration remained RTL. The visible Developer Options Force RTL switch was subsequently toggled on and off, and the effective device configuration read back `ldltr`.

## Follow-up

Create a separate bounded correction plan for production Home direction under device Force RTL, then repeat the unresolved SQ.9A acceptance cells; keep SQ.10-SQ.16 gated.
