#!/usr/bin/env python3
"""Focused adb checks for the isolated R0.11C debug component host."""
import argparse
import pathlib
import re
import subprocess
import time
import xml.etree.ElementTree as ET


ROOT = pathlib.Path(__file__).resolve().parents[2]
SCREEN_WIDTH = 1080
SCREEN_HEIGHT = 1920


def run(args, *, capture=True):
    result = subprocess.run(args, check=True, text=True, stdout=subprocess.PIPE if capture else None)
    return result.stdout.strip() if capture else ""


def node_bounds(value):
    nums = [int(part) for part in re.findall(r"\d+", value or "")]
    return nums if len(nums) == 4 else None


def inspect_hierarchy(adb, path):
    for _ in range(6):
        try:
            run([adb, "shell", "uiautomator", "dump", "/sdcard/oxygen-window.xml"])
            xml = run([adb, "shell", "cat", "/sdcard/oxygen-window.xml"])
            if "<node" in xml and "null root" not in xml:
                path.write_text(xml)
                return ET.fromstring(xml)
        except subprocess.CalledProcessError:
            pass
        time.sleep(0.5)
    raise AssertionError("uiautomator did not produce a valid hierarchy")


def capture_screen(adb, path):
    with path.open("wb") as output:
        subprocess.run([adb, "exec-out", "screencap", "-p"], check=True, stdout=output)


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
    x = SCREEN_WIDTH // 2
    run([adb, "shell", "input", "swipe", str(x), str(int(SCREEN_HEIGHT * 0.86)),
         str(x), str(int(SCREEN_HEIGHT * 0.22)), "450"])
    time.sleep(0.5)


def swipe_down(adb):
    x = max(20, int(SCREEN_WIDTH * 0.04))
    run([adb, "shell", "input", "swipe", str(x), str(int(SCREEN_HEIGHT * 0.22)),
         str(x), str(int(SCREEN_HEIGHT * 0.86)), "350"])
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
    parser.add_argument("--artifacts", default=".codex/test-artifacts/023-production-themed-details-source-components")
    args = parser.parse_args()
    adb = str((ROOT / args.adb).resolve())
    artifacts = (ROOT / args.artifacts).resolve()
    artifacts.mkdir(parents=True, exist_ok=True)
    global SCREEN_WIDTH, SCREEN_HEIGHT
    size_output = run([adb, "shell", "wm", "size"])
    sizes = re.findall(r"(?:Override size|Physical size): (\d+)x(\d+)", size_output)
    if sizes:
        SCREEN_WIDTH, SCREEN_HEIGHT = map(int, sizes[-1])
    source_contract_checks()
    run([adb, "shell", "am", "force-stop", "com.oxygen.weather"])
    run([adb, "shell", "am", "start", "-n", "com.oxygen.weather/.ProductionComponentsActivity"])
    time.sleep(2)
    page_descs = ["Now page, 1 of 4", "Hourly page, 2 of 4", "Daily page, 3 of 4", "Details page, 4 of 4"]
    root = None
    for _ in range(18):
        try:
            candidate = inspect_hierarchy(adb, artifacts / "focused-initial.xml")
            if all(find_desc(candidate, desc) is not None for desc in page_descs) and has_fact(candidate, "28°"):
                root = candidate
                break
        except AssertionError:
            pass
        swipe_up(adb)
    assert root is not None, "debug host did not expose the global page selector"
    page_nodes = [find_desc(root, desc) for desc in page_descs]
    assert all(node.get("content-desc", "").startswith(prefix) for node, prefix in zip(page_nodes, page_descs))
    assert sum(", selected" in node.get("content-desc", "") for node in page_nodes) == 1
    current_facts = ("Demo Station", "28°", "Partly cloudy", "Feels 29°")
    for fact in current_facts:
        assert has_fact(root, fact), \
            f"current hero fact is missing: {fact}"
    for fact in ("56%", "18°"):
        assert has_fact(root, fact), f"current hero fact is missing: {fact}"
    capture_screen(adb, artifacts / "focused-hero-lower.png")
    for _ in range(12):
        swipe_down(adb)
    print("current hero: supplied location, temperature, condition, apparent temperature, humidity, dew point visible/semantic")

    # Appearance inputs are presentation-only: check the same mapped facts after
    # Effects Off, High contrast, Simple layout, and an alternate theme resolve.
    for option in ("Off", "High contrast", "Simple", "Glass"):
        control = None
        for _ in range(18):
            state = inspect_hierarchy(adb, artifacts / f"appearance-{option.lower().replace(' ', '-')}.xml")
            control = find_text(state, option)
            if control is not None:
                break
            swipe_down(adb)
        assert control is not None, f"appearance control is missing: {option}"
        tap_node(adb, control)
        appearance_facts = ("28°", "Partly cloudy", "Feels 29°")
        observed = ""
        for _ in range(18):
            state = inspect_hierarchy(adb, artifacts / f"appearance-{option.lower().replace(' ', '-')}-after.xml")
            observed += "\n" + "\n".join(
                node.get("text", "") + node.get("content-desc", "") for node in all_nodes(state)
            )
            if all(fact in observed for fact in appearance_facts):
                break
            swipe_up(adb)
        for fact in appearance_facts:
            assert fact in observed, \
                f"{option} changed or hid supplied current fact: {fact}"
    print("appearance invariance: current weather facts remain present across Off, High contrast, Simple, Glass")
    density_out = run([adb, "shell", "wm", "density"])
    densities = re.findall(r"(?:Override density|Physical density): (\d+)", density_out)
    density = int(densities[-1])
    min_px = round(48 * density / 160)
    for node in page_nodes:
        bounds = node_bounds(node.get("bounds"))
        assert bounds[3] - bounds[1] + 1 >= min_px, f"page control below 48dp: {node.attrib}"
    print(f"page selector: four named targets, each at least 48dp ({min_px}px at density {density})")

    for desc, callback in zip(page_descs, range(4)):
        root = inspect_hierarchy(adb, artifacts / f"page-{callback}-before.xml")
        tap_node(adb, find_desc(root, desc))
        for _ in range(18):
            root = inspect_hierarchy(adb, artifacts / f"page-{callback}-after.xml")
            if find_text(root, f"Page callback: {callback}") is not None:
                break
            swipe_down(adb)
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
    assert dbounds[3] - dbounds[1] + 1 >= min_px, f"date target below 48dp: {date_node.attrib}"
    tap_node(adb, date_node)
    selected_date = inspect_hierarchy(adb, artifacts / "date-selected-hierarchy.xml")
    selected_date_node = find_desc(selected_date, "forecast window 4, selected")
    assert selected_date_node is not None, "date jump did not expose selected wording"
    selected_target = find_clickable_parent(selected_date, selected_date_node)
    assert selected_target is not None and selected_target.get("checked") == "true", \
        "date jump did not expose Compose selected semantics on its clickable target"
    for _ in range(8):
        swipe_down(adb)
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
        assert bounds[3] - bounds[1] + 1 >= min_px, f"window control below 48dp: {target.attrib}"
    assert earlier_target.get("enabled") == "false", "first-window Earlier should be disabled"
    assert later_target.get("enabled") == "true", "Later should be enabled for the complete horizon"
    tap_node(adb, earlier)
    root = inspect_hierarchy(adb, artifacts / "disabled-earlier.xml")
    assert find_text(root, "Earlier callback") is None, "disabled Earlier emitted a callback"
    tap_node(adb, later)
    for _ in range(5):
        swipe_down(adb)
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
        swipe_down(adb)
    root = inspect_hierarchy(adb, artifacts / "fixture-switches.xml")
    sparse = find_text(root, "Sparse")
    assert sparse is not None, "sparse fixture switch is not reachable"
    tap_node(adb, sparse)
    root = inspect_hierarchy(adb, artifacts / "sparse-hierarchy.xml")
    assert find_text(root, "Unavailable") is not None or any("Unavailable" in n.get("content-desc", "") for n in all_nodes(root)), \
        "sparse fixture did not expose unavailable fields"
    assert find_text(root, "0°") is None and find_text(root, "0%") is None, "sparse fixture fabricated a zero placeholder"
    print("sparse fixture: unavailable content exposed without numeric fallback")

    def find_top_control(label, artifact_name):
        for _ in range(17):
            state = inspect_hierarchy(adb, artifacts / artifact_name)
            control = find_text(state, label)
            if control is not None:
                return state, control
            swipe_down(adb)
        raise AssertionError(f"Control is not reachable: {label}")

    def select_fixture(label):
        state, control = find_top_control(
            label, f"fixture-{label.lower().replace(' ', '-')}-controls.xml")
        assert control is not None, f"Details fixture control is missing: {label}"
        tap_node(adb, control)
        time.sleep(0.5)
        name = label.lower().replace(" ", "-")
        observed = []

        def collect(state):
            observed.extend(node.get("text", "") for node in all_nodes(state))
            observed.extend(node.get("content-desc", "") for node in all_nodes(state))

        root = inspect_hierarchy(adb, artifacts / f"details-{name}-scanning.xml")
        collect(root)
        for _ in range(30):
            if find_text(root, "Source") is not None:
                break
            swipe_up(adb)
            root = inspect_hierarchy(adb, artifacts / f"details-{name}-scanning.xml")
            collect(root)
        else:
            raise AssertionError(f"Details source panel is not reachable for fixture {label}")
        inspect_hierarchy(adb, artifacts / f"details-{name}-source.xml")
        capture_screen(adb, artifacts / f"details-{name}-source.png")
        for _ in range(12):
            if find_text(root, "Historical context") is not None:
                break
            swipe_up(adb)
            root = inspect_hierarchy(adb, artifacts / f"details-{name}-groups.xml")
            collect(root)
        root = inspect_hierarchy(adb, artifacts / f"details-{name}.xml")
        capture_screen(adb, artifacts / f"details-{name}.png")
        return "\n".join(observed), root

    def ordered_text(visible, expected):
        cursor = 0
        for value in expected:
            found = visible.find(value, cursor)
            assert found >= 0, f"Details text is missing or out of order: {value}"
            cursor = found + len(value)

    complete, complete_root = select_fixture("Complete")
    ordered_text(complete, ["Details inspection", "Source", "Update time", "Conditions",
                            "Forecast pattern", "Historical context"])
    for fact in ("Feels like", "Humidity", "Dew point", "Forecast pattern", "Historical context"):
        assert fact in complete, f"Details presentation fact is missing: {fact}"
    print("Details complete fixture: source/update and supplied group/metric text remain visible in order")

    for option in ("Off", "High contrast", "Simple", "Glass"):
        state, control = find_top_control(
            option, f"details-{option.lower().replace(' ', '-')}-control.xml")
        assert control is not None, f"Details appearance control is missing: {option}"
        tap_node(adb, control)
        visible, state = select_fixture("Complete")
        for fact in ("Details inspection", "Offline development fixture", "Conditions", "Feels like", "Historical context"):
            assert fact in visible, f"{option} changed or hid a supplied Details fact: {fact}"
        capture_screen(adb, artifacts / f"details-{option.lower().replace(' ', '-')}.png")
    print("Details appearance invariance: key source and metric facts remain across Off, High contrast, Simple, and Glass")

    sparse_details, sparse_root = select_fixture("Sparse")
    ordered_text(sparse_details, ["Details inspection", "Source", "Update time"])
    assert "No data" not in sparse_details, "sparse Details fixture invented a placeholder metric"
    print("Details sparse fixture: no placeholder metric was added")

    long_source, long_source_root = select_fixture("Long source")
    for fact in ("Northwestern Regional Weather Observation", "Tuesday, September 22, 2026"):
        assert fact in long_source, f"long source/update text was clipped or hidden: {fact}"
    print("Details long source fixture: supplied source and update lines remain reachable")

    long_metrics, long_metrics_root = select_fixture("Long metrics")
    for fact in ("Extended supporting measurement label for wrapping",
                 "A deliberately long supplied value", "Supporting context remains visible"):
        assert fact in long_metrics, f"long metric text is missing: {fact}"
    print("Details long metric fixture: label, value, and supporting line remain reachable")

    root, rtl = find_top_control("RTL", "rtl-controls.xml")
    if rtl is None:
        root, rtl = find_top_control("RTL on", "rtl-controls.xml")
    assert rtl is not None, "RTL control is missing"
    tap_node(adb, rtl)
    rtl_details, root = select_fixture("Complete")
    assert "Details inspection" in rtl_details and "Offline development fixture" in rtl_details, \
        "RTL changed or hid supplied Details text"
    capture_screen(adb, artifacts / "details-rtl.png")
    print("Details RTL fixture: source and group meaning remain visible")
if __name__ == "__main__":
    main()
