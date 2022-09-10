package io.prashant.topalbums.data.local.db.dao

import androidx.room.*
import io.prashant.topalbums.data.local.db.entity.AlbumEntity
import io.prashant.topalbums.data.local.db.entity.AlbumImageEntity


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

}