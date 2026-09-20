#!/usr/bin/env python3
"""Cross-platform developer entry point for Oxygen Weather."""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]

GRADLE_TASKS = {
    "build": [":app:assembleDebug"],
    "test": [":app:testDebugUnitTest"],
    "lint": [":app:lintDebug"],
    "check": [":app:testDebugUnitTest", ":app:lintDebug", ":app:assembleDebug"],
    "install": [":app:installDebug"],
}


def gradle_launcher() -> Path:
    return ROOT / ("gradlew.bat" if os.name == "nt" else "gradlew")


def run_gradle(tasks: list[str]) -> int:
    launcher = gradle_launcher()
    if not launcher.exists():
        print(f"Gradle launcher not found: {launcher}", file=sys.stderr)
        return 2
    gradle_args = [str(launcher), "--no-daemon", *tasks]
    if os.name == "nt":
        command_line = subprocess.list2cmdline(gradle_args)
        command = ["cmd.exe", "/d", "/s", "/c", command_line]
    else:
        command = gradle_args
    return subprocess.run(command, cwd=ROOT, check=False).returncode



def source_contract() -> int:
    source_root = ROOT / "app" / "src" / "main" / "java"
    kotlin_files = list(source_root.rglob("*.kt"))
    combined = "\n".join(path.read_text(encoding="utf-8") for path in kotlin_files)
    forbidden = {
        "legacy package": "org.atmospheredeck",
        "legacy page rail": "PageRail",
        "legacy atmosphere dial": "AtmosphereDial",
        "legacy weather braid": "WeatherBraid",
        "legacy fingerprint UI": "ForecastFingerprint",
    }
    failures = [name for name, marker in forbidden.items() if marker in combined]

    ui_file = ROOT / "app" / "src" / "main" / "java" / "com" / "oxygen" / "weather" / "ui" / "OxygenWeatherApp.kt"
    ui_text = ui_file.read_text(encoding="utf-8")
    pager_count = ui_text.count("HorizontalPager(")
    if pager_count != 1:
        failures.append(f"expected exactly one outer HorizontalPager, found {pager_count}")

    manifest = (ROOT / "app" / "src" / "main" / "AndroidManifest.xml").read_text(encoding="utf-8")
    build_file = (ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
    if "com.oxygen.weather" not in build_file or "Oxygen Weather" not in manifest:
        failures.append("Oxygen application identity is incomplete")

    if failures:
        print("Source-contract check failed:", file=sys.stderr)
        for failure in failures:
            print(f"  - {failure}", file=sys.stderr)
        return 1
    print("Source-contract check passed: new Oxygen UI only, one outer pager, Oxygen app identity present.")
    return 0

def diff_check() -> int:
    probe = subprocess.run(
        ["git", "rev-parse", "--is-inside-work-tree"],
        cwd=ROOT,
        check=False,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )
    if probe.returncode != 0:
        return 0
    return subprocess.run(["git", "diff", "--check"], cwd=ROOT, check=False).returncode


def adb_path() -> str | None:
    binary = "adb.exe" if os.name == "nt" else "adb"
    direct = shutil.which(binary)
    if direct:
        return direct
    for key in ("ANDROID_SDK_ROOT", "ANDROID_HOME"):
        root = os.environ.get(key)
        if root:
            candidate = Path(root) / "platform-tools" / binary
            if candidate.exists():
                return str(candidate)
    return None


def adb_command(serial: str | None, *args: str) -> list[str] | None:
    adb = adb_path()
    if not adb:
        print("adb was not found. Add Android platform-tools to PATH or set ANDROID_SDK_ROOT/ANDROID_HOME.", file=sys.stderr)
        return None
    command = [adb]
    if serial:
        command += ["-s", serial]
    return [*command, *args]


def launch_app(serial: str | None) -> int:
    command = adb_command(serial, "shell", "am", "start", "-n", "com.oxygen.weather/.MainActivity")
    if command is None:
        return 2
    return subprocess.run(command, cwd=ROOT, check=False).returncode


def screenshot(serial: str | None, output: Path) -> int:
    command = adb_command(serial, "exec-out", "screencap", "-p")
    if command is None:
        return 2
    output = output if output.is_absolute() else ROOT / output
    output.parent.mkdir(parents=True, exist_ok=True)
    with output.open("wb") as handle:
        result = subprocess.run(command, cwd=ROOT, stdout=handle, check=False)
    if result.returncode == 0:
        print(output)
    return result.returncode


def main() -> int:
    parser = argparse.ArgumentParser(description="Build, run, and verify Oxygen Weather on Windows, macOS, or Linux.")
    parser.add_argument(
        "command",
        choices=["build", "test", "lint", "contract", "workflow", "check", "install", "run", "screenshot"],
        nargs="?",
        default="check",
    )
    parser.add_argument("--serial", help="Optional adb device/emulator serial.")
    parser.add_argument(
        "--output",
        type=Path,
        default=Path(".codex/test-artifacts/manual/home.png"),
        help="Screenshot output path for the screenshot command.",
    )
    args = parser.parse_args()

    if args.command == "contract":
        return source_contract()

    if args.command == "workflow":
        return subprocess.run([sys.executable, str(ROOT / "scripts" / "codex_cycle.py"), "validate"], cwd=ROOT, check=False).returncode

    if args.command in GRADLE_TASKS:
        if args.command == "check":
            rc = subprocess.run([sys.executable, str(ROOT / "scripts" / "codex_cycle.py"), "validate"], cwd=ROOT, check=False).returncode
            if rc != 0:
                return rc
            rc = source_contract()
            if rc != 0:
                return rc
        rc = run_gradle(GRADLE_TASKS[args.command])
        if rc != 0:
            return rc
        return diff_check() if args.command == "check" else 0

    if args.command == "run":
        rc = run_gradle(GRADLE_TASKS["install"])
        return rc if rc != 0 else launch_app(args.serial)

    if args.command == "screenshot":
        return screenshot(args.serial, args.output)

    return 2


if __name__ == "__main__":
    raise SystemExit(main())
