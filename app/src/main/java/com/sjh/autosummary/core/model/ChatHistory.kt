package com.sjh.autosummary.core.model

data class ChatHistory(
    val id: Long,
    val date: String,
    val name: String,
    val messageList: List<ChatMessage>,
)
