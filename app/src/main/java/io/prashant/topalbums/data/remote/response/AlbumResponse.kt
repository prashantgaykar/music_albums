package io.prashant.topalbums.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TopAlbumResponse(
    @Expose
    @SerializedName("feed")
    val feed: Feed
)

data class Feed(
    @Expose
    @SerializedName("entry")
    val entries: List<Entry>
)


data class Entry(

    @Expose
    @SerializedName("id")
    val id: AlbumId,

    @Expose
    @SerializedName("im:name")
    val name: AlbumName,

    @Expose
    @SerializedName("im:image")
    val images: List<AlbumImage>,

    @Expose
    @SerializedName("im:price")
    val price: AlbumPrice,

    @Expose
    @SerializedName("title")
    val title: AlbumTitle,

    @Expose
    @SerializedName("im:artist")
    val artist: AlbumArtist,
)

/** Entry fields **/

data class AlbumId(
    @Expose
    @SerializedName("attributes")
    val attributes: Attributes
) {
    data class Attributes(
        @Expose
        @SerializedName("im:id")
        val id: String
    )
}

data class AlbumName(
    @Expose
    @SerializedName("label")
    val label: String
)

data class AlbumTitle(
    @Expose
    @SerializedName("label")
    val label: String
)

data class AlbumImage(
    @Expose
    @SerializedName("label")
    val label: String,
    @Expose
    @SerializedName("attributes")
    val attributes: Attributes
) {
    data class Attributes(
        @Expose
        @SerializedName("height")
        val height: String
    )
}

data class AlbumPrice(
    @Expose
    @SerializedName("label")
    val label: String
)

data class AlbumArtist(
    @Expose
    @SerializedName("label")
    val label: String
)