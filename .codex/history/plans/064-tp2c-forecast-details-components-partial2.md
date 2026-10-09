# Plan 064-partial2 — TP.2C Details/source component contracts

Status: Completed  
Cycle ID: 064-tp2c-forecast-details-components-partial2  
Roadmap item: TP.2C-partial2  
Created: 2026-09-26  
Depends on: PASS for `064-tp2c-forecast-details-components`  
Difficulty: 4/10  
Context budget: target at most 35% of a fresh context window; stop before 45%.

## Objective

Verify and, only if a contract test demonstrates a defect, make the smallest
correction to `ProductionSourceFreshnessPanel` and
`ProductionInspectionMetricGroup`. The installed components must preserve the
caller-supplied provenance facts, metric group structure, strings, and order
for every resolved theme and the bounded responsive cases below.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionDetailsComponents.kt`:
  `ProductionSourceFreshnessPanel` and
  `ProductionInspectionMetricGroup` only. Change
  `ProductionSectionSurface` only for a demonstrated defect in these two
  components.
- `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionDetailsComponentsTest.kt`:
  focused installed Compose contract and rendering checks for those
  components, including screenshot artifact capture described below.
- `.codex/history/plans/064-tp2c-forecast-details-components-partial2.md`,
  `.codex/current.md`, `docs/theme-pack-roadmap.md`, and
  `.codex/test-artifacts/064-tp2c-forecast-details-components-partial2/`:
  active-cycle record, roadmap state, and cycle evidence.

Keep component APIs stable unless a failing contract assertion proves a narrow
correction necessary; record why. Do not change forecast components, page
composition or callers, page/window state, presentation/domain models,
`HomePresentationMapper`, providers, repositories, cache, or weather meaning.

## Functional invariants

- Source and update-time facts remain separately labeled, visible, and exactly
  equal to supplied strings. They remain outside and before the metric groups.
- Group titles are visible and expose heading semantics. Metric labels, values,
  and supplied support render exactly in caller order. Null support is omitted;
  supplied unavailable text is preserved literally.
- An empty metric list omits its group panel and does not invent a metric.
  Components do not infer meaning from group titles or metric labels. This
  follows `docs/theme-system/design-pack/DETAILS.md`'s adopted sparse-group
  rule, which takes precedence over the initial conflicting plan wording.
- All five resolved themes use typed values and `ResolvedTheme`; no raw-theme
  identity branching is added. Contrast and Effects Off retain facts and
  semantics; Effects Off remains opaque and static.
- No page navigation, application state, provenance, accessibility meaning,
  or weather data changes.

## Visual and installed verification

The visual objective is to confirm the existing component treatment keeps the
inspection facts readable and ordered. This is not a Details-page redesign,
pixel-match review, or visual-acceptance claim. Use the installed Compose
instrumentation host at 360 × 640 dp where supported. Do not clip text or shrink
critical text to fit; long content may wrap and scroll.

Run exact-content, ordering, support, empty-group, and heading assertions for
all five themes at Standard contrast/Subtle effects. Run the same facts and
semantics assertions for all five themes at High contrast/Subtle and Standard
contrast/Effects Off. Add these five focused responsive cases; they are a
targeted set, not a Cartesian product:

| Theme | Responsive case |
| --- | --- |
| Atmospheric | Compact 360 × 640 dp, font scale 1.3, long supplied text |
| Glass | RTL, long supplied text |
| Minimal OLED | Compact 360 × 640 dp and RTL |
| Instrument | High contrast, font scale 1.3, RTL, long supplied text |
| Terminal | Effects Off, compact 360 × 640 dp, font scale 1.3, long supplied text |

Capture ten installed-host PNGs through the instrumentation test: one
Standard/Subtle baseline for each theme and one responsive case per theme.
Include the actual theme, viewport, font scale, layout direction, contrast,
and effects values in each filename or companion manifest. Screenshots are
rendering evidence only; no pixel assertions or visual-match claims are part of
this slice. If the test host cannot persist and export these images, record the
exact technical blocker and do not substitute preview images.

## Implementation steps

1. Confirm the forecast predecessor's PASS record. Audit the two components,
   `MetricGroupPresentation`, their design-pack contracts, and current test
   dependencies. Save a concise component-to-contract map and observed gaps in
   this cycle's evidence directory.
2. Add focused Android Compose tests:
   - For every theme, assert exact source/update labels and values, their
     separate ordering before groups, nonempty group headings and heading semantics,
     exact ordered labels/values/support, literal unavailable strings, omitted
     null support, and omission of an empty group panel.
   - For every theme, repeat the content/semantics contract at High
     contrast/Subtle and Standard/Effects Off.
   - Add the five responsive cases above with long text and a scrollable test
     host. Assert each supplied fact is reachable after scrolling, headings
     remain headings, and caller ordering is retained in LTR and RTL.
   - Capture the ten labeled installed-host PNGs with the Compose test root's
     `captureToImage()` result. Write them to the target app's external files
     directory, then export them with `adb pull` to the cycle evidence path.
     Retain a small manifest with the actual test conditions and filenames.
3. Correct only a failing defect inside the named component boundary. If a
   failure needs page integration, source/freshness logic, model semantics, or
   new theme tokens, record the reproducing assertion and stop this slice.
4. Run `python scripts/dev.py android-test --serial <device>` on a compatible
   installed emulator/device. Then run `python scripts/dev.py test`, `build`,
   `contract`, `workflow`, `check`, and `git diff --check`. Record each exact
   command and result separately.
5. Review the final diff against the boundary. At close, update the TP.2C
   entries in `docs/theme-pack-roadmap.md` with this slice's exact PASS/BLOCKED
   result and whether the TP.2C umbrella is complete. Write the cycle history
   record and evidence before marking the cycle complete.

## Acceptance criteria

- Focused instrumentation passes for all stated themes, states, and responsive
  cases; exact supplied content, order, optional support, unavailable text,
  empty-group omission, and nonempty heading semantics are verified.
- Ten screenshots and their actual-condition manifest are retained, or a
  precise capture-environment blocker is recorded. A capture blocker prevents
  claiming the visual evidence criterion passed.
- All six repository checks and `git diff --check` pass and are recorded. If no
  compatible device is available or a required check fails, close BLOCKED with
  exact output and APK/build evidence as applicable; build success alone is
  not a component behavior pass.
- No forecast component, Details page composition, mapper/model, data access,
  weather semantics, new token, pixel-diff gate, TalkBack service traversal,
  or release acceptance is included.
- TP.2C passes only when both this slice and its forecast predecessor pass.

## Verification and evidence

Retain component audit, instrumentation output/results, device identity,
condition manifest, ten screenshots, each repository-check output, and final
boundary review under
`.codex/test-artifacts/064-tp2c-forecast-details-components-partial2/`.
Record exact limitations in history. This slice does not claim page-level app
migration, service-level TalkBack traversal, pixel-level visual acceptance, or
release acceptance. TP.2E owns the broader shared-component showcase; TP.3
owns normal-app page migration and visual acceptance.

## Risks and assumptions

- The cycle-064 Android instrumentation runner and a compatible emulator remain
  available.
- The test host can write screenshots to an app-scoped device directory and
  export them to the cycle evidence directory. If not, the capture gate is
  blocked with the exact failure recorded.
- `MetricGroupPresentation` already carries all content needed by these
  components; no new model or derived value is needed.
- No new geometry or palette token is approved by this plan.

## Out of scope

- Hourly/Daily behavior and tests; completed in the dependent predecessor.
- TP.2D marks/backdrops, TP.2E shared-component showcase, and TP.3 app
  migration/visual acceptance.
- Details page composition, application state, presentation/domain changes,
  provider/repository/cache behavior, settings persistence, or weather meaning.
- Pixel snapshots/assertions, owner visual approval, and accessibility-service
  traversal.
