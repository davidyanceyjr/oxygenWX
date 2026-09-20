# Replacing the Existing Oxygen Prototype

This tree is intended to become the new implementation base after its UI and production data path are verified.

## Recommended repository migration

Do not replace the existing Oxygen `.git` directory. Keep the existing repository history and copy this candidate's tracked project files into a feature branch of the Oxygen repository.

Suggested sequence:

```text
existing Oxygen repository
-> create replacement-v1 branch
-> remove superseded implementation files on that branch
-> copy this candidate tree (excluding .git/build/local SDK artifacts)
-> reconcile LICENSE/NOTICE
-> run clean-clone build/tests
-> run installed UI evidence workflow
-> open/review replacement PR
-> tag 1.0 only after release gates pass
```

The candidate already uses `com.oxygen.weather` and `rootProject.name = "OxygenWeather"`, so a later package/application-id migration should not be necessary.

## Preserve from the existing Oxygen project as features are rebuilt

The previous prototype contains validated behavior worth reintroducing behind the new boundaries, including provider adapters, cache/offline behavior, location handling, units, appearance preferences, official-alert separation, and accessibility test ideas. Port behavior deliberately; do not copy the retired screen composition into the new UI.

## Licensing

This candidate still carries the Atmosphere Deck Apache-2.0 project license while the existing Oxygen repository is GPL-3.0-or-later. Apache-2.0 code is GPLv3-compatible, but the final replacement repository should state one clear release license and preserve any notices required for retained material. Make that a deliberate release task rather than changing licensing implicitly during UI work.
