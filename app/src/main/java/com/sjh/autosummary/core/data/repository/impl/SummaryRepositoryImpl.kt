package com.sjh.autosummary.core.data.repository.impl

import com.sjh.autosummary.core.data.repository.SummaryRepository
import com.sjh.autosummary.core.database.LocalSummaryDataSource
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import com.sjh.autosummary.core.model.ChatSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val localSummaryDataSource: LocalSummaryDataSource,
) : SummaryRepository {

    override suspend fun addOrUpdateChatSummary(chatSummary: ChatSummary): Long? {
        val chatSummaryEntity = chatSummary.toChatSummaryEntity()
        val result = localSummaryDataSource.insertChatSummary(chatSummaryEntity)
        return result.getOrNull()
    }

    override suspend fun findChatSummary(chatSummaryId: Long): Result<ChatSummary> =
        withContext(Dispatchers.IO) {
            try {
                val result = localSummaryDataSource.getChatSummaryById(chatSummaryId)
                result.fold(
                    onSuccess = { entity ->
                        entity?.let {
                            Result.success(it.toChatSummary())
                        }
                            ?: Result.failure(NoSuchElementException("No ChatSummary found for ID $chatSummaryId"))
                    },
                    onFailure = { exception ->
                        Result.failure(exception)
                    }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun findAllChatSummaries(): Result<List<ChatSummary>> =
        withContext(Dispatchers.IO) {
            try {
                val result = localSummaryDataSource.getAllChatSummaries()
                result.fold(
                    onSuccess = { entities ->
                        if (entities.isNotEmpty()) {
                            Result.success(entities.map(ChatSummaryEntity::toChatSummary))
                        } else {
                            Result.failure(NoSuchElementException("No ChatSummaries found."))
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

    override suspend fun deleteChatSummary(chatSummary: ChatSummary) {
        localSummaryDataSource.deleteChatSummary(chatSummary.toChatSummaryEntity())
    }
}


private fun ChatSummary.toChatSummaryEntity() = ChatSummaryEntity(
    id = id,
    title = title,
    subTitle = subTitle,
    content = content
)

private fun ChatSummaryEntity.toChatSummary() = ChatSummary(
    id = id,
    title = title,
    subTitle = subTitle,
    content = content
)
