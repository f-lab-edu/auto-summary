package com.sjh.autosummary.core.model

data class ChatHistory(
    // yyyy-mm-dd
    val date: String,
    val messages: List<Message>,
    val id: Long = 0L,
    // 사용자가 정의
    val name: String = "",
) {
    data class Message(
        var content: String,
        var isUser: Boolean,
    )
}
