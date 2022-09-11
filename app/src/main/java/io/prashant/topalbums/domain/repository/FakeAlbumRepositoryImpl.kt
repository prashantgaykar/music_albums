package io.prashant.topalbums.domain.repository

import io.prashant.topalbums.domain.model.Album

class FakeAlbumRepositoryImpl() : AlbumRepository {

    companion object {
        private const val TAG = "FakeAlbumRepositoryImpl"
    }

    override suspend fun loadAlbums(
        searchByAlbumName: String,
        fetchFromNetwork: Boolean
    ): List<Album> {
        return emptyList()
    }

    override suspend fun setAlbumFavorite(albumId: String, isFavorite: Boolean): Boolean {
        return true
    }

    override suspend fun isFavoriteAlbum(albumId: String) = false

}