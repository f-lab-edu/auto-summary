package com.sjh.autosummary.core.data.repository.impl

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.repository.SummaryRepository
import com.sjh.autosummary.core.database.LocalDataSource
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import com.sjh.autosummary.core.model.ChatSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : SummaryRepository {

    override suspend fun insertChatSummary(chatSummary: ChatSummary): Long? {
        val chatSummaryEntity = chatSummary.toChatSummaryEntity()

        val result =
            localDataSource.insertChatSummary(chatSummaryEntity)

        return result.getOrNull()
    }

    override fun getChatSummary(chatSummaryId: Long): Flow<LoadState<ChatSummary>> =
        flow {
            val result = localDataSource.getChatSummaryById(chatSummaryId)

            if (result.isSuccess) {
                val requestSucceededResult = result.getOrNull()

                if (requestSucceededResult != null) {
                    emit(LoadState.Succeeded(data = requestSucceededResult.toChatSummary()))
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

    override fun getAllChatSummaries(): Flow<LoadState<List<ChatSummary>>> =
        flow {
            val result = localDataSource.getAllChatSummaries()

            if (result.isSuccess) {
                val requestSucceededResult = result.getOrNull()

                if (requestSucceededResult != null) {
                    emit(LoadState.Succeeded(data = requestSucceededResult.map(ChatSummaryEntity::toChatSummary)))
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

    override suspend fun updateChatSummary(chatSummary: ChatSummary) {
        localDataSource.updateChatSummary(chatSummary.toChatSummaryEntity())
    }

    override suspend fun deleteChatSummary(chatSummary: ChatSummary) {
        localDataSource.deleteChatSummary(chatSummary.toChatSummaryEntity())
    }
}

private fun ChatSummary.toChatSummaryEntity() =
    ChatSummaryEntity(
        id = id,
        title = title,
        subTitle = subTitle,
        content = content
    )

private fun ChatSummaryEntity.toChatSummary() =
    ChatSummary(
        id = id,
        title = title,
        subTitle = subTitle,
        content = content
    )
