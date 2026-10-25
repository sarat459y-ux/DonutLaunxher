# DonutLauncher

DonutLauncher is an unofficial Android companion launcher for **Minecraft Bedrock on Android**. It has a custom dark/green interface, a one-tap button to open the installed Minecraft app, saved performance profiles, copyable settings recommendations, and shortcuts to Android app/display settings.

> **Honest performance note:** Android does not let a normal third-party app reliably measure another app's live FPS or rewrite Minecraft's private graphics settings. DonutLauncher therefore does not fake a live FPS counter, claim to “unlock” FPS, edit game files, or promise a specific FPS increase. Profiles are recommendations you apply in Minecraft's own settings. Thermal limits, hardware, the world, and the game version still affect performance.

This is an independent helper project and is not affiliated with or endorsed by Mojang or Microsoft. It does not bundle Minecraft, game assets, accounts, or game data. Install and own Minecraft separately through an official source.

## Build the APK

### Android Studio (recommended)

1. Install Android Studio and the Android SDK Platform 35.
2. Open the `DonutLauncher` project folder in Android Studio.
3. Use JDK 17 for Gradle.
4. Let Android Studio sync the Gradle project.
5. Select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
6. The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Command line

Install JDK 17 and Android SDK Platform 35, set `ANDROID_HOME` or `ANDROID_SDK_ROOT`, then run:

```sh
./build_apk.sh
```

The script uses a local Gradle installation if available; otherwise it downloads Gradle 8.9. An Android SDK is still required.

### Build using GitHub Actions

1. Create a private or public GitHub repository and upload this project.
2. Open the repository's **Actions** tab.
3. Run **Build DonutLauncher APK** using **Run workflow**, or push to `main`/`master`.
4. Download the `DonutLauncher-debug-apk` artifact from that workflow run.

## Install

Transfer `app-debug.apk` to your Android phone, open it, and allow installation from that file source when Android asks. This is a debug-signed APK, suitable for testing. For public distribution, create a release build signed with your own private signing key.

## What's included

- Minecraft Bedrock launch shortcut (opens the official app already installed on the device)
- App-installed status check
- Akıcı FPS, Dengeli, and Görsel kalite profiles saved locally
- Copyable profile recommendations
- Shortcut to Minecraft's Android app details and display settings
- Responsive, no-network, no-account app UI
- GitHub Actions workflow to build a debug APK

## Project details

- Package: `com.donutlauncher.app`
- Minimum Android: 8.0 (API 26)
- Compile SDK: 35
- Version: 1.0.0
