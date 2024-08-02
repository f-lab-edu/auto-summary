package com.sjh.autosummary.core.network

import com.sjh.autosummary.core.network.model.GptChatRequest
import com.sjh.autosummary.core.network.model.GptChatResponse
import kotlinx.coroutines.flow.Flow

interface NetworkDataSource {
    fun createChatCompletion(chatRequest: GptChatRequest): Flow<GptChatResponse>
}
