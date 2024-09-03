package com.sjh.autosummary.core.data.model

import com.sjh.autosummary.core.model.MessageContent

data class ChatRequest(
    val requestMessages: List<MessageContent>,
)
