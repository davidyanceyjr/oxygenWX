# Plan 007 — Roadmap context-budget audit

Status: Completed
Cycle ID: 007-roadmap-context-budget-audit
Roadmap item: R0.11
Created: 2026-09-20

## Objective

Audit every unfinished roadmap item against the approximately 45% context-window
implementation budget. Replace every oversized item with ordered bounded
sub-slices that retain the original identifier for the first portion and use
`A`, `B`, and later suffixes for dependent deferred portions. Report the revised
execution sequence without changing product/release scope or production code.

## Production boundary

Changes are limited to `docs/ROADMAP.md` and this cycle's `.codex/` records.
No source, build, test, generated-image, or runtime configuration file is
changed.

## Functional invariants

- A split does not add release scope, change product/safety semantics, alter
  provider strategy, or remove an existing acceptance obligation.
- A later suffix is an ordered dependent portion of its base identifier; it is
  not an implicitly optional or release-deferred feature unless its parent
  roadmap area is already marked deferred.
- The Theme B delivery order remains resolver, shared components, Hourly,
  Daily, Details, then Now.
- Existing completed/history-backed items remain completed; the audit corrects
  the stale R1.1 roadmap status to match its completed history record.

## Implementation steps

1. Read the full unfinished roadmap and the governing specification; classify
   each item by likely production files, data/interaction surface, verification
   matrix, external-environment dependence, and cross-cutting risk.
2. Keep items whose stated boundary is safely below the 45% budget unchanged.
   Split only items that combine multiple independently verifiable boundaries
   or would require a broad platform/environment matrix in one cycle.
3. Add the context-budget rule to the roadmap and create the resulting suffix
   slices with explicit dependency wording. Preserve original identifiers for
   the initial portion and use `A`, then `B`, for later ordered work.
4. Reconcile R1.1's roadmap status with its existing completed history record;
   do not alter the record or claim new verification.
5. Validate workflow structure and inspect the final roadmap diff. Report both
   the changed slices and the required new ordered sequence.

## Acceptance criteria

- Every unfinished item that exceeds the budget has an explicit bounded base
  slice and one or more dependent suffix slices; all other items remain intact.
- Each suffix states what it owns and what predecessor it requires.
- The roadmap defines the suffix convention and does not misuse `DEFERRED` as
  a status for ordinary follow-on implementation work.
- The report identifies the complete new execution sequence for split work.
- `python scripts/dev.py workflow`, `python scripts/codex_cycle.py validate`,
  and `git diff --check` pass.

## Verification and evidence

Evidence path:

```text
.codex/test-artifacts/007-roadmap-context-budget-audit/
```

Retain the exact command output for:

```sh
python scripts/dev.py workflow
python scripts/codex_cycle.py validate
git diff --check
```

No product test, build, installation, or screenshot is evidence for this
documentation-only cycle.

## Risks and assumptions

- The 45% threshold is an approximate planning budget, not a measured token
  count. The audit uses boundary breadth and verification surface as a
  conservative proxy.
- Later implementation may reveal a new dependency or larger-than-expected
  boundary. It must be split again before activation rather than absorbed.
- Cross-host validation is intentionally sequential here so each suffix has a
  clear, auditable prerequisite despite the hosts being independently runnable.

## Out of scope

- Production implementation of any roadmap item, including Theme B resolver or
  components.
- Changes to product requirements, release scope, source/provider strategy,
  data semantics, visual reference assets, or existing history evidence.
- Creating the detailed implementation plans for the new slices; each starts
  only when selected as the next active cycle.
