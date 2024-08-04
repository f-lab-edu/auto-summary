package com.sjh.autosummary.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity

@Dao
interface ChatHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatHistory(chatHistory: ChatHistoryEntity): Long

    @Update
    suspend fun updateChatHistory(chatHistory: ChatHistoryEntity)

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity)

    @Query("DELETE FROM chat_history WHERE id = :id")
    suspend fun deleteChatHistoryById(id: Long)

    @Query("SELECT * FROM chat_history WHERE id = :id")
    suspend fun getChatHistoryById(id: Long): ChatHistoryEntity?

    @Query("SELECT * FROM chat_history")
    suspend fun getAllChatHistories(): List<ChatHistoryEntity>
}
