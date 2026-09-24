# Owner guide — proposed theme pack

**Status:** proposed static design references, not owner-approved.
**Packet revision:** `tp1d-proposed-r1-cycle029-checklist030`
**Review basis:** cycle 029 reviewed design revision plus cycle 030 installed-comparison checklist.

The packet contains 20 primary theme/page references, twelve indexed examples, the exact illustrative fixture and render generator, the four page contracts, cross-pack review, source decisions, and the future installed comparison checklist. The references are not installed-app, interaction, accessibility-service, translated RTL, or approval evidence. Preserve `Now → Hourly → Daily → Details`, six supplied chronological hourly entries and five supplied daily entries, typed units and null behavior, source/update/valid-time provenance, load-state meaning, and derived/history distinctions.

## Decisions requested

### D28 — font family

Affected references: all 20 primary cells and all twelve examples. Choosing B changes the proposed target for the full pack and requires regenerating/reviewing all 32 references.

- **A — Accept proposed reference families:** Fira Sans (Atmospheric), Noto Sans (Glass and Minimal OLED), Noto Sans (Instrument), and Noto Sans Mono (Terminal). These are the reproducible families used in all 32 SVGs. TP.2 implements this choice; TP.3 still compares actual Android metrics.
- **B — Request candidate-family remeasure:** provision Inter / Roboto / Roboto Mono, then regenerate and remeasure all affected references before approval. This changes the proposed target and requires a new reviewed packet revision.

### D29 — weather-mark detail

Affected references: the ten Now/Hourly primary cells and six related examples: [Glass Now compact](renders/glass-now-compact.svg), [Glass Hourly font 1.3](renders/glass-hourly-font-1.3.svg), [Terminal Hourly RTL](renders/terminal-hourly-rtl.svg), [Atmospheric Now wide](renders/atmospheric-now-wide.svg), [Glass Now Effects Off](renders/glass-now-effects-off.svg), and [Instrument Hourly High contrast](renders/instrument-hourly-high-contrast.svg). Choosing B requires a bounded redesign and review of these affected references before pack approval.

- **A — Accept proposed schematic marks:** retain optional 40 dp hero and 36 dp Hourly SVG strokes. They are decorative; adjacent text carries condition meaning. Other condition families and Android drawing remain later implementation/acceptance work.
- **B — Request bounded theme-specific vector-detail pass:** revise marks against source style, then re-render and review affected Now/Hourly references before approval. This requires a new packet revision.

### D31 — Atmospheric palette/scene

Affected references: [Atmospheric Now](renders/atmospheric-now.svg), [Hourly](renders/atmospheric-hourly.svg), [Daily](renders/atmospheric-daily.svg), [Details](renders/atmospheric-details.svg), [Now wide](renders/atmospheric-now-wide.svg), and [Details wide](renders/atmospheric-details-wide.svg). Choosing B requires contrast remeasurement and regeneration/review of all six.

- **A — Accept consistent dark-teal treatment:** retain the proposed catalog-driven treatment across all four Atmospheric pages and both wide examples.
- **B — Request a shared blue/scenic treatment:** develop one treatment for Atmospheric pages, then remeasure text contrast and regenerate/review Atmospheric Now, Hourly, Daily, Details, Now wide, and Details wide. The source phone has no separate Details design or codifiable scenic asset. This requires a new packet revision.

## Reply template

Reply against this exact packet revision and include all fields:

```text
Owner: <name or role>
Response date: <YYYY-MM-DD>
Packet revision: tp1d-proposed-r1-cycle029-checklist030
Manifest SHA-256: <full MANIFEST_SHA256 value from .codex/test-artifacts/031-tp-1d-owner-packet-decision/audit-output.txt>
Overall: approve | revise | reject
D28: A | B
D28 rationale: <text>
D29: A | B
D29 rationale: <text>
D31: A | B
D31 rationale: <text>
Requested changes (if any): <specific references and changes, or none>
```

Approval is actionable only for the exact revision and digest and only when all three decisions are explicit. A requested change returns to a separately planned reference revision. Silence or partial answers leave the decisions open. No disposition is recorded in this packet.
