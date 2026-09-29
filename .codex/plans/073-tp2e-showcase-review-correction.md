# Plan 073 — TP.2E ten-case review and bounded correction

Status: Planned
Cycle ID: 073-tp2e-showcase-review-correction
Roadmap item: TP.2E-partial3
Depends on: TP.2E-partial2 PASS in cycle 072

**Difficulty: 5/10.** The ten installed captures and direct tests already
exist. Risk is limited to a contract-based visual review and, only if evidence
requires it, one tightly bounded correction and recapture pass.

**Context budget:** target at most 30% of a fresh context window; stop before
45%. This slice owns qualitative comparison and the single correction
allowance only. Repository-wide closure checks belong to partial4.

## Objective

Review all ten installed composites from cycles 071 and 072 against the
approved shared-component contracts and the fixed showcase objective. If a
reproducible contract defect is demonstrated, make at most one correction pass
in `ProductionMonitorComponents.kt`, `ProductionDetailsComponents.kt`, or
`ProductionWeatherVisuals.kt`, then rerun affected focused checks and recapture
affected cases. Do not change APIs, resolver/catalog/tokens, D29 mappings,
weather/presentation meaning, or the test fixture.

## Production boundary

- A production correction is permitted only in
  `ProductionMonitorComponents.kt`, `ProductionDetailsComponents.kt`, or
  `ProductionWeatherVisuals.kt`, only for a reproducible defect against an
  existing approved contract, and only once.
- Test assertions/fixtures may change only as required to reproduce or verify
  the demonstrated defect. No unrelated cleanup or restyling is in scope.
- Start only after cycles 071 and 072 close PASS.

## Functional invariants

All ten cases retain the same supplied facts, unavailable behavior, source and
freshness distinction, hierarchy, chronology, semantics, callbacks, and
appearance-only variation. Any accepted correction preserves those contracts.

## Implementation steps

1. Verify both prerequisite PASS histories, all ten final captures, condition
   manifest, and direct assertion results. Confirm the installed device/build
   identity and record the reviewed file hashes.
2. Review each theme at Subtle and Effects Off against the approved component
   contracts: six-group presence and legibility; stable hierarchy and facts;
   unavailable/source/details truthfulness; named and usable controls; 48 dp
   applicable targets; decorative, non-intercepting marks/backdrops; Effects
   Off opaque, static, complete behavior. Record a ten-row matrix with a
   finding and evidence location for each case.
3. Treat a correction as eligible only when the finding names an existing
   approved contract, reproduces on the installed app, and is fixable within
   the three allowed source files. Record before evidence before editing.
   Preference-only observations or unsupported reference comparisons do not
   authorize changes.
4. If eligible, make one focused correction pass. Rerun the affected focused
   JVM and instrumentation assertions, recapture all affected theme/effects
   cases, and record before/after hashes and disposition. Do not iterate a
   second time. If the issue needs a prohibited file/API/meaning change or
   remains material after that pass, close BLOCKED.
5. If no eligible defect exists, make no production change and record that
   review result with the ten-case matrix.
6. Update this plan, `.codex/current.md`, the TP.2E source entry in
   `docs/theme-pack-roadmap.md`, and cycle 073 evidence/history. On PASS,
   identify partial4 as the sole next eligible slice. Do not claim TP.2E
   complete here.

## Acceptance criteria

All ten captures have a documented contract-based disposition. Either no
correction was warranted, or one correction pass has focused verification and
before/after evidence. No material in-scope deviation remains. A missing
prerequisite, unsupported required fix, failed test, or unresolved material
defect closes BLOCKED and stops partial4.

## Verification and evidence

Evidence path:
`.codex/test-artifacts/073-tp2e-showcase-review-correction/`. Retain the
ten-case review matrix, reviewed image hashes, before/after evidence if
corrected, exact focused outputs, environment/build identity, final diff
review, and unverified boundaries.

## Risks and assumptions

- Cycles 071 and 072 provide valid installed images and matching actual
  condition manifests.
- Review findings can be judged against existing written component
  contracts. An aesthetic preference without a contract is not a defect.
- A necessary fix outside the three named files is a blocker, not a reason to
  widen this plan.

## Out of scope

Repository-wide `test`, `build`, `contract`, `workflow`, and `check`; new
showcase families or fixtures; second correction pass; broad restyling; page
composition/pixel parity; Full effects; large-font/RTL/High contrast matrix;
TalkBack service traversal; provider behavior; TP.2E/TP.3/release acceptance.
