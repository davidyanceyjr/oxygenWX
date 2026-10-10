# Plan 165 — Linux clean-clone developer build matrix

Status: Completed
Cycle ID: 165-linux-clean-clone-build-matrix
Roadmap item: R7.3
Created: 2026-10-10

## Objective

Prove the repository's documented Linux developer flow from a fresh clone of a
recorded repository commit. The clone must run the workflow, contract, test,
build, and aggregate check commands through `scripts/dev.py`, with exact host,
toolchain, command, and result evidence retained.

## Production boundary

Verification-only boundary: the repository source snapshot and Linux developer
tooling (`scripts/dev.py`, Gradle wrapper, and documented checks). This cycle
does not change production code, build configuration, or the developer tooling.

## Functional invariants

- The clean clone is tied to an exact commit and has no pre-existing worktree
  changes or generated build state.
- All five commands run from the clone root through `python scripts/dev.py`:
  `workflow`, `contract`, `test`, `build`, and `check`.
- The validation report distinguishes command success from failures and
  environment blockers; it does not treat a prior run in this working checkout
  as clean-clone evidence.
- The cycle remains verification-only. Any necessary source/tooling repair is
  recorded as a finding and requires a separately planned change before a
  rerun.

## Implementation steps

1. Record the source remote/branch where applicable, commit ID, tree ID, clean
   source status, Linux distribution/kernel, Python version, Java version,
   Android SDK/build-tools availability, and relevant environment constraints.
2. Create a new clone/worktree from the recorded commit in an isolated
   temporary path; confirm its HEAD and clean status before building.
3. From the clone root, run and capture separate logs/status for
   `python scripts/dev.py workflow`, `contract`, `test`, `build`, and `check`.
   Do not skip commands after a failure; record subsequent outcomes as well.
4. Preserve a concise run manifest, tool versions, command logs, and final
   outcome under `.codex/test-artifacts/165-linux-clean-clone-build-matrix/`.
   Keep temporary clone/build outputs outside the repository or remove them
   after evidence capture.
5. Compare results with the roadmap exit criterion and record any environment
   or source blocker without claiming R7.3 passed if any required command did
   not complete successfully.

## Acceptance criteria

- A fresh Linux clone is demonstrably at the recorded source commit and was
  clean before the run.
- The five documented `scripts/dev.py` commands all complete successfully in
  that clone.
- Archived evidence identifies the host and toolchain versions, exact commands,
  individual exit statuses, and the aggregate outcome.
- If any command fails or cannot run, the evidence records the exact failure
  and limitation; the cycle closes as blocked/unmet rather than changing source
  within this verification slice.

## Verification and evidence

Run, in the fresh clone:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py build
python scripts/dev.py check
```

Capture per-command stdout/stderr and exit code, clone commit/tree and clean
status, Linux distribution/kernel, Python/Java/Gradle/Android SDK versions, and
any relevant environment variables with secrets omitted. Evidence path:
`.codex/test-artifacts/165-linux-clean-clone-build-matrix/`. No installed-app
or visual evidence is applicable to this host-tooling verification slice.

## Risks and assumptions

- The host must have a usable JDK, Python, Android SDK, and required platform
  packages; missing or inaccessible dependencies may prevent the roadmap exit.
- Gradle dependency downloads may depend on network availability and remote
  repository state; record such failures separately from source/build failures.
- The current plan/state may be uncommitted when this plan is drafted. The
  tested clone must still use a recorded source commit; plan bookkeeping is
  preserved in this checkout's cycle evidence.
- The verification is specific to the recorded Linux host and commit and does
  not establish Windows or macOS behavior.

## Out of scope

- Repairing the wrapper, build files, scripts, dependencies, tests, or source;
  any fix requires a separate bounded cycle.
- Windows clean-host matrix (R7.3A), macOS clean-host matrix (R7.3B), signing
  and release configuration (R7.4), or the release-candidate gate (R7.5).
- Resolving the pending repository license decision or other R7.2 legal/source
  follow-ups; R7.3 is an independently ordered tooling verification item and
  does not clear or satisfy the R7.2 release blocker.
- Installed visual, accessibility-service, provider-network, or device/API
  verification.
