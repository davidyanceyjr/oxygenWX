# Plan 214 — Decide SQ.10 static measurement method

Status: Planned
Cycle ID: 214-sq10-method-decision
Roadmap item: SQ.10-METHOD-DECISION
Created: 2026-10-21
Evidence: .codex/test-artifacts/214-sq10-method-decision/

## Objective

Review the closed-cycle evidence from 211–213 and produce a source-by-source SQ.10-A method decision. For every Gate B measure, state the usable capture/endpoint, attribution and validity rules, retained calculations, and any feasibility evidence still required. Keep SQ.10-A gated wherever the current evidence cannot support a trustworthy method. This cycle produces no scored result.

The decision must keep two questions separate for every source: (1) what the
existing evidence establishes, and (2) whether the method is ready for scored
use. A plausible method description, synthetic calculation check, visible
screenshot, or neighboring source's successful result does not upgrade missing,
ambiguous, contaminated, or insufficient evidence. Use `VALID`,
`DIAGNOSTIC-ONLY`, or `UNVERIFIED` for retained evidence, then separately mark
the method `READY`, `READY AFTER NAMED FEASIBILITY CHECK`, or `NOT READY`.
Use `INVALID` only where positive evidence contradicts identity or shows
contamination; absence or ambiguity is `UNVERIFIED`.

## Production boundary

Documentation and existing evidence review only. Record the method decision and SQ.10-A readiness handoff under `.codex/test-artifacts/214-sq10-method-decision/`, linking cycle 211 raw measurement evidence and cycle 213 path/event evidence. Do not modify production, test, or build sources; do not collect new device measurements. If the existing evidence cannot decide a method, identify one separately bounded follow-up slice instead of expanding this cycle.

## Functional invariants

- Preserve the adopted 500 ms scripted-interaction limit and cycle 211's device-side semantic-readiness endpoint. Event delivery and query/confirmation time stay within the measured interval; screenshot-only confirmation is not an observer pass or accessibility-action claim.
- Preserve Gate B limits and calculation rules: at least 90% of valid transition frames meet the active refresh deadline, transition-frame p95 is at most 25 ms, added settled/peak PSS is at most 16 MiB, and scripted interaction p95 is at most 500 ms.
- Preserve the accepted cycle 208 app/APK identity and report device scope. None/Full appearance changes do not change weather meaning, navigation, provenance, or request/cache behavior.
- Keep missing, late, unattributable, or insufficient evidence UNVERIFIED;
  classify positively contradicted or contaminated evidence INVALID. Neither
  status implies Gate B PASS or FAIL, and invalid runs remain retained.
- Do not count setup scrolling, idle accumulation, startup diagnostics, screenshots, or previously unscored dry-run values as scored workload samples.

## Implementation steps

1. Establish the evidence index from cycle 211 `protocol.md`, `findings.md`,
   `handoff.md`, `analyze_capture_sources.py`, raw frame exports and trace
   processor logs, PSS CSVs, device/host clock snapshots, request/cache records,
   and `calculation-checks/`; use cycle 213 `handoff.md`,
   `connected/paths-final.json`, `connected/paths-final-reviewed.json`, and
   `visual-review.md`. Follow cycle 213's links into cycle 212 only where a
   predecessor observation is needed. Preserve the distinction between raw
   capture, automated classification, and manual screenshot adjudication.
2. Build one row per source/claim, with these columns: required Gate B claim;
   source artifact(s); what the record directly establishes; exclusions and
   uncertainty; evidence disposition; method-readiness disposition; exact
   endpoint/capture rule; matching or attribution rule; adequacy/invalidity
   criteria; and next bounded evidence needed. Cover, at minimum:
   - candidate, device, configuration, fixture, and restoration identity;
   - interaction input plus semantic event/query endpoint and safe target
     reachability, keeping screenshot-rendered state separate from event
     confirmation;
   - transition-frame selection, app process and surface attribution,
     action-window filtering, content correlation, sorter errors, and Late
     Present handling;
   - Perfetto/device and host/device clock joins, including measured uncertainty
     adequate for each intended correlation;
   - PSS process identity, sampling cadence, paired settled baseline, peak, and
     required post-action tail;
   - request/cache invariance controls and what the fixture-only candidate does
     and does not establish;
   - calculation definitions, pooling, invalid-run retention, insufficiency
     rules, and the unchanged Gate B thresholds.
3. Adjudicate each row independently. Do not transfer validity across sources
   or actions. State explicitly whether the existing method is usable as
   written, can become usable after a named bounded feasibility check, or is
   unsupported. For each unsupported row, identify the specific observation
   that would resolve it and the smallest safe scope that can collect that
   evidence. If several gaps need distinct methods, record them separately and
   order them; select the immediate next prerequisite based on dependency on
   SQ.10-A, without implying later prerequisites are satisfied. Do not infer a
   production defect or an accessibility failure from missing instrumentation
   targets/events.
4. Write `method-decision.md`, the source disposition matrix, and the SQ.10-A
   readiness handoff under the cycle evidence directory. Include direct
   relative links to the source artifacts and explain any conclusion that is
   an inference rather than a directly recorded result. Retain all failure and
   UNVERIFIED rules. State the exact SQ.10-A eligibility decision and any
   separately bounded next prerequisite(s) with observable exit criteria.
   Preserve the SQ.10-A workload/action matrix free of idle accumulation and
   setup scrolling; those activities cannot be used to manufacture samples.
5. Update `docs/UI_CONTEXT_ROADMAP.md` and the relevant summary in
   `docs/ROADMAP.md` so cycle 214's decision precedes SQ.10-A and the dependency
   gate reflects the decision. Do not mark SQ.10-A ready solely because a
   follow-up plan exists: any required feasibility validation must pass before
   its plan is authorized under the roadmap. Run `python scripts/dev.py
   workflow`, `git diff --check`, and review the final plan/roadmap/evidence
   diff.

## Acceptance criteria

- Every Gate B measure and the semantic interaction endpoint has a matrix row
  with source artifact(s), capture/stop rule, attribution or matching rule,
  validity criteria, exclusions, and missing/late/ambiguous/insufficient
  `UNVERIFIED` behavior.
- Evidence disposition and method-readiness are separate for every row. Each
  claim links to evidence that directly supports it; inferences are labeled.
  Cycle 211's sorter errors/Late Present contamination, missing action filtering
  and content correlation, inadequate PSS cadence/tail, incomplete clock
  uncertainty, and absent request/cache counters remain visible unless an
  identified existing raw record directly resolves that specific gap.
- Cycle 213 screenshot visibility and eligible event confirmation remain
  separate. Unique-target misses remain unresolved observer evidence and are
  not described as a production defect, accessibility failure, or pass.
- The handoff gives a direct `READY` or `GATED` SQ.10-A disposition. `READY`
  requires every scored source and the interaction endpoint to have an
  adequate method and any required feasibility validation to be complete. If
  gated, each unsupported source has a smallest bounded follow-up outcome and
  observable exit; the roadmap names the immediate dependency without
  conflating distinct later prerequisites.
- The unchanged endpoint is touchscreen down through fresh eligible event
  receipt and query/confirmation on device elapsed realtime, with the adopted
  500 ms interaction limit. The adopted Gate B thresholds and cycle 208
  candidate/device scope remain unchanged.
- No scored repetitions, latency claim, Gate B PASS/FAIL, app behavior change,
  or new physical-device measurement is produced.

## Verification and evidence

- No Android test or installed check is in scope: this documentation/evidence
  review changes no executable source and collects no device data.
- Preserve `method-decision.md`, `source-disposition-matrix.md`, and
  `sq10-a-readiness-handoff.md` under
  `.codex/test-artifacts/214-sq10-method-decision/`, with links to cycle 211
  and 213 evidence; add cycle 212 links only for evidence actually cited.
- Validate `.codex/current.md` and plan consistency with
  `python scripts/dev.py workflow`; run `git diff --check` and inspect the
  final plan, both roadmap summaries, and evidence links.

## Risks and assumptions

- Cycle 213's instrumentation could identify rendered destinations in
  screenshots but could not obtain unique safe targets or eligible event
  confirmations for several visible controls. Screenshots may establish
  rendered state for the path review; they do not validate the timed event
  endpoint.
- Cycle 211's frame, PSS, clock, and request/cache gaps are independent. They
  may require distinct feasibility slices; a single matrix or document does
  not make their measurements valid.
- A method can be specified while its evidence remains unverified. Cycle 214
  may define adequate future capture rules but may not call them experimentally
  validated unless the retained records meet the stated criteria.
- The existing 500 ms endpoint and thresholds are fixed inputs; changing them
  is outside this cycle.

## Out of scope

- Production app, resource, accessibility-semantics, test, or build-source changes.
- New device runs, scored or unscored performance measurements, latency claims, or Gate B disposition.
- Relaxing the 500 ms limit, changing thresholds/statistics, counting screenshots as event confirmation, or treating the owner's earlier cycle 210 waiver as performance acceptance.
- Full unit/connected suites, broad accessibility acceptance, visual/theme acceptance, weather/provider/cache implementation changes, or SQ.10-A itself.
