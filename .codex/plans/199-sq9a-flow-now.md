# Plan 199 — Remove the Now data enclosure

Status: Completed
**Cycle ID:** `199-sq9a-flow-now`
**Roadmap item:** `SQ.9A-FLOW-NOW`
**Created:** 2026-10-15
**Evidence root:** `.codex/test-artifacts/199-sq9a-flow-now/`

## Objective and observable outcome

On Glass/Smoked Quartz at Effects Full, remove the data-only enclosure around
Now's four supporting facts. Keep Humidity, Dew point, Precipitation, and Wind
visibly grouped through spacing and text hierarchy over the accepted shared
flow-field backdrop. Each label, value, and available supporting line must
remain legible and associated at compact width and large font scale. Other
themes and every other theme/effects-level combination retain their current
presentation.

The observable outcome is the installed Now page on the connected Pixel 5 at the
specified viewport and settings, backed by focused composition/semantics
checks. No weather or navigation behavior changes.

## Roadmap order, dependencies, and authority

- `SQ.9A-FLOW-SOURCE` and `SQ.9A-FLOW-BACKDROP` are complete in cycles 197 and
  198. This is the next eligible slice in `docs/UI_CONTEXT_ROADMAP.md`;
  Hourly, Daily, and four-page acceptance remain downstream.
- Follow `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`, `docs/UI_DEVELOPMENT_WORKFLOW.md`,
  and the amended SQ.2 matrix. The approved image is a visual source, not a
  runtime asset or semantic authority.
- Use the accepted shared-backdrop capture from cycle 198, cycle 193's Now
  baseline, and cycle 195's before/after captures as references. The owner
  directed use of the connected physical Pixel 5 instead of an emulator for
  this execution; use it for focused slice tests and installed evidence.

## Production boundary

Change only Glass Full Now supporting-fact grouping in
`NowSupportingMeasurements` in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`. Remove or adjust
the dedicated surface in
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`
only if it has no remaining production callers. Update directly affected
assertions in `app/src/androidTest/java/com/oxygen/weather/ui/ProductionHomeCompositionTest.kt`
and `app/src/androidTest/java/com/oxygen/weather/ui/ProductionOfficialAlertSummaryFlowTest.kt`.
No other page or theme composition changes belong in this cycle.

## Functional invariants

- Preserve every current value, unit, condition identity, missing-data state,
  source/provenance, freshness, and forecast/alert meaning.
- Preserve the Now visible/spoken summary, condition text and supplemental
  mark, supporting labels/values/available secondary lines, and source/update/
  status text. Keep the four supporting facts individually identifiable and
  visibly grouped; the backdrop and mark cannot carry weather meaning.
- Preserve page identity/navigation, controls and usable target sizes,
  alert-summary actions, content scrolling, and accessibility semantics.
- Apply the open grouping only to Glass Full. Preserve the presentation for
  Glass None (`OFF`) and Subtle, all three effect levels in every other theme,
  and independent High contrast/reduced-motion behavior. Do not change saved
  preferences or the effects enum/identity.
- Do not change weather/alert requests, cache behavior, or forecast data.

## Implementation steps

1. Review the accepted cycle 198 backdrop and source references listed above,
   the Now composition, the dedicated supporting surface and its production
   callers, and the assertions in the two named instrumentation test files.
   Confirm the four facts and any supporting lines in the deterministic
   presentation fixture.
2. Remove the enclosure for Glass Full while retaining the existing four
   labels/values and arranging their open layout so spacing and hierarchy
   preserve clear association and grouping. Adapt compact-width wrapping and
   vertical spacing as needed; keep the existing primary-field treatment and
   all other theme/effect paths intact. Remove the surface implementation only
   if it is unused after this change.
3. Update and rename
   `ProductionHomeCompositionTest.glassFullNowKeepsOneSupportingGroupAndFactSemantics`
   to `glassFullNowUsesOpenSupportingFactsAndPreservesSemantics`. Assert the
   old enclosure is absent, all four facts and any available supporting lines
   remain present and readable, and the spoken summary, condition, provenance,
   and status remain present. Replace bounds-within-container assertions with
   layout/semantics assertions that establish distinct labels and values,
   expected association/grouping, and no overlap or clipping at the tested
   compact and large-font configurations.
4. Update `showsOneSourceSuppliedAlertWithoutChangingForecastResult` only where
   it currently expects the removed grouping tag; continue to prove the alert
   summary/action and forecast result remain correct. Add focused regression
   assertions, if needed, that the change is selected only for Glass Full and
   that other themes/effect levels preserve their previous grouping behavior.
5. Run the named focused connected test methods on Pixel 5, then build and
   lint the changed boundary. Install the resulting app and complete the
   evidence matrix below. Review captures against the objective and record any
   gap rather than broadening production scope implicitly.

## Acceptance criteria

- Glass Full Now uses the shared backdrop without a data-only enclosure around
  the supporting facts.
- Humidity, Dew point, Precipitation, and Wind remain clearly grouped and each
  label remains associated with its value and any available supporting text.
  At compact width and large font, facts may wrap/stack but do not overlap,
  clip, become visually ambiguous, or require horizontal scrolling.
- Condition identity/mark, temperature and apparent temperature, visible and
  spoken summaries, provenance/freshness/status, and available alert action
  remain present and readable. Focused semantics/layout assertions pass.
- Glass None/Subtle and all other theme/effect-level combinations retain their
  existing grouping and presentation. No preference, meaning, behavior, or
  accessibility change occurs.
- The required installed Pixel 5 evidence is captured and reviewed at the
  stated settings. Focused connected tests, build, lint, workflow validation,
  and `git diff --check` pass. Evidence names actual device/settings and
  records any unverified boundary.

## Verification and evidence

**Required device setup:** physical Pixel 5 (`redfin`), serial `15141FDD4003WL`;
screen timeout set to 0 or
disabled and Stay Awake confirmed selected in Developer Options while testing.
Record device/API, build identity, actual viewport, font scale, theme/effects,
contrast, layout direction, reduced-motion state, timeout, and Stay Awake in
`device-config.md` under the evidence root.

**Focused automated checks:** run each method separately on Pixel 5 with
the wrapper's connected Android test task, Android's instrumentation class
filter, and `ANDROID_SERIAL` set to the Pixel 5 serial:

```sh
JAVA_HOME=/usr/lib/jvm/java-27-openjdk ANDROID_HOME="$PWD/.android-sdk" ANDROID_SDK_ROOT="$PWD/.android-sdk" ANDROID_SERIAL='15141FDD4003WL' ./gradlew --no-daemon :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionHomeCompositionTest#glassFullNowUsesOpenSupportingFactsAndPreservesSemantics'
JAVA_HOME=/usr/lib/jvm/java-27-openjdk ANDROID_HOME="$PWD/.android-sdk" ANDROID_SDK_ROOT="$PWD/.android-sdk" ANDROID_SERIAL='15141FDD4003WL' ./gradlew --no-daemon :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionOfficialAlertSummaryFlowTest#showsOneSourceSuppliedAlertWithoutChangingForecastResult'
```

Record the exact command, device serial, and PASS/FAIL output in
`focused-tests.txt`. Any added focused regression method must be listed there.

**Installed evidence matrix (Pixel 5):**

- Target state: Glass/Smoked Quartz, Full, Standard contrast, LTR, 360×640dp,
  font scale 1.0.
- Target stress state: same at 360×640dp, font scale 1.3. Confirm all four
  facts remain readable/grouped and the screen remains vertically scrollable
  without overlap or horizontal overflow.
- Preservation states at 360×640dp, font scale 1.0, Standard contrast, LTR:
  all three effects levels for Glass and all three levels for each other
  built-in theme (Atmospheric, Minimal OLED, Instrument, Terminal). Inspect
  that the open Full grouping does not leak into any non-target combination.
- Overlay states: Glass Full High contrast at compact 1.0 and 1.3; inspect
  RTL at compact 1.0 if the installed app can be placed in RTL using the
  supported test/device setup. Confirm reduced-motion leaves the static
  grouping and semantics complete. If a required state cannot be rendered,
  record it UNVERIFIED with the concrete cause; do not infer a pass.
- For each cell retain a screenshot and a PASS/FAIL/UNVERIFIED disposition in
  `review-matrix.md`. Preserve relevant hierarchy/semantics observations and
  hashes. The repository's general slice guidance names Pixel 3 XL; the owner
  specifically directed using the connected Pixel 5 instead of an emulator
  for this execution. Record this device substitution in cycle history.

Save screenshots, logs, configuration, review matrix, and relevant hashes under
`.codex/test-artifacts/199-sq9a-flow-now/`. Do not run
`python scripts/dev.py check` or a full connected suite; those require a
separate planned verification cycle. Do not claim performance or motion
acceptance.

Run the boundary checks:

```sh
python scripts/dev.py build
python scripts/dev.py lint
python scripts/dev.py workflow
git diff --check
```

Inspect the final diff and report any required check that could not run and
why. The existing RTL setup failure from cycle 198 is a known limitation; make
a concrete attempt to render the RTL state for this slice and retain the
result, without claiming RTL verified if rendering remains LTR.

## Risks and assumptions

- The existing grouped layout uses two columns when width permits and stacks
  at narrower widths. Removing its surface can weaken boundaries between
  facts; installed compact/large-font review must confirm spacing and label to
  value association, and implementation may tune spacing/typography within
  this component only.
- The current focused test encodes the old enclosure by checking the group
  tag and bounds. Replace those expectations rather than preserving the old
  visual contract.
- Cycle 198 reported that force-RTL did not change installed rendering. The
  RTL state must be attempted again; if the supported setup still renders LTR,
  it remains explicitly UNVERIFIED.
- The owner directed using the connected Pixel 5 instead of an emulator for
  this execution. Focused tests use its serial with the Gradle connected-test
  task and instrumentation runner's class/method filter; the Pixel 3 XL device
  condition is consequently not represented as verified.
- The full theme/effects preservation check is deliberately a single compact,
  standard-font capture per combination. Compact/large-font stress and
  contrast/RTL overlays are reserved for the changed Glass Full target to
  keep the verification matrix bounded.

**Context budget:** target 35–45% of the available execution context for
implementation and evidence review; split/replan if the estimate exceeds 55%.
The slice is one Now component, two focused test methods, one compact
theme/effects preservation matrix, and only the stress/overlay states that
touch the changed owner.

## Out of scope

- Hourly/Daily grouping or marks, Details, and the SQ.9A four-page acceptance
  review.
- Changes to the shared background recipe, weather marks beyond preserving
  the existing Now condition mark, weather/alert data meaning, provenance,
  navigation, requests/cache, saved preferences, or appearance identity.
- Changes to other themes or Glass None/Subtle appearance; only regression
  verification for those states is included.
- Animation, performance measurement, motion acceptance, full unit/connected
  suites, TalkBack service acceptance, and unrelated visual/accessibility
  remediation.
