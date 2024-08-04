package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptChatRequest(
    val messages: List<GptMessageContent>,
    val model: String,
)
