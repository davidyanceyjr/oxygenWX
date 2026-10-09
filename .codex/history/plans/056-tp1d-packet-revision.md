# Plan 056 — TP.1D immutable packet revision

Status: Completed  
Cycle ID: 056-tp1d-packet-revision  
Roadmap item: TP.1D-packet-revision  
Created: 2026-09-25  
Revised: 2026-09-25  
Slice difficulty: 5/10  
Context estimate: 30–40% of one agent context window; within the 45% limit.

## Objective

Prepare one new, immutable TP.1D owner-review packet revision that reconciles
the completed D28, D29, and D31 decisions. Record its exact identity, complete
manifest, and reproducible aggregate digest. Leave the overall packet
disposition pending for a separate cycle. This slice does not request or imply
owner approval.

## Production boundary

Documentation and static packet tooling only. The work is limited to:

- Cycle evidence under `.codex/test-artifacts/056-tp1d-packet-revision/`,
  including a new packet directory, deterministic assembly/audit scripts,
  focused tests, and verification outputs.
- The TP.1D execution head and the bounded-cycle record in
  `docs/theme-pack-roadmap.md`.
- A cycle history record under `.codex/history/` when implementation and
  verification are complete.
- `.codex/current.md` only to keep its objective and active plan reference
  aligned with this revised plan.

Do not edit Android production code/resources; current design proposals or
decision records; the r2 packet, its evidence, or prior cycle records; or
unrelated worktree changes. Do not conduct the owner disposition. If a required
decision, packet source, or integrity fact cannot be reconciled from repository
evidence, stop before assembly and record the exact blocker; do not guess.

## Functional invariants

- D28, D29, and D31 are represented only within the scope of their recorded
  decisions and reviewed artifact identities.
- The r2 packet at
  `.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/packet/tp1d-proposed-r2-symbol033-palette034/`
  remains byte-for-byte unchanged with aggregate digest
  `3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869`.
- The r3 packet is a distinct, self-contained review artifact. Its identity,
  complete file manifest, and aggregate digest are generated and verified
  before the roadmap offers it for owner review.
- Settled D28/D29/D31 questions are documented as settled and are not offered
  again as open alternatives. Only the overall disposition of the exact r3
  packet remains pending.
- Packet preparation does not close TP.1D/TP.1, establish installed visual
  acceptance, or make TP.2 eligible. No weather meaning, values, provenance,
  navigation, accessibility contract, or Effects Off behavior changes.

## Inputs and decision authority

Before copying or editing packet content, capture SHA-256 values for the exact
inputs and write a source ledger under the cycle evidence directory. The ledger
must include path, digest, role, and any reviewed-content digest distinct from a
later metadata-updated file digest.

- Base packet: the r2 path above. Verify its manifest, its recorded aggregate,
  and the 117-entry prior inventory before using it.
- D28: the explicit owner direction in the execution head of
  `docs/theme-pack-roadmap.md` and the prior TP.1D review record at
  `.codex/history/2026-09-23-028-tp-1d-integrated-pack-review.md`. Carry
  forward Option 1: accept the proposed reproducible font families used in the
  packet. Do not claim installed font rendering was verified.
- D29: `.codex/test-artifacts/049-d29-weather-mark-owner-approval/owner-decision.md`
  and its reviewed matrix digest `e2fec17fe1fde1a18b3161aa08dc72fb520ea53769c19e9112311ea3d56b9792`.
  The decision approves the 30-cell matrix as presented, including Terminal
  tokens and explicit no-mark gaps. It does not approve D32, packet approval,
  runtime artwork, or TP.2.
- D31: `docs/theme-system/design-pack/D31_OWNER_DECISION.md`,
  `.codex/history/2026-09-25-055-d31-integrated-owner-disposition.md`, and
  the exact reviewed mapping/guide digests recorded in that decision file.
  Carry forward approval of all twenty theme/page cells and the overall set,
  including the five proposed same-theme Details derivations. This decision
  does not establish installed visual/accessibility acceptance or packet
  approval.
- Current gate: `docs/theme-pack-roadmap.md`, including the execution head,
  TP.1D bounded exit, and `docs/SPECIFICATION.md` / adopted UI authority for
  product constraints.

If live source files differ from recorded reviewed digests, distinguish an
owner-reviewed digest from a current status-bearing file digest. Preserve both
in the ledger. Do not silently substitute revised source content for the
reviewed artifact.

## Implementation steps

1. **Capture state and verify inputs.** Save initial `git status --short`,
   current r2 file manifest/digest, and the source ledger. Independently verify
   the D28/D29/D31 decision evidence and the packet assembly/audit scripts from
   cycle 033B. Confirm all required source paths exist. Stop with a recorded
   blocker if an identity or outcome is unclear.
2. **Reconcile packet content.** Audit every packet-local occurrence of D28,
   D29, D31, `pending`, and the former D31 alternatives. Prepare a change map
   listing each changed path, why it must change, and its source. The new packet
   must describe the decisions as follows:
   - D28 Option 1 accepted; named font families remain proposed reproducible
     choices, not installed acceptance.
   - D29 matrix approved as presented at the recorded digest; D32 remains a
     separate proposed symbol map.
   - D31 twenty theme/page atmosphere proposals approved as recorded; overall
     D31 disposition approved.
   - Overall r3 packet response is blank/pending. Do not repeat D28/D29/D31 as
     open decisions and do not ask the owner to decide them again.
   Update stale packet-local status language wherever it states those three
   decisions are still open. Preserve unrelated technical contract wording.
3. **Assemble the immutable r3 packet.** Add a deterministic cycle-local
   assembler, its inputs/configuration, and a focused unittest module under
   `.codex/test-artifacts/056-tp1d-packet-revision/`. Use the exact revision
   identifier `tp1d-proposed-r3-d28-d29-d31` and keep r2 untouched. The
   assemble into a cycle-local staging directory first. If the final r3 path
   already exists, compare every path and digest and leave it untouched only
   when it is byte-identical; otherwise abort. Never recursively delete or
   overwrite an existing packet.
   Preserve the r2 file set except for the minimum changes in the change map.
   Include a clear `OWNER_GUIDE.md`, `REVISION_CHANGELOG.md`, complete
   `SOURCE_INVENTORY.json`, and `SHA256SUMS.txt`. The manifest hashes every
   packet file except itself. Define the owner-guide aggregate as SHA-256 over
   the sorted manifest rows excluding the owner-guide row to avoid self
   reference; the full manifest still hashes the guide. Keep the owner guide's
   overall response and date blank and identify the exact r3 revision/digest.
4. **Make decision and packet links auditable.** The r3 owner guide and
   changelog link to each governing decision record and state each boundary
   above. Keep links inside the packet relative and resolvable; cite repository
   decision evidence with stable repository paths and recorded digests. Do not
   alter current approved decision files to make packet-local links work.
5. **Independently audit the result.** Add a separate read-only audit script
   and tests. Verify all manifest entries and hashes, deterministic aggregate
   digest, source inventory coverage, copied source digests, packet-local links
   and anchors, required decision wording/status, blank overall owner response,
   and absence of stale statements that D28/D29/D31 remain pending. Verify the
   source change map covers every changed file and no unexplained file changed.
   Compare r2 against a before-assembly snapshot and its published digest.
6. **Update the execution head and future gate.** In
   `docs/theme-pack-roadmap.md`, record r3 identity, full manifest count,
   aggregate digest, verification location, and the next action: separate
   explicit owner disposition of this exact revision. Keep TP.1D/TP.1 open and
   TP.2 gated. Retain r2's historical revise disposition without rewriting its
   outcome. Update only the relevant execution-head / TP.1D status passages;
   do not rewrite prior history.
7. **Run focused verification and close the cycle.** Run the cycle-local
   focused tests and independent audit, `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, and `git diff --check`. Inspect the full
   diff and compare final status to initial status so unrelated D31 work remains
   intact. Save command lines, outputs, final status, r2 comparison, and any
   limits under the evidence directory. Write a history record with exact
   outcomes; do not claim unrun visual, device, owner, or Android verification.

## Acceptance criteria

- Source ledger names and hashes the packet base and D28/D29/D31 decision
  evidence, with owner-reviewed digests distinguished from current file digests.
- r3 is reproducibly assembled, self-contained, and differs from r2 only in
  the reviewed change map. Its owner guide presents D28/D29/D31 as settled
  within scope and leaves the exact r3 overall disposition pending. The
  manifest covers every packet file except itself; its owner-guide aggregate
  rule is explicit, deterministic, and independently reproducible.
- Independent audit verifies all files, links, source identities, decision
  wording, digest rules, and manifest coverage. Focused tests prove deterministic
  output, rerun safety, failure on missing/mismatched inputs, stale-decision
  language detection, and r2 immutability.
- Roadmap identifies r3 and its exact digest, points to the separate exact-r3
  owner disposition as next, and keeps TP.1D/TP.1 unresolved and TP.2 gated.
- Workflow, contract, focused tests/audit, and diff checks pass. The history
  record and evidence state the exact verification and unverified boundaries.

## Verification and evidence

Store under `.codex/test-artifacts/056-tp1d-packet-revision/`:

- `initial-worktree-status.txt`, `source-ledger.json`, and r2 before/after
  manifests/digest checks;
- `packet/` containing the immutable r3 revision and its complete manifest;
- cycle-local assembler, independent auditor, focused tests, test output, and
  audit output;
- packet change map, link audit, source-digest audit, manifest/aggregate
  verification, workflow/contract output, final status, and diff-check output.

No Android build/install, installed screenshot comparison, TalkBack review,
owner disposition, or approval is required or claimed in this cycle.

## Risks and assumptions

- The decision ledger and exact owner-reviewed artifact hashes are authoritative
  only within their recorded scopes; do not broaden them to runtime acceptance.
- r2's 117-file inventory and aggregate establish its prior identity. The r3
  manifest count and digest are generated during this cycle and must not be
  guessed or copied from r2.
- Packet-local links must remain valid after r3 is moved/copied as a directory;
  links to external repository decision evidence are documented path references,
  not falsely presented as included files.
- The packet may contain status prose beyond the owner guide. The content audit
  must find and reconcile every stale D28/D29/D31 pending claim before freeze.
- Context estimate assumes scripts/tests stay packet-specific and focused on
  deterministic static artifacts. No Android implementation or broad
  repository test framework is added.

## Out of scope

- Requesting or recording the owner disposition of r3.
- Editing the approved D28/D29/D31 decisions, their approved design proposals,
  or the r2 packet/evidence.
- TP.2 resolver, component, renderer, or runtime implementation.
- TP.3 installed application comparison or visual/accessibility acceptance.
- Changes to Android code/resources, weather semantics, app navigation, or
  unrelated working-tree changes.
