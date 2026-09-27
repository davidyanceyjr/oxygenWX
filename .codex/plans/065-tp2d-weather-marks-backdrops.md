# Plan 065 — TP.2D weather marks

Status: Completed
Cycle ID: 065-tp2d-weather-marks-backdrops
Roadmap item: TP.2D-partial1
Created: 2026-09-26
Draft: Revised bounded plan; mark and backdrop work is split under the 45% context rule.

**Difficulty: 6/10.** This slice translates the owner-approved 30-cell D29 matrix into five theme-specific render styles, including eleven intentional omissions and Terminal's literal tokens. It also needs deterministic mapping checks and installed rendering evidence within existing component contracts.

**Context budget:** target at most 35% of a fresh context window; stop before 45%. This slice owns the D29 weather marks only. The five shared backdrops and combined mark/backdrop installed review are dependent plan `065-tp2d-weather-marks-backdrops-partial2.md`.

## Objective

Implement the owner-approved D29 theme-condition mark matrix in the existing `ProductionWeatherMark` boundary. Marks remain optional decoration beside supplied condition text. This slice establishes mark mapping, treatment, omission, semantics, and installed rendering behavior; it does not change backdrops or page composition.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`: replace generic shared mark drawing with five resolved-style treatments and exact D29 omissions. Keep the public composable signature and existing resolved-theme inputs.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ProductionWeatherVisualsTest.kt`: add exact theme-condition mapping checks for all 30 D29 cells and null.
- `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/`: add focused installed Compose checks and mark-only image captures for D29 presence, omission, and rendering.
- `.codex/plans/065-tp2d-weather-marks-backdrops.md`, `.codex/current.md`, `docs/theme-pack-roadmap.md`, and `.codex/test-artifacts/065-tp2d-weather-marks-backdrops/`: active-cycle state, slice status, and evidence.

No page callers/layouts, component geometry, presentation/domain models, condition mapping, weather data, navigation, settings, resolver/catalog tokens, or backdrop renderer changes. Current callers allocate 76 dp or 52 dp for the Now mark, 38 dp for daily entries, and 26 dp for a Details row; the D29 artwork itself must stay within its specified 40 dp hero or 36 dp forecast footprint. Keep the allocated Compose bounds stable and scale/center drawing inside the allowed D29 footprint. If a treatment cannot fit or remain legible inside the existing caller-provided bound, use only the matrix's documented simplification/omission fallback; if that still requires layout/API changes, record the exact cell and stop this slice.

## Functional invariants

- `docs/theme-system/design-pack/WEATHER_ART.md` is the source of truth for the 30 theme-condition cells, including all eleven gaps and the literal Terminal tokens. Do not substitute D32 forms.
- Keep all six existing `WeatherMarkCondition` identities. A null condition and every D29 gap draw no mark. Supplied visible condition text and its existing semantics remain authoritative.
- Render only the D29 treatment approved for the `ResolvedTheme.weatherMarkStyle`; do not branch on raw theme identity, infer weather, or introduce new facts, labels, severity, time-of-day, animation, or interaction.
- Preserve existing allocated Compose bounds (76/52 dp Now, 38 dp forecast, 26 dp Details) while limiting the drawn D29 artwork to 40 dp for a hero and 36 dp for forecast marks. For Terminal, fit the approved literal token only where its actual supplied modifier permits it; apply the D29 fallback when it would crowd/truncate condition text. No tiny text or caller/layout change to force a token.
- Marks remain decorative and clear their own accessibility semantics. They do not capture pointer input. High contrast may strengthen contrast only. Effects Off removes glow, blur, translucency, and motion and retains an opaque static mark only where it remains legible; otherwise omit it beside visible text.
- Rendering changes do not affect weather values, chronology, provenance, fetches, page identity, navigation, or accessibility meaning.

## Visual objective and installed conditions

Make the implemented theme treatments recognizable as the D29 descriptions at the existing hero/forecast sizes, while keeping each mark subordinate to its adjacent text. This is a mark-component review, not a page-composition or pixel-match acceptance.

- Exercise all five resolved mark styles at Standard/Subtle and Effects Off. Cover all 19 non-gap matrix cells at their applicable D29 drawing footprint (40 dp hero, 36 dp forecast) while preserving the actual caller bounds (76/52 dp Now, 38 dp forecast, 26 dp Details), all eleven explicit gaps, and null condition.
- Include a representative High-contrast case for each of the five styles; confirm contrast changes do not alter the selected treatment.
- Use the installed Compose instrumentation host. Capture labeled mark-only grids or equivalent image evidence so every theme-condition case and omission is identifiable. Visually inspect for clipping, merged detail, unreadable Terminal tokens, and accidental visual similarity across theme styles.
- Include a compact 360 × 640 dp, font scale 1.3 case and an RTL case. RTL may affect placement only; it cannot change mapping, supplied text, or chronology.
- These captures do not verify backdrops or page compositions. The dependent partial2 plan owns backdrop captures and an integrated representative review.

## Implementation steps

1. Audit the D29 matrix, resolved mark styles/palettes, existing call sites/modifiers, current mark code, and installed test harness. Save a 30-cell expected mapping table and call-site bound/footprint inventory under the cycle evidence path. Confirm the eleven omissions and identify how existing callers supply the Terminal mark modifier.
2. Replace the generic path/glyph mapping with the smallest resolved-style implementation that expresses each approved D29 treatment. Keep the existing API and slot geometry; preserve legible static contrast and the documented fallback behavior.
3. Extend JVM tests to assert the exact expected mark/treatment identity for each of the 30 pairs and null, including the eleven omitted pairs. Tests must fail if a gap gains a mark, a valid pair is missing, a Terminal token changes, or two conditions resolve to the same identity within a style. Do not treat unique strings alone as proof of visual rendering.
4. Add focused installed Compose tests that render the matrix at the actual existing slot sizes, assert mark presence/omission and cleared semantics, verify adjacent supplied text remains visible, and cover effects/contrast plus compact/font-scale/RTL cases. Check pointer pass-through using a test host with an interactive sibling or parent; do not add pointer input to the mark.
5. Correct only defects within the named mark boundary. If a treatment requires a new slot, layout/API change, new token, or altered weather text, retain the failing case and stop with the exact boundary recorded.
6. Run focused JVM and Android instrumentation checks, then `python scripts/dev.py test`, `build`, `contract`, `workflow`, `check`, and `git diff --check`. Record exact command results. If installed Android instrumentation or image export is unavailable, record the technical blocker; compilation is not installed rendering evidence.
7. Review the diff against this plan and update the TP.2D execution head in `docs/theme-pack-roadmap.md` with this partial's exact PASS/BLOCKED result. Do not mark TP.2D complete; that requires dependent partial2 to pass.

## Acceptance criteria

- All 30 approved theme-condition pairs and null map exactly to their D29 treatment or omission. All eleven explicit gaps remain mark-free; all valid marks use their approved theme-specific treatment, including exact Terminal tokens where the existing slot supports them. Artwork stays within D29's 40 dp hero/36 dp forecast footprint without changing caller bounds.
- Installed evidence demonstrates the applicable marks and omissions at the existing sizes, alongside unchanged visible condition text. Mark semantics are cleared and pointer input passes through; High contrast and Effects Off meet the stated behavior.
- Compact/font-scale/RTL cases show no clipping, overlap, mapping changes, or condition-text loss. Any D29 fallback used is recorded by cell.
- Focused JVM/instrumentation checks and repository `test`, `build`, `contract`, `workflow`, `check`, plus `git diff --check` pass and are recorded. Otherwise close this partial as blocked with exact output and do not start dependent work.
- No backdrop change, page composition, caller/layout/API change, weather semantic change, or TP.2E/TP.3 acceptance claim is included.

## Verification and evidence

Retain under `.codex/test-artifacts/065-tp2d-weather-marks-backdrops/`:

- D29 30-cell runtime mapping and the existing mark-slot/caller audit;
- focused JVM and Android instrumentation output, device identity, viewport, density, font scale, layout direction, theme, contrast, and effects;
- labeled installed captures covering all mark cells/gaps at applicable slot size, Standard/Subtle and Effects Off, plus representative High-contrast cases;
- assertions/results for null and gap omission, adjacent visible text, decorative semantics, pointer pass-through, compact, large-font, and RTL conditions;
- exact repository command output and final changed-file boundary review;
- a list of any D29 simplification/omission fallback actually used and any unverified limitation.

Evidence is mark-component level. It does not establish backdrop behavior, combined showcase behavior, page-level visual acceptance, service-level TalkBack traversal, or release acceptance.

## Risks and assumptions

- TP.2C is PASS and its Compose instrumentation runner is reusable.
- D29 is owner-approved for all 30 cells, including Terminal tokens and explicit gaps. `WEATHER_ART.md` remains unchanged by this implementation slice.
- The resolved catalog already provides five mark styles and suitable semantic palette roles; no new token is approved here.
- Terminal tokens may not fit some existing constrained modifiers. D29's explicit fit/omission fallback applies; if using it requires component geometry or caller changes, the slice blocks at that cell.
- Shared theme backdrops have been selected for TP.2D by the owner. The implementation and installed backdrop review are deferred to partial2.

## Out of scope

- The five shared `ProductionBackdrop` styles, backdrop/effect visual corrections, and integrated backdrop review; owned by `065-tp2d-weather-marks-backdrops-partial2.md`.
- Page composition, app call sites/layouts, component structure/geometry, and `OxygenWeatherApp`; TP.3 owns composition.
- New marks, conditions, tokens, theme identities, models, weather/data behavior, settings, navigation, or resolver/catalog changes.
- TP.2E showcase, full responsive matrix, TalkBack service traversal, pixel-diff testing, and release acceptance.
