package io.prashant.topalbums.domain.repository

import io.prashant.topalbums.domain.model.Album

interface AlbumRepository {

    suspend fun loadAlbums(
        searchByAlbumName: String = "",
        fetchFromNetwork: Boolean = false
    ): List<Album>

    suspend fun setAlbumFavorite(albumId: String, isFavorite: Boolean): Boolean

    suspend fun isFavoriteAlbum(albumId: String): Boolean
}