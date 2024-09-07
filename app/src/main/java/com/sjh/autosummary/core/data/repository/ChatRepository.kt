package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.data.model.ChatResponse
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.MessageContent

interface ChatRepository {
    suspend fun receiveAIAnswer(askMessage: MessageContent): Result<ChatResponse>
    suspend fun receiveAISummary(originalMessage: MessageContent): Result<ChatResponse>
    suspend fun receiveAIMergedSummary(
        storedSummaries: List<ChatSummary>,
        newSummary: MessageContent,
    ): Result<ChatResponse>
}
