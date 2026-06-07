package com.akr.finalapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akr.finalapp.data.model.Song
import com.akr.finalapp.data.repository.YoutubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.akr.finalapp.data.preferences.UserPreferencesRepository
import com.akr.finalapp.data.model.SavedYoutubePlaylist
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val youtubeRepository: YoutubeRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val savedPlaylists = userPreferencesRepository.savedYoutubePlaylistsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun search() {
        val query = _searchQuery.value
        if (query.isBlank()) return

        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null

            val result = youtubeRepository.searchSongs(query)
            if (result.isSuccess) {
                _searchResults.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Unknown Error"
            }
            _isSearching.value = false
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
        _searchQuery.value = ""
        _errorMessage.value = null
    }

    fun resolveStreamUrl(song: Song, onResolved: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            // Repository does IO on Dispatchers.IO internally via withContext
            val result = youtubeRepository.resolveStreamUrl(song.id, songTitle = song.title, songArtist = song.artist)
            // Always deliver results on Main thread — Toast & playerViewModel require it
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    result.getOrNull()?.let { onResolved(it) } ?: onError("Failed to resolve URL")
                } else {
                    onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to resolve URL")
                }
            }
        }
    }

    fun loadPlaylist(playlistId: String, onSuccess: (String, List<Song>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = youtubeRepository.getPlaylist(playlistId)
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    val pair = result.getOrThrow()
                    onSuccess(pair.first, pair.second)
                } else {
                    onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to load playlist")
                }
            }
        }
    }

    fun extractPlaylistId(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null
        val fromUrl = Regex("[?&]list=([A-Za-z0-9_-]+)").find(trimmed)?.groupValues?.get(1)
        if (fromUrl != null) return fromUrl
        if (!trimmed.contains("/") && !trimmed.contains(".") && trimmed.length >= 10 && trimmed.all { it.isLetterOrDigit() || it == '_' || it == '-' }) {
            return trimmed
        }
        return null
    }

    fun savePlaylist(id: String, name: String, url: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveYoutubePlaylist(
                SavedYoutubePlaylist(id = id, name = name, url = url)
            )
        }
    }

    fun removeSavedPlaylist(playlistId: String) {
        viewModelScope.launch {
            userPreferencesRepository.removeSavedYoutubePlaylist(playlistId)
        }
    }
}
