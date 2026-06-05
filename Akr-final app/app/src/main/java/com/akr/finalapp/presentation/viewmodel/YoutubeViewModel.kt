package com.akr.finalapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akr.finalapp.data.model.Song
import com.akr.finalapp.data.repository.YoutubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val youtubeRepository: YoutubeRepository
) : ViewModel() {

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
            val result = youtubeRepository.resolveStreamUrl(song.id)
            if (result.isSuccess) {
                result.getOrNull()?.let { onResolved(it) } ?: onError("Failed to resolve URL")
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to resolve URL")
            }
        }
    }

    fun loadPlaylist(playlistId: String, onSuccess: (String, List<Song>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = youtubeRepository.getPlaylist(playlistId)
            if (result.isSuccess) {
                val pair = result.getOrThrow()
                onSuccess(pair.first, pair.second)
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to load playlist")
            }
        }
    }

    fun extractPlaylistId(url: String): String? {
        return Regex("[?&]list=([A-Za-z0-9_-]+)").find(url)?.groupValues?.get(1)
    }
}
