# Plan 158 — Typed appearance invariance (R6.4, first portion)

Status: Completed
Cycle ID: 158-reduced-motion-appearance-invariance
Roadmap item: R6.4
Created: 2026-10-08
Sequence: 1 of 3; followed by 158-reduced-motion-appearance-invariance-partial-A and -partial-B.
Evidence: .codex/test-artifacts/158-reduced-motion-appearance-invariance/

## Objective and observable outcome

Define one immutable, typed, appearance-independent semantic snapshot at the
presentation/UI test boundary. Prove with deterministic tests that the same
weather and navigation input has equal semantic snapshots for all 30 production
theme × contrast × effects combinations and under reduced motion. Verify
appearance-specific resolver properties separately, including Effects Off.
This portion establishes the contract for the later production flow; it does
not close the complete R6.4 roadmap exit.

## Authority and handoff

Use docs/SPECIFICATION.md, docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md,
docs/ROADMAP.md R6.4, and the preserved broad plan
158-reduced-motion-appearance-invariance-original.md. Cycle 156's failed
appearance-flow assertion and Cycles 143/145's reduced-motion evidence are
context, not acceptance evidence for this portion. Partial-A inherits the
snapshot contract and owns Activity fixture diagnosis, appearance-flow repair,
and request counters. Partial-B owns installed captures. Keep R6.4 open until
all three portions close with their actual evidence.

## Production boundary

Test boundary: canonical WeatherBundle fixture and typed derived/presentation
state, Home presentation models, theme resolver, reduced-motion policy, and
deterministic JVM tests. Keep snapshot construction test-only. A narrow
production presentation contract is allowed only if inspection proves an
existing typed value or control state cannot otherwise be observed; document
its owner and add a focused assertion. Do not change provider, repository,
cache, weather meaning, or appearance persistence behavior.

## Typed semantic snapshot contract

- Define immutable test-only data classes with explicit fields; do not put a
  WeatherBundle, DerivedWeather, HomePresentation, or ResolvedTheme object into
  the snapshot wholesale. Construct one fixed input from
  DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 23, 9, 0)),
  HistoricalSynthesis.derive(bundle), and HomePresentationMapper.map(bundle,
  derived). Use UnitPreset.METRIC and a fixed Live/UNKNOWN load status with no
  cache write, matching DeterministicCaptureFixtureTest. Require the mapped
  Home presentation and its 12 hourly and two daily windows to exist before
  projection; fail on missing fixture/typed state rather than recording an
  empty success.
- Snapshot weather meaning at the typed input/presentation boundary, not
  formatted or rendered output. Equality includes the complete canonical
  `CurrentWeather` field set (`observedAt`, condition, temperature, apparent
  temperature, dew point, humidity, pressure, wind speed/gust/direction, cloud
  cover, visibility, and precipitation amount); every field of each of the
  first 72 `HourWeather` values (`time`, condition, temperature, dew point,
  pressure, wind speed, precipitation probability/amount, and cloud cover) and
  first ten `DayWeather` values (`date`, condition, low/high temperature,
  precipitation probability/amount, wind gust, and sunshine hours); and all
  `DerivedWeather` outputs (seasonal percentile, temperature/pressure
  departures, three-hour pressure tendency and thermal momentum, persistence,
  texture, volatility, analog years, historical sample count, and hourly
  entries used). Preserve nullable values as nullable and carry canonical
  units in field names or value types (C, hPa, kph, mm, percent, km, hours,
  degrees). Preserve exact `LocalDateTime`/`LocalDate` identity and list order;
  do not round, convert through the selected display preset, normalize null to
  zero, or compare only values selected for a sample assertion. For the
  historical context shown in Details, also include normal temperature and
  pressure, baseline analog years, sample count, and reference-period
  identity. Individual historical samples are derivation inputs, not
  separately displayed facts; their effect is represented by derived outputs
  and sample count.
- Model navigation from the same 72-hour/ten-day mapped horizon as the UI:
  windows are consecutive chunks of six hourly entries and five daily entries,
  with a final short chunk retained and no padding. Snapshot page identity as
  a test-owned enum NOW, HOURLY, DAILY, DETAILS in that order, selected page,
  and zero-based requested hourly/daily window indices normalized by the UI
  rule (`coerceIn(0, lastIndex)` when nonempty). With no windows, the UI's
  effective selected index is zero and it exposes no Earlier/Later controls.
  With one or more windows, clamp the selected index into `0..lastIndex`.
  Earlier is enabled exactly when the effective index is greater than zero,
  and Later exactly when it is less
  than `lastIndex`. Their actions decrement/increment one window and cannot
  cross those boundaries. Cover empty, single-window, first, middle, last,
  and partial-final-window cases deterministically.
- Give page choices and navigation actions stable test-owned IDs, with
  selected/enabled/available state. Hourly date jumps are the mapper's ordered
  entries whose `windowIndex` is within the hourly window indices; preserve
  its one-entry-per-distinct-label result and do not invent a jump for a date
  with no window start. Their semantic identity is the canonical `LocalDate`
  of the first hourly entry in that window (`windowIndex * 6`), not the
  localized weekday label. A jump selects that window; it is selected exactly
  when its index equals the effective hourly index. Daily has no date-jump
  control. Omit invalid out-of-range mapper indices and assert valid jumps
  select the same window as the UI. Keep date-menu expansion and actual Compose
  pager state out of this portion: Partial-A must observe them from the
  Activity.
- Include `WeatherBundle.currentProvenance` and `forecastProvenance` (`DataType`,
  source ID, `validAt`, `retrievedAt`), plus the fixed load-state origin,
  freshness, and cache-write outcome. The demo bundle has no authoritative
  alert input, so record a typed alert state of not supplied by this fixture; never
  infer no official alerts. Partial-A must add authoritative alert state from
  its production input. Include `WeatherBundle.location.id` and time-zone ID,
  not its display name.
- Exclude colors, typography, dimensions, animation, screenshots, localized
  labels, and formatted display strings. Never parse rendered strings back
  into weather values.
- Keep mapper output outside snapshot equality except typed window counts,
  date-jump window indices, and typed field availability if useful for a
  separate mapping assertion. PresentationField.Available.text is still a
  formatted string and must not be copied into equality. Assert representative
  mapped visible facts and control/window counts separately so a constant
  snapshot built only from the fixture cannot falsely prove rendering.
- Hold fixture, units, page, date/window, and controls fixed across resolver
  comparisons. The matrix proves semantic inputs and test projection remain
  appearance independent; it does not prove Activity state survived a user
  appearance action. Preserve that stronger claim for Partial-A.

## Functional invariants

- Preserve Now -> Hourly -> Daily -> Details, visible named page identity,
  outer pager ownership, static-tap and Back behavior, and Hourly/Daily window
  semantics.
- Theme, contrast, effects, and reduced motion change presentation only.
  Values, chronology, units, missing states, provenance, freshness, alert
  meaning, and semantic controls stay equal.
- Effects Off remains opaque, static, and complete; reduced motion removes no
  required fact, control, or meaningful semantic state. Important facts remain
  visible text; decorative artwork stays supplemental.
- This test-only contract must not trigger weather fetches or alter production
  weather, location, alert, or preference behavior.

## Implementation steps

1. Reuse the fixed-anchor fixture and inspect the Home mapper's truncation,
   windowing, date jumps, and Detail fields. Identify exactly which facts are
   canonical, derived, typed load state, or display-only; document omissions.
2. Add test-only snapshot types and projection under app/src/test. Keep the
   fixed input and navigation selection explicit. Validate nonempty canonical
   data, expected mapper window counts, and window/date correspondence before
   comparing snapshots. Add at least one alternate selected window case and
   one missing-field case to check control boundaries and nullable semantics.
3. Assert the fixed input's exact typed anchor, a representative current
   value, first/last hourly and daily identities, both provenance types,
   freshness, and derived/reference classification. Separately assert key
   mapper-visible text and availability for those same facts.
4. Enumerate exactly five production themes x two contrast modes x three
   effects levels at STANDARD layout. For each cell resolve the theme, apply
   system animation scales 1f and 0f, and compare independently constructed
   semantic snapshots to the baseline. Assert 30 distinct cells and separately
   check resolved appearance identity and Effects Off solid/none/opaque/static
   policy. Reuse existing ThemeResolverTest and ReducedMotionPolicyTest
   assertions instead of duplicating their full policy suite.
5. Keep production code unchanged unless the preceding tests demonstrate an
   indispensable typed presentation seam. If so, record the exact missing
   value, owner, and focused regression assertion before editing that seam.
   Inspect the final diff and write the Partial-A handoff with observed and
   still Activity-only state clearly separated.

## Acceptance criteria

- The typed snapshot contract has a named test and excludes appearance and
  rendered fields by construction; no comparison reverse-parses display text.
- All 30 theme × contrast × effects combinations preserve the same semantic
  input's snapshot at standard motion and reduced motion; cell count is exact.
- Boundary-window and missing-field tests demonstrate that control and
  unavailable state are represented honestly. Separate mapper assertions
  establish representative visible correspondence without making text part of
  snapshot equality.
- Separate appearance assertions prove resolver/effects policy, including
  Effects Off opaque/static output.
- Any narrow production presentation change has a focused regression
  assertion and stays within the stated boundary.
- Handoff evidence identifies the fixture, unit policy, typed projection,
  and control IDs for Partial-A. R6.4 remains open.

## Verification and evidence

Run the new named snapshot JVM test, ThemeResolverTest,
ReducedMotionPolicyTest, DeterministicCaptureFixtureTest, and
HomePresentationTest with `./gradlew :app:testDebugUnitTest --tests
"com.oxygen.weather.<exact test class>"` selectors on this Linux host (use
`gradlew.bat` on Windows). Then run python scripts/dev.py
contract, python scripts/dev.py check, python scripts/dev.py workflow, and
git diff --check; inspect git diff. If a command is unavailable, record its
exact failure and boundary. Record commands, results, snapshot schema and
fixture/units, 30-cell matrix count, representative mapper assertions, and
Partial-A handoff in .codex/test-artifacts/158-reduced-motion-appearance-invariance/verification.md.
This is a deterministic contract slice: no installed viewport/font/RTL capture
is required here. Partial-B owns installed compact-profile, Effects Off and
system reduced-motion evidence; this portion makes no visual acceptance claim.

## Risks and assumptions

- This snapshot is an input contract, not a readback of Compose state. Page
  selection and control IDs are test-owned because HomePage is private and
  pager/window indices are rememberSaveable in OxygenWeatherApp. Partial-A
  must read actual Activity state/actions and compare it to this contract.
- HomePresentationMapper currently exposes formatted display strings for
  many facts. Canonical and derived values supply typed equality; separate
  mapping assertions reduce the risk of a tautological fixture comparison.
- The demo fixture has no authoritative alert record. The typed state must
  say that alert input was not supplied, not that no alerts exist.
- The resolver matrix cannot prove production appearance actions preserve
  state or network counts. Partial-A supplies that evidence.

## Context audit

Owner-directed three-part split on 2026-10-08. Conservative execution estimate:
20,000–35,000 combined input/generated tokens, including discovery,
implementation, debugging, checks, and evidence. Runtime context-window size
is unconfirmed; no percentage or 65% PASS is claimed. Confirm before
activation. The preserved broad plan and Partial-A/B retain all obligations.

## Out of scope

- Cycle 156's 28 °C Activity assertion diagnosis/repair, production
  appearance actions, transport/cache counts, and selected-location positive
  controls (Partial-A).
- Installed 20-cell Effects Off/reduced-motion capture and review (Partial-B).
- Simple layout and Settings matrix (R6.4A), RTL chronology (closed R6.3),
  TalkBack service review (R6.5), new themes or effects, unrelated redesign.
