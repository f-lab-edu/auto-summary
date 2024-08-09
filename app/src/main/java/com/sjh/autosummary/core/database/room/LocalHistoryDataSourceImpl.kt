package com.sjh.autosummary.core.database.room

import androidx.room.Transaction
import com.sjh.autosummary.core.database.LocalHistoryDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.dao.ChatHistoryDao
import com.sjh.autosummary.core.database.room.dao.MessageContentDao
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalHistoryDataSourceImpl @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao,
    private val messageContentDao: MessageContentDao,
) : LocalHistoryDataSource {

    override suspend fun getChatHistoryWithMessagesById(chatHistoryId: Long): Result<ChatHistoryWithMessages?> =
        withContext(Dispatchers.IO) {
            try {
                val chatHistoryWithMessages =
                    chatHistoryDao.getChatHistoryWithMessagesById(chatHistoryId)
                Result.success(chatHistoryWithMessages)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getAllChatHistoriesWithMessages(): Result<List<ChatHistoryWithMessages>> =
        withContext(Dispatchers.IO) {
            try {
                val chatHistoriesWithMessages = chatHistoryDao.getAllChatHistoriesWithMessages()
                Result.success(chatHistoriesWithMessages)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    @Transaction
    override suspend fun insertChatHistoryWithMessages(
        chatHistoryEntity: ChatHistoryEntity,
        messageContentEntities: List<MessageContentEntity>
    ): Result<Long> =
        try {
            val chatHistoryId = chatHistoryDao.insertChatHistory(chatHistoryEntity)
            for (messageContent in messageContentEntities) {
                messageContentDao.insertMessageContent(messageContent.copy(chatHistoryId = chatHistoryId))
            }
            Result.success(chatHistoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }


    override suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity) {
        withContext(Dispatchers.IO) {
            chatHistoryDao.deleteChatHistory(chatHistory)
        }
    }
}
