package io.prashant.topalbums.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.prashant.topalbums.data.local.db.DatabaseService
import io.prashant.topalbums.data.remote.ApiHelper
import io.prashant.topalbums.data.remote.NetworkService
import io.prashant.topalbums.data.remote.Networking
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule() {

    @Singleton
    @Provides
    fun provideNetworkService(@ApplicationContext context: Context): NetworkService =
        Networking.create(
            ApiHelper.BASE_URl,
            cacheDir = context.cacheDir,
            cacheSize = (5 * 1024 * 1024).toLong()
        )


    @Provides
    @Singleton
    fun provideDatabaseService(@ApplicationContext applicationContext: Context): DatabaseService {
        return Room.databaseBuilder(
            applicationContext,
            DatabaseService::class.java,
            "album_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }


}