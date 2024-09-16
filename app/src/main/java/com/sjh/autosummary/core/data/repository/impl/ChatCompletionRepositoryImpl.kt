package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.common.const.GptConst
import com.sjh.autosummary.core.data.repository.ChatCompletionRepository
import com.sjh.autosummary.core.network.model.GptChatCompletionsRequest
import com.sjh.autosummary.core.network.model.GptChatCompletionsResponse
import com.sjh.autosummary.core.network.retrofit.RetrofitGptApiHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatCompletionRepositoryImpl @Inject constructor(
    private val retrofitGptApiHolder: RetrofitGptApiHolder
) : ChatCompletionRepository {

    override suspend fun completeChat(latestUserMessage: String): Result<String> =
        withContext(Dispatchers.IO) {
            Log.d("whatisthis", "completeChat latestUserMessage : $latestUserMessage")
            try {
                val assistantMessage = retrofitGptApiHolder
                    .retrofitGptApi
                    .createChatCompletion(latestUserMessage.toGptChatCompletionsRequest())
                    .extractContent()

                Result.success(assistantMessage)
            } catch (e: Exception) {
                Log.e("whatisthis", e.toString())
                Result.failure(e)
            }
        }

    private fun String.toGptChatCompletionsRequest() =
        GptChatCompletionsRequest(
            messages = listOf(
                GptChatCompletionsRequest.Message(
                    content = this,
                    role = "user"
                )
            ),
            model = GptConst.DEFAULT_GPT_MODEL
        )

    private fun GptChatCompletionsResponse.extractContent(): String =
        this.choices
            .first()
            .message
            .content
}
