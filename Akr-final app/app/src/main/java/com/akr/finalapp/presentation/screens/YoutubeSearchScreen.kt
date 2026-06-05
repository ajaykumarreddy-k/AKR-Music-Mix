package com.akr.finalapp.presentation.screens

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.akr.finalapp.presentation.components.MiniPlayerHeight
import com.akr.finalapp.presentation.viewmodel.PlayerViewModel
import com.akr.finalapp.presentation.viewmodel.YoutubeViewModel
import com.akr.finalapp.presentation.components.subcomps.EnhancedSongListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoutubeSearchScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    youtubeViewModel: YoutubeViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val searchQuery by youtubeViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by youtubeViewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by youtubeViewModel.isSearching.collectAsStateWithLifecycle()
    val errorMessage by youtubeViewModel.errorMessage.collectAsStateWithLifecycle()
    val stablePlayerState by playerViewModel.stablePlayerState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var showPlaylistDialog by remember { mutableStateOf(false) }
    var playlistUrlInput by remember { mutableStateOf("") }
    var playlistUrlError by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val inputColors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                )

                Box(Modifier.weight(1f)) {
                    DockedSearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                modifier = Modifier.focusRequester(focusRequester),
                                query = searchQuery,
                                onQueryChange = { youtubeViewModel.updateSearchQuery(it) },
                                onSearch = { youtubeViewModel.search(); keyboardController?.hide() },
                                expanded = false,
                                onExpandedChange = {},
                                placeholder = { Text("Search YouTube Music", color = MaterialTheme.colorScheme.primary) },
                                leadingIcon = { Icon(Icons.Rounded.Search, null, tint = MaterialTheme.colorScheme.primary) },
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(
                                            onClick = { youtubeViewModel.clearResults() },
                                            modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                                        ) { Icon(Icons.Rounded.Close, null, tint = MaterialTheme.colorScheme.primary) }
                                    }
                                },
                                colors = inputColors
                            )
                        },
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier.clip(RoundedCornerShape(28.dp)),
                        colors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            inputFieldColors = inputColors
                        ),
                        content = {}
                    )
                }
                
                IconButton(onClick = { showPlaylistDialog = true }) {
                    Icon(
                        imageVector = Icons.Rounded.PlaylistPlay,
                        contentDescription = "Open Playlist",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isSearching -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                    !errorMessage.isNullOrEmpty() -> Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center).padding(16.dp), textAlign = TextAlign.Center)
                    searchResults.isEmpty() && searchQuery.isNotBlank() -> Text("No results found.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.Center))
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = paddingValues.calculateBottomPadding() + MiniPlayerHeight + 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        items(searchResults) { song ->
                            Box(modifier = Modifier.padding(bottom = 12.dp)) {
                                EnhancedSongListItem(
                                    song = song,
                                    isPlaying = stablePlayerState.isPlaying && stablePlayerState.currentSong?.id == song.id,
                                    isCurrentSong = stablePlayerState.currentSong?.id == song.id,
                                    onClick = {
                                        // 1. Force an Error-level log that Realme cannot block
                                        Log.e("AKR_MUSIC", "👉 EXACT TAP DETECTED: ${song.title}")
                                        
                                        // 2. Force an immediate screen popup so we know the button isn't dead
                                        Toast.makeText(context, "Connecting to YouTube...", Toast.LENGTH_SHORT).show()
                                        
                                        youtubeViewModel.resolveStreamUrl(song,
                                            onResolved = { url ->
                                                Log.e("AKR_MUSIC", "✅ URL FOUND: $url")
                                                playerViewModel.playSongs(listOf(song.copy(contentUriString = url)), song.copy(contentUriString = url), "YouTube Search", null)
                                            },
                                            onError = { err ->
                                                Log.e("AKR_MUSIC", "❌ STREAM ERROR: $err")
                                                Toast.makeText(context, "Error: $err", Toast.LENGTH_LONG).show()
                                            }
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
        
        if (showPlaylistDialog) {
            AlertDialog(
                onDismissRequest = { showPlaylistDialog = false; playlistUrlError = false },
                title = { Text("Open YT Music Playlist") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = playlistUrlInput,
                            onValueChange = { playlistUrlInput = it; playlistUrlError = false },
                            label = { Text("Paste playlist URL") },
                            isError = playlistUrlError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (playlistUrlError) {
                            Text("Invalid URL", color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val id = youtubeViewModel.extractPlaylistId(playlistUrlInput)
                        if (id != null) {
                            showPlaylistDialog = false
                            playlistUrlInput = ""
                            navController.navigate("youtube_playlist/$id")
                        } else {
                            playlistUrlError = true
                        }
                    }) { Text("Open") }
                },
                dismissButton = {
                    TextButton(onClick = { showPlaylistDialog = false; playlistUrlError = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
