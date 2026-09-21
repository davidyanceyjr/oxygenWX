# Plan 015 — Theme B Now base page

Status: Active
Cycle ID: 015-theme-b-now-base-page
Roadmap item: R0.10
Created: 2026-09-21

## Objective

Apply the established Theme B monitor system to the Standard Home Now page.
Now must make the current temperature and condition the primary answer, keep
supporting current measurements and source/update context readable, and retain
the existing page/pager and presentation-data boundary. This is the final
R0 Theme B page-composition slice; it does not revive the retired art-sheet
dashboard or add live alerts/providers.

## Context-budget boundary

This is one bounded page-composition slice under the roadmap rule that a slice
must remain below approximately 45% of one context window. Work is limited to
the Now renderer, focused presentation assertions only where they protect the
renderer inputs, and the explicitly listed documentation/evidence records.
Large screenshots, hierarchy dumps, and build logs belong in the cycle
artifact directory, not in this plan or the history summary.

## Production boundary

The production boundary is `NowPage` in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`, with existing
shared components and resolved appearance tokens as its only UI dependencies.
The cycle may make a minimal direct adjustment to `MetricTile` only if an
installed Now observation proves a shared component cannot display the
supplied text at the required compact/large-font states; such a change must
remain presentation-only and be recorded. No new data, navigation, provider,
repository, or alert contract is part of this plan.

The current presentation inputs are `CurrentPresentation`, `sourceLine`,
`updatedLine`, and the existing optional `Forecast pattern` group. The
renderer must consume those supplied values without parsing, recalculating,
sorting, capping, or inventing fields. If a required Now fact or alert state is
not represented by the current presentation model, record the dependency and
stop the slice rather than widening it into a domain/presentation change.

## Functional invariants

- Home order remains `Now -> Hourly -> Daily -> Details`; the outer Home
  pager remains the sole horizontal-swipe owner.
- The named Now page remains selected and visible. Static content does not
  advance the pager, and Android Back from Now retains normal host behavior.
- Current temperature and condition retain the strongest hierarchy. Apparent
  temperature, humidity, dew point, precipitation, wind, source, and update
  information remain visible text or honest unavailable text as supplied.
- The weather mark is supplemental. The page remains understandable when it
  is absent, opaque, static, or hidden from accessibility semantics.
- Source/provenance and update/freshness wording remains supplied product data;
  the renderer does not relabel model estimates as observations or forecasts.
  No official alert language is derived from weather conditions. Any existing
  alert-summary semantics remain truthful; official alert integration is out
  of scope.
- Effects Off remains opaque, static, and complete. Theme/effects resolution
  may alter presentation only, never weather meaning or semantics.
- Compact 360x640, font scale 1.3, and RTL-capable layouts must preserve
  readable primary facts, usable controls, and chronological/semantic order.
  Interactive targets retain the existing 48dp guidance where applicable.
- No nested pager, new horizontal gesture, refetch, provider DTO, repository,
  raw theme identifier, or legacy Atmosphere Deck composition is introduced.

## Implementation steps

1. Create `.codex/test-artifacts/015-theme-b-now-base-page/` and record the
   pre-edit results of `python scripts/dev.py workflow`, `contract`, `test`,
   and `git diff --check`. Record the existing worktree boundary and do not
   rewrite unrelated changes.

2. Inspect the current Now baseline in `OxygenWeatherApp.kt`, the
   `CurrentPresentation` contract and mapper, `MetricTile`/`MonitorSection`,
   the outer pager/page selector, and existing Now assertions. Compare the
   implementation with the adopted Now anatomy: named identity, source/update
   context, primary current-condition surface, supporting metric surfaces, and
   optional forecast-pattern context. Record what already satisfies the
   contract before changing it.

3. Implement the smallest coherent Now composition correction within
   `NowPage`:

   - retain the vertically scrollable page and resolved page insets/gaps;
   - keep `MonitorHeader` with visible location identity and supplied
     source/update context;
   - use the established opaque monitor surface and resolved typography for
     the primary temperature/condition hierarchy;
   - keep apparent temperature, humidity, and dew point visible as supplied;
   - keep precipitation and wind in shared metric surfaces with their supplied
     headline/supporting wording;
   - render the optional supplied forecast-pattern metrics only when present,
     with their group meaning visible and no fabricated fallback; and
   - preserve the existing `spokenSummary` semantics without hiding visible
     facts that it does not summarize.

   Do not add a chart, alert provider, alert heuristic, new presentation
   field, or alternative page composition. If the current renderer already
   meets a listed point, leave it unchanged and capture that fact.

4. Add or strengthen deterministic tests only at the presentation boundary.
   Protect current temperature/condition text, supplied source/update lines,
   precipitation and wind unavailable wording, optional pattern omission, and
   the existing concise spoken summary. Do not add Compose test infrastructure
   or duplicate mapper logic in UI tests.

5. Run the focused and repository gates after implementation:

   ```sh
   python scripts/dev.py workflow
   python scripts/dev.py contract
   python scripts/dev.py test
   python scripts/dev.py check
   git diff --check
   ```

   Record the selected JDK/Android SDK and exact unavailable command/reason in
   the evidence. Do not repair the toolchain or emulator inside this slice.

6. When Android tooling is available, install the actual debug app using the
   repository emulator path and verify at compact 360x640:

   - Now reached through the named page selector and outer pager;
   - primary temperature/condition hierarchy and location identity;
   - source/update context, supporting current measurements, precipitation,
     wind, and optional pattern content;
   - normal Subtle and debug Effects Off states; and
   - Android Back from Now using normal host behavior.

   Repeat the affected Now state at font scale 1.3 in both effects states.
   Inspect visible text and semantics, clipping/overlap, opacity/static
   behavior, and meaningful weather-mark hiding. Exercise RTL only if the
   local environment supports it; do not claim service-level TalkBack unless
   it is actually run. If the known emulator black-frame/ANR recurs, preserve
   raw evidence and make no installed visual claim.

7. Update only completed facts in the cycle records: the architecture and
   adopted UI specification notes for the Theme B Now composition,
   `docs/ROADMAP.md` to mark only R0.10 complete after acceptance evidence,
   `VERIFICATION.md`, and the cycle history record. Do not advance R1 or add
   live provider, alert, location, cache, settings, or release claims.

## Acceptance criteria

- The source change is confined to the Now page composition and any explicitly
  justified presentation-test/component adjustment in this plan.
- The installed Now page visibly uses the established Theme B header,
  resolved opaque surfaces, primary current-condition hierarchy, supporting
  metric tiles, and optional pattern context without the retired composition.
- Visible text and inspected semantics preserve current meaning, source/update
  context, unavailable wording, page identity, and existing pager behavior.
- Deterministic tests protect the supplied Now values and omission behavior;
  no provider/domain/alert/navigation contract changes are introduced.
- Compact and font-scale 1.3 Subtle/Effects Off evidence is captured when the
  toolchain is available, with emulator, RTL, and TalkBack limitations stated
  precisely when not verified.
- `workflow`, `contract`, `test`, `check`, and `git diff --check` pass for the
  final source state, or the history record names the exact unavailable gate.
- R0.10 is not marked complete until its evidence directory and completed
  history record exist.

## Verification and evidence

Expected artifacts live under
`.codex/test-artifacts/015-theme-b-now-base-page/`, including baseline/final
command logs, test results, toolchain notes, compact and large-font screenshots
or explicit capture failures, page-selector/Back notes, and concise semantic
observations. Raw logs and hierarchy dumps stay in the artifact directory.
Evidence is not inferred from compilation, a Compose preview, or source
review. The final history record must state every unverified boundary.

## Risks and assumptions

- The current `HomePresentation` has no live official-alert result; this plan
  preserves the existing Now alert/absence semantics and does not invent one.
- The existing Now renderer may already satisfy much of the adopted anatomy;
  visual changes must be driven by a concrete installed or source-level gap.
- The local emulator has previously produced black frames/ANR; failure is an
  evidence limitation, not permission to widen this slice into tooling repair.

## Out of scope

- Live forecast providers, repository/cache/refresh state, location, official
  alert provider/integration, units, persisted themes/effects, high contrast,
  Simple layout, settings, release checks, signing, and publication.
- Changes to canonical weather models, derived formulas, presentation state
  contracts, mapper semantics, pager architecture, or Compose test
  infrastructure.
- New charts, trend series, weather heuristics labeled as warnings, or any
  reintroduction of the retired Atmosphere Deck/art-sheet language.
- Full RTL closure and service-level TalkBack verification unless separately
  exercised and recorded as evidence.
