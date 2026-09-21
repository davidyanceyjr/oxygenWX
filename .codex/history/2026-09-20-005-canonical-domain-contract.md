# History — 005-canonical-domain-contract

Status: Completed
Cycle ID: 005-canonical-domain-contract
Roadmap item: R1.1
Closed: 2026-09-20
Plan: .codex/plans/005-canonical-domain-contract.md
Evidence: .codex/test-artifacts/005-canonical-domain-contract/

## Outcome

Completed R1.1 canonical domain contract: nullable finite canonical weather fields, chronology validation that preserves sparse/duplicate entries, a provenance-guarded OfficialAlert, and repository result facts for origin/freshness/refresh/cache-write outcomes. Demo fixture values and Home output remain stable; partial values map honestly to unavailable or omission and never fabricate precipitation or weather marks.

## Verification

Focused data/derived/presentation tests, python scripts/dev.py test, python scripts/dev.py check, source-contract checks, and git diff --check passed. The final check ran 30 JVM tests with 0 failures/errors, lintDebug, and debug assembly. The debug APK was installed on headless oxygen_starter; compact Now, Hourly, Daily, and Details screenshots and UI hierarchy evidence are retained under .codex/test-artifacts/005-canonical-domain-contract/.

## Limitations / not verified

The installed smoke used the complete fixture; partial-data behavior is deterministic presentation-test evidence rather than an installed partial fixture. Large-font, RTL, Effects Off, and TalkBack service traversal were not repeated for this data-contract slice.

## Follow-up

Select R1.2 Presentation state contract deliberately before exposing repository outcome facts as loading, stale, or failure UI states.
