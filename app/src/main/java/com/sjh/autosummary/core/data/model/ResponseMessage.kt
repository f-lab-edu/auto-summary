package com.sjh.autosummary.core.data.model

import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.Message
import com.sjh.autosummary.core.network.model.GptResponse

data class ResponseMessage(
    override var content: String,
    override var role: ChatRoleType = ChatRoleType.GPT,
) : Message

fun GptResponse.toResponseMessage() = ResponseMessage(
    content = choices
        .first()
        .message
        .content
)
