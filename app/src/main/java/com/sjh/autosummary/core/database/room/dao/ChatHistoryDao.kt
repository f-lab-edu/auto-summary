package com.sjh.autosummary.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sjh.autosummary.core.database.model.ChatHistoryWithMessages
import com.sjh.autosummary.core.database.room.entity.ChatHistoryEntity

@Dao
interface ChatHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatHistory(chatHistory: ChatHistoryEntity): Long

    @Update
    suspend fun updateChatHistory(chatHistory: ChatHistoryEntity)

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistoryEntity)

    @Query("DELETE FROM chat_history WHERE id = :chatHistoryId")
    suspend fun deleteChatHistoryById(chatHistoryId: Long)

    @Query("SELECT * FROM chat_history WHERE id = :chatHistoryId")
    suspend fun getChatHistoryById(chatHistoryId: Long): ChatHistoryEntity?

    @Query("SELECT * FROM chat_history")
    suspend fun getAllChatHistories(): List<ChatHistoryEntity>

    @Transaction
    @Query("SELECT * FROM chat_history WHERE id = :chatHistoryId")
    suspend fun getChatHistoryWithMessagesById(chatHistoryId: Long): ChatHistoryWithMessages?

    @Transaction
    @Query("SELECT * FROM chat_history")
    suspend fun getAllChatHistoriesWithMessages(): List<ChatHistoryWithMessages>
}
