# History — 004-gradle-wrapper-distribution-repair

Status: Completed
Cycle ID: 004-gradle-wrapper-distribution-repair
Roadmap item: R7.3
Closed: 2026-09-20
Plan: .codex/plans/004-gradle-wrapper-distribution-repair.md
Evidence: .codex/test-artifacts/004-gradle-wrapper-distribution-repair/

## Outcome

Repaired the Gradle wrapper bootstrap by using the compatible Gradle 9.7.0 distribution, adding a checksum-verified standard wrapper JAR, and removing unsupported versioned wrapper-JAR downloads from both launchers.

## Verification

python scripts/dev.py workflow passed. With Java 26 and the project-local Android SDK, python scripts/dev.py test and python scripts/dev.py check passed through the repaired wrapper; check completed unit tests, lint, and debug APK assembly. git diff --check and sh -n gradlew passed.

## Limitations / not verified

This host requires JAVA_HOME to select Java 17 or later and ANDROID_SDK_ROOT to identify the installed Android SDK. Windows launcher behavior was changed equivalently but was not executed on Windows in this Linux environment.

## Follow-up

Run the R7.3 clean-host wrapper matrix on Windows, macOS, and Linux when release hardening is scheduled.
