package com.sjh.autosummary.core.network

import com.sjh.autosummary.core.network.model.GptChatRequest
import com.sjh.autosummary.core.network.model.GptChatResponse

interface NetworkDataSource {
    suspend fun createChatCompletion(chatRequest: GptChatRequest): Result<GptChatResponse>
}
