package com.sjh.autosummary.core.database

import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatMessageEntity

interface LocalHistoryDataSource {
    suspend fun getChatHistoryById(chatHistoryId: Long): Result<ChatHistoryEntity?>

    suspend fun getChatHistoryWithMessagesById(chatHistoryId: Long): Result<ChatHistoryWithMessages?>

    suspend fun getAllChatHistoriesWithMessages(): Result<List<ChatHistoryWithMessages>>

    suspend fun insertChatHistoryWithMessages(
        chatHistoryEntity: ChatHistoryEntity,
        chatMessageEntities: List<ChatMessageEntity>,
    ): Result<Long>

    suspend fun updateChatHistoryWithMessage(
        chatHistoryEntity: ChatHistoryEntity,
        chatMessageEntities: List<ChatMessageEntity>
    ): Result<Long>

    suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity): Result<Unit>
}
