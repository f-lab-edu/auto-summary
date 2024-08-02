package com.sjh.autosummary.core.network.di

import com.sjh.autosummary.core.network.NetworkDataSource
import com.sjh.autosummary.core.network.retrofit.RetrofitGpt
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
    abstract fun bindNetworkDataSource(retrofitGpt: RetrofitGpt): NetworkDataSource
}
