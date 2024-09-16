package com.sjh.autosummary.core.data.repository

interface ChatCompletionRepository {
    suspend fun completeChat(latestUserMessage: String): Result<String>
}
