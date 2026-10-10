# Plan 166 — Linux clean-clone build matrix completion

Status: Completed
Cycle ID: 166-linux-clean-clone-build-matrix-completion
Roadmap item: R7.3
Created: 2026-10-10

## Objective

Complete the R7.3 Linux clean-clone matrix on the previously recorded source commit, supplying the installed Android SDK through `ANDROID_SDK_ROOT` so test, build, and aggregate check can run.

## Production boundary

Verification-only: one fresh clone of source commit `a6c01ce4c4cf1a58f82d635aab154ecad112b9e0`, developer entry point, Gradle wrapper, and installed SDK. No production, build, or developer tooling edits.

## Functional invariants

- The tested clone is clean before execution and matches the recorded commit/tree.
- All five documented commands run from the clone root through `python scripts/dev.py`.
- The SDK path is supplied explicitly from this checkout's `.android-sdk/`; SDK binaries and generated build state remain outside the clone.
- Command outcomes and any environment blockers are recorded accurately. A failed command means R7.3 remains unmet.
- This cycle only completes the verification left unmet by cycle 165; no source repair or visual/device acceptance work is included.

## Implementation steps

1. Record source identity, clean source status, Linux/toolchain details, SDK packages, SDK path, emulator availability, and relevant environment constraints.
2. Create an isolated fresh clone at the recorded source commit and confirm clean status and commit/tree identity.
3. Set `ANDROID_SDK_ROOT` and `ANDROID_HOME` to the installed `.android-sdk/` for every command; run and capture separate logs and statuses for workflow, contract, test, build, and check, continuing after failures.
4. Store manifest, command logs, statuses, and aggregate outcome under `.codex/test-artifacts/166-linux-clean-clone-build-matrix-completion/`.
5. Run `git diff --check`, inspect cycle changes, and close with exact results, evidence paths, limitations, and follow-up.

## Acceptance criteria

- A fresh clone demonstrably matches the recorded commit and was clean before execution.
- All five `scripts/dev.py` commands complete successfully with the SDK path supplied.
- Evidence identifies host, Python/JDK/Gradle/SDK versions, exact commands, separate exit codes, and aggregate result.
- If any command fails, record the exact blocker and close without claiming R7.3 passed.

## Verification and evidence

Run these clone-root commands with `ANDROID_SDK_ROOT=/home/opsman/project_git/oxygenWX/.android-sdk` and `ANDROID_HOME` set to the same path:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py build
python scripts/dev.py check
```

Capture command stdout/stderr separately, exit statuses, clone HEAD/tree/status, Linux distribution/kernel, Python, selected Java, Gradle wrapper, SDK platforms/build-tools/platform-tools, and emulator/device availability. Evidence path: `.codex/test-artifacts/166-linux-clean-clone-build-matrix-completion/`. No visual evidence is required for this host tooling matrix.

## Risks and assumptions

- The repository-local `.android-sdk/` exists and contains packages needed by compile SDK 37; access from the temporary clone is via environment path, not copied SDK files.
- Gradle dependencies may need network access and can fail independently of SDK setup.
- This matrix is specific to the recorded commit and Linux host; it does not establish Windows or macOS behavior.

## Out of scope

- Source, wrapper, build configuration, developer script, test, or dependency repair.
- Windows R7.3A, macOS R7.3B, release signing R7.4, or release gate R7.5.
- R7.2 legal/source follow-ups and installed-app visual/accessibility/provider verification.
