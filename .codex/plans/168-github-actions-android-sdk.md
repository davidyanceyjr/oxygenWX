# Plan 168 — Resolve GitHub Actions Android SDK package provisioning

Status: Completed
Cycle ID: 168-github-actions-android-sdk
Roadmap item: R7.3
Created: 2026-10-11

## Objective

Repair the Android SDK package identifiers used by PR #5's GitHub Actions workflow, keeping its checkout v7 and `platform-tools` setup, and prove the workflow requests packages available from the official stable SDK channel.

## Production boundary

Only `.github/workflows/android.yml` may change. Do not change `compileSdk`, `targetSdk`, build tools in Gradle, dependencies, or verification behavior. The local persistent `.codex` cycle records process and evidence but is not part of the PR change.

## Functional invariants

- Keep `actions/checkout@v7`.
- Keep `android-actions/setup-android@v3` with `packages: platform-tools`.
- Keep Android compile and target SDK 37 and the exact build-tools version unless package evidence requires otherwise.
- Keep `python3 scripts/dev.py check` enabled in CI.
- Do not use local absolute paths, copy SDK packages into the repository, or change checkout security settings.

## Implementation steps

1. Inspect the latest PR head and identify the actual local SDK root and installed package metadata.
2. Query official SDK package availability on the stable channel and distinguish it from preview-channel packages.
3. Update only the SDK installation command with exact available package identifiers and explicit stable channel selection.
4. Run `python scripts/dev.py check` against the actual local SDK, inspect the complete PR diff, and run `git diff --check`.
5. Push the fix to PR #5, inspect its resulting Actions run and mergeability, and close this cycle without claiming the full R7.3 clean-host matrix complete.

## Acceptance criteria

- The CI install command requests `platforms;android-37.0` and `build-tools;37.0.0` from stable channel 0.
- Checkout v7, `platform-tools`, Gradle configuration, and verification command are preserved.
- Local SDK metadata and official package-list evidence support the chosen identifiers.
- The full local `scripts/dev.py check` passes, `git diff --check` passes, and the final diff contains only the bounded workflow fix.
- The pushed PR remains open and is not merged; its new check run and current mergeability are reported accurately.

## Verification and evidence

- Capture concise local SDK package metadata and channel-0 availability under `.codex/test-artifacts/168-github-actions-android-sdk/`.
- Capture `python scripts/dev.py check`, final diff, and `git diff --check` results in the same cycle evidence directory.
- Inspect the GitHub Actions run triggered by the pushed commit and the latest PR mergeability.

## Risks and assumptions

- The installed `.android-sdk` root is a local development SDK; package metadata alone is not sufficient, so remote sdkmanager listings on an empty SDK root must confirm channel availability.
- If SDK 37 packages are unavailable from stable channel 0, stop before choosing another compile/target baseline because target SDK changes affect platform behavior.

## Out of scope

- Lowering `compileSdk` or `targetSdk`, changing application runtime behavior, upgrading dependencies or actions other than the requested SDK package correction, changing security settings, disabling checks, or merging PR #5.
- Completing the broader R7.3 clean-host build matrix.
