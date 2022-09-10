package io.prashant.topalbums.data.remote


import io.prashant.topalbums.data.remote.response.TopAlbumResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkService {

    @GET(ApiHelper.TOP_ALBUMS_ENDPOINT)
    suspend fun getAlbums(
        @Path(ApiHelper.Field.ALBUM_LIMIT) albumLimit: Int,
    ): TopAlbumResponse

}