package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptChatResponse(
    var choices: List<GptChoice>,
)

@Serializable
data class GptChoice(
    var index: Int,
    var message: GptMessageContent,
)
