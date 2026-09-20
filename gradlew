#!/bin/sh
set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_SHA256="cb0da6751c2b753a16ac168bb354870ebb1e162e9083f116729cec9c781156b8"

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

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Oxygen Weather: missing checked-in Gradle Wrapper JAR: $WRAPPER_JAR" >&2
    exit 1
fi

actual=$(sha256_file "$WRAPPER_JAR")
if [ "$actual" != "$WRAPPER_SHA256" ]; then
    echo "Oxygen Weather: existing Gradle Wrapper JAR failed checksum verification." >&2
    exit 1
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
