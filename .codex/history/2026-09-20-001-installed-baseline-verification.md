# History — 001-installed-baseline-verification

Status: Completed
Cycle ID: 001-installed-baseline-verification
Roadmap item: R0.2
Closed: 2026-09-20
Plan: .codex/plans/001-installed-baseline-verification.md
Evidence: .codex/test-artifacts/001-installed-baseline-verification/

## Outcome

Installed and verified the replacement Now → Hourly → Daily → Details UI on the oxygen_starter Android emulator at the compact 360x640 viewport. Fixed the bounded AGP/Kotlin build incompatibility, the page-tab semantics compile issue, and large-font clipping/overlap by allowing content to grow and scroll within the existing UI boundary.

## Verification

Source contract passed. Direct Gradle :app:testDebugUnitTest, :app:lintDebug, and :app:assembleDebug passed using cached Gradle 9.7.0, Java 26, and /home/opsman/project_git/oxygen/.android-sdk. APK installed and launched as com.oxygen.weather/.MainActivity on emulator-5554. Captured Now, Hourly, Hourly Later, Hourly date jump, Daily, Daily Later, Details, Details bottom, and large-font screenshots under .codex/test-artifacts/001-installed-baseline-verification/. UI hierarchy exposed all four page identities and forecast-entry semantics. Back verified Details → Daily → Hourly → Now, then launcher from Now. git diff --check passed.

## Limitations / not verified

python scripts/dev.py test/check could not use the repository wrapper because its Gradle 9.6.0 wrapper bootstrap URL returned HTTP 404; equivalent direct Gradle checks passed. TalkBack/service-level speech verification was not run. The emulator/AVD is reused from /home/opsman/project_git/oxygen rather than copied into this repository.

## Follow-up

Select the next roadmap slice deliberately from docs/ROADMAP.md; R0.3 Appearance-off baseline is the next recommended item.
