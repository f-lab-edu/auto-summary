package com.sjh.autosummary.core.database.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_message",
    foreignKeys = [
        ForeignKey(
            entity = ChatHistoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["chat_history_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["chat_history_id"])],
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Long = 0,
    @ColumnInfo(name = "chat_history_id") val chatHistoryId: Long,
    var content: String,
    var isUser: Boolean,
)
