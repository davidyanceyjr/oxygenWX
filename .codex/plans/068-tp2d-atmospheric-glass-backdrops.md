# Plan 068 — Atmospheric and Glass backdrop rendering

Status: Completed
Cycle ID: 068-tp2d-atmospheric-glass-backdrops
Roadmap item: TP.2D-partial3
Created: 2026-09-27
Depends on: PASS for `TP.2D-partial1` (cycle 065) and `TP.2D-partial2` (cycle 067)
Difficulty: **5/10**
Context budget: target at most 30% of a fresh context window; stop before 45%.

## Objective

Verify the installed non-Off shared backdrop rendering for Atmospheric and Glass against their approved field-level source direction. Exercise Standard/Subtle for both themes and High/Subtle for both themes. Preserve caller content, semantic meaning, contrast, and pointer behavior. Show one completed D29 weather mark for each theme over its backdrop in both contrast states. Make one narrow correction only if installed evidence identifies a defect inside the named backdrop branches.

Acceptance uses focused source-informed inspection and explicit runtime assertions. It does not claim pixel equality or complete page-reference matching.

## Visual objective

At the fixed compact phone viewport, the Atmospheric capture reads as its approved vertically varied blue field and the Glass capture reads as its approved cool blue-violet field with broad, restrained light forms. In all four captures, the caller’s weather text, semantic foreground surface, and action remain distinct and usable. Review uses the standalone backdrop asset for field identity; combined phone/sheet imagery supplies theme context only, not page-composition requirements.

## Production boundary

- Inspect and, only if a failed check demonstrates a defect, adjust the Atmospheric and Glass cases in `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt` (`ProductionBackdrop`). No public API changes.
- Add deterministic assertions to `app/src/test/java/com/oxygen/weather/ui/themeengine/ProductionWeatherVisualsTest.kt` only for a missing contract that can be tested without changing the existing D29 matrix.
- Add `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionBackdropAtmosphericGlassTest.kt`, or extend an existing focused backdrop test if reuse is smaller and leaves the test intent clear.
- Update this plan, `.codex/current.md`, and `docs/theme-pack-roadmap.md`; preserve focused and repository verification in `.codex/test-artifacts/068-tp2d-atmospheric-glass-backdrops/`.

If a fix requires resolver/catalog changes, public composable API changes, another theme branch, caller/page composition, or a changed product semantic, record the observed failure and stop this slice. Do not absorb the separate design decision.

## Functional invariants

- The backdrop and D29 mark are decorative. Neither exposes independent weather meaning or intercepts pointer input. Supplied visible condition text remains the weather meaning.
- Caller text, caller-provided semantics, and foreground interaction remain available above the backdrop. One foreground action invokes its callback exactly once.
- High contrast preserves legible text and visible control boundaries over its resolved opaque semantic surfaces. Selected state and condition remain understandable without color alone.
- Effects Off remains opaque, static, complete, and unchanged. Subtle remains static in this slice.
- Theme rendering does not change weather values, presentation models, navigation, fetch behavior, or provenance.
- Keep the approved D29 30-cell matrix unchanged. Use Atmospheric `PARTLY_CLOUDY` and Glass `RAIN`, both existing non-gap cells.

## Approved field direction and source review

Use the source audit and page-atmosphere record as the authority for what each backdrop asset supports:

- **Atmospheric:** `docs/theme-system/design-pack/D31_SOURCE_AUDIT.md`, source id `atmospheric-backdrop`, and the Atmospheric proposals in `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md`. Inspect `docs/assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png` and the separately indexed Atmospheric phone crop. The standalone image is a vertically varied blue field; it does not contain the phone crop’s mountain/forest scene. Review the installed field for a blue-led vertical shift and restrained treatment. Do not require a landscape based on the separate phone crop.
- **Glass:** the corresponding `glass-backdrop` and `glass-phone` records, plus the Glass asset-sheet record, in those D31 documents. Inspect `docs/assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png`, `glass_phone.png`, and the cited Glass asset sheet. Review for cool blue-violet depth, broad soft warm/cool light forms, and restrained highlights while foreground information remains distinct. Scenic phone/sheet content does not establish page composition requirements.

At implementation start, verify each backdrop asset’s SHA-256 against its entry in `D31_SOURCE_AUDIT.md`; record asset path, dimensions, digest, audit locator, and any mismatch. Review only the named backdrop region/field when judging backdrop identity. Keep direct source observations separate from interpretation and installed implementation findings. Do not infer exact values from sparse source samples, or claim visual equality.

For every matrix capture, record whether the installed field retains the broad theme direction above and note any visible deviation. A difference is a failure only when it contradicts supported field direction or obscures/distorts the functional invariants; do not convert subjective preference into an unapproved design requirement. If source evidence does not resolve a material deviation, stop and record it for owner direction.

## Installed test matrix and evidence

Use a deterministic `ProductionBackdrop` host at the project compact baseline of **360 × 640 dp**, **font scale 1.0**, **LTR**, with **Subtle effects**. Render exactly these cases. Font scale 1.3, RTL, Full, and Effects Off are explicit non-goals here; Effects Off is covered by cycle 067.

| Theme | Contrast | Effects | D29 mark | Required evidence |
| --- | --- | --- | --- | --- |
| Atmospheric | Standard | Subtle | `PARTLY_CLOUDY` | Installed capture; text, semantics, click, and appearance assertions |
| Glass | Standard | Subtle | `RAIN` | Installed capture; text, semantics, click, and appearance assertions |
| Atmospheric | High | Subtle | `PARTLY_CLOUDY` | Installed capture; text, semantics, click, resolved-pair and boundary assertions |
| Glass | High | Subtle | `RAIN` | Installed capture; text, semantics, click, resolved-pair and boundary assertions |

The host must include a visible exact condition text equivalent, the decorative mark, an opaque semantic foreground surface using the resolved roles, and one named foreground button with a minimum 48 dp target. Keep the button’s action and condition available through Compose semantics. The mark itself must have cleared semantics. Use the resolved `content` and `surface` roles rather than hard-coded test colors.

For all four cases, assert the supplied condition text is visible and semantically available, the mark does not add a weather-bearing node, the button is discoverable by its caller-provided label, and one click invokes the callback exactly once. Verify the backdrop drawing layer contributes no semantics or pointer handling. For both High cases, assert the fixture uses fully opaque resolved foreground surfaces and calculate the used `content`/`surface` text contrast using the project’s documented WCAG relative-luminance method in `docs/theme-system/design-pack/DETAILS.md`; require at least 4.5:1. If the fixture uses `secondaryData`, apply the documented High-contrast promotion rule (at least 7:1 or promote to `content`). Assert the visible boundary role/opacity as well. Do not substitute token-only assertions for inspection of the installed capture.

Capture exactly four labeled PNGs. Inspect each capture against the matching standalone backdrop and cited theme references. Preserve per-case results and deviations; successful composition or passing assertions alone do not constitute visual acceptance.

## Implementation steps

1. Confirm the cycle 065 and 067 PASS records and inspect the current Atmospheric/Glass branches, resolver’s Standard/High appearance values, the unchanged D29 matrix, installed-test patterns, and artifact export method. Create the cycle evidence directory. Discover and record actual SDK, device/emulator, display, app/build identity, and exact command setup. If installed verification is unavailable, preserve its exact failure and stop BLOCKED.
2. Verify the source asset hashes and dimensions against `D31_SOURCE_AUDIT.md`. Write a concise checklist from the supported field directions above; label direct observations, interpretations, and unestablished details separately.
3. Add or extend the focused installed test for the four matrix cases. Include the caller text/semantics, one 48 dp-or-larger action, selected D29 mark, resolved semantic surface/foreground roles, and High-contrast role/boundary assertions. Export four labeled captures through the established cycle 065/067 pattern.
4. Run the focused installed test. Inspect and record all four actual captures beside their cited standalone source assets. If evidence identifies an in-boundary defect, make one minimal correction in the matching backdrop branch, then rerun the focused JVM and installed checks and reinspect all four captures. If evidence requires scope expansion or leaves a material source-direction question unresolved, retain failure evidence and stop BLOCKED.
5. Run the smallest relevant JVM test while iterating. For final verification run `python scripts/dev.py test`, `python scripts/dev.py build`, `python scripts/dev.py contract`, `python scripts/dev.py workflow`, `python scripts/dev.py check`, and `git diff --check`. Preserve exact output and a specific reason for any unavailable command under the cycle evidence directory.
6. Review the final diff, test output, device/build metadata, source identities, and four inspected captures against this plan. Record exact PASS/BLOCKED status, verification, deviations, and unverified boundaries in `docs/theme-pack-roadmap.md`; keep partial4 planned and dependent on partial3 PASS. Close the cycle only after all required checks have a recorded outcome.

## Acceptance criteria

- All four installed cases pass visible condition text, caller semantics, decorative-mark semantics, named action, exact-once click, and no-backdrop-input checks.
- Both selected D29 marks render in Standard/Subtle and High/Subtle without changing the D29 expected matrix; visible condition text remains sufficient to understand the condition.
- Both High cases pass resolved text/surface contrast and opaque-surface/boundary assertions under the existing documented ratio method.
- Four installed captures are inspected against the correctly identified and hashed sources. Per-theme findings and any deviations are recorded; no pixel-match claim is made.
- Any production correction stays within the Atmospheric/Glass `ProductionBackdrop` branches. An out-of-boundary or unresolved material issue is recorded and stops the slice BLOCKED.
- Focused JVM and Android instrumentation checks, repository `test`, `build`, `contract`, `workflow`, `check`, and `git diff --check` pass, or exact unavailable-host limitations are recorded. No required gate is represented as passing if it did not run.
- Effects Off, Full effects, other theme backdrops, page compositions, app migration, weather meaning, and broader responsive/RTL/large-font behavior remain outside this slice.

## Verification and evidence

Evidence path: `.codex/test-artifacts/068-tp2d-atmospheric-glass-backdrops/`.

Retain:

- cycle 065/067 dependency PASS references;
- both source asset paths, dimensions, SHA-256 values, audit ids/locators, and a short direct-observation/interpretation checklist;
- four installed 360 × 640 captures labeled with theme, contrast, effects, font scale, and layout direction;
- instrumentation output and per-case text, semantics, action, click, contrast, and visible-boundary outcomes;
- device/emulator, SDK, app/build metadata, exact commands and repository outputs;
- capture-by-capture visual findings, any deviations, final diff review, and each unverified boundary with its reason.

This slice does not establish page-level reference matching, pixel-diff acceptance, compact/large-font/RTL behavior, Effects Off (cycle 067), TalkBack service traversal, TP.2E showcase acceptance, TP.3 app compositions, or release acceptance.

## Risks and assumptions

- Cycles 065 and 067 are the required passed dependencies; verify their history and evidence before proceeding.
- An installed Android test host is available. Discover actual host configuration rather than assuming default environment values. An unavailable installed gate blocks this slice.
- High contrast is `ContrastLevel.HIGH` with `ThemeEffectsLevel.SUBTLE`; no additional effects or layout matrix is implied.
- The current Atmospheric implementation contains procedural ridge shapes while the standalone source backdrop does not. Review installed output against the standalone field; the separate phone crop does not authorize a page landscape. If source evidence cannot resolve a material mismatch within the existing branch, preserve evidence and stop for owner direction.
- Difficulty is **5/10**: the production boundary is small, while source identity, installed visual review, contrast checks, and evidence capture require careful verification.

## Out of scope

- Minimal OLED, Instrument, and Terminal backdrop rendering; these are TP.2D-partial4/5.
- Effects Off changes, Full effects behavior, motion policy, or appearance settings persistence.
- D29 identity/treatment changes or edits to any D29 cell beyond using the two specified approved cells as installed examples.
- Resolver/catalog/theme-token changes, new assets, page composition/layout migration, normal-app cutover, weather/data/provider/presentation semantics, navigation/fetch behavior, or settings redesign.
- TP.2E showcase, pixel-diff or exact reference matching, comprehensive responsive/RTL/large-font matrices, TalkBack service traversal, or release acceptance.

## Execution result

PASS after one in-boundary correction pass. The two named standalone assets matched their D31 audit digests. Four final installed cases passed the focused semantics, action, role contrast, and field-level visual review; initial failed-field captures and final captures are retained in the cycle evidence directory. Focused JVM and installed checks and the required repository commands passed. The history record carries the exact verification and unverified boundaries.
