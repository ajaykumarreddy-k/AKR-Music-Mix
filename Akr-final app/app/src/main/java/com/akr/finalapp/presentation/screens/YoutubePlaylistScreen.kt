package com.akr.finalapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.akr.finalapp.presentation.components.MiniPlayerHeight
import com.akr.finalapp.presentation.viewmodel.PlayerViewModel
import com.akr.finalapp.presentation.viewmodel.YoutubeViewModel
import com.akr.finalapp.presentation.components.subcomps.EnhancedSongListItem
import com.akr.finalapp.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoutubePlaylistScreen(
    playlistId: String,
    paddingValues: PaddingValues,
    navController: NavController,
    youtubeViewModel: YoutubeViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val stablePlayerState by playerViewModel.stablePlayerState.collectAsStateWithLifecycle()
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var title by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(playlistId) {
        youtubeViewModel.loadPlaylist(playlistId,
            onSuccess = { t, s -> title = t; songs = s; isLoading = false },
            onError = { e -> errorMessage = e; isLoading = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = paddingValues.calculateBottomPadding() + MiniPlayerHeight + 16.dp,
                        start = 16.dp, end = 16.dp
                    )
                ) {
                    items(songs) { song ->
                        Box(modifier = Modifier.padding(bottom = 12.dp)) {
                            EnhancedSongListItem(
                                song = song,
                                isPlaying = stablePlayerState.isPlaying && stablePlayerState.currentSong?.id == song.id,
                                isCurrentSong = stablePlayerState.currentSong?.id == song.id,
                                onClick = {
                                    youtubeViewModel.resolveStreamUrl(song,
                                        onResolved = { url ->
                                            playerViewModel.playSongs(listOf(song.copy(contentUriString = url)), song.copy(contentUriString = url), title, playlistId)
                                        },
                                        onError = {}
                                    )
                                },
                                onMoreOptionsClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
