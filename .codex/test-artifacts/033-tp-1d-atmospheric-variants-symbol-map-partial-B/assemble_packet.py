#!/usr/bin/env python3
"""Deterministically assemble the TP.1D proposed review packet revision."""
from __future__ import annotations

import hashlib
import json
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
EVIDENCE = ROOT / ".codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B"
BASE = ROOT / ".codex/test-artifacts/031-tp-1d-owner-packet-decision/packet/tp1d-proposed-r1-cycle029-checklist030"
REV = "tp1d-proposed-r2-symbol033-palette034"
PACKET = EVIDENCE / "packet" / REV
MANIFEST = "SHA256SUMS.txt"

OVERLAYS = [
    "docs/theme-system/design-pack/INTEGRATED_PACK.md",
    "docs/theme-system/design-pack/SOURCE_DECISIONS.md",
    "docs/theme-system/design-pack/renders/README.md",
    "docs/theme-system/design-pack/renders/symbol-source-map.json",
    "docs/theme-system/design-pack/renders/audit_symbol_map.py",
    "docs/theme-system/design-pack/proposals/atmospheric-light-palette.json",
    "docs/theme-system/design-pack/renders/atmospheric-now-light-proposal.svg",
]
PROPOSAL_EVIDENCE = [
    "atmospheric-now-compact-light-proposal-full.svg",
    "atmospheric-now-font-1.3-light-proposal-full.svg",
    "atmospheric-now-rtl-light-proposal-full.svg",
    "atmospheric-now-high-contrast-light-proposal-full.svg",
    "atmospheric-now-effects-off-light-proposal-full.svg",
]

def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def main() -> None:
    if PACKET.exists():
        shutil.rmtree(PACKET)
    PACKET.mkdir(parents=True)
    old_inventory = json.loads((BASE / "SOURCE_INVENTORY.json").read_text())
    source_rows: dict[str, dict] = {}
    # Copy every frozen packet file except its manifest and generated source inventory.
    for src in sorted(p for p in BASE.rglob("*") if p.is_file() and p.name not in {MANIFEST, "SOURCE_INVENTORY.json"}):
        rel = src.relative_to(BASE).as_posix()
        dst = PACKET / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, dst)
        source_rows[rel] = {"packet_path": rel, "source_path": str(src.relative_to(ROOT)), "source_sha256": sha(src), "packet_sha256": sha(dst), "transformation": "byte-for-byte copy from frozen cycle-031 packet"}
    nested_owner = "docs/theme-system/design-pack/OWNER_GUIDE.md"
    nested_owner_path = PACKET / nested_owner
    nested_owner_path.write_text(
        "> **Revision 2 notice:** This retained design-pack guide contains the cycle-031 review choices. "
        "For the current packet revision, digest, and owner response fields, use the [revision 2 owner guide](../../../OWNER_GUIDE.md). "
        "Do not submit a disposition against the superseded cycle-031 revision.\n\n" + nested_owner_path.read_text()
    )
    source_rows[nested_owner]["packet_sha256"] = sha(nested_owner_path)
    source_rows[nested_owner]["transformation"] = "cycle-031 owner guide retained with a revision-2 handoff notice prepended; original decision wording remains unchanged"
    # Overlay only accepted, cycle-033/034-reviewed design-pack files.
    for rel in OVERLAYS:
        src, dst = ROOT / rel, PACKET / rel
        if not src.is_file():
            raise SystemExit(f"required overlay missing: {rel}")
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, dst)
        source_rows[rel] = {"packet_path": rel, "source_path": rel, "source_sha256": sha(src), "packet_sha256": sha(dst), "transformation": "byte-for-byte copy of accepted current design-pack input (cycles 033/034)"}
    # Carry every unique board/crop/phone reference named by cycle 033's map.
    symbol_map = json.loads((PACKET / "docs/theme-system/design-pack/renders/symbol-source-map.json").read_text())
    map_sources: dict[str, str | None] = {}
    for entry in symbol_map["entries"]:
        source_rel = entry["source"].split("#", 1)[0]
        if source_rel.startswith("docs/assets/"):
            previous = map_sources.setdefault(source_rel, entry.get("source_sha256"))
            if previous != entry.get("source_sha256"):
                raise SystemExit(f"conflicting symbol source digest: {source_rel}")
    for rel, expected in sorted(map_sources.items()):
        src, dst = ROOT / rel, PACKET / rel
        if not src.is_file(): raise SystemExit(f"symbol source missing: {rel}")
        if expected and sha(src) != expected: raise SystemExit(f"symbol source digest mismatch: {rel}")
        if dst.is_file():
            if sha(dst) != sha(src): raise SystemExit(f"cycle-031 reference differs from accepted symbol source: {rel}")
            continue
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, dst)
        source_rows[rel] = {"packet_path": rel, "source_path": rel, "source_sha256": sha(src), "packet_sha256": sha(dst), "transformation": "byte-for-byte copy of cycle-033 mapped source board/crop; locator is retained in symbol-source-map.json"}
    # Include the palette checker/review evidence and a compact set of unindexed variants.
    evid_rel = ".codex/test-artifacts/034-tp-1d-atmospheric-light-palette-proposal"
    evidence_files = ["static-review.md", "palette-audit.txt", "contrast-results.json", "source-inventory.json"]
    evidence_files += PROPOSAL_EVIDENCE
    for name in evidence_files:
        rel = f"docs/theme-system/design-pack/review-evidence/palette034/{name}"
        src = ROOT / evid_rel / name
        if not src.is_file():
            raise SystemExit(f"required cycle-034 evidence missing: {src}")
        dst = PACKET / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, dst)
        source_rows[rel] = {"packet_path": rel, "source_path": f"{evid_rel}/{name}", "source_sha256": sha(src), "packet_sha256": sha(dst), "transformation": "byte-for-byte copy; static review evidence retained as unindexed proposed examples"}

    index_path = PACKET / "docs/theme-system/design-pack/PACKET_INDEX.md"
    index = index_path.read_text()
    index += ("\n\n## Proposed revision 2 additions\n\n"
              "This packet retains the complete indexed 20-cell primary matrix and 12-example set. "
              "The cycle-033 proposed theme-specific condition map and cycle-034 proposed Atmospheric light palette are included as review material. "
              "Five extra light-palette examples are unindexed review evidence under `review-evidence/palette034/`; they do not change the established index. "
              "The light palette is derived, proposed, and unapproved; system appearance is a proposed color-only mapping. D31 remains unresolved.\n")
    index_path.write_text(index)

    (PACKET / "REVISION_CHANGELOG.md").write_text("""# Revision changelog — tp1d-proposed-r2-symbol033-palette034

This proposed revision starts from the frozen cycle-031 packet and adds only accepted cycle-033/034 material.

- Added the proposed five-theme × six-condition source map and its deterministic audit; no primary cell or pre-existing indexed example changed.
- Added the derived Atmospheric light-palette proposal, mode mapping, contrast results, and one default light SVG alongside the unchanged dark candidate.
- Added five unindexed light-palette static review examples (compact, font scale 1.3, RTL, High contrast, Effects Off) plus review notes and input inventory.
- Updated the packet-local integrated contract, source decisions, render guidance, and index description to identify the proposal and pending decisions.
- D28, D29, and D31 remain pending. Both D31 options remain available for owner review. This packet is proposed and unapproved.

All packet source paths, source and packet SHA-256 values, and transformations are in `SOURCE_INVENTORY.json`. The frozen cycle-031 packet remains unmodified.
""")
    # Generated packet-local metadata has provenance but no copied source bytes.
    for rel, transform in [
        ("docs/theme-system/design-pack/PACKET_INDEX.md", "generated revision-2 index note; existing 20 cells and 12 examples retained"),
        ("REVISION_CHANGELOG.md", "generated cycle handoff summary"),
        ("OWNER_GUIDE.md", "generated owner response guide; response fields intentionally blank"),
        ("SOURCE_INVENTORY.json", "generated source inventory; self-hash excluded as mathematically self-referential"),
    ]:
        source_rows[rel] = {"packet_path": rel, "source_path": "cycle-033B plan and accepted upstream history", "source_sha256": None, "packet_sha256": None if rel in {"OWNER_GUIDE.md", "SOURCE_INVENTORY.json"} else sha(PACKET / rel), "transformation": transform}
    # Generate inventory before manifest; generated metadata is explicitly identified.
    rows = sorted(source_rows.values(), key=lambda r: r["packet_path"])
    inventory = {
        "revision": REV,
        "status": "proposed and unapproved; owner decisions pending",
        "base_packet": str(BASE.relative_to(ROOT)),
        "base_manifest_sha256": hashlib.sha256((BASE / MANIFEST).read_bytes()).hexdigest(),
        "aggregate_manifest_sha256_definition": "SHA-256 of the UTF-8 SOURCE_INVENTORY payload list, sorted by packet_path; this stable review-payload digest avoids a self-referential hash in owner-guide metadata. Full-file hashes are independently recorded in SHA256SUMS.txt.",
        "source_count": len(rows),
        "sources": rows,
    }
    (PACKET / "SOURCE_INVENTORY.json").write_text(json.dumps(inventory, indent=2, sort_keys=True) + "\n")
    # The inventory itself is covered by the complete file manifest; its own source digest is not self-referential.
    manifest_rows = []
    for p in sorted(x for x in PACKET.rglob("*") if x.is_file() and x.name != MANIFEST):
        rel = p.relative_to(PACKET).as_posix()
        manifest_rows.append(f"{sha(p)}  {rel}")
    (PACKET / MANIFEST).write_text("\n".join(manifest_rows) + "\n")
    aggregate = sha(PACKET / "SOURCE_INVENTORY.json")
    # A complete SHA256SUMS covers OWNER_GUIDE.md. Its self-declared aggregate
    # therefore uses the same manifest rows with the owner-guide row omitted;
    # the full manifest still records and verifies the owner guide itself.
    pre_owner_rows = []
    for p in sorted(x for x in PACKET.rglob("*") if x.is_file() and x.name != MANIFEST):
        rel = p.relative_to(PACKET).as_posix()
        if rel != "OWNER_GUIDE.md":
            pre_owner_rows.append(f"{sha(p)}  {rel}")
    manifest_digest = hashlib.sha256(("\n".join(pre_owner_rows) + "\n").encode()).hexdigest()
    owner = f"""# Owner guide — {REV}

**Status:** Proposed and unapproved. **Revision:** `{REV}`.

**Packet manifest aggregate SHA-256:** `{manifest_digest}`. This is SHA-256 over the sorted `SHA256SUMS.txt` rows with the `OWNER_GUIDE.md` row omitted to avoid self-reference; the full manifest still includes and verifies every packet file except itself. **Source inventory SHA-256:** `{aggregate}`.

This packet is a self-contained proposed TP.1D design-reference review. The inventory traces every copied reference to its source path and SHA-256. The original cycle-031 packet is preserved unchanged. Cycle-033 and cycle-034 inputs are copied byte-for-byte; packet index/changelog/owner guide and inventory are generated metadata. Review evidence under `docs/theme-system/design-pack/review-evidence/palette034/` is explicitly unindexed and supplements the existing example index.

## Owner response

Choose one response for this exact revision: `approve`, `revise`, or `reject`.

Overall response: ____________________  Date: ____________________

## Pending decisions

| Decision | State | Owner response | Date |
| --- | --- | --- | --- |
| D28 | Pending |  |  |
| D29 | Pending |  |  |
| D31 | Pending |  |  |

### D31 alternatives

- **Option A — Accept consistent dark-teal treatment:** retain the proposed catalog-driven treatment across all four Atmospheric pages and both wide examples.
- **Option B — Request a shared blue/scenic treatment:** develop one treatment for Atmospheric pages, then remeasure text contrast and regenerate/review Atmospheric Now, Hourly, Daily, Details, Now wide, and Details wide. The source phone has no separate Details design or codifiable scenic asset. This requires a new packet revision.

Both options are included for equal review; neither is recommended or selected here. The cycle-034 light-palette mapping changes colors only and does not select a scene option. Its light colors are derived because light-specific source art is absent.

## Review limits

No owner approval, installed Android result, runtime system-mode behavior, TalkBack result, TP.1D/TP.1 completion, or TP.2 eligibility is claimed. D28, D29, and D31 remain open until the owner records a response. Preserve this packet unchanged after disposition; requested revisions require a new bounded cycle.
"""
    (PACKET / "OWNER_GUIDE.md").write_text(owner)
    # Owner guide is generated after the main inventory and added to the complete manifest.
    manifest_rows = []
    for p in sorted(x for x in PACKET.rglob("*") if x.is_file() and x.name != MANIFEST):
        manifest_rows.append(f"{sha(p)}  {p.relative_to(PACKET).as_posix()}")
    (PACKET / MANIFEST).write_text("\n".join(manifest_rows) + "\n")
    (EVIDENCE / "assembly-output.json").write_text(json.dumps({"revision": REV, "packet": str(PACKET.relative_to(ROOT)), "files_in_manifest": len(manifest_rows), "source_entries": len(rows), "source_inventory_sha256": aggregate, "packet_manifest_aggregate_sha256_excluding_owner_guide_row": manifest_digest, "sha256sums_file_sha256": sha(PACKET / MANIFEST), "base_packet_manifest_sha256": inventory["base_manifest_sha256"]}, indent=2) + "\n")
    print((EVIDENCE / "assembly-output.json").read_text(), end="")

if __name__ == "__main__":
    main()
