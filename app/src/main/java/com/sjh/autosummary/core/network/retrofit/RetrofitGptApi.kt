package com.sjh.autosummary.core.network.retrofit

import com.sjh.autosummary.core.network.model.GptRequest
import com.sjh.autosummary.core.network.model.GptResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitGptApi {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body request: GptRequest,
    ): GptResponse
}
