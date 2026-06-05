package com.akr.finalapp.data.repository

import com.akr.finalapp.data.model.Song
import com.music.innertube.YouTube
import com.music.innertube.NewPipeExtractor
import com.music.innertube.models.SongItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class YoutubeRepository @Inject constructor() {

    suspend fun searchSongs(query: String): Result<List<Song>> = withContext(Dispatchers.IO) {
        runCatching {
            YouTube.search(query, YouTube.SearchFilter.FILTER_SONG)
                .getOrThrow().items
                .filterIsInstance<SongItem>()
                .map { it.toSong() }
        }
    }

    suspend fun getPlaylist(playlistId: String): Result<Pair<String, List<Song>>> = withContext(Dispatchers.IO) {
        runCatching {
            val page = YouTube.playlist(playlistId).getOrThrow()
            val title = page.playlist.title
            val songs = page.songs.filterIsInstance<SongItem>().map { it.toSong() }
            Pair(title, songs)
        }
    }

    suspend fun resolveStreamUrl(videoId: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            // Strategy 1: ANDROID_VR client — returns plain, non-ciphered URLs directly.
            // No JS deobfuscation needed. Specifically avoids AV1 codec (good for audio-only).
            val androidResponse = YouTube.player(
                videoId = videoId,
                client = com.music.innertube.models.YouTubeClient.ANDROID_VR_NO_AUTH
            ).getOrNull()

            val plainUrl = androidResponse?.streamingData?.adaptiveFormats
                ?.filter { it.mimeType.startsWith("audio/") && it.url != null }
                ?.maxByOrNull { it.bitrate }
                ?.url

            if (plainUrl != null) return@runCatching plainUrl

            // Strategy 2: ANDROID_VR older client (1.43.32) — also returns plain URLs
            val vrOldResponse = YouTube.player(
                videoId = videoId,
                client = com.music.innertube.models.YouTubeClient.ANDROID_VR_1_43_32
            ).getOrNull()

            val vrPlainUrl = vrOldResponse?.streamingData?.adaptiveFormats
                ?.filter { it.mimeType.startsWith("audio/") && it.url != null }
                ?.maxByOrNull { it.bitrate }
                ?.url

            if (vrPlainUrl != null) return@runCatching vrPlainUrl

            // Strategy 3: WEB_REMIX response + NewPipe cipher deobfuscation as last resort
            val webResponse = YouTube.player(videoId).getOrThrow()

            val bestCipherFormat = webResponse.streamingData?.adaptiveFormats
                ?.filter { it.mimeType.startsWith("audio/") }
                ?.maxByOrNull { it.bitrate }
                ?: throw Exception("No audio stream found for videoId=$videoId")

            NewPipeExtractor.getStreamUrl(bestCipherFormat, videoId)
                ?: throw Exception("All stream resolution strategies failed for videoId=$videoId")
        }
    }

    private fun SongItem.toSong() = Song(
        id = id,
        title = title,
        artist = artists.firstOrNull()?.name ?: "Unknown Artist",
        artistId = 0L,
        album = album?.name ?: "YouTube",
        albumId = 0L,
        path = "",
        contentUriString = "youtube://$id",
        albumArtUriString = thumbnail,
        duration = (duration ?: 0) * 1000L,
        mimeType = "audio/youtube",
        bitrate = null,
        sampleRate = null,
        dateModified = System.currentTimeMillis() / 1000L,
        dateAdded = System.currentTimeMillis() / 1000L
    )
}
