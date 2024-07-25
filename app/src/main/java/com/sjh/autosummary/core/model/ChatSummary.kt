package com.sjh.autosummary.core.model

data class ChatSummary(
    val id: Long,
    val title: String,
    val subTitle: String? = null,
    val content: List<InformationForm>,
)

data class InformationForm(
    val head: String,
    val body: String,
    val informationForm: InformationForm? = null,
)

val initChatSummary =
    ChatSummary(
        id = 0L,
        title = "",
        subTitle = null,
        content = emptyList(),
    )
