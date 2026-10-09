# Plan 070 — Terminal backdrop and TP.2D evidence integration

Status: Completed
Cycle ID: 070-tp2d-terminal-backdrop-integration
Roadmap item: TP.2D-partial5
Created: 2026-09-27
Depends on: TP.2D-partial1/2/3/4 PASS in cycles 065, 067, 068, and 069
Difficulty: **4/10**
Context budget: target at most 30% of a fresh context window; stop before 45%.

## Objective

Verify the installed Terminal Subtle backdrop in Standard and High contrast at the compact baseline, with caller content, semantics, and interaction intact. Include one approved D29 mark and one null-condition/no-mark case with honest caller text. Then audit the resolved backdrop mapping and recorded focused, installed, and repository verification for all five TP.2D children. Close TP.2D only if each child has its required passing evidence; do not imply page-level or pixel-match acceptance.

## Visual objective

The Terminal field reads as a black-first surface with a fine, dense, subdued rectangular grid behind legible caller content. The standalone backdrop supports the grid field; the Terminal phone crop shows sparser content separators, which remain a separate visual element. Grid density is judged qualitatively against the source and functional contrast, not assigned unsupported exact source dimensions. High contrast keeps foreground text, surfaces, and boundaries legible while the decorative grid remains subordinate.

## Production boundary

- Inspect and, only if installed evidence demonstrates a defect, adjust the `TERMINAL_GRID` case in `ProductionBackdrop` in `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`. No public API changes.
- Add one focused installed test at `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionBackdropTerminalTest.kt`, following the existing focused backdrop test pattern. Keep the existing D29 30-cell expected matrix unchanged and run its existing test as a regression check.
- Run `ProductionWeatherVisualsTest`'s existing five-style backdrop-resolution and Effects Off assertions. Add or change resolver/render-style assertions only if the audit identifies a concrete missing or incorrect contract; any needed change outside the Terminal backdrop branch blocks this slice.
- Update this plan, `.codex/current.md`, `docs/theme-pack-roadmap.md`, and evidence under `.codex/test-artifacts/070-tp2d-terminal-backdrop-integration/`.
- If correction requires resolver/catalog/token changes, another theme branch, public composable API, page composition, or weather semantics, record findings and stop this slice BLOCKED.

## Functional invariants

- Backdrop and weather mark are decorative and expose no independent weather meaning or pointer handling. Caller-provided condition text remains visible and understandable, including when `WeatherMarkCondition` is null.
- Caller semantics remain available; a named foreground action of at least 48 dp invokes its callback exactly once. Taps on empty backdrop do not trigger that action.
- The High contrast case retains resolved opaque foreground surfaces and satisfies the project contrast rules for text and visible boundaries. Meaning does not rely on color alone.
- Effects Off remains opaque, static, complete, and unchanged. Subtle remains static in this slice.
- The approved D29 matrix, weather values, provenance, navigation, fetch behavior, and all other theme renderings remain unchanged.

## Source review and installed matrix

Before implementation, verify the `terminal-backdrop` source identity against `docs/theme-system/design-pack/D31_SOURCE_AUDIT.md`: `docs/assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png`, 1440 × 3200 px, SHA-256 `5cb511e9a05b7dbfb7775ea1385961d56ed2d726438d55e5c747f51a661448a2`. Review the matching `terminal-phone` crop and the `terminal-now` entry in `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md` for context, keeping direct observation separate from interpretation. The phone crop is not a page-composition target. The source proposal specifically warns that its sparse dashed content rules and the standalone dense grid are distinct.

Use a deterministic installed `ProductionBackdrop` host at **360 × 640 dp**, **font scale 1.0**, **LTR**, and **Subtle** effects. Capture exactly these two cases:

| Theme | Contrast | Mark and visible caller text | Required evidence |
| --- | --- | --- | --- |
| Terminal | Standard | `WeatherMarkCondition.CLEAR` / `terminal:[SUN]` with visible `Clear` text | Grid field, text/semantics, decorative mark, action, backdrop-tap behavior |
| Terminal | High | null `WeatherMarkCondition` / no mark with visible `Condition unavailable` text | Same, plus resolved contrast, opaque surface, and visible boundary |

Assert the selected D29 signatures using the existing `markStyleSignature` contract before composing the host: Terminal/CLEAR resolves to `terminal:[SUN]`; Terminal/null resolves to no mark. The null case exercises the null input contract; it does not change or replace the existing explicit Terminal RAIN/STORM/SNOW source-gap cells. Do not alter the shared D29 matrix. Confirm the caller condition text is exposed and the decorative mark adds no spoken weather meaning. Use resolved theme roles for the caller surface, content, and boundary. For High contrast calculate content/surface and boundary/surface ratios using the documented method in `docs/theme-system/design-pack/DETAILS.md`; require at least 4.5:1 for text and 3:1 for the visible boundary. Retain two labeled captures.

## Implementation steps

1. Confirm PASS history/evidence for partial1 through partial4 (cycles 065, 067, 068, 069). Record cycle 066 as a blocked earlier attempt superseded by the passing cycle 067, not as a passing child. Inspect the current Terminal backdrop branch, resolver styles for all five themes, existing D29 matrix, test harness, and artifact export. Discover and record actual SDK/device/emulator/display and app/build identity. If installed verification is unavailable, preserve its exact failure and stop BLOCKED.
2. Verify the Terminal backdrop digest/dimensions against the D31 audit. Record source observations, the supported interpretation, and the limitation that standalone source coordinates do not define installed dp density.
3. Add the two-case installed host and assertions for condition text/semantics, mark or intentional no-mark behavior, named 48 dp action, exact-once callback, empty-backdrop pointer delivery, and High contrast roles. Preserve and run the unchanged D29 matrix test and the existing resolver/effects-off tests. Capture two labeled PNGs.
4. Run focused JVM and installed tests. Inspect both installed captures alongside the standalone source. If evidence demonstrates an in-boundary defect, make at most one narrow correction in the Terminal grid branch, then rerun focused checks and reinspect both captures. Stop BLOCKED for an out-of-boundary defect or unresolved material source conflict.
5. Audit the five resolved styles (Atmospheric, Glass, Minimal OLED, Instrument, Terminal), Effects Off guarantee, and each TP.2D child history/evidence against its scoped focused, installed, and repository checks. Do not rerun earlier visual matrices; identify exact records, results, and limitations. A missing or failed child gate keeps the TP.2D umbrella open and stops umbrella closure.
6. Run the repository gates for this slice: `python scripts/dev.py test`, `build`, `contract`, `workflow`, and `check` (including lint), plus `git diff --check`. Preserve exact outputs, focused test results, source identity, captures, review findings, and device/build metadata in the cycle evidence directory. Export installed captures from the instrumentation app's external-files directory into `.codex/test-artifacts/070-tp2d-terminal-backdrop-integration/` and verify both files open before recording them as evidence.
7. Review the final diff and complete evidence. Update the roadmap with Terminal's actual result, the five-style mapping/evidence audit, and exact TP.2D umbrella disposition. Mark TP.2D complete only if all five children have their required focused, installed, and repository PASS records; otherwise keep it open and record the precise blocker. Close this cycle with exact verification and limitations.

## Acceptance criteria

- The two installed Terminal cases preserve caller text and semantics, decorative mark/no-mark expectations, a named action of at least 48 dp, exact-once foreground action, and no empty-backdrop action.
- High contrast passes resolved text contrast, opacity, and visible-boundary checks; two captures are visually reviewed against the correctly identified standalone backdrop and Terminal context. No page or pixel parity claim is made.
- Source digest/dimensions and direct observations are recorded. No exact density is inferred from image dimensions.
- Existing D29 30-cell expectations are unchanged and its focused matrix test passes.
- The five resolved backdrop styles are reviewed; each TP.2D child has an auditable focused, installed, and repository result or the umbrella remains open with the missing/failed gate named. Cite the exact cycle history/evidence locations and do not infer a gate from a screenshot or build result.
- Focused JVM/instrumentation, repository `test`, `build`, `contract`, `workflow`, `check`, and `git diff --check` have exact recorded outcomes. No unavailable gate is described as passing.
- Production edits, if any, stay in the Terminal backdrop branch. A need to change another boundary blocks this slice.

## Verification and evidence

Evidence path: `.codex/test-artifacts/070-tp2d-terminal-backdrop-integration/`.

Retain the partial1–partial4 PASS references; cycle 066's superseded blocked context; Terminal source path, dimensions, digest, and audit locator; device/emulator, SDK, display, app/build identity; exact commands and outputs; two labeled captures; per-case text, semantics, interaction, contrast, and field findings; five-style mapping review; child evidence audit; final diff review; and every unverified boundary with its reason.

This slice does not establish page composition, pixel equality, Full effects, large-font/RTL behavior, TalkBack service traversal, provider/fetch or weather-value behavior, TP.2E showcase acceptance, or release acceptance.

## Risks and assumptions

- The Terminal grid already exists at 24 dp spacing and 0.30 outline alpha; installed review determines whether this reads as a restrained field under both contrast levels. A visual concern without support in the audited source or a functional contrast failure is not grounds for a speculative correction.
- The standalone source has a dense rectangular grid; the phone crop's sparse separators do not define grid spacing. The approved qualitative direction is sufficient for field-level review, but no numeric density is claimed.
- The installed host and emulator are available. Verify rather than assume; unavailability blocks visual acceptance.
- The five TP.2D children are cycles 065, 067, 068, 069, and this cycle. Cycle 066 is a blocked earlier attempt for partial2 and is superseded by cycle 067.
- Difficulty is **4/10**: the runtime branch is narrow and existing component tests provide a pattern; installed evidence and cross-cycle gate audit add careful but bounded verification.

## Execution record

PASS. The D31 standalone source identity was verified at the planned dimensions
and SHA-256. Installed review found the initial 24 dp Terminal grid visibly
coarser than the source's fine dense field. One permitted correction changed
only the `TERMINAL_GRID` spacing to 16 dp; final Standard and High captures were
reviewed and pass the scoped field-level objective. No resolver, other backdrop
branch, D29 identity, or public API changed.

The focused `ProductionWeatherVisualsTest` and focused
`ProductionBackdropTerminalTest` both passed after the correction. Both final
captures and instrumentation output are under
`.codex/test-artifacts/070-tp2d-terminal-backdrop-integration/installed/` and
that directory's root log files. Repository `test`, `build`, `contract`,
`workflow`, and `check` (including lint) passed; `git diff --check` passed.
Device/build identity, source observations, per-case results, and the child-gate
audit are retained beside them.

The child audit confirms partial1/2/3/4/5 PASS in cycles 065/067/068/069/070.
Cycle 066 remains a superseded blocked partial2 attempt and is not counted as a
PASS. TP.2D is complete at the shared-component field level. Page composition,
pixel parity, TP.2E, TP.3, Full effects, large-font/RTL Terminal cases, and
TalkBack service traversal remain outside this slice.

## Out of scope

- Changes to D29 identities/matrix, other backdrop branches, resolver/catalog/tokens, new artwork, theme selection, page composition, normal app cutover, forecast values, provider/data/presentation semantics, navigation, fetch behavior, or settings.
- Effects Off changes, Full effects, responsive/large-font/RTL matrices, TalkBack service traversal, pixel-diff or page-reference parity, TP.2E, TP.3, and release acceptance.
