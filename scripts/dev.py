#!/usr/bin/env python3
"""Cross-platform developer entry point for Oxygen Weather."""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
MIN_JAVA_MAJOR = 17

GRADLE_TASKS = {
    "build": [":app:assembleDebug"],
    "test": [":app:testDebugUnitTest"],
    "lint": [":app:lintDebug"],
    "check": [":app:testDebugUnitTest", ":app:lintDebug", ":app:assembleDebug"],
    "install": [":app:installDebug"],
}


def gradle_launcher() -> Path:
    return ROOT / ("gradlew.bat" if os.name == "nt" else "gradlew")


def java_executable(java_home: Path) -> Path:
    return java_home / "bin" / ("java.exe" if os.name == "nt" else "java")


def java_major(java_home: Path) -> int | None:
    executable = java_executable(java_home)
    if not executable.is_file():
        return None
    result = subprocess.run(
        [str(executable), "-version"],
        check=False,
        capture_output=True,
        text=True,
    )
    version_output = f"{result.stdout}\n{result.stderr}"
    match = re.search(r'version "([0-9]+)(?:\.([0-9]+))?', version_output)
    if not match:
        return None
    major = int(match.group(1))
    return int(match.group(2)) if major == 1 and match.group(2) else major


def java_home_candidates() -> list[Path]:
    candidates: list[Path] = []

    configured = os.environ.get("JAVA_HOME")
    if configured:
        candidates.append(Path(configured))

    on_path = shutil.which("java.exe" if os.name == "nt" else "java")
    if on_path:
        candidates.append(Path(on_path).resolve().parent.parent)

    if os.name == "nt":
        for variable in ("ProgramFiles", "ProgramFiles(x86)", "LOCALAPPDATA"):
            parent = os.environ.get(variable)
            if parent:
                candidates.extend(Path(parent).glob("Java/*"))
                candidates.extend(Path(parent).glob("Eclipse Adoptium/*"))
    elif sys.platform == "darwin":
        candidates.extend(Path("/Library/Java/JavaVirtualMachines").glob("*/Contents/Home"))
        candidates.extend(Path("/usr/local/opt").glob("openjdk*/libexec/openjdk.jdk/Contents/Home"))
    else:
        for parent in (Path("/usr/lib/jvm"), Path("/usr/java"), Path("/opt/java")):
            if parent.is_dir():
                candidates.extend(path for path in parent.iterdir() if path.is_dir())

    unique: list[Path] = []
    seen: set[Path] = set()
    for candidate in candidates:
        resolved = candidate.expanduser().resolve()
        if resolved not in seen:
            seen.add(resolved)
            unique.append(resolved)
    return unique


def resolve_java_home() -> tuple[Path, int] | None:
    supported: list[tuple[Path, int]] = []
    for candidate in java_home_candidates():
        major = java_major(candidate)
        if major is not None and major >= MIN_JAVA_MAJOR:
            supported.append((candidate, major))

    if not supported:
        return None

    configured = os.environ.get("JAVA_HOME")
    if configured:
        configured_path = Path(configured).expanduser().resolve()
        for candidate, major in supported:
            if candidate == configured_path:
                return candidate, major

    return max(supported, key=lambda item: (item[1], str(item[0])))


def gradle_environment() -> dict[str, str] | None:
    resolved = resolve_java_home()
    if resolved is None:
        print(
            f"Gradle requires JDK {MIN_JAVA_MAJOR} or later. "
            "Install a compatible JDK or set JAVA_HOME.",
            file=sys.stderr,
        )
        return None

    java_home, major = resolved
    environment = os.environ.copy()
    environment["JAVA_HOME"] = str(java_home)
    environment["PATH"] = str(java_home / "bin") + os.pathsep + environment.get("PATH", "")
    print(f"Using JDK {major}: {java_home}", flush=True)
    if not environment.get("ANDROID_SDK_ROOT") and not environment.get("ANDROID_HOME"):
        local_sdk = ROOT / ".android-sdk"
        if local_sdk.is_dir():
            environment["ANDROID_SDK_ROOT"] = str(local_sdk)
            environment["ANDROID_HOME"] = str(local_sdk)
            print(f"Using Android SDK: {local_sdk}", flush=True)
    return environment


def run_gradle(tasks: list[str]) -> int:
    launcher = gradle_launcher()
    if not launcher.exists():
        print(f"Gradle launcher not found: {launcher}", file=sys.stderr)
        return 2
    environment = gradle_environment()
    if environment is None:
        return 2
    gradle_args = [str(launcher), "--no-daemon", *tasks]
    if os.name == "nt":
        command_line = subprocess.list2cmdline(gradle_args)
        command = ["cmd.exe", "/d", "/s", "/c", command_line]
    else:
        command = gradle_args
    return subprocess.run(command, cwd=ROOT, env=environment, check=False).returncode



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

    ui_root = ui_file.parent
    ui_combined = "\n".join(path.read_text(encoding="utf-8") for path in ui_root.rglob("*.kt"))
    if "com.oxygen.weather.data." in ui_combined:
        failures.append("Compose UI imports canonical data instead of presentation models")

    manifest = (ROOT / "app" / "src" / "main" / "AndroidManifest.xml").read_text(encoding="utf-8")
    build_file = (ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
    if "com.oxygen.weather" not in build_file or "Oxygen Weather" not in manifest:
        failures.append("Oxygen application identity is incomplete")

    if failures:
        print("Source-contract check failed:", file=sys.stderr)
        for failure in failures:
            print(f"  - {failure}", file=sys.stderr)
        return 1
    print("Source-contract check passed: new Oxygen UI only, one outer pager, presentation-only Compose boundary, Oxygen app identity present.")
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
    for key in ("ANDROID_SDK_ROOT", "ANDROID_HOME"):
        root = os.environ.get(key)
        if root:
            candidate = Path(root) / "platform-tools" / binary
            if candidate.exists():
                return str(candidate)
    local_sdk = ROOT / ".android-sdk"
    local_candidate = local_sdk / "platform-tools" / binary
    if local_candidate.exists():
        return str(local_candidate)
    direct = shutil.which(binary)
    if direct:
        return direct
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
