package com.sjh.autosummary.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

data class ChatHistoryWithMessages(
    @Embedded val chatHistory: ChatHistoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chat_history_id"
    )
    val messageContents: List<MessageContentEntity>
)
