"""Deterministic checks for the complete proposed D29 weather-art matrix."""

import copy
import json
from pathlib import Path
import re
import unittest

from scripts.verification import weather_art_spec as checker


class WeatherArtSpecTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.text = checker.SPEC_PATH.read_text(encoding="utf-8")
        cls.data = checker._matrix(cls.text)

    def replace_matrix(self, data, text=None):
        source = text or self.text
        encoded = json.dumps(data, indent=2)
        return re.sub(
            r"(```json\s*)(.*?)(\s*```)" ,
            lambda match: match.group(1) + encoded + match.group(3),
            source,
            count=1,
            flags=re.DOTALL,
        )

    def test_document_has_exact_complete_matrix_and_sources(self):
        self.assertEqual(checker.validate(self.text), [])
        self.assertEqual(len(self.data["cells"]), 30)
        self.assertEqual({cell["condition"] for cell in self.data["cells"]}, checker.CONDITIONS)
        self.assertEqual({cell["theme"] for cell in self.data["cells"]}, checker.THEMES)

    def test_missing_cell_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"].pop()
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("exactly 30" in error for error in errors))
        self.assertTrue(any("matrix pairs missing" in error for error in errors))

    def test_duplicate_cell_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][1] = copy.deepcopy(data["cells"][0])
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("duplicates matrix key" in error for error in errors))
        self.assertTrue(any("matrix pairs missing" in error for error in errors))

    def test_extra_cell_rejected(self):
        data = copy.deepcopy(self.data)
        extra = copy.deepcopy(data["cells"][0])
        extra["theme"] = "retired_theme"
        data["cells"].append(extra)
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("exactly 30" in error for error in errors))
        self.assertTrue(any("invalid theme ID" in error for error in errors))
        self.assertTrue(any("unexpected pairs" in error for error in errors))

    def test_invalid_theme_and_condition_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0]["theme"] = "retired_theme"
        data["cells"][1]["condition"] = "FOG"
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("invalid theme ID" in error for error in errors))
        self.assertTrue(any("invalid condition ID" in error for error in errors))

    def test_missing_field_and_empty_required_text_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0].pop("locator")
        data["cells"][1]["fallback"] = "  "
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("missing fields" in error and "locator" in error for error in errors))
        self.assertTrue(any("requires non-empty fallback" in error for error in errors))

    def test_invalid_source_status_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0]["source_status"] = "approved"
        self.assertTrue(any("invalid source_status" in error for error in checker.validate(self.replace_matrix(data))))

    def test_empty_or_generic_locator_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0]["locator"] = " "
        data["cells"][1]["locator"] = "board only"
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("requires non-empty locator" in error for error in errors))
        self.assertTrue(any("locator is too generic" in error for error in errors))

    def test_unresolvable_source_path_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0]["source"] = "docs/no-such-art.png"
        self.assertTrue(any("source path does not resolve" in error for error in checker.validate(self.replace_matrix(data))))

    def test_malformed_json_rejected(self):
        text = re.sub(r"(?<=```json\n).*?(?=\n```)" , "{broken json", self.text, count=1, flags=re.S)
        self.assertTrue(any("matrix JSON is invalid" in error for error in checker.validate(text)))

    def test_coverage_metadata_rejected(self):
        data = copy.deepcopy(self.data)
        data["cell_count"] = 15
        data["covered_conditions"] = ["CLEAR"]
        data["pending_conditions"] = ["SNOW"]
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("cell_count metadata" in error for error in errors))
        self.assertTrue(any("covered_conditions" in error for error in errors))
        self.assertTrue(any("pending_conditions" in error for error in errors))

    def test_ungrounded_proposal_and_gap_rejected(self):
        data = copy.deepcopy(self.data)
        proposed = next(cell for cell in data["cells"] if cell["source_status"] == "proposed")
        proposed["treatment"] = "Draw a mark with no source basis."
        gap = next(cell for cell in data["cells"] if cell["source_status"] == "gap")
        gap["treatment"] = "Draw a snowflake from intuition."
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("same-theme visual basis" in error for error in errors))
        self.assertTrue(any("distinguish its basis" in error for error in errors))
        self.assertTrue(any("gap/omission must explicitly record" in error for error in errors))
        self.assertTrue(any("gap/omission needs an explicit no-mark" in error for error in errors))

    def test_direct_art_must_be_condition_specific(self):
        data = copy.deepcopy(self.data)
        rain = next(cell for cell in data["cells"] if cell["theme"] == "glass" and cell["condition"] == "RAIN")
        rain["source"] = "docs/assets/design-references/production-themes/glass/extracted/icons/cloudy.png"
        rain["locator"] = "03_iconography crop, Cloudy icon centered above its caption"
        self.assertTrue(any("condition-specific source art" in error for error in checker.validate(self.replace_matrix(data))))

    def test_unsupported_approval_claim_rejected(self):
        self.assertTrue(any("unsupported broader approval" in error for error in checker.validate(self.text + "\nTP.1D packet approved.\n")))

    def test_approved_matrix_requires_decision_record(self):
        data = copy.deepcopy(self.data)
        data["status"] = "owner-approved proposed matrix"
        data.pop("owner_decision", None)
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("structured owner_decision" in error for error in errors))
        data["owner_decision"] = {
            "date": "2026-09-24",
            "disposition": "approved as presented",
            "scope": "D29 30-cell weather-mark matrix only",
            "reviewed_artifact_sha256": "e" * 64,
        }
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("hash must appear" in error for error in errors))

    def test_approved_matrix_cannot_claim_packet_or_tp2_approval(self):
        self.assertTrue(any(
            "unsupported broader approval" in error
            for error in checker.validate(self.text + "\nThe TP.1D packet is approved.\n")
        ))

    def test_stale_partial_claim_rejected(self):
        self.assertTrue(any("stale partial-coverage" in error for error in checker.validate(self.text + "\nCoverage: partial.\n")))

    def test_matrix_markers_and_json_block_required(self):
        self.assertIn("markers are missing", checker.validate("no markers" )[0])
        text = self.text.replace("```json", "```text", 1)
        self.assertIn("JSON block is missing", checker.validate(text)[0])


if __name__ == "__main__":
    unittest.main()
