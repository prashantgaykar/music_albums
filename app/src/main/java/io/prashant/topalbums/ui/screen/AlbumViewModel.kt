package io.prashant.topalbums.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.prashant.topalbums.domain.model.Album
import io.prashant.topalbums.domain.repository.AlbumRepository
import io.prashant.topalbums.util.ApiResult
import io.prashant.topalbums.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AlbumViewModel"
    }

    private val _albumsState: MutableStateFlow<ApiResult<List<Album>>> =
        MutableStateFlow(ApiResult.loading(false))
    val albumsState = _albumsState.asStateFlow()

    private val _albumsFavoriteState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val albumsFavoriteState = _albumsFavoriteState.asStateFlow()

    private val _searchTextState: MutableStateFlow<String> = MutableStateFlow("")
    val searchTextState = _searchTextState.asStateFlow()

    fun loadAlbums(searchByAlbumName: String = "", loadFromNetwork: Boolean = false) {
        viewModelScope.launch {
            _searchTextState.emit(searchByAlbumName)
            _albumsState.emit(ApiResult.loading(true))
            try {
                val albums = albumRepository.loadAlbums(searchByAlbumName, loadFromNetwork)
                _albumsState.emit(ApiResult.success(albums))
            } catch (e: Exception) {
                AppLogger.e(TAG, "loadAlbums() - $e")
                _albumsState.emit(ApiResult.failure(e))
            }
        }
    }

    fun setAlbumFavorite(albumId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            albumRepository.setAlbumFavorite(albumId, isFavorite)
            isAlbumFavorite(albumId)
        }
    }

    fun isAlbumFavorite(albumId: String) {
        viewModelScope.launch {
            _albumsFavoriteState.emit(
                albumRepository.isFavoriteAlbum(albumId)
            )
        }
    }
}