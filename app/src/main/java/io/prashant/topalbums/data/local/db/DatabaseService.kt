package io.prashant.topalbums.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.prashant.topalbums.data.local.db.dao.AlbumDao
import io.prashant.topalbums.data.local.db.entity.AlbumEntity
import io.prashant.topalbums.data.local.db.entity.AlbumImageEntity
import io.prashant.topalbums.data.local.db.entity.FavoriteAlbumEntity


@Database(
    entities = [
        AlbumEntity::class,
        AlbumImageEntity::class,
        FavoriteAlbumEntity::class,
    ],
    exportSchema = false,
    version = 1
)

abstract class DatabaseService : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
}