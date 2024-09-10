package com.sjh.autosummary.core.model

data class ChatHistory(
    // yyyy-mm-dd
    val date: String,
    val messageList: List<Message>,
    val id: Long? = null,
    // 사용자가 정의
    val name: String = "",
)
