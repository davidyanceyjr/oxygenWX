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

    def test_twenty_cell_contract_and_prior_cells_are_stable(self):
        self.assertEqual(["now", "hourly", "daily", "details"], self.mapping["scope"]["pages"])
        self.assertEqual(20, self.mapping["scope"]["cell_count"])
        self.assertEqual(20, len(self.mapping["cells"]))
        self.assertEqual(
            [(theme, page) for page in ("now", "hourly", "daily", "details") for theme in
             ("atmospheric", "glass", "minimal_oled", "instrument", "terminal")],
            [(cell["theme"], cell["page"]) for cell in self.mapping["cells"]],
        )
        baseline = self.root / ".codex/test-artifacts/052-d31-daily-atmosphere-mapping/pre-edit-cells.json"
        if baseline.exists():
            import json
            self.assertEqual(json.loads(baseline.read_text(encoding="utf-8")), self.mapping["cells"][:10])

    def test_details_cells_and_prior_fifteen_are_stable(self):
        self.assertEqual("complete", self.mapping["scope"]["coverage"])
        baseline = self.root / ".codex/test-artifacts/053-d31-details-atmosphere-mapping/pre-edit-cells.json"
        import json
        import hashlib
        prior = json.loads(baseline.read_text(encoding="utf-8"))
        self.assertEqual(prior, self.mapping["cells"][:15])
        digest = hashlib.sha256(json.dumps(prior, ensure_ascii=False, sort_keys=True, separators=(",", ":")).encode()).hexdigest()
        self.assertEqual("eabb49818e217d7301fa0a3775f57041e394ec38165bd1ad275d475418ede5b7", digest)
        self.assertEqual([f"{theme}-details" for theme in ("atmospheric", "glass", "minimal_oled", "instrument", "terminal")], [c["id"] for c in self.mapping["cells"][15:]])

    def test_details_locator_and_evidence_rules(self):
        cases = [
            (lambda d: d["cells"][15]["source_refs"][0].__setitem__("evidence_class", "direct_region"), "same_theme_derivation"),
            (lambda d: d["cells"][15]["source_refs"][0].__setitem__("locator", "Phone crop is a dedicated Details screen with all groups."), "concrete visible cue"),
            (lambda d: d["cells"][15]["source_refs"][0].__setitem__("locator", "Phone crop, dedicated Details screen shows all metrics."), "must not assert a dedicated Details screen"),
            (lambda d: d["cells"][15]["source_refs"][1].__setitem__("evidence_class", "same_theme_derivation"), "backdrop field evidence must be direct_region"),
            (lambda d: d["cells"][19]["source_refs"][0].__setitem__("source_id", "terminal-asset-sheet-absent"), "cannot be evidence"),
            (lambda d: d["cells"][17]["source_refs"][0].__setitem__("locator", "Phone crop around the current panel content."), "concrete visible cue"),
        ]
        for mutate, expected in cases:
            with self.subTest(expected=expected):
                self.assertTrue(any(expected in error for error in self.check(mutate)))

    def test_details_same_theme_cue_derivation_and_backdrop_are_accepted(self):
        self.assertEqual([], self.check())

    def test_missing_duplicate_and_extra_cells_fail(self):
        missing = self.check(lambda d: d["cells"].pop())
        self.assertTrue(any("expected exactly 20 cells" in error for error in missing))
        duplicate = self.check(lambda d: d["cells"].__setitem__(1, copy.deepcopy(d["cells"][0])))
        self.assertTrue(any("duplicate theme/page cell" in error for error in duplicate))
        extra = self.check(lambda d: d["cells"].append(copy.deepcopy(d["cells"][0])))
        self.assertTrue(any("expected exactly 20 cells" in error for error in extra))

    def test_daily_matrix_order_scope_and_cell_failures(self):
        cases = [
            (lambda d: d["cells"].pop(), "expected exactly 20 cells"),
            (lambda d: d["cells"].__setitem__(10, copy.deepcopy(d["cells"][9])), "duplicate theme/page cell ('terminal', 'hourly')"),
            (lambda d: d["cells"][14].__setitem__("theme", "atmospheric"), "expected 'atmospheric-daily'"),
            (lambda d: d["cells"][10].__setitem__("id", "glass-hourly"), "expected 'atmospheric-daily'"),
            (lambda d: d["cells"][10].__setitem__("page", "hourly"), "canonical theme/page pair"),
            (lambda d: d["cells"].__setitem__(slice(10, 15), list(reversed(d["cells"][10:15]))), "canonical theme/page pair"),
            (lambda d: d["scope"].__setitem__("pages", ["now", "daily", "hourly"]), "scope.pages"),
            (lambda d: d["scope"].__setitem__("cell_count", 14), "scope.cell_count"),
            (lambda d: d["scope"].__setitem__("coverage", "partial"), "scope.coverage"),
        ]
        for mutate, expected in cases:
            with self.subTest(expected=expected):
                self.assertTrue(any(expected in error for error in self.check(mutate)))

    def test_daily_nested_fields_evidence_sources_derivation_and_locators_fail(self):
        cases = [
            (lambda d: d["cells"][10]["observation"].__setitem__("surfaces", " "), "cells[10].observation.surfaces"),
            (lambda d: d["cells"][11]["state_constraints"].__setitem__("effects_off", " "), "cells[11].state_constraints.effects_off"),
            (lambda d: d["cells"][12]["source_refs"][0].__setitem__("evidence_class", "guess"), "cells[12].source_refs[0].evidence_class"),
            (lambda d: d["cells"][13]["source_refs"][0].__setitem__("source_id", "unknown-source"), "unknown source ID 'unknown-source'"),
            (lambda d: d["cells"][13]["source_refs"][0].__setitem__("source_id", "glass-phone"), "belongs to theme 'Glass'"),
            (lambda d: d["cells"][10]["source_refs"][0].__setitem__("source_id", "atmospheric-asset-sheet-absent"), "cannot be evidence"),
            (lambda d: d["cells"][13].__setitem__("derivation_basis", ["glass-backdrop"]), "glass-backdrop"),
            (lambda d: d["cells"][10]["source_refs"][0].__setitem__("locator", " "), "mapping-specific non-blank locator"),
            (lambda d: d["cells"][10]["source_refs"][0].__setitem__("locator", self.audit["inventory"][1]["locator"]), "too generic"),
            (lambda d: d["cells"][10]["source_refs"][0].__setitem__("locator", "Native phone crop: Hourly strip under the Now hero."), "Daily-relevant region"),
            (lambda d: d["cells"][11]["source_refs"][0].__setitem__("locator", "08 SCREEN EXAMPLE current-condition hero at top of mockup."), "Daily-relevant region"),
            (lambda d: d["cells"][14]["source_refs"][1].__setitem__("locator", "Overview board Terminal phone: Hourly forecast columns."), "Daily-relevant region"),
        ]
        for mutate, expected in cases:
            with self.subTest(expected=expected):
                self.assertTrue(any(expected in error for error in self.check(mutate)))

    def test_daily_completion_and_approval_claims_fail(self):
        for claim, expected in (
            ("D31 is complete.", "D31 completion"),
            ("TP.1D is closed.", "TP.1D completion"),
            ("TP.1 is approved.", "TP.1 completion"),
            ("The packet is approved.", "packet approval"),
            ("TP.2 is eligible.", "TP.2 eligibility"),
        ):
            with self.subTest(claim=claim):
                self.assertTrue(any(expected in error for error in self.check(prose=claim)))

    def test_theme_page_pair_coverage_and_hourly_specific_errors_fail(self):
        missing_hourly = self.check(lambda d: d["cells"].pop())
        self.assertTrue(any("canonical theme/page pair" in error for error in missing_hourly))
        duplicate_hourly = self.check(lambda d: d["cells"].__setitem__(9, copy.deepcopy(d["cells"][8])))
        self.assertTrue(any("duplicate theme/page cell ('instrument', 'hourly')" in error for error in duplicate_hourly))
        extra_pair = self.check(lambda d: d["cells"][9].__setitem__("theme", "atmospheric"))
        self.assertTrue(any("expected 'atmospheric-hourly'" in error for error in extra_pair))
        wrong_id = self.check(lambda d: d["cells"][5].__setitem__("id", "atmospheric-now"))
        self.assertTrue(any("cells[5].id: expected 'atmospheric-hourly'" in error for error in wrong_id))
        wrong_page = self.check(lambda d: d["cells"][5].__setitem__("page", "daily"))
        self.assertTrue(any("canonical theme/page pair" in error for error in wrong_page))
        bad_field = self.check(lambda d: d["cells"][5]["observation"].__setitem__("surfaces", " "))
        self.assertTrue(any("cells[5].observation.surfaces" in error for error in bad_field))
        bad_evidence = self.check(lambda d: d["cells"][5]["source_refs"][0].__setitem__("evidence_class", "guess"))
        self.assertTrue(any("cells[5].source_refs[0].evidence_class" in error for error in bad_evidence))

    def test_scope_theme_page_id_and_order_fail(self):
        cases = [
            (lambda d: d["scope"].__setitem__("themes", list(reversed(d["scope"]["themes"]))), "scope.themes"),
            (lambda d: d["scope"].__setitem__("pages", ["hourly", "now"]), "scope.pages"),
            (lambda d: d["scope"].__setitem__("cell_count", 9), "scope.cell_count"),
            (lambda d: d["scope"].__setitem__("coverage", "partial"), "scope.coverage"),
            (lambda d: d["cells"][0].__setitem__("theme", "unknown"), "cells[0].theme"),
            (lambda d: d["cells"][0].__setitem__("page", "hourly"), "duplicate theme/page cell"),
            (lambda d: d["cells"][0].__setitem__("id", "wrong-id"), "cells[0].id"),
            (lambda d: d["cells"].__setitem__(slice(0, 5), list(reversed(d["cells"][:5]))), "canonical theme/page pair"),
            (lambda d: d["cells"].__setitem__(slice(5, 10), list(reversed(d["cells"][5:10]))), "canonical theme/page pair"),
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

    def test_malformed_hourly_nested_fields_and_source_refs_fail(self):
        cases = [
            (lambda d: d["cells"][6]["proposed_treatment"].pop("palette"), "cells[6].proposed_treatment.palette: missing field"),
            (lambda d: d["cells"][7]["state_constraints"].__setitem__("high_contrast", " "), "cells[7].state_constraints.high_contrast"),
            (lambda d: d["cells"][8].__setitem__("source_gaps", []), "cells[8].source_gaps"),
            (lambda d: d["cells"][9].__setitem__("derivation_basis", ["uncited-id"]), "uncited-id"),
            (lambda d: d["cells"][5]["source_refs"][0].__setitem__("source_id", "unknown-source"), "unknown source ID 'unknown-source'"),
            (lambda d: d["cells"][5]["source_refs"][0].__setitem__("source_id", "terminal-backdrop"), "belongs to theme 'Terminal'"),
            (lambda d: d["cells"][5]["source_refs"][0].__setitem__("source_id", "atmospheric-asset-sheet-absent"), "cannot be evidence"),
            (lambda d: d["cells"][5]["source_refs"][0].__setitem__("locator", " "), "mapping-specific non-blank locator"),
            (lambda d: d["cells"][6]["source_refs"][0].__setitem__("locator", "Full extracted phone crop; inspect inner display from y≈35 to y≈725, excluding bezel/status frame; native pixels."), "too generic"),
        ]
        for mutate, expected in cases:
            with self.subTest(expected=expected):
                self.assertTrue(any(expected in error for error in self.check(mutate)))

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
            ("D31 completed.", "D31 completion"),
            ("TP.1D is approved.", "TP.1D completion"),
            ("TP.1 is closed.", "TP.1 completion"),
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
