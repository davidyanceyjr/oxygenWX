#!/usr/bin/env python3
"""Validate the approved theme JSON catalog against its typed Kotlin authority."""
from __future__ import annotations

import json
import math
import re
import sys
from pathlib import Path
from typing import Any

KOTLIN_DEFAULT = Path("app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt")

THEME_FILES = {
    "atmospheric": "atmospheric.json",
    "glass": "glass.json",
    "minimal_oled": "minimal_oled.json",
    "instrument": "instrument.json",
    "terminal": "terminal.json",
}
MANIFEST = "theme_manifest.json"
THEME_NAMES = {
    "atmospheric": "Atmospheric",
    "glass": "Glass",
    "minimal_oled": "Minimal OLED",
    "instrument": "Instrument",
    "terminal": "Terminal",
}
MANIFEST_KEYS = ("schemaVersion", "package", "themes")
MANIFEST_THEME_KEYS = ("id", "name", "intent", "background", "surface", "typography", "weatherMarks", "accent")
THEME_KEYS = ("id", "colors", "surface", "spacingDp", "motion")
COLOR_KEYS = ("canvas", "atmosphereTop", "atmosphereBottom", "atmosphereGlow", "surface", "elevatedSurface", "content", "secondaryData", "outline", "conditionAccent", "precipitationAccent", "warning", "danger")
SURFACE_KEYS = ("opacity", "radiusDp", "borderDp")
SPACING_KEYS = ("gutter", "stack", "grid", "panel")
MOTION_KEYS = ("default", "supportsFull")
COLOR_PATTERN = re.compile(r"^#[0-9A-Fa-f]{6}$")


class DuplicateKeyError(ValueError):
    pass


def _object_pairs(pairs: list[tuple[str, Any]]) -> dict[str, Any]:
    result: dict[str, Any] = {}
    for key, value in pairs:
        if key in result:
            raise DuplicateKeyError(f"duplicate JSON object key {key!r}")
        result[key] = value
    return result


def _reject_constant(value: str) -> None:
    raise ValueError(f"non-finite JSON number {value}")


def load_catalog(root: Path) -> tuple[dict[str, Any], dict[str, dict[str, Any]], list[str]]:
    """Load inputs without validation; return objects and deterministic load errors."""
    errors: list[str] = []

    def load(name: str) -> Any:
        path = root / name
        try:
            return json.loads(path.read_text(encoding="utf-8"), object_pairs_hook=_object_pairs, parse_constant=_reject_constant)
        except FileNotFoundError:
            errors.append(f"{name} $: file is missing")
        except (OSError, UnicodeError, json.JSONDecodeError, DuplicateKeyError, ValueError) as exc:
            errors.append(f"{name} $: invalid JSON ({exc})")
        return None

    manifest = load(MANIFEST)
    themes = {theme_id: load(filename) for theme_id, filename in THEME_FILES.items()}
    return manifest, themes, errors


def _keys(value: Any, expected: tuple[str, ...], filename: str, path: str, errors: list[str]) -> bool:
    if not isinstance(value, dict):
        errors.append(f"{filename} {path}: expected object")
        return False
    actual = set(value)
    for key in expected:
        if key not in actual:
            errors.append(f"{filename} {path}.{key}: missing required key")
    for key in sorted(actual - set(expected)):
        errors.append(f"{filename} {path}.{key}: unknown key")
    return True


def _color(value: Any, filename: str, path: str, errors: list[str]) -> None:
    if not isinstance(value, str) or not COLOR_PATTERN.fullmatch(value):
        errors.append(f"{filename} {path}: expected six-digit #RRGGBB color")


def _nonnegative_int(value: Any, filename: str, path: str, errors: list[str]) -> None:
    if type(value) is not int or value < 0:
        errors.append(f"{filename} {path}: expected non-negative JSON integer")


def _validate_theme(filename: str, expected_id: str, data: Any, errors: list[str]) -> None:
    if not _keys(data, THEME_KEYS, filename, "$", errors):
        return
    if "id" in data and data["id"] != expected_id:
        errors.append(f"{filename} $.id: expected {expected_id!r}, got {data['id']!r}")
    if _keys(data.get("colors"), COLOR_KEYS, filename, "$.colors", errors):
        for key in COLOR_KEYS:
            if key in data["colors"]:
                _color(data["colors"][key], filename, f"$.colors.{key}", errors)
    if _keys(data.get("surface"), SURFACE_KEYS, filename, "$.surface", errors):
        surface = data["surface"]
        if "opacity" in surface:
            value = surface["opacity"]
            if type(value) not in (int, float) or not math.isfinite(value) or not 0 <= value <= 1:
                errors.append(f"{filename} $.surface.opacity: expected finite JSON number in [0, 1]")
        for key in ("radiusDp", "borderDp"):
            if key in surface:
                _nonnegative_int(surface[key], filename, f"$.surface.{key}", errors)
    if _keys(data.get("spacingDp"), SPACING_KEYS, filename, "$.spacingDp", errors):
        for key in SPACING_KEYS:
            if key in data["spacingDp"]:
                _nonnegative_int(data["spacingDp"][key], filename, f"$.spacingDp.{key}", errors)
    if _keys(data.get("motion"), MOTION_KEYS, filename, "$.motion", errors):
        motion = data["motion"]
        if "default" in motion and motion["default"] not in ("off", "subtle", "full"):
            errors.append(f"{filename} $.motion.default: expected one of off, subtle, full")
        if "supportsFull" in motion and type(motion["supportsFull"]) is not bool:
            errors.append(f"{filename} $.motion.supportsFull: expected JSON boolean")


def validate_catalog(root: Path) -> list[str]:
    manifest, themes, errors = load_catalog(root)
    if manifest is not None and _keys(manifest, MANIFEST_KEYS, MANIFEST, "$", errors):
        if type(manifest.get("schemaVersion")) is not int or manifest["schemaVersion"] != 1:
            errors.append(f"{MANIFEST} $.schemaVersion: expected integer 1")
        if manifest.get("package") != "oxygenWX-theme-engine":
            errors.append(f"{MANIFEST} $.package: expected 'oxygenWX-theme-engine'")
        records = manifest.get("themes")
        if not isinstance(records, list):
            errors.append(f"{MANIFEST} $.themes: expected array")
        else:
            seen: set[str] = set()
            for index, record in enumerate(records):
                path = f"$.themes[{index}]"
                if not _keys(record, MANIFEST_THEME_KEYS, MANIFEST, path, errors):
                    continue
                theme_id = record.get("id")
                if not isinstance(theme_id, str) or theme_id not in THEME_FILES:
                    errors.append(f"{MANIFEST} {path}.id: unknown theme identity {theme_id!r}")
                elif theme_id in seen:
                    errors.append(f"{MANIFEST} {path}.id: duplicate theme identity {theme_id!r}")
                else:
                    seen.add(theme_id)
                if isinstance(theme_id, str) and theme_id in THEME_NAMES and record.get("name") != THEME_NAMES[theme_id]:
                    errors.append(f"{MANIFEST} {path}.name: expected {THEME_NAMES[theme_id]!r}")
                for key in ("name", "intent", "background", "surface", "typography", "weatherMarks"):
                    value = record.get(key)
                    if not isinstance(value, str) or not value.strip():
                        errors.append(f"{MANIFEST} {path}.{key}: expected non-empty string")
                _color(record.get("accent"), MANIFEST, f"{path}.accent", errors)
            for theme_id in THEME_FILES:
                if theme_id not in seen:
                    errors.append(f"{MANIFEST} $.themes: missing theme identity {theme_id!r}")
    for theme_id, filename in THEME_FILES.items():
        data = themes[theme_id]
        if data is not None:
            _validate_theme(filename, theme_id, data, errors)
    return errors


class KotlinSourceError(ValueError):
    """An unsupported or ambiguous ThemeCatalog.kt source form."""


def _mask_comments(source: str) -> str:
    """Blank comments while preserving offsets and quoted string contents."""
    out = list(source)
    i = 0
    while i < len(source):
        if source.startswith('"""', i):
            end = source.find('"""', i + 3)
            if end < 0:
                raise KotlinSourceError(f"line {source.count(chr(10), 0, i) + 1}: unterminated string")
            i = end + 3
        elif source[i] == '"':
            i += 1
            while i < len(source):
                if source[i] == "\\":
                    i += 2
                elif source[i] == '"':
                    i += 1
                    break
                else:
                    i += 1
        elif source.startswith("//", i):
            end = source.find("\n", i)
            end = len(source) if end < 0 else end
            for j in range(i, end): out[j] = " "
            i = end
        elif source.startswith("/*", i):
            depth, end = 1, i + 2
            while end < len(source) and depth:
                if source.startswith("/*", end): depth += 1; end += 2
                elif source.startswith("*/", end): depth -= 1; end += 2
                else: end += 1
            if depth: raise KotlinSourceError(f"line {source.count(chr(10), 0, i) + 1}: unterminated comment")
            for j in range(i, end):
                if source[j] != "\n": out[j] = " "
            i = end
        else:
            i += 1
    return "".join(out)


def _matching(source: str, masked: str, opening: int) -> int:
    pairs = {"(": ")", "{": "}", "[": "]"}
    stack: list[str] = []
    i = opening
    while i < len(masked):
        # Ignore quoted content by observing original source.
        if source[i] == '"':
            if source.startswith('"""', i):
                end = source.find('"""', i + 3)
                if end < 0: break
                i = end + 3
            else:
                i += 1
                while i < len(source):
                    if source[i] == "\\": i += 2
                    elif source[i] == '"': i += 1; break
                    else: i += 1
            continue
        ch = masked[i]
        if ch in pairs: stack.append(pairs[ch])
        elif ch in ")]}":
            if not stack or stack.pop() != ch:
                raise KotlinSourceError(f"line {source.count(chr(10), 0, i) + 1}: unbalanced delimiter")
            if not stack: return i
        i += 1
    raise KotlinSourceError(f"line {source.count(chr(10), 0, opening) + 1}: unclosed expression")


def _split_args(source: str, masked: str, start: int, end: int) -> list[str]:
    pieces, piece_start, stack, i = [], start, [], start
    pairs = {"(": ")", "{": "}", "[": "]"}
    while i < end:
        if source[i] == '"':
            if source.startswith('"""', i): i = source.find('"""', i + 3) + 3
            else:
                i += 1
                while i < end:
                    if source[i] == "\\": i += 2
                    elif source[i] == '"': i += 1; break
                    else: i += 1
            continue
        ch = masked[i]
        if ch in pairs: stack.append(pairs[ch])
        elif ch in ")]}":
            if stack: stack.pop()
        elif ch == "," and not stack:
            pieces.append(source[piece_start:i].strip()); piece_start = i + 1
        i += 1
    tail = source[piece_start:end].strip()
    if tail: pieces.append(tail)
    return pieces


def _call(source: str, masked: str, expression: str, expected: str, context: str) -> list[str]:
    match = re.fullmatch(rf"\s*{re.escape(expected)}\s*\((.*)\)\s*", expression, re.S)
    if not match:
        raise KotlinSourceError(f"{context}: unsupported {expected} expression {expression!r}")
    open_at = expression.find("(")
    close_at = _matching(expression, _mask_comments(expression), open_at)
    if expression[close_at + 1:].strip(): raise KotlinSourceError(f"{context}: trailing source after {expected}")
    return _split_args(expression, _mask_comments(expression), open_at + 1, close_at)


def _one(pattern: str, source: str, context: str) -> re.Match[str]:
    matches = list(re.finditer(pattern, source, re.S))
    if len(matches) != 1:
        raise KotlinSourceError(f"{context}: expected exactly one source anchor, found {len(matches)}")
    return matches[0]


def _literal_number(expr: str, context: str, suffix: str = "") -> float:
    value = expr.strip()
    if suffix:
        if not value.endswith(suffix): raise KotlinSourceError(f"{context}: expected literal with {suffix} suffix, got {value!r}")
        value = value[:-len(suffix)]
    if not re.fullmatch(r"(?:0|[1-9][0-9]*)(?:\.[0-9]+)?", value):
        raise KotlinSourceError(f"{context}: unsupported numeric literal {expr!r}")
    return float(value)


def _theme_blocks(source: str) -> dict[str, str]:
    masked = _mask_comments(source)
    names = {"atmospheric": "atmospheric", "glass": "glass", "minimalOled": "minimal_oled", "instrument": "instrument", "terminal": "terminal"}
    blocks = {}
    for variable, theme_id in names.items():
        matches = list(re.finditer(rf"\bval\s+{variable}\s*=\s*ThemeDefinition\s*\(", masked))
        if len(matches) != 1: raise KotlinSourceError(f"ThemeCatalog.kt {theme_id}: expected exactly one ThemeDefinition assignment, found {len(matches)}")
        opening = masked.find("(", matches[0].start())
        closing = _matching(source, masked, opening)
        blocks[theme_id] = source[opening + 1:closing]
    # Unknown top-level definitions are ambiguous: don't silently omit a new theme.
    all_defs = list(re.finditer(r"\bval\s+([A-Za-z][A-Za-z0-9]*)\s*=\s*ThemeDefinition\s*\(", masked))
    if len(all_defs) != len(names): raise KotlinSourceError(f"ThemeCatalog.kt: expected {len(names)} theme definitions, found {len(all_defs)}")
    return blocks


def _named_overrides(expression: str, context: str) -> dict[str, str]:
    match = re.fullmatch(r"\s*([A-Za-z][A-Za-z0-9]*)\s*=\s*(.+?)\s*", expression, re.S)
    if not match: raise KotlinSourceError(f"{context}: unsupported override {expression!r}")
    return {match.group(1): match.group(2)}


def _geometry_helpers(models_source: str) -> dict[str, dict[str, float]]:
    masked = _mask_comments(models_source)
    result: dict[str, dict[str, float]] = {}
    fields = ("pageGutter", "pageVerticalInset", "pageStackGap", "gridGap", "controlGap", "tabHorizontalInset", "tabVerticalInset", "tabGap", "panelInset", "compactPanelInset", "heroPanelInset", "controlTargetMinimum", "panelBorderWidth", "panelCornerRadius")
    for helper in ("spaciousGeometry", "compactGeometry"):
        pattern = rf"\bfun\s+{helper}\s*\(\s*radius\s*:\s*Dp\s*\)\s*=\s*ThemeGeometry\s*\("
        matches = list(re.finditer(pattern, masked))
        if len(matches) != 1: raise KotlinSourceError(f"ThemeModels.kt {helper}: expected exactly one ThemeGeometry helper, found {len(matches)}")
        constructor = masked.find("ThemeGeometry", matches[0].start())
        opening = masked.find("(", constructor)
        closing = _matching(models_source, masked, opening)
        args = _split_args(models_source, masked, opening + 1, closing)
        values: dict[str, float] = {}
        radius_seen = False
        for arg in args:
            if arg.strip() == "radius: Dp":
                continue
            named = re.fullmatch(r"\s*([A-Za-z][A-Za-z0-9]*)\s*=\s*(.+?)\s*", arg, re.S)
            if not named: raise KotlinSourceError(f"ThemeModels.kt {helper}: unsupported constructor argument {arg!r}")
            key, raw = named.groups()
            if key not in fields: raise KotlinSourceError(f"ThemeModels.kt {helper}.{key}: unknown geometry field")
            if key in values or (key == "panelCornerRadius" and radius_seen): raise KotlinSourceError(f"ThemeModels.kt {helper}.{key}: duplicate geometry field")
            if key == "panelCornerRadius" and raw == "radius":
                radius_seen = True
                continue
            if key == "panelCornerRadius": raise KotlinSourceError(f"ThemeModels.kt {helper}.panelCornerRadius: expected radius parameter")
            values[key] = _literal_number(raw, f"ThemeModels.kt {helper}.{key}", ".dp")
        expected = set(fields) - {"panelCornerRadius"}
        if set(values) != expected or not radius_seen: raise KotlinSourceError(f"ThemeModels.kt {helper}: geometry fields differ; missing={sorted(expected - set(values))}, radius_parameter={radius_seen}, extra={sorted(set(values) - expected)}")
        result[helper] = values
    return result


def _geometry_values(theme_id: str, expression: str, helper_defaults: dict[str, dict[str, float]]) -> dict[str, Any]:
    context = f"ThemeCatalog.kt {theme_id}.geometry"
    pattern = r"\s*(spaciousGeometry|compactGeometry)\(\s*((?:0|[1-9][0-9]*)(?:\.[0-9]+)?)\.dp\s*\)\s*(?:\.copy\s*\((.*)\))?\s*"
    match = re.fullmatch(pattern, expression, re.S)
    if not match: raise KotlinSourceError(f"{context}: unsupported helper/override expression {expression!r}")
    helper, radius, copy_body = match.groups()
    values: dict[str, Any] = dict(helper_defaults[helper], panelCornerRadius=float(radius))
    if copy_body is not None:
        override_keys: set[str] = set()
        copy_expr = "copy(" + copy_body + ")"
        for arg in _call(copy_expr, _mask_comments(copy_expr), copy_expr, "copy", context):
            item = _named_overrides(arg, context)
            key, raw = next(iter(item.items()))
            if key in override_keys: raise KotlinSourceError(f"{context}.{key}: duplicate geometry override")
            override_keys.add(key)
            if key not in values: raise KotlinSourceError(f"{context}: unknown geometry override {key!r}")
            if key in values and key == "panelCornerRadius" and values[key] != float(radius): raise KotlinSourceError(f"{context}: ambiguous geometry override {key}")
            values[key] = _literal_number(raw, f"{context}.{key}", ".dp")
    return values


def _parse_theme(theme_id: str, block: str, helper_defaults: dict[str, dict[str, float]]) -> dict[str, Any]:
    context = f"ThemeCatalog.kt {theme_id}"
    masked = _mask_comments(block)
    args = _split_args(block, masked, 0, len(block))
    if len(args) != 6: raise KotlinSourceError(f"{context}: expected six ThemeDefinition arguments, found {len(args)}")
    identity = re.fullmatch(r"WeatherThemeId\.([A-Z_]+)", args[0].strip())
    name = re.fullmatch(r'"([^"\\]*)"', args[1].strip())
    if not identity or not name: raise KotlinSourceError(f"{context}: unsupported identity or display name expression")
    palette_args = _call(args[2], _mask_comments(args[2]), args[2], "palette", f"{context}.palette")
    if len(palette_args) != 15: raise KotlinSourceError(f"{context}.palette: expected 15 arguments, found {len(palette_args)}")
    hexes = []
    for index, raw in enumerate(palette_args):
        literal = re.fullmatch(r'"([0-9A-Fa-f]{6})"', raw.strip())
        if not literal: raise KotlinSourceError(f"{context}.palette argument {index + 1}: unsupported color {raw!r}")
        hexes.append("#" + literal.group(1).upper())
    # args[3] is runtime typography and is intentionally outside JSON parity.
    geom = _geometry_values(theme_id, args[4], helper_defaults)
    visual = _call(args[5], _mask_comments(args[5]), args[5], "ThemeVisualLanguage", f"{context}.visualLanguage")
    if len(visual) != 8: raise KotlinSourceError(f"{context}.visualLanguage: expected eight arguments, found {len(visual)}")
    enums = []
    enum_types = ("BackdropStyle", "SurfaceStyle", "HeroStyle", "WeatherMarkStyle", "MotionStyle")
    for raw, enum_type in zip(visual[:5], enum_types):
        found = re.fullmatch(rf"{enum_type}\.([A-Z_]+)", raw.strip())
        if not found: raise KotlinSourceError(f"{context}.visualLanguage.{enum_type}: unsupported expression {raw!r}")
        enums.append(found.group(1))
    boolean = visual[5].strip()
    if boolean not in ("true", "false"): raise KotlinSourceError(f"{context}.visualLanguage.supportsFullMotion: unsupported boolean {boolean!r}")
    opacity = _literal_number(visual[6], f"{context}.visualLanguage.panelOpacity", "f")
    if visual[7].strip() != "1f": raise KotlinSourceError(f"{context}.visualLanguage.outlineOpacity: only approved literal 1f is supported")
    return {"identity": identity.group(1), "name": name.group(1), "palette": hexes, "geometry": geom,
            "visual": {"backdrop": enums[0], "surface": enums[1], "weatherMarks": enums[3], "motion": enums[4],
                       "supportsFull": boolean == "true", "opacity": opacity}}


def validate_parity(root: Path, kotlin_path: Path, models_path: Path | None = None) -> list[str]:
    """Check every JSON-owned catalog mapping against static typed Kotlin source."""
    errors = validate_catalog(root)
    if errors: return errors
    try:
        source = kotlin_path.read_text(encoding="utf-8")
        model_source_path = models_path or kotlin_path.with_name("ThemeModels.kt")
        helper_defaults = _geometry_helpers(model_source_path.read_text(encoding="utf-8"))
        blocks = _theme_blocks(source)
        parsed = {theme_id: _parse_theme(theme_id, block, helper_defaults) for theme_id, block in blocks.items()}
    except (OSError, UnicodeError, KotlinSourceError) as exc:
        return [f"{locals().get('model_source_path', kotlin_path)}: {exc}"]
    manifest, themes, _ = load_catalog(root)
    manifest_by_id = {record["id"]: record for record in manifest["themes"]}
    # palette(...) args map in this fixed, inventory-approved order.
    role_order = ("canvas", "atmosphereTop", "atmosphereBottom", "atmosphereGlow", "surface", "elevatedSurface", "content", "secondaryData", "outline", "conditionAccent", "precipitationAccent", "warning", "danger", "action", "actionContent")
    geometry_map = {"gutter": "pageGutter", "stack": "pageStackGap", "grid": "gridGap", "panel": "panelInset"}
    enum_maps = {"atmosphere": "ATMOSPHERE", "glass_gradient": "GLASS_GRADIENT", "pure_black": "PURE_BLACK", "instrument_grid": "INSTRUMENT_GRID", "terminal_grid": "TERMINAL_GRID",
                 "soft_translucent": "SOFT_TRANSLUCENT", "glass": "GLASS", "minimal": "MINIMAL", "instrument_panel": "INSTRUMENT_PANEL", "terminal_flat": "TERMINAL_FLAT",
                 "illustrative_line": "ILLUSTRATIVE_LINE", "soft_line": "SOFT_LINE", "minimal_line": "MINIMAL_LINE", "instrument_line": "INSTRUMENT_LINE", "terminal_glyph": "TERMINAL_GLYPH"}
    for theme_id in THEME_FILES:
        filename, data = THEME_FILES[theme_id], themes[theme_id]
        actual, manifest_row = parsed[theme_id], manifest_by_id[theme_id]
        def compare(path: str, expected: Any, got: Any) -> None:
            if expected != got: errors.append(f"{filename} {path}: expected {expected!r}, Kotlin ThemeCatalog.kt {theme_id}: actual {got!r}")
        compare("$.id", theme_id.upper(), actual["identity"])
        compare("theme_manifest.json $.themes[].name", manifest_row["name"], actual["name"])
        for index, role in enumerate(role_order):
            expected = manifest_row["accent"] if role == "action" else data["colors"]["canvas"] if role == "actionContent" else data["colors"][role]
            compare(f"$.colors.{role}", expected.upper(), actual["palette"][index])
        # The Kotlin palette helper intentionally mirrors content into primaryData.
        compare("$.colors.content (intentional palette.primaryData mirror)", data["colors"]["content"].upper(), actual["palette"][6])
        compare("$.surface.opacity", float(data["surface"]["opacity"]), actual["visual"]["opacity"])
        compare("$.surface.radiusDp", float(data["surface"]["radiusDp"]), actual["geometry"]["panelCornerRadius"])
        compare("$.surface.borderDp", float(data["surface"]["borderDp"]), actual["geometry"]["panelBorderWidth"])
        for json_key, kotlin_key in geometry_map.items(): compare(f"$.spacingDp.{json_key}", float(data["spacingDp"][json_key]), actual["geometry"][kotlin_key])
        compare("$.motion.default", data["motion"]["default"].upper(), actual["visual"]["motion"])
        compare("$.motion.supportsFull", data["motion"]["supportsFull"], actual["visual"]["supportsFull"])
        for json_key, actual_key in (("background", "backdrop"), ("surface", "surface"), ("weatherMarks", "weatherMarks")):
            mapped = enum_maps.get(manifest_row[json_key])
            if mapped is None: errors.append(f"theme_manifest.json $.themes[].{json_key}: no closed Kotlin enum mapping for {manifest_row[json_key]!r}")
            else: compare(f"theme_manifest.json $.themes[].{json_key}", mapped, actual["visual"][actual_key])
        compare("theme_manifest.json $.themes[].accent", manifest_row["accent"].upper(), actual["palette"][13])
    return errors


def main() -> int:
    repo = Path(__file__).resolve().parents[2]
    root = repo / "docs/theme-system/tokens/catalog"
    kotlin_path = repo / KOTLIN_DEFAULT
    args = sys.argv[1:]
    if args and not args[0].startswith("--"):
        root = Path(args.pop(0)).resolve()
    if args:
        if len(args) != 2 or args[0] != "--kotlin":
            print("usage: theme_catalog_conformance.py [catalog-dir] [--kotlin ThemeCatalog.kt]", file=sys.stderr)
            return 2
        kotlin_path = Path(args[1]).resolve()
    errors = validate_parity(root, kotlin_path)
    if errors:
        print("Theme catalog conformance: FAIL")
        for error in errors:
            print(error)
        return 1
    print("Theme catalog conformance: PASS")
    print(f"{MANIFEST}: schemaVersion=1; package=oxygenWX-theme-engine")
    for theme_id in THEME_FILES:
        print(f"{theme_id}: {THEME_FILES[theme_id]}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
