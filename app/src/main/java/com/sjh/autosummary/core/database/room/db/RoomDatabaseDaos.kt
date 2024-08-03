package com.sjh.autosummary.core.database.room.db

import com.sjh.autosummary.core.database.LocalDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.dao.ChatHistoryDao
import com.sjh.autosummary.core.database.room.dao.MessageContentDao
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomDatabaseDaos @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao, private val messageContentDao: MessageContentDao
) : LocalDataSource {

    override suspend fun getChatHistoryWithMessagesById(id: Long): Result<ChatHistoryWithMessages?> {
        return withContext(Dispatchers.IO) {
            try {
                val chatHistory = chatHistoryDao.getChatHistoryById(id)
                val messageContents =
                    chatHistory?.let { messageContentDao.getMessageContentsByChatHistoryId(it.id) }
                if (chatHistory != null && messageContents != null) {
                    Result.success(ChatHistoryWithMessages(chatHistory, messageContents))
                } else {
                    Result.success(null)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getAllChatHistoriesWithMessages(): Result<List<ChatHistoryWithMessages>> {
        return withContext(Dispatchers.IO) {
            try {
                val chatHistories = chatHistoryDao.getAllChatHistories()
                val chatHistoriesWithMessages = chatHistories.map { chatHistory ->
                    val messageContents =
                        messageContentDao.getMessageContentsByChatHistoryId(chatHistory.id)
                    ChatHistoryWithMessages(chatHistory, messageContents)
                }
                Result.success(chatHistoriesWithMessages)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun insertMessageContent(messageContent: MessageContentEntity) {
        withContext(Dispatchers.IO) {
            messageContentDao.insertMessageContent(messageContent)
        }
    }

    override suspend fun insertChatHistoryWithMessages(
        chatHistoryEntity: ChatHistoryEntity, messageContentEntitys: List<MessageContentEntity>
    ): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                val chatHistoryId = chatHistoryDao.insertChatHistory(chatHistoryEntity)
                messageContentEntitys.forEach { messageContent ->
                    messageContentDao.insertMessageContent(messageContent.copy(chatHistoryId = chatHistoryId))
                }

                Result.success(chatHistoryId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteChatHistoryById(chatHistoryId: Long) {
        withContext(Dispatchers.IO) {
            chatHistoryDao.deleteChatHistoryById(chatHistoryId)
        }
    }

}
