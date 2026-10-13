# Plan 184 — Approve four-page Smoked Quartz Static Full design source

Status: Planned
Cycle ID: 184-sq2-four-page-static-full-source
Roadmap item: SQ.2
Created: 2026-10-13

## Objective

Produce a complete, owner-reviewable Smoked Quartz Static Full design source
for Now, Hourly, Daily, and Details. Resolve the source-level composition,
palette/contrast, typography, surfaces, decorative mark coverage/fallbacks,
functional-icon inventory, artwork decision, effects-level appearance, and
compact/large-font/RTL/high-contrast/missing-data treatments. Preserve a clear
approval record and leave runtime implementation for later slices.

## Production boundary

Design-source documentation and candidate assets under
`resources/themes/smoked-quartz/`, plus cycle-specific review evidence and
roadmap status. No Android runtime, Compose, resource packaging, provider,
weather-data, navigation, or device-configuration changes.

## Functional invariants

- Keep the product page order and identity `Now -> Hourly -> Daily -> Details`.
- Preserve the existing page content, fixture values, weather meaning,
  chronology, provenance, navigation, and functional control meanings.
- Decorative backgrounds, marks, and overlays never communicate required
  weather facts, severity, alerts, or chronology; missing marks use an explicit
  neutral fallback.
- Effects None remains the approved Smoked Quartz opaque, static, complete
  treatment. High contrast and reduced motion remain independent overlays.
- Keep the five-theme and shared Effects contract intact; no asset is marked
  runtime-approved solely by completing this design cycle.
- Apply Gate B as the selected future performance policy, not as a pass or a
  reason to claim performance without measurements. Physical Pixel Fold
  evidence is required for later performance acceptance; API 37 emulator data
  is supplemental.

## Implementation steps

1. Read the adopted UI/specification authorities and current Glass/Smoked
   Quartz asset worksheet, inventory, manifest, font references, motion study,
   and existing cycle 180–182 installed baseline evidence. Record the specific
   installed configurations used for comparison.
2. Define four page-specific Static Full compositions that retain existing
   content order and page identity. Review page backgrounds, semantic palette
   roles and contrast pairs, typography and scaling/localization roles, glass
   surface hierarchy, decorative mark coverage and fallback, functional icon
   inventory, and artwork inventory (including an explicit “None” decision if
   appropriate).
3. Specify None, Subtle, and Static Full appearances for all four pages;
   state layer/texture limits and how high contrast and reduced motion apply.
   Motion animation, lifecycle, duration, and triggers remain outside this
   slice.
4. Define compact viewport, large-font, RTL, high-contrast, and missing-data
   treatments per affected page. Include visible-text/accessibility requirements
   and 48dp control-target guidance where applicable.
5. Update the Smoked Quartz design source, asset manifest/inventory, and
   coverage/decision matrix so every candidate has an explicit status, role,
   approval state, and fallback. Mark the source ready for owner review; record
   any unresolved item and stop before claiming approval if it affects
   implementation.
6. Compare source decisions against actual installed baseline captures from
   cycles 180–182. Preserve page-by-page comparisons and review evidence in
   `.codex/test-artifacts/184-sq2-four-page-static-full-source/`.
7. Complete focused artifact/manifest/coverage checks, `git diff --check`,
   inspect final changes, and update roadmap/history accurately. Do not activate
   downstream implementation based on a draft or an unrecorded owner decision.

## Acceptance criteria

- A reviewable source covers Now, Hourly, Daily, and Details and names their
  composition, page treatment, semantic palette/contrast, typography, surface
  hierarchy, decorative mark coverage/fallbacks, functional icon inventory,
  and artwork decision.
- The inventory and coverage matrix explicitly settle compact, large-font, RTL,
  high-contrast, missing-data, None, Subtle, and Static Full treatments; each
  page has an installed-baseline comparison or a precise unverified boundary.
- Functional invariants above are checked against the adopted UI contract;
  decorative art remains nonsemantic, and all required weather facts remain
  readable as text with meaningful accessibility semantics.
- Candidate assets have explicit statuses and fallbacks. The owner review
  outcome is recorded; if approval or any required design choice is still
  pending, the source remains unapproved and dependent implementation stays
  ineligible.
- No runtime implementation, production app behavior, or performance pass is
  claimed. Gate B values and Fold scope are linked to cycle 183 as future
  measurement policy.

## Verification and evidence

Use `.codex/test-artifacts/184-sq2-four-page-static-full-source/` for the
source review matrix, owner decision, installed-baseline comparison, changed-
path audit, focused validation output, and screenshot/source references.
Reuse the actual installed Pixel 3 XL, Pixel 5 (`redfin`), and Pixel Fold
captures from cycles 180–182; identify device, API, page, Fold posture, viewport,
font, effects, contrast, and motion state for every referenced capture. Capture
new installed evidence only if a concrete source decision cannot be checked
against those baselines and the relevant device is available.

Run `python scripts/dev.py workflow`, `python scripts/codex_cycle.py validate`,
the focused design-source/manifest checks defined by the implementation plan,
and `git diff --check`. No app tests, build, or full-suite check is required for
this documentation/design-source boundary unless a changed file makes a
specific check relevant. Do not treat SVG validity, compilation, or a mockup
alone as evidence of installed visual success.

## Risks and assumptions

- Cycle 183 records owner selection of Gate B: static Full requires at least
  90% frames within 16.67 ms, p95 frame duration at most 25 ms, added
  settled/peak process PSS at most 16 MiB, and scripted interaction p95 at most
  500 ms. Conditional motion deltas are at most +5 percentage points in
  deadline-miss fraction, +4 MiB peak PSS, and +100 ms interaction p95; one
  pass must return to static within a later approved motion window. These are
  future measurement criteria; this cycle does not measure them or select a
  numeric motion duration.
- The 2023 Pixel Fold is the required named physical device for future
  performance evidence; both closed outer and open inner configurations may be
  relevant to the affected UI. API 37 emulator results are supplemental.
- Existing design worksheet covers Now only and labels assets as candidates.
  Do not infer approval for the other pages or runtime use from the worksheet.
- Design review can identify required owner choices. Keep disputed or
  unresolved choices visible and do not silently invent product policy.
- No devices were attached during cycle 183; cycle 180–182 installed artifacts
  are the accepted comparison baseline unless a newly identified gap requires
  fresh capture.

## Out of scope

- Compose/runtime implementation, Android resources, asset conversion or
  packaging, app behavior, or weather-data changes.
- Installing or measuring Static Full, Subtle, or motion; claiming any Gate B
  threshold passes; defining a numeric motion duration; or approving the
  motion lifecycle study.
- Implementing Subtle or None beyond documenting their design treatment.
- Broad cross-theme acceptance, full-suite unit/connected verification, or
  downstream SQ.3+ implementation slices.
