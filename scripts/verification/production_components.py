#!/usr/bin/env python3
"""Focused adb checks for the isolated R0.11C debug component host."""
import argparse
import pathlib
import re
import subprocess
import time
import xml.etree.ElementTree as ET


ROOT = pathlib.Path(__file__).resolve().parents[2]


def run(args, *, capture=True):
    result = subprocess.run(args, check=True, text=True, stdout=subprocess.PIPE if capture else None)
    return result.stdout.strip() if capture else ""


def node_bounds(value):
    nums = [int(part) for part in re.findall(r"\d+", value or "")]
    return nums if len(nums) == 4 else None


def inspect_hierarchy(adb, path):
    for _ in range(6):
        run([adb, "shell", "uiautomator", "dump", "/sdcard/oxygen-window.xml"])
        xml = run([adb, "shell", "cat", "/sdcard/oxygen-window.xml"])
        if "<node" in xml and "null root" not in xml:
            path.write_text(xml)
            return ET.fromstring(xml)
        time.sleep(0.5)
    raise AssertionError("uiautomator did not produce a valid hierarchy")


def all_nodes(root):
    return list(root.iter("node"))


def find_desc(root, needle):
    return next((node for node in all_nodes(root) if needle in node.get("content-desc", "")), None)


def find_text(root, needle):
    return next((node for node in all_nodes(root) if needle in node.get("text", "")), None)


def find_clickable_parent(root, child):
    def visit(node, ancestors):
        if node is child:
            return ancestors
        for nested in node:
            result = visit(nested, ancestors + [node])
            if result is not None:
                return result
        return None
    ancestors = visit(root, []) or []
    return next((node for node in reversed(ancestors) if node.get("clickable") == "true"), None)


def tap_node(adb, node):
    bounds = node_bounds(node.get("bounds"))
    assert bounds, f"node has no bounds: {node.attrib}"
    x1, y1, x2, y2 = bounds
    run([adb, "shell", "input", "tap", str((x1 + x2) // 2), str((y1 + y2) // 2)])
    time.sleep(0.7)


def swipe_up(adb):
    run([adb, "shell", "input", "swipe", "450", "1500", "450", "300", "700"])
    time.sleep(0.5)


def swipe_down(adb):
    run([adb, "shell", "input", "swipe", "20", "350", "20", "1450", "350"])
    time.sleep(0.3)


def has_fact(root, fact):
    return find_text(root, fact) is not None or any(
        fact in node.get("content-desc", "") for node in all_nodes(root)
    )


def source_contract_checks():
    main = (ROOT / "app/src/main/java/com/oxygen/weather/MainActivity.kt").read_text()
    app = (ROOT / "app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt").read_text()
    production = "\n".join(path.read_text() for path in
                           (ROOT / "app/src/main/java/com/oxygen/weather/ui/themeengine/components").glob("*.kt"))
    assert "Production" not in main and "Production" not in app
    assert "HorizontalPager" not in production
    assert not re.search(r"toDoubleOrNull|toIntOrNull|parse", production, re.IGNORECASE)
    print("source boundary: normal app has no production component reference; no pager or text parsing")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--adb", default=".android-sdk/platform-tools/adb")
    parser.add_argument("--artifacts", default=".codex/test-artifacts/022-production-themed-shared-components")
    args = parser.parse_args()
    adb = str((ROOT / args.adb).resolve())
    artifacts = (ROOT / args.artifacts).resolve()
    artifacts.mkdir(parents=True, exist_ok=True)
    source_contract_checks()
    run([adb, "shell", "am", "force-stop", "com.oxygen.weather"])
    run([adb, "shell", "am", "start", "-n", "com.oxygen.weather/.ProductionComponentsActivity"])
    time.sleep(2)
    for _ in range(7):
        run([adb, "shell", "input", "swipe", "20", "350", "20", "1450", "250"])
    page_descs = ["Now page, 1 of 4", "Hourly page, 2 of 4", "Daily page, 3 of 4", "Details page, 4 of 4"]
    root = None
    for _ in range(15):
        try:
            candidate = inspect_hierarchy(adb, artifacts / "focused-initial.xml")
            if all(find_desc(candidate, desc) is not None for desc in page_descs):
                root = candidate
                break
        except AssertionError:
            pass
        time.sleep(1)
    assert root is not None, "debug host did not expose the global page selector"
    page_nodes = [find_desc(root, desc) for desc in page_descs]
    assert all(node.get("content-desc", "").startswith(prefix) for node, prefix in zip(page_nodes, page_descs))
    assert sum(", selected" in node.get("content-desc", "") for node in page_nodes) == 1
    current_facts = ("Demo Station", "28°", "Partly cloudy", "Feels 29°")
    for fact in current_facts:
        assert has_fact(root, fact), \
            f"current hero fact is missing: {fact}"
    swipe_up(adb)
    root = inspect_hierarchy(adb, artifacts / "focused-hero-lower.xml")
    for fact in ("56%", "18°"):
        assert has_fact(root, fact), f"current hero fact is missing: {fact}"
    for _ in range(3):
        swipe_down(adb)
    print("current hero: supplied location, temperature, condition, apparent temperature, humidity, dew point visible/semantic")

    # Appearance inputs are presentation-only: check the same mapped facts after
    # Effects Off, High contrast, Simple layout, and an alternate theme resolve.
    for option in ("Off", "High contrast", "Simple", "Glass"):
        state = inspect_hierarchy(adb, artifacts / f"appearance-{option.lower().replace(' ', '-')}.xml")
        control = find_text(state, option)
        assert control is not None, f"appearance control is missing: {option}"
        tap_node(adb, control)
        state = inspect_hierarchy(adb, artifacts / f"appearance-{option.lower().replace(' ', '-')}-after.xml")
        for fact in ("28°", "Partly cloudy", "Feels 29°"):
            assert has_fact(state, fact), \
                f"{option} changed or hid supplied current fact: {fact}"
    print("appearance invariance: current weather facts remain present across Off, High contrast, Simple, Glass")
    density_out = run([adb, "shell", "wm", "density"])
    density = int(re.search(r"Physical density: (\d+)", density_out).group(1))
    min_px = round(48 * density / 160)
    for node in page_nodes:
        bounds = node_bounds(node.get("bounds"))
        assert bounds[3] - bounds[1] >= min_px, f"page control below 48dp: {node.attrib}"
    print(f"page selector: four named targets, each at least 48dp ({min_px}px at density {density})")

    for desc, callback in zip(page_descs, range(4)):
        root = inspect_hierarchy(adb, artifacts / f"page-{callback}-before.xml")
        tap_node(adb, find_desc(root, desc))
        root = inspect_hierarchy(adb, artifacts / f"page-{callback}-after.xml")
        assert find_text(root, f"Page callback: {callback}") is not None, f"page callback {callback} was not emitted"
    print("page callbacks: indices 0 through 3 emitted")

    for _ in range(1):
        swipe_up(adb)
    root = inspect_hierarchy(adb, artifacts / "date-control-hierarchy.xml")
    date_prefix = re.compile(r"^(Mon|Tue|Wed|Thu|Fri|Sat|Sun), forecast window \d+, (selected|not selected)$")
    date_node = next((n for n in all_nodes(root)
                      if n.get("text") == "" and n.get("content-desc", "").startswith("Wed, ")
                      and date_prefix.match(n.get("content-desc", ""))), None)
    if date_node is None:
        date_node = next((n for n in all_nodes(root)
                          if n.get("text") == "" and date_prefix.match(n.get("content-desc", ""))), None)
    assert date_node is not None, "a supplied date jump is missing from visible hierarchy"
    dbounds = node_bounds(date_node.get("bounds"))
    assert dbounds[3] - dbounds[1] >= min_px, f"date target below 48dp: {date_node.attrib}"
    tap_node(adb, date_node)
    selected_date = inspect_hierarchy(adb, artifacts / "date-selected-hierarchy.xml")
    selected_date_node = find_desc(selected_date, "forecast window 4, selected")
    assert selected_date_node is not None, "date jump did not expose selected wording"
    selected_target = find_clickable_parent(selected_date, selected_date_node)
    assert selected_target is not None and selected_target.get("checked") == "true", \
        "date jump did not expose Compose selected semantics on its clickable target"
    for _ in range(8):
        run([adb, "shell", "input", "swipe", "20", "450", "20", "1450", "250"])
        time.sleep(0.1)
    root = inspect_hierarchy(adb, artifacts / "date-callback.xml")
    assert find_text(root, "Date callback: 3") is not None, "date jump did not emit its supplied windowIndex callback"
    print("hourly date control: visible, at least 48dp, callback emitted")

    earlier = later = None
    for _ in range(12):
        root = inspect_hierarchy(adb, artifacts / "window-controls.xml")
        earlier = find_text(root, "Earlier")
        later = find_text(root, "Later")
        if earlier is not None and later is not None:
            break
        swipe_up(adb)
    assert earlier is not None and later is not None, "window controls are missing"
    earlier_target = find_clickable_parent(root, earlier)
    later_target = find_clickable_parent(root, later)
    assert earlier_target is not None and later_target is not None, "window control target bounds are missing"
    for target in (earlier_target, later_target):
        bounds = node_bounds(target.get("bounds"))
        assert bounds[3] - bounds[1] >= min_px, f"window control below 48dp: {target.attrib}"
    assert earlier_target.get("enabled") == "false", "first-window Earlier should be disabled"
    assert later_target.get("enabled") == "true", "Later should be enabled for the complete horizon"
    tap_node(adb, earlier)
    root = inspect_hierarchy(adb, artifacts / "disabled-earlier.xml")
    assert find_text(root, "Earlier callback") is None, "disabled Earlier emitted a callback"
    tap_node(adb, later)
    for _ in range(5):
        run([adb, "shell", "input", "swipe", "450", "350", "450", "1450", "250"])
        time.sleep(0.1)
    root = inspect_hierarchy(adb, artifacts / "later-callback.xml")
    assert find_text(root, "Later callback") is not None, "enabled Later did not emit its callback"
    print("window controls: Earlier disabled without callback; Later emits callback")

    # Inspect actual mapped forecast summaries in chronological order.
    first_hour = None
    for _ in range(12):
        root = inspect_hierarchy(adb, artifacts / "hourly-entry-hierarchy.xml")
        first_hour = find_desc(root, "9 AM, Clear, 19°")
        if first_hour is not None:
            break
        swipe_up(adb)
    assert first_hour is not None, "first supplied hourly spoken summary is missing"
    first_day = None
    for _ in range(14):
        root = inspect_hierarchy(adb, artifacts / "daily-entry-hierarchy.xml")
        first_day = find_desc(root, "TODAY, Clear, low 19°, high 31°, precipitation 8%")
        if first_day is not None:
            break
        swipe_up(adb)
    assert first_day is not None, "first supplied daily spoken summary is missing"
    print("forecast entries: first actual hourly and daily spoken summaries match fixture values/order")

    # The top controls are returned to view before checking sparse states.
    for _ in range(5):
        run([adb, "shell", "input", "swipe", "20", "350", "20", "1450", "350"])
        time.sleep(0.15)
    root = inspect_hierarchy(adb, artifacts / "fixture-switches.xml")
    sparse = find_text(root, "Sparse")
    assert sparse is not None, "sparse fixture switch is not reachable"
    tap_node(adb, sparse)
    root = inspect_hierarchy(adb, artifacts / "sparse-hierarchy.xml")
    assert find_text(root, "Unavailable") is not None or any("Unavailable" in n.get("content-desc", "") for n in all_nodes(root)), \
        "sparse fixture did not expose unavailable fields"
    assert find_text(root, "0°") is None and find_text(root, "0%") is None, "sparse fixture fabricated a zero placeholder"
    print("sparse fixture: unavailable content exposed without numeric fallback")
if __name__ == "__main__":
    main()
