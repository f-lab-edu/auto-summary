package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.data.repository.ChatHistoryRepository
import com.sjh.autosummary.core.database.LocalHistoryDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatMessageEntity
import com.sjh.autosummary.core.model.ChatHistory
import javax.inject.Inject

class ChatHistoryRepositoryImpl @Inject constructor(
    private val localHistoryDataSource: LocalHistoryDataSource
) : ChatHistoryRepository {

    override suspend fun addOrUpdateChatHistory(chatHistory: ChatHistory): ChatHistory? {
        val updateResult = if (chatHistory.id != 0L) {
            updateChatHistory(chatHistory)
        } else {
            addChatHistory(chatHistory)
        }

        return if (updateResult != null) {
            chatHistory.copy(id = updateResult)
        } else {
            null
        }
    }

    override suspend fun updateChatHistoryMessages(
        chatHistory: ChatHistory,
        latestMessage: String,
        isUser: Boolean,
    ): ChatHistory? {
        val latestChatHistory = chatHistory.copy(
            messages = chatHistory
                .messages
                .toMutableList()
                .apply {
                    this.add(
                        ChatHistory.Message(
                            content = latestMessage,
                            isUser = isUser
                        )
                    )
                }
        )

        val updateResult = localHistoryDataSource
            .updateChatHistoryWithMessage(
                chatHistoryEntity = latestChatHistory.toChatHistoryEntity(),
                chatMessageEntities = latestChatHistory
                    .messages
                    .map { it.toChatMessageEntity(chatHistory.id) }
            )
            .getOrNull()

        return if (updateResult != null) {
            latestChatHistory
        } else {
            null
        }
    }

    override suspend fun findChatHistory(chatHistoryId: Long): Result<ChatHistory?> =
        try {
            localHistoryDataSource
                .getChatHistoryWithMessagesById(chatHistoryId)
                .mapCatching { entity ->
                    entity?.toChatHistory()
                }
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }

    override suspend fun retrieveAllChatHistories(): Result<List<ChatHistory>> =
        try {
            localHistoryDataSource
                .getAllChatHistoriesWithMessages()
                .mapCatching { entities ->
                    entities.map(ChatHistoryWithMessages::toChatHistory)
                }
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }

    override suspend fun deleteChatHistory(chatHistory: ChatHistory): Result<Unit> =
        localHistoryDataSource.deleteChatHistory(chatHistory.toChatHistoryEntity())

    private suspend fun updateChatHistory(
        chatHistory: ChatHistory,
    ): Long? =
        localHistoryDataSource
            .updateChatHistoryWithMessage(
                chatHistoryEntity = chatHistory.toChatHistoryEntity(),
                chatMessageEntities = chatHistory
                    .messages
                    .map {
                        it.toChatMessageEntity(chatHistory.id)
                    }
            )
            .getOrNull()

    private suspend fun addChatHistory(
        chatHistory: ChatHistory,
    ): Long? =
        localHistoryDataSource
            .insertChatHistoryWithMessages(
                chatHistoryEntity = chatHistory.toChatHistoryEntity(),
                chatMessageEntities = chatHistory
                    .messages
                    .map {
                        it.toChatMessageEntity(chatHistory.id)
                    }
            )
            .getOrNull()
}

private fun ChatHistory.toChatHistoryEntity() =
    ChatHistoryEntity(
        id = id,
        date = date,
        name = name,
    )

private fun ChatHistoryWithMessages.toChatHistory(): ChatHistory =
    ChatHistory(
        id = chatHistory.id,
        date = chatHistory.date,
        name = chatHistory.name,
        messages = chatMessages.map(ChatMessageEntity::toMessage),
    )

private fun ChatHistory.Message.toChatMessageEntity(chatHistoryId: Long): ChatMessageEntity =
    ChatMessageEntity(
        chatHistoryId = chatHistoryId,
        content = content,
        isUser = isUser,
    )

private fun ChatMessageEntity.toMessage(): ChatHistory.Message =
    if (isUser) {
        ChatHistory.Message(
            content = content,
            isUser = true,
        )
    } else {
        ChatHistory.Message(
            content = content,
            isUser = false,
        )
    }
