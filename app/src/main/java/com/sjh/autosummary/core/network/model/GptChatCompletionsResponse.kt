package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptChatCompletionsResponse(
    val choices: List<Choice>
) {
    @Serializable
    data class Choice(
        val message: Message,
    )

    @Serializable
    data class Message(
        val content: String,
        val role: String
    )
}
