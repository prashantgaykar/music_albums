package io.prashant.topalbums.data.local.db.entity

import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(
    tableName = "album_image",
    foreignKeys = [ForeignKey(
        entity = AlbumEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("album_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["id", "album_id"])]
)
data class AlbumImageEntity(

    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @NotNull
    @ColumnInfo(name = "album_id")
    val albumId: String,

    @NotNull
    @ColumnInfo(name = "url")
    val url: String,

    @NotNull
    @ColumnInfo(name = "height")
    val height: Int,

    )

