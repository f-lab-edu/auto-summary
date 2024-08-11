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
        val messageContentEntities = chatHistory.messageList.map {
            it.toMessageContentEntity(chatHistoryEntity.id)
        }

        return if (chatHistory.id != null) {
            val existingChatHistory = localHistoryDataSource.getChatHistoryById(chatHistory.id)
            existingChatHistory.getOrNull()?.let {
                localHistoryDataSource.updateChatHistoryWithMessage(
                    chatHistoryEntity = chatHistoryEntity,
                    messageContentEntities = messageContentEntities
                )
            }
        } else {
            localHistoryDataSource.insertChatHistoryWithMessages(
                chatHistoryEntity = chatHistoryEntity,
                messageContentEntities = messageContentEntities
            )
        }?.getOrNull()
    }

    override suspend fun findChatHistory(chatHistoryId: Long): Result<ChatHistory?> =
        withContext(Dispatchers.IO) {
            try {
                localHistoryDataSource
                    .getChatHistoryWithMessagesById(chatHistoryId)
                    .mapCatching { entity ->
                        entity?.toChatHistory()
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun retrieveAllChatHistories(): Result<List<ChatHistory>> =
        withContext(Dispatchers.IO) {
            try {
                localHistoryDataSource.getAllChatHistoriesWithMessages()
                    .mapCatching { entities ->
                        entities.map(ChatHistoryWithMessages::toChatHistory)
                    }
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
