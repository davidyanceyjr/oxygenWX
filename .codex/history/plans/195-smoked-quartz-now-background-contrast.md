# Plan 195 — Increase Smoked Quartz Now background contrast

Status: Completed
Cycle ID: 195-smoked-quartz-now-background-contrast
Roadmap item: SQ.9A-NOWBG
Created: 2026-10-14

## Objective

Make the existing Smoked Quartz Glass Now diagonal mineral background read
with more separation: move the light-side background channels 20% toward white
and reduce the dark-side background channels by 7% toward black. Rebuild and
install the app, recapture the real Pixel 3 XL Now screen using the currently
selected Standard-contrast appearance state, and show the installed capture.
This is a Now background refinement and does not accept the pending four-page
Static Full matrix.

## Authority and baseline

- Product and presentation invariants: `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`, and
  `resources/themes/smoked-quartz/SOURCE.md`.
- Governing branch order: `docs/UI_CONTEXT_ROADMAP.md`, SQ.9,
  SQ.9A-CORR, this SQ.9A-NOWBG entry, then SQ.9A.
- Current renderer: `ProductionBackdrop` calls `drawMineralPage` for Glass
  Home pages. Now uses the opaque `palette.canvas`, a diagonal field tinted
  with `palette.atmosphereBottom`, and an upper-left radial light from
  `palette.atmosphereTop`. Geometry mirrors in RTL.
- Installed baseline: owner-requested Pixel 3 XL capture from this task, saved
  to this cycle's `screenshots/before-now.png` before production edits. Record
  the actual app state, viewport, effects, contrast, layout, units, locale,
  build, device API, and screen-awake configuration in `device-config.md`.

## Production boundary

Change only the Glass Now background drawing in
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`
and its focused `ProductionWeatherVisualsTest` coverage. Keep the existing
Now geometry, light-field center, direction mirroring, texture density, and
effects strengths. For standard contrast, define the requested percentages
as sRGB channel operations on the rendered sides: multiply each light-side
channel by `1.20` after applying its existing effect-level blend, and multiply
each dark-side channel by `0.93`, rounding to the nearest 8-bit value and
clamping to 255. Preserve the effect-level difference by calculating the
light-side blended color at the current mineral strength before applying its
lift.
Keep the original colors and approved flat black/white treatment at High
contrast; do not alter Hourly, Daily, Details, other themes, or foreground,
surface, outline, type, and weather colors.

The installed comparison uses the device's native 1440×2960 viewport at 560
dpi and Glass Standard contrast state, matching the pre-change capture, and
preserve the currently selected effects/layout/units. If that state cannot be
confirmed or the sRGB interpretation produces a material mismatch with the
requested visible operation, stop for replanning rather than changing the
definition mid-cycle.

## Functional invariants

- Preserve all Now weather values, condition text, source/provenance,
  update/freshness, alerts, accessibility semantics, and control meaning.
- Preserve page identity and menu/pager/Back behavior, RTL mirroring, and the
  current render policy for Glass None/Subtle/Full and High contrast.
- Do not change forecasts, request/cache behavior, fixture data, preferences,
  or other themes/pages.

## Implementation steps

1. Record the before screenshot and device/app configuration.
2. Add focused renderer tests for the exact sRGB channel transforms and
   blended light-side result, High-contrast preservation, and unchanged
   non-Now/background behavior.
3. Apply the color transforms only to the Standard-contrast Glass Now mineral
   background; leave the overlay strength and geometry unchanged.
4. Run the focused `ProductionWeatherVisualsTest`, build, applicable lint,
   contract and workflow checks, and `git diff --check`.
5. Install the built app on Pixel 3 XL, confirm the Glass Now state/settings,
   capture the final screen, inspect before/after visual difference and
   readability, hash both screenshots, and display the final capture with
   `/usr/bin/feh` on `DISPLAY=:0`.

## Acceptance criteria

- Focused tests verify the 20% light-side and 7% dark-side sRGB operations and
  verify no change to High contrast, non-Now geometry, or other themes.
- The installed Pixel 3 XL capture has stronger separation across the
  existing diagonal Now mineral field, with all content and functional
  invariants preserved and no text readability regression.
- The displayed final capture is the same hash-verified image stored under
  this cycle's evidence directory.
- Cycle 195 is closed as a Now background refinement only; SQ.9A remains
  pending as a separate four-page acceptance session.

## Verification and evidence

Use the Pixel 3 XL (`crosshatch`), screen timeout 0, Developer Options Stay
Awake enabled. Run the focused `ProductionWeatherVisualsTest` JVM command
documented in the cycle evidence, plus `python scripts/dev.py build`,
`python scripts/dev.py lint` when Kotlin production code changes,
`python scripts/dev.py contract`, `python scripts/dev.py workflow`, installed
Pixel 3 XL capture/review, and `git diff --check`. Do not run a full suite or
`python scripts/dev.py check` in this implementation slice.

Evidence root: `.codex/test-artifacts/195-smoked-quartz-now-background-contrast/`.
Retain before/after screenshots, device/config record, test/check outputs,
SHA-256 sums, brief visual disposition, and final diff notes.

## Risks and assumptions

- “Lighten 20%” means multiplying each rendered light-side sRGB channel by
  1.20; “darken 7%” means
  multiplying each sRGB channel by 0.93. Both operations round to the nearest
  8-bit channel and clamp to the valid range. This deterministic brightness
  interpretation is recorded on the roadmap and must be used consistently in
  tests and the renderer.
- Text sits in front of the background and remains sharp; compare at the
  actual installed device viewport before acceptance.

## Out of scope

- SQ.9A four-page Static Full acceptance; SQ.10 performance; other page
  backgrounds; High-contrast palette changes; Effects None/Subtle/Full redesign;
  other themes; weather/data/provider/request/cache changes; full unit or
  connected-test suites; source asset approval or updates.
