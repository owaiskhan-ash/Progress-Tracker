# Project Tracker

A minimal Android project tracker with a high-contrast acid-lime and ink interface. The app has three screens: project list, project naming, and the progress ring with milestone markers. All project data is stored locally in the app WebView (`localStorage`); it is never uploaded.

## Open the preview

The Vite preview runs at port 5173 with live reload: edits to the screen JavaScript, styles, and HTML appear automatically in the viewport without manual refresh. Start it with:

```sh
npm run start
```

The preview disables the offline service worker to keep its live updates immediate. The installed app still uses the service worker for its offline shell.

## Build an Android APK

An Android Capacitor project is included under `android/`. Build it on a machine with Node.js, Java 21 and Android SDK/build tools installed:

```sh
npm install
npm run android:sync
cd android
./gradlew assembleDebug
```

The local debug APK is generated at `android/app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions also builds and verifies APKs on the hosted Android toolchain; tagged release commits publish the APK to GitHub.

## Features

- Create, rename and delete projects; optionally add or clear a target date.
- Add, edit and delete milestone percentages and notes. Each milestone is shown as a dot around the ring; progress follows the highest remaining marker.
- Set and clear milestone reminders. The Android build uses local Android notifications; it asks for notification permission where required, and the Capacitor notification receiver restores scheduled notifications after reboot. Android may delay delivery to conserve battery. These reminders are not a substitute for an alarm clock.
- Undo the last progress change and review a project’s progress history.
- Transparent Android home-screen widget. Add multiple instances independently, choose a project for each one, and tap a widget to open its project. Widget data refreshes when project data changes.
- Offline app shell via service worker. Project information remains on-device.

The browser preview persists reminders but does not schedule browser notifications or provide Android widgets. Native reminders and the transparent widget are available in the Android build.

## Sign your own release APK

The GitHub APK release is signed with Android's shared debug key by default, suitable for trying the app. The CI workflow can instead use your private release key when all four signing secrets are configured. For your own distributable build, create a private release key and keep it backed up. **Never send the key or passwords in chat, commit them, or share them publicly.** Losing the key can prevent updates to apps installed with builds signed by it.

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

### Use the key in GitHub Actions

In the repository, open **Settings → Secrets and variables → Actions** and add these repository secrets. For the keystore secret, base64-encode your `.jks` file on your own machine (Linux: `base64 -w 0 ~/.android/project-tracker-release.jks`; macOS: `base64 -i ~/.android/project-tracker-release.jks | tr -d '\n'`) and paste the output directly into GitHub's secret form—never into chat or a commit:

- `ANDROID_KEYSTORE_BASE64` — base64 contents of the `.jks` file
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS` — for the command above, `project-tracker`
- `ANDROID_KEY_PASSWORD`

With all four secrets present, the workflow builds a release-signed APK. After adding them, run **Actions → Build Android release APK → Run workflow** to rebuild and update the current release asset. If the secrets are absent, CI builds a debug-signed APK instead.

Android updates must be signed with the same key as the installed version. Because v1.0.0 uses the debug key, uninstall that version before installing a release-key-signed APK. If publishing through Google Play, use Play App Signing and keep the upload key backed up.
