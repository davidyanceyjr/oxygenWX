# Plan 167 — Windows clean-host build matrix

Status: Blocked (closed)
Cycle ID: 167-windows-clean-host-build-matrix
Roadmap item: R7.3A
Created: 2026-10-10

## Objective

Verify the Windows clean-clone developer flow at a recorded repository commit using the Windows Gradle wrapper through `scripts/dev.py`, and retain exact host, tooling, command, and outcome evidence. Close as passed only if the complete documented matrix succeeds; otherwise record the reproducible blocker and leave R7.3A unmet.

## Production boundary

Verification-only: one fresh Windows clone, repository developer entry point, `gradlew.bat`, and the installed Android SDK/toolchain. No production, build, or developer tooling edits are included.

## Functional invariants

- The clone is clean before execution and tied to an exact commit and tree.
- Every command runs from the clone root through `python scripts/dev.py`.
- Windows selection of `gradlew.bat` is exercised by the prescribed entry point.
- Individual command outcomes and environmental blockers are recorded accurately; a failed required command means the R7.3A exit remains unmet.
- Any source or tooling defect discovered is recorded for a separately planned repair; this cycle remains verification-only.

## Implementation steps

1. Record source commit/tree, clean status, Windows edition/version/architecture, Python, Java, Gradle wrapper, Android SDK packages, and relevant environment constraints (excluding secrets).
2. Create an isolated fresh clone at the recorded source commit and confirm its identity and clean status before running commands.
3. Run workflow, contract, test, build, and aggregate check through `python scripts/dev.py`, capturing separate logs and exit statuses and continuing after failures.
4. Store the run manifest, toolchain details, command logs/statuses, and aggregate outcome under `.codex/test-artifacts/167-windows-clean-host-build-matrix/`.
5. Compare results with R7.3A's exit criterion, run `git diff --check` on cycle-record changes, and close with exact outcome, evidence paths, limitations, and follow-up.

## Acceptance criteria

- A fresh Windows clone is demonstrably at the recorded commit/tree and clean before execution.
- The five documented commands (`workflow`, `contract`, `test`, `build`, and `check`) are run from that clone through `scripts/dev.py`.
- Evidence records Windows and toolchain details, exact commands, separate exit statuses, and the aggregate result.
- R7.3A is reported passed only if all required commands succeed. If a host, SDK, network, or repository issue prevents completion, record the exact blocker and close without claiming the roadmap item complete.

## Verification and evidence

Run from the fresh clone root:

```powershell
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py build
python scripts/dev.py check
```

Capture stdout/stderr and exit code for each command, clone HEAD/tree/status, Windows version/architecture, Python and Java versions, wrapper/Gradle version, SDK platforms/build-tools/platform-tools, and emulator/device availability. Record SDK environment paths without exposing credentials. Evidence path: `.codex/test-artifacts/167-windows-clean-host-build-matrix/`. Installed-app visual evidence is not applicable to this host-tooling matrix.

## Risks and assumptions

- A Windows host with Python, a supported JDK, Android SDK packages for compile SDK 37, and network access for Gradle dependencies is available.
- `scripts/dev.py` selects the Windows wrapper correctly; shell invocation and filesystem behavior may expose platform-specific failures.
- Results apply only to the recorded commit and Windows host configuration; they do not establish macOS behavior or installed-app acceptance.

## Out of scope

- Any source, wrapper, build, dependency, test, or developer-script repair; such changes require a separate bounded cycle.
- macOS clean-host matrix R7.3B, release build/signing preparation R7.4, release candidate gate R7.5, or release decision R7.5A.
- R7.2 legal/source follow-ups, provider/network behavior, and visual or accessibility verification.
