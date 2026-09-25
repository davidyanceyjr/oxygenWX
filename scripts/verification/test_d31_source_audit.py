import json
import unittest
from pathlib import Path

from d31_source_audit import THEMES, parse_audit, validate


class D31SourceAuditTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.root = Path(__file__).resolve().parents[2]
        cls.audit = parse_audit(cls.root / "docs/theme-system/design-pack/D31_SOURCE_AUDIT.md")
        cls.manifest = cls.root / "docs/theme-system/ASSET_MANIFEST.json"

    def check(self, mutate):
        data = json.loads(json.dumps(self.audit))
        mutate(data)
        return validate(data, self.root, Path("docs/theme-system/ASSET_MANIFEST.json"))

    def test_audit_passes(self):
        self.assertEqual([], validate(self.audit, self.root, Path("docs/theme-system/ASSET_MANIFEST.json")))

    def test_missing_theme_or_source_class_fails(self):
        errors = self.check(lambda d: d["inventory"].pop(0))
        self.assertTrue(any("coverage: missing" in e for e in errors))

    def test_duplicate_id_fails(self):
        def mutate(d): d["inventory"][1]["id"] = d["inventory"][0]["id"]
        self.assertTrue(any("duplicate source ID" in e for e in self.check(mutate)))

    def test_nonexistent_path_fails(self):
        def mutate(d): d["inventory"][0]["path"] = "missing.png"
        self.assertTrue(any("nonexistent path" in e for e in self.check(mutate)))

    def test_hash_mismatch_fails(self):
        def mutate(d): d["inventory"][0]["sha256"] = "0" * 64
        self.assertTrue(any("hash mismatch" in e for e in self.check(mutate)))

    def test_missing_locator_and_dimensions_fail(self):
        def mutate(d):
            d["inventory"][0]["locator"] = ""
            d["inventory"][0]["dimensions_px"] = None
        errors = self.check(mutate)
        self.assertTrue(any("missing locator" in e for e in errors))
        self.assertTrue(any("invalid dimensions_px" in e for e in errors))

    def test_unrecorded_absent_sheet_fails(self):
        def mutate(d): d["inventory"] = [r for r in d["inventory"] if r["theme"] != "Terminal" or r["source_class"] != "absent_asset_sheet"]
        self.assertTrue(any("Terminal absent_asset_sheet" in e for e in self.check(mutate)))

    def test_profile_evidence_fields_required(self):
        def mutate(d): d["profiles"][THEMES[0]]["interpretation"] = ""
        self.assertTrue(any("profile Atmospheric: missing interpretation" in e for e in self.check(mutate)))


if __name__ == "__main__":
    unittest.main()
