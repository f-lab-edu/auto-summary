package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.model.ChatHistory

interface HistoryRepository {
    suspend fun addOrUpdateChatHistory(chatHistory: ChatHistory): Long?

    suspend fun findChatHistory(chatHistoryId: Long): Result<ChatHistory?>

    suspend fun retrieveAllChatHistories(): Result<List<ChatHistory>>

    suspend fun deleteChatHistory(chatHistory: ChatHistory): Result<Unit>
}
