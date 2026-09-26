# Owner decision — TP.2A spacing authority

Date: 2026-09-26
Related cycle: `058-tp2a-approved-tokens-resolver`

## Decision

The owner selected **Make approved JSON authoritative** in response to the
interactive decision prompt. The JSON spacing values are the approved target;
the corresponding Kotlin runtime geometry values must be updated in a later
bounded implementation cycle. Keep the approved JSON unchanged. The direct
field mapping recorded in `inventory-and-blocker.md` stands.

The six runtime targets are:

| Theme | Kotlin property | Current | Approved JSON target |
| --- | --- | ---: | ---: |
| Glass | `geometry.pageStackGap` | 10dp | 12dp |
| Glass | `geometry.gridGap` | 8dp | 10dp |
| Glass | `geometry.panelInset` | 12dp | 14dp |
| Minimal OLED | `geometry.gridGap` | 8dp | 12dp |
| Minimal OLED | `geometry.panelInset` | 12dp | 8dp |
| Terminal | `geometry.pageStackGap` | 8dp | 10dp |

## Gate status

This resolves the authority decision only. No runtime values or approved JSON
were changed, and TP.2A part one remains incomplete until its bounded runtime
alignment and catalog conformance work passes. `TP.2A-partial2` remains gated
until part one passes.
