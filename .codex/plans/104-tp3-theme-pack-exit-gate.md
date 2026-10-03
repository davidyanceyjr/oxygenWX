# Plan 104 — Theme pack TP.3 exit gate

Status: Completed  
Cycle ID: 104-tp3-theme-pack-exit-gate  
Roadmap item: TP.3  
Created: 2026-10-03  
Reviewed: 2026-10-03

## Objective

Audit the retained TP.3 baseline, recovery, sparse-fixture, and responsive/state
evidence; produce a criterion-to-evidence gate report. Record TP.3 as PASS only
if every current roadmap exit criterion has direct supporting evidence.
Otherwise record the gate as BLOCKED, identify exact evidence gaps, and make no
completion claim.

The independently observable outcome is one traceable gate report under
`.codex/test-artifacts/104-tp3-theme-pack-exit-gate/` that maps every exit
criterion to evidence, validators, candidate identity, disposition, and limits.

## Production boundary

No production-code or test-code changes. This is a read-only evidence audit,
plus new cycle-local report/manifest files under
`.codex/test-artifacts/104-tp3-theme-pack-exit-gate/` and the normal cycle
history closeout if performed later. Existing history, roadmap, approved
reference material, manifests, screenshots, and logs are immutable inputs.

No fresh build, install, capture, visual correction, or accessibility service
run is part of this evidence-closure cycle. If existing evidence does not
support a criterion, record the gap; do not create replacement evidence inside
this cycle.

## Authority and dependency interpretation

- Product and architecture semantics follow `docs/SPECIFICATION.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Theme-pack sequence and exit criteria follow `docs/theme-pack-roadmap.md`,
  as delegated by `docs/ROADMAP.md`.
- The approved immutable comparison target is the cycle 094 r4 Metric packet
  with aggregate SHA-256
  `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`.
- Cycle 096 (`TP.3C-partial-A`) remains historically BLOCKED with ten of twenty
  deviations. The owner clarified on 2026-10-03 that separately authorized
  recovery cycles 098–100 resolve those deviations and satisfy the TP.3C
  baseline-correction/acceptance prerequisite; cycle 100 records the passing
  all-twenty baseline gate. This interpretation is recorded in
  `docs/theme-pack-roadmap.md`. The corresponding commits are `55b4a04`
  (cycle 098 Hourly PASS), `1d87910` (cycle 099 Details PASS), and `4989df8`
  (cycle 100 Now and all-twenty baseline PASS). Do not retroactively change
  cycle 096 or claim that it passed. Audit the recovery evidence and all-twenty
  result against that clarified criterion.
- Evidence from closed records counts only for the scope and conditions it
  actually verifies. A later PASS must not erase earlier blocked history.
- The TP.3 exit is an evidence-closure gate, not another correction or capture
  cycle. Read the three TP.3C recovery slices as accepted evidence only because
  the roadmap's 2026-10-03 owner clarification explicitly says cycles 098–100
  satisfy the baseline prerequisite; do not generalize that authority to other
  criteria.

## Functional invariants

- Preserve Now → Hourly → Daily → Details and the single global horizontal
  swipe owner.
- Weather facts, chronology, provenance, missing-value behavior, navigation,
  controls, request behavior, and accessibility meaning remain unchanged.
- Effects Off remains opaque, static, and complete; static screenshots alone do
  not prove temporal-motion behavior.
- Count only the owner-approved r4 primary references and accepted case IDs;
  do not substitute regression variants or amend prior evidence.
- Distinguish automated manifest/hash validation, human visual review,
  installed interaction evidence, and service-level accessibility evidence.
- Report TalkBack/service-level traversal and temporal-motion verification as
  unverified unless direct evidence is present. Neither is silently promoted to
  a TP.3 pass requirement if the roadmap keeps it as a separate boundary.
- Preserve exact installed APK identities per evidence slice. Do not imply
  cycles 098/099/100/102/103 used one shared APK unless their records prove it.

## Implementation steps

1. Freeze the audit inputs in a source inventory: current roadmap text; cycle
   histories 094–103; approved packet and its hash; accepted correction
   handoff; all cycle-local manifests, validators, review reports, and evidence
   referenced by those histories. Confirm recovery commits `55b4a04`,
   `1d87910`, and `4989df8` are ancestors of the audited checkout, and record
   their full IDs, changed history/plan paths, file paths, and relevant digests
   in `inputs-and-hashes.md`; never edit source evidence.
2. Verify cycle status, outcome, scope, candidate/build identity, installed
   conditions, limitations, and reported focused/regression checks for:
   - 094 TP.3C-R approval and packet identity;
   - 095 twenty-case comparison and correction handoff;
   - 096 original TP.3C-partial-A BLOCKED outcome;
   - 098 Hourly recovery partial-A PASS;
   - both 099 Details recovery history records, `.codex/history/2026-10-02-099-tp-3c-details-reference-identity-and-acceptance.md`
     and `.codex/history/2026-10-02-099-tp3c-details-reference-acceptance.md`;
     reconcile their distinct cycle IDs, outcomes, and artifact links to
     identify the record supporting recovery partial-B, and report the
     duplicate without merging identities;
   - 100 Now recovery partial-C and all-twenty baseline PASS;
   - 101 blocked TP.3D attempt, 102 TP.3D-S PASS, and 103 TP.3D PASS.
3. Re-run the read-only evidence validators from their retained locations:
   - cycle 094 `validate_packet.py` and `validate_hashes.py`;
   - cycle 098 `installed/validate_cycle.py`;
   - each cycle 099 artifact tree's own validator:
     `.codex/test-artifacts/099-tp3c-details-reference-identity-and-acceptance/installed/validate_cycle.py`
     and
     `.codex/test-artifacts/099-tp3c-details-reference-acceptance/validation/validate_cycle.py`;
   - cycle 100 `installed/validate_cycle.py`;
   - cycle 102 `validate_cycle.py`;
   - cycle 103 `validate_cycle.py`.
   Preserve commands and stdout/stderr/exit status in cycle-local `logs/`.
   If a validator cannot be rerun because a required input/tool is absent,
   record that as unverified; do not repair or regenerate historical evidence.
4. Build a criterion matrix covering at minimum:
   - approved packet identity and approval;
   - REVIEW COMPLETE and correction handoff for all twenty baseline cases;
   - cycle 096's blocked state and ten deviations;
   - all three recovery slice exits, with five primary Hourly, five Details,
     and twenty final baseline cases accounted for;
   - the single correction-pass rule and whether cycle 100's bounded recovery
     is authorized by the roadmap without implying it rewrites 096;
   - TP.3D-S sparse fixture debug-only/default-release conditions;
   - exactly 30 TP.3D cases: 15 Now responsive/effects cases, ten RTL
     Hourly/Daily cases, and five sparse/missing-data cases, including final
     candidate identity, hierarchy/capture metadata, visual/semantic review,
     and focused/regression checks;
   - all unresolved limits, including TalkBack and temporal Effects Off.
5. Confirm the owner clarification in the roadmap is present and that the
   all-twenty baseline PASS evidence and cycles 098–100 support its stated
   interpretation. Do not treat cycle 096 itself as passing. Escalate any
   remaining authority/evidence conflict in the report; do not resolve a new
   material ambiguity by assumption.
6. Write `gate-report.md` with a criterion-by-criterion status (`PASS`,
   `FAIL`, `UNVERIFIED`, or `OWNER DECISION REQUIRED`), evidence links, exact
   artifact/build identity, validator results, and remaining limits. Include a
   concise disposition and explain any mismatch between the roadmap, history,
   or artifact records.
7. Run final workflow and diff checks, inspect the plan/report/diff, and, only
   if all TP.3 criteria are supported and authority is coherent, close the
   cycle using the repository cycle workflow with a truthful TP.3 PASS history
   record. If any blocker or owner decision remains, close the audit BLOCKED
   with that exact reason and do not mark TP.3 complete. Do not create an
   automatic retry or follow-up slice.

## Acceptance criteria

- The cycle-local report maps every TP.3 exit clause to named histories and
  evidence artifacts; no clause is accepted from a general summary alone.
- The report confirms 20/20 approved baseline cases and the required 30/30
  TP.3D cases only after validating the matching manifests, identities,
  capture/hierarchy files, dispositions, and relevant interaction evidence.
- Recovery evidence is shown as cycle 098 (Hourly), 099 (Details), and 100
  (Now plus all-twenty gate); cycle 096 remains separately recorded as
  BLOCKED. No historical record is rewritten or conflated.
- Both cycle 099 history/artifact identities are accounted for in the source
  inventory and validator results; the report identifies which record supports
  the Details recovery exit and preserves the other as a distinct duplicate.
- Recovery commit ancestry and contents are confirmed: `55b4a04` adds the
  Hourly PASS, `1d87910` adds the Details PASS, and `4989df8` adds the Now
  recovery and all-twenty baseline PASS records.
- The owner clarification in `docs/theme-pack-roadmap.md` is reflected
  accurately: recovery evidence satisfies the baseline-correction/acceptance
  prerequisite, while cycle 096 remains historically BLOCKED.
- TP.3 is PASS only when the all-twenty baseline gate, the three recovery
  slices, TP.3D-S, TP.3D, and every other current roadmap condition are
  evidenced as passing.
- Any missing/failed criterion, inconsistent identity/hash, or unverified
  requirement prevents TP.3 PASS and is stated precisely in the report/history
  disposition.
- TP.3D-S verifies sparse fixture selection only in a debuggable build, normal
  fixture behavior without the launch extra, and retained release/default
  behavior, according to cycle 102 evidence.
- TP.3D covers the roadmap's exact case counts and conditions. Installed
  conditions represented by cycle 103 include compact 360 × 640 dp, font scale
  1.3, Effects Off, RTL Hourly/Daily, and representative sparse data across
  five themes. Use the matrix's exact per-case conditions rather than
  generalizing them.
- The report explicitly leaves TalkBack/service traversal and temporal-motion
  behavior unverified if no new evidence is found; static Effects Off evidence
  is not described as motion verification.
- The plan review does not activate or execute cycle 104. During eventual
  execution, close the cycle only after the criterion report establishes PASS
  or a specific BLOCKED disposition; a BLOCKED audit must name each unmet
  criterion and preserve all source histories unchanged.

## Verification and evidence paths

Read-only source inputs:

- `.codex/history/2026-10-01-094-tp3c-r-metric-reference-reconciliation.md`
- `.codex/history/2026-10-01-095-tp3c-baseline-installed-visual-comparison.md`
- `.codex/history/2026-10-01-096-baseline-correction-and-installed-acceptance.md`
- `.codex/history/2026-10-02-098-tp3c-hourly-reference-identity-and-acceptance.md`
- `.codex/history/2026-10-02-099-tp3c-details-reference-identity-and-acceptance.md`
- `.codex/history/2026-10-02-099-tp-3c-details-reference-identity-and-acceptance.md`
- `.codex/history/2026-10-02-100-tp3c-now-cases-and-full-baseline-gate.md`
- `.codex/history/2026-10-02-101-tp3d-responsive-state-regression.md`
- `.codex/history/2026-10-02-102-tp3ds-debug-sparse-launch-extra.md`
- `.codex/history/2026-10-02-103-tp3d-responsive-state-regression.md`
- Corresponding `.codex/test-artifacts/<cycle-id>/` directories, especially
  cycle 094's approved packet, cycle 095's comparison/handoff, cycle 096's
  matrix, cycles 098–100's recovery evidence, cycle 102's sparse fixture
  validation, and cycle 103's `matrix.csv`, `final-review.md`, validator, and
  RTL interaction evidence. For cycle 099, inspect both
  `.codex/test-artifacts/099-tp3c-details-reference-identity-and-acceptance/`
  and `.codex/test-artifacts/099-tp3c-details-reference-acceptance/` as
  separately identified source trees.
- `docs/theme-pack-roadmap.md` sections TP.3C, TP.3C recovery, TP.3D-S, TP.3D,
  and TP.3 exit; `docs/ROADMAP.md`'s theme-pack authority note.

New cycle evidence:

- `.codex/test-artifacts/104-tp3-theme-pack-exit-gate/inputs-and-hashes.md`
- `.codex/test-artifacts/104-tp3-theme-pack-exit-gate/criterion-matrix.md`
- `.codex/test-artifacts/104-tp3-theme-pack-exit-gate/gate-report.md`
- `.codex/test-artifacts/104-tp3-theme-pack-exit-gate/logs/` for exact
  validator, workflow, and diff-check commands/results.

Required final commands: invoke each validator in step 3 with Python from its
own cycle artifact root (preserve the exact command, stdout, stderr, and exit
status), then run `python scripts/dev.py workflow` and `git diff --check` from
the repository root. No app build/test or installed capture is expected
because this cycle changes no app/test code; record any validator that could
not run and why.

## Visual and accessibility conditions

This cycle creates no visual work or screenshots. It audits prior installed
visual evidence. Preserve each source case's recorded viewport, font scale,
locale/direction, contrast, layout, effects, device/API, and APK identity.
Cycle 103's matrix includes compact 360 × 640 dp, font scale 1.3, Effects Off,
RTL Hourly/Daily, and sparse missing-data captures across five themes; the
indexed baseline comparison is 393 × 852 dp, font scale 1.0, en-US/LTR,
Standard contrast. These conditions are not interchangeable. Service-level
TalkBack and temporal-motion verification remain explicit limitations unless
direct evidence is found.

## Risks and assumptions

- The owner has resolved the prior criterion-identity ambiguity: the recovery
  work and cycle 100's all-twenty PASS satisfy the baseline-correction and
  acceptance prerequisite. The roadmap records this while retaining cycle
  096's original BLOCKED outcome.
- Two 099 Details history entries use similar but distinct cycle IDs/paths.
  Retain both identities and establish their roles from the records and
  artifacts; do not merge them or assume one is canonical without evidence.
- Existing summaries may omit exact conditions, hashes, or validator output;
  inspect source artifacts and report missing evidence as unverified.
- All listed validators and source artifacts are assumed to remain available.
  If one is absent or no longer runnable, do not recreate its historical
  output.
- Read-only evidence validation and human review do not establish TalkBack
  service traversal or absence of temporal motion.
- The work is bounded to the TP.3 gate evidence named here. Inventory and
  inspect manifests, hashes, criterion records, logs, and representative
  screenshot/hierarchy evidence as needed to validate each claim; do not
  re-review unrelated TP.1/TP.2 work or re-perform visual comparisons already
  accepted by the recorded reviews.
- Context target: 20–30% of the available execution context, stop before 45%,
  and never exceed the roadmap's 65% ceiling. If artifact volume or a
  contradictory record threatens that bound, stop and report the unresolved
  criterion rather than broadening the audit. This is a planning target, not a
  measured token estimate; the executing agent must assess available context
  before beginning and report any needed split instead of silently exceeding
  the limit.

## Out of scope

- Production/test-code changes, visual polish, new corrections, builds,
  installations, screenshot capture, or capture infrastructure.
- Reopening or rewriting closed histories, changing the approved r4 packet,
  substituting unaccepted references, or retroactively changing cycle 096's
  disposition.
- TalkBack/service-level traversal or temporal-motion testing; report these
  as unverified if absent.
- Reopening TP.3D work, starting downstream R2.1 forecast work, or creating an
  automatic retry/follow-up slice.
- Declaring TP.3 complete without evidence closure against the current roadmap.
