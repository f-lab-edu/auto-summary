package com.sjh.autosummary.core.data.model

import com.sjh.autosummary.core.common.const.GptConst
import com.sjh.autosummary.core.model.MessageContent
import com.sjh.autosummary.core.network.model.GptChatRequest
import com.sjh.autosummary.core.network.model.toGptRequestMessage

data class ChatRequest(
    var requestMessage: MessageContent,
)

fun ChatRequest.toGptChatRequest() =
    GptChatRequest(
        messages =
            listOf(
                GptConst.DEFAULT_REQUEST_MESSAGE,
                requestMessage,
            ).map(MessageContent::toGptRequestMessage),
        model = GptConst.DEFAULT_GPT_MODEL,
    )
