package com.cariboa.app.data.di

import android.content.Context
import androidx.room.Room
import com.cariboa.app.data.local.CaribouDatabase
import com.cariboa.app.data.local.dao.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CaribouDatabase =
        Room.databaseBuilder(context, CaribouDatabase::class.java, "caribou.db").build()

    @Provides
    fun provideTripDao(db: CaribouDatabase): TripDao = db.tripDao()
}
