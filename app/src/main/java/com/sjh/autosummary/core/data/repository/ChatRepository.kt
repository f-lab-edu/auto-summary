package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.model.ChatResponse
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun createChatCompletion(chatRequest: ChatRequest): Flow<LoadState<ChatResponse>>
}
