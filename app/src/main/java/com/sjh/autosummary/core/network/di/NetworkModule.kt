package com.sjh.autosummary.core.network.di

import com.sjh.autosummary.core.network.retrofit.RetrofitGptApiHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitGptApiHolder(
        json: Json,
    ): RetrofitGptApiHolder =
        RetrofitGptApiHolder(json)
}
