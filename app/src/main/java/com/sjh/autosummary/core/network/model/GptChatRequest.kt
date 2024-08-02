package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptChatRequest(
    var messages: List<GptMessageContent>,
    var model: String,
)
