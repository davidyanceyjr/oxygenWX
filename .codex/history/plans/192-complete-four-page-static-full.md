# Plan 192 — Static Full implementation (Part 1 of 2)

Status: Completed
Cycle ID: 192-complete-four-page-static-full
Roadmap item: SQ.9
Created: 2026-10-14

## Objective and observable outcome

Implement the source-supported static Full decorative treatment for Smoked
Quartz (the existing `WeatherThemeId.GLASS` identity) on Now, Hourly, and
Daily. Produce focused regression results and an installed compact smoke
review of all four pages. The observable result is that the approved static
decoration appears on the applicable pages while Details remains quiet and
decoration-free, and the real app retains all existing facts, controls, and
semantics.

This is SQ.9 Part 1 only. SQ.9A is a separate installed acceptance cycle for
the complete matrix and must pass before SQ.10 or later dependent work. This
plan does not accept the complete Static Full design or authorize later
effects levels/performance work.

## Authority and dependencies

- Governing requirements: `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/EFFECTS_CONTRACT.md`, and `AGENTS.md`.
- Roadmap ordering and SQ.9 exit: `docs/UI_CONTEXT_ROADMAP.md` SQ.9, following
  SQ.8 and SQ.2. SQ.8 is recorded complete in cycles 190–191.
- SQ.2 owner disposition (cycle 184) approves the written design decisions
  only. Candidate SVGs, their exact asset geometry, and runtime packaging are
  not approved. Implement native/existing Compose recipes only where the
  approved text and current typed condition mapping support them. For Glass,
  the approved D29 mapping supports `CLEAR`, `PARTLY_CLOUDY`, `CLOUDY`, `RAIN`,
  and `STORM`; `SNOW` is an explicit source gap and null has no mark. Reuse the
  existing mapping and renderer; do not add or infer condition identities or
  artwork.
- Owner clarification for this review (2026-10-14): reuse existing typed
  mappings and omit unsupported marks.
- No owner decision is pending for this bounded slice. If a required visual
  result depends on candidate SVG geometry or any unapproved design choice,
  omit that optional treatment, record the exact gap, and do not infer approval.

## Production boundary

Change only the current Glass/Smoked Quartz Static Full decoration seam and
directly required decorative mark rendering on Now, Hourly, and Daily, with
focused tests. Details may receive no weather mark or separate inclusion
overlay; preserve its existing quiet mineral field and micro-etch composition.
Do not change other themes, the stored Glass identity, page compositions from
SQ.4–SQ.8, data/presentation mapping, or app request/cache behavior.

Use only existing/native Compose primitives and the existing Glass `SOFT_LINE`
condition mapping. Its five mapped conditions remain decorative and secondary
to visible condition text; `SNOW`, null, and any unsupported identity render
no mark. The candidate mark SVGs are runtime-unapproved; do not trace, package,
or render their candidate geometry. The candidate inclusion overlay is
omitted. A static edge accent may be added only if it follows the approved
written recipe within the existing rendering seam, stays outside text-safe
areas, and does not import candidate asset geometry. No authored bitmap or
bundled asset is needed.

## Functional invariants

- Preserve page order and visible identity `Now -> Hourly -> Daily -> Details`,
  outer-pager swipe ownership, Android Back, page/menu selection, and all
  existing date/window/action controls and their labels, hit targets, and
  semantics.
- Preserve weather values/units, condition identity and text, actual horizon
  and chronology, window membership, source/provenance, valid/update/freshness
  data, missing-value behavior, alert meaning, and request/cache behavior.
- Decoration is supplemental, carries no weather/status/time/value meaning,
  and is hidden from accessibility only where existing visible text and spoken
  summaries retain the meaning. Missing or unsupported condition identities
  show no mark and never receive a generic substitute.
- Keep critical text stationary, readable, and in the source-defined safe
  regions. Do not add row-aligned or value-encoding bands, per-entry cards,
  Details marks, motion, or overlays that compete with facts.
- None (internal `OFF`) remains opaque, static, and complete. Do not leak
  Full-only treatment into None or Subtle. Preserve the approved Smoked Quartz
  None base-field exception and do not change preference identity/storage.
- Preserve all non-Glass theme rendering and functional icon meanings.

## Implementation steps

1. Recheck the relevant SQ.2 source and cycle 184 disposition, SQ.4–SQ.8
   outcomes, `ProductionWeatherVisuals`, `ProductionWeatherMark`, and the
   existing typed condition map. Confirm the Glass mapping against the
   approved D29 matrix: `CLEAR`, `PARTLY_CLOUDY`, `CLOUDY`, `RAIN`, and `STORM`
   map to existing marks; `SNOW` is a source gap and null maps to no mark.
   Write a short implementation note in the evidence directory listing each
   recipe used and each optional mark or overlay omitted, with its reason.
2. Implement the smallest source-supported static Full treatment on Now,
   Hourly, and Daily through the existing Glass rendering seam. Keep marks
   strictly secondary to visible condition text. Do not add decoration to
   Details or import candidate asset geometry. Keep all other themes and
   effects levels unchanged.
3. Add/update focused automated coverage for the Glass Static Full recipe and
   the existing Glass mark mapping, including the explicit `SNOW` gap and null
   fallback, decorative accessibility semantics, and isolation from None,
   Subtle, and other themes. Prefer the existing
   `ProductionWeatherVisualsTest` and `ProductionWeatherMarkTest` seams; add
   only the smallest production-path test needed to cover a gap they cannot
   exercise. Do not extend the typed condition map or add mark identities.
4. Run the focused JVM test and focused Android rendering test on the physical
   Pixel 3 XL. Before device use, set screen timeout to disabled/0 and confirm
   Developer Options **Stay Awake** is selected. Record exact commands,
   device/API/build identity, and outcomes.
5. Build/install the real app and capture a same-fixture smoke review of Now,
   Hourly, Daily, and Details on Pixel 3 XL at 360×640 dp, font scale 1.0,
   Standard contrast, LTR, Metric units, Glass/Smoked Quartz, Static Full.
   Reuse the Demo Station development fixture and matching SQ.4 baseline
   where available; record the actual fixture/build identity. Exercise page
   navigation and, on Hourly/Daily, an available window control. Cover one
   supported and one missing/unsupported condition where the deterministic
   fixture permits; otherwise record the unavailable case and verify its
   focused automated fallback. Confirm Details has no weather mark/inclusion
   overlay. This is a smoke pass, not SQ.9A acceptance.
6. Review screenshots and hierarchy/semantics observations against the SQ.2
   page recipes and compact criteria; compare with relevant SQ.4–SQ.8
   baselines. Record per-page PASS/FAIL/UNVERIFIED and any follow-up gap.
7. Run changed-boundary build, applicable lint, contract and workflow checks,
   and `git diff --check`; inspect the final diff. Preserve commands and
   evidence under the cycle evidence directory. Do not run `python
   scripts/dev.py check`; full relevant suites belong to the separately
   planned SQ.V verification session.

## Acceptance criteria

- Focused tests prove the new Static Full behavior is Glass-only, static, and
  limited to source-supported treatments; the existing Glass mapping covers
  `CLEAR`, `PARTLY_CLOUDY`, `CLOUDY`, `RAIN`, and `STORM`, while `SNOW`, null,
  and unsupported conditions are omitted. Decorative semantics do not replace
  or duplicate weather meaning.
- Focused tests show no Full-only marks/highlights leak into None or Subtle,
  and other theme outputs remain unchanged for the affected seam.
- Installed Pixel 3 XL smoke evidence exists for all four pages in the stated
  compact configuration. Now, Hourly, and Daily show the implemented
  source-supported decoration; Details has no weather mark/inclusion overlay.
  Unsupported cases use the no-mark fallback.
- Installed review confirms page identity, current facts, provenance/freshness,
  visible condition text, controls, and inspected semantics remain present;
  navigation/window interactions work, and no weather/request/cache behavior
  changed.
- Verification and page dispositions are recorded with retained evidence.
  The full compact/font-scale 1.3, RTL, High contrast, sparse/missing,
  condition-coverage, and cross-effects acceptance matrix remains pending
  SQ.9A; this plan and closeout must not claim four-page Static Full
  acceptance.

## Verification and evidence

Evidence root: `.codex/test-artifacts/192-complete-four-page-static-full/`.
Retain at minimum:

- `implementation-notes.md` — source recipe audit and omissions;
- `device-config.md` — Pixel 3 XL identity/API, build, screen timeout, Stay
  Awake state, actual viewport/font scale, locale/direction, units,
  contrast/effects, and deterministic fixture identity;
- `focused-jvm-tests.txt` and `focused-android-tests.txt` — exact commands and
  results;
- `screenshots/now.png`, `hourly.png`, `daily.png`, `details.png`, plus
  `sha256sums.txt`;
- `semantics-review.md` — visible facts/controls, relevant hierarchy or
  semantics observations, interaction results, and per-page
  PASS/FAIL/UNVERIFIED disposition;
- `build-lint-contract-workflow.txt`, `diff-check.txt`, and final changed-file
  review notes.

Run these focused checks (with the Pixel 3 XL connected for the instrumented
test), adding or adjusting a narrowly targeted test only if implementation
exposes a coverage gap:

```sh
./gradlew --no-daemon :app:testDebugUnitTest \
  --tests com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest
./gradlew --no-daemon :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionWeatherMarkTest
```

Then run `python scripts/dev.py build`, `python scripts/dev.py lint` if Kotlin
production sources changed, `python scripts/dev.py contract`,
`python scripts/dev.py workflow`, and `git diff --check`. Do not run
`python scripts/dev.py test`, `python scripts/dev.py android-test`, or
`python scripts/dev.py check` as full-suite substitutes in this implementation
slice. Record any unavailable check with its reason. Installed screenshots or
previews alone do not prove functional invariance.

## Risks and assumptions

- The source packet is approved for written design direction, not exact
  candidate SVG geometry or runtime assets. Native rendering may therefore
  support fewer optional decorations; omission is an acceptable fallback
  unless the roadmap exit is materially unmet, in which case record the gap
  and stop dependent work.
- The approved Glass matrix explicitly leaves Snow without a mark. Preserve
  that source gap and null fallback; do not broaden the typed condition model
  or infer condition identities in this visual-only slice.
- A compact smoke pass may reveal a defect that needs broad page redesign or a
  new design decision. Keep corrections within the named Glass Full seam;
  material scope expansion needs a separate plan.
- SQ.9A may find failures despite a successful smoke pass. Preserve raw
  evidence and treat each required matrix cell as still open.

## Out of scope

- SQ.9A's full installed acceptance matrix; SQ.10 Static Full performance/Gate
  B; SQ.11 Subtle; SQ.12 Smoked Quartz None; SQ.13 appearance switching and
  chooser label; SQ.14 broad interaction reconciliation; SQ.15–SQ.16 motion
  and lifecycle; SQ.17A/B layout/localization and appearance acceptance;
  SQ.18 motion performance/branch closeout; SQ.V full suites.
- Candidate SVG/bitmap runtime assets, candidate geometry tracing, newly
  bundled fonts/assets, inclusion overlays, animation, changes to other
  themes, saved-preference migration, weather/data/provider/fixture changes,
  and unrelated page/navigation/layout redesign.
- Full-suite verification, Pixel Fold performance, Gate B measurement, and
  live TalkBack traversal. Do not claim these were verified unless separately
  performed under their own planned boundaries.

## Context audit

This bounded slice is estimated at 30,000–45,000 combined input/generated
tokens, including source/code review, focused test and device iterations,
installed capture review, and evidence recording. The current runtime context
capacity is not exposed by repository workflow checks, so no context percentage
is claimed. Keep SQ.9A's wider matrix in its separate cycle; if execution
approaches the UI workflow's 55% planning threshold, checkpoint the audit,
implementation, and verification evidence before continuing.
