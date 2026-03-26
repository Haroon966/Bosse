# Bosse — project purpose and features

## Purpose

**Bosse** is a **TV-first Android** app that turns a folder of your own video files into a **private, offline-friendly “streaming” library** on the couch. You keep media on USB, SSD, HDD, SD cards, or other attached storage; the app **does not upload your library to the cloud**. Playback works **without internet**; network use is **optional** and only for enriching metadata when you choose to configure it.

**Problem it solves:** A directory of `.mkv` (and similar) files does not feel like a curated home theater experience. Bosse indexes that tree, remembers progress, and presents **movies**, **series**, and **continue watching** in an OTT-style interface built for the remote and the living room.

**Ideal setup:** Android TV (or similar TV form factor) plus local or attached storage for a **personal “OTT”** that competes on comfort, not on subscriptions.

---

## Product principles

| Principle | What it means |
| --- | --- |
| **Your storage** | You pick a **library root** via Android’s **Storage Access Framework (SAF)**; the app works from the URIs and permissions you grant. |
| **Offline-first playback** | Watching does not depend on the network. |
| **Optional cloud metadata** | **The Movie Database (TMDB)** is used only when you add an API key; it improves posters and text, not access to your files. |
| **Incremental library maintenance** | Scans can **skip unchanged files** using path, size, and `lastModified` so rescans stay practical as libraries grow. |

---

## Features

### Library and discovery

- **Choose library folder** — Pick one or more roots through the system folder picker; persist read access where the OS allows.
- **Background library scan** — **WorkManager** runs indexing without blocking the UI; Home can show **updating library** while a scan is in progress.
- **Refresh library** — Manually enqueue a full rescan from Settings (e.g. after adding, moving, or renaming files).
- **Forgiving folder layout** — Scanner and classifiers understand common patterns:
  - **Movies:** e.g. `Movies/Title (Year)/file.mkv` or videos directly under a movies area.
  - **Series:** show folders, **Season 01** / **Season 1** style season folders, and episode hints such as **`S01E01`** in file names.
- **Sidecar subtitles** — **`.srt`** files with the **same base name** as the video in the **same folder** are picked up for playback.

### Home experience (OTT-style shell)

- **Continue watching** — Surfaces in-progress **movies** and **series episodes** from stored watch progress.
- **Recently added** — Highlights a slice of newly indexed **movies** (when a library is configured).
- **Movies** and **Series** rows — Horizontal, remote-friendly **poster rows** (Jetpack Compose for TV) for browsing the full catalog.
- **Empty state guidance** — When no library is set, Home directs you to **pick a library folder** (Settings).

### Detail and playback

- **Movie detail** — Poster, metadata when available, **Play** / **Resume** based on saved position (with sensible thresholds near start and end).
- **Series detail** — Show poster, overview when available, and a **focusable list of episodes** with per-episode play and resume.
- **Player (Media3 ExoPlayer)** — Plays the video URI from your library; applies **resume position**; loads optional **SRT** sidecar as subtitles.
- **Next episode** — From the player, **CHANNEL_UP** / **MEDIA_NEXT** (and similar) can jump to the **next episode** in order when series context is known.
- **Watch progress** — **Room**-backed progress is **saved on stop** and when playback **ends**, so Home and detail screens stay in sync.

### Settings and optional enrichment

- **TMDB API key** — Optional field; saved locally. When non-empty, scans can **enrich** titles with **posters** and **overviews** from TMDB after indexing.
- **Back navigation** — Settings and detail flows return to the previous screen without losing app state.

### Platform and quality bar

- **Android TV–oriented** — Leanback/TV UX: D-pad focus, rows, and buttons suited to a remote.
- **Modern stack** — Kotlin, **minSdk 26**, target/compile aligned with the project (`README.md`), **Jetpack Compose for TV**, **Room**, **WorkManager**, **Coil** for images, **Retrofit** where TMDB is used.

---

## Non-goals and constraints (honest scope)

These are **not** promised as core product features; they depend on device or are outside current scope:

- **Codec and 4K support** vary by **SoC** and OEM; some devices struggle with high-bitrate **HEVC**.
- **Dolby / DTS passthrough** behavior varies; if audio fails on a given TV, trying stereo or **AAC** encodes is a practical workaround.
- **Internet** is not required for the app’s main job; it is only relevant for **optional TMDB** enrichment.

---

## Relationship to `README.md`

`README.md` is the **operator guide**: first run, folder layout tips, build commands, ADB sideload, and permissions. **This file** is the **product and intent** reference: why Bosse exists, what principles it follows, and **what features it is meant to provide** (as implemented or clearly aligned with the codebase and stated goals).

---

*Bosse — offline shelf, cinema energy.*
