package io.prashant.topalbums.domain.repository

import io.prashant.topalbums.data.local.db.DatabaseService
import io.prashant.topalbums.data.local.db.entity.AlbumEntity
import io.prashant.topalbums.data.local.db.entity.AlbumImageEntity
import io.prashant.topalbums.data.local.db.entity.FavoriteAlbumEntity
import io.prashant.topalbums.data.remote.NetworkService
import io.prashant.topalbums.data.remote.response.TopAlbumResponse
import io.prashant.topalbums.domain.model.Album
import io.prashant.topalbums.domain.model.AlbumImage
import io.prashant.topalbums.util.AppLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val databaseService: DatabaseService,
    private val networkService: NetworkService
) : AlbumRepository {

    private val albumDao by lazy { databaseService.albumDao() }

    companion object {
        private const val TAG = "AlbumRepository"
        private const val ALBUM_FETCH_LIMIT = 50
    }


    override suspend fun loadAlbums(
        searchByAlbumName: String,
        fetchFromNetwork: Boolean
    ): List<Album> {
        return if (!fetchFromNetwork && albumDao.getAlbumCount() > 0) {
            getAlbums(searchByAlbumName)
        } else {
            fetchAndSaveAlbums()
            getAlbums(searchByAlbumName)
        }
    }

    override suspend fun setAlbumFavorite(albumId: String, isFavorite: Boolean): Boolean {
        val favoriteAlbumEntity = FavoriteAlbumEntity(albumId)
        if (isFavorite) {
            albumDao.addAlbumInFavorite(favoriteAlbumEntity)
        } else {
            albumDao.removeAlbumFromFavorite(favoriteAlbumEntity)
        }
        return isFavorite
    }

    override suspend fun isFavoriteAlbum(albumId: String) =
        albumDao.isFavoriteAlbum(albumId) != null


    private suspend fun getAlbums(searchByAlbumName: String): List<Album> {
        val albumEntityMap = albumDao.getAlbums(searchByAlbumName)
        return mapAlbumEntityToAlbum(albumEntityMap)
    }

    private suspend fun fetchAndSaveAlbums() {
        AppLogger.e(TAG, "fetchAndSaveAlbums()")
        val albumResponse = networkService.getAlbums(ALBUM_FETCH_LIMIT)
        val (albumEntities, albumImageEntities) = mapTopAlbumResponseToEntity(albumResponse)
        albumDao.addAlbums(albumEntities, albumImageEntities)
    }

    /**
     * Convert Database Data to Domain Data
     **/
    private fun mapAlbumEntityToAlbum(albumEntityMap: Map<AlbumEntity, List<AlbumImageEntity>>): List<Album> {
        val albums = mutableListOf<Album>()
        for ((albumEntity, albumImageEntities) in albumEntityMap) {
            with(albumEntity) {
                val albumImages = mutableListOf<AlbumImage>()
                albumImageEntities.forEach {
                    albumImages.add(
                        AlbumImage(
                            url = it.url,
                            height = it.height
                        )
                    )
                }
                albums.add(
                    Album(
                        id = id,
                        name = name,
                        price = price,
                        artist = artist,
                        images = albumImages
                    )
                )
            }
        }
        return albums
    }

    /**
     * Convert Network Data to Database Data
     **/
    private fun mapTopAlbumResponseToEntity(albumResponse: TopAlbumResponse): Pair<List<AlbumEntity>, List<AlbumImageEntity>> {
        val albumEntities = mutableListOf<AlbumEntity>()
        val albumImageEntities = mutableListOf<AlbumImageEntity>()
        albumResponse.feed.entries.forEach { entry ->
            with(entry) {
                albumEntities.add(
                    AlbumEntity(
                        id = id.attributes.id,
                        name = name.label,
                        price = price.label,
                        artist = artist.label
                    )
                )
                images.forEach { albumImage ->
                    albumImageEntities.add(
                        AlbumImageEntity(
                            albumId = entry.id.attributes.id,
                            url = albumImage.label,
                            height = albumImage.attributes.height.toInt()
                        )
                    )
                }
            }
        }
        return Pair(albumEntities, albumImageEntities)
    }
}