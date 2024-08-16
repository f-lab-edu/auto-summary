package com.sjh.autosummary.core.database

import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

interface LocalHistoryDataSource {
    suspend fun getChatHistoryById(chatHistoryId: Long): Result<ChatHistoryEntity?>

    suspend fun getChatHistoryWithMessagesById(chatHistoryId: Long): Result<ChatHistoryWithMessages?>

    suspend fun getAllChatHistoriesWithMessages(): Result<List<ChatHistoryWithMessages>>

    suspend fun insertChatHistoryWithMessages(
        chatHistoryEntity: ChatHistoryEntity,
        messageContentEntities: List<MessageContentEntity>,
    ): Result<Long>

    suspend fun updateChatHistoryWithMessage(
        chatHistoryEntity: ChatHistoryEntity,
        messageContentEntities: List<MessageContentEntity>
    ): Result<Long>

    suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity)

    suspend fun deleteAllChatHistories()
}
