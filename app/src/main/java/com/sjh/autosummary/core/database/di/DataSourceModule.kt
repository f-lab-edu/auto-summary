package com.sjh.autosummary.core.database.di

import com.sjh.autosummary.core.database.LocalHistoryDataSource
import com.sjh.autosummary.core.database.LocalSummaryDataSource
import com.sjh.autosummary.core.database.room.LocalHistoryDataSourceImpl
import com.sjh.autosummary.core.database.room.LocalSummaryDataSourceImpl
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
    abstract fun bindLocalHistoryDataSource(localHistoryDataSource: LocalHistoryDataSource): LocalHistoryDataSourceImpl

    @Binds
    @Singleton
    abstract fun bindLocalSummaryDataSource(localSummaryDataSource: LocalSummaryDataSource): LocalSummaryDataSourceImpl
}
