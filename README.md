# Oxygen Weather — v1.0 UI Candidate

This repository is a clean-room UI-first rebuild of the earlier Atmosphere Deck prototype using the product specification and development discipline from the Oxygen Weather prototype as design authority.

The old Atmosphere Deck UI has been removed. The project keeps its useful provider-neutral weather models, deterministic demo data, and experimental historical/derived analysis, but the presentation is a new implementation organized around Oxygen's Standard Home contract:

```text
Now -> Hourly -> Daily -> Details
```

The current build is intentionally a **UI candidate**, not a production-ready 1.0 weather service. It runs from a deterministic offline fixture while the provider/cache/location/alert layers are rebuilt behind stable presentation boundaries.

## What changed

- Removed the old Atmosphere Deck page rail, atmosphere dial, hourly braid, daily fingerprints, paper palette, and related visual language.
- Adopted Oxygen's four-page Standard Home information architecture.
- Added a single outer horizontal pager; Hourly and Daily use explicit Earlier/Later controls instead of nested horizontal pagers.
- Expanded the development fixture to a rolling 72-hour hourly forecast and ten-day daily forecast.
- Added provider-neutral weather-condition identity and data provenance.
- Added a presentation mapper so Compose receives formatted, typed presentation models rather than raw provider values.
- Added a new Oxygen-inspired atmospheric palette, procedural weather marks, weather-state background treatment, and compact grouped surfaces.
- Preserved and improved the historical/derived layer. Percentiles now use empirical samples, missing derived windows can remain unavailable, and forecast-change metrics are timestamp-aware.
- Added a cross-platform Python developer entry point that delegates to the repository Gradle wrapper on Windows, macOS, and Linux.

## Current UI

### Now

Current conditions are visually dominant. Temperature, condition, apparent temperature, humidity, dew point, near-term precipitation, wind, source type, and update time remain readable without depending on the atmospheric background.

### Hourly

The UI shows six actual chronological entries at a time in a stable two-column by three-row composition. Visible date controls jump to the first window for a represented date. Earlier/Later changes one six-entry window at a time.

### Daily

The UI shows five real forecast days at a time with condition, low/high, and precipitation meaning. Earlier/Later switches between the first and second five-day windows.

### Details

Provider-normalized measurements remain separate from experimental forecast-pattern and historical-context signals. Derived values are explanatory UI signals, not official meteorological products.

## Clone and build

Prerequisites:

- Python 3.10+ recommended for the cross-platform developer command.
- JDK 17.
- Android SDK platform 37 and build tools 37.0.0.
- Network access on first build unless Gradle/Android dependencies are already cached.

The project includes Gradle launchers for POSIX and Windows. The Python helper chooses the correct launcher automatically:

```sh
python scripts/dev.py build
python scripts/dev.py test
python scripts/dev.py lint
python scripts/dev.py contract
python scripts/dev.py workflow
python scripts/dev.py check
python scripts/dev.py run
python scripts/dev.py screenshot --output .codex/test-artifacts/manual/home.png
```

`run` installs the debug build and launches `com.oxygen.weather/.MainActivity` through adb. `screenshot` captures the selected device/emulator; use `--serial` when multiple devices are attached.

You can also call Gradle directly:

```sh
./gradlew :app:assembleDebug
```

The current application identity is `com.oxygen.weather` and the development version is `1.0.0-alpha01` so this codebase can become the replacement Oxygen repository without a later package migration.

## Product specification and persistent development record

The replacement project now has a full local implementation authority at [`docs/SPECIFICATION.md`](docs/SPECIFICATION.md) and an ordered execution roadmap at [`docs/ROADMAP.md`](docs/ROADMAP.md). Development cycles are persisted under `.codex/` so a fresh Codex session does not depend on prior chat context:

```text
.codex/current.md   current/next bounded cycle
.codex/plans/       implementation plans
.codex/history/     closed-cycle outcomes and verification
.codex/test-artifacts/ installed evidence/logs (normally local/untracked)
```

Validate the workflow with:

```sh
python scripts/dev.py workflow
```

See [`docs/CODEX.md`](docs/CODEX.md) for start/activate/close commands.

## UI development workflow

Visual work follows the workflow captured in [`docs/UI_DEVELOPMENT_WORKFLOW.md`](docs/UI_DEVELOPMENT_WORKFLOW.md): define one visual objective, establish a baseline, make one bounded change, install/render the real app, capture evidence, verify accessibility/layout constraints, then run focused and broader checks.

The adopted UI requirements are summarized in [`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`](docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md). The exact upstream repository/commit used as the reference is recorded in [`docs/upstream/OXYGEN_SOURCE_REFERENCE.md`](docs/upstream/OXYGEN_SOURCE_REFERENCE.md).

## Repository layout

```text
app/src/main/java/com/oxygen/weather/
  data/          canonical demo/domain data
  derived/       experimental historical and forecast synthesis
  presentation/  provider-neutral -> UI presentation mapping
  ui/            new Oxygen-style Compose implementation
app/src/test/     deterministic unit tests
docs/            adopted product/UI rules and engineering notes
scripts/dev.py    cross-platform build/test/lint/check entry point
```

## Production work still required

The UI candidate does not yet include live weather networking, location selection, saved locations, offline cache, official alerts, unit preferences, theme/settings persistence, or release signing. Those are intentionally kept out of this UI replacement so they can be reintroduced behind explicit domain/repository contracts without coupling them to the visual rewrite.

See [`docs/ROADMAP.md`](docs/ROADMAP.md).

## Licensing

This prototype retains its Apache-2.0 license. The upstream Oxygen repository used as a design/specification reference is GPL-3.0-or-later; this candidate does not vendor the upstream Oxygen implementation. Before using this tree as the replacement Oxygen 1.0 repository, reconcile the final repository licensing and notices deliberately.
