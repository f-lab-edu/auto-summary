package com.sjh.autosummary.core.database.room

import androidx.room.Transaction
import com.sjh.autosummary.core.database.LocalHistoryDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.dao.ChatHistoryDao
import com.sjh.autosummary.core.database.room.dao.ChatMessageDao
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalHistoryDataSourceImpl @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao,
    private val chatMessageDao: ChatMessageDao,
) : LocalHistoryDataSource {

    override suspend fun getChatHistoryById(chatHistoryId: Long): Result<ChatHistoryEntity?> =
        withContext(Dispatchers.IO) {
            try {
                val chatHistory =
                    chatHistoryDao.getChatHistoryById(chatHistoryId)
                Result.success(chatHistory)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

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
        chatMessageEntities: List<ChatMessageEntity>
    ): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val chatHistoryId = chatHistoryDao.insertChatHistory(chatHistoryEntity)
                for (chatMessage in chatMessageEntities) {
                    chatMessageDao.insertMessageContent(chatMessage.copy(chatHistoryId = chatHistoryId))
                }
                Result.success(chatHistoryId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateChatHistoryWithMessage(
        chatHistoryEntity: ChatHistoryEntity,
        chatMessageEntities: List<ChatMessageEntity>
    ): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                // 기존 메시지 내용 삭제
                chatMessageDao.deleteMessagesByChatHistoryId(chatHistoryEntity.id)
                // 채팅 기록 업데이트
                chatHistoryDao.updateChatHistory(chatHistoryEntity)
                // 새 메시지 내용 삽입
                for (chatMessage in chatMessageEntities) {
                    chatMessageDao.insertMessageContent(chatMessage.copy(chatHistoryId = chatHistoryEntity.id))
                }
                Result.success(chatHistoryEntity.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity) =
        withContext(Dispatchers.IO) {
            try {
                Result.success(chatHistoryDao.deleteChatHistory(chatHistory))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
