# History — 058-tp2a-approved-tokens-resolver

Status: Blocked
Cycle ID: 058-tp2a-approved-tokens-resolver
Roadmap item: TP.2A part one
Closed: 2026-09-26
Plan: .codex/plans/058-tp2a-approved-tokens-resolver.md
Evidence: .codex/test-artifacts/058-tp2a-approved-tokens-resolver/

## Outcome

The catalog schema and proposed JSON-to-Kotlin field mapping were inventoried.
The direct spacing mapping revealed six mismatches between the approved JSON
catalog and current typed runtime geometry. The active plan forbids changing
approved JSON and runtime appearance values, and directs that apparent
approved-semantics conflicts be recorded as blockers. No checker or runtime
code was changed, and TP.2A part one is not complete.

## Verification

- `python scripts/dev.py workflow`: passed before closure with state ACTIVE and
  after closure with state IDLE.
- Catalog JSON and the typed catalog source were read and inventoried; exact
  mismatch values and runtime definitions are recorded in the cycle evidence.
- Focused checker tests and a checker command were not implemented or run.
- `python scripts/dev.py contract`: passed after closure.
- `git diff --check`: passed after closure.
- `python scripts/dev.py check`: passed after closure; Gradle reported unit
  tests, lint, and debug assembly successful (most tasks were up to date).

## Limitations / not verified

No static conformance PASS, positive/negative checker fixtures, or architecture
command documentation is claimed. The repository unit-test/lint/build tasks
passed, but they did not exercise the proposed checker or resolver matrix.
Installed rendering and visual/accessibility acceptance are not claimed. The
resolver and 60-combination matrix remain deferred to the dependent partial2
plan.

## Follow-up

Create a bounded roadmap decision for reconciling the six spacing mismatches
and confirming the intended direct field mapping while preserving the approved
JSON/runtime authority boundary. Only after that decision may a new catalog
conformance cycle proceed. Keep TP.2A incomplete and TP.2B gated.
