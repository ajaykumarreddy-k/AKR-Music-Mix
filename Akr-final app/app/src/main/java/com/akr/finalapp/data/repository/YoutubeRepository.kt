package com.akr.finalapp.data.repository

import com.akr.finalapp.data.model.Song
import com.music.innertube.YouTube
import com.music.innertube.NewPipeExtractor
import com.music.innertube.models.SongItem
import com.music.innertube.models.YouTubeClient
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
            val allSongs = page.songs.toMutableList()
            
            var continuationToken = page.songsContinuation ?: page.continuation
            var pageCount = 0
            val maxPages = 50 // Limit to avoid infinite loops, allows up to 5000 songs
            while (continuationToken != null && pageCount < maxPages) {
                android.util.Log.d("AKR_MUSIC", "Loading playlist page ${pageCount + 1} with token: $continuationToken")
                val continuationPageResult = YouTube.playlistContinuation(continuationToken)
                val continuationPage = continuationPageResult.getOrNull()
                if (continuationPage != null) {
                    allSongs.addAll(continuationPage.songs)
                    continuationToken = continuationPage.continuation
                    pageCount++
                } else {
                    android.util.Log.e("AKR_MUSIC", "Failed to fetch playlist continuation page ${pageCount + 1}", continuationPageResult.exceptionOrNull())
                    break
                }
            }
            
            val songs = allSongs.filterIsInstance<SongItem>().map { it.toSong() }
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

            // Strategy 2: Try various InnerTube clients + NewPipe cipher deobfuscation
            android.util.Log.d("AKR_MUSIC", "🔄 Falling back to Strategy 2 (Multi-client InnerTube + cipher)")
            val clientsToTry = listOf(
                YouTubeClient.WEB_REMIX,
                YouTubeClient.TVHTML5_SIMPLY_EMBEDDED_PLAYER,
                YouTubeClient.ANDROID_VR_1_43_32,
                YouTubeClient.IOS,
                YouTubeClient.ANDROID_NO_SDK
            )

            for (client in clientsToTry) {
                try {
                    android.util.Log.d("AKR_MUSIC", "🔄 Strategy 2: Trying client ${client.clientName} (${client.friendlyName ?: ""})")
                    val webResponse = YouTube.player(videoId, client = client).getOrThrow()
                    
                    val status = webResponse.playabilityStatus?.status
                    android.util.Log.d("AKR_MUSIC", "📡 Client ${client.clientName} status=$status")
                    
                    val formats = webResponse.streamingData?.adaptiveFormats ?: emptyList()
                    val audioFormats = formats.filter { it.mimeType.startsWith("audio/") }
                    android.util.Log.d("AKR_MUSIC", "📡 Client ${client.clientName} audio formats count=${audioFormats.size}")
                    
                    if (audioFormats.isNotEmpty()) {
                        val bestCipherFormat = audioFormats.maxByOrNull { it.bitrate }
                        if (bestCipherFormat != null) {
                            // Try plain URL first
                            bestCipherFormat.url?.let {
                                android.util.Log.d("AKR_MUSIC", "✅ Strategy 2 succeeded with client ${client.clientName} (plain URL)")
                                return@runCatching it
                            }
                            // Try cipher deobfuscation
                            val resolvedUrl = NewPipeExtractor.getStreamUrl(bestCipherFormat, videoId)
                            if (resolvedUrl != null) {
                                android.util.Log.d("AKR_MUSIC", "✅ Strategy 2 succeeded with client ${client.clientName} (deobfuscated cipher)")
                                return@runCatching resolvedUrl
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AKR_MUSIC", "❌ Strategy 2 client ${client.clientName} failed: ${e.message}")
                }
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
