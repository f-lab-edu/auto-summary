package com.sjh.autosummary.core.database

import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

interface LocalDataSource {

    suspend fun insertChatSummary(chatSummary: ChatSummaryEntity): Result<Long>

    suspend fun updateChatSummary(chatSummary: ChatSummaryEntity)

    suspend fun deleteChatSummary(chatSummary: ChatSummaryEntity)

    suspend fun getChatSummaryById(id: Long): Result<ChatSummaryEntity?>

    suspend fun getAllChatSummaries(): Result<List<ChatSummaryEntity>>

    suspend fun getChatHistoryWithMessagesById(id: Long): Result<ChatHistoryWithMessages?>

    suspend fun getAllChatHistoriesWithMessages(): Result<List<ChatHistoryWithMessages>>

    suspend fun insertMessageContent(messageContent: MessageContentEntity)

    suspend fun insertChatHistoryWithMessages(
        chatHistoryEntity: ChatHistoryEntity,
        messageContentEntitys: List<MessageContentEntity>,
    ): Result<Long>

    suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity)
}
