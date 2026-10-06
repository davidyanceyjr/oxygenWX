# Plan 128 — Alert provider and result contract

Status: Completed
Cycle ID: 128-alert-provider-result-contract
Roadmap item: R4.1
Created: 2026-10-05

## Objective

Define a provider-neutral official-alert request/result boundary that can
represent a supported successful query (including zero alerts), an unsupported
region, or a provider failure without conflating those outcomes. Preserve
source-supplied alert identity, issuer/event, severity, effective/expiry,
description/instructions, provenance, and safe source URL through the contract.
This is a contract-only prerequisite for regional alert-provider work.

## Production boundary

Add the provider-neutral official-alert provider and result types under the
data boundary, plus deterministic contract tests. Existing `OfficialAlert`
remains distinct from forecast records and retains its source/provenance
validation. No transport, provider parser, repository, application state, or UI
composition changes are part of this cycle.

## Functional invariants

- Official alerts are authoritative-source records and remain semantically
  separate from forecast heuristics and ordinary weather values.
- A supported query returning no records is distinct from an unsupported
  region and from a failed query.
- Preserve issuer, event name, optional severity, effective/expiry times,
  supplied description/instructions, source identity/provenance, and optional
  safe source URL without substituting inferred alert meaning.
- Missing optional source fields remain absent; they are never synthesized.
- Provider-neutral canonical weather and forecast models are unchanged.

## Implementation steps

1. Inspect existing weather source, request/result, alert model, and test
   conventions; confirm an alert request has only the location identity needed
   for regional support and does not carry provider-specific transport details.
2. Define a provider-neutral alert query and sealed/result representation for
   supported results (including an empty alert list), unsupported regions, and
   failures. Define provider identity/capability only to the extent needed by
   this boundary.
3. Add deterministic tests for each outcome and preservation of supplied
   `OfficialAlert` fields, including absent optional fields and official-alert
   provenance.
4. Run focused alert-contract tests, `python scripts/dev.py contract`,
   `python scripts/dev.py check`, workflow validation, and `git diff --check`;
   record exact commands/results and limitations.

## Acceptance criteria

- Contract types compile and make supported-empty, unsupported, and failed
  outcomes distinct without encoding provider-specific HTTP/JSON behavior.
- Deterministic tests cover all three result categories and preserve the
  issuer/event/effective/expiry/provenance fields and supplied description,
  instructions, severity, and source URL, including null optional values.
- Alert data remains separate from forecast data, and no alert language is
  derived from forecast conditions.
- Focused tests, contract check, repository check, workflow check, and diff
  check pass; command output is preserved under the cycle evidence directory.

## Verification and evidence

No installed UI evidence is required: this slice does not change rendering or
application composition. Preserve focused test output and a concise verification
record under `.codex/test-artifacts/128-alert-provider-result-contract/`.
Run the smallest relevant alert contract test while iterating, then
`python scripts/dev.py contract`, `python scripts/dev.py check`,
`python scripts/dev.py workflow`, and `git diff --check` before closeout.

## Risks and assumptions

- The existing `OfficialAlert` model already enforces nonblank issuer/event and
  official-alert provenance; contract work should reuse it rather than create a
  competing alert representation.
- A supported empty response must be represented explicitly as success with an
  empty collection, not null or unsupported.
- Safe URL validation should follow existing repository URL conventions if
  present; this slice must not introduce provider URL allowlists that belong to
  the NWS adapter.
- Android installed verification is not applicable unless implementation
  unexpectedly changes an application-facing boundary; any such scope change
  requires updating the plan before implementation.

## Out of scope

- NOAA/NWS HTTP transport, response decoding, parser fixtures, or provider
  identification/attribution details (R4.2).
- Alert repository integration, selected-location state, refresh scheduling,
  and alert caching (R4.2A).
- Home alert summary, alert detail/selection navigation, or spoken UI semantics
  (R4.3–R4.4A and R6.1A).
- Forecast/provider/cache/location behavior or changes to weather values.
- Any implementation beyond the single R4.1 contract boundary.
