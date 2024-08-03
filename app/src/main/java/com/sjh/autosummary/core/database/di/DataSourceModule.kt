package com.sjh.autosummary.core.database.di

import com.sjh.autosummary.core.database.LocalDataSource
import com.sjh.autosummary.core.database.room.db.RoomDatabaseDaos
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindLocalDataSource(roomDatabaseDaos: RoomDatabaseDaos): LocalDataSource
}
