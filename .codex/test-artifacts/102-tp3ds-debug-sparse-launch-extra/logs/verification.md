# Cycle 102 verification

## Initial worktree state

Before implementation, the worktree already contained changes to
`.codex/current.md` and `docs/theme-pack-roadmap.md`, and untracked cycle 101
history/plan plus this cycle's plan. Those files were preserved as existing
work. The cycle began `PLANNED`; `python scripts/dev.py workflow` passed, and
`python scripts/codex_cycle.py activate` activated cycle
`102-tp3ds-debug-sparse-launch-extra` before production edits.

## Verification performed

- `python scripts/dev.py test` — PASS; 75 unit tests, 0 failures/errors/skips.
  `LaunchEffectsTest` includes sparse-extra requested, absent/false, and
  non-debuggable cases. The full Gradle XML is retained in the build output;
  the focused report is `launch-effects-unit-results.xml`.
- `python scripts/dev.py build` — PASS; debug APK assembled.
- `python scripts/dev.py android-test` — PASS on `oxygen_starter` API 37; 23
  tests, 0 failures/errors/skips. This includes
  `ProductionHomeSparseCompositionTest`. Full result XML is
  `android-test-results.xml`.
- `python scripts/dev.py check` — PASS; workflow validation, source contract,
  unit tests, lint, debug assembly, and its diff check all passed.
- `python scripts/dev.py contract` — PASS.
- `python scripts/dev.py workflow` — PASS; state ACTIVE before close.
- `JAVA_HOME=/usr/lib/jvm/java-27-openjdk PATH=/usr/lib/jvm/java-27-openjdk/bin:$PATH ANDROID_SDK_ROOT="$PWD/.android-sdk" ANDROID_HOME="$PWD/.android-sdk" ./gradlew --no-daemon :app:assembleRelease` — PASS; release assembly succeeded.
- The initial plain `./gradlew --no-daemon :app:assembleRelease` invocation
  failed before build because this shell selected JVM 8. Retrying with the JDK
  27 required by the repository workflow succeeded.
- Installed normal-app capture — PASS. The debug APK was installed on
  `oxygen_starter` (API 37) and launched with both
  `oxygen_deterministic_capture=true` and `oxygen_sparse_fixture=true`.
  Actual conditions: 393 × 852 dp, font scale 1.0, en-US, LTR, Atmospheric,
  Standard contrast, Subtle effects. The screenshot visually shows unavailable
  current values, partial hourly/daily horizons, and the named fixture with
  freshness unknown. The hierarchy preserves page identity and meaningful
  unavailable semantics. The installed APK SHA-256 matches the captured build
  APK. Exact identity, command, screenshot, and hierarchy are under `installed/`.
- `python .codex/test-artifacts/102-tp3ds-debug-sparse-launch-extra/validate_cycle.py` — PASS.
- Final `python scripts/dev.py contract`, `python scripts/dev.py workflow`,
  and `git diff --check` — PASS.

The emulator ran headless because its Qt window could not connect to the
available `:0` display in this shell; adb installation, normal activity
launch, screenshot, hierarchy collection, and instrumentation all worked.

## Scope and limitations

The installed capture demonstrates the sparse selection through the normal
`MainActivity` → derivation → presentation mapping → Home route. The existing
sparse composition test continues to assert visible missing-value behavior;
the new launch gate is unit-tested, and the installed route is checked by its
retained screenshot/hierarchy and validator. No additional Activity-specific
instrumentation test was added. This cycle does not run TP.3D's responsive
matrix and does not claim TP.3D or TP.3 completion. TalkBack service traversal
was not run.
