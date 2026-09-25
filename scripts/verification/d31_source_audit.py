"""Structural validation for the human-reviewed D31 source audit."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any

THEMES = ["Atmospheric", "Glass", "Minimal OLED", "Instrument", "Terminal"]
CLASSES = ["overview_board", "phone_crop", "backdrop", "asset_sheet", "absent_asset_sheet"]
AUDIT = Path("docs/theme-system/design-pack/D31_SOURCE_AUDIT.md")
MANIFEST = Path("docs/theme-system/ASSET_MANIFEST.json")


def parse_audit(path: Path = AUDIT) -> dict[str, Any]:
    source = path.read_text(encoding="utf-8")
    matches = re.findall(r"```json\s*\n(.*?)\n```", source, re.S)
    if len(matches) != 1:
        raise ValueError("audit must contain exactly one structured JSON inventory block")
    return json.loads(matches[0])


def validate(data: dict[str, Any], root: Path = Path("."), manifest_path: Path = MANIFEST) -> list[str]:
    errors: list[str] = []
    manifest = json.loads((root / manifest_path).read_text(encoding="utf-8"))
    hashes = {item["path"]: item["sha256"] for item in manifest["files"]}
    records = data.get("inventory", [])
    ids: set[str] = set()
    found: set[tuple[str, str]] = set()
    for i, rec in enumerate(records):
        label = rec.get("id", f"inventory[{i}]")
        required = ("id", "theme", "source_class", "locator", "coverage", "limitation")
        if rec.get("source_class") != "absent_asset_sheet":
            required += ("path", "sha256", "dimensions_px")
        for key in required:
            if not rec.get(key):
                errors.append(f"{label}: missing {key}")
        if label in ids:
            errors.append(f"{label}: duplicate source ID")
        ids.add(label)
        theme, cls = rec.get("theme"), rec.get("source_class")
        if theme not in THEMES and not (cls == "overview_board" and theme == "Cross-theme"):
            errors.append(f"{label}: unknown theme {theme!r}")
        if cls not in CLASSES:
            errors.append(f"{label}: unknown source class {cls!r}")
        if theme in THEMES and cls in CLASSES:
            found.add((theme, cls))
        dims = rec.get("dimensions_px")
        if cls != "absent_asset_sheet" and (not isinstance(dims, list) or len(dims) != 2 or not all(isinstance(x, int) and x > 0 for x in dims)):
            errors.append(f"{label}: invalid dimensions_px")
        if cls == "absent_asset_sheet":
            if rec.get("path") is not None or rec.get("sha256") is not None or dims is not None:
                errors.append(f"{label}: absent sheet must not claim path/hash/dimensions")
        else:
            asset = rec.get("path")
            if not asset or not (root / asset).is_file():
                errors.append(f"{label}: nonexistent path {asset!r}")
            expected = hashes.get(asset)
            if expected is None:
                errors.append(f"{label}: path is not in asset manifest")
            elif rec.get("sha256") != expected:
                errors.append(f"{label}: hash mismatch for {asset}")
    for theme in THEMES:
        for cls in ("phone_crop", "backdrop"):
            if (theme, cls) not in found:
                errors.append(f"coverage: missing {theme} {cls}")
    for cls in ("overview_board",):
        if not any(r.get("source_class") == cls for r in records):
            errors.append(f"coverage: missing {cls}")
    for theme in THEMES:
        expected = "asset_sheet" if theme in ("Glass", "Instrument") else "absent_asset_sheet"
        if (theme, expected) not in found:
            errors.append(f"coverage: missing {theme} {expected} record")
    profiles = data.get("profiles", {})
    inventory_by_id = {r.get("id"): r for r in records if isinstance(r, dict) and isinstance(r.get("id"), str)}
    for theme in THEMES:
        profile = profiles.get(theme, {})
        for field in ("sources_used", "observation", "interpretation", "unavailable_evidence"):
            if not profile.get(field):
                errors.append(f"profile {theme}: missing {field}")
        sources_used = profile.get("sources_used")
        if not isinstance(sources_used, list) or not sources_used:
            errors.append(f"profile {theme}: sources_used must be a non-empty array")
            continue
        seen: set[str] = set()
        for source_id in sources_used:
            if not isinstance(source_id, str):
                errors.append(f"profile {theme}: sources_used entries must be strings")
                continue
            if source_id in seen:
                errors.append(f"profile {theme}: duplicate sources_used ID {source_id!r}")
            seen.add(source_id)
            source = inventory_by_id.get(source_id)
            if source is None:
                errors.append(f"profile {theme}: unknown sources_used ID {source_id!r}")
            elif source.get("theme") not in (theme, "Cross-theme"):
                errors.append(
                    f"profile {theme}: sources_used ID {source_id!r} belongs to {source.get('theme')!r}"
                )
    return errors


def main() -> int:
    try:
        errors = validate(parse_audit())
    except Exception as exc:  # report malformed source as a failed check
        print(f"D31 audit invalid: {exc}", file=sys.stderr)
        return 1
    if errors:
        print("D31 audit invalid:\n" + "\n".join(f"- {e}" for e in errors), file=sys.stderr)
        return 1
    print("D31 source audit inventory valid: 16 records; five complete theme profiles; manifest hashes match.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
