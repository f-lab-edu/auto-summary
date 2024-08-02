package com.sjh.autosummary.core.common.const

import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.MessageContent

object GptConst {
    val DEFAULT_GPT_MODEL = "gpt-4o-mini-2024-07-18"

    val DEFAULT_REQUEST_MESSAGE =
        MessageContent(
            content = "You are a helpful assistant.",
            role = ChatRoleType.SYSTEM,
        )
}
