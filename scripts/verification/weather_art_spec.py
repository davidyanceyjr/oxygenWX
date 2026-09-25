"""Structural validator for the proposed D29 weather-art matrix."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[2]
SPEC_PATH = ROOT / "docs/theme-system/design-pack/WEATHER_ART.md"
THEMES = {"atmospheric", "glass", "minimal_oled", "instrument", "terminal"}
CONDITIONS = {"CLEAR", "PARTLY_CLOUDY", "CLOUDY", "RAIN", "STORM", "SNOW"}
REQUIRED = {
    "theme", "condition", "source", "locator", "source_status", "treatment",
    "footprint", "background_contrast", "fallback",
}
STATUSES = {"direct", "adapted", "proposed", "gap", "omitted"}
BEGIN = "<!-- WEATHER_ART_MATRIX:BEGIN -->"
END = "<!-- WEATHER_ART_MATRIX:END -->"
GENERIC_LOCATOR = re.compile(r"\b(board|sheet)\s+only\b|\bgeneric\s+board\b|\bthe image\b", re.I)


def _matrix(text: str) -> dict:
    if BEGIN not in text or END not in text:
        raise ValueError("weather-art matrix markers are missing")
    body = text.split(BEGIN, 1)[1].split(END, 1)[0]
    match = re.search(r"```json\s*(.*?)\s*```", body, re.DOTALL)
    if not match:
        raise ValueError("matrix JSON block is missing")
    try:
        return json.loads(match.group(1))
    except json.JSONDecodeError as exc:
        raise ValueError(f"matrix JSON is invalid: {exc}") from exc


def validate(text: str, root: Path = ROOT) -> list[str]:
    errors: list[str] = []
    try:
        data = _matrix(text)
    except ValueError as exc:
        return [str(exc)]

    if data.get("coverage") != "complete":
        errors.append("coverage must be explicitly complete")
    if set(data.get("covered_conditions", [])) != CONDITIONS:
        errors.append("covered_conditions must be exactly the six WeatherMarkCondition IDs")
    if data.get("pending_conditions") != []:
        errors.append("pending_conditions must be empty for a complete proposed matrix")
    if data.get("themes") != ["atmospheric", "glass", "minimal_oled", "instrument", "terminal"]:
        errors.append("themes metadata must list the five design-pack theme IDs in canonical order")
    status = data.get("status")
    if status not in {"proposed; owner review pending", "owner-approved proposed matrix"}:
        errors.append("matrix status must be proposed pending review or owner-approved as proposed")
    if data.get("cell_count") != 30:
        errors.append("cell_count metadata must equal 30")
    if re.search(r"(?im)^\s*Coverage:\s*partial\b|\b15\s*/\s*30\b|\bpart[- ]2\s+(?:is\s+)?pending\b", text):
        errors.append("complete matrix document retains stale partial-coverage wording")
    owner_decision = data.get("owner_decision")
    if status == "owner-approved proposed matrix":
        outside_matrix = text.split(BEGIN, 1)[0] + text.split(END, 1)[-1]
        if not isinstance(owner_decision, dict):
            errors.append("owner-approved status requires a structured owner_decision record")
        else:
            if owner_decision.get("disposition") != "approved as presented":
                errors.append("owner_decision disposition must be approved as presented")
            if owner_decision.get("scope") != "D29 30-cell weather-mark matrix only":
                errors.append("owner_decision scope must be limited to the D29 30-cell matrix")
            digest = owner_decision.get("reviewed_artifact_sha256", "")
            if not isinstance(digest, str) or not re.fullmatch(r"[0-9a-f]{64}", digest):
                errors.append("owner_decision requires a 64-character reviewed artifact SHA-256")
            elif digest not in outside_matrix:
                errors.append("owner_decision hash must appear in the written owner decision record")
        if not re.search(r"(?im)^## Owner decision\b", text):
            errors.append("owner-approved status requires an Owner decision section")
        if not re.search(r"approved as\s+presented", outside_matrix, re.I):
            errors.append("owner-approved status requires an explicit as-presented disposition in the written record")
        if "D29 design matrix only" not in outside_matrix:
            errors.append("owner decision prose must limit approval to the D29 design matrix")
    elif owner_decision is not None:
        errors.append("owner_decision metadata requires owner-approved matrix status")
    if re.search(r"(?im)^\s*(?:the\s+)?TP\.1D(?:\s+packet)?\s+(?:is\s+)?approved\b|^\s*(?:the\s+)?TP\.1\s+approved\b|^\s*(?:the\s+)?TP\.2\s+(?:is\s+)?eligible\b", text):
        errors.append("weather-art matrix makes an unsupported broader approval or completion claim")

    cells = data.get("cells")
    if not isinstance(cells, list):
        return errors + ["cells must be a list"]
    expected = {(theme, condition) for theme in THEMES for condition in CONDITIONS}
    seen: set[tuple[str, str]] = set()
    for index, cell in enumerate(cells):
        prefix = f"cell {index + 1}"
        if not isinstance(cell, dict):
            errors.append(f"{prefix} must be an object")
            continue
        missing = REQUIRED - cell.keys()
        if missing:
            errors.append(f"{prefix} missing fields: {', '.join(sorted(missing))}")
        theme, condition = cell.get("theme"), cell.get("condition")
        if theme not in THEMES:
            errors.append(f"{prefix} has invalid theme ID: {theme}")
        if condition not in CONDITIONS:
            errors.append(f"{prefix} has invalid condition ID: {condition}")
        key = (theme, condition)
        if key in seen:
            errors.append(f"{prefix} duplicates matrix key: {key}")
        seen.add(key)
        status = cell.get("source_status")
        if status not in STATUSES:
            errors.append(f"{prefix} has invalid source_status")
        for field in ("source", "locator", "treatment", "footprint", "background_contrast", "fallback"):
            value = cell.get(field)
            if not isinstance(value, str) or not value.strip():
                errors.append(f"{prefix} requires non-empty {field}")
        locator = cell.get("locator", "")
        if isinstance(locator, str) and GENERIC_LOCATOR.search(locator):
            errors.append(f"{prefix} locator is too generic")
        source = cell.get("source")
        if isinstance(source, str) and source.strip():
            source_path = source.split("#", 1)[0]
            if not (root / source_path).is_file():
                errors.append(f"{prefix} source path does not resolve: {source_path}")

        treatment = cell.get("treatment", "")
        source_text = cell.get("source", "")
        locator_text = cell.get("locator", "")
        basis = f"{source_text} {locator_text} {treatment}".lower()
        if status in {"gap", "omitted"}:
            if "no direct" not in basis and "not shown" not in basis and "absent" not in basis:
                errors.append(f"{prefix} gap/omission must explicitly record that direct condition art is absent")
            if not re.search(r"\b(no mark|omit|omitted|omission)\b", treatment, re.I):
                errors.append(f"{prefix} gap/omission needs an explicit no-mark treatment")
        elif status == "proposed":
            if "same-theme" not in basis and "same theme" not in basis:
                errors.append(f"{prefix} proposal must cite its same-theme visual basis")
            if not ("no direct" in basis or "not shown" in basis or "not a direct" in basis):
                errors.append(f"{prefix} proposal must distinguish its basis from direct condition art")
        elif status in {"direct", "adapted"}:
            direct_terms = {
                "CLEAR": ("sunny", "clear"),
                "PARTLY_CLOUDY": ("partly cloudy", "partly_cloudy"),
                "CLOUDY": ("cloudy",),
                "RAIN": ("rain",),
                "STORM": ("storm",),
                "SNOW": ("snow",),
            }.get(condition, ())
            if not any(term in source_text.lower() or term in locator_text.lower() for term in direct_terms):
                errors.append(f"{prefix} direct/adapted source must identify condition-specific source art")

    if len(cells) != 30:
        errors.append(f"complete matrix must contain exactly 30 cells, found {len(cells)}")
    if seen != expected:
        missing = sorted(expected - seen)
        extra = sorted(seen - expected)
        if missing:
            errors.append(f"matrix pairs missing: {missing}")
        if extra:
            errors.append(f"matrix has unexpected pairs: {extra}")
    return errors


def main() -> int:
    text = SPEC_PATH.read_text(encoding="utf-8")
    errors = validate(text)
    if errors:
        print("weather art spec: FAIL")
        for error in errors:
            print(f"- {error}")
        return 1
    print("weather art spec: PASS — 30 cells; 6 conditions × 5 themes; D29 matrix owner-approved")
    return 0


if __name__ == "__main__":
    sys.exit(main())
