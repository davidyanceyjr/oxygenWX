# Plan 109 — Remove the persistent Home page selector

Status: Completed
Cycle ID: 109-ui-remove-persistent-page-selector
Roadmap item: UI.2
Created: 2026-10-03
Plan reviewed: 2026-10-03

## Objective and independently observable outcome

Remove the persistent `Now / Hourly / Daily / Details` row from the standard
Home shell. Keep the current page's name visible and make that title open a
direct-selection menu for all four pages. The row's reclaimed vertical space
returns to the page body. Weather content and values remain unchanged.

The installed Now baseline and owner-approved proposal already exist under
`.codex/test-artifacts/109-ui-remove-persistent-page-selector/`. The owner
approved the title-menu direction on 2026-10-03. This cycle remains PLANNED
until separately activated; no implementation is claimed by the mockup.

## Roadmap relationship and dependencies

- This implements UI.2 in `docs/UI_CONTEXT_ROADMAP.md`; its exit criteria govern
  acceptance.
- The title-menu direction is already approved and recorded in the UI roadmap
  and `proposal-notes.md`; no further owner decision is pending.
- The cycle has no code dependency on the R2.2 forecast-provider work. It
  changes only the existing Home shell and its navigation affordance.
- The production boundary is expected to remain below the repository's 65%
  context-budget limit: one Home-shell control and focused Compose coverage.
  The shared header is also used by the page bodies and component showcase;
  keep those call sites unchanged. Do not absorb adjacent appearance or forecast
  work.

## Production boundary

When activated, limit production changes to:

- the standard Home shell and its new title-menu control in
  `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`; change the
  shared header component only if the shell cannot meet the acceptance criteria
  locally, and then preserve its existing non-shell callers;
- the existing Home composition tests that navigate through the page selector:
  `ProductionHomeCompositionTest`, `ProductionDailyDetailsCompositionTest`,
  `ProductionDailyDetailsSparseCompositionTest`, and
  `ProductionHomeSparseCompositionTest` under `app/src/androidTest/` (the Daily
  sparse class lives in `ProductionDailyDetailsCompositionTest.kt`);
- cycle evidence under
  `.codex/test-artifacts/109-ui-remove-persistent-page-selector/`.

Do not change forecast/presentation data, page content, provider or repository
behavior, theme definitions, or the debug-only component showcase. Keep the
outer pager as the only horizontal-swipe owner. The existing reusable selector
may remain available to the debug showcase; it must no longer render in the
standard Home shell.

## Functional invariants

- Preserve the four semantic pages, their names, order, and existing content.
- Preserve outer-pager swiping, page selection, and Android Back behavior: Back
  from a non-Now page moves one page toward Now, regardless of how that page
  was reached; Back from Now uses normal host behavior.
- Keep the selected page name visibly present at all times.
- From each page, expose direct selection of all four named pages through the
  title-attached menu; selecting a destination updates the existing pager.
- Give the title control and menu items meaningful accessibility names and
  selected-state semantics; make the title control and menu targets at least
  48dp where applicable.
- Preserve weather values/text, chronology, provenance, freshness, unavailable
  states, and presentation semantics. Do not refetch weather or change request,
  cache, or fixture behavior.
- Preserve RTL layout/navigation meaning and all five theme presentations.

## Implementation steps

1. After activation, inspect the shared Home shell, title/header component,
   existing navigation tests, and the approved baseline/proposal. Record the
   activated cycle and device state in the evidence directory. Treat the
   existing 393×852dp, font-scale-1.0, en-US/LTR capture as the approved visual
   reference; do not recreate or modify it. The current shell passes location
   as the shared header's large title and page name as its supporting text;
   preserve location while turning the *page-name line* into the tappable
   title-menu control. Do not accidentally make the location the page picker.
2. Add a discoverable page-name control with the approved downward chevron in
   the Home shell. Its open menu lists Now, Hourly, Daily, and Details in pager
   order, identifies the selected destination semantically, and navigates
   through `PagerState.moveToPage`. Keep one pager state and close the menu on
   selection/dismissal. Remove `ProductionPageSelector` from the standard Home
   shell and let the weighted body receive the released space. Keep the current
   page name visible with the menu closed and open. Preserve the separate
   theme picker and its accessibility target.
3. Update the existing Home Compose tests to use the title menu and cover direct
   navigation from each page to every destination, selected/current-page
   semantics, a 48dp-or-larger title-menu target, and absence of the old
   persistent selector. Retain coverage for page content, hourly/daily window
   controls, and sparse/unavailable data.
4. Add or update navigation coverage proving outer horizontal swiping still
   changes pages in the existing order and Android Back from a non-Now page
   moves one page toward Now. Verify menu dismissal without page change and
   menu selection of the already-current page. Confirm no nested horizontal
   pager or additional global swipe owner was introduced.
5. Build and install the real debug app. Inspect all four pages and the open
   menu in the approved 393×852dp baseline, then capture the responsive and
   navigation evidence described below. Compare weather text and values with
   the existing installed baseline and verify that the only intended visual
   change is removal of the persistent row, title-menu affordance, and use of
   the recovered space.
6. Run focused Android UI coverage, broader repository checks, workflow
   validation, and `git diff --check`. Save command summaries, screenshots,
   device/configuration metadata, and any unverified boundary under the cycle
   evidence directory.

## Acceptance criteria

- The persistent page-selector row is absent on Now, Hourly, Daily, and Details
  in the standard Home shell; page identity remains visible by name.
- On every page, the visible page-name control opens a menu containing all four
  named destinations in order; choosing each one reaches the corresponding
  page, and the menu item for the current page has selected semantics. The
  control has a meaningful accessible name and visible chevron; it and each
  menu item have targets at least 48dp in both dimensions. Dismissal leaves
  the page unchanged.
- Existing outer swipe order and Android Back behavior pass focused automated
  coverage. Hourly/Daily window controls continue to work and no nested global
  horizontal pager is added.
- The installed app shows the title menu and all four pages without clipping or
  overlap at the compact and large-font conditions below. The title remains
  discoverable, and the body uses the freed row space.
- RTL preserves named-page selection and usable menu interaction. All five
  themes retain legible title/menu states. Effects Off is checked using the
  existing debug launch option; it remains opaque, static, and complete.
- Existing weather text, values, provenance, freshness, chronology, unavailable
  behavior, and request/fetch behavior are unchanged.
- Evidence and limitations are recorded at
  `.codex/test-artifacts/109-ui-remove-persistent-page-selector/`, and the
  cycle is closed to history only after the checks actually performed are
  recorded.

## Verification and evidence

### Focused automated checks

- Run `python scripts/dev.py --serial emulator-5554 android-test` on the
  supported AVD. The focused cases live in `ProductionHomeCompositionTest` and
  the updated page-entry helpers/cases in
  `ProductionDailyDetailsCompositionTest.kt` and
  `ProductionHomeSparseCompositionTest.kt`; retain their existing supplied-fact,
  theme, sparse-state, and window-control assertions.
- Assert the 4×4 source/destination menu matrix, current-page menu selected
  semantics, visible page identity with menu closed/open, 48dp title-control
  and menu-item bounds, dismissal and same-page selection, absence of old selector *control*
  semantics in the standard app, horizontal page order, Back toward Now, and
  unchanged hourly/daily window controls. Do not assert absence of the page
  names as text: the title/menu still legitimately contain them. Use the
  selected-page viewport when checking page content because the pager composes
  adjacent pages.

### Installed visual and interaction evidence

Use the actual installed debug application and deterministic development
fixture, not a preview. The existing before image is the 393×852dp,
font-scale-1.0, en-US/LTR Now capture on `oxygen_starter` / `emulator-5554`.
Record the APK digest, emulator/device, viewport, density, font scale, locale,
theme, effects level, foreground activity, and capture method in
`installed-state.md`. Save command outcomes in `verification.md` and the
per-condition pass/fail inspection in `inspection-matrix.md`.

- At 393×852dp and font scale 1.0, capture each of the four pages and the open
  title menu; save as `baseline-now.png`, `baseline-hourly.png`,
  `baseline-daily.png`, `baseline-details.png`, and `baseline-menu.png`. Confirm
  Now weather text/value fidelity against `now-before.png`; the earlier approved
  mockup is a direction reference, not implemented-app evidence.
- At 360×640dp, capture all four pages at font scale 1.0 and 1.3 under
  `compact-fs1/` and `compact-fs1p3/`. Inspect
  clipping, overlap, page-title/menu visibility, control reachability, and the
  reclaimed body area. Capture at least one open-menu state at each font scale;
  include a long location name if the existing fixture/debug path supports it,
  otherwise record that condition as unverified.
- In RTL at 393×852dp, exercise title-menu navigation to all four pages and
  verify the selected page stays named and reachable; save `rtl-page.png` and
  `rtl-menu.png` and record the device layout direction.
- Exercise all five themes with the title closed/open at 393×852dp and save
  captures under `themes/<theme-name>/`. Exercise Effects Off using the existing
  debug launch option, save `effects-off-page.png` and
  `effects-off-menu.png`, and inspect opacity/static completeness. The current
  RTL/effects capabilities are debug verification conditions, not new user
  settings introduced by this slice. Record any requested condition the debug
  launch option cannot reach as unverified.
- Store screenshots and a concise matrix/inspection record in
  `.codex/test-artifacts/109-ui-remove-persistent-page-selector/`.

### Broader checks and record hygiene

- `python scripts/dev.py check` — unit tests, lint, and debug assembly.
- `python scripts/dev.py workflow` — persistent cycle record validity.
- `git diff --check` — whitespace/conflict-marker hygiene.
- Inspect the final diff, including no data/fixture/provider changes and only
  the intended Home navigation production change. Report any visual, RTL,
  accessibility, or device
  condition that could not be verified. Compilation or screenshot evidence by
  itself does not establish the other acceptance conditions.

## Risks and assumptions

- The owner-approved proposal shows the menu closed; its implemented open state
  and interaction remain to be verified in this cycle.
- The approved mockup is an image edit, not an implementation or pixel-level
  specification. Preserve the fixture values and visual intent without using
  the generated image as runtime content.
- The standard app has a shared title identity and an existing page selector;
  use the existing pager callbacks rather than creating a second navigation
  state.
- Effects Off is available for debug verification through the existing launch
  option. Do not add persistence or settings UI as part of this slice.
- Large-font and RTL capture details may depend on the available AVD controls;
  if unavailable, record the concrete limitation and do not claim that
  condition passed.

## Out of scope

- Activating this PLANNED cycle or changing application code during plan review.
- New pages, destinations, navigation models, nested pagers, or changes to the
  current page order or Android Back contract.
- Weather facts, presentation mapping, provenance/freshness, provider/cache
  behavior, fetch timing, or fixture data.
- Page-content redesign, theme redesign, theme picker changes, or changes to
  the debug-only shared-component showcase.
- New persisted Effects Off setting, RTL feature work outside this navigation
  affordance, or unrelated accessibility redesign.
