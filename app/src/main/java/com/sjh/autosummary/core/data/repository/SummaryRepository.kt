package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.model.ChatSummary

interface SummaryRepository {
    suspend fun mergeSummaries(content: String): Result<Boolean>

    suspend fun findChatSummary(chatSummaryId: Long): Result<ChatSummary?>

    suspend fun retrieveAllChatSummaries(): Result<List<ChatSummary>>

    suspend fun deleteChatSummary(chatSummary: ChatSummary): Result<Unit>
}
