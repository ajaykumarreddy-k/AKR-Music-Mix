# AKR - Pixelplayer 🎵

<p align="center">
  <img src="assets/icon.png" alt="App Icon" width="128"/>
</p>

<p align="center">
  <strong>A beautiful, feature-rich music player for Android and Wear OS</strong><br>
  Built with Jetpack Compose, Material Design 3, and advanced streaming integrations.
</p>

<p align="center">
    <a href="https://github.com/ajaykumarreddy-k/AKR-Music-Mix/releases/latest">
        <img src="https://img.shields.io/github/v/release/ajaykumarreddy-k/AKR-Music-Mix?include_prereleases&logo=github&style=for-the-badge&label=Latest%20Release" alt="Latest Release">
    </a>
    <a href="https://github.com/ajaykumarreddy-k/AKR-Music-Mix/releases">
        <img src="https://img.shields.io/github/downloads/ajaykumarreddy-k/AKR-Music-Mix/total?logo=github&style=for-the-badge" alt="Total Downloads">
    </a>
    <img src="https://img.shields.io/badge/Android-11%2B-green?style=for-the-badge&logo=android" alt="Android 11+">
    <img src="https://img.shields.io/badge/Kotlin-100%25-purple?style=for-the-badge&logo=kotlin" alt="Kotlin">
</p>

---

## ‼️ DISCLAIMER
- No fork of this project will receive support. If you use a fork, ask the forker to support you.

---

## 📸 Screenshots

### Mobile App
<p align="center">
  <img src="../result/screenshot1.jpg" width="30%" />
  <img src="../result/screenshot2.jpg" width="30%" />
  <img src="../result/screenshot3.jpg" width="30%" />
</p>

### Wear OS & Output Settings
<p align="center">
  <img src="../result/WhatsApp%20Image%202026-06-08%20at%2018.50.46.jpeg" width="22%" />
  <img src="../result/WhatsApp%20Image%202026-06-08%20at%2018.50.46%20(1).jpeg" width="22%" />
  <img src="../result/WhatsApp%20Image%202026-06-08%20at%2018.50.46%20(2).jpeg" width="22%" />
  <img src="../result/WhatsApp%20Image%202026-06-08%20at%2018.50.45.jpeg" width="22%" />
</p>

---

## ✨ Features

### 🎨 Modern UI/UX
- **Material You** - Dynamic color theming that adapts to your wallpaper.
- **Smooth Animations** - Fluid transitions and micro-interactions.
- **Customizable UI** - Adjustable corner radius for the navigation bar and layout properties.
- **Dark/Light Theme** - Automatic or manual theme switching.
- **Album Art Colors** - Dynamic color extraction from album artwork.

### 🎵 Powerful Playback
- **Media3 ExoPlayer** - Industry-leading audio engine with FFmpeg support.
- **Background Playback** - Full media session integration.
- **Queue Management** - Drag-and-drop reordering.
- **Shuffle & Repeat** - All playback modes supported.
- **Gapless Playback** - Seamless transitions between tracks.
- **Custom Transitions** - Configure crossfades between songs.
- **Equalizer** - High-fidelity built-in equalizer for audio fine-tuning.

### 📚 Library Management
- **Multi-format Support** - MP3, FLAC, AAC, OGG, WAV, and more.
- **Browse By** - Songs, Albums, Artists, Genres, Folders.
- **Smart Artist Parsing** - Configurable delimiters for multi-artist tracks.
- **Album Artist Grouping** - Proper album organization.
- **Folder Filtering** - Choose which directories to scan.

### 🔗 Linked Cloud Accounts & Services
- **Telegram** - Stream and import audio files directly from your Telegram chats and channels (powered by `TDLib`).
- **NetEase Cloud Music** - Sync your NetEase library and playlists.
- **QQ Music** - Connect your QQ Music account to access your streaming library.
- **Navidrome / Subsonic** - Link your self-hosted Subsonic-compatible server to stream music on the go.
- **Jellyfin** - Connect your Jellyfin media server to stream your tracks.
- **Google Drive** - Direct cloud integration for library syncing and streaming (coming soon).

### ⌚ Wear OS Companion App
- **Remote Controller** - Adjust playback, volume, and view track details on your wrist.
- **Library Explorer** - Browse playlists, albums, and artists directly from the watch.
- **Offline Mode** - Download and store tracks locally on your Wear OS device for phone-free workouts.
- **Lyrics Screen** - Follow synchronized lyrics on your watch.
- **Playback Transfer** - Seamlessly transfer music playback between your phone and your watch.
- **Output Screen** - Select audio playback outputs dynamically on the watch (Phone, Watch Speaker, Bluetooth headphones).

### 🤖 AI-Powered Playlists & Translations
- **AI Playlist Generator** - Generate custom playlists from natural language prompts (supports Gemini, Deepseek, OpenAI, etc.).
- **Daily Mix** - AI-powered personalized playlist based on listening habits.
- **AI Lyrics Translation** - Automatically translate synchronized lyrics using AI models.

### ⚙️ Advanced Features
- **Tag Editor** - Edit metadata with TagLib & JAudioTagger (MP3, FLAC, M4A support).
- **Synchronized Lyrics** - LRC format via LRCLIB API.
- **Glance Widgets** - Home screen control widgets in multiple layouts (4x1, 4x2, 2x2).
- **Chromecast** - Stream your playback to TV or smart speakers via Ktor-powered media casting.
- **Device Capabilities & Performance Report** - A diagnostic result section showing real-time audio output routing (active speaker, headset, Bluetooth), playback path parameters (PCM float, low latency), format support analysis, and local storage footprints, alongside a user-exportable diagnostic performance report.

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | [Kotlin](https://kotlinlang.org/) 100% |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| **Design System** | [Material Design 3](https://m3.material.io/) |
| **Audio Engine** | [Media3 ExoPlayer](https://developer.android.com/guide/topics/media/media3) + FFmpeg |
| **Architecture** | MVVM with StateFlow/SharedFlow |
| **DI** | [Hilt](https://dagger.dev/hilt/) |
| **Database** | [Room](https://developer.android.com/training/data-storage/room) |
| **Networking** | [Retrofit](https://square.github.io/retrofit/) + OkHttp |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) |
| **Async** | Kotlin Coroutines & Flow |
| **Background Tasks**| WorkManager |
| **Metadata** | [TagLib](https://github.com/nicholaus/taglib-android) + JAudioTagger |
| **Widgets** | [Glance](https://developer.android.com/jetpack/compose/glance) |
| **Casting/Server** | [Ktor Server](https://ktor.io/) (CIO) |
| **Cloud/Chat API** | [TDLib](https://github.com/tdlib/td) (Telegram Database Library) |
| **AI Integrations** | [Google Generative AI SDK](https://github.com/google/generative-ai-android) (Gemini) |

---

## 📱 Requirements

- **Android 11** (API 30) or higher
- **6GB RAM** recommended for smooth performance

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug | 2024.2.1 or newer
- Android SDK 30+ (Compile SDK 37)
- JDK 21+

### Installation

1. **Clone the repository**
   ```sh
   git clone https://github.com/ajaykumarreddy-k/AKR-Music-Mix.git
   ```

2. **Configure Local Properties**
   To enable Telegram music synchronization, create a `local.properties` file in the root directory and add your API credentials:
   ```properties
   TELEGRAM_API_ID=your_api_id
   TELEGRAM_API_HASH=your_api_hash
   ```

3. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory `AKR-Music/Core/Akr-final app`

4. **Sync and Build**
   - Wait for Gradle to sync dependencies.
   - Build the project (`Build` → `Make Project`).

5. **Run**
   - Connect a device or start an emulator.
   - Click Run (▶️).

---

## 📂 Project Structure

```
Akr-final app/
├── app/src/main/java/com/akr/finalapp/
│   ├── data/
│   │   ├── database/       # Room entities, DAOs, migrations
│   │   ├── model/          # Domain models (Song, Album, Artist, etc.)
│   │   ├── network/        # API services (LRCLIB, Deezer, Cloud integrations)
│   │   ├── preferences/    # DataStore preferences
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # MusicService, HTTP server for Casting, Wear integration
│   │   └── worker/         # WorkManager sync workers
│   ├── di/                 # Hilt dependency injection modules
│   ├── presentation/
│   │   ├── components/     # Reusable Compose components
│   │   ├── navigation/     # Navigation graph
│   │   ├── screens/        # Screen composables (About, Accounts, Player, etc.)
│   │   ├── viewmodel/      # ViewModels
│   │   └── (service folders)# Auth and dashboards for NetEase, QQ Music, Telegram, Navidrome, Jellyfin
│   ├── ui/
│   │   ├── glancewidget/   # Home screen Glance widgets
│   │   └── theme/          # Colors, typography, custom shapes, and themes
│   └── utils/              # Extensions and utilities
├── shared/                 # Wear OS data transfer objects (DTOs) & protocols
├── wear/                   # Wear OS companion application codebase
└── innertube/              # Scraper client for YouTube Music search and streaming
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under a Proprietary License - see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/ajaykumarreddy-k">ajaykumarreddy-k</a>
</p>
