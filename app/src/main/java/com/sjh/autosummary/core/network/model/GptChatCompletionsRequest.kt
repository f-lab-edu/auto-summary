package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptChatCompletionsRequest(
    val model: String,
    val messages: List<Message>
) {
    @Serializable
    data class Message(
        val content: String,
        val role: String
    )
}
