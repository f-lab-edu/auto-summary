package com.sjh.autosummary.core.database

import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity

interface LocalSummaryDataSource {

    suspend fun insertChatSummary(chatSummary: ChatSummaryEntity): Result<Long>

    suspend fun deleteChatSummary(chatSummary: ChatSummaryEntity): Result<Unit>

    suspend fun getChatSummaryById(chatSummaryId: Long): Result<ChatSummaryEntity?>

    suspend fun getAllChatSummaries(): Result<List<ChatSummaryEntity>>
}
