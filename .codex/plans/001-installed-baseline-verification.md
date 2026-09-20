# Plan 001 — Installed Baseline Verification

Status: Planned
Cycle ID: 001-installed-baseline-verification
Roadmap item: R0.2
Created: 2026-09-20

## Objective

Verify the new replacement UI through the installed Android application before production networking and persistence work begins. The independently observable outcome is a retained four-page baseline with working page/window navigation and a documented list of any layout defects.

## Production boundary

The deterministic development `WeatherBundle` fixture through the current presentation mapper and real Compose `MainActivity`/Home path.

No production provider, repository, location, alert, settings, or cache behavior is introduced in this slice.

## Functional invariants

- Now → Hourly → Daily → Details remains the global Standard Home order.
- There is exactly one outer `HorizontalPager`.
- Hourly and Daily use visible controls, not nested horizontal pagers.
- Fixture values, provenance labels, and derived/history semantics do not change to make screenshots look better.
- Missing values are not replaced with fabricated values.
- Retired Atmosphere Deck UI concepts remain absent.

## Implementation steps

1. Run the source/workflow contract checks before UI edits.
2. Build and install the debug application on the selected emulator/device.
3. Capture an untouched baseline for Now, Hourly, Daily, and Details.
4. Exercise Hourly Earlier/Later and at least one date jump.
5. Exercise Daily Earlier/Later.
6. Exercise global page navigation and Android Back from Details through Now.
7. Add focused Compose UI tests only where required to make page/window semantics deterministic.
8. If an observable clipping/overlap/control defect exists, fix only that bounded defect and recapture the affected page.
9. Run focused tests, then the applicable broader debug checks.
10. Record exact commands/results/evidence and close the cycle.

## Acceptance criteria

- Debug application installs and launches through `com.oxygen.weather/.MainActivity`.
- All four page identities are visible and semantically exposed.
- Hourly displays at most six chronological entries per visible window and Earlier/Later changes one window.
- A represented date control lands on the first window containing that date.
- Daily displays at most five chronological rows and Earlier/Later changes one window.
- Back from a non-Now page returns to the previous global page; Back at Now retains normal host behavior.
- No critical overlap/clipping prevents use at the project compact baseline.
- Large-font inspection is recorded even if it identifies follow-up work.
- Source-contract and focused tests pass.

## Verification and evidence

Minimum commands, adjusted for the host as necessary:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py build
python scripts/dev.py run
```

Capture installed evidence under:

```text
.codex/test-artifacts/001-installed-baseline-verification/
```

Recommended files:

```text
now.png
hourly.png
hourly-later.png
daily.png
details.png
verification.txt
```

If connected/UI test infrastructure is added, retain the exact method names and result output in the history record.

## Risks and assumptions

- The current sandbox that created the candidate could not perform this Android build because its SDK/dependency network was unavailable; this plan must run in a prepared Android environment.
- Visual defects may require a follow-up slice if fixing them would materially expand the production boundary.
- Screenshot evidence does not substitute for semantics or navigation assertions.

## Out of scope

- Open-Meteo or MET Norway networking.
- Geocoding/location permission.
- Room/DataStore persistence.
- Official alerts.
- Unit/theme/effects persistence.
- Historical-provider implementation.
- Release signing or publication.
