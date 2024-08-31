package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.MessageContent

interface SummaryRepository {
    suspend fun addOrUpdateChatSummary(chatSummaryContent: MessageContent): List<Long>

    suspend fun findChatSummary(chatSummaryId: Long): Result<ChatSummary?>

    suspend fun retrieveAllChatSummaries(): Result<List<ChatSummary>>

    suspend fun retrieveAllChatSummariesInJson(): Result<List<String>>

    suspend fun deleteChatSummary(chatSummary: ChatSummary): Result<Unit>
}
