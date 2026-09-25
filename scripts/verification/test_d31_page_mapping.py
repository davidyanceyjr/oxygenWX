import copy
import unittest
from pathlib import Path

from d31_page_mapping import parse_mapping, parse_mapping_text, validate
from d31_source_audit import parse_audit


class D31PageMappingTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.root = Path(__file__).resolve().parents[2]
        cls.mapping, cls.prose = parse_mapping(cls.root / "docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md")
        cls.audit = parse_audit(cls.root / "docs/theme-system/design-pack/D31_SOURCE_AUDIT.md")

    def check(self, mutate=None, prose=None, audit=None):
        data = copy.deepcopy(self.mapping)
        if mutate:
            mutate(data)
        return validate(data, copy.deepcopy(audit if audit is not None else self.audit), self.prose if prose is None else prose)

    def test_repository_mapping_passes(self):
        self.assertEqual([], self.check())

    def test_missing_duplicate_and_extra_cells_fail(self):
        missing = self.check(lambda d: d["cells"].pop())
        self.assertTrue(any("expected exactly 5 cells" in error for error in missing))
        duplicate = self.check(lambda d: d["cells"].__setitem__(1, copy.deepcopy(d["cells"][0])))
        self.assertTrue(any("duplicate theme/page cell" in error for error in duplicate))
        extra = self.check(lambda d: d["cells"].append(copy.deepcopy(d["cells"][0])))
        self.assertTrue(any("expected exactly 5 cells" in error for error in extra))

    def test_scope_theme_page_id_and_order_fail(self):
        cases = [
            (lambda d: d["scope"].__setitem__("themes", list(reversed(d["scope"]["themes"]))), "scope.themes"),
            (lambda d: d["scope"].__setitem__("pages", ["hourly"]), "scope.pages"),
            (lambda d: d["scope"].__setitem__("cell_count", 4), "scope.cell_count"),
            (lambda d: d["scope"].__setitem__("coverage", "complete"), "scope.coverage"),
            (lambda d: d["cells"][0].__setitem__("theme", "unknown"), "cells[0].theme"),
            (lambda d: d["cells"][0].__setitem__("page", "hourly"), "cells[0].page"),
            (lambda d: d["cells"][0].__setitem__("id", "wrong-id"), "cells[0].id"),
            (lambda d: d["cells"].reverse(), "canonical order"),
        ]
        for mutate, expected in cases:
            with self.subTest(expected=expected):
                self.assertTrue(any(expected in error for error in self.check(mutate)))

    def test_bad_schema_and_status_fail(self):
        for field, value in (("schema_version", 2), ("status", "approved")):
            errors = self.check(lambda d, f=field, v=value: d.__setitem__(f, v))
            self.assertTrue(any(f"mapping.{field}" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0].__setitem__("review_status", "approved"))
        self.assertTrue(any("review_status" in error for error in errors))

    def test_missing_wrong_type_and_blank_nested_fields_fail(self):
        errors = self.check(lambda d: d["cells"][0]["observation"].__setitem__("palette", "  "))
        self.assertTrue(any("observation.palette" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0].__setitem__("state_constraints", []))
        self.assertTrue(any("state_constraints: expected object" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0]["proposed_treatment"].pop("surfaces"))
        self.assertTrue(any("proposed_treatment.surfaces: missing field" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0].__setitem__("limitations", [" "]))
        self.assertTrue(any("limitations: expected non-empty array" in error for error in errors))

    def test_invalid_evidence_class_and_source_references_fail(self):
        errors = self.check(lambda d: d["cells"][0]["source_refs"][0].__setitem__("evidence_class", "inferred"))
        self.assertTrue(any("evidence_class" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0]["source_refs"][0].__setitem__("source_id", "missing-source"))
        self.assertTrue(any("unknown source ID 'missing-source'" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0]["source_refs"][0].__setitem__("source_id", "terminal-backdrop"))
        self.assertTrue(any("belongs to theme 'Terminal'" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0]["source_refs"][0].__setitem__("source_id", "atmospheric-asset-sheet-absent"))
        self.assertTrue(any("cannot be evidence" in error for error in errors))

    def test_generic_locator_and_derivation_integrity_fail(self):
        generic = self.audit["inventory"][1]["locator"]
        errors = self.check(lambda d: d["cells"][0]["source_refs"][0].__setitem__("locator", generic))
        self.assertTrue(any("too generic" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0]["source_refs"][0].__setitem__("locator", " "))
        self.assertTrue(any("mapping-specific non-blank locator" in error for error in errors))
        errors = self.check(lambda d: d["cells"][0].__setitem__("derivation_basis", ["not-cited"]))
        self.assertTrue(any("not-cited" in error and "not cited" in error for error in errors))

    def test_audit_profile_integrity_and_completion_claims_fail(self):
        bad_audit = copy.deepcopy(self.audit)
        bad_audit["profiles"]["Minimal OLED"]["sources_used"][2] = "missing-source"
        errors = self.check(audit=bad_audit)
        self.assertTrue(any("source audit profile Minimal OLED" in error for error in errors))
        for claim, expected in (
            ("D31 is complete.", "D31 completion"),
            ("TP.1D is approved.", "TP.1D completion"),
            ("The packet is approved.", "packet approval"),
            ("TP.2 is eligible.", "TP.2 eligibility"),
        ):
            with self.subTest(claim=claim):
                errors = self.check(prose=claim)
                self.assertTrue(any(expected in error for error in errors))

    def test_marked_json_block_must_be_unique_and_well_formed(self):
        valid_text = (self.root / "docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md").read_text(encoding="utf-8")
        cases = (
            (valid_text.replace("D31_PAGE_ATMOSPHERES:END", "", 1), "exactly one ordered BEGIN/END"),
            (valid_text.replace("```json", "```json", 1).replace("D31_PAGE_ATMOSPHERES:END", "```json\n{}\n```\nD31_PAGE_ATMOSPHERES:END", 1), "exactly one JSON code block"),
            (valid_text.replace("\"schema_version\": 1", "\"schema_version\": invalid", 1), "Expecting value"),
        )
        for text, expected in cases:
            with self.subTest(expected=expected):
                with self.assertRaisesRegex(ValueError, expected):
                    parse_mapping_text(text)


if __name__ == "__main__":
    unittest.main()
