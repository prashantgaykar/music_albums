package io.prashant.topalbums.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(
    tableName = "favorite_album",
    indices = [Index(value = ["album_id"])]
)
data class FavoriteAlbumEntity(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "album_id")
    val albumId: String,
)

