package com.sjh.autosummary.core.data.repository.impl

import com.sjh.autosummary.core.data.repository.HistoryRepository
import com.sjh.autosummary.core.database.LocalHistoryDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.core.model.MessageContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val localHistoryDataSource: LocalHistoryDataSource
) : HistoryRepository {
    override suspend fun addOrUpdateChatHistory(chatHistory: ChatHistory): Long? {
        val chatHistoryEntity = chatHistory.toChatHistoryEntity()

        val result =
            localHistoryDataSource.insertChatHistoryWithMessages(
                chatHistoryEntity = chatHistoryEntity,
                messageContentEntitys = chatHistory.messageList.map {
                    it.toMessageContentEntity(chatHistoryEntity.id)
                },
            )

        return result.getOrNull()
    }

    override suspend fun findChatHistory(chatHistoryId: Long): Result<ChatHistory> =
        withContext(Dispatchers.IO) {
            try {
                val result = localHistoryDataSource.getChatHistoryWithMessagesById(chatHistoryId)
                result.fold(
                    onSuccess = { entity ->
                        entity?.let {
                            Result.success(it.toChatHistory())
                        }
                            ?: Result.failure(NoSuchElementException("No ChatHistory found for ID $chatHistoryId"))
                    },
                    onFailure = { exception ->
                        Result.failure(exception)
                    }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun findAllChatHistories(): Result<List<ChatHistory>> =
        withContext(Dispatchers.IO) {
            try {
                val result = localHistoryDataSource.getAllChatHistoriesWithMessages()
                result.fold(
                    onSuccess = { entities ->
                        if (entities.isNotEmpty()) {
                            Result.success(entities.map(ChatHistoryWithMessages::toChatHistory))
                        } else {
                            Result.failure(NoSuchElementException("No ChatHistories found."))
                        }
                    },
                    onFailure = { exception ->
                        Result.failure(exception)
                    }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteChatHistory(chatHistory: ChatHistory) {
        localHistoryDataSource.deleteChatHistory(chatHistory.toChatHistoryEntity())
    }
}

private fun ChatHistory.toChatHistoryEntity() =
    ChatHistoryEntity(
        id = id ?: 0L,
        date = date,
        name = name,
    )

private fun ChatHistoryWithMessages.toChatHistory(): ChatHistory {
    return ChatHistory(
        id = this.chatHistory.id,
        date = this.chatHistory.date,
        name = this.chatHistory.name,
        messageList = this.messageContents.map(MessageContentEntity::toMessageContent),
    )
}

private fun MessageContent.toMessageContentEntity(chatHistoryId: Long): MessageContentEntity {
    return MessageContentEntity(
        chatHistoryId = chatHistoryId,
        content = this.content,
        role = this.role,
    )
}

private fun MessageContentEntity.toMessageContent(): MessageContent {
    return MessageContent(
        content = this.content,
        role = this.role,
    )
}
