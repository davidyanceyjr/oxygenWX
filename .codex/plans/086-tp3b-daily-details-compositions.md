# Plan 086 — Daily and Details normal-app compositions

Status: Completed
Cycle ID: 086-tp3b-daily-details-compositions
Roadmap item: TP.3B
Created: 2026-09-30

## Objective and observable outcome

Migrate only Daily and Details to the approved production Home compositions in the installed normal app. Both pages must work across Atmospheric, Glass, Minimal OLED, Instrument, and Terminal, retain supplied weather meaning and navigation, and produce ten installed baseline captures (two pages × five themes) at 393 × 852 dp. This is the composition and functional gate; TP.3C owns measured reference comparison and visual acceptance.

## Authority and dependency

- `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` govern weather meaning, navigation, and accessibility. `docs/theme-pack-roadmap.md` orders TP.3B after TP.3A.
- TP.3A passed in cycle 085, with ten installed Now/Hourly captures and focused normal-app checks; its reference-parity and responsive boundaries remain open. TP.2 shared components and resolver are available.
- Use `docs/theme-system/design-pack/DAILY.md`, `DETAILS.md`, `FOUNDATION.md`, `INTEGRATED_PACK.md`, `CONTENT_AND_STATE_RULES.md`, and `REFERENCE_MEASUREMENT_METHOD.md` for accepted composition direction and integrated refinements. Use typed `HomePresentation` and `HomeLoadState` as the data boundary. Proposed geometry is subject to TP.3C installed comparison.
- Current normal-app Daily has a separately scrolling list and no source/status; Details has source/groups but no status. Confirm exact source and component contracts before editing.

## Production boundary

Normal-app Compose composition for Daily and Details inside the existing `OxygenWeatherApp` path, with focused tests for those pages and the installed baseline evidence. Reuse resolved appearance and TP.2 components; make only page-local or shared-shell changes needed to preserve the specified order, scroll reachability, and status treatment. Do not alter provider, repository, cache, canonical data, presentation values, fetch behavior, theme catalog/resolver, or the global pager architecture.

## Functional invariants

- Keep `Now -> Hourly -> Daily -> Details`, named page identity, one outer horizontal pager, static-tap behavior, and Back stepping toward Now. Now and Hourly TP.3A behavior remains intact.
- Daily shows only the selected supplied window's zero-to-five chronological entries, with exact range label and supplied date, condition, Low/High, precipitation, and spoken summary. No padding, inferred weather, numeric parsing, or nested horizontal swipe. Earlier/Later move one actual window and are visibly named, at least 48 dp, and disabled at bounds.
- Daily source/update and outer load/freshness status remain separate from range and rows. A partial horizon uses only supported wording; no invented end date or missing count.
- Details shows supplied source and update labels, outer status, and nonempty `detailGroups` and metrics in supplied order. Preserve exact label/value/optional supporting text. Keep normalized Conditions, experimental Forecast pattern, and Historical context visibly separated by their supplied group titles; never style derived/history as an observation, official alert, or source forecast.
- Missing values remain supplied unavailable text or omitted slots. No placeholder metric, day, source time, or historical reference is created. Loading, cached, refresh-failed, unavailable, and failed-without-data states retain their supplied status and do not display fixture facts where no data is supplied.
- Themes and effects change presentation only. They do not change facts, callbacks, provenance, request count, group order, or accessibility meaning. Decorative marks/backgrounds carry no required meaning. Effects Off stays opaque, static, and complete.

## Visual objective and environment

Use one vertically reachable body below the persistent named selector. Daily reading order is heading, range, supplied rows, Earlier/Later, source/update, status. Details reading order is heading, source/update, status, then supplied metric groups. Let text and rows grow and wrap; avoid clipping, ellipsizing critical values, or overlaying provenance. Use the five approved theme treatments through resolved appearance. Preserve safe insets and a readable centered width where applicable.

Capture the installed normal app at 393 × 852 dp, font scale 1.0, LTR, Standard contrast, using each theme's primary render effective effects setting; record that setting per image. Compact 360 × 640, font 1.3, RTL, High contrast, full Effects Off, and sparse screenshot matrices are systematic TP.3D acceptance work. Preserve their functional invariants and report any blocker found during TP.3B; do not claim those matrices verified here.

## Implementation steps

1. Trace Daily/Details normal-app composition, typed presentation/load-state paths, TP.2 component APIs, approved page records, and the cycle 085 capture/test harness. Record any contract mismatch before changing production code.
2. Compose Daily as a supplied-order vertical page. Keep selected-window state bounded when windows change, use the existing semantic one-window controls, and include source/update/status after the rows. Make rows and controls reachable in document order, including sparse/empty-window states.
3. Compose Details with source/update/status before the supplied nonempty groups. Keep each group and metric in typed order; allow long content to wrap and scroll. Omit absent groups/support and preserve the distinction between source facts and derived/historical context.
4. Add focused normal-app assertions for exact Daily values, row summaries, sparse/partial behavior, one-window controls and boundary state, and Details group titles/order, metrics, optional support, provenance/status, and absence behavior. Exercise each page under all five themes; verify that theme changes preserve data and control state. Cover load-state behavior at the appropriate typed UI boundary.
5. Run focused tests and source contract. Install the normal `MainActivity` app and capture Daily and Details once for each theme at the baseline configuration. Exercise the Daily window controls; inspect all ten images and hierarchy outputs for page/theme identity, nonblank render, required text/controls, and vertical reachability. Record exact device/build/settings metadata and image hashes.
6. Run broader checks, inspect final diff, and close with exact passed, failed, and unverified boundaries. A missing installed gate or functional/readability blocker prevents TP.3B PASS and stops dependent TP.3C work.

## Acceptance criteria

- Only Daily and Details compositions change in production. Now/Hourly and the shared navigation contract pass regression checks.
- Focused assertions through the production Home path verify supplied Daily chronology and exact text, sparse/unavailable values, one-window Earlier/Later behavior and disabled bounds, source/update/status, plus Details group/metric order, provenance separation, optional support, and honest omission. Important facts have visible text and meaningful semantics.
- Exactly ten installed normal-app PNGs cover Daily and Details × five themes at 393 × 852 dp. Each has verified page/theme identity, no crash or blank screen, no clipped critical content, and required content and controls visible or reachable by vertical scroll. This is functional baseline inspection, not reference parity.
- `python scripts/dev.py test`, `python scripts/dev.py android-test`, `python scripts/dev.py contract`, `python scripts/dev.py check`, and `git diff --check` results are recorded; a check subsumed by another run need only execute once with its covered task identified. Required installed evidence cannot be replaced by preview or TP.2 showcase output.

## Verification and evidence

Store evidence in `.codex/test-artifacts/086-tp3b-daily-details-compositions/`:

- Focused test names/results, JVM and instrumentation XML/logs, contract/check output, and final diff-check result.
- `captures/daily_<theme>.png` and `captures/details_<theme>.png` for all five themes, without missing or duplicate cases; hierarchy captures and `capture-manifest.json` with cycle/build, app variant, device/Android version, viewport dp and pixels/density, system insets, font scale, locale/direction, contrast, theme, page, effective effects, filename, and SHA-256.
- `review.md` with case-by-case functional/readability findings, control/scroll observations, and any unavailable checks with exact cause. Record reference parity, TP.3D matrices, and TalkBack service traversal as unverified unless actually performed.

## Risks and assumptions

- Execution contract finding: normal `OxygenWeatherApp` currently accepts a non-null `HomePresentation` and separate `StatusPresentation`; it does not bind `HomeLoadState` or render loading/failed-without-data directly. TP.3B keeps that shared app API and tests those no-data states at the existing presentation-mapper boundary. Installed Daily/Details captures therefore cover the normal data-bearing fixture, not loading or failure-without-data UI.

- Daily's current separate list scroll can leave source/status outside the desired document flow. Verify reachability on the installed app, including the second window.
- Details source/update describe current data and do not certify derived or historical group provenance. Keep supplied group headings and support text visible; do not reinterpret metric origin from display strings.
- The approved references propose geometry and vary in direct Daily/Details coverage. Apply accepted slot/order and theme grammar now; defer measured reference correction to TP.3C.
- The known Rain glyph gap does not change supplied condition text or semantics. Record a blocker if it prevents TP.3B functional readability; do not absorb unrelated mark work.
- If an Android device/emulator or SDK is unavailable, retain exact failed-gate evidence and close BLOCKED rather than infer installed acceptance.

## Out of scope

- Now/Hourly redesign; all-20 baseline comparison, measured visual correction, or visual-acceptance claim (TP.3C).
- Compact, font-scale, RTL, High-contrast, full Effects Off, sparse screenshot regression matrix and TalkBack service-level closure (TP.3D and later accessibility gate).
- New providers, cache, persistence, location, official alerts, unit/appearance preferences, historical derivations, settings, or weather semantics.
- New chart, gauge, advisory, fabricated data, nested pager, retired Atmosphere Deck presentation, or unrelated theme-pack artwork correction.
