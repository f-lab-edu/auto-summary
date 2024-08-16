package com.sjh.autosummary.core.network.retrofit

import com.sjh.autosummary.core.network.model.GptChatRequest
import com.sjh.autosummary.core.network.model.GptChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitGptApi {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body chatRequest: GptChatRequest,
    ): GptChatResponse
}
