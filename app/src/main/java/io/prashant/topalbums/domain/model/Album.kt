package io.prashant.topalbums.domain.model

data class Album(
    val id: String,
    val name: String,
    val price: String,
    val artist: String,
    val images: List<AlbumImage>
)
