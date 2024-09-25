package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.data.repository.ChatHistoryRepository
import com.sjh.autosummary.core.database.LocalHistoryDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatMessageEntity
import com.sjh.autosummary.core.model.ChatHistory
import kotlinx.serialization.json.JsonNull.content
import javax.inject.Inject

class ChatHistoryRepositoryImpl @Inject constructor(
    private val localHistoryDataSource: LocalHistoryDataSource
) : ChatHistoryRepository {

    override suspend fun addOrUpdateChatHistory(chatHistory: ChatHistory): Long? =
        if (chatHistory.id != 0L) {
            updateChatHistory(chatHistory)
        } else {
            addChatHistory(chatHistory)
        }

    override suspend fun updateChatHistoryMessages(
        chatHistory: ChatHistory,
        latestMessage: String,
        isFromUser: Boolean,
    ): ChatHistory? {
        val latestChatHistory = chatHistory.copy(
            messages = chatHistory.messages + ChatHistory.Message(
                latestMessage,
                isFromUser
            )
        )

        val updateResult = localHistoryDataSource
            .updateChatHistoryWithMessage(
                chatHistoryEntity = ChatHistoryEntity(
                    latestChatHistory.id,
                    latestChatHistory.date,
                    latestChatHistory.name,
                ),
                chatMessageEntities = latestChatHistory
                    .messages
                    .map { message ->
                        ChatMessageEntity(
                            chatHistoryId = chatHistory.id,
                            content = message.content,
                            isFromUser = message.isFromUser,
                        )
                    }
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
        localHistoryDataSource.deleteChatHistory(
            ChatHistoryEntity(
                chatHistory.id,
                chatHistory.date,
                chatHistory.name,
            )
        )

    private suspend fun updateChatHistory(
        chatHistory: ChatHistory,
    ): Long? =
        localHistoryDataSource
            .updateChatHistoryWithMessage(
                chatHistoryEntity = ChatHistoryEntity(
                    chatHistory.id,
                    chatHistory.date,
                    chatHistory.name,
                ),
                chatMessageEntities = chatHistory
                    .messages
                    .map { message ->
                        ChatMessageEntity(
                            chatHistoryId = chatHistory.id,
                            content = message.content,
                            isFromUser = message.isFromUser,
                        )
                    }
            )
            .getOrNull()

    private suspend fun addChatHistory(
        chatHistory: ChatHistory,
    ): Long? =
        localHistoryDataSource
            .insertChatHistoryWithMessages(
                chatHistoryEntity = ChatHistoryEntity(
                    chatHistory.id,
                    chatHistory.date,
                    chatHistory.name,
                ),
                chatMessageEntities = chatHistory
                    .messages
                    .map { message ->
                        ChatMessageEntity(
                            chatHistoryId = chatHistory.id,
                            content = message.content,
                            isFromUser = message.isFromUser,
                        )
                    }
            )
            .getOrNull()
}

private fun ChatHistoryWithMessages.toChatHistory(): ChatHistory =
    ChatHistory(
        id = chatHistory.id,
        date = chatHistory.date,
        name = chatHistory.name,
        messages = chatMessages.map { message ->
            ChatHistory.Message(
                content = content,
                isFromUser = message.isFromUser,
            )
        },
    )
