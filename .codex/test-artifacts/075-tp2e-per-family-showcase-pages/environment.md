# Installed environment — cycle 075

- Date/time: 2026-09-28 local (America/Chicago); successful installed run completed at approximately 20:18; emulator reports Android 17 / API 37.
- AVD: `oxygen_tp2b_api37`; serial `emulator-5554`; model `sdk_gphone64_x86_64`.
- Android emulator: 37.1.11.0 (build 15917651); SDK platform `android-37.0`, image `google_apis/x86_64`; build tools 36.0.0.
- Display: physical 320 x 640 px, `wm size` override 360 x 640 px; density 160 dpi; host baseline 360 x 640 dp; font scale 1.0.
- Test conditions: LTR, Standard contrast, Subtle effects.
- JDK: OpenJDK 27 at `/usr/lib/jvm/java-27-openjdk`; Gradle wrapper 9.7.0.
- Android Debug Bridge: 1.0.41, platform-tools 37.0.0-android-tools.
- Repository starting commit: `67ae415`.
- Prerequisite: cycle 071 TP.2E installed shared-component showcase is BLOCKED for the composite stack fit gate; its history is `.codex/history/2026-09-28-071-tp2e-installed-shared-component-showcase.md`. Cycle 075 supersedes that composite with six independent test-only pages as directed by the owner.
- Default `adb devices -l` initially showed no devices. The repository API 37 AVD was started with `.android-sdk/emulator/emulator -avd oxygen_tp2b_api37 -no-window -no-audio -no-boot-anim -gpu swiftshader_indirect`, then confirmed connected and configured to the required display/font conditions.
- Installed application: `com.oxygen.weather`, version `1.0.0-alpha01`.
- Installed debug APK after the instrumentation build: SHA-256 `669ad8bca9cd5cc071c37a388f09ad7ded10236cf72068f35de14dfbf58cce40` (same identity as the recorded prior build; production sources/resources were unchanged).
- Screenshot export: 30 final PNGs and the runtime manifest were pulled from `/sdcard/Download/oxygen-weather-tp2e-075/`. Five duplicate page-identity PNGs from the initial assertion-mismatch attempt are retained in `installed/raw/` for traceability; `installed/png/` contains the five captures from the successful rerun, selected by matching their SHA-256 values to the runtime manifest.
