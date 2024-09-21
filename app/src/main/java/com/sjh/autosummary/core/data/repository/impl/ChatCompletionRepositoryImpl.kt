package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.common.const.GptConst
import com.sjh.autosummary.core.data.repository.ChatCompletionRepository
import com.sjh.autosummary.core.network.model.GptChatCompletionsRequest
import com.sjh.autosummary.core.network.retrofit.RetrofitGptApiHolder
import javax.inject.Inject

class ChatCompletionRepositoryImpl @Inject constructor(
    private val retrofitGptApiHolder: RetrofitGptApiHolder
) : ChatCompletionRepository {

    override suspend fun completeChat(latestUserMessage: String): Result<String> =
        try {
            Log.d("whatisthis", "completeChat latestUserMessage : $latestUserMessage")
            val chatCompletionResponse = retrofitGptApiHolder
                .retrofitGptApi
                .createChatCompletion(
                    GptChatCompletionsRequest(
                        GptConst.DEFAULT_GPT_MODEL,
                        listOf(
                            GptChatCompletionsRequest.Message(
                                content = latestUserMessage,
                                role = "user"
                            )
                        ),
                    )
                )

            val assistantMessage = chatCompletionResponse
                .choices
                .first()
                .message
                .content

            Result.success(assistantMessage)
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }
}
