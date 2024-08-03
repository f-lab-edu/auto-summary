package com.sjh.autosummary.core.data.repository.impl

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.repository.HistoryRepository
import com.sjh.autosummary.core.database.LocalDataSource
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.core.model.MessageContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(private val localDataSource: LocalDataSource) :
    HistoryRepository {

    override suspend fun insertChatHistory(chatHistory: ChatHistory): Long? {
        val chatHistoryEntity = chatHistory.toChatHistoryEntity()

        val result = localDataSource.insertChatHistoryWithMessages(chatHistoryEntity,
            chatHistory.messageList.map { it.toMessageContentEntity(chatHistoryEntity.id) })

        return result.getOrNull()
    }

    override fun getChatHistory(chatHistoryId: Long): Flow<LoadState<ChatHistory>> = flow {
        val result = localDataSource.getChatHistoryWithMessagesById(chatHistoryId)

        if (result.isSuccess) {
            val requestSucceededResult = result.getOrNull()

            if (requestSucceededResult != null) {
                emit(LoadState.Succeeded(data = requestSucceededResult.toChatHistory()))
            } else {
                emit(LoadState.Failed(exception = Exception("Received null response from DB")))
            }
        } else {
            val requestFailedResult = result.exceptionOrNull()

            if (requestFailedResult != null) {
                emit(LoadState.Failed(exception = requestFailedResult))
            } else {
                emit(LoadState.Failed(exception = Exception("Unknown error occurred")))
            }
        }
    }.catch { e ->
        emit(LoadState.Failed(exception = e))
    }.onStart {
        emit(LoadState.InProgress)
    }.flowOn(Dispatchers.IO)

    override fun getAllChatHistories(): Flow<LoadState<List<ChatHistory>>> = flow {
        val result = localDataSource.getAllChatHistoriesWithMessages()

        if (result.isSuccess) {
            val requestSucceededResult = result.getOrNull()

            if (requestSucceededResult != null) {
                emit(LoadState.Succeeded(data = requestSucceededResult.map(ChatHistoryWithMessages::toChatHistory)))
            } else {
                emit(LoadState.Failed(exception = Exception("Received null response from DB")))
            }
        } else {
            val requestFailedResult = result.exceptionOrNull()

            if (requestFailedResult != null) {
                emit(LoadState.Failed(exception = requestFailedResult))
            } else {
                emit(LoadState.Failed(exception = Exception("Unknown error occurred")))
            }
        }
    }.catch { e ->
        emit(LoadState.Failed(exception = e))
    }.onStart {
        emit(LoadState.InProgress)
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteChatHistory(chatHistory: ChatHistory) {
        localDataSource.deleteChatHistoryById(chatHistory.id ?: 0L) // id가 null인 경우 기본값 0을 사용
    }
}

private fun ChatHistory.toChatHistoryEntity() = ChatHistoryEntity(
    id = id ?: 0L,
    date = date,
    name = name,
)

private fun ChatHistoryWithMessages.toChatHistory(): ChatHistory {
    return ChatHistory(
        id = this.chatHistory.id,
        date = this.chatHistory.date,
        name = this.chatHistory.name,
        messageList = this.messageContents.map(MessageContentEntity::toMessageContent)
    )
}

private fun MessageContent.toMessageContentEntity(chatHistoryId: Long): MessageContentEntity {
    return MessageContentEntity(
        chatHistoryId = chatHistoryId, content = this.content, role = this.role
    )
}

private fun MessageContentEntity.toMessageContent(): MessageContent {
    return MessageContent(
        content = this.content, role = this.role
    )
}
