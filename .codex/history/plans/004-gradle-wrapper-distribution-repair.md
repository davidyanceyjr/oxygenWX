# Plan 004 — Gradle wrapper distribution repair

Status: Completed
Cycle ID: 004-gradle-wrapper-distribution-repair
Roadmap item: R7.3
Created: 2026-09-20

## Objective

Restore the repository's normal Gradle-wrapper workflow by replacing the nonexistent
`gradle-9.6.0-bin.zip` distribution with the known-compatible Gradle 9.7.0 distribution and its
official SHA-256 checksum. The observable outcome is that `python scripts/dev.py test` and
`python scripts/dev.py check` bootstrap through `./gradlew` rather than failing with an HTTP 404.

## Production boundary

`gradle/wrapper/gradle-wrapper.properties`, a standard checked-in Gradle 9.7.0 wrapper JAR, and
the minimal launcher changes that remove unsupported network bootstrap behavior from `gradlew` and
`gradlew.bat`, plus this cycle's evidence and closure records. The wrapper is a prerequisite to
the clean-host verification goal in R7.3; this is not a broad build-tool upgrade.

## Functional invariants

- Preserve the existing launcher platforms, Java selection, wrapper invocation, and checksum
  verification. Replace the unsupported download-at-launch JAR bootstrap with a checked-in,
  checksum-verified standard wrapper JAR.
- Preserve the current Android Gradle Plugin, Kotlin, Java target, dependencies, source code, and
  application behavior.
- Retain checksum validation and use the official `services.gradle.org` distribution URL.
- Verify through the repository's `scripts/dev.py` entry point, not only an externally installed
  Gradle executable.

## Implementation steps

1. Confirm the checked-in distribution URL returns HTTP 404 and confirm Gradle 9.7.0 availability
   and checksum from the official distribution index.
2. Change the distribution URL/checksum, add the standard Gradle 9.7.0 wrapper JAR, and make the
   checked-in launcher scripts validate that JAR locally rather than download a versioned wrapper
   JAR endpoint.
3. Run `python scripts/dev.py test`, `python scripts/dev.py check`, and `git diff --check`; retain
   command output under `.codex/test-artifacts/004-gradle-wrapper-distribution-repair/`.

## Acceptance criteria

- The wrapper requests Gradle 9.7.0 and the launcher no longer depends on a versioned wrapper-JAR
  download endpoint.
- The replacement URL and SHA-256 match the official Gradle 9.7.0 binary distribution.
- `python scripts/dev.py test` and `python scripts/dev.py check` run through the wrapper and pass,
  or any independent environmental failure after successful bootstrap is recorded precisely.
- The final diff changes no application source or dependency/tool version other than the wrapper
  distribution reference.

## Verification and evidence

Evidence path: `.codex/test-artifacts/004-gradle-wrapper-distribution-repair/`.

Run:

```sh
python scripts/dev.py workflow
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

Record whether Gradle 9.7.0 was downloaded or used from its wrapper cache, the exact tasks run,
and any Android SDK or dependency-resolution limitation. No installed-app evidence is required.

## Risks and assumptions

- Gradle 9.7.0 was already used successfully for this repository's unit-test and lint tasks before
  this correction, but wrapper bootstrap remains the acceptance path.
- A changed distribution needs the matching checksum; retaining the prior checksum would cause the
  wrapper to reject the download.
- If a compatibility failure appears after successful wrapper bootstrap, stop rather than widening
  this maintenance slice into plugin or dependency upgrades.

## Out of scope

- Android Gradle Plugin, Kotlin, Java, SDK, dependency, or wrapper distribution upgrades beyond
  the compatible Gradle 9.7.0 repair.
- Changes to application code, tests, product documentation, or the R7.3 cross-platform matrix.
- Repairing unrelated network/proxy configuration outside this repository.
