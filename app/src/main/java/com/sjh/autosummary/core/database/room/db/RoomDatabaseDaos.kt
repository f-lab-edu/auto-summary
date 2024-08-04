package com.sjh.autosummary.core.database.room.db

import com.sjh.autosummary.core.database.LocalDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.dao.ChatHistoryDao
import com.sjh.autosummary.core.database.room.dao.ChatSummaryDao
import com.sjh.autosummary.core.database.room.dao.MessageContentDao
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomDatabaseDaos @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao,
    private val messageContentDao: MessageContentDao,
    private val chatSummaryDao: ChatSummaryDao,
) : LocalDataSource {

    override suspend fun insertChatSummary(chatSummary: ChatSummaryEntity): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val chatSummaryId = chatSummaryDao.insertChatSummary(chatSummary = chatSummary)

                Result.success(chatSummaryId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateChatSummary(chatSummary: ChatSummaryEntity) {
        withContext(Dispatchers.IO) {
            chatSummaryDao.updateChatSummary(chatSummary)
        }
    }

    override suspend fun deleteChatSummary(chatSummary: ChatSummaryEntity) {
        withContext(Dispatchers.IO) {
            chatSummaryDao.deleteChatSummary(chatSummary)
        }
    }

    override suspend fun getChatSummaryById(id: Long): Result<ChatSummaryEntity?> =
        withContext(Dispatchers.IO) {
            try {
                val chatSummary = chatSummaryDao.getChatSummaryById(id)

                chatSummary?.let {
                    Result.success(it)
                } ?: Result.success(null)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getAllChatSummaries(): Result<List<ChatSummaryEntity>> =
        withContext(Dispatchers.IO) {
            try {
                val chatSummarys = chatSummaryDao.getAllChatSummaries() ?: emptyList()

                Result.success(chatSummarys)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getChatHistoryWithMessagesById(id: Long): Result<ChatHistoryWithMessages?> =
        withContext(Dispatchers.IO) {
            try {
                val chatHistory = chatHistoryDao.getChatHistoryById(id)

                val messageContents = chatHistory?.let {
                    messageContentDao.getMessageContentsByChatHistoryId(it.id)
                } ?: emptyList()

                chatHistory?.let {
                    Result.success(ChatHistoryWithMessages(it, messageContents))
                } ?: Result.success(null)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getAllChatHistoriesWithMessages(): Result<List<ChatHistoryWithMessages>> =
        withContext(Dispatchers.IO) {
            try {
                val chatHistories = chatHistoryDao.getAllChatHistories()
                val chatHistoriesWithMessages =
                    chatHistories.map { chatHistory ->
                        val messageContents =
                            messageContentDao.getMessageContentsByChatHistoryId(chatHistory.id)
                        ChatHistoryWithMessages(chatHistory, messageContents)
                    }
                Result.success(chatHistoriesWithMessages)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    override suspend fun insertMessageContent(messageContent: MessageContentEntity) {
        withContext(Dispatchers.IO) {
            messageContentDao.insertMessageContent(messageContent)
        }
    }

    override suspend fun insertChatHistoryWithMessages(
        chatHistoryEntity: ChatHistoryEntity,
        messageContentEntitys: List<MessageContentEntity>,
    ): Result<Long> = withContext(Dispatchers.IO) {
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

    override suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity) {
        withContext(Dispatchers.IO) {
            chatHistoryDao.deleteChatHistory(chatHistory)
        }
    }
}
