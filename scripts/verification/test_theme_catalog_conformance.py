from __future__ import annotations

import json
import subprocess
import sys
import shutil
import tempfile
import unittest
from pathlib import Path

from scripts.verification.theme_catalog_conformance import validate_catalog, validate_parity

REPO = Path(__file__).resolve().parents[2]
SOURCE = REPO / "docs/theme-system/tokens/catalog"
KOTLIN_SOURCE = REPO / "app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt"
MODELS_SOURCE = REPO / "app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeModels.kt"


class ThemeCatalogConformanceTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name) / "catalog"
        shutil.copytree(SOURCE, self.root)
        self.kotlin = Path(self.temp.name) / "ThemeCatalog.kt"
        shutil.copy2(KOTLIN_SOURCE, self.kotlin)
        self.models = Path(self.temp.name) / "ThemeModels.kt"
        shutil.copy2(MODELS_SOURCE, self.models)

    def tearDown(self) -> None:
        self.temp.cleanup()

    def read(self, name: str):
        return json.loads((self.root / name).read_text(encoding="utf-8"))

    def write(self, name: str, value) -> None:
        (self.root / name).write_text(json.dumps(value), encoding="utf-8")

    def errors(self) -> str:
        return "\n".join(validate_catalog(self.root))

    def parity_errors(self) -> str:
        return "\n".join(validate_parity(self.root, self.kotlin, self.models))

    def test_clean_repository_catalog(self) -> None:
        before = {p.name: p.read_bytes() for p in SOURCE.glob("*.json")}
        self.assertEqual([], validate_catalog(SOURCE))
        self.assertEqual(before, {p.name: p.read_bytes() for p in SOURCE.glob("*.json")})

    def test_missing_and_unknown_required_keys(self) -> None:
        data = self.read("glass.json")
        del data["colors"]["canvas"]
        data["surprise"] = 1
        self.write("glass.json", data)
        errors = self.errors()
        self.assertIn("glass.json $.colors.canvas", errors)
        self.assertIn("glass.json $.surprise", errors)

    def test_unsupported_version_and_duplicate_manifest_identity(self) -> None:
        data = self.read("theme_manifest.json")
        data["schemaVersion"] = True
        data["themes"][1]["id"] = data["themes"][0]["id"]
        self.write("theme_manifest.json", data)
        errors = self.errors()
        self.assertIn("theme_manifest.json $.schemaVersion", errors)
        self.assertIn("duplicate theme identity", errors)
        self.assertIn("missing theme identity 'glass'", errors)

    def test_unknown_manifest_identity_and_missing_theme_file(self) -> None:
        data = self.read("theme_manifest.json")
        data["themes"][0]["id"] = "unknown"
        self.write("theme_manifest.json", data)
        (self.root / "terminal.json").unlink()
        errors = self.errors()
        self.assertIn("theme_manifest.json $.themes[0].id", errors)
        self.assertIn("terminal.json $: file is missing", errors)

    def test_bad_color_and_invalid_numeric_types_or_ranges(self) -> None:
        data = self.read("atmospheric.json")
        data["colors"]["canvas"] = "blue"
        data["surface"]["opacity"] = True
        data["surface"]["radiusDp"] = -1
        data["spacingDp"]["gutter"] = "16"
        self.write("atmospheric.json", data)
        errors = self.errors()
        for path in ("$.colors.canvas", "$.surface.opacity", "$.surface.radiusDp", "$.spacingDp.gutter"):
            self.assertIn(f"atmospheric.json {path}", errors)

    def test_invalid_nested_object_types_and_manifest_theme_file_mismatch(self) -> None:
        data = self.read("glass.json")
        data["motion"] = []
        data["id"] = "instrument"
        self.write("glass.json", data)
        errors = self.errors()
        self.assertIn("glass.json $.motion: expected object", errors)
        self.assertIn("glass.json $.id: expected 'glass'", errors)

    def test_duplicate_json_object_key_rejected(self) -> None:
        data = (self.root / "glass.json").read_text(encoding="utf-8")
        (self.root / "glass.json").write_text(data.replace('"id": "glass"', '"id": "glass", "id": "glass"', 1), encoding="utf-8")
        errors = self.errors()
        self.assertIn("glass.json $: invalid JSON (duplicate JSON object key 'id')", errors)

    def test_nan_and_motion_values_rejected(self) -> None:
        data = (self.root / "terminal.json").read_text(encoding="utf-8").replace('"opacity": 1.0', '"opacity": NaN')
        (self.root / "terminal.json").write_text(data, encoding="utf-8")
        manifest = self.read("theme_manifest.json")
        manifest["themes"][0]["accent"] = "#ABC"
        self.write("theme_manifest.json", manifest)
        errors = self.errors()
        self.assertIn("terminal.json $: invalid JSON", errors)
        self.assertIn("theme_manifest.json $.themes[0].accent", errors)

    def test_clean_repository_catalog_and_typed_source_have_full_parity(self) -> None:
        self.assertEqual([], validate_catalog(SOURCE))
        self.assertEqual([], validate_parity(SOURCE, KOTLIN_SOURCE, MODELS_SOURCE))

    def test_direct_color_intentional_roles_and_manifest_accent_mismatch(self) -> None:
        data = self.read("glass.json")
        data["colors"]["surface"] = "#010203"
        self.write("glass.json", data)
        errors = self.parity_errors()
        self.assertIn("glass.json $.colors.surface", errors)
        self.assertIn("Kotlin ThemeCatalog.kt glass", errors)
        data["colors"]["surface"] = "#31527A"
        data["colors"]["content"] = "#010203"
        self.write("glass.json", data)
        errors = self.parity_errors()
        self.assertIn("intentional palette.primaryData mirror", errors)
        manifest = self.read("theme_manifest.json")
        manifest["themes"][1]["accent"] = "#010203"
        self.write("theme_manifest.json", manifest)
        self.assertIn("$.colors.action", self.parity_errors())

    def test_effective_surface_and_each_geometry_family_mismatch(self) -> None:
        data = self.read("minimal_oled.json")
        data["surface"].update(opacity=0.5, radiusDp=17, borderDp=2)
        data["spacingDp"].update(gutter=19, stack=19, grid=13, panel=9)
        self.write("minimal_oled.json", data)
        errors = self.parity_errors()
        for path in ("$.surface.opacity", "$.surface.radiusDp", "$.surface.borderDp", "$.spacingDp.gutter", "$.spacingDp.stack", "$.spacingDp.grid", "$.spacingDp.panel"):
            self.assertIn(f"minimal_oled.json {path}", errors)

    def test_motion_and_each_manifest_style_mapping_mismatch(self) -> None:
        data = self.read("terminal.json")
        data["motion"].update(default="subtle", supportsFull=True)
        self.write("terminal.json", data)
        manifest = self.read("theme_manifest.json")
        manifest["themes"][4].update(background="atmosphere", surface="glass", weatherMarks="soft_line")
        self.write("theme_manifest.json", manifest)
        errors = self.parity_errors()
        for fragment in ("$.motion.default", "$.motion.supportsFull", "$.themes[].background", "$.themes[].surface", "$.themes[].weatherMarks"):
            self.assertIn(fragment, errors)

    def test_missing_duplicate_and_unsupported_theme_source_shapes_fail_closed(self) -> None:
        original = self.kotlin.read_text(encoding="utf-8")
        self.kotlin.write_text(original.replace("val glass = ThemeDefinition(", "val glassWas = ThemeDefinition(", 1), encoding="utf-8")
        errors = self.parity_errors()
        self.assertIn("ThemeCatalog.kt glass", errors)
        duplicate = original[original.index("    val glass = ThemeDefinition("):original.index("    val minimalOled = ThemeDefinition(")]
        self.kotlin.write_text(original + "\n" + duplicate, encoding="utf-8")
        self.assertIn("ThemeCatalog.kt glass: expected exactly one ThemeDefinition assignment, found 2", self.parity_errors())
        self.kotlin.write_text(original.replace("spaciousGeometry(26.dp)", "unknownGeometry(26.dp)", 1), encoding="utf-8")
        self.assertIn("ThemeCatalog.kt glass.geometry: unsupported helper/override", self.parity_errors())

    def test_unsupported_palette_constructor_and_override_fail_with_context(self) -> None:
        original = self.kotlin.read_text(encoding="utf-8")
        self.kotlin.write_text(original.replace('palette("0B1220"', 'palette(colorFromToken("0B1220")', 1), encoding="utf-8")
        errors = self.parity_errors()
        self.assertIn("ThemeCatalog.kt glass.palette argument 1", errors)
        self.kotlin.write_text(original.replace("gridGap = 10.dp", "gridGap = dimensionToken", 1), encoding="utf-8")
        self.assertIn("ThemeCatalog.kt glass.geometry.gridGap: expected literal with .dp suffix", self.parity_errors())

    def test_stable_developer_catalog_command_success_and_failure(self) -> None:
        fixture = Path(self.temp.name) / "repo"
        (fixture / "scripts/verification").mkdir(parents=True)
        (fixture / "docs/theme-system/tokens").mkdir(parents=True)
        (fixture / "app/src/main/java/com/oxygen/weather/ui/themeengine").mkdir(parents=True)
        shutil.copy2(REPO / "scripts/dev.py", fixture / "scripts/dev.py")
        shutil.copy2(REPO / "scripts/verification/theme_catalog_conformance.py", fixture / "scripts/verification/theme_catalog_conformance.py")
        shutil.copytree(SOURCE, fixture / "docs/theme-system/tokens/catalog")
        shutil.copy2(MODELS_SOURCE, fixture / "app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeModels.kt")
        shutil.copy2(KOTLIN_SOURCE, fixture / "app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt")
        command = [sys.executable, str(fixture / "scripts/dev.py"), "catalog"]
        passed = subprocess.run(command, cwd=fixture, text=True, capture_output=True, check=False)
        self.assertEqual(0, passed.returncode, passed.stdout + passed.stderr)
        self.assertIn("Theme catalog conformance: PASS", passed.stdout)
        failed_data = json.loads((fixture / "docs/theme-system/tokens/catalog/atmospheric.json").read_text(encoding="utf-8"))
        failed_data["colors"]["canvas"] = "#010203"
        (fixture / "docs/theme-system/tokens/catalog/atmospheric.json").write_text(json.dumps(failed_data), encoding="utf-8")
        failed = subprocess.run(command, cwd=fixture, text=True, capture_output=True, check=False)
        self.assertEqual(1, failed.returncode)
        self.assertIn("atmospheric.json $.colors.canvas", failed.stdout)


if __name__ == "__main__":
    unittest.main()
