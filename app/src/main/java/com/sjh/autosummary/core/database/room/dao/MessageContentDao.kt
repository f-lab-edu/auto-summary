package com.sjh.autosummary.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

@Dao
interface MessageContentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMessageContent(messageContent: MessageContentEntity): Long

    @Query("DELETE FROM message_content WHERE chat_history_id = :chatHistoryId")
    suspend fun deleteMessagesByChatHistoryId(chatHistoryId: Long)
}
