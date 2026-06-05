package com.akr.finalapp.presentation.screens

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.akr.finalapp.data.model.Song
import com.akr.finalapp.presentation.components.subcomps.EnhancedSongListItem
import com.akr.finalapp.presentation.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Immutable
internal data class LibrarySongPlaybackUiState(
    val isCurrentSong: Boolean = false,
    val isPlaying: Boolean = false
)

@OptIn(UnstableApi::class)
@Composable
internal fun LibraryPlaybackAwareSongItem(
    song: Song,
    playerViewModel: PlayerViewModel,
    albumArtSize: Dp = 50.dp,
    isSelected: Boolean = false,
    selectionIndex: Int? = null,
    isSelectionMode: Boolean = false,
    onLongPress: () -> Unit = {},
    onMoreOptionsClick: (Song) -> Unit,
    onClick: () -> Unit,
    isCurrentSong: Boolean? = null,
    isPlaying: Boolean? = null
) {
    val finalIsCurrentSong: Boolean
    val finalIsPlaying: Boolean

    if (isCurrentSong != null && isPlaying != null) {
        finalIsCurrentSong = isCurrentSong
        finalIsPlaying = isPlaying
    } else {
        val playbackUiState by remember(song.id, playerViewModel) {
            playerViewModel.stablePlayerState
                .map { state ->
                    val isCurrent = state.currentSong?.id == song.id
                    LibrarySongPlaybackUiState(
                        isCurrentSong = isCurrent,
                        isPlaying = isCurrent && state.isPlaying
                    )
                }
                .distinctUntilChanged()
        }.collectAsStateWithLifecycle(initialValue = LibrarySongPlaybackUiState())
        finalIsCurrentSong = playbackUiState.isCurrentSong
        finalIsPlaying = playbackUiState.isPlaying
    }

    EnhancedSongListItem(
        song = song,
        isPlaying = finalIsPlaying,
        isCurrentSong = finalIsCurrentSong,
        isLoading = false,
        albumArtSize = albumArtSize,
        isSelected = isSelected,
        selectionIndex = selectionIndex,
        isSelectionMode = isSelectionMode,
        onLongPress = onLongPress,
        onMoreOptionsClick = onMoreOptionsClick,
        onClick = onClick
    )
}
