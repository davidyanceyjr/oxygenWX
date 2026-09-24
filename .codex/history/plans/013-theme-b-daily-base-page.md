# Plan 013 — Theme B Daily base page

Status: Completed
Cycle ID: 013-theme-b-daily-base-page
Roadmap item: R0.8
Created: 2026-09-21

## Objective

Apply the established Theme B monitor composition to the Standard Home Daily
page. The page must present the supplied five-day window through the shared
`DailyForecastRow` component, with a visible Daily identity/range and explicit
Earlier/Later controls, while preserving the existing page/window state and
presentation-only boundary.

## Production boundary

Production changes are limited to the Daily page composition and its direct
layout helper in `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`.
The renderer may use existing shared monitor components and resolved layout
tokens, but must not change their contracts, presentation models, canonical
weather data, repository behavior, pager behavior, or effects resolution.

Focused deterministic coverage may be added only where it protects the
presentation inputs consumed by Daily. Documentation and installed evidence
are part of the cycle record.

## Functional invariants

- Home page order remains `Now -> Hourly -> Daily -> Details` and the outer
  pager remains the sole horizontal-swipe owner.
- Daily shows actual chronological entries supplied by the presentation model;
  a complete horizon is two five-day windows and a sparse horizon is never
  padded, repeated, or fabricated by Compose.
- Each row retains the supplied date/day, condition, numeric low/high or
  unavailable wording, precipitation meaning, units, and spoken summary.
- Earlier/Later changes exactly one supplied five-day window and is disabled at
  the first/last window. Android Back from Daily still returns to Hourly.
- Page identity and selected range remain visible text. Important weather facts
  remain visible independently of the supplemental weather mark.
- Rendering continues to consume `ResolvedAppearance`; there are no raw theme
  identifiers, legacy art-sheet composition, nested pagers, or data refetches.
- Effects Off remains opaque, static, and complete; large text and compact
  360x640 presentation must not clip critical facts or make controls unusable.
- RTL may mirror physical placement, but chronological order remains earliest
  to latest.

## Implementation steps

1. Create `.codex/test-artifacts/013-theme-b-daily-base-page/` and capture
   baseline workflow/contract/test and `git diff --check` results. Inspect the
   installed Daily baseline and the Theme B reference anatomy.
2. Update only the Daily page composition as needed: retain `MonitorHeader`,
   place the supplied rows in the opaque shared section surface, preserve all
   supplied entries without a renderer-side cap, and retain
   `ForecastWindowControls` with the existing window bounds.
3. Add or adjust focused presentation assertions only if the current tests do
   not already prove two five-day windows, chronology, sparse preservation, and
   missing low/high/precipitation values.
4. Run focused workflow, contract, JVM test, lint/build/check, and diff gates
   as available. Build and install the actual app through the repository
   emulator path.
5. Capture compact 360x640 Daily first/last-window states, Later navigation,
   Android Back, representative row hierarchy/semantics, and large-font
   (font scale 1.3) normal and Effects Off states. Inspect for clipping,
   overlap, chronology, and usable controls. Do not run service-level TalkBack;
   record RTL status explicitly.
6. Update completed-fact documentation in `docs/ARCHITECTURE.md`, the adopted
   UI specification, `docs/ROADMAP.md`, and `VERIFICATION.md`; preserve all
   evidence under the cycle artifact directory.
7. Close the cycle only after the history record states exact commands,
   installed evidence, and limitations. Do not advance R0.9 or other roadmap
   items.

## Acceptance criteria

- The installed Daily page visibly uses the Theme B header, opaque section
  surface, shared daily rows, and explicit Earlier/Later controls.
- The first and last supplied windows contain exactly the actual daily entries,
  and navigation changes one window without nested horizontal scrolling.
- Visible text and semantics expose date, condition, low/high, precipitation,
  unavailable values, selected page identity, and control meaning.
- Focused tests and the broadest available repository checks pass, with any
  unavailable verification boundary explicitly recorded.
- Evidence and a completed `.codex/history/` record exist for R0.8.

## Verification and evidence

Expected artifacts live under
`.codex/test-artifacts/013-theme-b-daily-base-page/`, including command logs,
screenshots, and concise hierarchy/visual notes. Required checks are:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

When Android tooling is available, use the actual `oxygen_starter` emulator at
compact 360x640 density 160, repeat affected states at font scale 1.3, and
restore any emulator settings changed for evidence.

## Risks and assumptions

- The existing `DailyForecastRow` may be visually sufficient; avoid changing
  shared component contracts unless a concrete Daily acceptance failure proves
  it necessary, and split such work into a dependent slice if needed.
- The existing presentation model already owns five-entry windowing and date
  formatting; this slice assumes no new date-jump control is required by the
  Daily contract.
- Installed visual evidence may be unavailable if the local Android emulator
  cannot start; this is a limitation to record, not a reason to claim visual
  acceptance from compilation alone.

## Out of scope

- Details or Now Theme B page application.
- Provider networking, repository/cache, refresh/error state, location,
  alerts, units, persisted themes/effects, high contrast, Simple layout, or
  live settings.
- New chart/trend inputs, new presentation-state contracts, or Compose UI-test
  infrastructure.
- Service-level TalkBack verification and release signing/publication.
