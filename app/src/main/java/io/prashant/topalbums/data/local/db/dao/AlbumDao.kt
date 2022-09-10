package io.prashant.topalbums.data.local.db.dao

import androidx.room.*
import io.prashant.topalbums.data.local.db.entity.AlbumEntity
import io.prashant.topalbums.data.local.db.entity.AlbumImageEntity
import io.prashant.topalbums.data.local.db.entity.FavoriteAlbumEntity


@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlbumImages(albumImages: List<AlbumImageEntity>)

    @Query(
        "SELECT * FROM album" +
                " LEFT JOIN album_image ON album.id = album_image.album_id"
    )
    fun getAlbums(): Map<AlbumEntity, List<AlbumImageEntity>>

    @Query("SELECT count(id) FROM album")
    suspend fun getAlbumCount(): Int

    @Transaction
    suspend fun addAlbums(albums: List<AlbumEntity>, albumImages: List<AlbumImageEntity>) {
        addAlbums(albums)
        addAlbumImages(albumImages)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlbumInFavorite(favoriteAlbumEntity: FavoriteAlbumEntity)

    @Delete
    suspend fun removeAlbumFromFavorite(favoriteAlbumEntity: FavoriteAlbumEntity)

    @Query("SELECT album_id FROM favorite_album WHERE album_id = :albumId")
    suspend fun isFavoriteAlbum(albumId: String): String?
}