#!/usr/bin/env python3
"""Independent, standard-library audit of the proposed TP.1D revision."""
from __future__ import annotations

import hashlib
import json
import re
import sys
from pathlib import Path
from urllib.parse import unquote, urlparse

ROOT = Path(__file__).resolve().parents[3]
EVIDENCE = ROOT / ".codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B"
PACKET = EVIDENCE / "packet/tp1d-proposed-r2-symbol033-palette034"
BASE = ROOT / ".codex/test-artifacts/031-tp-1d-owner-packet-decision/packet/tp1d-proposed-r1-cycle029-checklist030"
MANIFEST = "SHA256SUMS.txt"
errors: list[str] = []

def sha(p: Path) -> str:
    return hashlib.sha256(p.read_bytes()).hexdigest()

def fail(message: str) -> None:
    errors.append(message)

def slug(text: str) -> str:
    text = re.sub(r"[`*_]", "", text).strip().lower()
    text = re.sub(r"[^\w\- ]", "", text)
    return re.sub(r"\s+", "-", text)

def audit_manifest() -> tuple[set[str], str]:
    lines = (PACKET / MANIFEST).read_text().splitlines()
    listed: set[str] = set()
    for line in lines:
        try:
            expected, rel = line.split("  ", 1)
        except ValueError:
            fail(f"malformed manifest line: {line}"); continue
        if rel in listed: fail(f"duplicate manifest path: {rel}")
        listed.add(rel)
        p = PACKET / rel
        if not p.is_file(): fail(f"manifest target missing: {rel}")
        elif sha(p) != expected: fail(f"manifest hash mismatch: {rel}")
    actual = {p.relative_to(PACKET).as_posix() for p in PACKET.rglob("*") if p.is_file() and p.name != MANIFEST}
    if listed != actual:
        fail(f"manifest coverage mismatch: missing={sorted(actual-listed)} extra={sorted(listed-actual)}")
    return listed, sha(PACKET / MANIFEST)

def audit_base() -> int:
    count = 0
    allowed_baseline_diffs = {
        "docs/theme-system/design-pack/INTEGRATED_PACK.md",
        "docs/theme-system/design-pack/SOURCE_DECISIONS.md",
        "docs/theme-system/design-pack/renders/README.md",
        "docs/theme-system/design-pack/OWNER_GUIDE.md",
        "docs/theme-system/design-pack/PACKET_INDEX.md",
        "SOURCE_INVENTORY.json",
    }
    lines = (BASE / MANIFEST).read_text().splitlines()
    for line in lines:
        expected, rel = line.split("  ", 1)
        p = BASE / rel
        if not p.is_file() or sha(p) != expected: fail(f"frozen cycle-031 changed/missing: {rel}")
        target = PACKET / rel
        if not target.is_file(): fail(f"cycle-031 packet file omitted from revision 2: {rel}")
        elif sha(target) != expected and rel not in allowed_baseline_diffs:
            fail(f"cycle-031 packet file changed outside explicit packet-document updates: {rel}")
        elif rel in {
            "docs/theme-system/design-pack/INTEGRATED_PACK.md",
            "docs/theme-system/design-pack/SOURCE_DECISIONS.md",
            "docs/theme-system/design-pack/renders/README.md",
            "docs/theme-system/design-pack/PACKET_INDEX.md",
        }:
            # The three upstream contract files must match the accepted current files;
            # the packet index has only its declared revision-2 section appended.
            source = ROOT / rel
            if rel != "docs/theme-system/design-pack/PACKET_INDEX.md" and sha(target) != sha(source):
                fail(f"packet contract overlay differs from accepted input: {rel}")
            if rel == "docs/theme-system/design-pack/PACKET_INDEX.md":
                if not target.read_text().startswith(p.read_text()): fail("packet index changed outside appended revision-2 note")
        count += 1
    if sha(BASE / MANIFEST) != "5326956c75653790754a6c87cb53eb40c94e2ad2dc53cc85cb02cc892b294663":
        fail("cycle-031 manifest digest no longer matches recorded frozen digest")
    return count

def audit_sources(actual_paths: set[str]) -> dict:
    inv = json.loads((PACKET / "SOURCE_INVENTORY.json").read_text())
    rows = inv["sources"]
    paths = [r["packet_path"] for r in rows]
    if len(paths) != len(set(paths)): fail("duplicate SOURCE_INVENTORY packet_path")
    # The inventory lists every packet file except its own SHA256SUMS.txt manifest.
    expected = actual_paths
    if set(paths) != expected:
        fail("source inventory coverage mismatch")
    for r in rows:
        rel = r["packet_path"]
        dst = PACKET / rel
        if rel == "SOURCE_INVENTORY.json":
            if r.get("source_sha256") is not None or r.get("packet_sha256") is not None: fail("inventory self hash must be explicitly excluded")
            continue
        if rel == "OWNER_GUIDE.md":
            if r.get("source_sha256") is not None or r.get("packet_sha256") is not None: fail("generated owner guide self digest must be excluded from source inventory")
            continue
        if not dst.is_file(): fail(f"inventory target missing: {rel}"); continue
        if r.get("packet_sha256") != sha(dst): fail(f"packet hash mismatch in source inventory: {rel}")
        source = r.get("source_path")
        if source and source != "cycle-033B plan and accepted upstream history":
            sp = ROOT / source
            if not sp.is_file(): fail(f"source missing: {source}")
            elif r.get("source_sha256") != sha(sp): fail(f"source hash mismatch: {source}")
            elif r.get("source_sha256") != r.get("packet_sha256"):
                expected_transform = "cycle-031 owner guide retained with a revision-2 handoff notice prepended; original decision wording remains unchanged"
                if rel != "docs/theme-system/design-pack/OWNER_GUIDE.md" or r.get("transformation") != expected_transform:
                    fail(f"unrecorded or unapproved transformation: {rel}")
    return inv

def audit_links() -> int:
    checked = 0
    for md in PACKET.rglob("*.md"):
        text = md.read_text(errors="replace")
        heads: set[str] = set()
        for line in text.splitlines():
            m = re.match(r"^#{1,6}\s+(.+?)\s*#*\s*$", line)
            if m: heads.add(slug(m.group(1)))
        for target in re.findall(r"!?\[[^\]]*\]\(([^)]+)\)", text):
            target = target.strip().split()[0].strip("<>")
            if not target or target.startswith(("http:", "https:", "mailto:", "data:")): continue
            parsed = urlparse(target)
            path = unquote(parsed.path)
            if path:
                resolved = (md.parent / path).resolve()
                if not resolved.exists(): fail(f"broken local link: {md.relative_to(PACKET)} -> {target}")
                checked += 1
            elif parsed.fragment and parsed.fragment not in heads:
                fail(f"broken heading anchor: {md.relative_to(PACKET)} -> {target}")
            if parsed.fragment and path:
                resolved = (md.parent / path).resolve()
                if resolved.suffix.lower() == ".md" and resolved.is_file():
                    dest_text = resolved.read_text(errors="replace")
                    dest_heads = {slug(m.group(1)) for line in dest_text.splitlines() if (m := re.match(r"^#{1,6}\s+(.+?)\s*#*\s*$", line))}
                    if parsed.fragment not in dest_heads: fail(f"broken linked heading anchor: {md.relative_to(PACKET)} -> {target}")
    return checked

def main() -> int:
    if not PACKET.is_dir(): return 2
    listed, manifest_file_sha = audit_manifest()
    base_count = audit_base()
    inv = audit_sources(listed)
    index_path = PACKET / "docs/theme-system/design-pack/renders/index.json"
    index = json.loads(index_path.read_text())
    primary = [x for x in index if x.get("condition") == "primary"]
    cells = {(x["theme"], x["page"]) for x in primary}
    expected_cells = {(t, p) for t in ("atmospheric", "glass", "minimal_oled", "instrument", "terminal") for p in ("Now", "Hourly", "Daily", "Details")}
    if cells != expected_cells or len(primary) != 20: fail("primary index is not exactly 20 unique theme/page cells")
    targets = [x["file"] for x in index]
    if len(targets) != len(set(targets)): fail("duplicate render index target")
    if len(targets) != 32: fail(f"expected 32 established indexed examples, got {len(targets)}")
    for target in targets:
        if not (PACKET / "docs/theme-system/design-pack/renders" / target).is_file(): fail(f"missing indexed target: {target}")
    # All 32 indexed renders and fixture remain byte-identical to the frozen packet.
    for target in targets + ["fixture.json"]:
        rel = f"docs/theme-system/design-pack/renders/{target}"
        if sha(PACKET / rel) != sha(BASE / rel): fail(f"fixture/indexed render changed from cycle-031: {target}")
    sm_path = PACKET / "docs/theme-system/design-pack/renders/symbol-source-map.json"
    sm = json.loads(sm_path.read_text())
    mapping_entries = sm.get("mappings", sm.get("entries", []))
    if len(mapping_entries) != 30: fail(f"cycle-033 symbol map expected 30 condition mappings, got {len(mapping_entries)}")
    if not (PACKET / "docs/theme-system/design-pack/renders/audit_symbol_map.py").is_file(): fail("cycle-033 symbol-map audit missing")
    if not (PACKET / "docs/theme-system/design-pack/proposals/atmospheric-light-palette.json").is_file(): fail("Atmospheric light proposal missing")
    if not (PACKET / "docs/theme-system/design-pack/renders/atmospheric-now.svg").is_file() or not (PACKET / "docs/theme-system/design-pack/renders/atmospheric-now-light-proposal.svg").is_file(): fail("Atmospheric dark/light palette examples missing")
    extras = ["atmospheric-now-compact-light-proposal-full.svg", "atmospheric-now-font-1.3-light-proposal-full.svg", "atmospheric-now-rtl-light-proposal-full.svg", "atmospheric-now-high-contrast-light-proposal-full.svg", "atmospheric-now-effects-off-light-proposal-full.svg"]
    for n in extras:
        if not (PACKET / "docs/theme-system/design-pack/review-evidence/palette034" / n).is_file(): fail(f"missing explicitly inventoried palette example: {n}")
    owner = (PACKET / "OWNER_GUIDE.md").read_text()
    for s in ["approve`, `revise`, or `reject", "D28 | Pending", "D29 | Pending", "D31 | Pending", "Option A — Accept consistent dark-teal treatment", "Option B — Request a shared blue/scenic treatment", "does not select a scene option", "unapproved"]:
        if s not in owner: fail(f"owner decision state missing/incorrect: {s}")
    if "Overall response: ____________________  Date: ____________________" not in owner: fail("owner response/date fields are not blank")
    # Verify the owner-guide aggregate by independently normalizing only its self row.
    manifest_lines = (PACKET / MANIFEST).read_text().splitlines()
    normalized = [line for line in manifest_lines if not line.endswith("  OWNER_GUIDE.md")]
    aggregate = hashlib.sha256(("\n".join(normalized) + "\n").encode()).hexdigest()
    if aggregate not in owner: fail("owner guide manifest aggregate digest missing or incorrect")
    links = audit_links()
    result = {"status": "PASS" if not errors else "FAIL", "revision": inv["revision"], "packet_files": len(listed) + 1, "manifest_entries": len(listed), "manifest_file_sha256": manifest_file_sha, "manifest_aggregate_excluding_owner_guide_row": aggregate, "source_entries": len(inv["sources"]), "frozen_cycle031_manifest_entries": base_count, "primary_cells": len(primary), "indexed_examples": len(targets), "symbol_map_entries": len(mapping_entries), "palette_proposal_examples": 6, "local_links_checked": links, "errors": errors}
    out = EVIDENCE / "independent-audit.json"
    out.write_text(json.dumps(result, indent=2) + "\n")
    print(json.dumps(result, indent=2))
    return 1 if errors else 0

if __name__ == "__main__":
    sys.exit(main())
