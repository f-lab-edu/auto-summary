package com.sjh.autosummary.core.data.model

import com.sjh.autosummary.core.common.const.GptConst
import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.Message
import com.sjh.autosummary.core.network.model.GptMessage
import com.sjh.autosummary.core.network.model.GptRequest

data class RequestMessage(
    override var content: String,
    override var role: ChatRoleType = ChatRoleType.USER,
) : Message {

    companion object {
        fun makeGptRequest(requestMessages: List<RequestMessage>) = GptRequest(
            messages = requestMessages.map(RequestMessage::toGptMessage),
            model = GptConst.DEFAULT_GPT_MODEL
        )
    }

    private fun toGptMessage() = GptMessage(
        content = content,
        role = role.role,
    )
}
