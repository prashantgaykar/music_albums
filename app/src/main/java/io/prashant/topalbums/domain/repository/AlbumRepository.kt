package io.prashant.topalbums.domain.repository

import io.prashant.topalbums.data.local.db.DatabaseService
import io.prashant.topalbums.data.local.db.entity.AlbumEntity
import io.prashant.topalbums.data.local.db.entity.AlbumImageEntity
import io.prashant.topalbums.data.remote.NetworkService
import io.prashant.topalbums.data.remote.response.TopAlbumResponse
import io.prashant.topalbums.domain.model.Album
import io.prashant.topalbums.domain.model.AlbumImage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    private val databaseService: DatabaseService,
    private val networkService: NetworkService
) {

    private val albumDao = databaseService.albumDao()

    companion object {
        private const val TAG = "AlbumRepository"
        private const val ALBUM_FETCH_LIMIT = 100
    }

    suspend fun getAlbums(fetchFromNetwork: Boolean = false): List<Album> {
        return if (!fetchFromNetwork && albumDao.getAlbumCount() > 0) {
            getAlbumsFromDb()
        } else {
            fetchAndSaveAlbums()
            getAlbumsFromDb()
        }
    }

    private fun getAlbumsFromDb(): List<Album> {
        val albums = mutableListOf<Album>()
        val albumEntityMap = albumDao.getAlbums()
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

    private suspend fun fetchAndSaveAlbums() {
        val albumResponse = networkService.getAlbums(ALBUM_FETCH_LIMIT)
        val (albumEntities, albumImageEntities) = mapTopAlbumResponseToEntity(albumResponse)
        albumDao.addAlbums(albumEntities, albumImageEntities)
    }

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