package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.data.model.ChatResponse
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.MessageContent

interface ChatRepository {
    suspend fun requestChatResponse(requestMessage: MessageContent): Result<ChatResponse>
    suspend fun requestChatResponseSummary(responseContent: MessageContent): Result<ChatResponse>
    suspend fun requestChatSummaryUpdate(
        chatSummaries: List<ChatSummary>,
        responseSummaryContent: MessageContent,
    ): Result<ChatResponse>
}
