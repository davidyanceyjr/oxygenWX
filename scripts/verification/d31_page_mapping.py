"""Structural and source-integrity checks for the proposed D31 page mapping."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any

from d31_source_audit import AUDIT, THEMES, parse_audit

ROOT = Path(__file__).resolve().parents[2]
MAPPING = ROOT / "docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md"
BEGIN = "D31_PAGE_ATMOSPHERES:BEGIN"
END = "D31_PAGE_ATMOSPHERES:END"
EXPECTED_STATUS = "owner-approved; documentary proposal"
SOURCE_THEME = {
    "atmospheric": "Atmospheric",
    "glass": "Glass",
    "minimal_oled": "Minimal OLED",
    "instrument": "Instrument",
    "terminal": "Terminal",
}
MAPPING_THEMES = list(SOURCE_THEME)
MAPPING_PAGES = ["now", "hourly", "daily", "details"]
FIELDS = ("palette", "scene_backdrop", "surfaces", "weather_art_relationship")
STATE_FIELDS = ("effects_off", "high_contrast", "responsive_accessibility")
CELL_KEYS = {
    "id", "theme", "page", "review_status", "source_refs", "observation",
    "interpretation", "proposed_treatment", "state_constraints", "source_gaps",
    "derivation_basis", "rationale", "limitations",
}


def parse_mapping_text(source: str) -> tuple[dict[str, Any], str]:
    starts = [m.start() for m in re.finditer(re.escape(BEGIN), source)]
    ends = [m.start() for m in re.finditer(re.escape(END), source)]
    if len(starts) != 1 or len(ends) != 1 or ends[0] <= starts[0]:
        raise ValueError("mapping must contain exactly one ordered BEGIN/END block")
    body = source[starts[0] + len(BEGIN):ends[0]]
    blocks = re.findall(r"```json\s*\n(.*?)\n```", body, re.S)
    if len(blocks) != 1:
        raise ValueError("mapping markers must contain exactly one JSON code block")
    prose = source[:starts[0]] + source[ends[0] + len(END):]
    return json.loads(blocks[0]), prose


def parse_mapping(path: Path = MAPPING) -> tuple[dict[str, Any], str]:
    return parse_mapping_text(path.read_text(encoding="utf-8"))


def _nonblank(value: Any) -> bool:
    return isinstance(value, str) and bool(value.strip())


def _array_of_nonblank(value: Any) -> bool:
    return isinstance(value, list) and bool(value) and all(_nonblank(item) for item in value)


def _completion_claims(prose: str) -> list[str]:
    checks = (
        (r"\bD31\s+(?:is\s+)?(?:complete|completed|approved)\b", "affirmative D31 completion/approval claim"),
        (r"\bTP\.1D\s+(?:is\s+)?(?:complete|completed|approved|closed)\b", "affirmative TP.1D completion/approval claim"),
        (r"\bTP\.1\s+(?:is\s+)?(?:complete|completed|approved|closed)\b", "affirmative TP.1 completion/approval claim"),
        (r"\bpacket\s+(?:is\s+)?approved\b", "affirmative packet approval claim"),
        (r"\bTP\.2\s+(?:is\s+)?eligible\b", "affirmative TP.2 eligibility claim"),
    )
    return [message for pattern, message in checks if re.search(pattern, prose, re.I)]


def validate(
    data: dict[str, Any],
    audit: dict[str, Any],
    prose: str = "",
) -> list[str]:
    errors: list[str] = []
    if not isinstance(data, dict):
        return ["mapping: root must be an object"]
    if data.get("schema_version") != 1:
        errors.append("mapping.schema_version: expected 1")
    scope = data.get("scope")
    if not isinstance(scope, dict):
        errors.append("mapping.scope: expected object")
        scope = {}
    if scope.get("themes") != MAPPING_THEMES:
        errors.append("mapping.scope.themes: expected canonical ordered five-theme list")
    if scope.get("pages") != MAPPING_PAGES:
        errors.append("mapping.scope.pages: expected exactly ['now', 'hourly', 'daily', 'details']")
    if scope.get("cell_count") != 20:
        errors.append("mapping.scope.cell_count: expected 20")
    if scope.get("coverage") != "complete":
        errors.append("mapping.scope.coverage: expected 'complete'")
    if data.get("status") != EXPECTED_STATUS:
        errors.append(f"mapping.status: expected {EXPECTED_STATUS!r}")

    inventory_list = audit.get("inventory", []) if isinstance(audit, dict) else []
    inventory = {
        item.get("id"): item for item in inventory_list
        if isinstance(item, dict) and isinstance(item.get("id"), str)
    }
    profiles = audit.get("profiles", {}) if isinstance(audit, dict) else {}
    for theme in THEMES:
        profile = profiles.get(theme, {}) if isinstance(profiles, dict) else {}
        used = profile.get("sources_used", []) if isinstance(profile, dict) else []
        if isinstance(used, list):
            for source_id in used:
                if isinstance(source_id, str) and source_id not in inventory:
                    errors.append(f"source audit profile {theme}: unknown sources_used ID {source_id!r}")

    cells = data.get("cells")
    if not isinstance(cells, list):
        errors.append("mapping.cells: expected array")
        cells = []
    if len(cells) != 20:
        errors.append(f"mapping.cells: expected exactly 20 cells, found {len(cells)}")

    seen_pairs: set[str] = set()
    seen_ids: set[str] = set()
    expected_pairs = [(theme, page) for page in MAPPING_PAGES for theme in MAPPING_THEMES]
    actual_pairs: list[tuple[Any, Any]] = []
    for index, cell in enumerate(cells):
        prefix = f"cells[{index}]"
        if not isinstance(cell, dict):
            errors.append(f"{prefix}: expected object")
            continue
        for key in sorted(CELL_KEYS - cell.keys()):
            errors.append(f"{prefix}.{key}: missing required field")
        for key in sorted(cell.keys() - CELL_KEYS):
            errors.append(f"{prefix}.{key}: unexpected field")
        theme, page = cell.get("theme"), cell.get("page")
        pair = (theme, page)
        actual_pairs.append(pair)
        if not isinstance(theme, str) or theme not in SOURCE_THEME:
            errors.append(f"{prefix}.theme: unknown theme {theme!r}")
        if page not in MAPPING_PAGES:
            errors.append(f"{prefix}.page: expected one of {MAPPING_PAGES!r}, found {page!r}")
        pair_key = json.dumps(pair, sort_keys=True, default=repr)
        if pair_key in seen_pairs:
            errors.append(f"{prefix}: duplicate theme/page cell {pair!r}")
        seen_pairs.add(pair_key)
        expected_id = f"{theme}-{page}" if isinstance(theme, str) and page in MAPPING_PAGES else ""
        if cell.get("id") != expected_id:
            errors.append(f"{prefix}.id: expected {expected_id!r}")
        cell_id = cell.get("id")
        if isinstance(cell_id, str):
            if cell_id in seen_ids:
                errors.append(f"{prefix}.id: duplicate cell ID {cell_id!r}")
            seen_ids.add(cell_id)
        if cell.get("review_status") != "proposed":
            errors.append(f"{prefix}.review_status: expected 'proposed'")

        for group_name in ("observation", "proposed_treatment"):
            group = cell.get(group_name)
            if not isinstance(group, dict):
                errors.append(f"{prefix}.{group_name}: expected object")
                continue
            for field in FIELDS:
                if not _nonblank(group.get(field)):
                    errors.append(f"{prefix}.{group_name}.{field}: expected non-blank string")
            for field in group.keys() - set(FIELDS):
                errors.append(f"{prefix}.{group_name}.{field}: unexpected field")
            for field in set(FIELDS) - group.keys():
                errors.append(f"{prefix}.{group_name}.{field}: missing field")
        if not _nonblank(cell.get("interpretation")):
            errors.append(f"{prefix}.interpretation: expected non-blank string")

        states = cell.get("state_constraints")
        if not isinstance(states, dict):
            errors.append(f"{prefix}.state_constraints: expected object")
        else:
            for field in STATE_FIELDS:
                if not _nonblank(states.get(field)):
                    errors.append(f"{prefix}.state_constraints.{field}: expected non-blank string")
            for field in states.keys() - set(STATE_FIELDS):
                errors.append(f"{prefix}.state_constraints.{field}: unexpected field")
            for field in set(STATE_FIELDS) - states.keys():
                errors.append(f"{prefix}.state_constraints.{field}: missing field")

        for field in ("source_gaps", "limitations"):
            if not _array_of_nonblank(cell.get(field)):
                errors.append(f"{prefix}.{field}: expected non-empty array of non-blank strings")
        if not _nonblank(cell.get("rationale")):
            errors.append(f"{prefix}.rationale: expected non-blank string")

        refs = cell.get("source_refs")
        cited_ids: set[str] = set()
        if not isinstance(refs, list) or not refs:
            errors.append(f"{prefix}.source_refs: expected non-empty array")
            refs = []
        for ref_index, ref in enumerate(refs):
            ref_prefix = f"{prefix}.source_refs[{ref_index}]"
            if not isinstance(ref, dict):
                errors.append(f"{ref_prefix}: expected object")
                continue
            required = {"source_id", "locator", "supports", "evidence_class"}
            for field in sorted(required - ref.keys()):
                errors.append(f"{ref_prefix}.{field}: missing field")
            for field in sorted(ref.keys() - required):
                errors.append(f"{ref_prefix}.{field}: unexpected field")
            source_id = ref.get("source_id")
            if not isinstance(source_id, str) or not source_id:
                errors.append(f"{ref_prefix}.source_id: expected non-blank string")
                continue
            cited_ids.add(source_id)
            source = inventory.get(source_id)
            if source is None:
                errors.append(f"{ref_prefix}.source_id: unknown source ID {source_id!r}")
            else:
                source_theme = source.get("theme")
                expected_source_theme = SOURCE_THEME.get(theme) if isinstance(theme, str) else None
                if source_theme not in (expected_source_theme, "Cross-theme"):
                    errors.append(f"{ref_prefix}.source_id: {source_id!r} belongs to theme {source_theme!r}")
                if source.get("source_class") == "absent_asset_sheet":
                    errors.append(f"{ref_prefix}.source_id: absent-sheet record {source_id!r} cannot be evidence")
                locator = ref.get("locator")
                generic = source.get("locator")
                if not _nonblank(locator):
                    errors.append(f"{ref_prefix}.locator: expected mapping-specific non-blank locator")
                elif len(locator.strip()) < 24 or locator.strip() == generic:
                    errors.append(f"{ref_prefix}.locator: too generic; use a narrower mapping-specific region")
                elif page == "details" and source.get("source_class") in ("phone_crop", "asset_sheet", "overview_board", "backdrop"):
                    if source.get("source_class") == "backdrop":
                        if ref.get("evidence_class") != "direct_region":
                            errors.append(f"{ref_prefix}.evidence_class: Details backdrop field evidence must be direct_region")
                    else:
                        if ref.get("evidence_class") != "same_theme_derivation":
                            errors.append(f"{ref_prefix}.evidence_class: Details phone/sheet/overview cue must be same_theme_derivation")
                        if not re.search(r"\b(field|surface|rule|light|texture|separator|outline|grid|monospace|atmosphere)\b", locator, re.I):
                            errors.append(f"{ref_prefix}.locator: Details derivation must identify a concrete visible cue")
                        if re.search(r"\bdedicated\s+Details\s+(?:screen|page)\b|\bDetails\s+(?:screen|page)\s+(?:shows|contains|depicts)\b", locator, re.I):
                            errors.append(f"{ref_prefix}.locator: must not assert a dedicated Details screen")
                elif source.get("source_class") in ("phone_crop", "asset_sheet", "overview_board"):
                    if page == "now":
                        page_pattern, page_label = r"\b(now|hero|condition|current)\b", "Now"
                    elif page == "hourly":
                        page_pattern, page_label = r"\b(hourly|forecast|compact forecast row)\b", "Hourly"
                    elif page == "daily":
                        page_pattern, page_label = r"\b(daily|5[- ]day forecast|daily forecast)\b", "Daily"
                    else:
                        page_pattern, page_label = r"\b(daily|5[- ]day forecast|daily forecast)\b", "Daily"
                    if not re.search(page_pattern, locator, re.I):
                        errors.append(f"{ref_prefix}.locator: phone/sheet locator must identify its {page_label}-relevant region")
            if not _nonblank(ref.get("supports")):
                errors.append(f"{ref_prefix}.supports: expected non-blank claim")
            if ref.get("evidence_class") not in ("direct_region", "same_theme_derivation"):
                errors.append(f"{ref_prefix}.evidence_class: expected direct_region or same_theme_derivation")
        basis = cell.get("derivation_basis")
        if not _array_of_nonblank(basis):
            errors.append(f"{prefix}.derivation_basis: expected non-empty array of source IDs")
        else:
            for source_id in basis:
                if source_id not in cited_ids:
                    errors.append(f"{prefix}.derivation_basis: {source_id!r} is not cited in source_refs")

    if actual_pairs != expected_pairs:
        errors.append("mapping.cells: expected one cell per canonical theme/page pair in canonical page/theme order")
    errors.extend(_completion_claims(prose))
    return errors


def main() -> int:
    try:
        data, prose = parse_mapping()
        audit = parse_audit(ROOT / AUDIT)
        errors = validate(data, audit, prose)
    except Exception as exc:
        print(f"D31 page mapping invalid: {exc}", file=sys.stderr)
        return 1
    if errors:
        print("D31 page mapping invalid:\n" + "\n".join(f"- {error}" for error in errors), file=sys.stderr)
        return 1
    print("D31 page mapping valid: twenty ordered owner-approved documentary proposals; source relationships and complete structural coverage are valid.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
