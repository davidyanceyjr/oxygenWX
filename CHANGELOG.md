# Changelog

## 1.0.0-alpha01 — Oxygen UI rewrite candidate

- Retired the original Atmosphere Deck UI implementation.
- Rebuilt Standard Home as Now, Hourly, Daily, and Details using the Oxygen specification/workflow as design authority.
- Added one outer horizontal pager with explicit Hourly/Daily window controls.
- Added 72-hour and ten-day deterministic forecast fixtures.
- Added provider-neutral weather conditions and provenance.
- Added presentation models/mapping and accessibility summaries.
- Added new Oxygen palette, procedural weather marks, and atmospheric backgrounds.
- Improved historical/forecast synthesis with empirical percentiles and missing-data-safe derived contracts.
- Added cross-platform Python build/test/lint/check helper.
- Pinned the upstream Oxygen design reference to commit `0fcbdcb2736d264fbe6cdca9c8b9530f589f4573`.
- Added `docs/SPECIFICATION.md` as the full replacement-track 1.0 product/technical authority.
- Replaced the high-level roadmap with an ordered, dependency-aware execution roadmap using stable slice IDs.
- Added persistent `.codex/current.md`, `.codex/plans/`, `.codex/history/`, and local evidence conventions.
- Added `scripts/codex_cycle.py` plus `python scripts/dev.py workflow` validation for cross-platform plan/history lifecycle management.

## 0.1.0 — Atmosphere Deck prototype

- Original experimental four-surface Compose prototype.
- Deterministic offline weather dataset and early derived-weather experiments.
