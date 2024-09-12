package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptResponse(
    val choices: List<GptChoice>,
)

@Serializable
data class GptChoice(
    val index: Int,
    val message: GptMessage,
)
