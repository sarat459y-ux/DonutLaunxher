#!/usr/bin/env sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
cd "$ROOT"

if [ -z "${ANDROID_HOME:-}" ] && [ -z "${ANDROID_SDK_ROOT:-}" ]; then
  echo "Android SDK not found. Install Android Studio + Android SDK Platform 35 first."
  echo "Then set ANDROID_HOME (or ANDROID_SDK_ROOT) to your SDK folder."
  exit 2
fi

if command -v gradle >/dev/null 2>&1; then
  GRADLE_CMD="gradle"
else
  VERSION="8.9"
  CACHE="${HOME}/.cache/donutlauncher/gradle-${VERSION}"
  if [ ! -x "$CACHE/bin/gradle" ]; then
    mkdir -p "$(dirname "$CACHE")"
    TMP="$(mktemp -d)"
    trap 'rm -rf "$TMP"' EXIT INT TERM
    URL="https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip"
    if command -v curl >/dev/null 2>&1; then
      curl -fL "$URL" -o "$TMP/gradle.zip"
    elif command -v wget >/dev/null 2>&1; then
      wget -O "$TMP/gradle.zip" "$URL"
    else
      echo "Install curl or wget to download Gradle."; exit 2
    fi
    if ! command -v unzip >/dev/null 2>&1; then echo "Install unzip first."; exit 2; fi
    unzip -q "$TMP/gradle.zip" -d "$TMP"
    mkdir -p "$(dirname "$CACHE")"
    mv "$TMP/gradle-${VERSION}" "$CACHE"
  fi
  GRADLE_CMD="$CACHE/bin/gradle"
fi

"$GRADLE_CMD" --no-daemon assembleDebug
printf '\nAPK created at:\n%s\n' "$ROOT/app/build/outputs/apk/debug/app-debug.apk"
