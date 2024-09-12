package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptRequest(
    val messages: List<GptMessage>,
    val model: String,
)
