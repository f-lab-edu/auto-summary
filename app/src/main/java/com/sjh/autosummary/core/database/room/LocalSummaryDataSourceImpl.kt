package com.sjh.autosummary.core.database.room

import com.sjh.autosummary.core.database.LocalSummaryDataSource
import com.sjh.autosummary.core.database.room.dao.ChatSummaryDao
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalSummaryDataSourceImpl @Inject constructor(
    private val chatSummaryDao: ChatSummaryDao,
) : LocalSummaryDataSource {

    override suspend fun insertChatSummary(chatSummary: ChatSummaryEntity): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val chatSummaryId = chatSummaryDao.insertChatSummary(chatSummary)

                Result.success(chatSummaryId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteChatSummary(chatSummary: ChatSummaryEntity): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val deleteResult = chatSummaryDao.deleteChatSummary(chatSummary)

                Result.success(deleteResult)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getChatSummaryById(chatSummaryId: Long): Result<ChatSummaryEntity?> =
        withContext(Dispatchers.IO) {
            try {
                val chatSummary = chatSummaryDao.getChatSummaryById(chatSummaryId)

                Result.success(chatSummary)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getAllChatSummaries(): Result<List<ChatSummaryEntity>> =
        withContext(Dispatchers.IO) {
            try {
                val chatSummarys = chatSummaryDao.getAllChatSummaries().orEmpty()

                Result.success(chatSummarys)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
