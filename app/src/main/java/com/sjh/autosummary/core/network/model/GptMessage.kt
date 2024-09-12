package com.sjh.autosummary.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GptMessage(
    val content: String,
    val role: String,
)
