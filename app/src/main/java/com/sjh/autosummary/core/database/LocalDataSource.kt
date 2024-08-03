package com.sjh.autosummary.core.database

import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

interface LocalDataSource {

    suspend fun getChatHistoryWithMessagesById(id: Long): Result<ChatHistoryWithMessages?>

    suspend fun getAllChatHistoriesWithMessages(): Result<List<ChatHistoryWithMessages>>

    suspend fun insertMessageContent(messageContent: MessageContentEntity)

    suspend fun insertChatHistoryWithMessages(
        chatHistoryEntity: ChatHistoryEntity,
        messageContentEntitys: List<MessageContentEntity>
    ): Result<Long>

    suspend fun deleteChatHistoryById(chatHistoryId: Long)

}
