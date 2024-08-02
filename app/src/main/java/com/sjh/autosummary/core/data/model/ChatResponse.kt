package com.sjh.autosummary.core.data.model

import com.sjh.autosummary.core.model.MessageContent
import com.sjh.autosummary.core.network.model.GptChatResponse
import com.sjh.autosummary.core.network.model.toMessageContent

data class ChatResponse(
    var responseMessage: MessageContent?,
)

fun GptChatResponse.toChatResponse(): ChatResponse {
    val firstChoice = choices.firstOrNull()
    return ChatResponse(
        responseMessage = firstChoice?.message?.toMessageContent(),
    )
}
