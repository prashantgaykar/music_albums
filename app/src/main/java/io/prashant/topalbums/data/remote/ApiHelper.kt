package io.prashant.topalbums.data.remote

import io.prashant.topalbums.data.remote.ApiHelper.Field.ALBUM_LIMIT

object ApiHelper {

    const val BASE_URl = "https://itunes.apple.com/us/rss"
    const val TOP_ALBUMS_ENDPOINT = "/topalbums/limit={$ALBUM_LIMIT}/json"

    object Field {
        const val ALBUM_LIMIT = "album_limit"
    }
}