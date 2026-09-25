"""Structural validator for the partial D29 weather-art design document."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[2]
SPEC_PATH = ROOT / "docs/theme-system/design-pack/WEATHER_ART.md"
THEMES = {"atmospheric", "glass", "minimal_oled", "instrument", "terminal"}
CONDITIONS = {"CLEAR", "PARTLY_CLOUDY", "CLOUDY"}
PENDING = {"RAIN", "STORM", "SNOW"}
REQUIRED = {
    "theme", "condition", "source", "locator", "source_status", "treatment",
    "footprint", "background_contrast", "fallback",
}
STATUSES = {"direct", "adapted", "proposed", "gap", "omitted"}
BEGIN = "<!-- WEATHER_ART_MATRIX:BEGIN -->"
END = "<!-- WEATHER_ART_MATRIX:END -->"


def _matrix(text: str) -> dict:
    if BEGIN not in text or END not in text:
        raise ValueError("partial matrix markers are missing")
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
    if re.search(r"(?im)^\s*Coverage:\s*complete\b|\bD29 (?:design )?(?:is )?complete\b|\bcomplete D29 matrix\b", text):
        errors.append("partial document makes a full-coverage or D29-complete claim")
    try:
        data = _matrix(text)
    except ValueError as exc:
        return [str(exc)]

    if data.get("coverage") != "partial":
        errors.append("coverage must be explicitly partial")
    if set(data.get("covered_conditions", [])) != CONDITIONS:
        errors.append("covered_conditions must be exactly CLEAR, PARTLY_CLOUDY, CLOUDY")
    if set(data.get("pending_conditions", [])) != PENDING:
        errors.append("pending_conditions must be exactly RAIN, STORM, SNOW")
    if data.get("status") != "proposed; owner review pending":
        errors.append("matrix status must keep proposal and owner review pending")

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
            errors.append(f"{prefix} has out-of-scope/invalid condition ID: {condition}")
        key = (theme, condition)
        if key in seen:
            errors.append(f"{prefix} duplicates matrix key: {key}")
        seen.add(key)
        if cell.get("source_status") not in STATUSES:
            errors.append(f"{prefix} has invalid source_status")
        for field in ("locator", "treatment", "footprint", "background_contrast", "fallback"):
            value = cell.get(field)
            if not isinstance(value, str) or not value.strip():
                errors.append(f"{prefix} requires non-empty {field}")
        locator = cell.get("locator", "")
        if isinstance(locator, str) and re.search(r"\b(board|sheet)\s+only\b|\bgeneric\s+board\b", locator, re.I):
            errors.append(f"{prefix} locator is too generic")
        source = cell.get("source")
        if not isinstance(source, str) or not source.strip():
            errors.append(f"{prefix} requires a source path")
        else:
            source_path = source.split("#", 1)[0]
            if not (root / source_path).is_file():
                errors.append(f"{prefix} source path does not resolve: {source_path}")
    if len(cells) != 15:
        errors.append(f"partial matrix must contain exactly 15 cells, found {len(cells)}")
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
    print("weather art spec: PASS — 15 proposed cells; 3 conditions × 5 themes; 3 conditions pending")
    return 0


if __name__ == "__main__":
    sys.exit(main())
