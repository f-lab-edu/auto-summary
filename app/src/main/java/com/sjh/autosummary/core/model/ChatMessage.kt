package com.sjh.autosummary.core.model

import kotlinx.serialization.Serializable

// Todo : db 생성하면 MessageContent로 대체
@Serializable
data class ChatMessage(
    val isFromUser: Boolean,
    val prompt: String,
)
