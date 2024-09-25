package com.sjh.autosummary.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sjh.autosummary.core.database.room.entity.ChatMessageEntity

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMessageContent(chatMessageEntity: ChatMessageEntity): Long

    @Query("DELETE FROM chat_message WHERE chat_history_id = :chatHistoryId")
    suspend fun deleteMessagesByChatHistoryId(chatHistoryId: Long)
}
