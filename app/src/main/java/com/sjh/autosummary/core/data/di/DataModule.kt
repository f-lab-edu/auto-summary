package com.sjh.autosummary.core.data.di

import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.data.repository.HistoryRepository
import com.sjh.autosummary.core.data.repository.SummaryRepository
import com.sjh.autosummary.core.data.repository.impl.ChatRepositoryImpl
import com.sjh.autosummary.core.data.repository.impl.HistoryRepositoryImpl
import com.sjh.autosummary.core.data.repository.impl.SummaryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindsChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindsHistoryRepository(historyRepositoryImpl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindsSummaryRepository(summaryRepositoryImpl: SummaryRepositoryImpl): SummaryRepository
}
