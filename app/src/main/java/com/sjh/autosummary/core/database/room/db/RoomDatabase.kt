package com.sjh.autosummary.core.database.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sjh.autosummary.core.database.room.dao.ChatHistoryDao
import com.sjh.autosummary.core.database.room.dao.ChatSummaryDao
import com.sjh.autosummary.core.database.room.dao.MessageContentDao
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

@Database(
    entities = [ChatHistoryEntity::class, MessageContentEntity::class, ChatSummaryEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun chatHistoryDao(): ChatHistoryDao

    abstract fun messageContentDao(): MessageContentDao

    abstract fun chatSummaryDao(): ChatSummaryDao
}
