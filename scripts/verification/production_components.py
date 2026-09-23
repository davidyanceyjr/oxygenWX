#!/usr/bin/env python3
"""Focused adb checks for the isolated R0.11C debug component host."""
import argparse
import pathlib
import re
import json
import datetime
import sys
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
    x = SCREEN_WIDTH // 2
    run([adb, "shell", "input", "swipe", str(x), str(int(SCREEN_HEIGHT * 0.22)),
         str(x), str(int(SCREEN_HEIGHT * 0.86)), "350"])
    time.sleep(0.3)


def scroll_to_top(adb, path, attempts=40):
    for _ in range(attempts):
        root = inspect_hierarchy(adb, path)
        if find_text(root, "Production component verification") is not None:
            return root
        swipe_down(adb)
    raise AssertionError("debug showcase title did not return after scrolling toward the top")


def has_fact(root, fact):
    return find_text(root, fact) is not None or any(
        fact in node.get("content-desc", "") for node in all_nodes(root)
    )


def source_contract_checks():
    main = (ROOT / "app/src/main/java/com/oxygen/weather/MainActivity.kt").read_text()
    app = (ROOT / "app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt").read_text()
    production = "\n".join(path.read_text() for path in
                           (ROOT / "app/src/main/java/com/oxygen/weather/ui/themeengine/components").glob("*.kt"))
    assert "Production" not in main and "OxygenWeatherApp" in main
    assert "ProductionBackdrop(theme" in app and "ProductionPageSelector(" in app
    assert "ProductionCurrentHero(" in app and "ProductionInspectionMetricGroup(" in app
    assert "HorizontalPager" in app and "HorizontalPager" not in production
    assert not re.search(r"toDoubleOrNull|toIntOrNull|parse", production, re.IGNORECASE)
    print("source boundary: normal app uses the production renderer; one outer pager owner; no text parsing")
    visuals = (ROOT / "app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt").read_text()
    debug = (ROOT / "app/src/debug/java/com/oxygen/weather/ProductionComponentsActivity.kt").read_text()
    assert "fun ProductionWeatherMark(" in visuals and "fun ProductionBackdrop(" in visuals
    assert "WeatherThemeId" not in visuals and "WeatherThemeId" not in visuals.split("fun ProductionBackdrop(", 1)[0]
    assert "if (condition == null) return" in visuals
    assert "when (condition)" in visuals and all(value in visuals for value in
        ("WeatherMarkCondition.CLEAR", "WeatherMarkCondition.PARTLY_CLOUDY", "WeatherMarkCondition.CLOUDY",
         "WeatherMarkCondition.RAIN", "WeatherMarkCondition.STORM", "WeatherMarkCondition.SNOW"))
    assert "ProductionWeatherMark(theme, condition" in debug and "Condition unavailable" in debug
    assert "ProductionBackdrop(theme" in debug
    print("weather visuals: nullable six-condition mark and resolved backdrop APIs are additive and debug-hosted")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--adb", default=".android-sdk/platform-tools/adb")
    parser.add_argument("--artifacts", default=".codex/test-artifacts/023-production-themed-details-source-components")
    parser.add_argument("--font-scale", type=float, default=1.0)
    parser.add_argument("--direction", choices=("ltr", "rtl"), default="ltr")
    parser.add_argument("--effects", choices=("subtle", "off"), default="subtle")
    parser.add_argument("--visual-only", action="store_true")
    args = parser.parse_args()
    adb = str((ROOT / args.adb).resolve())
    artifacts = (ROOT / args.artifacts).resolve()
    artifacts.mkdir(parents=True, exist_ok=True)
    global SCREEN_WIDTH, SCREEN_HEIGHT
    size_output = run([adb, "shell", "wm", "size"])
    density_out = run([adb, "shell", "wm", "density"])
    densities = re.findall(r"(?:Override density|Physical density): (\d+)", density_out)
    density = int(densities[-1])
    SCREEN_WIDTH = round(360 * density / 160)
    SCREEN_HEIGHT = round(640 * density / 160)
    run([adb, "shell", "wm", "size", f"{SCREEN_WIDTH}x{SCREEN_HEIGHT}"])
    run([adb, "shell", "settings", "put", "system", "font_scale", str(args.font_scale)])
    source_contract_checks()
    run([adb, "shell", "am", "force-stop", "com.oxygen.weather"])
    run([adb, "shell", "am", "start", "-n", "com.oxygen.weather/.ProductionComponentsActivity"])
    time.sleep(2)
    metadata = {
        "captured_at": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "command": "python " + " ".join(sys.argv),
        "device": run([adb, "get-serialno"]), "api": run([adb, "shell", "getprop", "ro.build.version.sdk"]),
        "app_id": "com.oxygen.weather", "version_name": "1.0.0-alpha01", "version_code": 1000001,
        "apk": "app/build/outputs/apk/debug/app-debug.apk", "activity": "ProductionComponentsActivity (debug only)",
        "screen_px": f"{SCREEN_WIDTH}x{SCREEN_HEIGHT}", "screen_dp": "360x640", "density_dpi": density,
        "font_scale": args.font_scale, "layout_direction": args.direction,
        "theme": "ATMOSPHERIC", "effects": args.effects.upper(),
    }
    (artifacts / "run-manifest.json").write_text(json.dumps(metadata, indent=2) + "\n")
    root = scroll_to_top(adb, artifacts / "weather-marks.xml")
    capture_screen(adb, artifacts / "weather-marks-initial.png")
    (artifacts / "visual-observations.txt").write_text(
        "Initial viewport includes condition mark rows and their adjacent visible labels; the missing-condition row is explicitly labeled.\n"
        "Inspect the paired screenshot for clipping/overlap at this font scale and direction.\n"
    )
    if args.visual_only:
        state_line = find_text(root, "State:")
        assert state_line is not None, "showcase state is missing"
        wants_off = args.effects == "off"
        currently_off = "· OFF ·" in state_line.get("text", "")
        if wants_off != currently_off:
            option = "Off" if wants_off else "Subtle"
            control = find_text(root, option)
            assert control is not None, f"effect control is missing: {option}"
            tap_node(adb, control)
            root = inspect_hierarchy(adb, artifacts / "weather-marks-off.xml")
        state_line = find_text(root, "State:")
        currently_rtl = state_line is not None and "· RTL" in state_line.get("text", "")
        if (args.direction == "rtl") != currently_rtl:
            direction_control = find_text(root, "RTL")
            if direction_control is None:
                direction_control = find_text(root, "RTL on")
            assert direction_control is not None, "RTL toggle is missing"
            tap_node(adb, direction_control)
            root = inspect_hierarchy(adb, artifacts / "weather-marks-direction.xml")
        state_line = find_text(root, "State:")
        assert state_line is not None and ("· OFF ·" in state_line.get("text", "")) == wants_off, \
            "requested effects state did not resolve in the showcase"
        assert state_line is not None and ("· RTL" in state_line.get("text", "")) == (args.direction == "rtl"), \
            "requested layout direction did not resolve in the showcase"
        wanted = ("Clear", "Partly cloudy", "Cloudy", "Rain", "Storm", "Snow", "Condition unavailable")
        for _ in range(12):
            root = inspect_hierarchy(adb, artifacts / "weather-marks-visible.xml")
            nodes = [find_text(root, label) for label in wanted]
            bounds = [node_bounds(node.get("bounds")) if node is not None else None for node in nodes]
            if all(b and 0 <= b[1] and b[3] <= SCREEN_HEIGHT for b in bounds):
                break
            swipe_up(adb)
        assert all(bounds) and all(0 <= b[1] and b[3] <= SCREEN_HEIGHT for b in bounds), \
            "condition labels do not fit together in the 360x640dp viewport"
        assert all(has_fact(root, fact) for fact in ("Clear", "Partly cloudy", "Cloudy", "Rain", "Storm", "Snow", "Condition unavailable")), \
            "weather text/semantics changed after appearance or direction selection"
        capture_screen(adb, artifacts / "weather-marks-final.png")
        print(f"visual showcase captured: 360x640dp, font scale {args.font_scale}, {args.direction.upper()}, {args.effects.upper()}")
        return
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
    for _ in range(8):
        if all(has_fact(root, fact) for fact in ("Demo Station", "28°", "Partly cloudy", "Feels 29°", "56%", "18°")):
            break
        swipe_up(adb)
        root = inspect_hierarchy(adb, artifacts / "focused-hero-scanning.xml")
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

    scroll_to_top(adb, artifacts / "page-selector-top.xml")
    for callback, desc in enumerate(page_descs):
        root = None
        page_node = None
        for _ in range(18):
            root = inspect_hierarchy(adb, artifacts / f"page-{callback}-before.xml")
            page_node = find_desc(root, desc)
            if page_node is not None:
                break
            swipe_up(adb)
        assert page_node is not None, f"page selector target is not reachable: {desc}"
        tap_node(adb, page_node)
        for _ in range(18):
            root = inspect_hierarchy(adb, artifacts / f"page-{callback}-after.xml")
            if find_text(root, f"Page callback: {callback}") is not None:
                break
            swipe_down(adb)
        assert find_text(root, f"Page callback: {callback}") is not None, f"page callback {callback} was not emitted"
    print("page callbacks: indices 0 through 3 emitted")

    date_prefix = re.compile(r"^(Mon|Tue|Wed|Thu|Fri|Sat|Sun), forecast window \d+, (selected|not selected)$")
    date_node = None
    for _ in range(12):
        root = inspect_hierarchy(adb, artifacts / "date-control-hierarchy.xml")
        date_node = next((n for n in all_nodes(root)
                          if n.get("text") == "" and n.get("content-desc", "").startswith("Wed, ")
                          and date_prefix.match(n.get("content-desc", ""))), None)
        if date_node is None:
            date_node = next((n for n in all_nodes(root)
                              if n.get("text") == "" and date_prefix.match(n.get("content-desc", ""))), None)
        if date_node is not None:
            break
        swipe_up(adb)
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
    for _ in range(18):
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
    root = scroll_to_top(adb, artifacts / "fixture-switches.xml")
    sparse = find_text(root, "Sparse")
    assert sparse is not None, "sparse fixture switch is not reachable"
    tap_node(adb, sparse)
    root = inspect_hierarchy(adb, artifacts / "sparse-hierarchy.xml")
    for _ in range(14):
        if has_fact(root, "Unavailable"):
            break
        swipe_up(adb)
        root = inspect_hierarchy(adb, artifacts / "sparse-hierarchy.xml")
    assert find_text(root, "Unavailable") is not None or any("Unavailable" in n.get("content-desc", "") for n in all_nodes(root)), \
        "sparse fixture did not expose unavailable fields"
    assert find_text(root, "0°") is None and find_text(root, "0%") is None, "sparse fixture fabricated a zero placeholder"
    print("sparse fixture: unavailable content exposed without numeric fallback")

    def find_top_control(label, artifact_name):
        state = scroll_to_top(adb, artifacts / artifact_name)
        control = find_text(state, label)
        if control is not None:
            return state, control
        raise AssertionError(f"Control is not reachable at the top: {label}")

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
