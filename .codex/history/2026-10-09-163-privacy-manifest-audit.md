# History — 163-privacy-manifest-audit

Status: Completed
Cycle ID: 163-privacy-manifest-audit
Roadmap item: R7.1
Closed: 2026-10-09
Plan: .codex/plans/163-privacy-manifest-audit.md
Evidence: .codex/test-artifacts/163-privacy-manifest-audit/

## Outcome

Audited the merged Release manifest and runtime dependency graph; constrained backup to settings in cloud and legacy backups, retained location preferences for device transfer, and marked location hardware optional.

## Verification

Release manifest processing and unsigned assembly passed after final changes; Release APK SHA-256 3b59611cd4aab0a3a7c8a0d05eed1e0b2334da488b7c5d228c447c56dc0479c1. Packaged manifest, permission list, backup XML, merger report, and releaseRuntimeClasspath were inspected and retained under .codex/test-artifacts/163-privacy-manifest-audit/. scripts/dev.py contract and check passed; workflow and git diff --check passed before close. The post-close workflow also passed with state=IDLE and 163 history records; final git diff --check passed.

## Limitations / not verified

Cloud/device-transfer backup behavior was verified from packaged rules, not exercised through Android backup services. Release APK is unsigned; no clean-host, dependency notice/license, provider attribution, or release-readiness claim is made. Two unused direct dependency candidates remain documented for future review.

## Follow-up

Proceed to R7.2 data-source, dependency-notice, and license audit.
