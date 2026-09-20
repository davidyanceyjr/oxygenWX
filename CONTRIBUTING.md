# Contributing to Oxygen Weather

The repository is currently a UI-first v1.0 candidate. Contributions should preserve the separation between provider/domain meaning, derived meteorology, presentation mapping, and Compose rendering.

## Prerequisites

- Git
- Python 3.10+ recommended
- JDK 17
- Android SDK platform 37 / build tools 37.0.0

Use the repository Gradle wrapper through the cross-platform helper:

```sh
python scripts/dev.py check
```


## Development cycle record

Material work should have a bounded plan under `.codex/plans/` and an active pointer in `.codex/current.md`. Start by running:

```sh
python scripts/dev.py workflow
```

Use `python scripts/codex_cycle.py start ...`, `activate`, and `close` to maintain the plan/history lifecycle. Closed work must record what was verified and what was not.

## UI changes

Follow `docs/UI_DEVELOPMENT_WORKFLOW.md`. State functional invariants, visual objective, layout/environment constraints, and verification evidence. Do not reintroduce retired Atmosphere Deck UI concepts.

## Meteorological changes

State inputs, units, time interval, calculation, missing-data behavior, limitations, and semantic class. Derived values require deterministic tests and must not look like official provider products.

## Before a pull request

```sh
python scripts/dev.py check
git diff --check
```

For material visual changes, include installed-app screenshots/evidence for the applicable compact/font/RTL/effects cases.
