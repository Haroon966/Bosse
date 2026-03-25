<div align="center">

<img src="docs/readme-hero.svg" alt="Bosse — local OTT-style home theater for Android TV" width="920" />

<br/>

[![Android TV](https://img.shields.io/badge/Android%20TV-Leanback-E50914?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/tv)
[![Min SDK 26](https://img.shields.io/badge/minSdk-26-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#build-and-run)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge)](https://developer.android.com/jetpack/compose)

### Your couch. Your files. No subscription.

**Bosse** is a native **Android TV** app that gives you an **OTT-style library** from **local video** on USB, SSD, HDD, SD cards, or other storage — **playback works offline** once your media is on the device.

</div>

---

## At a glance

| | |
| :---: | :--- |
| 📺 | **TV-first UI** — Jetpack Compose for TV: posters, rows, remote-friendly focus. |
| 💾 | **Your storage** — Pick a library folder via **Storage Access Framework** (SAF). |
| 🔌 | **Offline watching** — No internet required to play; optional **TMDB** only enriches metadata. |
| 🎬 | **Serious playback** — **Media3 ExoPlayer**: resume, next episode, sidecar **`.srt`** subtitles. |

> **Tip:** The banner above uses a **gentle SVG animation** (gradient, shine, glow). If it looks static, your viewer may not run SMIL motion — the app on your TV is unchanged.

---

## Table of contents

- [Why Bosse](#why-bosse)
- [Stack and app identity](#stack-and-app-identity)
- [Super simple first run](#super-simple-first-run)
- [Recommended library layout](#recommended-library-layout)
- [Build and run](#build-and-run)
- [Sideload with ADB](#sideload-with-adb)
- [Permissions](#permissions)
- [Notes](#notes)
- [Moving the project folder](#moving-the-project-folder)

---

## Why Bosse

🎬 **Problem:** A folder full of `.mkv` files does not feel like Netflix on the sofa.

🍿 **What Bosse does:** You choose **one library root** on your drive. Bosse **indexes** it with **Room**, supports **incremental rescans** (skip unchanged files by path + size + `lastModified`), and surfaces **movies, series, and continue watching** like a streaming app — **without uploading your library to the cloud**.

🛋️ **Ideal setup:** **Android TV + attached storage** (USB, SSD, HDD, SD, etc.) for a **private, local “OTT”** experience.

---

## Stack and app identity

| | |
| --- | --- |
| 🧩 **Stack** | Kotlin, Jetpack Compose for TV, Room, WorkManager, Media3 ExoPlayer, optional Retrofit/TMDB |
| 🆔 **Application ID** | `dev.olufsen.bosse` — **keep stable** for updates |
| 📱 **SDKs** | **Min SDK:** 26 · **Target / compile:** 35 |

---

## Super simple first run

1. Open **Bosse** on the TV.  
2. Go to **Settings** → **Choose library folder** and pick the root on your drive (Android’s folder picker).  
3. Wait for the **background scan** — Home shows **Updating library…** while it runs.  
4. Browse and play. Use **Refresh library** in Settings after you add or rename files.  

✨ **TMDB (optional):** Add an API key under **TMDB API key** in Settings for posters and overviews. Get a key at [The Movie Database](https://www.themoviedb.org/settings/api). Metadata refresh runs after a scan when a key is set.

---

## Recommended library layout

The scanner is forgiving, but this layout works best:

| Type | Example paths |
| --- | --- |
| 🎥 **Movies** | `Movies/Some Title (2024)/file.mkv` or `Movies/file.mkv` |
| 📺 **Series** | `Show Name/Season 01/S01E01 Episode Title.mkv` (or `Season 1`, `S01E01` in the filename) |

Episode detection uses patterns such as **`S01E01`** in the file name and folders named **`Season 1`** / **`Season 01`**.

📝 **Sidecar subtitles:** place **`same-name.srt`** next to the video in the same folder.

---

## Build and run

1. Install [Android Studio](https://developer.android.com/studio) with **Android TV** system images (or use a TV device).  
2. Open this directory as the project root (`settings.gradle.kts` here).  
3. **Run** the `app` configuration on an **Android TV** emulator (D-pad) or hardware.  

```bash
# With JDK 17+ on PATH:
./gradlew assembleDebug
```

📦 **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk`

---

## Sideload with ADB

Works for **Android TV**, **Chromecast with Google TV**, and similar devices.

1. Enable **Developer options** and **USB debugging** (or **Wireless debugging**) on the device.  
2. Connect with `adb connect <ip>:5555` if using network ADB.  
3. Install:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

For release builds, sign an AAB/APK in Android Studio and install the same way.

---

## Permissions

| Permission | Why |
| --- | --- |
| 🌐 **Internet** | Only if you configure **TMDB**. |
| 📂 **READ_MEDIA_VIDEO** / legacy storage | Helpers where applicable; library access is mainly via **SAF** after you pick a folder. |

---

## Notes

- 🎞️ **Codecs and 4K** depend on your SoC; some TVs struggle with high-bitrate HEVC.  
- 🔊 **Dolby / DTS** passthrough varies by device; if audio fails, try stereo / AAC encodes.  

---

## Moving the project folder

You can rename this directory anytime. Keep **`applicationId`** and signing keys consistent so Android treats updates as the same app.

---

<div align="center">

**Bosse** — *offline shelf, cinema energy.* 📺✨

</div>
# Bosse
