# Cycle 120 verification

Date: 2026-10-04

## Checks

- `python scripts/dev.py workflow` — passed while the cycle was ACTIVE.
- Focused `ProductionForecastCompositionTest` JVM test — passed; deterministic
  request, source, result, and safe failure behavior are recorded in
  `focused-flow.log`.
- Focused `ProductionForecastCompositionFlowTest` installed Activity test —
  passed, including exact location coordinates, coverage and field set,
  Open-Meteo attribution, valid-time display, and selected failure with no
  fixture weather. Latest output: `focused-flow.log`.
- Focused `ManualLocationSearchFlowTest` regression — passed with the selected
  candidate's forecast failure shown as unavailable rather than fixture data;
  output: `manual-search-regression.log`.
- `python scripts/dev.py check` — passed (unit tests, Android build, lint, and
  source contract); output: `check.log`.
- `python scripts/dev.py contract` — passed.
- `python scripts/dev.py android-test` — passed on the local emulator:
  32 tests, 0 failures, 0 ignored; output: `android-test.log`.
- `python scripts/dev.py workflow` — passed after completion changes.
- `git diff --check` — passed.

## Installed visual evidence

The `oxygen_starter` AVD was Android 17 (`google/sdk_gphone64_x86_64`), LTR,
font scale 1.0. Its physical panel is 1080×2400 at density 420; the test
profile override is 360×640 pixels at density 160, which yields the required
360×640 dp viewport. The exact device fingerprint and display settings are in
`emulator-fingerprint.txt` and `emulator-display.txt`.

The installed Activity success capture `selected-live-now.png` shows Springfield
with the deterministic live result. `selected-live-provenance.png` shows
Open-Meteo and the result's forecast valid time. `selected-failure-no-fixture.png`
shows Springfield with unavailable weather and an explicit source error, with
no fixture facts presented under that location. These captures were visually
inspected after the tests.

## Scope and limitations

The production composition is Open-Meteo only per the owner decision in
`owner-decision.md`. MET Norway fallback remains unconfigured until a separate
slice implements and verifies its response-cache/conditional-request policy
and usable identifying contact metadata. The selected location remains
transient; persistence and restoration belong to R3.2. Provider terms and
request requirements reviewed for this decision are summarized in
`provider-documentation.md` with the source references copied into this
evidence directory.

The full suite used the recorded compact emulator profile because the initial
emulator's default pixel-density mapping caused pre-existing showcase capture
assertions to compare dp-derived dimensions against physical pixels. With the
explicit 360×640 dp profile, all showcase assertions and all 32 tests passed.
Large-font and RTL screenshots were not required for this slice; the visual
change was checked in compact LTR and shared app regression coverage passed.
