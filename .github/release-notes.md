## Project Tracker 1.1.1

Small UI refinement to Project Tracker 1.1.0: removed the home-screen promotional headline and tagline for a cleaner start screen.

Includes the transparent per-project Android launcher widget, local milestone reminders, and the three-screen progress tracker. Project data stays on-device.

Download `Project-Tracker.apk` and install it on Android. If your release signing secrets are configured in GitHub Actions, this will be signed with your private release key; otherwise CI produces a debug-signed APK. Android may delay reminder delivery to conserve battery. The launcher widget is transparent and each instance is configured independently.
