# Project Tracker

A minimal Android project tracker with a high-contrast acid-lime and ink interface. The app has three screens: project list, project naming, and the progress ring with milestone markers. All project data is stored locally in the app WebView (`localStorage`); it is never uploaded.

## Open the preview

The workspace preview runs at port 4173:

```sh
npm run start
```

## Build an Android APK

An Android Capacitor project is included under `android/`. Build it on a machine with Node.js, Java 21 and Android SDK/build tools installed:

```sh
npm install
npm run android:sync
cd android
./gradlew assembleDebug
```

The debug APK is generated at `android/app/build/outputs/apk/debug/app-debug.apk`. The preview workspace has no Java runtime or Android SDK, so I could not compile and verify an APK here.

## Features

- Create, rename and delete projects; optionally add or clear a target date.
- Add, edit and delete milestone percentages and notes. Each milestone is shown as a dot around the ring; progress follows the highest remaining marker.
- Set and clear milestone reminders. The Android build uses local Android notifications; it asks for notification permission where required, and the Capacitor notification receiver restores scheduled notifications after reboot. Android may delay delivery to conserve battery. These reminders are not a substitute for an alarm clock.
- Undo the last progress change and review a project’s progress history.
- Transparent Android home-screen widget. Add multiple instances independently, choose a project for each one, and tap a widget to open its project. Widget data refreshes when project data changes.
- Offline app shell via service worker. Project information remains on-device.

The browser preview persists reminders but does not schedule browser notifications or provide Android widgets. Native reminders and the transparent widget are available in the Android build.

## Sign your own release APK

The APK on the v1.0.0 GitHub release is signed with Android's shared debug key, suitable for trying the app. For your own distributable build, create a private release key and keep it backed up. **Never send the key or passwords in chat, commit them, or share them publicly.** Losing the key can prevent updates to apps installed with builds signed by it.

1. Generate a key on your own computer. `keytool` will prompt for passwords; don't put them directly in shell history:

   ```sh
   keytool -genkeypair -v -keystore "$HOME/.android/project-tracker-release.jks" -keyalg RSA -keysize 4096 -validity 10000 -alias project-tracker
   ```

2. Copy `android/keystore.properties.example` to `android/keystore.properties` and fill in the absolute keystore path, alias, and passwords. That completed file and common keystore files are git-ignored.
3. Build the release APK:

   ```sh
   npm run android:sync
   cd android && ./gradlew assembleRelease
   ```

   The output is `android/app/build/outputs/apk/release/app-release.apk`. Check it with `apksigner verify --verbose --print-certs android/app/build/outputs/apk/release/app-release.apk`.

Android updates must be signed with the same key as the installed version. Because v1.0.0 uses the debug key, uninstall that version before installing a release-key-signed APK. If publishing through Google Play, use Play App Signing and keep the upload key backed up.
