# AniDit

Fresh rebuild (see ARCHITECTURE.md for exactly what's real vs. placeholder).

## Termux setup (from a completely clean state)

```bash
pkg update && pkg upgrade
pkg install git gh unzip
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

## Get the project onto your phone and into GitHub

1. Download `anidit-scaffold.zip` from the chat and let it save to your
   phone's Downloads folder.
2. In Termux:
```bash
termux-setup-storage   # one-time, allow the permission prompt
cd ~
cp ~/storage/downloads/anidit-scaffold.zip .
unzip -o anidit-scaffold.zip
cd anidit
```
3. Confirm the real code is there before doing anything else:
```bash
ls app/src/main/java/com/anidit/app/ui/screens/ImportMediaScreen.kt
```
4. Push to GitHub (this also auto-triggers the APK build via the included
   GitHub Actions workflow):
```bash
git init
git add .
git commit -m "AniDit initial build"
gh auth login
gh auth setup-git
gh repo create anidit --public --source=. --push
gh run watch
```
5. Once the run shows all green checkmarks:
```bash
gh run download --name app-debug-apk
find ~/anidit -iname "*.apk"
```
6. **Uninstall any previous version first** (debug builds are signed with a
   fresh key each CI run, so installing over a different signature silently
   fails):
```bash
adb uninstall com.anidit.app
```
(or long-press the app icon on your phone and uninstall manually if `adb`
can't see the device)

7. Copy the new APK somewhere your file manager can reach and install it:
```bash
cp ~/anidit/app-debug.apk ~/storage/downloads/anidit-debug.apk
```
Open Downloads in your file manager, tap `anidit-debug.apk`, install.

## What to check first after installing

Open the app, tap **New Project**, and on the Import screen tap **Anime
Clips** — your phone's system file/media picker should open immediately.
If it doesn't, that's the one thing to report back with a screenshot.
