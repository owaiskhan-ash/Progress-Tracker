## Project Tracker 1.1.0

A minimal project tracker with an acid-lime interface and a transparent Android launcher widget.

### Included
- Three-screen flow: project list, project naming, and a project progress ring.
- Add, edit, and delete milestone percentages and notes; ring markers and progress recalculate from the milestone list.
- Rename or delete projects, add an optional deadline, undo the last progress change, and review history.
- Transparent Android home-screen widgets. Configure each instance with its own project; widgets refresh when project data changes.
- Local Android milestone reminders with notification permission support and reboot restoration. Android may delay delivery to conserve battery; reminders are not an alarm-clock replacement.
- Project data stored locally on the device.

### Install
Download `Project-Tracker.apk`, transfer it to an Android device, and allow installation from that source when prompted. If no private signing key is configured in the repository Actions secrets, this release is a debug-signed APK. A private-key-signed build can be enabled by following the signing instructions in the README and adding the four Android signing secrets.
