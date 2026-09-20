# AGENTS.md — Oxygen Weather UI Candidate

This repository is the replacement-track implementation for Oxygen Weather. The earlier Atmosphere Deck UI is retired.

## Product authority

The UI contract is:

```text
Now -> Hourly -> Daily -> Details
```

Use `docs/SPECIFICATION.md` as the product/technical authority, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` as the detailed presentation authority, `docs/ROADMAP.md` for ordered delivery, and `docs/UI_DEVELOPMENT_WORKFLOW.md` for visual development. The upstream reference commit is recorded in `docs/upstream/OXYGEN_SOURCE_REFERENCE.md`.

Do not reintroduce the retired Atmosphere Deck presentation language: no page rail, atmosphere instrument/dial, hourly weather braid, daily fingerprint glyphs, paper palette, or copied old screen composition.


## Required Codex lifecycle

The repository keeps persistent development state under `.codex/`. Before production code changes:

1. Read `docs/SPECIFICATION.md`, `docs/ROADMAP.md`, and `.codex/current.md`.
2. Run `python scripts/dev.py workflow`.
3. A bounded plan under `.codex/plans/` must exist for the work. If `current.md` is `PLANNED`, activate it with `python scripts/codex_cycle.py activate`. If it is `IDLE`, create a plan from a roadmap item before editing production code.
4. Work within the plan's production boundary and out-of-scope limits.
5. Preserve applicable evidence under the cycle-specific `.codex/test-artifacts/` path.
6. Close completed work into `.codex/history/`; do not rely on chat history as the development record.

Do not claim a roadmap slice complete unless its history record states the verification actually performed and any unverified boundary.

## Repository map

- `app/src/main/java/com/oxygen/weather/data/` — canonical/provider-neutral weather data and development fixture.
- `app/src/main/java/com/oxygen/weather/derived/` — experimental historical/derived signals.
- `app/src/main/java/com/oxygen/weather/presentation/` — formatting and typed UI models.
- `app/src/main/java/com/oxygen/weather/ui/` — Compose rendering only.
- `app/src/test/` — deterministic tests.
- `scripts/dev.py` — cross-platform build/test/lint/check command.

## Build and verification

Prefer the Python entry point because it selects `gradlew` or `gradlew.bat` correctly for the host platform:

```sh
python scripts/dev.py test
python scripts/dev.py build
python scripts/dev.py contract
python scripts/dev.py check
```

Before declaring repository-level work complete:

1. Run the smallest relevant test while iterating.
2. Run `python scripts/dev.py check` when the Android SDK/dependencies are available.
3. For visual work, install/render the actual app and capture evidence at the required viewport/font/effects conditions.
4. Run `git diff --check` and inspect the final diff.
5. Report verification that could not run and why.

## Architectural boundaries

- Compose receives presentation models, not provider DTOs or repositories.
- Canonical weather values remain provider-neutral and unit-stable until presentation mapping.
- Observations, model estimates, forecasts, official alerts, historical statistics, and derived values remain semantically distinct.
- Provenance and valid/update time are product data, not debug metadata.
- Missing data must be unavailable/omitted honestly; never substitute zero or a plausible placeholder.
- Alerts must come from authoritative alert providers. Do not derive official warning language from forecast heuristics.
- New derived signals require inputs, interval, range/units, missing-data behavior, limitations, and deterministic tests.

## UI constraints

- Standard Home has one horizontal-swipe owner: the outer page pager.
- Hourly and Daily change windows through visible controls; do not add nested horizontal pagers.
- Global page identity stays visible by name.
- Important weather facts must be visible text and meaningful accessibility semantics.
- Decorative weather marks/scenes cannot be required to understand the forecast.
- Effects Off must remain opaque, static, and complete when implemented as a user setting.
- Themes/effects/layout may change presentation but never weather meaning, navigation semantics, provenance, or accessibility meaning.
- Controls should meet 48dp target guidance where applicable.
- Visual-only changes must not refetch weather or rewrite meteorological meaning to make a screenshot look better.

## UI slice workflow

Every visual slice should identify:

- functional invariants that must not change;
- the visual objective;
- compact/large-font/RTL/effects constraints that apply;
- the actual installed state used for verification;
- focused automated checks and broader regression checks.

Do not treat compilation or a Compose preview as evidence that a visual objective succeeded.
