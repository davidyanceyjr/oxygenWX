# Plan 018 — Unit conversion boundary

Status: Completed
Cycle ID: 018-unit-conversion-boundary
Roadmap item: R1.3
Created: 2026-09-21
Revised: 2026-09-21

## Objective

Add an additive, pure presentation API that converts and formats canonical
weather quantities for Metric, US, and UK presets. This slice establishes and
tests the boundary; it does not connect the preset to existing Home strings.
Canonical models and the currently rendered fixture remain unchanged. Applying
these functions to Home presentation mapping is R1.3A; persisting a user's
choice is R5.1.

The result of conversion is a presentation value with an explicit unit (or a
unit-bearing display value), not a mutation or copy of a canonical weather
record. The API must distinguish absolute temperature from temperature
difference: °C to °F absolute values use the offset, while changes/departures
use scale only.

## Preset contract

The product specification names Metric, US, and UK but leaves the precise units
to this implementation contract. Use this table:

| Quantity | Metric | US | UK |
| --- | --- | --- | --- |
| Absolute temperature | °C | °F | °C |
| Temperature difference / departure / rate | °C | °F | °C |
| Wind speed and gust | km/h | mph | mph |
| Pressure and pressure difference / tendency | hPa | inHg | hPa |
| Visibility | km | mi | mi |
| Precipitation amount and rate | mm | in | mm |
| Precipitation probability, humidity, cloud cover | % | % | % |
| Wind direction | degrees | degrees | degrees |
| Sunshine duration | h | h | h |

Use exact conversion constants: Fahrenheit `C * 9 / 5 + 32` for absolute
temperature; `C * 9 / 5` for temperature differences; mph `km/h / 1.609344`;
inHg `hPa / 33.8638866667`; miles `km / 1.609344`; inches `mm / 25.4`.
Metric and UK values retain their canonical numeric unit. Conversion must not
clamp values or infer a plausible value. Canonical inputs are finite by domain
contract; converter functions preserve null as unavailable.

## Production boundary

Production changes are limited to additive pure APIs under
`app/src/main/java/com/oxygen/weather/presentation/`. Do not change existing
function signatures, presentation data classes, `HomePresentationMapper`,
domain/derived models, fixture output, UI, or persisted settings. Existing
callers therefore keep their current behavior and source compatibility.

Update only:

- the R1.3 entry in `docs/ROADMAP.md`, to state exact conversion coverage and
  that mapping integration remains R1.3A;
- the unit-formatting paragraph in `docs/ARCHITECTURE.md`, to document the
  additive presentation boundary, absolute-versus-difference handling, and
  deterministic locale-independent numeric formatting.

Do not edit `docs/SPECIFICATION.md`: its release contract already calls for
Metric/US/UK and canonical values unchanged. Do not edit R1.3A or R5.1 scope.

This remains one slice, estimated below 25% of an agent context window and
under the roadmap's 45% limit. Keep implementation to one API file (or a
closely related pair only if Kotlin visibility/clarity requires it), one
focused test file, and the two named document updates. If a compatible API
cannot be added within that boundary, revise and split the plan before any
scope expansion.

## Functional invariants

- Canonical domain units and values remain provider-neutral, metric, nullable,
  and unchanged.
- All conversion occurs in presentation-owned code. Compose, repositories,
  provider DTOs, and preferences are not dependencies.
- Conversion accepts canonical values and a preset; formatting never parses a
  formatted string or modifies a canonical object.
- Null stays null/unavailable. Zero stays a real zero. Values are not clamped,
  substituted, or rounded before the explicit formatting step.
- Absolute temperature includes the Fahrenheit offset; temperature differences
  and rates do not. Pressure differences/tendencies convert as pressure
  differences with no additive offset.
- Equivalent quantities use the same preset units everywhere, including
  current/hourly/daily values and source-derived Details metrics. This is an
  API coverage requirement only; Home mapping stays untouched in this slice.
- Percentage, direction, and duration quantities keep their canonical numeric
  meaning and units in all presets.
- Numeric formatting is stable across device locale and timezone. Use
  `Locale.ROOT` for decimal formatting and do not consult the default timezone.
  Localized numeric formatting is not part of this slice.
- API is additive: no existing type constructor, mapper output, fixture, or
  application behavior changes.

## Required quantity coverage

Provide typed conversion/display functions covering the canonical and derived
numeric quantities presently modeled:

- absolute temperature: current temperature, apparent temperature, dew point,
  hourly temperature/dew point, and daily low/high;
- temperature difference: thermal momentum, thermal departure from normal, and
  any other signed temperature delta in Details;
- wind speed: current/hourly speed, current gust, and daily gust;
- pressure: current/hourly pressure, pressure departure, and pressure
  tendency;
- distance: current visibility;
- precipitation depth: current precipitation rate and hourly/daily amounts;
- unchanged-unit quantities: precipitation probability, relative humidity,
  cloud cover, wind direction, sunshine duration, and existing unitless
  indexes/percentiles.

The API does not need record-specific overloads for every field. It must use
dimensionally explicit value types/functions so an absolute temperature cannot
accidentally be formatted as a delta, or a pressure tendency as a temperature.
Do not convert temperature samples, historical normals, or provider models in
place; their scalar dimensions use the same pure functions when a later mapper
needs display values.

## Deterministic display policy

Define the display precision in one place and test exact strings:

- absolute temperatures: nearest whole degree, `°C` or `°F`;
- temperature differences/departures/rates: signed one decimal, degree unit;
- wind speed/gust: nearest whole unit, `km/h` or `mph`;
- pressure and pressure difference/tendency: one decimal, `hPa` or `inHg`;
- visibility and precipitation amount/rate: one decimal, `km`/`mi` and `mm`/`in`;
- percentage values: nearest whole percent, `%`;
- wind direction: nearest whole degree with `°` (no normalization or compass
  remapping at this boundary);
- sunshine duration: one decimal hour, `h`;
- unitless indexes/percentiles: retain integer value and current semantic
  labeling; do not invent physical units.

When an unsigned formatted value rounds to negative zero, display zero. For
signed differences, preserve an explicit sign, including a rounded negative
zero. This policy describes the new boundary API; existing UI formatting
remains untouched until R1.3A.

## Implementation steps

1. Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`.
   Inspect canonical and derived quantity types plus current mapper call sites.
   Confirm there are no existing unit APIs or public signatures to preserve
   beyond the additive requirement.
2. Add an explicit `UnitPreset` (Metric/US/UK) and typed quantity/display
   values in `presentation/`. Add pure nullable conversion functions for each
   physical dimension above. Keep absolute temperature and temperature delta
   functions distinct. Use named exact factors and a single deterministic
   formatting implementation using `Locale.ROOT`; avoid ambient locale,
   timezone, Android, Compose, or repository APIs.
3. Add unit tests with exact expected numeric conversions and exact formatted
   labels/strings. Cover every preset and dimension, including derived
   temperature and pressure deltas, plus all unchanged-unit quantities.
4. Update only the specified R1.3 text in `docs/ROADMAP.md` and the unit
   boundary paragraph in `docs/ARCHITECTURE.md`. Re-read both edits against
   this plan and the higher-authority specification.
5. Run focused tests, then `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, `python scripts/dev.py test`,
   `python scripts/dev.py check`, and `git diff --check`. Inspect the final
   diff. Record exact outcomes in
   `.codex/test-artifacts/018-unit-conversion-boundary/verification.md`.

## Tests

Add `app/src/test/java/com/oxygen/weather/presentation/WeatherUnitsTest.kt`
(or the matching focused test name if the API naming differs). Tests must cover:

- preset table outputs for absolute temperature, temperature delta, wind,
  pressure, pressure delta, visibility, precipitation depth/rate;
- reference values in each preset, including zero and negative temperatures,
  exact conversion-factor examples, and both signs for deltas;
- all unchanged units: probability, humidity, cloud cover, direction,
  sunshine duration, and unitless indexes/percentiles;
- null input preservation for every nullable converter and formatter;
- exact precision, rounding edges, signed output, negative-zero handling, and
  explicit unit labels;
- locale independence by temporarily switching the JVM default locale to one
  with a comma decimal separator, comparing output, and restoring it in a
  `finally` block; confirm that no timezone is needed by the numeric API;
- canonical values remain unchanged (conversion takes scalar/value inputs and
  returns presentation results without mutating or rewriting weather records).

## Acceptance criteria

Acceptance requires all of the following:

- APIs are additive and presentation-owned, with no change to current mapper
  results or fixture data.
- Every quantity in the coverage list has an explicit unit and a deterministic
  formatter or is explicitly unitless.
- Absolute and delta temperature semantics are distinct; pressure deltas use
  the same factor as pressure with no offset.
- Nulls, signs, rounding, and exact labels satisfy the tested display policy.
- The focused test and repository verification commands above pass, or any
  unavailable command and reason is recorded accurately in the evidence file.
- Final `git diff --check` passes. No visual install is required because no
  rendering changes are in scope.
- R1.3 is closed only with actual verification outcomes and limitations in the
  cycle history; this plan alone does not mark the roadmap item complete.

## Verification and evidence

Run and record the actual result of each command below; do not infer success
from another command or from prior cycle evidence:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

Run focused converter tests first while iterating. Preserve exact commands,
test counts, outcomes, and unavailable-command reasons in
`.codex/test-artifacts/018-unit-conversion-boundary/verification.md`. No
emulator install or screenshot is required because this slice does not change
rendering. Close R1.3 into history only after recording the checks actually
performed and any remaining verification boundary.

## Risks and assumptions

- The unit table is an implementation choice under the specification's
  Metric/US/UK labels. UK uses Celsius, mph, hPa, km visibility, and mm rain;
  US uses Fahrenheit, mph, inHg, miles, and inches. This is fixed in this plan
  to avoid implementation-time ambiguity.
- Absolute and differential temperature conversions differ by the 32-degree
  offset; using a single generic temperature conversion for Details would be
  wrong.
- Locale-independent strings use a decimal point. User-locale formatting can
  be added later through an explicit locale parameter without using ambient
  device state.
- This cycle creates reusable APIs but does not claim that current screens are
  unit-aware until R1.3A integrates them.

## Out of scope

- Applying presets to `HomePresentationMapper`, visible strings, spoken
  summaries, or Compose; that is R1.3A.
- Unit preference selection, persistence, defaults, or settings UI; that is
  R5.1.
- Changes to canonical/derived models, constructors, values, provider inputs,
  fixture output, cache/storage, or repository behavior.
- Localized formatting, timezone/date formatting, or user-facing text changes.
- Location, alerts, freshness, navigation, layout/theme/effects, or visual
  redesign.
- Live networking, emulator screenshots, or service-level accessibility
  verification.
