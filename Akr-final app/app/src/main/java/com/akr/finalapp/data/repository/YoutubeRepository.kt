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
            android.util.Log.d("AKR_MUSIC", "🔍 resolveStreamUrl START: videoId=$videoId")

            // Strategy 1: NewPipe direct page scrape — no API auth/PoToken needed.
            // Uses NewPipe's own JS player extraction pipeline (same as NewPipe app).
            val newPipeStreams = NewPipeExtractor.newPipePlayer(videoId)
            android.util.Log.d("AKR_MUSIC", "📡 NewPipe streams count=${newPipeStreams.size}")

            if (newPipeStreams.isNotEmpty()) {
                // Audio-only itags: 139(48k AAC), 140(128k AAC), 141(256k AAC),
                //                   249(low WebM), 250(med WebM), 251(high WebM/Opus)
                val audioItagPreference = listOf(141, 251, 140, 250, 139, 249)
                val url = audioItagPreference
                    .firstNotNullOfOrNull { itag -> newPipeStreams.find { it.first == itag }?.second }
                    ?: newPipeStreams.firstOrNull()?.second

                if (url != null) {
                    android.util.Log.d("AKR_MUSIC", "✅ Strategy 1 (NewPipe) succeeded: ${url.take(80)}...")
                    return@runCatching url
                }
            }

            // Strategy 2: WEB_REMIX + NewPipe cipher deobfuscation
            android.util.Log.d("AKR_MUSIC", "🔄 Falling back to Strategy 2 (WEB_REMIX + cipher)")
            val webResponse = YouTube.player(videoId).getOrThrow()
            android.util.Log.d("AKR_MUSIC", "📡 WEB_REMIX status=${webResponse.playabilityStatus?.status} audioFormats=${webResponse.streamingData?.adaptiveFormats?.count { it.mimeType.startsWith("audio/") }}")

            val bestCipherFormat = webResponse.streamingData?.adaptiveFormats
                ?.filter { it.mimeType.startsWith("audio/") }
                ?.maxByOrNull { it.bitrate }

            if (bestCipherFormat != null) {
                // Try plain URL first
                bestCipherFormat.url?.let { return@runCatching it }
                // Try cipher deobfuscation
                NewPipeExtractor.getStreamUrl(bestCipherFormat, videoId)?.let { return@runCatching it }
            }

            throw Exception("All stream resolution strategies failed for videoId=$videoId")
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
