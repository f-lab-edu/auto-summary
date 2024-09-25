package com.sjh.autosummary.core.database.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sjh.autosummary.core.database.room.dao.ChatHistoryDao
import com.sjh.autosummary.core.database.room.dao.ChatMessageDao
import com.sjh.autosummary.core.database.room.dao.ChatSummaryDao
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatMessageEntity
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity

@Database(
    entities = [ChatHistoryEntity::class, ChatMessageEntity::class, ChatSummaryEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun chatHistoryDao(): ChatHistoryDao

    abstract fun chatMessageDao(): ChatMessageDao

    abstract fun chatSummaryDao(): ChatSummaryDao
}
