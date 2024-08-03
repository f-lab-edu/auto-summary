package com.sjh.autosummary.core.model

data class ChatHistory(
    val date: String, // yyyy-mm-dd
    val messageList: List<MessageContent>,
    val id: Long? = null,
    val name: String = "", // 사용자가 정의
)
