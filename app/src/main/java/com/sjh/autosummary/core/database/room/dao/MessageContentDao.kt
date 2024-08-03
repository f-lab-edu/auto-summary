package com.sjh.autosummary.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

@Dao
interface MessageContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageContent(messageContent: MessageContentEntity): Long

    @Update
    suspend fun updateMessageContent(messageContent: MessageContentEntity)

    @Delete
    suspend fun deleteMessageContent(messageContent: MessageContentEntity)

    @Query("DELETE FROM message_content WHERE messageId = :messageId")
    suspend fun deleteMessageContentById(messageId: Long)

    @Query("SELECT * FROM message_content WHERE chat_history_id = :chatHistoryId")
    suspend fun getMessageContentsByChatHistoryId(chatHistoryId: Long): List<MessageContentEntity>

    @Query("SELECT * FROM message_content WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: Long): MessageContentEntity?
}
