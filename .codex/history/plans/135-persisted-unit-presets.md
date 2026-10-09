# Plan 135 — Persisted unit presets

Status: Completed
Cycle ID: 135-persisted-unit-presets
Roadmap item: R5.1
Created: 2026-10-06

## Objective

Add a small application preference boundary for the existing `UnitPreset`
(`METRIC`, `US`, `UK`) and persist the selected value locally. A new store
instance must restore the selected preset after recreation/relaunch, with
Metric as the defined default when no valid choice is stored. This cycle owns
choice state and persistence only; R5.1A applies the restored choice to Home
presentation.

## Production boundary

Add a provider-neutral `UnitPresetStore` contract in `application/` and an
Android `SharedPreferencesUnitPresetStore` adapter. Keep `UnitPreset` in its
existing presentation package as the canonical set of choices. The adapter
accepts `Context` for production construction and an internal injectable
preferences seam for deterministic tests, following existing SharedPreferences
store patterns. Its public `Context` constructor is the production access point
for R5.1A; do not wire an unused store into `MainActivity` or add a separate
factory/general preferences framework.

No Compose rendering or settings destination is needed for this storage slice.
Do not thread the preference into `HomePresentationMapper` or alter the
currently rendered Metric output; R5.1A owns applying the choice across Home.

## Functional invariants

- The only persisted values are the existing Metric, US, and UK preset IDs.
- An absent or unrecognized stored value restores Metric; invalid persisted
  data is not interpreted as a different preset.
- The read result distinguishes a valid stored preset, a defaulted value
  (absent or unrecognized identifier), and a storage failure. A storage failure
  must not be reported as a successful restoration of the stored value.
- Saving then creating a fresh store over the same backing preferences restores
  the saved preset, modeling process recreation/relaunch.
- A persistence failure is represented honestly by the write result and never
  reported as a successful save.
- Preference reads/writes do not mutate canonical weather, derived values, or
  normalized forecast cache content and do not trigger a forecast/alert fetch.
- Existing presentation callers continue to use their current Metric default
  until R5.1A connects the restored choice to presentation mapping.
- Unit meaning remains defined by `WeatherUnits`; persistence does not add
  locale-based inference or conversion policy.

## Implementation steps

1. Follow existing `application/` store contracts and
   `SharedPreferencesSavedLocationStore`'s injected preference seam. Define
   `UnitPresetStore.read()` with explicit `Found(preset)`, `Defaulted`, and
   `Failure` outcomes, and `save(preset)` with explicit success/failure. Map
   `Defaulted` to Metric at the adapter boundary; reserve `Failure` for storage
   exceptions so callers can distinguish unavailable storage from a valid
   default.
2. Implement the adapter with a dedicated stable versioned namespace and one
   key. Persist only exact `UnitPreset.name` identifiers; use synchronous
   `commit()` and return failure on false or exception, matching the existing
   local store durability/result pattern. Do not add clear, migration, or
   observation APIs because this slice has no consumer requiring them.
3. Use the adapter's public `Context` constructor as the production access
   point. Keep construction independent of forecast/alert composition; do not
   initialize it in `MainActivity` solely for future use.
4. Add deterministic contract/adapter tests for all three presets, absent and
   unknown identifiers, read exception, write false/exception, and restoration
   by a new adapter over shared preferences. Snapshot a representative
   canonical fixture/cache value before and after store operations to verify
   structural equality. No weather-request assertion is required because the
   store boundary has no weather composition or network dependency; verify by
   dependency boundary that it cannot issue requests.
5. Run focused store tests, the full unit suite, contract/workflow checks, and
   repository check when the Android environment permits. Record exact
   commands/results and any environment limitation under the cycle evidence
   path.

## Acceptance criteria

- Metric, US, and UK can each be saved and restored through a fresh store
  instance over the same backing preferences.
- Missing and unsupported stored identifiers resolve to Metric and do not
  fabricate another choice.
- Read distinguishes stored, defaulted, and failed states; write failure is
  distinguishable from success.
- Tests prove preference storage leaves representative canonical weather/cache
  values structurally unchanged. The store contract and dependency graph have
  no weather-request capability or dependency.
- Existing `HomePresentationMapper` default behavior remains Metric, and no
  rendered unit output changes in this cycle.
- Implementation stays within the preference model/store/adapter and minimal
  composition boundary; no settings UI or Home application is included.

## Verification and evidence

Focused verification should cover the unit-preset store contract and Android
adapter using the repository's existing test seams. Run:

```sh
./gradlew :app:testDebugUnitTest --tests com.oxygen.weather.application.UnitPresetStoreTest --tests com.oxygen.weather.SharedPreferencesUnitPresetStoreTest
```

On Windows, use the same task and filters with `gradlew.bat`. Then run:

1. `python scripts/dev.py test` for the full deterministic unit suite.
2. `python scripts/dev.py contract` for architecture/data boundary checks.
3. `python scripts/dev.py check` when Android SDK and dependencies are
   available.
4. `python scripts/dev.py workflow` and `git diff --check`.

This slice does not change rendered UI, so installed visual captures are not
required. Preserve focused command output and environment limitations in
`.codex/test-artifacts/135-persisted-unit-presets/`. The closeout must state
which restore/failure cases were exercised and any unverified platform
boundary.

## Risks and assumptions

- `UnitPreset` already exists in the presentation package and is the stable
  choice set; moving or redefining it would exceed this slice.
- A dedicated namespace `unit_preset_v1` and key `preset` avoids
  collisions with selected-location and saved-location storage. The adapter
  uses the existing injectable SharedPreferences seam pattern for deterministic
  tests.
- The later `R5.1A` application slice may need a state observation mechanism.
  The later slice can choose its state owner; this plan exposes only read/save
  and does not preemptively implement UI/application state beyond persistence.
- R4.2A roadmap status was reconciled to DONE from cycle 130's completed
  history evidence before selecting R5.1 as the next eligible item.

## Out of scope

- Applying the selected preset to Now, Hourly, Daily, or Details; this is R5.1A.
- Settings navigation, controls, labels, selection semantics, or visual design;
  these belong to later R5.6 settings work.
- Changing `WeatherUnits`, canonical units, provider mappings, forecast cache
  schema, weather data, alerts, or fetch behavior.
- Theme, contrast, effects, layout preferences, or general settings framework.
- Locale/region-based automatic preset selection, cloud sync, or migration of
  unrelated preferences.
