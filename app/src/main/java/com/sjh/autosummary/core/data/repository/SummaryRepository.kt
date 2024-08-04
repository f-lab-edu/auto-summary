package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.model.ChatSummary
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {

    suspend fun insertChatSummary(chatSummary: ChatSummary): Long?

    fun getChatSummary(chatSummaryId: Long): Flow<LoadState<ChatSummary>>

    fun getAllChatSummaries(): Flow<LoadState<List<ChatSummary>>>

    suspend fun updateChatSummary(chatSummary: ChatSummary)

    suspend fun deleteChatSummary(chatSummary: ChatSummary)
}
