package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.model.ChatResponse

interface ChatRepository {
    suspend fun requestChatResponse(chatRequest: ChatRequest): Result<ChatResponse>
}
