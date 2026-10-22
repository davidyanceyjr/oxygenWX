# Plan 224 — Establish Smoked Quartz motion lifecycle gate

- Status: Deprecated
- Cycle ID: 224-smoked-quartz-motion-lifecycle-gate
- Roadmap item: SQ.15
- Created: 2026-10-22

## Objective

Establish, with focused checks and an installed probe, that decorative drawing
on Smoked Quartz Home is eligible only while its page is active and the app is
foregrounded, and that system reduced motion / animator scale zero disables
decorative motion without changing the selected static appearance. This gate
decides whether the conditional SQ.16 Full seam glint may proceed; it does not
implement that artwork.

## Production boundary

Inspect and verify the existing Smoked Quartz (`WeatherThemeId.GLASS`) effects
and Home lifecycle path. Add only the smallest lifecycle or motion-policy
support and focused probe needed to establish active-page, foreground,
reduced-motion, and animator-scale behavior for a future decorative drawing
source. Keep the persisted effects preference and static Full/Subtle/None
rendering intact. The probe may use a test-only diagnostic signal if it can
observe whether decorative drawing is running without adding user-visible
artwork or a permanent production animation.

The installed probe uses the Pixel 3 XL at the compact 360×640dp baseline,
font scale 1.0, Standard layout, LTR, Smoked Quartz/Glass, Standard contrast,
Effects Full, and the recorded system animator/reduced-motion settings. It
exercises each Home page as active and inactive, app foreground/background,
background-to-foreground return, reduced motion enabled, and animator scale
zero. Use the supported production navigation path for page changes.

## Functional invariants

- Preserve the global page order and sole outer-pager swipe ownership; page
  identity and existing controls remain available.
- Preserve weather values, condition identity, units, missing-data behavior,
  provenance, freshness, chronology, and accessibility meaning.
- Preserve forecast and official-alert request/cache behavior; appearance and
  lifecycle changes do not initiate weather work.
- Preserve saved theme/effects identities and selected preferences. Effective
  motion fallback does not rewrite a saved choice.
- Effects None remains opaque, static, and complete with the accepted Smoked
  Quartz base stone, sediment, and grain; Subtle remains static. Full stays
  visually static for this slice because no seam glint is added.
- Critical text and controls remain stationary; any probe signal is
  diagnostic and cannot encode weather meaning.

## Implementation steps

1. Inspect the existing resolver, reduced-motion policy, Home page selection,
   Activity lifecycle, and rendering/test hooks. Identify the smallest
   observable signal for whether a future decorative drawing source is active.
2. Add focused deterministic policy/lifecycle tests for active versus
   inactive page, foreground versus background, reduced motion, and animator
   scale zero. Cover stop and resume transitions and confirm saved appearance
   preferences do not change.
3. Run the focused checks on the Pixel 3 XL with screen timeout disabled and
   Stay Awake confirmed. Install the app and capture probe state for all four
   active pages, inactive-page states, background/return, reduced-motion, and
   animator-scale-zero conditions; record exact configuration and interaction
   sequence.
4. If the probe shows decorative drawing continues while its page is
   inactive, the app is backgrounded, or motion is disabled, make only the
   narrow lifecycle/policy correction within this boundary and repeat the
   focused checks and affected installed cases. If the issue needs a broader
   architecture or visual change, record the exact failing case and stop for
   a new plan.
5. Inspect the final diff and run `git diff --check`; retain test output,
   probe captures/recordings, and configuration under the cycle evidence path.

## Acceptance criteria

- Focused checks demonstrate that an eligible decorative drawing source runs
  only while its Home page is active and the app is foregrounded, and stops
  when the page becomes inactive or the app backgrounds. Returning to an
  eligible foreground page resumes it.
- Reduced motion and animator scale zero disable decorative motion while
  retaining the selected static appearance and saved preference.
- The installed probe confirms these transitions through the real app path
  for Now, Hourly, Daily, and Details, or records an exact failure/unverified
  case that blocks SQ.16. No seam-glint artwork or motion treatment is
  introduced.
- Any correction has focused regression coverage; weather state, navigation,
  semantics, and request/cache behavior remain unchanged.
- If the current renderer has no running decorative drawing source, the probe
  must still verify lifecycle/policy eligibility at the production rendering
  boundary; do not claim observed drawing stopped without an observable
  positive control and state transition.

## Verification and evidence

- Run the smallest focused unit and Android instrumentation checks covering
  the changed lifecycle/policy behavior on the Pixel 3 XL. Reuse existing
  tests where they prove the same behavior; add only focused coverage for a
  demonstrated gap.
- Confirm the Pixel 3 XL screen timeout is disabled and Stay Awake is selected
  before device checks. Capture the device/API, build, viewport, font scale,
  layout, direction, theme, contrast, effects, reduced-motion state, animator
  scale, active page, and app lifecycle state for each probe result.
- Run applicable app/test compilation or build checks for changed sources and
  `git diff --check`. Do not run `python scripts/dev.py check`; the full
  relevant suites belong to the separate SQ.V cycle.
- Store focused test logs, probe method/configuration, installed captures or
  recordings, and the result matrix in
  `.codex/test-artifacts/224-smoked-quartz-motion-lifecycle-gate/`.
- Record unsupported or unobservable system states as unverified, with the
  exact limitation. Installed evidence is required; preview/compilation alone
  does not establish the lifecycle gate.

## Risks and assumptions

- Existing Smoked Quartz rendering may be entirely static. If no recurring
  decorative drawing exists, the gate needs an observable lifecycle-controlled
  probe and a positive control; static screenshots cannot establish that a
  draw loop stopped.
- Compose page composition/visibility and Activity foreground lifecycle may
  expose separate lifecycle owners. Confirm the production ownership before
  changing code; avoid broad Home navigation or host-lifecycle refactors.
- System reduced-motion and animator-scale state must be set/read back and
  recorded independently. A preference fallback must not mutate stored
  effects settings.
- Estimate: 35–45% context. If implementation discovery puts the slice above
  the roadmap's 55% replanning threshold, stop and split before expanding the
  production boundary; never exceed the 65% hard maximum.

## Out of scope

- Seam-glint artwork or any other new motion treatment (SQ.16).
- Static Full/Subtle/None visual redesign or static performance measurement.
- SQ.17A layout/localization acceptance, SQ.17B appearance/behavior
  acceptance, and the separate full-suite SQ.V session.
- Broad theme, layout, font, RTL, contrast, accessibility-service, provider,
  forecast, alert, or cache changes beyond diagnosing an in-scope lifecycle
  regression.
- Any change to user-facing appearance labels, persisted preference names,
  or meteorological meaning.
