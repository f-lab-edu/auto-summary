package com.sjh.autosummary.core.database.model

import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

data class ChatHistoryWithMessages(
    val chatHistory: ChatHistoryEntity,
    val messageContents: List<MessageContentEntity>
)
