# History 000 — Replacement Prototype Bootstrap

Status: Completed
Cycle ID: 000-replacement-prototype-bootstrap
Roadmap item: R0.1
Closed: 2026-09-20

## Outcome

The project pivoted from the original Atmosphere Deck presentation to a new Oxygen replacement-track prototype. The old Atmosphere Deck UI was retired rather than merged. The new codebase keeps useful provider-neutral weather/data and derived-analysis concepts while implementing a fresh Oxygen-oriented presentation contract.

## Changed

- Application identity moved to `com.oxygen.weather`.
- Fresh Standard Home composition established: Now → Hourly → Daily → Details.
- Single global horizontal pager established.
- Hourly six-entry windows/date controls and Daily five-day windows established against a deterministic 72-hour/10-day fixture.
- New procedural weather/atmospheric presentation implemented.
- Presentation mapping separates UI formatting from domain values.
- Historical synthesis was improved for empirical percentiles, timestamp-aware momentum, bounded derived indices, and unavailable-on-insufficient-data behavior.
- Python cross-platform developer command added.
- Full local 1.0 product/technical specification and dependency-ordered execution roadmap added.
- Persistent `.codex` current/plan/history lifecycle added so future development does not depend on chat history.
- Oxygen upstream product/UI/workflow references pinned to commit `0fcbdcb2736d264fbe6cdca9c8b9530f589f4573`.

## Intentionally not changed/implemented

- No live weather provider path.
- No location/saved-location persistence.
- No offline forecast cache.
- No official-alert provider.
- No persisted units/themes/layout/effects.
- No release signing/publication.
- No old Atmosphere Deck page rail, atmosphere dial, weather braid, forecast fingerprints, or old visual system retained.

## Verification completed

- Source contract confirmed the retired Atmosphere UI markers are absent and exactly one outer `HorizontalPager` is present.
- Dependency-free Kotlin meteorological/presentation harness passed for 72 hourly entries, 10 daily entries, forecast windows/date jumps, and bounded derived values.
- Repository packaging/integrity checks completed.

## Verification not completed

The environment used for the bootstrap did not have a usable Android SDK/dependency network, so a full Gradle Android/Compose build, installed rendering, emulator navigation, and installed screenshot/accessibility evidence were not completed. `VERIFICATION.md` records this limitation.

## Follow-up

Next roadmap slice: R0.2 Installed baseline verification.

Planned execution file: `.codex/plans/001-installed-baseline-verification.md`.
