package com.akr.finalapp.data.repository

import com.akr.finalapp.data.model.Song
import com.music.innertube.YouTube
import com.music.innertube.NewPipeExtractor
import com.music.innertube.models.SongItem
import com.music.innertube.models.Artist
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

    suspend fun resolveStreamUrl(
        videoId: String,
        songTitle: String? = null,
        songArtist: String? = null
    ): Result<String> = resolveStreamUrlInternal(videoId, songTitle, songArtist, isFallback = false)

    private suspend fun resolveStreamUrlInternal(
        videoId: String,
        songTitle: String?,
        songArtist: String?,
        isFallback: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            android.util.Log.d("AKR_MUSIC", "🔍 resolveStreamUrl START: videoId=$videoId, songTitle=$songTitle, songArtist=$songArtist, isFallback=$isFallback")

            var title = songTitle
            var author = songArtist

            // Validate if the videoId actually matches the requested songTitle
            var isVideoIdValid = true
            if (!isFallback && !title.isNullOrBlank()) {
                val mediaInfo = YouTube.getMediaInfo(videoId).getOrNull()
                if (mediaInfo != null) {
                    val videoTitle = mediaInfo.title
                    if (!videoTitle.isNullOrBlank()) {
                        val score = getTitleSimilarityScore(title, videoTitle)
                        android.util.Log.d("AKR_MUSIC", "🔍 Verifying videoId=$videoId title='${videoTitle}' against expected='${title}'. Score: $score")
                        if (score < 0.45) {
                            android.util.Log.w("AKR_MUSIC", "⚠️ Video ID title mismatch! Expected '$title', got '$videoTitle'. Rejecting original videoId.")
                            isVideoIdValid = false
                        }
                    }
                }
            }

            if (title.isNullOrBlank()) {
                try {
                    val mediaInfo = YouTube.getMediaInfo(videoId).getOrNull()
                    title = mediaInfo?.title
                    author = mediaInfo?.author
                } catch (e: Exception) {
                    android.util.Log.e("AKR_MUSIC", "❌ getMediaInfo failed: ${e.message}")
                }
            }

            // Only try Strategy 1 and 2 if the videoId is valid
            if (isVideoIdValid) {
                // Strategy 1: NewPipe direct page scrape — no API auth/PoToken needed.
                // Uses NewPipe's own JS player extraction pipeline (same as NewPipe app).
                val newPipeStreams = try {
                    NewPipeExtractor.newPipePlayer(videoId)
                } catch (e: Exception) {
                    android.util.Log.e("AKR_MUSIC", "❌ Strategy 1 (NewPipe) exception: ${e.message}")
                    emptyList()
                }
                android.util.Log.d("AKR_MUSIC", "📡 NewPipe streams count=${newPipeStreams.size}")

                if (newPipeStreams.isNotEmpty()) {
                    val audioItagPreference = listOf(141, 140, 139, 251, 250, 249)
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
                val signatureTimestamp = NewPipeExtractor.getSignatureTimestamp(videoId).getOrNull()
                android.util.Log.d("AKR_MUSIC", "📡 Strategy 2 signatureTimestamp=$signatureTimestamp")

                val clientsToTry = listOf(
                    YouTubeClient.ANDROID_TESTSUITE,
                    YouTubeClient.ANDROID_MUSIC,
                    YouTubeClient.WEB_REMIX,
                    YouTubeClient.TVHTML5_SIMPLY_EMBEDDED_PLAYER,
                    YouTubeClient.ANDROID_VR_1_43_32,
                    YouTubeClient.ANDROID_VR_NO_AUTH,
                    YouTubeClient.ANDROID_VR_1_61_48,
                    YouTubeClient.IOS,
                    YouTubeClient.IPADOS,
                    YouTubeClient.VISIONOS,
                    YouTubeClient.ANDROID_NO_SDK,
                    YouTubeClient.ANDROID_CREATOR
                )

                for (client in clientsToTry) {
                    try {
                        android.util.Log.d("AKR_MUSIC", "🔄 Strategy 2: Trying client ${client.clientName} (${client.friendlyName ?: ""})")
                        val webResponse = YouTube.player(videoId, client = client, signatureTimestamp = signatureTimestamp).getOrThrow()
                        
                        val status = webResponse.playabilityStatus.status
                        android.util.Log.d("AKR_MUSIC", "📡 Client ${client.clientName} status=$status")
                        
                        val formats = webResponse.streamingData?.adaptiveFormats ?: emptyList()
                        val audioFormats = formats.filter { it.mimeType.startsWith("audio/") }
                        android.util.Log.d("AKR_MUSIC", "📡 Client ${client.clientName} audio formats count=${audioFormats.size}")
                        
                        if (status == "OK" && audioFormats.isNotEmpty()) {
                            // Prioritize audio/mp4 (AAC) over WebM/Opus to prevent hardware/DSP offload issues (static noise/silence)
                            val bestCipherFormat = audioFormats
                                .filter { it.mimeType.contains("mp4") }
                                .maxByOrNull { it.bitrate }
                                ?: audioFormats.maxByOrNull { it.bitrate }

                            if (bestCipherFormat != null) {
                                val resolvedUrl = NewPipeExtractor.getStreamUrl(bestCipherFormat, videoId)
                                if (resolvedUrl != null) {
                                    android.util.Log.d("AKR_MUSIC", "✅ Strategy 2 succeeded with client ${client.clientName} (resolved URL)")
                                    return@runCatching resolvedUrl
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AKR_MUSIC", "❌ Strategy 2 client ${client.clientName} failed: ${e.message}")
                    }
                }
            } else {
                android.util.Log.d("AKR_MUSIC", "🔄 Skipping Strategy 1 and Strategy 2 due to invalid original videoId.")
            }

            // Strategy 3 & 4: Search fallback for alternative video stream (only if not already a fallback request)
            if (!isFallback) {
                android.util.Log.d("AKR_MUSIC", "🔄 Strategy 3 & 4: Falling back to alternative video search for videoId=$videoId")
                try {
                    var title = songTitle
                    var author = songArtist
                    if (title.isNullOrBlank()) {
                        try {
                            val mediaInfo = YouTube.getMediaInfo(videoId).getOrNull()
                            title = mediaInfo?.title
                            author = mediaInfo?.author
                        } catch (e: Exception) {
                            android.util.Log.e("AKR_MUSIC", "❌ getMediaInfo failed: ${e.message}")
                        }
                    }
                    
                    android.util.Log.d("AKR_MUSIC", "📡 Metadata for fallback: title='$title', author='$author'")
                    if (!title.isNullOrBlank()) {
                        val searchQuery = if (!author.isNullOrBlank()) "$author $title" else title
                        
                        // 1. Scraping the first video ID from youtube.com/results
                        android.util.Log.d("AKR_MUSIC", "🔍 Scraping youtube.com/results for query: '$searchQuery'...")
                        val scrapedId = withContext(Dispatchers.IO) { scrapeFirstVideoId(searchQuery) }
                        if (scrapedId != null && scrapedId != videoId) {
                            android.util.Log.d("AKR_MUSIC", "🔄 Trying scraped videoId=$scrapedId")
                            val url = resolveStreamUrlInternal(scrapedId, null, null, isFallback = true).getOrNull()
                            if (url != null) {
                                android.util.Log.d("AKR_MUSIC", "✅ Scraper fallback succeeded: resolved stream URL for videoId=$scrapedId")
                                return@runCatching url
                            }
                        }
                        
                        // 2. Fetch search results for both SONG and VIDEO filters
                        android.util.Log.d("AKR_MUSIC", "🔍 Fetching SONG search results...")
                        val songResult = YouTube.search(searchQuery, YouTube.SearchFilter.FILTER_SONG).getOrNull()
                        val songItems = songResult?.items?.filterIsInstance<SongItem>() ?: emptyList()
                        
                        android.util.Log.d("AKR_MUSIC", "🔍 Fetching VIDEO search results...")
                        val videoResult = YouTube.search(searchQuery, YouTube.SearchFilter.FILTER_VIDEO).getOrNull()
                        val videoItems = videoResult?.items?.filterIsInstance<SongItem>() ?: emptyList()
                        
                        // 2. Pick the first search result from SONG or VIDEO directly
                        android.util.Log.d("AKR_MUSIC", "🔄 Trying top search result directly...")
                        val topCandidates = (songItems.take(1) + videoItems.take(1)).distinctBy { it.id }
                        for (candidate in topCandidates) {
                            if (candidate.id != videoId) {
                                android.util.Log.d("AKR_MUSIC", "🔄 Trying candidate videoId=${candidate.id} (Title: ${candidate.title}) directly")
                                val url = resolveStreamUrlInternal(candidate.id, candidate.title, candidate.artists.firstOrNull()?.name, isFallback = true).getOrNull()
                                if (url != null) {
                                    android.util.Log.d("AKR_MUSIC", "✅ Strategy 3 succeeded: resolved top candidate videoId=${candidate.id}")
                                    return@runCatching url
                                }
                            }
                        }

                        // 3. Fallback: Score all candidates using fuzzy matching if the first results failed to resolve
                        android.util.Log.d("AKR_MUSIC", "🔄 Falling back to scored candidate matching...")
                        val allCandidates = (songItems + videoItems).distinctBy { it.id }
                        val scoredCandidates = allCandidates.map { candidate ->
                            val score = scoreCandidate(title, author, candidate)
                            android.util.Log.d("AKR_MUSIC", "   - Candidate: videoId=${candidate.id}, title='${candidate.title}', score=$score")
                            Pair(candidate, score)
                        }.filter { it.second > 0.0 }
                        .sortedByDescending { it.second }
                        
                        for ((candidate, score) in scoredCandidates) {
                            if (candidate.id != videoId) {
                                android.util.Log.d("AKR_MUSIC", "🔄 Trying candidate videoId=${candidate.id} (Title: ${candidate.title}, Score: $score)")
                                val url = resolveStreamUrlInternal(candidate.id, candidate.title, candidate.artists.firstOrNull()?.name, isFallback = true).getOrNull()
                                if (url != null) {
                                    android.util.Log.d("AKR_MUSIC", "✅ Strategy 4 succeeded: resolved alternative videoId=${candidate.id} with score=$score")
                                    return@runCatching url
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AKR_MUSIC", "❌ Strategy 3/4 fallback search failed: ${e.message}")
                }
            }

            throw Exception("All stream resolution strategies failed for videoId=$videoId")
        }
    }

    private fun scrapeFirstVideoId(query: String): String? {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = java.net.URL("https://www.youtube.com/results?search_query=$encodedQuery")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            
            val responseCode = connection.responseCode
            if (responseCode != 200) {
                android.util.Log.e("AKR_MUSIC", "Scrape failed: HTTP response code $responseCode")
                return null
            }
            
            val reader = java.io.BufferedReader(java.io.InputStreamReader(connection.inputStream))
            val sb = java.lang.StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()
            
            val html = sb.toString()
            val regex = """\"videoId\":\"([a-zA-Z0-9_-]{11})\"""".toRegex()
            val match = regex.find(html)
            if (match != null) {
                val videoId = match.groupValues[1]
                android.util.Log.d("AKR_MUSIC", "🔍 Scraped videoId: $videoId using videoId regex")
                return videoId
            }
        } catch (e: Exception) {
            android.util.Log.e("AKR_MUSIC", "Error scraping first videoId: ${e.message}", e)
        }
        return null
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = IntArray(s2.length + 1) { it }
        for (i in 1..s1.length) {
            var prev = dp[0]
            dp[0] = i
            for (j in 1..s2.length) {
                val temp = dp[j]
                if (s1[i - 1] == s2[j - 1]) {
                    dp[j] = prev
                } else {
                    dp[j] = minOf(dp[j - 1], dp[j], prev) + 1
                }
                prev = temp
            }
        }
        return dp[s2.length]
    }

    private fun getTitleSimilarityScore(original: String, candidate: String): Double {
        val origLower = original.lowercase()
        val candLower = candidate.lowercase()
        
        // Filter out remixes/instrumentals/covers/teasers/promos if the original was a clean track
        val filters = listOf("remix", "dj", "mashup", "instrumental", "karaoke", "cover", "reaction", "review", "teaser", "promo", "trailer", "making", "interview", "behind", "shorts")
        for (f in filters) {
            if (candLower.contains(f) && !origLower.contains(f)) {
                return 0.0
            }
        }
        
        val cleanOriginal = normalizeTitle(original)
        val cleanCandidate = normalizeTitle(candidate)
        
        val languages = listOf("hindi", "tamil", "telugu", "kannada", "malayalam", "bengali", "marathi", "punjabi")
        val originalLangs = languages.filter { origLower.contains(it) }
        val candidateLangs = languages.filter { candLower.contains(it) }
        var langPenalty = 0.0
        for (lang in candidateLangs) {
            if (lang !in originalLangs) {
                android.util.Log.d("AKR_MUSIC", "⚠️ Language penalty candidate: '$candidate' (has '$lang', original has not)")
                langPenalty += 0.35
            }
        }
        
        if (cleanOriginal.isBlank() || cleanCandidate.isBlank()) return 0.0
        
        if (cleanOriginal.contains(cleanCandidate) || cleanCandidate.contains(cleanOriginal)) {
            return 1.0
        }
        
        val stopwords = setOf("and", "the", "with", "from", "for", "off", "out", "our")
        val originalWords = cleanOriginal.split(" ").filter { it.isNotBlank() && it !in stopwords }
        val candidateWords = cleanCandidate.split(" ").filter { it.isNotBlank() && it !in stopwords }
        
        if (originalWords.isEmpty() || candidateWords.isEmpty()) return 0.0
        
        var matchedWordsCount = 0
        for (origWord in originalWords) {
            var foundMatch = false
            val origSimp = simplifyPhonetics(origWord)
            for (candWord in candidateWords) {
                val candSimp = simplifyPhonetics(candWord)
                if (origSimp == candSimp || candSimp.contains(origSimp) || origSimp.contains(candSimp)) {
                    foundMatch = true
                    break
                }
                val dist = levenshteinDistance(origSimp, candSimp)
                val maxLen = maxOf(origSimp.length, candSimp.length)
                val similarity = 1.0 - dist.toDouble() / maxLen
                if (similarity >= 0.7 || (maxLen <= 4 && dist <= 1)) {
                    foundMatch = true
                    break
                }
            }
            if (foundMatch) {
                matchedWordsCount++
            }
        }
        
        val wordScore = matchedWordsCount.toDouble() / originalWords.size
        
        // Also compute character-level similarity on space-removed simplified titles
        val origSpaceRemoved = simplifyPhonetics(cleanOriginal.replace(" ", ""))
        val candSpaceRemoved = simplifyPhonetics(cleanCandidate.replace(" ", ""))
        val charDist = levenshteinDistance(origSpaceRemoved, candSpaceRemoved)
        val charMaxLen = maxOf(origSpaceRemoved.length, candSpaceRemoved.length)
        val charScore = if (charMaxLen > 0) 1.0 - charDist.toDouble() / charMaxLen else 0.0
        
        val baseScore = maxOf(wordScore, charScore)
        return maxOf(0.0, baseScore - langPenalty)
    }

    private fun simplifyPhonetics(word: String): String {
        return word.lowercase()
            .replace("sh", "s")
            .replace("dh", "d")
            .replace("th", "t")
            .replace("kh", "k")
            .replace("gh", "g")
            .replace("bh", "b")
            .replace("ph", "p")
            .replace("ee", "i")
            .replace("oo", "u")
            .replace("aa", "a")
            .replace("w", "v")
            .replace("y", "i")
    }

    private fun isArtistSimilar(originalArtist: String?, candidate: SongItem): Boolean {
        if (originalArtist.isNullOrBlank()) return true
        val origLower = originalArtist.lowercase()
        
        val stopwords = setOf("and", "feat", "ft", "featuring", "with", "x", "vs")
        val originalWords = origLower.split(" ", ",", "&")
            .map { it.trim().replace(Regex("[^\\p{L}\\p{N}]+"), "") }
            .filter { it.isNotBlank() && it !in stopwords }
            
        if (originalWords.isEmpty()) return true
        
        // 1. Check in candidate artists metadata
        for (artist in candidate.artists) {
            val candLower = artist.name.lowercase()
            val candWords = candLower.split(" ", ",", "&")
                .map { it.trim().replace(Regex("[^\\p{L}\\p{N}]+"), "") }
                .filter { it.isNotBlank() && it !in stopwords }
                
            val common = originalWords.intersect(candWords.toSet())
            if (common.isNotEmpty()) {
                return true
            }
        }
        
        // 2. Check if artist name exists inside the candidate title (common for channel uploads)
        val titleLower = candidate.title.lowercase()
        for (word in originalWords) {
            if (word.length >= 3 && titleLower.contains(word)) {
                return true
            }
        }
        
        return false
    }

    private fun scoreCandidate(originalTitle: String, originalArtist: String?, candidate: SongItem): Double {
        // Filter out short clips/teasers (< 60 seconds)
        val duration = candidate.duration
        if (duration != null && duration < 60) {
            return 0.0
        }
        
        var score = 0.0
        
        val titleSim = getTitleSimilarityScore(originalTitle, candidate.title)
        if (titleSim < 0.45) {
            return 0.0
        }
        
        score += titleSim * 100.0
        
        val artistSim = isArtistSimilar(originalArtist, candidate)
        if (artistSim) {
            score += 50.0
        }
        
        if (titleSim >= 0.7 && artistSim) {
            score += 50.0
        }
        
        val cleanOrig = normalizeTitle(originalTitle)
        val cleanCand = normalizeTitle(candidate.title)
        if (cleanCand.contains(cleanOrig)) {
            score += 30.0
        }
        
        return score
    }

    private fun normalizeTitle(title: String): String {
        return title.lowercase()
            .replace(Regex("[\\(\\)\\[\\]\\-\\:\\,\\.\\\"\\'\\?\\!\\/\\&]"), " ")
            .replace(Regex("\\b(official|video|audio|lyric|lyrics|from|movie|song|full|hd|4k|lirical|lirik|clean|uncut)\\b"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
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
