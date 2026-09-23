# Plan 024 — Production themed weather marks and backgrounds

Status: Pivoted
Cycle ID: 024-production-themed-weather-marks-backgrounds
Roadmap item: R0.11CAA + user-directed production renderer replacement
Created: 2026-09-22
Revised: 2026-09-23

> Pivoted on 2026-09-23 by explicit roadmap direction. This plan is retained
> as a record of the interrupted cycle, not as current implementation authority.
> Its working-tree changes and evidence remain intact for later review against
> TP.1–TP.3; no completion claim is made.

## Objective

Add the production theme-engine weather-mark and backdrop primitives as an
additive Compose component family, then integrate the production renderer into
the real application path. Replace the Theme B sketch with the production
theme-aware composition for Now, Hourly, Daily, and Details. Expose all five
approved themes for selection and make their different art direction visible
through resolved backgrounds, typography, surfaces, marks, and page treatment.
Atmospheric remains the initial theme. Keep the supplied weather values,
four-page navigation, forecast membership/order, provenance, and accessibility
meaning intact.

User-directed verification support: repair
`scripts/run_visible_emulator.sh` so it does not silently reuse a headless
emulator when the user requests a visible manual-test window, and make its
launch result explicit. This is developer tooling only; it does not change
the Android app's launch behavior or production renderer boundary.

Latest user direction raises the visual acceptance bar to exact art-reference
fidelity. The current reference set and written contract explicitly permit
interpretation and do not define all page/theme states. Before further renderer
changes are accepted, return to visual design and approve a codifiable design
pack. The readiness audit is at
`.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/design-readiness-audit.md`.

## Production boundary

Production changes include the `ui/themeengine/` component family and the
normal `OxygenWeatherApp` composition that consumes existing presentation
models. The components consume `ResolvedTheme`, typed presentation values, and
caller-owned callbacks. They must not read domain/provider/persistence values,
infer conditions from measurements, format or rewrite weather facts, or add
weather data contracts. The app uses Atmospheric by default and lets the user
select among the five built-in themes in memory; persistence is not added.

Verification changes are limited to
`app/src/debug/java/com/oxygen/weather/ProductionComponentsActivity.kt`,
`scripts/verification/production_components.py`, and evidence under
`.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/`, plus
the user-requested manual-test launcher repair in
`scripts/run_visible_emulator.sh`.
The showcase remains debug-only and supplemental. It does not substitute for
the installed normal application path.

After implementation and verification, update only the applicable authority
records: `docs/ARCHITECTURE.md` to describe the implemented theme-engine
primitives and their presentation boundary; `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`
to record their supplemental/accessibility and Effects Off behavior; and
`docs/ROADMAP.md` to replace R0.11CAA ACTIVE with its evidence-backed outcome.
Do not change `docs/SPECIFICATION.md` unless implementation reveals a genuine
product-contract gap; any proposed semantic or release-scope change requires a
separate roadmap decision rather than silent expansion here.

## Functional invariants

- Map the six existing `WeatherMarkCondition` values—clear, partly cloudy,
  cloudy, rain, storm, and snow—exhaustively. Each must remain visually
  distinguishable at compact mark sizes. A null condition draws no weather
  mark; it does not draw a generic/clear mark or create placeholder text.
- Marks and backdrops are decorative. Existing adjacent supplied text remains
  the sole weather meaning and accessible summary. Mark semantics are cleared
  to avoid duplicate speech; the backdrop is not an accessibility node.
- Components read only resolved style, palette, and motion/effects policy plus
  the explicitly supplied condition. They do not branch on raw theme IDs,
  inspect measurements, create weather state, or change facts, provenance,
  chronology, navigation, or callbacks.
- Implement all `WeatherMarkStyle`, `BackdropStyle`, `SurfaceStyle`, and
  `HeroStyle` variants currently represented by the resolved model, with
  exhaustive handling so a future enum addition requires an explicit
  implementation decision. The approved codifiable design pack defines the
  exact visual target. Do not accept a generalized theme approximation.
- Backdrop drawing is behind caller content, does not capture pointer input,
  and cannot obscure or lower the contrast of its supplied foreground content.
  It uses the resolved palette and no downloaded/runtime image assets.
- Effects Off resolves and renders a solid opaque canvas, with no atmospheric
  gradient, grid, overlay, animation, or other motion. Foreground content and
  contrast remain complete. Subtle/Full may render their resolved static
  backdrop styles; motion implementation is not part of this slice.
- Standard/High contrast, Standard/Simple layout, and RTL do not remove or
  reorder adjacent weather facts. Components do not add interactive controls or
  horizontal scrolling.

## Implementation steps

1. Inspect the current production theme model/resolver, existing sketch mark,
   design-reference manifest, and production component conventions. Confirm
   the exact source enum and current style variants before editing. Keep the
   production API additive and consistent with the existing `ResolvedTheme`
   component family.
2. Add a production weather-mark composable that accepts the resolved theme
   and nullable presentation condition. Reuse provider-neutral drawing ideas
   where appropriate, but style through `weatherMarkStyle` and semantic
   palette roles. Keep each of the six cases recognizable at small sizes,
   avoid theme-ID branches, and clear decorative semantics.
3. Add a production backdrop composable that accepts the resolved theme,
   caller modifier, and foreground content. Handle every resolved backdrop
   style using bounded Compose drawing/surfaces; clip or layer only as needed
   to keep drawing behind content and avoid intercepting interaction. Preserve
   the opaque/static Effects Off policy at the rendering boundary as well as
   in resolver output.
4. Add deterministic tests for exhaustive condition/style coverage, null
   condition behavior, no raw theme-ID dependency in component APIs, each
   backdrop style, and Effects Off's solid/opaque/static policy. Prefer pure
   drawing-spec helpers for assertions where Compose pixel tests would be
   fragile; do not add arbitrary snapshots or brittle color-coordinate tests.
5. Extend the isolated debug showcase with all six condition marks beside their
   existing visible condition text, null/missing condition, and the resolved
   backdrop cases. Keep current source, page, forecast, and Details fixtures
   intact. Keep the host debug-only; it supplements but does not substitute
   for normal app integration or installed review.
6. Extend the installed verifier with source-boundary checks for the new
   composables and debug-only host, checks that every mark fixture retains its
   visible text/accessible summary, and repeatable captures/hierarchy for the
   foreground over Atmospheric Subtle and Effects Off. Collect actual display
   dimensions/density and font scale rather than relying on hard-coded pixel
   dimensions. Inspect screenshots at 393x852dp as the primary reference
   viewport and 360x640dp as the compact/short-window stress viewport, at font
   scale 1.0 and 1.3 and in RTL; correct clipping, overlap, misleading marks,
   or content obstruction before acceptance.
7. Replace normal app composition with the resolved production renderer for
   all four named pages. Add the theme picker without adding a nested pager;
   preserve Back behavior and outer-pager ownership. Give the five visual
   systems distinct treatments through resolved visual styles and tokens.
8. Install and inspect the actual app in all five theme states at the primary
   393x852dp reference viewport, and verify the 360x640dp compact/short-window
   stress case, a large-font state, RTL page navigation, and Effects Off.
   Compare the installed screen against the exact approved reference for that
   theme/page at the named viewport; record any deviations and correct them
   before acceptance.
9. Run focused tests, the installed verifier, repository checks and workflow
   gates; preserve exact commands, device/build metadata, output, screenshots,
   hierarchy, and observations. Update the named authority documents only to
   describe behavior actually implemented and verified. Inspect `git diff`,
   run `git diff --check`, and close the cycle into `.codex/history/` only when
   every acceptance item is met or a precise verification boundary is recorded.

## Acceptance criteria

- Each of the six supplied conditions produces a distinct, recognizable
  decorative mark in the production implementation; null produces no mark.
- Every currently declared `WeatherMarkStyle` and `BackdropStyle` has an
  explicit rendering path driven by `ResolvedTheme` fields, not raw theme ID.
- The backdrop stays behind content and does not intercept input or alter
  supplied text/semantics. Effects Off is a solid fully opaque static render
  with no decorative overlays or motion.
- Focused tests cover exhaustive condition and style handling, null behavior,
  and the Effects Off backdrop policy. The debug host/verifier exercises all
  six marks, missing condition, Atmospheric Subtle, Effects Off, and adjacent
  visible/accessibility text.
- Installed evidence covers 393x852dp as the primary reference viewport and
  360x640dp as the compact/short-window stress viewport, font scale 1.0 and
  1.3, LTR and RTL text adjacency, Atmospheric Subtle, and Effects Off. Record
  verified readability/overlap and any limitation; a build or preview alone
  is not visual acceptance.
- `python scripts/dev.py test`, `python scripts/dev.py check`,
  `python scripts/dev.py workflow`, `python scripts/dev.py contract`, the
  focused installed verifier, and `git diff --check` pass; otherwise preserve
  exact failure output and leave the cycle active without a completion claim.
- Authority docs describe only behavior verified in this slice. R0.11CAA is
  marked DONE only when its history record includes verification performed,
  evidence location, and unverified boundaries.
- `MainActivity`, presentation mapping, forecast behavior, and weather fetch
  behavior remain unchanged. The Theme B sketch is no longer the normal UI.

## Verification and evidence

Use the dedicated Oxygen slice emulator when available. Keep evidence under:

```text
.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/
```

Retain focused test and workflow output, verifier output, screenshots, saved
UI hierarchy, and a concise run manifest with exact commands, device serial,
API, app/build identity, display dimensions, density, font scale, layout
direction, resolved theme/effects inputs, and visual observations. Capture at
least one all-condition showcase state plus Atmospheric Subtle and Effects Off
at both font scales, and RTL adjacency evidence. Do not claim service-level
TalkBack traversal unless it was actually performed.

Run focused unit tests first, then:

```sh
python scripts/dev.py test
python scripts/dev.py check
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/verification/production_components.py --adb .android-sdk/platform-tools/adb --artifacts .codex/test-artifacts/024-production-themed-weather-marks-backgrounds/<run>
git diff --check
```

If Android tooling/device access prevents installed evidence, record the exact
environment failure, retain successful automated evidence, and keep the cycle
active. Do not mark the roadmap item complete without installed visual evidence.

## Risks and assumptions

- The user explicitly expanded this active cycle from an isolated component
  showcase to the production app cutover across all five themes. The user has
  since clarified that exact art-reference matching is required; the earlier
  permissive interpretation is superseded. The current reference package is
  incomplete for exact reproduction. Return to design and approve the pack
  described in `.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/design-readiness-audit.md`
  before accepting visual implementation.
- Installed screenshots verify rendering and visible facts, not service-level
  TalkBack traversal.
- Existing `WeatherMarkCondition` mirrors the six canonical condition values.
  Do not change that presentation mapping or domain model in this slice.
- If implementation demonstrates that the existing resolved model cannot
  express the required rendering policy, stop at the smallest documented
  model gap and revise the roadmap/plan before broadening production scope.

## Out of scope

- Preference persistence, new theme IDs, animation, system-motion integration,
  downloaded/runtime image assets, or new dependencies.
- Weather/provider/repository/cache/alert/location/unit behavior, condition
  inference, new presentation models, new weather facts, or meteorological
  meaning changes.
- Page composition, controls, navigation, Android app launch behavior, theme
  settings, unrelated cleanup, or service-level accessibility claims.

## Context budget

The user explicitly redirected this active cycle from an isolated component
showcase to replacement of the normal app renderer. The original R0.11D–R0.11H
sequence is consolidated here for the requested correction. Retain every
functional invariant, record exact verification boundaries, and leave the cycle
active if any acceptance item remains unverified.
