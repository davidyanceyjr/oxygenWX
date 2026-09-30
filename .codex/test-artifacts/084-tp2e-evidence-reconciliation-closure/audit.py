"""Read-only reconciliation of TP.2E retained manifests and review matrices."""

from collections import Counter
from hashlib import sha256
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[3]
EVIDENCE = ROOT / ".codex/test-artifacts"
SETS = {
    "subtle": (
        EVIDENCE / "075-tp2e-per-family-showcase-pages/installed/manifest.txt",
        EVIDENCE / "075-tp2e-per-family-showcase-pages/installed/png",
        30,
    ),
    "off1": (
        EVIDENCE / "076-tp2e-effects-off-per-family-pages/installed/export/manifest.txt",
        EVIDENCE / "076-tp2e-effects-off-per-family-pages/installed/export",
        15,
    ),
    "off2": (
        EVIDENCE / "077-tp2e-effects-off-remaining-family-pages/manifest.txt",
        EVIDENCE / "077-tp2e-effects-off-remaining-family-pages/captures/oxygen-weather-tp2e-077-partial2",
        15,
    ),
}
MATRICES = (
    EVIDENCE / "078-tp2e-cross-effects-comparison-review/pair-review-matrix.md",
    EVIDENCE / "078-tp2e-cross-effects-comparison-review-partial2/pair-review-matrix.md",
)
INVENTORIES = (
    EVIDENCE / "078-tp2e-cross-effects-comparison-review/source-inventory.md",
    EVIDENCE / "078-tp2e-cross-effects-comparison-review-partial2/source-inventory.md",
)
EXPECTED_FAMILIES = {
    "page-identity", "current-conditions", "forecast-windows",
    "source-inspection", "weather-mark", "backdrop",
}
EXPECTED_THEMES = {"atmospheric", "glass", "minimal_oled", "instrument", "terminal"}
ENTRY = re.compile(
    r"^(\S+\.png): page=([A-Z_]+); theme=([A-Z_]+); 360x640px; sha256=([a-f0-9]{64})$"
)
REFERENCE = re.compile(r"`([^\`]+\.png)`<br>SHA-256 `([a-f0-9]{64})`")


def digest(path):
    return sha256(path.read_bytes()).hexdigest()


def load_set(name):
    manifest, directory, expected_count = SETS[name]
    text = manifest.read_text()
    rows = {}
    for line in text.splitlines():
        match = ENTRY.fullmatch(line)
        if not match:
            continue
        filename, family, theme, recorded_hash = match.groups()
        key = (family.lower().replace("_", "-"), theme.lower())
        assert key not in rows, (name, key)
        file = directory / filename
        data = file.read_bytes()
        assert sha256(data).hexdigest() == recorded_hash, file
        assert data[:16] == b"\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR", file
        assert int.from_bytes(data[16:20], "big") == 360, file
        assert int.from_bytes(data[20:24], "big") == 640, file
        rows[key] = (file, recorded_hash)
    assert len(rows) == expected_count, (name, len(rows))
    assert {theme for _, theme in rows} == EXPECTED_THEMES, name
    expected_families = EXPECTED_FAMILIES if name == "subtle" else (
        {"page-identity", "current-conditions", "forecast-windows"}
        if name == "off1" else
        {"source-inspection", "weather-mark", "backdrop"}
    )
    assert {family for family, _ in rows} == expected_families, name
    inventories = INVENTORIES if name == "subtle" else (
        (INVENTORIES[0],) if name == "off1" else (INVENTORIES[1],)
    )
    for inventory in inventories:
        assert digest(manifest) in inventory.read_text() or (
            name == "off1" and
            digest(EVIDENCE / "076-tp2e-effects-off-per-family-pages/installed/manifest.txt")
            in inventory.read_text()
        ), (name, inventory)
    print(f"{name}: {len(rows)} captures; manifest SHA-256 {digest(manifest)}")
    return rows


def main():
    sets = {name: load_set(name) for name in SETS}
    off = sets["off1"] | sets["off2"]
    assert not (sets["off1"].keys() & sets["off2"].keys())
    assert sets["subtle"].keys() == off.keys()
    dispositions = Counter()
    seen = set()
    limitations = set()
    for index, matrix in enumerate(MATRICES, 1):
        rows = 0
        for line in matrix.read_text().splitlines():
            if not line.startswith("| ") or "<br>SHA-256" not in line:
                continue
            columns = [part.strip() for part in line.strip().split("|")[1:-1]]
            assert len(columns) == 8, (matrix, line)
            family = columns[0].lower().replace(" and ", "-").replace(" ", "-")
            theme = columns[1].lower().replace(" ", "_")
            key = (family, theme)
            assert key not in seen, key
            seen.add(key)
            for cell, source in ((columns[2], sets["subtle"]), (columns[3], off)):
                refs = REFERENCE.findall(cell)
                assert len(refs) == 1, (key, cell)
                path, hash_value = refs[0]
                canonical_path, canonical_hash = source[key]
                assert Path(path).name == canonical_path.name, (key, path)
                if "/" in path:
                    assert ROOT / path == canonical_path, (key, path)
                assert hash_value == canonical_hash, (key, path)
            disposition = columns[7]
            assert disposition in {"PASS", "KNOWN-LIMITATION", "FINDING"}
            dispositions[disposition] += 1
            if disposition == "KNOWN-LIMITATION":
                assert "Rain" in columns[4] + columns[6], key
                limitations.add(key)
            rows += 1
        assert rows == 15, (matrix, rows)
        print(f"matrix {index}: {rows} unique pairs")
    assert seen == sets["subtle"].keys()
    expected_limits = {
        (family, theme)
        for family in ("forecast-windows", "weather-mark")
        for theme in ("atmospheric", "minimal_oled", "terminal")
    }
    assert limitations == expected_limits, limitations
    assert dispositions == {"PASS": 24, "KNOWN-LIMITATION": 6}, dispositions
    print(f"combined: {len(seen)} unique pairs / 60 captures; {dict(dispositions)}; 0 FINDING")
    print("Rain limitations:", sorted(limitations))


if __name__ == "__main__":
    main()
