package io.prashant.topalbums.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(
    tableName = "album",
    indices = [Index(value = ["id"])]
)
data class AlbumEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NotNull
    val id: String,

    @ColumnInfo(name = "name")
    @NotNull
    val name: String,

    @ColumnInfo(name = "price")
    @NotNull
    val price: String,

    @ColumnInfo(name = "artist")
    @NotNull
    val artist: String,
)
