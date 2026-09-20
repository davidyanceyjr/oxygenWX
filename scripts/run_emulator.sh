#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd -P)
SDK_ROOT=${OXYGEN_SDK_ROOT:-${ANDROID_SDK_ROOT:-/home/opsman/project_git/oxygen/.android-sdk}}
AVD_HOME=${OXYGEN_AVD_HOME:-/home/opsman/project_git/oxygen/.android/avd}
AVD_NAME=${OXYGEN_AVD_NAME:-oxygen_starter}
PACKAGE_NAME=${OXYGEN_PACKAGE_NAME:-com.oxygen.weather}
ACTIVITY_NAME=${OXYGEN_ACTIVITY_NAME:-.MainActivity}
SERIAL=${OXYGEN_SERIAL:-}
NO_BUILD=0
CLEAN_INSTALL=0

usage() {
    cat <<'EOF'
Usage: scripts/run_emulator.sh [options]

Starts or reuses the Oxygen emulator, builds the current debug APK, installs it,
and launches MainActivity.

Options:
  --no-build        Use the existing app/build/outputs/apk/debug/app-debug.apk.
  --clean-install   Uninstall the existing package if a normal install fails.
  --serial SERIAL   Use a specific adb serial, such as emulator-5554.
  -h, --help        Show this help.

Environment overrides:
  OXYGEN_SDK_ROOT, OXYGEN_AVD_HOME, OXYGEN_AVD_NAME,
  OXYGEN_GRADLE_BIN, OXYGEN_SERIAL
EOF
}

while (($# > 0)); do
    case "$1" in
        --no-build)
            NO_BUILD=1
            ;;
        --clean-install)
            CLEAN_INSTALL=1
            ;;
        --serial)
            if (($# < 2)); then
                echo "--serial requires a value" >&2
                exit 2
            fi
            SERIAL=$2
            shift
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

if [[ ! -x "$SDK_ROOT/platform-tools/adb" ]]; then
    echo "Android adb not found under $SDK_ROOT/platform-tools" >&2
    echo "Set OXYGEN_SDK_ROOT or ANDROID_SDK_ROOT to the Android SDK path." >&2
    exit 1
fi
if [[ ! -x "$SDK_ROOT/emulator/emulator" ]]; then
    echo "Android emulator not found under $SDK_ROOT/emulator" >&2
    exit 1
fi

ADB="$SDK_ROOT/platform-tools/adb"
EMULATOR="$SDK_ROOT/emulator/emulator"
APK_PATH="$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk"

if [[ -z "${JAVA_HOME:-}" ]]; then
    for candidate in /usr/lib/jvm/java-17-openjdk /usr/lib/jvm/java-26-openjdk; do
        if [[ -x "$candidate/bin/java" ]]; then
            export JAVA_HOME=$candidate
            break
        fi
    done
fi

resolve_gradle() {
    if [[ -n "${OXYGEN_GRADLE_BIN:-}" && -x "$OXYGEN_GRADLE_BIN" ]]; then
        printf '%s\n' "$OXYGEN_GRADLE_BIN"
        return
    fi
    if command -v gradle >/dev/null 2>&1; then
        command -v gradle
        return
    fi

    local sibling_cache="$ROOT_DIR/../oxygen/.gradle/wrapper/dists"
    if [[ -d "$sibling_cache" ]]; then
        local cached_gradle
        cached_gradle=$(find "$sibling_cache" -type f -path '*/bin/gradle' -perm -111 -print 2>/dev/null | sort | tail -n 1)
        if [[ -n "$cached_gradle" ]]; then
            printf '%s\n' "$cached_gradle"
            return
        fi
    fi

    echo "Gradle was not found. Set OXYGEN_GRADLE_BIN to a Gradle executable." >&2
    exit 1
}

find_online_serial() {
    "$ADB" devices | awk '$2 == "device" { print $1; exit }'
}

wait_for_device() {
    local expected_serial=$1
    local booted_serial=""
    local boot_state=""
    local attempt

    for attempt in $(seq 1 60); do
        if [[ -n "$expected_serial" ]]; then
            if [[ "$("$ADB" -s "$expected_serial" get-state 2>/dev/null || true)" != "device" ]]; then
                sleep 2
                continue
            fi
            booted_serial=$expected_serial
        else
            booted_serial=$(find_online_serial || true)
            if [[ -z "$booted_serial" ]]; then
                sleep 2
                continue
            fi
        fi

        boot_state=$("$ADB" -s "$booted_serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
        if [[ "$boot_state" == "1" ]]; then
            SERIAL=$booted_serial
            printf 'Emulator ready: %s\n' "$SERIAL"
            return 0
        fi
        sleep 2
    done

    echo "Timed out waiting for an Android emulator to finish booting." >&2
    "$ADB" devices >&2 || true
    exit 1
}

"$ADB" start-server >/dev/null

if [[ -z "$(find_online_serial || true)" ]]; then
    log_path=${OXYGEN_EMULATOR_LOG:-${TMPDIR:-/tmp}/oxygenWX-emulator.log}
    mkdir -p "$(dirname "$log_path")"
    echo "Starting AVD $AVD_NAME"
    env \
        ANDROID_SDK_ROOT="$SDK_ROOT" \
        ANDROID_HOME="$SDK_ROOT" \
        ANDROID_AVD_HOME="$AVD_HOME" \
        nohup "$EMULATOR" \
            -avd "$AVD_NAME" \
            -no-window \
            -no-audio \
            -no-boot-anim \
            -no-snapshot \
            -gpu swiftshader_indirect \
            >"$log_path" 2>&1 < /dev/null &
    echo "Emulator log: $log_path"
fi

wait_for_device "$SERIAL"

if ((NO_BUILD == 0)); then
    GRADLE_BIN=$(resolve_gradle)
    echo "Building debug APK with $GRADLE_BIN"
    env \
        ANDROID_SDK_ROOT="$SDK_ROOT" \
        ANDROID_HOME="$SDK_ROOT" \
        "$GRADLE_BIN" --no-daemon -p "$ROOT_DIR" :app:assembleDebug
fi

if [[ ! -f "$APK_PATH" ]]; then
    echo "Debug APK not found: $APK_PATH" >&2
    echo "Run without --no-build or build the debug APK first." >&2
    exit 1
fi

echo "Installing $APK_PATH on $SERIAL"
if ! "$ADB" -s "$SERIAL" install -r "$APK_PATH"; then
    if ((CLEAN_INSTALL == 0)); then
        echo "Install failed. If this is a signature mismatch, retry with --clean-install." >&2
        exit 1
    fi
    echo "Removing the existing $PACKAGE_NAME package before reinstalling."
    "$ADB" -s "$SERIAL" uninstall "$PACKAGE_NAME" >/dev/null || true
    "$ADB" -s "$SERIAL" install "$APK_PATH"
fi

echo "Launching $PACKAGE_NAME/$ACTIVITY_NAME"
"$ADB" -s "$SERIAL" shell am force-stop "$PACKAGE_NAME"
"$ADB" -s "$SERIAL" shell am start -n "$PACKAGE_NAME/$ACTIVITY_NAME"
