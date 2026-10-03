#!/usr/bin/env python3
"""Validate the retained installed sparse-capture evidence packet."""

import json
import struct
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parent
INSTALLED = ROOT / "installed"
identity = json.loads((INSTALLED / "identity.json").read_text(encoding="utf-8"))
required = {
    "apk_sha256",
    "installed_apk_sha256",
    "package",
    "version_name",
    "version_code",
    "device",
    "api_level",
    "viewport_dp",
    "font_scale",
    "locale",
    "layout_direction",
    "theme",
    "contrast",
    "effects",
    "fixture_name",
    "launch_extras",
    "launch_command",
    "screenshot",
    "hierarchy",
}
missing = sorted(required - identity.keys())
assert not missing, f"identity.json missing fields: {missing}"
assert identity["package"] == "com.oxygen.weather"
assert identity["apk_sha256"] == identity["installed_apk_sha256"], "installed APK does not match captured build"
assert identity["fixture_name"] == "demo_sparse_partial_horizon_v1"
assert identity["launch_extras"] == {
    "oxygen_deterministic_capture": True,
    "oxygen_sparse_fixture": True,
}

png = (INSTALLED / identity["screenshot"]).read_bytes()
assert png[:8] == b"\x89PNG\r\n\x1a\n", "screenshot is not PNG"
width, height = struct.unpack(">II", png[16:24])
assert (width, height) == (393, 852), f"unexpected screenshot size: {width} x {height}"

tree = ET.parse(INSTALLED / identity["hierarchy"])
nodes = list(tree.getroot().iter())
texts = [node.attrib.get("text", "") for node in nodes]
descriptions = [node.attrib.get("content-desc", "") for node in nodes]
combined = "\n".join(texts + descriptions)
for marker in (
    "Now page, 1 of 4, selected",
    "Unavailable",
    "Hourly forecast horizon is partial.",
    "Daily forecast horizon is partial.",
    "demo_sparse_partial_horizon_v1 development fixture. Freshness: unknown.",
):
    assert marker in combined, f"hierarchy is missing expected installed state: {marker}"
assert "Live weather data" not in combined, "fixture was presented as live provider data"

print("Cycle 102 evidence validation passed: matching installed/build APK, 393x852 PNG, and expected sparse Home semantics.")
