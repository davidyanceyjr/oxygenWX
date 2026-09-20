#!/bin/sh
set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_URL="https://services.gradle.org/distributions/gradle-9.6.0-wrapper.jar"
WRAPPER_SHA256="497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7"

sha256_file() {
    if command -v sha256sum >/dev/null 2>&1; then
        sha256sum "$1" | awk '{print $1}'
    elif command -v shasum >/dev/null 2>&1; then
        shasum -a 256 "$1" | awk '{print $1}'
    else
        echo "Oxygen Weather: sha256sum or shasum is required to verify the Gradle Wrapper JAR." >&2
        exit 1
    fi
}

bootstrap_wrapper() {
    mkdir -p "$(dirname "$WRAPPER_JAR")"
    tmp="$WRAPPER_JAR.tmp.$$"
    trap 'rm -f "$tmp"' EXIT HUP INT TERM

    echo "Oxygen Weather: bootstrapping Gradle Wrapper 9.6.0..." >&2
    if command -v curl >/dev/null 2>&1; then
        curl --fail --location --silent --show-error "$WRAPPER_URL" --output "$tmp"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$WRAPPER_URL" -O "$tmp"
    else
        echo "Oxygen Weather: curl or wget is required for the first Gradle invocation." >&2
        exit 1
    fi

    actual=$(sha256_file "$tmp")
    if [ "$actual" != "$WRAPPER_SHA256" ]; then
        echo "Oxygen Weather: Gradle Wrapper JAR checksum mismatch." >&2
        echo "Expected: $WRAPPER_SHA256" >&2
        echo "Actual:   $actual" >&2
        exit 1
    fi

    mv "$tmp" "$WRAPPER_JAR"
    trap - EXIT HUP INT TERM
}

if [ ! -f "$WRAPPER_JAR" ]; then
    bootstrap_wrapper
else
    actual=$(sha256_file "$WRAPPER_JAR")
    if [ "$actual" != "$WRAPPER_SHA256" ]; then
        echo "Oxygen Weather: existing Gradle Wrapper JAR failed checksum verification." >&2
        exit 1
    fi
fi

if [ -n "${JAVA_HOME:-}" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

if ! command -v "$JAVACMD" >/dev/null 2>&1 && [ ! -x "$JAVACMD" ]; then
    echo "Oxygen Weather: Java was not found. Install JDK 17 or set JAVA_HOME." >&2
    exit 1
fi

exec "$JAVACMD" -Dorg.gradle.appname=gradlew -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
