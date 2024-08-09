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

    override suspend fun findChatSummary(chatSummaryId: Long): Result<ChatSummary?> =
        withContext(Dispatchers.IO) {
            try {
                localSummaryDataSource.getChatSummaryById(chatSummaryId)
                    .mapCatching { entity ->
                        entity?.toChatSummary()
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun retrieveAllChatSummaries(): Result<List<ChatSummary>> =
        withContext(Dispatchers.IO) {
            try {
                localSummaryDataSource.getAllChatSummaries()
                    .mapCatching { entities ->
                        entities.map(ChatSummaryEntity::toChatSummary)
                    }
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
