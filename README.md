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
- Offline app shell via service worker. Project information remains on-device.

The browser preview persists reminders but does not schedule browser notifications. Native Android reminder delivery is enabled in the Capacitor build. The Android launcher widget from the original project brief is not implemented in this version; this repo provides the three-screen tracker app and notification-enabled APK source.
