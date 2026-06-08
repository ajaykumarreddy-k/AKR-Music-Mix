# AKR - PixelPlayer 🎵

<p align="center">
  <img src="Music%20Logo%20.png" alt="AKR - PixelPlayer Logo" width="160"/>
</p>

Welcome to the **AKR - PixelPlayer** monorepo, the central hub for the development of a state-of-the-art music streaming and playback ecosystem for Android and Wear OS. 

This repository represents the unification of two powerful music player codebases, combining local high-resolution playback capabilities with vast cloud streaming services and advanced Wear OS integration.

---

## 📸 Screenshots

### Mobile App
<p align="center">
  <img src="result/screenshot1.jpg" width="45%" />
  <img src="result/screenshot3.jpg" width="45%" />
</p>

### Wear OS & Output Settings
<p align="center">
  <img src="result/WhatsApp%20Image%202026-06-08%20at%2018.50.46.jpeg" width="22%" />
  <img src="result/WhatsApp%20Image%202026-06-08%20at%2018.50.46%20(1).jpeg" width="22%" />
  <img src="result/WhatsApp%20Image%202026-06-08%20at%2018.50.46%20(2).jpeg" width="22%" />
  <img src="result/WhatsApp%20Image%202026-06-08%20at%2018.50.45.jpeg" width="22%" />
</p>

---

## 📂 Repository Structure

The core folder is organized into the following components:

### 1. 🚀 [Akr-final app](file:///home/prince/ProjectsMain/AKR-Music/Core/Akr-final%20app) (Active Workspace)
This is the main, active project directory containing the unified application **AKR - Pixelplayer**. It is the combination of the local engine of PixelPlayer with the streaming and sync features inspired by Echo-Music.
- **`:app`** - The primary Android application module featuring Jetpack Compose, Material You design, metadata tag editing, cloud logins (Telegram, NetEase, QQ Music, Navidrome, Jellyfin), and a comprehensive Device Capabilities diagnostics section detailing active output routing (speakers, headphones, Bluetooth) and audio latency paths.
- **`:wear`** - A companion Wear OS client supporting offline downloads, synchronized lyrics, remote control, playback transfer, and a dedicated **Output Screen** to dynamically switch routing between the phone, watch speaker, and Bluetooth headphones.
- **`:shared`** - Data transfer protocols sharing playback status, volume controls, and file transfer progress between the phone and watch.
- **`:innertube`** - YouTube and YouTube Music integration layer for online search and streaming.

### 2. 🗄️ Historical Reference Codebases
These folders serve as historical references containing the original source codebases that were merged to form the final application:
- **[Echo-Music-main](file:///home/prince/ProjectsMain/AKR-Music/Core/Echo-Music-main)** (and associated `.zip` archive) - The source repository for the streaming client features, lyrics integrations, and scraper modules.
- **[PixelPlayer-master](file:///home/prince/ProjectsMain/AKR-Music/Core/PixelPlayer-master)** (and associated `.zip` archive) - The source repository for the Material 3 UI/UX design tokens, custom widgets, local metadata parser, and library database systems.

---

## 🛠️ Development & Compilation Workflow

To build and run the main application:

1. **Open the Project:**
   Launch **Android Studio** (Ladybug 2024.2.1 or newer recommended) and select **Open**. Navigate to and select the inner **`Akr-final app`** directory.

2. **Configure API Credentials:**
   Create or modify `local.properties` inside the `Akr-final app/` directory to configure credentials for the Telegram integration (TDLib):
   ```properties
   TELEGRAM_API_ID=your_api_id
   TELEGRAM_API_HASH=your_api_hash
   ```

3. **Build Requirements:**
   - **JDK:** Ensure your Gradle settings are using **JDK 21**.
   - **Android SDK:** Compile SDK is configured for API 37, requiring SDK 30+ tools.

4. **Sync and Run:**
   Allow Android Studio to sync the Gradle build files. Once synced, you can deploy the `:app` module to your Android phone, or the `:wear` module to your Wear OS watch.

---

## 📜 Credits & Acknowledgments

This project is a modified and unified version of two exceptional open-source music applications. Special thanks and credits go to:
- **[PixelPlayer](https://github.com/theovilardo/PixelPlayer)** by [theovilardo](https://github.com/theovilardo) – for the beautiful Material 3 UI/UX foundation and local playback engine.
- **[Echo-Music](https://github.com/EchoMusicApp)** by [EchoMusicApp](https://github.com/EchoMusicApp) – for the advanced streaming integrations, scraper modules, and lyrics syncing.

---

## 🤝 Reference Links
- Read the detailed app features and technical stack in the [App README](file:///home/prince/ProjectsMain/AKR-Music/Core/Akr-final%20app/README.md).
- Understand the diagnostic reports in the [Performance Report](file:///home/prince/ProjectsMain/AKR-Music/Core/Akr-final%20app/docs/performance-report.md).
