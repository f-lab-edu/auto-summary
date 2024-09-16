package com.sjh.autosummary.core.network.retrofit

import com.sjh.autosummary.core.network.model.GptChatCompletionsRequest
import com.sjh.autosummary.core.network.model.GptChatCompletionsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitGptApi {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: GptChatCompletionsRequest,
    ): GptChatCompletionsResponse
}
