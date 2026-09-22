# Plan 018 — Unit conversion boundary

Status: Active
Cycle ID: 018-unit-conversion-boundary
Roadmap item: R1.3
Created: 2026-09-21

## Objective

Add a pure, deterministic presentation unit boundary for the Metric, US, and UK
presets. Canonical weather values remain metric and unchanged; each supported
quantity can be converted and formatted without duplicating formulas in Home
mapping or Compose. This cycle provides tested conversion/formatting functions
only. Applying the selected preset to Home presentation models is R1.3A.

Preset units for this slice:

| Quantity | Metric | US | UK |
| --- | --- | --- | --- |
| Temperature | °C | °F | °C |
| Wind speed and gust | km/h | mph | mph |
| Pressure | hPa | inHg | hPa |
| Visibility | km | mi | mi |
| Precipitation amount/rate | mm | in | mm |

Precipitation probability, relative humidity, cloud cover, and wind direction
remain percent/degree values; sunshine duration remains hours. The formatter
must label these consistently without applying a false conversion.

## Production boundary

Production changes are limited to pure conversion and formatting APIs under
`app/src/main/java/com/oxygen/weather/presentation/`. Deterministic unit tests
may be added under `app/src/test/`. The converter accepts canonical values and
a unit preset, returns a typed quantity/display value, and has no Android,
locale-device, repository, Compose, or preference-storage dependency.

Update only the R1.3 description in `docs/ROADMAP.md` and the relevant unit
boundary paragraph in `docs/ARCHITECTURE.md`. Do not modify canonical model
types or their constructors.

This is a single bounded slice expected to remain below 20% of the context
window, safely under the repository's 45% ceiling. If the supported quantity
set or API requires work beyond pure conversion/formatting and deterministic
tests, stop and split the additional work before crossing this boundary.

## Functional invariants

- Canonical values remain provider-neutral, metric, nullable, and unchanged by
  unit selection.
- Conversion is applied only at the presentation boundary. UI mapping,
  preference selection/persistence, and Compose rendering do not change here.
- Missing values remain absent; no zero or placeholder is introduced by
  conversion or formatting.
- Probability, humidity, cloud cover, wind direction, and sunshine duration
  keep their existing meanings and canonical units.
- Unit choice changes only numeric display and unit labels. It does not alter
  weather meaning, provenance, source/update/valid time, freshness, or
  navigation.
- Repeated conversion of the same canonical input and preset returns the same
  result, independent of device locale/timezone.

## Implementation steps

1. Run workflow/contract checks and inspect the canonical quantity fields and
   existing presentation formatting. Confirm the preset table above against
   product authority; record any necessary correction in the plan before code.
2. Add a small presentation-owned preset/value model and pure conversion
   functions for temperature, speed, pressure, distance, and precipitation.
   Add unit-bearing formatter functions for those converted quantities and
   unchanged percent, direction, and duration quantities used by the current
   model. Keep conversion separate from `HomePresentationMapper`.
3. Add deterministic tests for reference values in every preset, negative and
   boundary temperatures, nullable inputs, precision/rounding, explicit unit
   labels, unchanged canonical inputs, and locale independence.
4. Run the focused converter tests, then `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, `python scripts/dev.py test`,
   `python scripts/dev.py check`, and `git diff --check`. Inspect the final diff
   and preserve outcomes under
   `.codex/test-artifacts/018-unit-conversion-boundary/`.

## Acceptance criteria

- Pure converter/formatter functions cover each canonical quantity used by
  current, hourly, daily, and Details presentation, including quantities that
  remain unchanged between presets.
- Metric, US, and UK outputs match the preset table and use deterministic
  precision, rounding, and unit labels.
- Null inputs remain unavailable and never become numeric zero.
- Unit tests cover conversions, unchanged values, negative temperatures,
  precision boundaries, and stable output independent of device locale.
- Canonical domain models, Home mapping behavior, fixture output, Compose,
  refresh state, and navigation remain unchanged.
- R1.3 is recorded complete only with actual test/check outcomes and any
  unverified boundary in the cycle history.

## Verification and evidence

Focused unit tests cover conversion reference points and formatting for each
preset and quantity class. Minimum completion commands:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

Visual installation is not required: this slice does not change rendering.
Record exact test counts/results and any unavailable commands in
`.codex/test-artifacts/018-unit-conversion-boundary/verification.md`. Do not
claim RTL or TalkBack verification.

## Risks and assumptions

- Product authority names Metric, US, and UK presets but does not enumerate
  their units. The table above is the initial contract and must be confirmed
  against existing repository policy before implementation; do not silently
  choose alternate UK pressure/rain units.
- Formatting precision should preserve useful information across conversions;
  tests must make rounding behavior explicit rather than rely on ambient locale.
- R1.3A owns applying this boundary across current/hourly/daily/Details mapping
  and testing missing presentation values there.

## Out of scope

- Applying unit selection to `HomePresentationMapper` or Compose; this is R1.3A.
- Unit preference controls, persistence, defaults, or settings; this is R5.1.
- Changes to canonical model values, providers, cache/storage, or repository
  data.
- Location, alerts, freshness, time-zone formatting, derived meteorology,
  navigation, layout/theme/effects, or visual redesign.
- Live networking, emulator screenshots, and service-level accessibility
  verification.
