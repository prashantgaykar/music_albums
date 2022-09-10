package io.prashant.topalbums.data.remote

import io.prashant.topalbums.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object Networking {

    private const val TAG = "Networking"

    private const val DEFAULT_READ_TIMEOUT = 60
    private const val DEFAULT_WRITE_TIMEOUT = 60
    private const val DEFAULT_CONNECTION_TIMEOUT = 20

    fun create(baseUrl: String, cacheDir: File? = null, cacheSize: Long? = null): NetworkService {
        return Retrofit.Builder()
            .apply {
                baseUrl(baseUrl)
                client(buildOkHttpClient(cacheDir, cacheSize))
                addConverterFactory(ScalarsConverterFactory.create())
                addConverterFactory(GsonConverterFactory.create())
            }
            .build()
            .create(NetworkService::class.java)
    }

    private fun buildOkHttpClient(cacheDir: File?, cacheSize: Long?) =
        OkHttpClient.Builder().apply {
            if (cacheDir != null && cacheSize != null) {
                cache(Cache(cacheDir, cacheSize))
            }
            addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
                    })
            readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            connectTimeout(DEFAULT_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        }.build()

}