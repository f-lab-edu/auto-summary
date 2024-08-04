package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.model.ChatHistory
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun insertChatHistory(chatHistory: ChatHistory): Long?

    fun getChatHistory(chatHistoryId: Long): Flow<LoadState<ChatHistory>>

    fun getAllChatHistories(): Flow<LoadState<List<ChatHistory>>>

    suspend fun deleteChatHistory(chatHistory: ChatHistory)
}
