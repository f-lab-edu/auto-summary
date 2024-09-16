package com.sjh.autosummary.core.data.di

import com.sjh.autosummary.core.data.repository.ChatCompletionRepository
import com.sjh.autosummary.core.data.repository.ChatHistoryRepository
import com.sjh.autosummary.core.data.repository.ChatSummaryRepository
import com.sjh.autosummary.core.data.repository.impl.ChatCompletionRepositoryImpl
import com.sjh.autosummary.core.data.repository.impl.ChatHistoryRepositoryImpl
import com.sjh.autosummary.core.data.repository.impl.ChatSummaryRepositoryImpl
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
    abstract fun bindChatRepository(chatCompletionRepositoryImpl: ChatCompletionRepositoryImpl): ChatCompletionRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(chatHistoryRepositoryImpl: ChatHistoryRepositoryImpl): ChatHistoryRepository

    @Binds
    @Singleton
    abstract fun bindSummaryRepository(summaryRepositoryImpl: ChatSummaryRepositoryImpl): ChatSummaryRepository
}
