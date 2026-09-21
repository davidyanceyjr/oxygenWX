#!/usr/bin/env bash

# Start Oxygen Weather in a visible Android emulator window.
# Run this script from anywhere; it resolves the repository root itself.

set -euo pipefail

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd -P)
SDK_ROOT=${OXYGEN_SDK_ROOT:-${ANDROID_SDK_ROOT:-"$ROOT_DIR/.android-sdk"}}
AVD_HOME=${OXYGEN_AVD_HOME:-"$ROOT_DIR/.android/avd"}
AVD_NAME=${OXYGEN_AVD_NAME:-oxygen_starter}
PACKAGE_NAME=${OXYGEN_PACKAGE_NAME:-com.oxygen.weather}
ACTIVITY_NAME=${OXYGEN_ACTIVITY_NAME:-.MainActivity}
SERIAL=${OXYGEN_SERIAL:-}
CLEAN_INSTALL=0

usage() {
    cat <<'EOF'
Usage: scripts/run_visible_emulator.sh [options]

Starts or reuses a visible Android emulator window, builds the debug APK,
installs it, and launches Oxygen Weather.

Options:
  --avd NAME         AVD to start (default: oxygen_starter).
  --serial SERIAL    Use an already-running emulator, e.g. emulator-5554.
  --clean-install    Uninstall the app before installing it.
  -h, --help         Show this help.

Environment overrides:
  OXYGEN_SDK_ROOT, ANDROID_SDK_ROOT, OXYGEN_AVD_HOME, OXYGEN_AVD_NAME,
  OXYGEN_SERIAL, OXYGEN_PACKAGE_NAME, OXYGEN_ACTIVITY_NAME
EOF
}

while (($# > 0)); do
    case "$1" in
        --avd)
            AVD_NAME=${2:?--avd requires a value}
            shift
            ;;
        --serial)
            SERIAL=${2:?--serial requires a value}
            shift
            ;;
        --clean-install)
            CLEAN_INSTALL=1
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1" >&2
            usage >&2
            exit 2
            ;;
    esac
    shift
done

ADB="$SDK_ROOT/platform-tools/adb"
EMULATOR="$SDK_ROOT/emulator/emulator"
APK_PATH="$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk"

java_major() {
    local version
    [[ -x "$1/bin/java" ]] || return 1
    version=$("$1/bin/java" -version 2>&1 | awk -F '"' '/version/ { print $2; exit }')
    case "$version" in
        1.*) version=${version#1.} ;;
    esac
    version=${version%%.*}
    [[ "$version" =~ ^[0-9]+$ ]] || return 1
    printf '%s\n' "$version"
}

ensure_supported_jdk() {
    local major=0 candidate candidate_major
    if [[ -n "${JAVA_HOME:-}" ]]; then
        major=$(java_major "$JAVA_HOME" 2>/dev/null || printf '0')
    fi

    if ((major < 17)); then
        for candidate in \
            /usr/lib/jvm/java-17-openjdk \
            /usr/lib/jvm/java-21-openjdk \
            /usr/lib/jvm/java-26-openjdk \
            /usr/lib/jvm/java-27-openjdk; do
            candidate_major=$(java_major "$candidate" 2>/dev/null || true)
            if [[ "$candidate_major" =~ ^[0-9]+$ ]] && ((candidate_major >= 17)); then
                export JAVA_HOME="$candidate"
                major=$candidate_major
                break
            fi
        done
    fi

    if ((major < 17)); then
        echo "Gradle requires JDK 17 or later. Set JAVA_HOME to a compatible JDK and rerun." >&2
        exit 1
    fi
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Using JDK $major: $JAVA_HOME"
}

ensure_supported_jdk

if [[ ! -x "$ADB" ]]; then
    cat >&2 <<EOF
Android platform-tools (adb) were not found at:
  $ADB

Install them into this checkout's SDK, then rerun:
  yes | "$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --sdk_root="$SDK_ROOT" "platform-tools"
EOF
    exit 1
fi

if [[ ! -x "$EMULATOR" ]]; then
    echo "Android emulator was not found at: $EMULATOR" >&2
    exit 1
fi

find_running_emulator() {
    "$ADB" devices | awk '$1 ~ /^emulator-/ && $2 == "device" { print $1; exit }'
}

wait_for_boot() {
    local attempt boot_completed
    for attempt in $(seq 1 90); do
        if [[ "$($ADB -s "$SERIAL" get-state 2>/dev/null || true)" == "device" ]]; then
            boot_completed=$("$ADB" -s "$SERIAL" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
            if [[ "$boot_completed" == "1" ]]; then
                echo "Emulator ready: $SERIAL"
                return 0
            fi
        fi
        sleep 2
    done
    echo "Timed out waiting for $SERIAL to finish booting." >&2
    exit 1
}

"$ADB" start-server >/dev/null

if [[ -z "$SERIAL" ]]; then
    SERIAL=$(find_running_emulator || true)
fi

if [[ -z "$SERIAL" ]]; then
    if ! ANDROID_AVD_HOME="$AVD_HOME" "$EMULATOR" -list-avds | grep -Fxq "$AVD_NAME"; then
        echo "AVD '$AVD_NAME' was not found under $AVD_HOME." >&2
        echo "Available AVDs:" >&2
        ANDROID_AVD_HOME="$AVD_HOME" "$EMULATOR" -list-avds >&2 || true
        exit 1
    fi
    if [[ -z "${DISPLAY:-}" && -z "${WAYLAND_DISPLAY:-}" ]]; then
        echo "No graphical display is available, so a visible emulator window cannot be opened." >&2
        echo "Run this from a graphical desktop terminal (or connect an X/Wayland display)." >&2
        exit 1
    fi
    echo "Starting visible emulator window for AVD $AVD_NAME"
    ANDROID_SDK_ROOT="$SDK_ROOT" ANDROID_HOME="$SDK_ROOT" ANDROID_AVD_HOME="$AVD_HOME" \
        "$EMULATOR" -avd "$AVD_NAME" >/tmp/oxygenWX-emulator.log 2>&1 &
    emulator_pid=$!
    SERIAL=""
    for _ in $(seq 1 30); do
        SERIAL=$(find_running_emulator || true)
        [[ -n "$SERIAL" ]] && break
        if ! kill -0 "$emulator_pid" 2>/dev/null; then
            echo "The emulator exited before it connected to adb. See /tmp/oxygenWX-emulator.log." >&2
            exit 1
        fi
        sleep 1
    done
    if [[ -z "$SERIAL" ]]; then
        echo "The emulator did not connect to adb. See /tmp/oxygenWX-emulator.log." >&2
        exit 1
    fi
fi

wait_for_boot

echo "Building debug APK"
ANDROID_SDK_ROOT="$SDK_ROOT" ANDROID_HOME="$SDK_ROOT" \
    python "$ROOT_DIR/scripts/dev.py" build

if ((CLEAN_INSTALL == 1)); then
    echo "Removing existing $PACKAGE_NAME installation"
    "$ADB" -s "$SERIAL" uninstall "$PACKAGE_NAME" >/dev/null || true
fi

echo "Installing debug APK on $SERIAL"
"$ADB" -s "$SERIAL" install -r "$APK_PATH"

echo "Launching $PACKAGE_NAME/$ACTIVITY_NAME"
"$ADB" -s "$SERIAL" shell am force-stop "$PACKAGE_NAME"
"$ADB" -s "$SERIAL" shell am start -n "$PACKAGE_NAME/$ACTIVITY_NAME"
