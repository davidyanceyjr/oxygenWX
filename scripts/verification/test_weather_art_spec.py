"""Deterministic checks for the partial D29 weather-art specification."""

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
            r"(```json\s*)(.*?)(\s*```)",
            lambda match: match.group(1) + encoded + match.group(3),
            source,
            count=1,
            flags=re.DOTALL,
        )

    def test_document_has_exact_partial_matrix_and_existing_sources(self):
        self.assertEqual(checker.validate(self.text), [])

    def test_duplicate_cell_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][1] = copy.deepcopy(data["cells"][0])
        self.assertTrue(any("duplicates matrix key" in error for error in checker.validate(self.replace_matrix(data))))

    def test_missing_cell_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"].pop()
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("exactly 15" in error for error in errors))
        self.assertTrue(any("matrix pairs missing" in error for error in errors))

    def test_missing_source_locator_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0].pop("locator")
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("missing fields" in error and "locator" in error for error in errors))

    def test_invalid_theme_and_condition_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0]["theme"] = "retired_theme"
        data["cells"][1]["condition"] = "FOG"
        errors = checker.validate(self.replace_matrix(data))
        self.assertTrue(any("invalid theme ID" in error for error in errors))
        self.assertTrue(any("invalid condition ID" in error for error in errors))

    def test_unresolvable_source_path_rejected(self):
        data = copy.deepcopy(self.data)
        data["cells"][0]["source"] = "docs/no-such-art.png"
        self.assertTrue(any("source path does not resolve" in error for error in checker.validate(self.replace_matrix(data))))

    def test_full_coverage_claim_rejected(self):
        self.assertTrue(any("full-coverage" in error for error in checker.validate(self.text + "\nCoverage: complete.\n")))
        data = copy.deepcopy(self.data)
        data["coverage"] = "complete"
        self.assertTrue(any("coverage must be explicitly partial" in error for error in checker.validate(self.replace_matrix(data))))


if __name__ == "__main__":
    unittest.main()
