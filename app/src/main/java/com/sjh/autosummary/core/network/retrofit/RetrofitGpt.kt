package com.sjh.autosummary.core.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sjh.autosummary.core.network.NetworkDataSource
import com.sjh.autosummary.core.network.model.GptChatRequest
import com.sjh.autosummary.core.network.model.GptChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "https://api.openai.com/v1/"

// Todo : 깃헙 액션에서 관리
private const val GPT_API_KEY = ""

@Singleton
class RetrofitGpt
    @Inject
    constructor(
        json: Json,
        okhttpCallFactory: dagger.Lazy<Call.Factory>,
    ) : NetworkDataSource {
        private val networkApi =
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .callFactory { okhttpCallFactory.get().newCall(it) }
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(RetrofitGptApi::class.java)

        override suspend fun createChatCompletion(chatRequest: GptChatRequest): Result<GptChatResponse> =
            withContext(Dispatchers.IO) {
                try {
                    networkApi.createChatCompletion(
                        authorization = "Bearer $GPT_API_KEY",
                        chatRequest = chatRequest,
                    )
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
