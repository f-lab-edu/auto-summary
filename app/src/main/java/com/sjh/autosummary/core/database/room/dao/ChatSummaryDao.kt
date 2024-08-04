package com.sjh.autosummary.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity

@Dao
interface ChatSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSummary(chatSummary: ChatSummaryEntity): Long

    @Update
    suspend fun updateChatSummary(chatSummary: ChatSummaryEntity)

    @Delete
    suspend fun deleteChatSummary(chatSummary: ChatSummaryEntity)

    @Query("SELECT * FROM chat_summary WHERE id = :id")
    suspend fun getChatSummaryById(id: Long): ChatSummaryEntity?

    @Query("SELECT * FROM chat_summary")
    suspend fun getAllChatSummaries(): List<ChatSummaryEntity>
}
