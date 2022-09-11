package io.prashant.topalbums.domain.model

data class Album(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val artist: String = "",
    val isFavorite: Boolean = false,
    val images: List<AlbumImage> = emptyList()
)
