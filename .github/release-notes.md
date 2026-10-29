## Project Tracker 1.1.9

- Disabled Android cloud backup and device-to-device transfer for app data, keeping project information on the device.
- Removed milestone note text from the Android widget. Widgets show the project name and progress ring/percentage only.
- Milestones appear as a vertical list below the target-date stats; new entries append at the bottom, and tapping or long-pressing a row supports editing and deletion.
- New milestone percentages add to current progress as the next step, capped at 100%.
- Removed the simulated bottom gesture indicator while preserving bottom spacing.
- The app icon uses a fluorescent rising graph on a flat black background.

The release includes the independently configured transparent project widget and local reminders. Project data stays on-device.

Download `Project-Tracker.apk`. It uses your private release key if GitHub Actions signing secrets are configured; otherwise it is debug-signed.
